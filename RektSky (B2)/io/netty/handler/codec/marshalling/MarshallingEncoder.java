package io.netty.handler.codec.marshalling;

import io.netty.handler.codec.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import org.jboss.marshalling.*;

@ChannelHandler.Sharable
public class MarshallingEncoder extends MessageToByteEncoder<Object>
{
    private static final byte[] LENGTH_PLACEHOLDER;
    private final MarshallerProvider provider;
    
    public MarshallingEncoder(final MarshallerProvider provider) {
        this.provider = provider;
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
        final Marshaller marshaller = this.provider.getMarshaller(ctx);
        final int lengthPos = out.writerIndex();
        out.writeBytes(MarshallingEncoder.LENGTH_PLACEHOLDER);
        final ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
        marshaller.start((ByteOutput)output);
        marshaller.writeObject(msg);
        marshaller.finish();
        marshaller.close();
        out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
    }
    
    static {
        LENGTH_PLACEHOLDER = new byte[4];
    }
}
