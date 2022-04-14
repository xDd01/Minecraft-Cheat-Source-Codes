package io.netty.handler.codec.sctp;

import io.netty.handler.codec.*;
import io.netty.channel.sctp.*;
import io.netty.channel.*;
import java.util.*;
import io.netty.buffer.*;

public class SctpMessageCompletionHandler extends MessageToMessageDecoder<SctpMessage>
{
    private final Map<Integer, ByteBuf> fragments;
    
    public SctpMessageCompletionHandler() {
        this.fragments = new HashMap<Integer, ByteBuf>();
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final SctpMessage msg, final List<Object> out) throws Exception {
        final ByteBuf byteBuf = msg.content();
        final int protocolIdentifier = msg.protocolIdentifier();
        final int streamIdentifier = msg.streamIdentifier();
        final boolean isComplete = msg.isComplete();
        ByteBuf frag;
        if (this.fragments.containsKey(streamIdentifier)) {
            frag = this.fragments.remove(streamIdentifier);
        }
        else {
            frag = Unpooled.EMPTY_BUFFER;
        }
        if (isComplete && !frag.isReadable()) {
            out.add(msg);
        }
        else if (!isComplete && frag.isReadable()) {
            this.fragments.put(streamIdentifier, Unpooled.wrappedBuffer(frag, byteBuf));
        }
        else if (isComplete && frag.isReadable()) {
            this.fragments.remove(streamIdentifier);
            final SctpMessage assembledMsg = new SctpMessage(protocolIdentifier, streamIdentifier, Unpooled.wrappedBuffer(frag, byteBuf));
            out.add(assembledMsg);
        }
        else {
            this.fragments.put(streamIdentifier, byteBuf);
        }
        byteBuf.retain();
    }
}
