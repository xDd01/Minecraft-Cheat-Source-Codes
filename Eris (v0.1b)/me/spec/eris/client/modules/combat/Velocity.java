package me.spec.eris.client.modules.combat;

import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.client.EventPacket;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.api.value.types.NumberValue;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;

public class Velocity extends Module {
    public int counter;
    public double mX, mY, mZ;
    public ModeValue<Mode> modeValue = new ModeValue<>("Mode", Mode.PACKET, this);
    public NumberValue<Double> horiz = new NumberValue<Double>("Horizontal", 0.0, 0.0, 1.00, this, "Horizontal Velocity");
    public NumberValue<Double> vert = new NumberValue<Double>("Vertical", 0.0, 0.0, 1.00, this, "Vertical Velocity");

    public enum Mode {
        VELT, PACKET, AGCTEST, DISLOCATE, SPOOF
    }

    public Velocity(String racism) {
        super("Velocity", ModuleCategory.COMBAT, racism);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }


    @Override
    public void onEvent(Event e) {
        if (e instanceof EventPacket) {
            setMode(modeValue.getValue().toString());
            EventPacket event = (EventPacket) e;
            if (event.isReceiving()) {
                if (event.getPacket() instanceof S12PacketEntityVelocity) {
                    S12PacketEntityVelocity packet = (S12PacketEntityVelocity) event.getPacket();

                    if (packet.getEntityID() == mc.thePlayer.getEntityId()) {
                        double velX = mX = ((packet.getMotionX() / 8000) * horiz.getValue());
                        double velY = mY = ((packet.getMotionY() / 8000) * vert.getValue());
                        double velZ = mZ = ((packet.getMotionZ() / 8000) * horiz.getValue());
                        switch (modeValue.getValue()) {
                            case AGCTEST:
                                if (mc.thePlayer.hurtTime == 8) {
                                    mc.thePlayer.setVelocity((packet.getMotionX() / 8000), -((packet.getMotionY() / 8000)), (packet.getMotionZ() / 8000));
                                    event.setCancelled();
                                }
                                break;
                            case DISLOCATE:
                                if (counter >= 3) {
                                    counter = 0;
                                }
                                break;
                            case PACKET:
                                event.setCancelled();
                                packet.motionX = 0;
                                packet.motionY = 0;
                                packet.motionZ = 0;
                                break;
                            case VELT:
                                velY = getVelocity(packet.getMotionY(), 94.15);
                                mc.thePlayer.setVelocity(velX, velY, velZ);
                                mc.thePlayer.addVelocity(velX, 0.12F, velZ);
                                event.setCancelled();
                                break;
                            case SPOOF:
                                sendPosition(packet.getMotionX() / 8000, packet.getMotionY() / 8000, packet.getMotionZ() / 8000, mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0.0, packet.getMotionY() / 8000, 0.0)).size() > 0, mc.thePlayer.isMoving());

                                event.setCancelled();
                                break;
                        }
                    }
                }

                if (event.getPacket() instanceof S27PacketExplosion) {
                    event.setCancelled();
                }
            }
        }
    }

    public double getVelocity(double motionX, double reduction) {
        return motionX * reduction;
    }

}
