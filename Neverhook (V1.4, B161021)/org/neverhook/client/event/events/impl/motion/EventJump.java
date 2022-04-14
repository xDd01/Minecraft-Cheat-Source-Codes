package org.neverhook.client.event.events.impl.motion;

import org.neverhook.client.event.events.Event;
import org.neverhook.client.event.events.callables.EventCancellable;

public class EventJump extends EventCancellable implements Event {

    private float yaw;

    public EventJump(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}