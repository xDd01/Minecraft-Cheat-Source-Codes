package io.netty.handler.codec.protobuf;

import io.netty.handler.codec.*;
import io.netty.channel.*;
import java.util.*;
import com.google.protobuf.*;
import io.netty.buffer.*;

@ChannelHandler.Sharable
public class ProtobufEncoder extends MessageToMessageEncoder<MessageLiteOrBuilder>
{
    @Override
    protected void encode(final ChannelHandlerContext ctx, final MessageLiteOrBuilder msg, final List<Object> out) throws Exception {
        if (msg instanceof MessageLite) {
            out.add(Unpooled.wrappedBuffer(((MessageLite)msg).toByteArray()));
            return;
        }
        if (msg instanceof MessageLite.Builder) {
            out.add(Unpooled.wrappedBuffer(((MessageLite.Builder)msg).build().toByteArray()));
        }
    }
}
