package io.netty.handler.codec.http.websocketx;

import io.netty.handler.codec.*;
import io.netty.channel.*;
import java.util.*;

abstract class WebSocketProtocolHandler extends MessageToMessageDecoder<WebSocketFrame>
{
    @Override
    protected void decode(final ChannelHandlerContext ctx, final WebSocketFrame frame, final List<Object> out) throws Exception {
        if (frame instanceof PingWebSocketFrame) {
            frame.content().retain();
            ctx.channel().writeAndFlush(new PongWebSocketFrame(frame.content()));
            return;
        }
        if (frame instanceof PongWebSocketFrame) {
            return;
        }
        out.add(frame.retain());
    }
    
    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
        ctx.close();
    }
}
