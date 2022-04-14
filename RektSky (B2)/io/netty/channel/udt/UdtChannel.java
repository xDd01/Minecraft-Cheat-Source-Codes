package io.netty.channel.udt;

import io.netty.channel.*;
import java.net.*;

public interface UdtChannel extends Channel
{
    UdtChannelConfig config();
    
    InetSocketAddress localAddress();
    
    InetSocketAddress remoteAddress();
}
