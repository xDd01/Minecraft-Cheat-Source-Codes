package me.spec.eris.client.events.render;

import me.spec.eris.api.event.Event;
import net.minecraft.entity.Entity;

public class EventEntityRender extends Event {
    private float partialTicks;
    private Entity entity;

    public EventEntityRender(float partialTicks, Entity entity) {
        this.partialTicks = partialTicks;
        this.entity = entity;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}
