package io.github.nevalackin.client.impl.event.packet;

import io.github.nevalackin.client.api.event.CancellableEvent;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;

@SuppressWarnings("rawtypes")
public final class ReceivePacketEvent<T extends INetHandler> extends CancellableEvent {

    private final T packetListener;
    private final Packet packet;

    public ReceivePacketEvent(Packet packet, T packetListener) {
        this.packet = packet;
        this.packetListener = packetListener;
    }

    public T getPacketListener() {
        return packetListener;
    }

    public Packet getPacket() {
        return packet;
    }
}
