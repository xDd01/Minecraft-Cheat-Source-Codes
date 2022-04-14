package io.netty.handler.codec.rtsp;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

@ChannelHandler.Sharable
public abstract class RtspObjectEncoder<H extends HttpMessage> extends HttpObjectEncoder<H>
{
    protected RtspObjectEncoder() {
    }
    
    @Override
    public boolean acceptOutboundMessage(final Object msg) throws Exception {
        return msg instanceof FullHttpMessage;
    }
}
