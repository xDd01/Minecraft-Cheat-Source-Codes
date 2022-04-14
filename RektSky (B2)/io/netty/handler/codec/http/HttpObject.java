package io.netty.handler.codec.http;

import io.netty.handler.codec.*;

public interface HttpObject
{
    DecoderResult getDecoderResult();
    
    void setDecoderResult(final DecoderResult p0);
}
