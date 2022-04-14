package io.netty.channel.socket;

import io.netty.channel.*;
import java.net.*;

public interface ServerSocketChannel extends ServerChannel
{
    ServerSocketChannelConfig config();
    
    InetSocketAddress localAddress();
    
    InetSocketAddress remoteAddress();
}
