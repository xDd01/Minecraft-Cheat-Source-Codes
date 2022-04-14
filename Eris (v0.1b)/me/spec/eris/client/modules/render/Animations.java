package me.spec.eris.client.modules.render;

import me.spec.eris.api.event.Event;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.value.types.BooleanValue;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.api.value.types.NumberValue;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.client.modules.movement.NoSlowDown;

public class Animations extends Module {

    public ModeValue<Mode> mode = new ModeValue<Mode>("Mode", Mode.DEFAULT, this);
    public NumberValue<Double> swingSpeed = new NumberValue<Double>("Swing Speed", 6D, 1D, 20D, this);
    public BooleanValue<Boolean> farSword = new BooleanValue<Boolean>("Far Sword", false, this);
    public enum Mode {	EPIC, PUNCH, SLIDE2, HELIUM, POKE, SKID, SPIN, MEME, SLIDE, SLIDE3, SKIDMIX ,DEFAULT
    }
    public Animations(String racism) {
        super("Animations", ModuleCategory.RENDER, racism);
    }

    @Override
    public void onEvent(Event e) {
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
