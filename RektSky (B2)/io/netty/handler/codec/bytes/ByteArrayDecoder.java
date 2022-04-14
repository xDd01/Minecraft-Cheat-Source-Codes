package io.netty.handler.codec.bytes;

import io.netty.handler.codec.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import java.util.*;

public class ByteArrayDecoder extends MessageToMessageDecoder<ByteBuf>
{
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf msg, final List<Object> out) throws Exception {
        final byte[] array = new byte[msg.readableBytes()];
        msg.getBytes(0, array);
        out.add(array);
    }
}
