package me.spec.eris.client.events.client;

import me.spec.eris.api.event.Event;
import me.spec.eris.utils.misc.Helper;

public class EventClientTick extends Event {

    @Override
    public void call() {
        Helper.onTick();
    }
}
