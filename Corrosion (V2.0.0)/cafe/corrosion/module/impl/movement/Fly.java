/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.movement;

import cafe.corrosion.event.impl.EventCollision;
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
import com.google.common.util.concurrent.AtomicDouble;
import java.util.Objects;
import javax.vecmath.Vector3d;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovementInput;

@ModuleAttributes(name="Fly", description="Lets you fly", category=Module.Category.MOVEMENT)
public class Fly
extends Module {
    private final EnumProperty<Mode> mode = new EnumProperty((Module)this, "Mode", (INameable[])Mode.values());
    private final EnumProperty<FrictionMode> frictionMode = new EnumProperty((Module)this, "Friction Mode", (INameable[])FrictionMode.values());
    private final NumberProperty speed = new NumberProperty(this, "Speed", 1, 0.1, 8.5, 0.1);
    private final BooleanProperty latestVerus = new BooleanProperty((Module)this, "New Verus Compat", true);
    private final BooleanProperty forceFriction = new BooleanProperty((Module)this, "Force Friction", true);
    private final BooleanProperty allowSpeed = new BooleanProperty((Module)this, "Allow Speed", true);
    private final BooleanProperty damage = new BooleanProperty((Module)this, "Damage", true);
    private final BooleanProperty stop = new BooleanProperty((Module)this, "Stop", true);
    private final BooleanProperty antiKick = new BooleanProperty((Module)this, "Anti Kick", false);
    private final Vector3d startPosition = new Vector3d(0.0, 0.0, 0.0);
    private final AtomicDouble hDist = new AtomicDouble();
    private int ticks;

    public Fly() {
        this.allowSpeed.setHidden(() -> !Objects.equals(this.mode.getValue(), Mode.COLLIDE));
        this.latestVerus.setHidden(() -> !Objects.equals(this.mode.getValue(), Mode.COLLIDE));
        this.damage.setHidden(() -> !Objects.equals(this.mode.getValue(), Mode.COLLIDE));
        this.frictionMode.setHidden(() -> (Boolean)this.forceFriction.getValue() == false);
        this.registerEventHandler(EventStrafe.class, eventStrafe -> {
            MovementInput movementInput = Fly.mc.thePlayer.movementInput;
            double speed = ((Number)this.speed.getValue()).doubleValue();
            switch ((Mode)this.mode.getValue()) {
                case VANILLA: {
                    double x2 = Fly.mc.thePlayer.posX;
                    double y2 = Fly.mc.thePlayer.posY;
                    double z2 = Fly.mc.thePlayer.posZ;
                    double d2 = movementInput.jump ? speed : (Fly.mc.thePlayer.motionY = movementInput.sneak ? -speed : 0.0);
                    if (((Boolean)this.antiKick.getValue()).booleanValue()) {
                        Fly.mc.thePlayer.motionY -= 0.0625;
                    }
                    eventStrafe.setMotion(speed);
                    break;
                }
                case COLLIDE: {
                    if (((Boolean)this.latestVerus.getValue()).booleanValue() && PlayerUtil.isMovingOnGround()) {
                        Fly.mc.thePlayer.jump();
                        if (!((Boolean)this.allowSpeed.getValue()).booleanValue()) {
                            this.hDist.set(0.62);
                            eventStrafe.setMotion(this.handleFriction(this.hDist));
                        } else if (this.hDist.get() <= PlayerUtil.getVerusBaseSpeed()) {
                            this.hDist.set(0.62);
                        }
                        return;
                    }
                    if (Fly.mc.thePlayer.hurtTime >= 0) {
                        this.hDist.set(speed);
                    }
                    eventStrafe.setMotion(this.handleFriction(this.hDist));
                    break;
                }
                case VIPER: {
                    eventStrafe.setFriction(0.0f);
                    MovementUtil.setMotion(0.0);
                    break;
                }
                case MATRIX: {
                    double md2 = 0.42;
                    if (Fly.mc.thePlayer.movementInput.jump || Fly.mc.thePlayer.movementInput.sneak) {
                        this.startPosition.y = Fly.mc.thePlayer.posY;
                        if (Fly.mc.thePlayer.ticksExisted % 2 == 0) {
                            Fly.mc.thePlayer.motionY = Fly.mc.thePlayer.movementInput.jump ? (double)0.42f : (double)-0.42f;
                        }
                    } else if (Fly.mc.thePlayer.motionY > md2) {
                        this.startPosition.y = Fly.mc.thePlayer.posY;
                        Fly.mc.thePlayer.motionY = 0.0;
                    } else if (Fly.mc.thePlayer.posY <= this.startPosition.y) {
                        Fly.mc.thePlayer.motionY = md2;
                    }
                    PacketUtil.sendTimes(2, new C08PacketPlayerBlockPlacement(Fly.mc.thePlayer.getPosition().down(256), 256, new ItemStack(Blocks.stonebrick, 64), 2.0f, 10.0f, 36.0f));
                    PacketUtil.send(new C03PacketPlayer(true));
                    PacketUtil.send(new C03PacketPlayer(Fly.mc.thePlayer.onGround));
                }
            }
        });
        this.registerEventHandler(EventMove.class, eventMove -> {});
        this.registerEventHandler(EventUpdate.class, eventUpdate -> {
            Fly.mc.thePlayer.cameraYaw = 0.05f;
            MovementInput movementInput = Fly.mc.thePlayer.movementInput;
            double speed = ((Number)this.speed.getValue()).doubleValue();
            switch ((Mode)this.mode.getValue()) {
                case COLLIDE: {
                    if (!Fly.mc.thePlayer.movementInput.jump || Fly.mc.thePlayer.movementInput.sneak || Fly.mc.thePlayer.ticksExisted % 3 != 0) break;
                    Fly.mc.thePlayer.motionY = 0.42f;
                    eventUpdate.setOnGround(true);
                    this.startPosition.y = Fly.mc.thePlayer.posY;
                    break;
                }
                case VIPER: {
                    if (eventUpdate.isPre()) {
                        Fly.mc.thePlayer.motionY = movementInput.jump ? speed * 0.5 : (movementInput.sneak ? -speed * 0.5 : 0.0);
                        double newY = MovementUtil.roundPos(Fly.mc.thePlayer.posY);
                        Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX, newY, Fly.mc.thePlayer.posZ);
                        eventUpdate.setOnGround(true);
                        eventUpdate.setY(newY);
                        Fly.mc.thePlayer.setSprinting(true);
                        break;
                    }
                    MovementUtil.sendMotion(Math.max(0.7, this.handleFriction(this.hDist)), 0.216);
                    break;
                }
            }
            if (!eventUpdate.isPre()) {
                double x2 = Fly.mc.thePlayer.posX;
                double y2 = Fly.mc.thePlayer.posY;
                double z2 = Fly.mc.thePlayer.posZ;
                float yaw = Fly.mc.thePlayer.rotationYaw;
                float f2 = Fly.mc.thePlayer.rotationPitch;
            }
        });
        this.registerEventHandler(EventCollision.class, eventCollision -> {
            if (this.mode.getValue() == Mode.COLLIDE && !Fly.mc.thePlayer.isSneaking() && eventCollision.getBlock() instanceof BlockAir) {
                eventCollision.setBoundingBox(new AxisAlignedBB(-12.0, -1.0, -12.0, 12.0, 0.0, 12.0).offset(eventCollision.getBlockPos().getX(), (Boolean)this.latestVerus.getValue() != false ? this.startPosition.getY() : (double)eventCollision.getBlockPos().getY(), eventCollision.getBlockPos().getZ()));
            }
        });
    }

    private double handleFriction(AtomicDouble atomicDouble) {
        if (((Boolean)this.forceFriction.getValue()).booleanValue()) {
            double value = atomicDouble.get();
            switch ((FrictionMode)this.frictionMode.getValue()) {
                case NCP: {
                    atomicDouble.set(value - value / 159.0);
                    break;
                }
                case NEW: {
                    atomicDouble.set(value * 0.98);
                    break;
                }
                case LEGIT: {
                    atomicDouble.set(value * 0.91);
                    break;
                }
                case SILENT: {
                    atomicDouble.set(value - 1.0E-9);
                }
            }
            return Math.max(atomicDouble.get(), PlayerUtil.getVerusBaseSpeed());
        }
        return ((Number)this.speed.getValue()).doubleValue();
    }

    @Override
    public void onEnable() {
        this.ticks = 0;
        this.hDist.set(((Number)this.speed.getValue()).floatValue());
        switch ((Mode)this.mode.getValue()) {
            case COLLIDE: {
                if (Fly.mc.thePlayer == null) break;
                double x2 = Fly.mc.thePlayer.posX;
                double y2 = Fly.mc.thePlayer.posY;
                double z2 = Fly.mc.thePlayer.posZ;
                if (((Boolean)this.damage.getValue()).booleanValue() && Fly.mc.thePlayer.onGround) {
                    PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2 + 3.5, z2, false));
                    PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2, z2, false));
                    PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(x2, y2, z2, true));
                }
                if (!((Boolean)this.allowSpeed.getValue()).booleanValue()) {
                    this.hDist.set(0.62);
                }
                if (!((Boolean)this.latestVerus.getValue()).booleanValue()) break;
                Fly.mc.thePlayer.jump();
                break;
            }
        }
        if (Fly.mc.thePlayer != null) {
            this.startPosition.set(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY, Fly.mc.thePlayer.posZ);
        }
    }

    @Override
    public void onDisable() {
        Fly.mc.gameSettings.keyBindSneak.setPressed(false);
        if (((Boolean)this.stop.getValue()).booleanValue()) {
            Fly.mc.thePlayer.motionX = 0.0;
            Fly.mc.thePlayer.motionZ = 0.0;
        }
        Fly.mc.timer.timerSpeed = 1.0f;
        this.ticks = 0;
        super.onDisable();
    }

    @Override
    public String getMode() {
        return ((Mode)this.mode.getValue()).getName();
    }

    static enum FrictionMode implements INameable
    {
        NCP("NCP"),
        NEW("New Verus"),
        LEGIT("Legit"),
        SILENT("Silent");

        private final String name;

        private FrictionMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    static enum Mode implements INameable
    {
        VANILLA("Vanilla"),
        COLLIDE("Collide"),
        VIPER("Viper"),
        MATRIX("Matrix");

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

