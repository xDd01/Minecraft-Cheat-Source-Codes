package io.github.nevalackin.client.impl.event.game;

import io.github.nevalackin.client.api.event.Event;

public final class GetTimerSpeedEvent implements Event {

    private double timerSpeed;
    public static double lastTimerSpeed;


    public GetTimerSpeedEvent(double timerSpeed) {
        this.timerSpeed = timerSpeed;
    }

    public double getTimerSpeed() {
        return lastTimerSpeed = timerSpeed;
    }

    public void setTimerSpeed(double timerSpeed) {
        this.timerSpeed = timerSpeed;
    }
}
