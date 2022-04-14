package io.github.nevalackin.client.impl.event.world;

import io.github.nevalackin.client.api.event.Event;

public final class UpdateWorldTimeEvent implements Event {

    private long time;

    public UpdateWorldTimeEvent(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
