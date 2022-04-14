package io.github.nevalackin.client.impl.event.player;

import io.github.nevalackin.client.api.event.Event;

public final class SendSprintStateEvent implements Event {

    private boolean sprintState;

    public SendSprintStateEvent(boolean sprintState) {
        this.sprintState = sprintState;
    }

    public boolean isSprintState() {
        return sprintState;
    }

    public void setSprintState(boolean sprintState) {
        this.sprintState = sprintState;
    }
}
