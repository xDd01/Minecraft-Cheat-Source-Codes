package io.netty.channel.udt.nio;

import com.barchart.udt.*;
import java.util.*;
import io.netty.channel.*;
import com.barchart.udt.nio.*;

public class NioUdtByteAcceptorChannel extends NioUdtAcceptorChannel
{
    private static final ChannelMetadata METADATA;
    
    public NioUdtByteAcceptorChannel() {
        super(TypeUDT.STREAM);
    }
    
    @Override
    protected int doReadMessages(final List<Object> buf) throws Exception {
        final SocketChannelUDT channelUDT = this.javaChannel().accept();
        if (channelUDT == null) {
            return 0;
        }
        buf.add(new NioUdtByteConnectorChannel(this, channelUDT));
        return 1;
    }
    
    @Override
    public ChannelMetadata metadata() {
        return NioUdtByteAcceptorChannel.METADATA;
    }
    
    static {
        METADATA = new ChannelMetadata(false);
    }
}
