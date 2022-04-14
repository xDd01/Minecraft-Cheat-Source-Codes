package io.netty.channel.nio;

import java.nio.channels.*;

public interface NioTask<C extends SelectableChannel>
{
    void channelReady(final C p0, final SelectionKey p1) throws Exception;
    
    void channelUnregistered(final C p0, final Throwable p1) throws Exception;
}
