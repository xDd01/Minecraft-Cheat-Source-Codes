package io.netty.handler.codec.sctp;

import io.netty.handler.codec.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import java.util.*;
import io.netty.channel.sctp.*;

public class SctpOutboundByteStreamHandler extends MessageToMessageEncoder<ByteBuf>
{
    private final int streamIdentifier;
    private final int protocolIdentifier;
    
    public SctpOutboundByteStreamHandler(final int streamIdentifier, final int protocolIdentifier) {
        this.streamIdentifier = streamIdentifier;
        this.protocolIdentifier = protocolIdentifier;
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final List<Object> out) throws Exception {
        out.add(new SctpMessage(this.streamIdentifier, this.protocolIdentifier, msg.retain()));
    }
}
