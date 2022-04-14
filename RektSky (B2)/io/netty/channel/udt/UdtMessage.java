package io.netty.channel.udt;

import io.netty.buffer.*;
import io.netty.util.*;

public final class UdtMessage extends DefaultByteBufHolder
{
    public UdtMessage(final ByteBuf data) {
        super(data);
    }
    
    @Override
    public UdtMessage copy() {
        return new UdtMessage(this.content().copy());
    }
    
    @Override
    public UdtMessage duplicate() {
        return new UdtMessage(this.content().duplicate());
    }
    
    @Override
    public UdtMessage retain() {
        super.retain();
        return this;
    }
    
    @Override
    public UdtMessage retain(final int increment) {
        super.retain(increment);
        return this;
    }
}
