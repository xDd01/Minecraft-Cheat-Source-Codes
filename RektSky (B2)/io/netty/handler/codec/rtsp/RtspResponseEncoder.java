package io.netty.handler.codec.rtsp;

import io.netty.buffer.*;
import io.netty.util.*;
import io.netty.handler.codec.http.*;

public class RtspResponseEncoder extends RtspObjectEncoder<HttpResponse>
{
    private static final byte[] CRLF;
    
    @Override
    public boolean acceptOutboundMessage(final Object msg) throws Exception {
        return msg instanceof FullHttpResponse;
    }
    
    @Override
    protected void encodeInitialLine(final ByteBuf buf, final HttpResponse response) throws Exception {
        HttpHeaders.encodeAscii(response.getProtocolVersion().toString(), buf);
        buf.writeByte(32);
        buf.writeBytes(String.valueOf(response.getStatus().code()).getBytes(CharsetUtil.US_ASCII));
        buf.writeByte(32);
        HttpObjectEncoder.encodeAscii(String.valueOf(response.getStatus().reasonPhrase()), buf);
        buf.writeBytes(RtspResponseEncoder.CRLF);
    }
    
    static {
        CRLF = new byte[] { 13, 10 };
    }
}
