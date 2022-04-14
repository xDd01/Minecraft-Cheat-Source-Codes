/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.combat.auto;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.combat.ClickRandomizer;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.util.MovingObjectPosition;

@ModuleAttributes(name="TriggerBot", description="Automatically attacks entities while looking at them", category=Module.Category.COMBAT)
public class TriggerBot
extends Module {
    private final NumberProperty minCps = new NumberProperty(this, "Min CPS", 12, 0, 20, 1);
    private final NumberProperty maxCps = new NumberProperty(this, "Max CPS", 14, 1, 20, 1);
    private final NumberProperty randomizationLevel = new NumberProperty(this, "Randomization Level", 1, 0, 5, 1);
    private final BooleanProperty useRandomization = new BooleanProperty((Module)this, "Use Randomization", true);
    private final ClickRandomizer randomizer = new ClickRandomizer(this.minCps, this.maxCps, this.randomizationLevel, this.useRandomization);

    public TriggerBot() {
        this.registerEventHandler(EventUpdate.class, event -> {
            if (!event.isPre() || TriggerBot.mc.currentScreen != null) {
                return;
            }
            MovingObjectPosition objectPosition = TriggerBot.mc.objectMouseOver;
            if (objectPosition == null || objectPosition.entityHit == null || objectPosition.typeOfHit != MovingObjectPosition.MovingObjectType.ENTITY) {
                return;
            }
            if (objectPosition.entityHit instanceof EntityArmorStand) {
                return;
            }
            if (this.randomizer.hasElapsed()) {
                mc.clickMouse();
            }
        });
    }
}

