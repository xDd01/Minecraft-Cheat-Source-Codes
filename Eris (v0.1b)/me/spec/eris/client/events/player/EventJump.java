package me.spec.eris.client.events.player;

import me.spec.eris.api.event.Event;

public class EventJump extends Event {
    public double motionY;

    public EventJump(double motionY) {
        this.motionY = motionY;
    }

    public void setMotionY(double d) {
        this.motionY = d;
    }

    public double getMotionY() {
        return motionY;
    }
}
