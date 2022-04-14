/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.movement;

import cafe.corrosion.event.impl.EventStrafe;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.util.nameable.INameable;
import cafe.corrosion.util.packet.PacketUtil;
import cafe.corrosion.util.player.PlayerUtil;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleAttributes(name="LongJump", description="Nigger Launcher 69420", category=Module.Category.MOVEMENT)
public class LongJump
extends Module {
    private final EnumProperty<Mode> mode = new EnumProperty((Module)this, "Mode", (INameable[])Mode.values());
    private double hDist;
    private boolean fast;

    public LongJump() {
        this.registerEventHandler(EventStrafe.class, eventStrafe -> {
            switch ((Mode)this.mode.getValue()) {
                case HYPIXEL: {
                    if (LongJump.mc.thePlayer.onGround && LongJump.mc.thePlayer.hurtTime == 0) {
                        eventStrafe.setMotion(0.0);
                        return;
                    }
                    if (LongJump.mc.thePlayer.onGround) {
                        LongJump.mc.thePlayer.motionY = 0.6f;
                        eventStrafe.setMotion(PlayerUtil.getNCPBaseSpeed() * 1.7);
                        break;
                    }
                    if (this.fast) {
                        eventStrafe.setMotion(PlayerUtil.getNCPBaseSpeed() * 1.5);
                        LongJump.mc.thePlayer.motionY += 0.2;
                        this.fast = false;
                        break;
                    }
                    if (LongJump.mc.thePlayer.hurtTime <= 0) break;
                    eventStrafe.setFriction(eventStrafe.getFriction() * 2.7f);
                    break;
                }
            }
        });
    }

    @Override
    public void onEnable() {
        double x2 = LongJump.mc.thePlayer.posX;
        double y2 = LongJump.mc.thePlayer.posY;
        double z2 = LongJump.mc.thePlayer.posZ;
        for (int i2 = 0; i2 < 65; ++i2) {
            PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2 + 0.0624399212, z2, false));
            PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2, z2, false));
        }
        PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2, z2, true));
    }

    private static enum Mode implements INameable
    {
        VANILLA("Vanilla"),
        HYPIXEL("Hypixel"),
        VERUS("Verus");

        private final String name;

        private Mode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}

