package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.*;
import io.netty.util.*;

public class BinaryWebSocketFrame extends WebSocketFrame
{
    public BinaryWebSocketFrame() {
        super(Unpooled.buffer(0));
    }
    
    public BinaryWebSocketFrame(final ByteBuf binaryData) {
        super(binaryData);
    }
    
    public BinaryWebSocketFrame(final boolean finalFragment, final int rsv, final ByteBuf binaryData) {
        super(finalFragment, rsv, binaryData);
    }
    
    @Override
    public BinaryWebSocketFrame copy() {
        return new BinaryWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().copy());
    }
    
    @Override
    public BinaryWebSocketFrame duplicate() {
        return new BinaryWebSocketFrame(this.isFinalFragment(), this.rsv(), this.content().duplicate());
    }
    
    @Override
    public BinaryWebSocketFrame retain() {
        super.retain();
        return this;
    }
    
    @Override
    public BinaryWebSocketFrame retain(final int increment) {
        super.retain(increment);
        return this;
    }
}
