package org.neverhook.client.feature.impl.misc;

import org.neverhook.client.NeverHook;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.packet.EventMessage;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;

public class ChatAppend extends Feature {

    public ChatAppend() {
        super("ChatAppend", "Дописывает название чита в сообщение", Type.Misc);
    }

    @EventTarget
    public void onChatMessage(EventMessage event) {
        if (event.getMessage().startsWith("/"))
            return;

        event.message = event.message + " | " + NeverHook.instance.name;
    }
}
