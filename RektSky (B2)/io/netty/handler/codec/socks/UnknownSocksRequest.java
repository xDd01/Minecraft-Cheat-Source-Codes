package io.netty.handler.codec.socks;

import io.netty.buffer.*;

public final class UnknownSocksRequest extends SocksRequest
{
    public UnknownSocksRequest() {
        super(SocksRequestType.UNKNOWN);
    }
    
    @Override
    public void encodeAsByteBuf(final ByteBuf byteBuf) {
    }
}
