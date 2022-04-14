package io.netty.handler.codec.socks;

import io.netty.buffer.*;

public final class UnknownSocksResponse extends SocksResponse
{
    public UnknownSocksResponse() {
        super(SocksResponseType.UNKNOWN);
    }
    
    @Override
    public void encodeAsByteBuf(final ByteBuf byteBuf) {
    }
}
