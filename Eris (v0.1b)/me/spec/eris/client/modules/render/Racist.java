package me.spec.eris.client.modules.render;

import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.events.client.EventPacket;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;

public class Racist extends Module {

    public Racist(String racism) {
        super("Racist", ModuleCategory.RENDER, racism);
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {

        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}
