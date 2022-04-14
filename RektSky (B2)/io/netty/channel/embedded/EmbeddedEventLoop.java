package io.netty.channel.embedded;

import java.util.*;
import java.util.concurrent.*;
import io.netty.channel.*;
import io.netty.util.concurrent.*;

final class EmbeddedEventLoop extends AbstractEventExecutor implements EventLoop
{
    private final Queue<Runnable> tasks;
    
    EmbeddedEventLoop() {
        this.tasks = new ArrayDeque<Runnable>(2);
    }
    
    @Override
    public void execute(final Runnable command) {
        if (command == null) {
            throw new NullPointerException("command");
        }
        this.tasks.add(command);
    }
    
    void runTasks() {
        while (true) {
            final Runnable task = this.tasks.poll();
            if (task == null) {
                break;
            }
            task.run();
        }
    }
    
    @Override
    public Future<?> shutdownGracefully(final long quietPeriod, final long timeout, final TimeUnit unit) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public Future<?> terminationFuture() {
        throw new UnsupportedOperationException();
    }
    
    @Deprecated
    @Override
    public void shutdown() {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean isShuttingDown() {
        return false;
    }
    
    @Override
    public boolean isShutdown() {
        return false;
    }
    
    @Override
    public boolean isTerminated() {
        return false;
    }
    
    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        Thread.sleep(unit.toMillis(timeout));
        return false;
    }
    
    @Override
    public ChannelFuture register(final Channel channel) {
        return this.register(channel, new DefaultChannelPromise(channel, this));
    }
    
    @Override
    public ChannelFuture register(final Channel channel, final ChannelPromise promise) {
        channel.unsafe().register(this, promise);
        return promise;
    }
    
    @Override
    public boolean inEventLoop() {
        return true;
    }
    
    @Override
    public boolean inEventLoop(final Thread thread) {
        return true;
    }
    
    @Override
    public EventLoop next() {
        return this;
    }
    
    @Override
    public EventLoopGroup parent() {
        return this;
    }
}
