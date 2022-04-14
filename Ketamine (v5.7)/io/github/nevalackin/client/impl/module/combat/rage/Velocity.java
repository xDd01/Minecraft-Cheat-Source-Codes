package io.github.nevalackin.client.impl.module.combat.rage;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.event.packet.ReceivePacketEvent;
import io.github.nevalackin.client.impl.property.DoubleProperty;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.ChatComponentText;

public final class Velocity extends Module {

    private final DoubleProperty horizontalPercentageProperty = new DoubleProperty("Horizontal", 0.0, 0.0, 100.0, 1.0);
    private final DoubleProperty verticalPercentageProperty = new DoubleProperty("Vertical", 0.0, 0.0, 100.0, 1.0);
    private Aura aura;

    public Velocity() {
        super("Velocity", Category.COMBAT, Category.SubCategory.COMBAT_RAGE);

        this.setSuffix(() -> String.format("%s%% %s%%", this.horizontalPercentageProperty.getValue().intValue(), this.verticalPercentageProperty.getValue().intValue()));

        this.register(this.horizontalPercentageProperty, this.verticalPercentageProperty);
    }

    @EventLink
    private final Listener<ReceivePacketEvent> onReceivePacket = event -> {
        final Packet<?> packet = event.getPacket();

        if (packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity velocity = (S12PacketEntityVelocity) packet;
            final double x = velocity.getMotionX() / 8000.0D, y = velocity.getMotionY() / 8000.0D, z = velocity.getMotionZ() / 8000.0D;

            if (velocity.getEntityID() != this.mc.thePlayer.getEntityId()) return;

            if (x != 0 && y != 0 && z != 0 && velocity.getEntityID() == mc.thePlayer.getEntityId()) {
                if (Math.abs(x) < 0.16 && Math.abs(y) < 0.16 && Math.abs(z) < 0.16) {
                    //mc.thePlayer.addChatMessage(new ChatComponentText("Velocity Received on Tick " + "\247c" + mc.thePlayer.ticksExisted + ", [Staff] - Disabling Velocity"));
                    //setEnabled(false);
                }
            }

            mc.thePlayer.addChatMessage(new ChatComponentText("Velocity Received on Tick " + "\247c" + mc.thePlayer.ticksExisted + "."));

            final float verticalPercentage = verticalPercentageProperty.getValue().floatValue() / 100.0F;
            final float horizontalPercentage = horizontalPercentageProperty.getValue().floatValue() / 100.0F;

            if (verticalPercentage == 0 && horizontalPercentage == 0) {
                event.setCancelled();
                return;
            }

            velocity.setMotionX((int) Math.ceil(velocity.getMotionX() * horizontalPercentage));
            velocity.setMotionY((int) Math.ceil(velocity.getMotionY() * verticalPercentage));
            velocity.setMotionZ((int) Math.ceil(velocity.getMotionZ() * horizontalPercentage));
        }
        else if (packet instanceof S27PacketExplosion) {
            S27PacketExplosion explosion = (S27PacketExplosion) packet;

            final float verticalPercentage = verticalPercentageProperty.getValue().floatValue() / 100.0F;
            final float horizontalPercentage = horizontalPercentageProperty.getValue().floatValue() / 100.0F;

            if (Math.abs(explosion.getX()) < 0.16 && Math.abs(explosion.getY()) < 0.16 && Math.abs(explosion.getZ()) < 0.16 && explosion.getX() == explosion.getY() && explosion.getY() == explosion.getZ()) {
                //mc.thePlayer.addChatMessage(new ChatComponentText("Velocity Received on Tick " + "\247c" + mc.thePlayer.ticksExisted + ", [Staff] - Disabling Velocity"));
                //setEnabled(false);
            }

            if (verticalPercentage == 0 && horizontalPercentage == 0) {
                event.setCancelled();
                return;
            }

            explosion.setMotionY(explosion.getMotionY() * verticalPercentage);
            explosion.setMotionX(explosion.getMotionX() * horizontalPercentage);
            explosion.setMotionZ(explosion.getMotionZ() * horizontalPercentage);
        }
    };

    @Override
    public void onEnable() {
        if (aura == null) {
            aura = KetamineClient.getInstance().getModuleManager().getModule(Aura.class);
        }
    }

    @Override
    public void onDisable() {

    }
}
