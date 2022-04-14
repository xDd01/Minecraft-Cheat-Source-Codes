package io.netty.handler.codec.marshalling;

import io.netty.channel.*;
import io.netty.buffer.*;
import java.util.*;
import io.netty.handler.codec.*;
import org.jboss.marshalling.*;

public class CompatibleMarshallingDecoder extends ReplayingDecoder<Void>
{
    protected final UnmarshallerProvider provider;
    protected final int maxObjectSize;
    private boolean discardingTooLongFrame;
    
    public CompatibleMarshallingDecoder(final UnmarshallerProvider provider, final int maxObjectSize) {
        this.provider = provider;
        this.maxObjectSize = maxObjectSize;
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf buffer, final List<Object> out) throws Exception {
        if (this.discardingTooLongFrame) {
            buffer.skipBytes(this.actualReadableBytes());
            this.checkpoint();
            return;
        }
        final Unmarshaller unmarshaller = this.provider.getUnmarshaller(ctx);
        ByteInput input = (ByteInput)new ChannelBufferByteInput(buffer);
        if (this.maxObjectSize != Integer.MAX_VALUE) {
            input = (ByteInput)new LimitingByteInput(input, this.maxObjectSize);
        }
        try {
            unmarshaller.start(input);
            final Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            out.add(obj);
        }
        catch (LimitingByteInput.TooBigObjectException ignored) {
            this.discardingTooLongFrame = true;
            throw new TooLongFrameException();
        }
        finally {
            unmarshaller.close();
        }
    }
    
    @Override
    protected void decodeLast(final ChannelHandlerContext ctx, final ByteBuf buffer, final List<Object> out) throws Exception {
        switch (buffer.readableBytes()) {
            case 0: {
                return;
            }
            case 1: {
                if (buffer.getByte(buffer.readerIndex()) == 121) {
                    buffer.skipBytes(1);
                    return;
                }
                break;
            }
        }
        this.decode(ctx, buffer, out);
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        if (cause instanceof TooLongFrameException) {
            ctx.close();
        }
        else {
            super.exceptionCaught(ctx, cause);
        }
    }
}
