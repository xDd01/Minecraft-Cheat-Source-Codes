package io.netty.handler.codec.string;

import io.netty.handler.codec.*;
import io.netty.buffer.*;
import java.nio.charset.*;
import io.netty.channel.*;
import java.util.*;

@ChannelHandler.Sharable
public class StringDecoder extends MessageToMessageDecoder<ByteBuf>
{
    private final Charset charset;
    
    public StringDecoder() {
        this(Charset.defaultCharset());
    }
    
    public StringDecoder(final Charset charset) {
        if (charset == null) {
            throw new NullPointerException("charset");
        }
        this.charset = charset;
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf msg, final List<Object> out) throws Exception {
        out.add(msg.toString(this.charset));
    }
}
