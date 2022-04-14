package me.spec.eris.client.events.client;

import me.spec.eris.api.event.Event;
import net.minecraft.network.Packet;

public class EventPacket extends Event {

    private boolean sending;
    private Packet packet;

    public EventPacket(Packet packet, boolean sending) {
        this.sending = sending;
        this.packet = packet;
    }

    public boolean isSending() {
        return this.sending;
    }

    public boolean isReceiving() {
        return !this.sending;
    }

    public Packet getPacket() {
        return this.packet;
    }

    public void setPacket(Packet packet) {
        this.packet = packet;
    }

    @Override
    public void call() {
        super.call();
    }
}
