package io.netty.handler.codec.string;

import io.netty.handler.codec.*;
import java.nio.charset.*;
import io.netty.channel.*;
import java.util.*;
import java.nio.*;
import io.netty.buffer.*;

@ChannelHandler.Sharable
public class StringEncoder extends MessageToMessageEncoder<CharSequence>
{
    private final Charset charset;
    
    public StringEncoder() {
        this(Charset.defaultCharset());
    }
    
    public StringEncoder(final Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }
    
    @Override
    protected void encode(final ChannelHandlerContext ctx, final CharSequence msg, final List<Object> out) throws Exception {
        if (msg.length() == 0) {
            return;
        }
        out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(msg), this.charset));
    }
}
