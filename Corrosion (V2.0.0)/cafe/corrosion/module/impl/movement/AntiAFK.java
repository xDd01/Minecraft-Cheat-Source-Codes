/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.movement;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.NumberProperty;
import net.minecraft.util.MovementInput;

@ModuleAttributes(name="AntiAFK", description="Prevents you from being AFK kicked", category=Module.Category.PLAYER)
public class AntiAFK
extends Module {
    private final NumberProperty threshold = new NumberProperty(this, "Threshold (Seconds)", 30, 30, 500, 30);
    private int ticksSinceMovement;

    public AntiAFK() {
        this.registerEventHandler(EventUpdate.class, event -> {
            if (!event.isPre()) {
                return;
            }
            MovementInput input = AntiAFK.mc.thePlayer.movementInput;
            if (input.jump || input.sneak || input.moveForward > 0.0f) {
                this.ticksSinceMovement = 0;
            } else if (++this.ticksSinceMovement > ((Number)this.threshold.getValue()).intValue() * 20) {
                AntiAFK.mc.thePlayer.jump();
                this.ticksSinceMovement = 0;
            }
        });
    }
}

