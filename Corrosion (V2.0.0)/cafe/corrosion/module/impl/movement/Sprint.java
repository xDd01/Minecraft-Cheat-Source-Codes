/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.movement;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import net.minecraft.potion.Potion;

@ModuleAttributes(name="Sprint", description="Automatically sprints for you", category=Module.Category.MOVEMENT)
public class Sprint
extends Module {
    private final BooleanProperty blindness = new BooleanProperty(this, "Ignore Blindness");
    private final BooleanProperty hunger = new BooleanProperty(this, "Ignore Hunger");
    private final BooleanProperty omni = new BooleanProperty(this, "Omni");

    public Sprint() {
        this.registerEventHandler(EventUpdate.class, event -> {
            if (!event.isPre()) {
                return;
            }
            boolean backwards = Sprint.mc.thePlayer.moveForward <= 0.0f;
            boolean moving = Sprint.mc.thePlayer.moveStrafing != 0.0f || Sprint.mc.thePlayer.moveForward != 0.0f;
            boolean invalid = !moving || Sprint.mc.thePlayer.isSneaking() || Sprint.mc.thePlayer.isCollidedHorizontally || (Boolean)this.blindness.getValue() == false && Sprint.mc.thePlayer.isPotionActive(Potion.blindness) || (Boolean)this.hunger.getValue() == false && (float)Sprint.mc.thePlayer.getFoodStats().getFoodLevel() < 6.0f || (Boolean)this.omni.getValue() == false && backwards;
            Sprint.mc.thePlayer.setSprinting(!invalid);
        });
    }
}

