package io.netty.handler.codec.socks;

import io.netty.handler.codec.*;
import io.netty.channel.*;
import io.netty.buffer.*;

@ChannelHandler.Sharable
public class SocksMessageEncoder extends MessageToByteEncoder<SocksMessage>
{
    private static final String name = "SOCKS_MESSAGE_ENCODER";
    
    @Deprecated
    public static String getName() {
        return "SOCKS_MESSAGE_ENCODER";
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final SocksMessage msg, final ByteBuf out) throws Exception {
        msg.encodeAsByteBuf(out);
    }
}
