/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.player;

import cafe.corrosion.event.impl.EventPacketOut;
import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.NumberProperty;
import net.minecraft.item.ItemFireball;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;

@ModuleAttributes(name="FireballBooster", description="Increases the amount of velocity you take while taking fireball damage", category=Module.Category.PLAYER)
public class FireballBooster
extends Module {
    private final NumberProperty horizontalBoost = new NumberProperty(this, "Horizontal Boost", 3.7, 0.01, 5.01, 0.01);
    private final NumberProperty verticalBoost = new NumberProperty(this, "Vertical Boost", 3.7, 0.01, 5.01, 0.01);
    private boolean boost;

    public FireballBooster() {
        this.registerEventHandler(EventPacketOut.class, event -> {
            if (event.getPacket() instanceof C08PacketPlayerBlockPlacement && FireballBooster.mc.thePlayer.getHeldItem() != null && FireballBooster.mc.thePlayer.getHeldItem().getItem() instanceof ItemFireball) {
                this.boost = true;
            }
        });
        this.registerEventHandler(EventUpdate.class, event -> {
            if (!this.boost || !event.isPre()) {
                return;
            }
            float yaw = FireballBooster.mc.thePlayer.rotationYaw;
            double pi2 = Math.PI;
            double horizontalMotion = -1.0 * Math.sin((double)yaw * pi2 / 180.0) * ((Number)this.horizontalBoost.getValue()).doubleValue();
            double verticalMotion = ((Number)this.verticalBoost.getValue()).doubleValue();
            FireballBooster.mc.thePlayer.motionX = horizontalMotion;
            FireballBooster.mc.thePlayer.motionY = verticalMotion;
            FireballBooster.mc.thePlayer.motionZ = horizontalMotion;
            this.boost = false;
        });
    }

    public NumberProperty getHorizontalBoost() {
        return this.horizontalBoost;
    }

    public NumberProperty getVerticalBoost() {
        return this.verticalBoost;
    }

    public boolean isBoost() {
        return this.boost;
    }
}

