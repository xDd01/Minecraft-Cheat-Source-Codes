package io.github.nevalackin.client.impl.event.render.game;

import io.github.nevalackin.client.api.event.Event;

public class GetThirdPersonViewEvent implements Event {

    private int thirdPersonView;

    public GetThirdPersonViewEvent(int thirdPersonView) {
        this.thirdPersonView = thirdPersonView;
    }

    public int getThirdPersonView() {
        return thirdPersonView;
    }

    public void setThirdPersonView(int thirdPersonView) {
        this.thirdPersonView = thirdPersonView;
    }
}
