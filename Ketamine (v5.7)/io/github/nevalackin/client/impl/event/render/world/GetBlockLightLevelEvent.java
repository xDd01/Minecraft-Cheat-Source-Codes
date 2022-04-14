package io.github.nevalackin.client.impl.event.render.world;

import io.github.nevalackin.client.api.event.Event;

public final class GetBlockLightLevelEvent implements Event {

    private int lightLevel;

    public GetBlockLightLevelEvent(int lightLevel) {
        this.lightLevel = lightLevel;
    }

    public int getLightLevel() {
        return lightLevel;
    }

    public void setLightLevel(int lightLevel) {
        this.lightLevel = lightLevel;
    }
}
