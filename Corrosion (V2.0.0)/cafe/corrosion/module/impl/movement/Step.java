/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.movement;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;

@ModuleAttributes(name="Step", description="test", category=Module.Category.MOVEMENT)
public class Step
extends Module {
    public Step() {
        this.registerEventHandler(EventUpdate.class, eventUpdate -> {
            Step.mc.thePlayer.stepHeight = 1.0f;
        });
    }

    @Override
    public void onDisable() {
        Step.mc.thePlayer.stepHeight = 0.6f;
    }
}

