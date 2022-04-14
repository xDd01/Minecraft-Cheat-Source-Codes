package io.github.nevalackin.client.impl.event.render.game;

import io.github.nevalackin.client.api.event.Event;
import net.minecraft.client.gui.ScaledResolution;

public final class DrawScreenEvent implements Event {

    private final ScaledResolution scaledResolution;

    public DrawScreenEvent(ScaledResolution scaledResolution) {
        this.scaledResolution = scaledResolution;
    }

    public ScaledResolution getScaledResolution() {
        return scaledResolution;
    }
}
