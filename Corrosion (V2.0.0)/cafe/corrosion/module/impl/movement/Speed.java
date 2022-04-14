/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.movement;

import cafe.corrosion.event.impl.EventMove;
import cafe.corrosion.event.impl.EventStrafe;
import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.nameable.INameable;
import cafe.corrosion.util.packet.PacketUtil;
import cafe.corrosion.util.player.MovementUtil;
import cafe.corrosion.util.player.PlayerUtil;
import java.util.Objects;
import javax.vecmath.Vector2d;
import net.minecraft.network.play.client.C03PacketPlayer;

@ModuleAttributes(name="Speed", tMobileName="Remo Runner", description="Increases the rate at which you move", category=Module.Category.MOVEMENT)
public class Speed
extends Module {
    private final EnumProperty<Mode> mode = new EnumProperty((Module)this, "Mode", (INameable[])Mode.values());
    private final EnumProperty<VerusMode> verusMode = new EnumProperty((Module)this, "Verus Mode", (INameable[])VerusMode.values());
    private final EnumProperty<NCPMode> ncpMode = new EnumProperty((Module)this, "NCP Mode", (INameable[])NCPMode.values());
    private final NumberProperty speed = new NumberProperty(this, "Speed", 1, 0.1, 8.5, 0.1);
    private final BooleanProperty stop = new BooleanProperty((Module)this, "Stop", true);
    private final NumberProperty hurtTimeMultiplier = new NumberProperty(this, "Multiplier", 1, 0.2, 5, 0.2);
    private double hDist;
    private boolean hDistSlowdown;
    private int stage;

    public Speed() {
        this.hurtTimeMultiplier.setHidden(() -> !Objects.equals(this.mode.getValue(), Mode.HURT_TIME));
        this.verusMode.setHidden(() -> !Objects.equals(this.mode.getValue(), Mode.VERUS));
        this.ncpMode.setHidden(() -> !Objects.equals(this.mode.getValue(), Mode.NCP));
        this.registerEventHandler(EventUpdate.class, eventUpdate -> {
            switch ((Mode)this.mode.getValue()) {
                case HURT_TIME: {
                    if (!eventUpdate.isPre() || Speed.mc.thePlayer.hurtTime == 0) {
                        return;
                    }
                    double multiplier = ((Number)this.hurtTimeMultiplier.getValue()).doubleValue();
                    Speed.mc.thePlayer.motionX *= multiplier;
                    Speed.mc.thePlayer.motionZ *= multiplier;
                }
                case VERUS: {
                    switch ((VerusMode)this.verusMode.getValue()) {
                        case GROUND: {
                            if (this.stage > 8 || !PlayerUtil.isMoving()) return;
                            eventUpdate.setOnGround(true);
                            break;
                        }
                        case FAST_HOP: {
                            break;
                        }
                        case WEIRD: {
                            if (this.stage > 13 || !PlayerUtil.isMoving()) return;
                            eventUpdate.setOnGround(true);
                            break;
                        }
                        case YPORT: {
                            if (!Speed.mc.thePlayer.prevOnGround && !this.hDistSlowdown) return;
                            eventUpdate.setOnGround(true);
                        }
                    }
                    return;
                }
            }
        });
        this.registerEventHandler(EventStrafe.class, eventStrafe -> {
            double speed = ((Number)this.speed.getValue()).doubleValue();
            switch ((Mode)this.mode.getValue()) {
                case VERUS: {
                    switch ((VerusMode)this.verusMode.getValue()) {
                        case GROUND: {
                            if (PlayerUtil.isMoving()) {
                                if (Speed.mc.thePlayer.onGround) {
                                    Speed.mc.thePlayer.setPosition(Speed.mc.thePlayer.posX, Speed.mc.thePlayer.posY + (double)0.42f, Speed.mc.thePlayer.posZ);
                                    Speed.mc.thePlayer.motionY = 0.0;
                                    this.hDist = PlayerUtil.getVerusBaseSpeed() * 2.149;
                                    this.stage = 0;
                                } else {
                                    if (this.hDistSlowdown && this.stage <= 8) {
                                        this.hDist = PlayerUtil.getVerusBaseSpeed();
                                        Speed.mc.thePlayer.motionY = 0.0;
                                    } else if (this.stage <= 8) {
                                        this.hDist *= 1.089;
                                        Speed.mc.thePlayer.motionY = 0.0;
                                    } else {
                                        this.hDistSlowdown = false;
                                        this.hDist = 0.26;
                                    }
                                    ++this.stage;
                                }
                            } else {
                                this.hDistSlowdown = true;
                            }
                            eventStrafe.setMotion(Math.max(this.hDist, 0.32));
                            break;
                        }
                        case FAST_HOP: {
                            if (PlayerUtil.isMovingOnGround()) {
                                Speed.mc.thePlayer.motionY = 0.42f;
                                this.hDist *= (double)1.3f;
                                this.hDist = Math.min(1.0, this.hDist);
                                this.hDistSlowdown = true;
                            } else {
                                Speed.mc.thePlayer.motionY -= 0.016;
                                this.hDist *= this.hDist > 0.36 ? 0.993 : 0.99;
                            }
                            this.hDist = Math.max(this.hDist, PlayerUtil.getVerusBaseSpeed());
                            eventStrafe.setMotion(this.hDist);
                            break;
                        }
                        case YPORT: {
                            if (PlayerUtil.isMovingOnGround()) {
                                Speed.mc.thePlayer.setPosition(Speed.mc.thePlayer.posX, Speed.mc.thePlayer.posY + (double)0.42f, Speed.mc.thePlayer.posZ);
                                Speed.mc.thePlayer.motionY = 0.0;
                                this.hDist = PlayerUtil.getVerusBaseSpeed() * 2.149;
                                this.hDistSlowdown = true;
                            } else {
                                if (this.hDistSlowdown) {
                                    eventStrafe.setMotion(this.hDist * 1.95);
                                    Speed.mc.thePlayer.motionY = 0.0;
                                    this.hDistSlowdown = false;
                                    this.hDist = 0.37;
                                    return;
                                }
                                this.hDist -= this.hDist / 159.0;
                            }
                            eventStrafe.setMotion(this.hDist);
                            break;
                        }
                        case WEIRD: {
                            if (PlayerUtil.isMoving()) {
                                if (Speed.mc.thePlayer.onGround) {
                                    Speed.mc.thePlayer.setPosition(Speed.mc.thePlayer.posX, Speed.mc.thePlayer.posY + (double)0.42f, Speed.mc.thePlayer.posZ);
                                    Speed.mc.thePlayer.motionY = 0.0;
                                    this.hDist = PlayerUtil.getVerusBaseSpeed() * 2.149;
                                    this.stage = 0;
                                } else {
                                    if (this.hDistSlowdown) {
                                        if (this.stage <= 3) {
                                            this.hDist = PlayerUtil.getVerusBaseSpeed();
                                            Speed.mc.thePlayer.motionY = 0.0;
                                        } else {
                                            this.stage = 14;
                                            this.hDistSlowdown = false;
                                        }
                                    } else if (this.stage <= 13) {
                                        if (this.stage <= 9) {
                                            this.hDist *= 1.058;
                                        }
                                        Speed.mc.thePlayer.motionY = 0.0;
                                    } else {
                                        this.hDist = PlayerUtil.getVerusBaseSpeed() * 2.149;
                                        this.stage = 0;
                                    }
                                    ++this.stage;
                                }
                            } else {
                                this.hDistSlowdown = true;
                            }
                            eventStrafe.setMotion(Math.max(this.hDist, PlayerUtil.getVerusBaseSpeed()));
                        }
                    }
                    break;
                }
                case VANILLA: {
                    if (PlayerUtil.isMovingOnGround()) {
                        Speed.mc.thePlayer.motionY = 0.42f;
                    }
                    eventStrafe.setMotion(speed);
                    break;
                }
                case NCP: {
                    switch ((NCPMode)this.ncpMode.getValue()) {
                        case FAST_HOP: {
                            if (PlayerUtil.isMovingOnGround()) {
                                Speed.mc.thePlayer.motionY = 0.42f;
                                this.hDist *= (double)2.149f;
                                this.hDistSlowdown = true;
                            } else if (this.hDistSlowdown) {
                                this.hDist -= 0.66 * (this.hDist - PlayerUtil.getNCPBaseSpeed());
                                this.hDistSlowdown = false;
                            } else {
                                this.hDist -= this.hDist / 159.0;
                            }
                            Speed.mc.thePlayer.motionY -= 0.01;
                            this.hDist = Math.max(PlayerUtil.getNCPBaseSpeed(), this.hDist);
                            eventStrafe.setMotion(this.hDist);
                            break;
                        }
                        case HOP: {
                            if (PlayerUtil.isMovingOnGround()) {
                                Speed.mc.thePlayer.motionY = 0.42f;
                                this.hDist = PlayerUtil.getNCPBaseSpeed() * (double)2.149f;
                                this.hDistSlowdown = true;
                            } else if (this.hDistSlowdown) {
                                this.hDist -= 0.72 * (this.hDist - PlayerUtil.getNCPBaseSpeed());
                                this.hDistSlowdown = false;
                            } else {
                                this.hDist -= this.hDist / 159.0;
                            }
                            this.hDist = Math.max(PlayerUtil.getNCPBaseSpeed(), this.hDist);
                            break;
                        }
                        case HYPIXEL: {
                            if (PlayerUtil.isMovingOnGround()) {
                                Speed.mc.thePlayer.motionY = 0.42f;
                                this.hDist = PlayerUtil.getNCPBaseSpeed() * 2.149;
                                this.hDistSlowdown = true;
                                Vector2d vector2d = MovementUtil.getMotion(-0.063);
                                for (int i2 = 0; i2 < 3; ++i2) {
                                    PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(Speed.mc.thePlayer.posX + vector2d.x, Speed.mc.thePlayer.posY, Speed.mc.thePlayer.posZ + vector2d.y, true));
                                    PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(Speed.mc.thePlayer.posX, Speed.mc.thePlayer.posY, Speed.mc.thePlayer.posZ, true));
                                }
                            } else if (this.hDistSlowdown) {
                                this.hDist = PlayerUtil.getNCPBaseSpeed() * 1.15;
                                this.hDistSlowdown = false;
                            } else if ((double)Speed.mc.thePlayer.fallDistance < 1.5) {
                                switch (this.stage) {
                                    case 1: {
                                        this.hDist = PlayerUtil.getNCPBaseSpeed() * 1.7;
                                        break;
                                    }
                                    case 2: {
                                        this.hDist = PlayerUtil.getNCPBaseSpeed() * 1.45;
                                        break;
                                    }
                                    default: {
                                        this.hDist = PlayerUtil.getNCPBaseSpeed() * 1.05;
                                        this.stage = 0;
                                    }
                                }
                                ++this.stage;
                            } else {
                                this.hDist *= 0.905;
                            }
                            eventStrafe.setMotionPartialStrafe((float)this.hDist, 0.235f);
                        }
                    }
                    break;
                }
                case VIPER: {
                    float friction = eventStrafe.getFriction();
                    if (PlayerUtil.isMovingOnGround()) {
                        Speed.mc.thePlayer.motionY = 0.42f;
                        friction *= 2.0f;
                        this.hDistSlowdown = false;
                    } else if (Speed.mc.thePlayer.fallDistance <= 2.0f) {
                        if (this.hDistSlowdown) {
                            friction *= 2.6f;
                        } else {
                            this.hDistSlowdown = true;
                            friction *= 3.2f;
                        }
                    } else {
                        friction *= 0.7f;
                    }
                    eventStrafe.setFriction(friction);
                    MovementUtil.setMotion(Math.hypot(Speed.mc.thePlayer.motionX, Speed.mc.thePlayer.motionZ));
                    break;
                }
                case MATRIX: {
                    if (PlayerUtil.isMovingOnGround()) {
                        eventStrafe.setMotion(speed);
                        Speed.mc.thePlayer.motionY = Speed.mc.thePlayer.movementInput.jump ? (double)0.42f : 1.0E-4;
                    }
                    ++this.stage;
                }
            }
        });
        this.registerEventHandler(EventMove.class, eventMove -> {});
    }

    @Override
    public void onEnable() {
        this.hDist = PlayerUtil.getNCPBaseSpeed();
        this.hDistSlowdown = ((Mode)this.mode.getValue()).equals(Mode.VERUS) && ((VerusMode)this.verusMode.getValue()).equals(VerusMode.GROUND);
    }

    @Override
    public void onDisable() {
        if (((Boolean)this.stop.getValue()).booleanValue()) {
            Speed.mc.thePlayer.stopMovement();
        }
    }

    @Override
    public String getMode() {
        String mode = ((Mode)this.mode.getValue()).getName();
        if (((Mode)this.mode.getValue()).equals(Mode.VERUS)) {
            mode = mode + " | " + ((VerusMode)this.verusMode.getValue()).getName();
        }
        return mode;
    }

    private static enum NCPMode implements INameable
    {
        GROUND("Hypixel Ground"),
        FAST_HOP("Fast Hop"),
        HYPIXEL("Hypixel Hop"),
        HOP("Hop");

        private final String name;

        private NCPMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    private static enum VerusMode implements INameable
    {
        GROUND("Ground"),
        FAST_HOP("Fast Hop"),
        YPORT("Y-Port"),
        WEIRD("Weird");

        private final String name;

        private VerusMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    private static enum Mode implements INameable
    {
        VANILLA("Vanilla"),
        NCP("NCP"),
        VIPER("Viper"),
        MATRIX("Matrix"),
        HURT_TIME("Hurt Time"),
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

