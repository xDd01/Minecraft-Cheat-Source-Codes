package io.netty.handler.codec.spdy;

import io.netty.channel.*;

public final class SpdyHttpCodec extends CombinedChannelDuplexHandler<SpdyHttpDecoder, SpdyHttpEncoder>
{
    public SpdyHttpCodec(final SpdyVersion version, final int maxContentLength) {
        super(new SpdyHttpDecoder(version, maxContentLength), new SpdyHttpEncoder(version));
    }
    
    public SpdyHttpCodec(final SpdyVersion version, final int maxContentLength, final boolean validateHttpHeaders) {
        super(new SpdyHttpDecoder(version, maxContentLength, validateHttpHeaders), new SpdyHttpEncoder(version));
    }
}
