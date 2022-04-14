package io.netty.channel.udt.nio;

import com.barchart.udt.*;
import com.barchart.udt.nio.*;

public class NioUdtByteRendezvousChannel extends NioUdtByteConnectorChannel
{
    public NioUdtByteRendezvousChannel() {
        super((SocketChannelUDT)NioUdtProvider.newRendezvousChannelUDT(TypeUDT.STREAM));
    }
}
