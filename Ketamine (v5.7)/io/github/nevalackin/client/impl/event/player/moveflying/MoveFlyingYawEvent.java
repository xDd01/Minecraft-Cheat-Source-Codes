package io.github.nevalackin.client.impl.event.player.moveflying;

import io.github.nevalackin.client.api.event.Event;

public class MoveFlyingYawEvent implements Event {

    private float yaw;

    public MoveFlyingYawEvent(float yaw) {
        this.yaw = yaw;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }
}
