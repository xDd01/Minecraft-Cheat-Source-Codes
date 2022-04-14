/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.combat;

import cafe.corrosion.event.impl.EventStrafe;
import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.module.impl.combat.auto.AutoPotion;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.combat.AngleHelper;
import cafe.corrosion.util.combat.ClickRandomizer;
import cafe.corrosion.util.math.RandomUtil;
import cafe.corrosion.util.nameable.INameable;
import cafe.corrosion.util.packet.PacketUtil;
import cafe.corrosion.util.player.PlayerUtil;
import cafe.corrosion.util.player.RotationUtil;
import cafe.corrosion.util.player.TargetFilter;
import cafe.corrosion.util.player.TargetOptions;
import cafe.corrosion.util.player.extra.Rotation;
import cafe.corrosion.util.vector.impl.Vector3F;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

@ModuleAttributes(name="KillAura", description="Automatically attacks nearby entities", category=Module.Category.COMBAT)
public class KillAura
extends Module {
    private final EnumProperty<AttackMode> attackMode = new EnumProperty((Module)this, "Attack Mode", (INameable[])AttackMode.values());
    private final EnumProperty<TargetMode> targetMode = new EnumProperty((Module)this, "Aim Mode", (INameable[])TargetMode.values());
    private final EnumProperty<BlockMode> blockMode = new EnumProperty((Module)this, "Block Mode", (INameable[])BlockMode.values());
    private final BooleanProperty invisible = new BooleanProperty(this, "Invisibles");
    private final BooleanProperty players = new BooleanProperty(this, "Players");
    private final BooleanProperty animals = new BooleanProperty(this, "Animals");
    private final BooleanProperty hostile = new BooleanProperty(this, "Mobs");
    private final NumberProperty min = new NumberProperty(this, "Min CPS", 10, 1, 20, 1);
    private final NumberProperty max = new NumberProperty(this, "Max CPS", 10, 1, 20, 1);
    private final NumberProperty randomizationLevel = new NumberProperty(this, "Randomization Level", 2, 1, 5, 0.1);
    private final NumberProperty range = new NumberProperty(this, "Range", 4.25, 1.25, 6, 0.05);
    private final NumberProperty smoothing = new NumberProperty(this, "Smoothing", 20, 20, 100, 20);
    private final BooleanProperty randomizeAps = new BooleanProperty(this, "Randomized CPS");
    private final BooleanProperty movementCorrection = new BooleanProperty(this, "Motion Fix");
    private final BooleanProperty silent = new BooleanProperty((Module)this, "Silent Rotation", true);
    private final BooleanProperty keepSprint = new BooleanProperty((Module)this, "Keep Sprint", true);
    private final BooleanProperty sprintSpam = new BooleanProperty((Module)this, "Sprint Spam", true);
    private final BooleanProperty hitbox = new BooleanProperty((Module)this, "Hitbox Check", true);
    private final AngleHelper angleHelper = new AngleHelper(70.0f, 250.0f, 70.0f, 200.0f);
    private final ClickRandomizer clickRandomizer = new ClickRandomizer(this.min, this.max, this.randomizationLevel, this.randomizeAps);
    private int targetIndex;
    private int currentRandomCPS;
    private Rotation currentRotations;
    private EntityLivingBase currentEntity;
    private float currentYaw;
    private float currentPitch;
    private float pitchIncrease;
    private float blockPosValue;
    private boolean changingArea;
    private int maxYaw;
    private int maxPitch;
    private int rotationSwap;
    private int index;

    public KillAura() {
        this.registerEventHandler(EventUpdate.class, eventUpdate -> {
            Object targetOptions = ((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)TargetOptions.builder().hostile((Boolean)this.hostile.getValue())).invisible((Boolean)this.invisible.getValue())).players((Boolean)this.players.getValue())).animals((Boolean)this.animals.getValue())).range(((Number)this.range.getValue()).doubleValue())).hitbox((Boolean)this.hitbox.getValue())).build();
            List<EntityLivingBase> entityList = KillAura.mc.theWorld.getLoadedEntityList().stream().filter(entity -> entity instanceof EntityLivingBase).map(entity -> (EntityLivingBase)entity).filter(TargetFilter.targetFilter(targetOptions)).collect(Collectors.toList());
            if (entityList.size() > 0) {
                this.calculateRotations(entityList);
            } else {
                this.currentEntity = null;
            }
            if (eventUpdate.isPre()) {
                KillAura.mc.thePlayer.setFakeBlocking(false);
            }
            if (entityList.isEmpty() || eventUpdate.isPre() != ((AttackMode)this.attackMode.getValue()).isPre() || AutoPotion.isPotting()) {
                return;
            }
            if (((AttackMode)this.attackMode.getValue()).equals(AttackMode.PRE) && this.blockMode.getValue() != BlockMode.NONE) {
                this.handleAutoBlock(eventUpdate.isPre());
            }
            if (this.currentRotations != null) {
                eventUpdate.setRotationYaw(this.currentRotations.getRotationYaw());
                eventUpdate.setRotationPitch(this.currentRotations.getRotationPitch());
            }
            if (this.targetMode.getValue() == TargetMode.SWITCH) {
                ++this.targetIndex;
            }
            if (this.currentEntity != null && this.clickRandomizer.hasElapsed()) {
                if (KillAura.mc.thePlayer.isSprinting() && ((Boolean)this.sprintSpam.getValue()).booleanValue()) {
                    PacketUtil.sendNoEvent(new C0BPacketEntityAction(KillAura.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
                }
                PlayerUtil.attackSwing(this.currentEntity, (Boolean)this.keepSprint.getValue());
                if (KillAura.mc.thePlayer.isSprinting() && ((Boolean)this.sprintSpam.getValue()).booleanValue()) {
                    PacketUtil.sendNoEvent(new C0BPacketEntityAction(KillAura.mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING));
                }
                if (this.blockMode.getValue() == BlockMode.SEMI_LEGIT) {
                    PacketUtil.send(new C02PacketUseEntity((Entity)this.currentEntity, C02PacketUseEntity.Action.INTERACT));
                    PacketUtil.send(new C08PacketPlayerBlockPlacement(KillAura.mc.thePlayer.getHeldItem()));
                }
            }
        });
        this.registerEventHandler(EventStrafe.class, eventStrafe -> {
            if (((Boolean)this.movementCorrection.getValue()).booleanValue() && this.currentEntity != null && this.currentRotations != null) {
                eventStrafe.setYaw(this.currentRotations.getRotationYaw());
            }
        });
    }

    private void calculateRotations(List<EntityLivingBase> entityList) {
        EntityLivingBase first;
        if (KillAura.mc.theWorld == null || KillAura.mc.thePlayer == null) {
            return;
        }
        this.currentEntity = first = entityList.get(0);
        switch ((TargetMode)this.targetMode.getValue()) {
            case SINGLE: {
                this.currentRotations = RotationUtil.getRotationsRandom(this.currentEntity, true);
                break;
            }
            case SWITCH: {
                if (this.targetIndex >= entityList.size()) {
                    this.targetIndex = 0;
                }
                this.currentEntity = entityList.get(this.targetIndex);
                this.currentRotations = RotationUtil.getRotationsRandom(this.currentEntity, true);
                break;
            }
            case ADVANCED: {
                boolean ticks;
                AxisAlignedBB[] boxes = new AxisAlignedBB[]{this.currentEntity.getEntityBoundingBox(), KillAura.mc.thePlayer.getEntityBoundingBox()};
                Vector3F[] vectors = new Vector3F[2];
                for (int i2 = 0; i2 < vectors.length; ++i2) {
                    AxisAlignedBB bb2 = boxes[i2];
                    double minX = bb2.minX;
                    double minY = bb2.minY;
                    double minZ = bb2.minZ;
                    double maxX = bb2.maxX;
                    double maxZ = bb2.maxZ;
                    double posX = minX + (maxX - minX) / 2.0;
                    double posY = minY - (double)this.pitchIncrease;
                    double posZ = minZ + (maxZ - minZ) / 2.0;
                    vectors[i2] = new Vector3F(posX, posY, posZ);
                }
                AngleHelper.Angle srcAngle = new AngleHelper.Angle(Float.valueOf(KillAura.mc.thePlayer.rotationYaw), Float.valueOf(KillAura.mc.thePlayer.rotationPitch));
                AngleHelper.Angle dstAngle = this.angleHelper.calculateAngle(vectors[0], vectors[1], this.currentEntity, this.rotationSwap);
                AngleHelper.Angle newSmoothing = this.angleHelper.smoothAngle(dstAngle, srcAngle, 300.0f, 1200.0f);
                double x2 = this.currentEntity.posX - KillAura.mc.thePlayer.posX + (this.currentEntity.lastTickPosX - this.currentEntity.posX) / 2.0;
                double z2 = this.currentEntity.posZ - KillAura.mc.thePlayer.posZ + (this.currentEntity.lastTickPosZ - this.currentEntity.posZ) / 2.0;
                float destinationYaw = 0.0f;
                if (this.currentEntity != first) {
                    this.index = 3;
                }
                double smooth = 1.0 + ((Number)this.smoothing.getValue()).doubleValue() * 0.035;
                destinationYaw = RotationUtil.clampAngle(this.currentYaw - (float)(-(Math.atan2(x2, z2) * 58.0)));
                destinationYaw = (float)((double)this.currentYaw - (double)destinationYaw / (smooth += (double)KillAura.mc.thePlayer.getDistanceToEntity(this.currentEntity) * 0.1 + (KillAura.mc.thePlayer.ticksExisted % 4 == 0 ? 0.04 : 0.0)));
                boolean bl2 = ticks = KillAura.mc.thePlayer.ticksExisted % 20 == 0;
                if (KillAura.mc.thePlayer.ticksExisted % 15 == 0) {
                    if (this.rotationSwap++ >= 3) {
                        this.rotationSwap = 0;
                    }
                    this.pitchIncrease = (float)((double)this.pitchIncrease + (this.changingArea ? RandomUtil.random(-0.055, -0.075) : RandomUtil.random(0.055, 0.075)));
                }
                if ((double)this.pitchIncrease >= 0.9) {
                    this.changingArea = true;
                }
                if ((double)this.pitchIncrease <= -0.15) {
                    this.changingArea = false;
                }
                float theYaw = (float)RotationUtil.preciseRound(destinationYaw, 1.0) + (ticks ? 0.243437f : 0.14357f);
                float thePitch = (float)RotationUtil.preciseRound(newSmoothing.getPitch().floatValue(), 1.0) + (ticks ? 0.1335f : 0.13351f);
                this.currentRotations.setRotationPitch(thePitch);
                this.currentRotations.setRotationYaw(theYaw);
                break;
            }
            case SMART: {
                Rotation rotations = RotationUtil.getSmartRotations(this.currentEntity);
                float sensitivity = KillAura.mc.gameSettings.mouseSensitivity * 0.6f * 0.2f;
                float gcd = (float)Math.pow(sensitivity, 3.0) * 1.2f;
                float deltaX = rotations.getRotationYaw() - (KillAura.mc.thePlayer.rotationYaw - KillAura.mc.thePlayer.rotationYaw % gcd);
                float deltaY = rotations.getRotationPitch() - MathHelper.clamp_float(KillAura.mc.thePlayer.rotationPitch - KillAura.mc.thePlayer.rotationPitch % gcd, -90.0f, 90.0f);
                deltaX -= deltaX % gcd;
                deltaY -= deltaY % gcd;
                float rotationYaw = KillAura.mc.thePlayer.rotationYaw + deltaX * gcd;
                float rotationPitch = KillAura.mc.thePlayer.rotationPitch + deltaY * gcd;
                this.currentRotations = rotations;
                this.currentRotations.setRotationYaw(rotationYaw);
                this.currentRotations.setRotationPitch(rotationPitch);
                break;
            }
            case SRT_BYPASS_I: {
                float[] rotations = RotationUtil.doSRTBypassIRotations(this.currentEntity);
                this.currentRotations = new Rotation(KillAura.mc.thePlayer.posX, KillAura.mc.thePlayer.posY, KillAura.mc.thePlayer.posZ, rotations[0], rotations[1]);
            }
        }
        if (((Boolean)this.silent.getValue()).booleanValue() && !((TargetMode)this.targetMode.getValue()).isLockView()) {
            return;
        }
        KillAura.mc.getRenderViewEntity().rotationYaw = this.currentRotations.getRotationYaw();
        KillAura.mc.getRenderViewEntity().rotationPitch = this.currentRotations.getRotationPitch();
    }

    @Override
    public void onDisable() {
        KillAura.mc.thePlayer.setFakeBlocking(false);
        this.currentEntity = null;
    }

    public void handleAutoBlock(boolean pre) {
        if (KillAura.mc.thePlayer.getHeldItem() == null || !(KillAura.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword)) {
            return;
        }
        switch ((BlockMode)this.blockMode.getValue()) {
            case NCP: {
                PacketUtil.send(pre ? new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN) : new C08PacketPlayerBlockPlacement(KillAura.mc.thePlayer.getHeldItem()));
                break;
            }
            case HYPIXEL: {
                PacketUtil.send(pre ? new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN) : new C08PacketPlayerBlockPlacement(null));
            }
        }
        KillAura.mc.thePlayer.setFakeBlocking(true);
    }

    @Override
    public String getMode() {
        return ((TargetMode)this.targetMode.getValue()).getName() + " | " + DecimalFormat.getNumberInstance().format(this.range.getValue());
    }

    public EnumProperty<AttackMode> getAttackMode() {
        return this.attackMode;
    }

    public EnumProperty<TargetMode> getTargetMode() {
        return this.targetMode;
    }

    public EnumProperty<BlockMode> getBlockMode() {
        return this.blockMode;
    }

    public BooleanProperty getInvisible() {
        return this.invisible;
    }

    public BooleanProperty getPlayers() {
        return this.players;
    }

    public BooleanProperty getAnimals() {
        return this.animals;
    }

    public BooleanProperty getHostile() {
        return this.hostile;
    }

    public NumberProperty getMin() {
        return this.min;
    }

    public NumberProperty getMax() {
        return this.max;
    }

    public NumberProperty getRandomizationLevel() {
        return this.randomizationLevel;
    }

    public NumberProperty getRange() {
        return this.range;
    }

    public NumberProperty getSmoothing() {
        return this.smoothing;
    }

    public BooleanProperty getRandomizeAps() {
        return this.randomizeAps;
    }

    public BooleanProperty getMovementCorrection() {
        return this.movementCorrection;
    }

    public BooleanProperty getSilent() {
        return this.silent;
    }

    public BooleanProperty getKeepSprint() {
        return this.keepSprint;
    }

    public BooleanProperty getSprintSpam() {
        return this.sprintSpam;
    }

    public BooleanProperty getHitbox() {
        return this.hitbox;
    }

    public AngleHelper getAngleHelper() {
        return this.angleHelper;
    }

    public ClickRandomizer getClickRandomizer() {
        return this.clickRandomizer;
    }

    public int getTargetIndex() {
        return this.targetIndex;
    }

    public int getCurrentRandomCPS() {
        return this.currentRandomCPS;
    }

    public Rotation getCurrentRotations() {
        return this.currentRotations;
    }

    public EntityLivingBase getCurrentEntity() {
        return this.currentEntity;
    }

    public float getCurrentYaw() {
        return this.currentYaw;
    }

    public float getCurrentPitch() {
        return this.currentPitch;
    }

    public float getPitchIncrease() {
        return this.pitchIncrease;
    }

    public float getBlockPosValue() {
        return this.blockPosValue;
    }

    public boolean isChangingArea() {
        return this.changingArea;
    }

    public int getMaxYaw() {
        return this.maxYaw;
    }

    public int getMaxPitch() {
        return this.maxPitch;
    }

    public int getRotationSwap() {
        return this.rotationSwap;
    }

    public int getIndex() {
        return this.index;
    }

    static enum TargetMode implements INameable
    {
        SINGLE("Single", false),
        SWITCH("Switch", false),
        MULTIPLE("Multiple", false),
        ADVANCED("Advanced", true),
        SMART("Smart", true),
        SRT_BYPASS_I("SRT Bypass I", false),
        AIM_ASSIST("AimAssist", true);

        private final String name;
        private boolean lockView;

        private TargetMode(String name, boolean lockView) {
            this.name = name;
            this.lockView = lockView;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public boolean isLockView() {
            return this.lockView;
        }
    }

    static enum AttackMode implements INameable
    {
        PRE("Pre", true),
        POST("Post", false);

        private final String name;
        private final boolean pre;

        private AttackMode(String name, boolean pre) {
            this.name = name;
            this.pre = pre;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public boolean isPre() {
            return this.pre;
        }
    }

    static enum BlockMode implements INameable
    {
        NONE("None"),
        NCP("NCP"),
        HYPIXEL("Hypixel"),
        SEMI_LEGIT("Legit"),
        FAKE("Client-Sided");

        private final String name;

        private BlockMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}

