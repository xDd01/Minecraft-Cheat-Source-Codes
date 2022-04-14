/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.misc;

import cafe.corrosion.event.impl.EventTick;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.NumberProperty;
import java.text.DecimalFormat;

@ModuleAttributes(name="Timer", description="Modifies your game's tickrate", category=Module.Category.MISC)
public class Timer
extends Module {
    private final NumberProperty timerSpeed = new NumberProperty(this, "Timer Speed", 1.0, 0.05, 10.0, 0.1);

    public Timer() {
        this.registerEventHandler(EventTick.class, eventTick -> {
            Timer.mc.timer.timerSpeed = ((Number)this.timerSpeed.getValue()).floatValue();
        });
    }

    @Override
    public void onDisable() {
        Timer.mc.timer.timerSpeed = 1.0f;
    }

    @Override
    public String getMode() {
        return DecimalFormat.getNumberInstance().format(this.timerSpeed.getValue());
    }
}

