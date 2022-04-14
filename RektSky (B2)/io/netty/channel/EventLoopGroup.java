package io.netty.channel;

import io.netty.util.concurrent.*;

public interface EventLoopGroup extends EventExecutorGroup
{
    EventLoop next();
    
    ChannelFuture register(final Channel p0);
    
    ChannelFuture register(final Channel p0, final ChannelPromise p1);
}
