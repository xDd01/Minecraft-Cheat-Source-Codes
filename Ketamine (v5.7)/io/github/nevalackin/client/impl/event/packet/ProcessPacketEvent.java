package io.github.nevalackin.client.impl.event.packet;

import io.github.nevalackin.client.api.event.Event;
import net.minecraft.network.Packet;

public final class ProcessPacketEvent implements Event {

    private final Packet<?> packet;

    public ProcessPacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }
}
