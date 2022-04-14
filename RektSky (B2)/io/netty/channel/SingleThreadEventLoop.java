package io.netty.channel;

import java.util.concurrent.*;
import io.netty.util.concurrent.*;

public abstract class SingleThreadEventLoop extends SingleThreadEventExecutor implements EventLoop
{
    protected SingleThreadEventLoop(final EventLoopGroup parent, final ThreadFactory threadFactory, final boolean addTaskWakesUp) {
        super(parent, threadFactory, addTaskWakesUp);
    }
    
    @Override
    public EventLoopGroup parent() {
        return (EventLoopGroup)super.parent();
    }
    
    @Override
    public EventLoop next() {
        return (EventLoop)super.next();
    }
    
    @Override
    public ChannelFuture register(final Channel channel) {
        return this.register(channel, new DefaultChannelPromise(channel, this));
    }
    
    @Override
    public ChannelFuture register(final Channel channel, final ChannelPromise promise) {
        if (channel == null) {
            throw new NullPointerException("channel");
        }
        if (promise == null) {
            throw new NullPointerException("promise");
        }
        channel.unsafe().register(this, promise);
        return promise;
    }
    
    @Override
    protected boolean wakesUpForTask(final Runnable task) {
        return !(task instanceof NonWakeupRunnable);
    }
    
    interface NonWakeupRunnable extends Runnable
    {
    }
}
