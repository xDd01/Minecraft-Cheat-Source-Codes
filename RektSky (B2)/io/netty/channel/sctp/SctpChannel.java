package io.netty.channel.sctp;

import com.sun.nio.sctp.*;
import java.util.*;
import java.net.*;
import io.netty.channel.*;

public interface SctpChannel extends Channel
{
    SctpServerChannel parent();
    
    Association association();
    
    InetSocketAddress localAddress();
    
    Set<InetSocketAddress> allLocalAddresses();
    
    SctpChannelConfig config();
    
    InetSocketAddress remoteAddress();
    
    Set<InetSocketAddress> allRemoteAddresses();
    
    ChannelFuture bindAddress(final InetAddress p0);
    
    ChannelFuture bindAddress(final InetAddress p0, final ChannelPromise p1);
    
    ChannelFuture unbindAddress(final InetAddress p0);
    
    ChannelFuture unbindAddress(final InetAddress p0, final ChannelPromise p1);
}
