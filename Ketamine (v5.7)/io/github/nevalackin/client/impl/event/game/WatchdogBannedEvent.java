package io.github.nevalackin.client.impl.event.game;

import io.github.nevalackin.client.api.event.Event;
import net.minecraft.util.IChatComponent;

public final class WatchdogBannedEvent implements Event {

    private final IChatComponent reason;

    public WatchdogBannedEvent(IChatComponent reason) {
        this.reason = reason;
    }

    public IChatComponent getReason() {
        return reason;
    }
}
