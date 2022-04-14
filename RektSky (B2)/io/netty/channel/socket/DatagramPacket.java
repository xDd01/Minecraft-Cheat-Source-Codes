package io.netty.channel.socket;

import java.net.*;
import io.netty.buffer.*;
import io.netty.channel.*;
import io.netty.util.*;

public final class DatagramPacket extends DefaultAddressedEnvelope<ByteBuf, InetSocketAddress> implements ByteBufHolder
{
    public DatagramPacket(final ByteBuf data, final InetSocketAddress recipient) {
        super(data, recipient);
    }
    
    public DatagramPacket(final ByteBuf data, final InetSocketAddress recipient, final InetSocketAddress sender) {
        super(data, recipient, sender);
    }
    
    @Override
    public DatagramPacket copy() {
        return new DatagramPacket(((DefaultAddressedEnvelope<ByteBuf, A>)this).content().copy(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).recipient(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).sender());
    }
    
    @Override
    public DatagramPacket duplicate() {
        return new DatagramPacket(((DefaultAddressedEnvelope<ByteBuf, A>)this).content().duplicate(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).recipient(), ((DefaultAddressedEnvelope<M, InetSocketAddress>)this).sender());
    }
    
    @Override
    public DatagramPacket retain() {
        super.retain();
        return this;
    }
    
    @Override
    public DatagramPacket retain(final int increment) {
        super.retain(increment);
        return this;
    }
}
