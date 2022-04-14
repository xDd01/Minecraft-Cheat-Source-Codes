/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.combat;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.packet.PacketUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleAttributes(name="Regen", description="Increases the rate at which you regenerate health", category=Module.Category.COMBAT)
public class Regen
extends Module {
    private final NumberProperty packets = new NumberProperty(this, "Packets", 20, 1, 100, 1);
    private final NumberProperty health = new NumberProperty(this, "Health", 16, 1, 20, 0.5);
    private final BooleanProperty timerBypass = new BooleanProperty(this, "Timer bypass");
    private final BooleanProperty fastEat = new BooleanProperty(this, "Fast Eat");

    public Regen() {
        this.registerEventHandler(EventUpdate.class, eventUpdate -> {
            if (Regen.mc.thePlayer.getHealth() <= (float)((Number)this.health.getValue()).intValue() && (Regen.mc.thePlayer.getFoodStats().getFoodLevel() > 12 || Regen.mc.thePlayer.isEating() && ((Boolean)this.fastEat.getValue()).booleanValue()) && Regen.mc.thePlayer.isCollidedVertically) {
                for (int i2 = 0; i2 < ((Number)this.packets.getValue()).intValue(); ++i2) {
                    PacketUtil.send((Boolean)this.timerBypass.getValue() != false ? new C03PacketPlayer.C04PacketPlayerPosition(Regen.mc.thePlayer.posX, Regen.mc.thePlayer.posY, Regen.mc.thePlayer.posZ, true) : new C03PacketPlayer(true));
                }
            }
        });
    }
}

