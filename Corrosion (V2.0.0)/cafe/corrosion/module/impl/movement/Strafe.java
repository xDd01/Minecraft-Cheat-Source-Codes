/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.movement;

import cafe.corrosion.event.impl.EventStrafe;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.util.player.MovementUtil;

@ModuleAttributes(name="Strafe", description="Allows you to strafe", category=Module.Category.MOVEMENT)
public class Strafe
extends Module {
    public Strafe() {
        this.registerEventHandler(EventStrafe.class, eventStrafe -> MovementUtil.setMotion(Math.hypot(Strafe.mc.thePlayer.motionX, Strafe.mc.thePlayer.motionZ)));
    }
}

