package org.neverhook.client.event.events.impl.packet;

import org.neverhook.client.event.events.Event;
import org.neverhook.client.event.events.callables.EventCancellable;

public class EventMessage extends EventCancellable implements Event {

    public String message;

    public EventMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
