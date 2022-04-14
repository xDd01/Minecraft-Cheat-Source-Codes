/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;
import cafe.corrosion.event.attribute.EventCancellable;
import net.minecraft.network.Packet;

public class EventPacketOut
extends Event
implements EventCancellable {
    private Packet<?> packet;

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public <T extends Packet<?>> T getPacket() {
        return (T)this.packet;
    }

    public EventPacketOut(Packet<?> packet) {
        this.packet = packet;
    }

    public void setPacket(Packet<?> packet) {
        this.packet = packet;
    }
}

