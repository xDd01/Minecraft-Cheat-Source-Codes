package net.minecraft.network.play.server;

import java.io.*;
import io.netty.buffer.*;
import net.minecraft.network.play.*;
import net.minecraft.network.*;

public class S3FPacketCustomPayload implements Packet
{
    private String channel;
    private PacketBuffer data;
    
    public S3FPacketCustomPayload() {
    }
    
    public S3FPacketCustomPayload(final String channelName, final PacketBuffer dataIn) {
        this.channel = channelName;
        this.data = dataIn;
        if (dataIn.writerIndex() > 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }
    
    @Override
    public void readPacketData(final PacketBuffer data) throws IOException {
        this.channel = data.readStringFromBuffer(20);
        final int var2 = data.readableBytes();
        if (var2 >= 0 && var2 <= 1048576) {
            this.data = new PacketBuffer(data.readBytes(var2));
            return;
        }
        throw new IOException("Payload may not be larger than 1048576 bytes");
    }
    
    @Override
    public void writePacketData(final PacketBuffer data) throws IOException {
        data.writeString(this.channel);
        data.writeBytes(this.data);
    }
    
    public void process(final INetHandlerPlayClient p_180734_1_) {
        p_180734_1_.handleCustomPayload(this);
    }
    
    public String getChannelName() {
        return this.channel;
    }
    
    public PacketBuffer getBufferData() {
        return this.data;
    }
    
    @Override
    public void processPacket(final INetHandler handler) {
        this.process((INetHandlerPlayClient)handler);
    }
}
