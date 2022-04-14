package io.netty.handler.codec.protobuf;

import io.netty.handler.codec.*;
import io.netty.channel.*;
import com.google.protobuf.*;
import io.netty.buffer.*;
import java.io.*;

@ChannelHandler.Sharable
public class ProtobufVarint32LengthFieldPrepender extends MessageToByteEncoder<ByteBuf>
{
    @Override
    protected void encode(final ChannelHandlerContext ctx, final ByteBuf msg, final ByteBuf out) throws Exception {
        final int bodyLen = msg.readableBytes();
        final int headerLen = CodedOutputStream.computeRawVarint32Size(bodyLen);
        out.ensureWritable(headerLen + bodyLen);
        final CodedOutputStream headerOut = CodedOutputStream.newInstance((OutputStream)new ByteBufOutputStream(out), headerLen);
        headerOut.writeRawVarint32(bodyLen);
        headerOut.flush();
        out.writeBytes(msg, msg.readerIndex(), bodyLen);
    }
}
