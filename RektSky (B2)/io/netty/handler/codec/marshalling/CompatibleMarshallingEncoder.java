package io.netty.handler.codec.marshalling;

import io.netty.handler.codec.*;
import io.netty.channel.*;
import io.netty.buffer.*;
import org.jboss.marshalling.*;

@ChannelHandler.Sharable
public class CompatibleMarshallingEncoder extends MessageToByteEncoder<Object>
{
    private final MarshallerProvider provider;
    
    public CompatibleMarshallingEncoder(final MarshallerProvider provider) {
        this.provider = provider;
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final Object msg, final ByteBuf out) throws Exception {
        final Marshaller marshaller = this.provider.getMarshaller(ctx);
        marshaller.start((ByteOutput)new ChannelBufferByteOutput(out));
        marshaller.writeObject(msg);
        marshaller.finish();
        marshaller.close();
    }
}
