package io.netty.handler.codec.bytes;

import io.netty.handler.codec.*;
import io.netty.channel.*;
import java.util.*;
import io.netty.buffer.*;

@ChannelHandler.Sharable
public class ByteArrayEncoder extends MessageToMessageEncoder<byte[]>
{
    @Override
    protected void encode(final ChannelHandlerContext ctx, final byte[] msg, final List<Object> out) throws Exception {
        out.add(Unpooled.wrappedBuffer(msg));
    }
}
