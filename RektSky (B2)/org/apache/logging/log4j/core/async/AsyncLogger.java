package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.status.*;
import com.lmax.disruptor.util.*;
import org.apache.logging.log4j.message.*;
import org.apache.logging.log4j.*;
import org.apache.logging.log4j.core.impl.*;
import org.apache.logging.log4j.core.*;
import java.util.*;
import org.apache.logging.log4j.core.config.*;
import org.apache.logging.log4j.core.helpers.*;
import com.lmax.disruptor.dsl.*;
import java.util.concurrent.*;
import com.lmax.disruptor.*;

public class AsyncLogger extends Logger
{
    private static final int HALF_A_SECOND = 500;
    private static final int MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN = 20;
    private static final int RINGBUFFER_MIN_SIZE = 128;
    private static final int RINGBUFFER_DEFAULT_SIZE = 262144;
    private static final StatusLogger LOGGER;
    private static volatile Disruptor<RingBufferLogEvent> disruptor;
    private static Clock clock;
    private static ExecutorService executor;
    private final ThreadLocal<Info> threadlocalInfo;
    
    private static int calculateRingBufferSize() {
        int ringBufferSize = 262144;
        final String userPreferredRBSize = System.getProperty("AsyncLogger.RingBufferSize", String.valueOf(ringBufferSize));
        try {
            int size = Integer.parseInt(userPreferredRBSize);
            if (size < 128) {
                size = 128;
                AsyncLogger.LOGGER.warn("Invalid RingBufferSize {}, using minimum size {}.", userPreferredRBSize, 128);
            }
            ringBufferSize = size;
        }
        catch (Exception ex) {
            AsyncLogger.LOGGER.warn("Invalid RingBufferSize {}, using default size {}.", userPreferredRBSize, ringBufferSize);
        }
        return Util.ceilingNextPowerOfTwo(ringBufferSize);
    }
    
    private static WaitStrategy createWaitStrategy() {
        final String strategy = System.getProperty("AsyncLogger.WaitStrategy");
        AsyncLogger.LOGGER.debug("property AsyncLogger.WaitStrategy={}", strategy);
        if ("Sleep".equals(strategy)) {
            AsyncLogger.LOGGER.debug("disruptor event handler uses SleepingWaitStrategy");
            return (WaitStrategy)new SleepingWaitStrategy();
        }
        if ("Yield".equals(strategy)) {
            AsyncLogger.LOGGER.debug("disruptor event handler uses YieldingWaitStrategy");
            return (WaitStrategy)new YieldingWaitStrategy();
        }
        if ("Block".equals(strategy)) {
            AsyncLogger.LOGGER.debug("disruptor event handler uses BlockingWaitStrategy");
            return (WaitStrategy)new BlockingWaitStrategy();
        }
        AsyncLogger.LOGGER.debug("disruptor event handler uses SleepingWaitStrategy");
        return (WaitStrategy)new SleepingWaitStrategy();
    }
    
    private static ExceptionHandler getExceptionHandler() {
        final String cls = System.getProperty("AsyncLogger.ExceptionHandler");
        if (cls == null) {
            AsyncLogger.LOGGER.debug("No AsyncLogger.ExceptionHandler specified");
            return null;
        }
        try {
            final Class<? extends ExceptionHandler> klass = (Class<? extends ExceptionHandler>)Class.forName(cls);
            final ExceptionHandler result = (ExceptionHandler)klass.newInstance();
            AsyncLogger.LOGGER.debug("AsyncLogger.ExceptionHandler=" + result);
            return result;
        }
        catch (Exception ignored) {
            AsyncLogger.LOGGER.debug("AsyncLogger.ExceptionHandler not set: error creating " + cls + ": ", ignored);
            return null;
        }
    }
    
    public AsyncLogger(final LoggerContext context, final String name, final MessageFactory messageFactory) {
        super(context, name, messageFactory);
        this.threadlocalInfo = new ThreadLocal<Info>();
    }
    
    @Override
    public void log(final Marker marker, final String fqcn, final Level level, final Message data, final Throwable t) {
        Info info = this.threadlocalInfo.get();
        if (info == null) {
            info = new Info();
            info.translator = new RingBufferLogEventTranslator();
            info.cachedThreadName = Thread.currentThread().getName();
            this.threadlocalInfo.set(info);
        }
        final boolean includeLocation = this.config.loggerConfig.isIncludeLocation();
        info.translator.setValues(this, this.getName(), marker, fqcn, level, data, t, ThreadContext.getImmutableContext(), ThreadContext.getImmutableStack(), info.cachedThreadName, includeLocation ? this.location(fqcn) : null, AsyncLogger.clock.currentTimeMillis());
        AsyncLogger.disruptor.publishEvent((EventTranslator)info.translator);
    }
    
    private StackTraceElement location(final String fqcnOfLogger) {
        return Log4jLogEvent.calcLocation(fqcnOfLogger);
    }
    
    public void actualAsyncLog(final RingBufferLogEvent event) {
        final Map<Property, Boolean> properties = this.config.loggerConfig.getProperties();
        event.mergePropertiesIntoContextMap(properties, this.config.config.getStrSubstitutor());
        this.config.logEvent(event);
    }
    
    public static void stop() {
        final Disruptor<RingBufferLogEvent> temp = AsyncLogger.disruptor;
        AsyncLogger.disruptor = null;
        temp.shutdown();
        final RingBuffer<RingBufferLogEvent> ringBuffer = (RingBuffer<RingBufferLogEvent>)temp.getRingBuffer();
        for (int i = 0; i < 20 && !ringBuffer.hasAvailableCapacity(ringBuffer.getBufferSize()); ++i) {
            try {
                Thread.sleep(500L);
            }
            catch (InterruptedException ex) {}
        }
        AsyncLogger.executor.shutdown();
    }
    
    static {
        LOGGER = StatusLogger.getLogger();
        AsyncLogger.clock = ClockFactory.getClock();
        AsyncLogger.executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory("AsyncLogger-"));
        final int ringBufferSize = calculateRingBufferSize();
        final WaitStrategy waitStrategy = createWaitStrategy();
        AsyncLogger.disruptor = (Disruptor<RingBufferLogEvent>)new Disruptor((EventFactory)RingBufferLogEvent.FACTORY, ringBufferSize, (Executor)AsyncLogger.executor, ProducerType.MULTI, waitStrategy);
        final EventHandler<RingBufferLogEvent>[] handlers = (EventHandler<RingBufferLogEvent>[])new RingBufferLogEventHandler[] { new RingBufferLogEventHandler() };
        AsyncLogger.disruptor.handleExceptionsWith(getExceptionHandler());
        AsyncLogger.disruptor.handleEventsWith((EventHandler[])handlers);
        AsyncLogger.LOGGER.debug("Starting AsyncLogger disruptor with ringbuffer size {}...", AsyncLogger.disruptor.getRingBuffer().getBufferSize());
        AsyncLogger.disruptor.start();
    }
    
    private static class Info
    {
        private RingBufferLogEventTranslator translator;
        private String cachedThreadName;
    }
}
