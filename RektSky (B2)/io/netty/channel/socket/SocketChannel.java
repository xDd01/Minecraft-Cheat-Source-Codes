package io.netty.channel.socket;

import java.net.*;
import io.netty.channel.*;

public interface SocketChannel extends Channel
{
    ServerSocketChannel parent();
    
    SocketChannelConfig config();
    
    InetSocketAddress localAddress();
    
    InetSocketAddress remoteAddress();
    
    boolean isInputShutdown();
    
    boolean isOutputShutdown();
    
    ChannelFuture shutdownOutput();
    
    ChannelFuture shutdownOutput(final ChannelPromise p0);
}
