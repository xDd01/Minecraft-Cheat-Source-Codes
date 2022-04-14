package io.github.nevalackin.client.impl.event.render.world;

import io.github.nevalackin.client.api.event.Event;

public final class GetCameraPositionEvent implements Event {

    private double pos;

    public GetCameraPositionEvent(double pos) {
        this.pos = pos;
    }

    public double getPos() {
        return pos;
    }

    public void setPos(double pos) {
        this.pos = pos;
    }
}
