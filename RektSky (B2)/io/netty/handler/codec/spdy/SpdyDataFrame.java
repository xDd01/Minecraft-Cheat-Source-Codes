package io.netty.handler.codec.spdy;

import io.netty.buffer.*;

public interface SpdyDataFrame extends ByteBufHolder, SpdyStreamFrame
{
    SpdyDataFrame setStreamId(final int p0);
    
    SpdyDataFrame setLast(final boolean p0);
    
    ByteBuf content();
    
    SpdyDataFrame copy();
    
    SpdyDataFrame duplicate();
    
    SpdyDataFrame retain();
    
    SpdyDataFrame retain(final int p0);
}
