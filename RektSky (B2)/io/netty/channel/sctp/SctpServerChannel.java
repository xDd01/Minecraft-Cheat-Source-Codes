package io.netty.channel.sctp;

import java.util.*;
import java.net.*;
import io.netty.channel.*;

public interface SctpServerChannel extends ServerChannel
{
    SctpServerChannelConfig config();
    
    InetSocketAddress localAddress();
    
    Set<InetSocketAddress> allLocalAddresses();
    
    ChannelFuture bindAddress(final InetAddress p0);
    
    ChannelFuture bindAddress(final InetAddress p0, final ChannelPromise p1);
    
    ChannelFuture unbindAddress(final InetAddress p0);
    
    ChannelFuture unbindAddress(final InetAddress p0, final ChannelPromise p1);
}
