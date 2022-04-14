package io.netty.handler.codec.http.websocketx;

import java.net.*;
import io.netty.handler.codec.http.*;
import java.util.*;
import io.netty.channel.*;

public class WebSocketClientProtocolHandler extends WebSocketProtocolHandler
{
    private final WebSocketClientHandshaker handshaker;
    private final boolean handleCloseFrames;
    
    public WebSocketClientProtocolHandler(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength, final boolean handleCloseFrames) {
        this(WebSocketClientHandshakerFactory.newHandshaker(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength), handleCloseFrames);
    }
    
    public WebSocketClientProtocolHandler(final URI webSocketURL, final WebSocketVersion version, final String subprotocol, final boolean allowExtensions, final HttpHeaders customHeaders, final int maxFramePayloadLength) {
        this(webSocketURL, version, subprotocol, allowExtensions, customHeaders, maxFramePayloadLength, true);
    }
    
    public WebSocketClientProtocolHandler(final WebSocketClientHandshaker handshaker, final boolean handleCloseFrames) {
        this.handshaker = handshaker;
        this.handleCloseFrames = handleCloseFrames;
    }
    
    public WebSocketClientProtocolHandler(final WebSocketClientHandshaker handshaker) {
        this(handshaker, true);
    }
    
    @Override
    protected void decode(final ChannelHandlerContext ctx, final WebSocketFrame frame, final List<Object> out) throws Exception {
        if (this.handleCloseFrames && frame instanceof CloseWebSocketFrame) {
            ctx.close();
            return;
        }
        super.decode(ctx, frame, out);
    }
    
    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        final ChannelPipeline cp = ctx.pipeline();
        if (cp.get(WebSocketClientProtocolHandshakeHandler.class) == null) {
            ctx.pipeline().addBefore(ctx.name(), WebSocketClientProtocolHandshakeHandler.class.getName(), new WebSocketClientProtocolHandshakeHandler(this.handshaker));
        }
    }
    
    public enum ClientHandshakeStateEvent
    {
        HANDSHAKE_ISSUED, 
        HANDSHAKE_COMPLETE;
    }
}
