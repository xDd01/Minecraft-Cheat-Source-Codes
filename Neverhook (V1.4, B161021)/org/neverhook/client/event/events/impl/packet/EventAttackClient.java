package org.neverhook.client.event.events.impl.packet;

import net.minecraft.entity.Entity;
import org.neverhook.client.event.events.callables.EventCancellable;

public class EventAttackClient extends EventCancellable {

    private final Entity entity;

    public EventAttackClient(Entity targetEntity) {
        this.entity = targetEntity;
    }

    public Entity getEntity() {
        return entity;
    }
}
