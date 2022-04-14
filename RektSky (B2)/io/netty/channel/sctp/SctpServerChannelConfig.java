package io.netty.channel.sctp;

import com.sun.nio.sctp.*;
import io.netty.buffer.*;
import io.netty.channel.*;

public interface SctpServerChannelConfig extends ChannelConfig
{
    int getBacklog();
    
    SctpServerChannelConfig setBacklog(final int p0);
    
    int getSendBufferSize();
    
    SctpServerChannelConfig setSendBufferSize(final int p0);
    
    int getReceiveBufferSize();
    
    SctpServerChannelConfig setReceiveBufferSize(final int p0);
    
    SctpStandardSocketOptions.InitMaxStreams getInitMaxStreams();
    
    SctpServerChannelConfig setInitMaxStreams(final SctpStandardSocketOptions.InitMaxStreams p0);
    
    SctpServerChannelConfig setMaxMessagesPerRead(final int p0);
    
    SctpServerChannelConfig setWriteSpinCount(final int p0);
    
    SctpServerChannelConfig setConnectTimeoutMillis(final int p0);
    
    SctpServerChannelConfig setAllocator(final ByteBufAllocator p0);
    
    SctpServerChannelConfig setRecvByteBufAllocator(final RecvByteBufAllocator p0);
    
    SctpServerChannelConfig setAutoRead(final boolean p0);
    
    SctpServerChannelConfig setAutoClose(final boolean p0);
    
    SctpServerChannelConfig setWriteBufferHighWaterMark(final int p0);
    
    SctpServerChannelConfig setWriteBufferLowWaterMark(final int p0);
    
    SctpServerChannelConfig setMessageSizeEstimator(final MessageSizeEstimator p0);
}
