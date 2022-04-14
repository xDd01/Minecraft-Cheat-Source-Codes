/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.combat;

import cafe.corrosion.Corrosion;
import cafe.corrosion.event.impl.EventPacketIn;
import cafe.corrosion.event.impl.EventStrafe;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.module.impl.player.FireballBooster;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.nameable.INameable;
import java.util.function.Predicate;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

@ModuleAttributes(name="Velocity", description="Modifies how much velocity you take", category=Module.Category.COMBAT)
public class Velocity
extends Module {
    private final Predicate<Packet<?>> processPredicate = packet -> packet instanceof S12PacketEntityVelocity || packet instanceof S27PacketExplosion;
    private final EnumProperty<Mode> velocityMode = new EnumProperty((Module)this, "Mode", (INameable[])Mode.values());
    private final NumberProperty horizontalMod = new NumberProperty(this, "Horizontal", 0, 0, 100, 1);
    private final NumberProperty verticalMod = new NumberProperty(this, "Vertical", 0, 0, 100, 1);
    private final NumberProperty multiplier = new NumberProperty(this, "Motion Multiplier", Float.valueOf(0.91f), Float.valueOf(0.1f), Float.valueOf(0.99f), 0.1);
    private final NumberProperty maxStack = new NumberProperty(this, "Min Velocity", 2, 2, 5, 1);
    private int stackedAmount;

    public Velocity() {
        this.horizontalMod.setHidden(() -> !((Mode)this.velocityMode.getValue()).equals(Mode.NORMAL));
        this.verticalMod.setHidden(() -> !((Mode)this.velocityMode.getValue()).equals(Mode.NORMAL));
        this.multiplier.setHidden(() -> !((Mode)this.velocityMode.getValue()).equals(Mode.REDUCE));
        this.maxStack.setHidden(() -> !((Mode)this.velocityMode.getValue()).equals(Mode.STACK));
        this.registerEventHandler(EventPacketIn.class, eventPacketIn -> {
            if (!this.processPredicate.test((Packet<?>)eventPacketIn.getPacket())) {
                return;
            }
            Object packet = eventPacketIn.getPacket();
            double xzMod = ((Number)this.horizontalMod.getValue()).doubleValue() * 0.01;
            double yMod = ((Number)this.verticalMod.getValue()).doubleValue() * 0.01;
            double velocityX = 0.0;
            double velocityY = 0.0;
            double velocityZ = 0.0;
            if (packet instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity packetEntityVelocity = (S12PacketEntityVelocity)eventPacketIn.getPacket();
                if (packetEntityVelocity.getEntityID() != Velocity.mc.thePlayer.getEntityId()) {
                    return;
                }
                velocityX = (double)packetEntityVelocity.getMotionX() / 8000.0 * xzMod;
                velocityY = (double)packetEntityVelocity.getMotionY() / 8000.0 * yMod;
                velocityZ = (double)packetEntityVelocity.getMotionZ() / 8000.0 * xzMod;
                eventPacketIn.setCancelled(true);
            } else if (packet instanceof S27PacketExplosion) {
                FireballBooster booster = (FireballBooster)Corrosion.INSTANCE.getModuleManager().getModule(FireballBooster.class);
                if (booster.isEnabled() && booster.isBoost()) {
                    return;
                }
                S27PacketExplosion packetExplosion = (S27PacketExplosion)eventPacketIn.getPacket();
                velocityX = (double)packetExplosion.getMotionX() * xzMod;
                velocityY = packetExplosion.getY() * yMod;
                velocityZ = packetExplosion.getZ() * xzMod;
                eventPacketIn.setCancelled(true);
                if (((Mode)this.velocityMode.getValue()).equals(Mode.STACK) && this.stackedAmount++ > ((Number)this.maxStack.getValue()).intValue()) {
                    eventPacketIn.setCancelled(false);
                    this.stackedAmount = 0;
                    return;
                }
            }
            Velocity.mc.thePlayer.addVelocity(velocityX, velocityY, velocityZ);
        });
        this.registerEventHandler(EventStrafe.class, eventStrafe -> {
            if (this.velocityMode.getValue() == Mode.REDUCE && Velocity.mc.thePlayer.hurtTime > 0 && Velocity.mc.thePlayer.hurtTime < 8 && !Velocity.mc.thePlayer.onGround) {
                double reduceBy = ((Number)this.multiplier.getValue()).doubleValue();
                Velocity.mc.thePlayer.motionX *= reduceBy;
                Velocity.mc.thePlayer.motionZ *= reduceBy;
            }
        });
    }

    @Override
    public String getMode() {
        return ((Number)this.horizontalMod.getValue()).intValue() + "% " + ((Number)this.verticalMod.getValue()).intValue() + "%";
    }

    public static enum Mode implements INameable
    {
        NORMAL("Normal"),
        STACK("Stack"),
        REDUCE("Reduce");

        private final String name;

        @Override
        public String getName() {
            return this.name;
        }

        private Mode(String name) {
            this.name = name;
        }
    }
}

