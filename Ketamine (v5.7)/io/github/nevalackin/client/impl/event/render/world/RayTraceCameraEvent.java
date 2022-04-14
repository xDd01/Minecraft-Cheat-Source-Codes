package io.github.nevalackin.client.impl.event.render.world;

import io.github.nevalackin.client.api.event.CancellableEvent;

public final class RayTraceCameraEvent extends CancellableEvent {

    private double distance;

    public RayTraceCameraEvent(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
