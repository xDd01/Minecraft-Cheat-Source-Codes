package io.github.nevalackin.client.impl.event.world;

import io.github.nevalackin.client.api.event.Event;
import net.minecraft.client.multiplayer.WorldClient;

public final class LoadWorldEvent implements Event {

    private final WorldClient worldClient;

    public LoadWorldEvent(WorldClient worldClient) {
        this.worldClient = worldClient;
    }

    public WorldClient getWorldClient() {
        return worldClient;
    }
}
