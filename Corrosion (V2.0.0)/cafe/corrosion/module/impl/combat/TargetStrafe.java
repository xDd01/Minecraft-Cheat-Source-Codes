/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.combat;

import cafe.corrosion.Corrosion;
import cafe.corrosion.event.impl.EventStrafe;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.module.impl.combat.KillAura;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.player.RotationUtil;
import cafe.corrosion.util.player.extra.Rotation;
import net.minecraft.entity.EntityLivingBase;

@ModuleAttributes(name="TargetStrafe", description="Strafes around the current aura target.", category=Module.Category.COMBAT)
public class TargetStrafe
extends Module {
    private final NumberProperty range = new NumberProperty(this, "Range", 2, 0.5, 6, 0.25);
    private float strafeComponent = 1.0f;

    public TargetStrafe() {
        this.registerEventHandler(EventStrafe.class, eventStrafe -> {
            KillAura killAura = (KillAura)Corrosion.INSTANCE.getModuleManager().getModule(KillAura.class);
            if (killAura.isEnabled() && killAura.getCurrentEntity() != null) {
                EntityLivingBase target = killAura.getCurrentEntity();
                Rotation rotation = killAura.getCurrentRotations();
                eventStrafe.setYaw(rotation == null ? RotationUtil.getRotationsRandom(target, false).getRotationYaw() : rotation.getRotationYaw());
                eventStrafe.setForward(this.getForwardComponent(target));
                if (TargetStrafe.mc.thePlayer.isCollidedHorizontally) {
                    this.strafeComponent = -this.strafeComponent;
                }
                eventStrafe.setStrafe(this.strafeComponent);
            }
        });
    }

    public float getForwardComponent(EntityLivingBase entity) {
        return TargetStrafe.mc.thePlayer.getDistanceToEntity(entity) <= ((Number)this.range.getValue()).floatValue() ? 0.0f : 1.0f;
    }
}

