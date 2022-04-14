package io.netty.channel;

import io.netty.util.concurrent.*;

public interface EventLoop extends EventExecutor, EventLoopGroup
{
    EventLoopGroup parent();
}
