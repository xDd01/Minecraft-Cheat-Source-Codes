package io.netty.handler.codec.serialization;

import io.netty.handler.codec.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import java.io.*;

@ChannelHandler.Sharable
public class ObjectEncoder extends MessageToByteEncoder<Serializable>
{
    private static final byte[] LENGTH_PLACEHOLDER;
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final Serializable msg, final ByteBuf out) throws Exception {
        final int startIdx = out.writerIndex();
        final ByteBufOutputStream bout = new ByteBufOutputStream(out);
        bout.write(ObjectEncoder.LENGTH_PLACEHOLDER);
        final ObjectOutputStream oout = new CompactObjectOutputStream(bout);
        oout.writeObject(msg);
        oout.flush();
        oout.close();
        final int endIdx = out.writerIndex();
        out.setInt(startIdx, endIdx - startIdx - 4);
    }
    
    static {
        LENGTH_PLACEHOLDER = new byte[4];
    }
}
