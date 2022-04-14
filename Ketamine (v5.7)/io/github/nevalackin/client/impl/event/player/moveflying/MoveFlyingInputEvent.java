package io.github.nevalackin.client.impl.event.player.moveflying;

import io.github.nevalackin.client.api.event.Event;

public class MoveFlyingInputEvent implements Event {

    private float forward;
    private float strafe;

    public MoveFlyingInputEvent(float forward, float strafe) {
        this.forward = forward;
        this.strafe = strafe;
    }

    public float getForward() {
        return forward;
    }

    public void setForward(float forward) {
        this.forward = forward;
    }

    public float getStrafe() {
        return strafe;
    }

    public void setStrafe(float strafe) {
        this.strafe = strafe;
    }
}
