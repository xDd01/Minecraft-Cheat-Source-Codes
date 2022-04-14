package io.netty.handler.codec.spdy;

public interface SpdySynReplyFrame extends SpdyHeadersFrame
{
    SpdySynReplyFrame setStreamId(final int p0);
    
    SpdySynReplyFrame setLast(final boolean p0);
    
    SpdySynReplyFrame setInvalid();
}
