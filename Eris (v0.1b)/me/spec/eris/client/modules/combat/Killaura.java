package me.spec.eris.client.modules.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import me.spec.eris.Eris;
import me.spec.eris.api.event.Event;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.integration.server.interfaces.Gamemode;
import me.spec.eris.client.modules.movement.Flight;
import me.spec.eris.client.modules.movement.Longjump;
import me.spec.eris.client.modules.movement.Scaffold;
import me.spec.eris.client.modules.movement.Speed;
import me.spec.eris.api.value.types.BooleanValue;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.api.value.types.NumberValue;
import me.spec.eris.utils.player.PlayerUtils;
import me.spec.eris.utils.world.TimerUtils;
import me.spec.eris.utils.math.MathUtils;
import me.spec.eris.utils.math.rotation.AngleUtility;
import me.spec.eris.utils.math.rotation.RotationUtils;
import me.spec.eris.utils.math.vec.Vector;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.*;
import net.minecraft.potion.Potion;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;

public class Killaura extends Module {
    public ModeValue<Mode> modeValue = new ModeValue<>("Mode", Mode.SWITCH, this);
    public BooleanValue<Boolean> attackSettings = new BooleanValue<>("Attack settings", false, this, true, "Display settings for attacking");
    /*Attack settings*/
    public ModeValue<BlockMode> autoBlock = new ModeValue<>("Autoblock", BlockMode.OFF, this, () -> attackSettings.getValue(), "Autoblock modes");
    public NumberValue<Integer> clicksPerSecond = new NumberValue<Integer>("CPS", 15, 1, 20, this, () -> attackSettings.getValue(), "Clicks per second");
    public NumberValue<Integer> clicksPerSecondRandom = new NumberValue<Integer>("CPS Randomization", 0, 0, 5, this, () -> attackSettings.getValue(), "Dynamic randomization range");
    public NumberValue<Double> targetingDist = new NumberValue<Double>("Blocking Distance", 4.25, 2.0, 10.0, this, () -> attackSettings.getValue(), "Range at which the killaura aims and blocks");
    public NumberValue<Double> range = new NumberValue<Double>("Attack distance", 4.25, 2.0, 6.0, this, () -> attackSettings.getValue(), "Maximum range at which the killaura attacks");
    public NumberValue<Double> rayCastDist = new NumberValue<Double>("Walls Distance", 1.0, 0.0, 3.0, this, () -> attackSettings.getValue(), "If the entity is farther than this distance behind a wall, they wont be attacked");
    public NumberValue<Integer> abusiveAura = new NumberValue<Integer>("Reach VL", 0, 0, 6, this, () -> attackSettings.getValue(), "Advanced setting to exploit VL in reach checks, keep at 0 if unknown");
    public BooleanValue<Boolean> attackTarget = new BooleanValue<>("Attack Target", true, this, () -> attackSettings.getValue(), "Should the killaura attack, or just aim and block?");
    public BooleanValue<Boolean> dynamicAttack = new BooleanValue<>("Dynamic Attacks", true, this, () -> attackSettings.getValue(), "Optimize the speed at which the killaura attacks, great for hvh and hypixel");
    public BooleanValue<Boolean> armorBreak = new BooleanValue<>("Armor Breaker", false, this, () -> attackSettings.getValue(), "Send change item packets to increase the amount of armor broken, flags Nc+");
    public BooleanValue<Boolean> hitbox = new BooleanValue<>("Hitbox Checks", false, this, () -> attackSettings.getValue(), "Properly check if the killaura is aiming at the target before actually attacking");
    public BooleanValue<Boolean> skiddedNoCheat = new BooleanValue<>("Hit Glitch", false, this, () -> attackSettings.getValue(), "Abuses a flaw in hypixel's (and some other's) anticheat to bypass the hitglitch you retreive post flaging a check");

    public BooleanValue<Boolean> aimingSettings = new BooleanValue<>("Aiming settings", false, this, true, "Display settings for aiming");
    /*Aim settings*/
    public ModeValue<AimMode> aimMode = new ModeValue<>("Aim Mode", AimMode.BASIC, this, () -> aimingSettings.getValue(), "The mode it aims at - basic for NC+, assist is literal aim assist");
    public BooleanValue<Boolean> sprint = new BooleanValue<>("Sprint Checks", false, this, () -> aimingSettings.getValue(), "Select if the anticheat checks for sprint speed like cowards");
    public BooleanValue<Boolean> lockView = new BooleanValue<>("Silent Aiming", true, this, () -> aimingSettings.getValue(), "Aim silently, do not force player to change");

    public ModeValue<LockMode> lockMode = new ModeValue<>("Lock view Mode", LockMode.BOTH, this, () -> aimingSettings.getValue() && !lockView.getValue(), "Change the axis that aim is forced on (yaw/pitch etc)");
    public NumberValue<Integer> angleSmoothing = new NumberValue<Integer>("Smoothing", 20, 20, 100, this, () -> aimingSettings.getValue(), "How smooth is the aura");
    /*Targetting settings*/
    public BooleanValue<Boolean> targetingSettings = new BooleanValue<>("Targeting settings", false, this, true, "Display settings for attacking");
    public BooleanValue<Boolean> invisible = new BooleanValue<>("Invisibles", false, this, () -> targetingSettings.getValue(), "Attack invisibles");
    public BooleanValue<Boolean> animals = new BooleanValue<>("Animals", true, this, () -> targetingSettings.getValue(), "Attack animals");
    public BooleanValue<Boolean> players = new BooleanValue<>("Players", true, this, () -> targetingSettings.getValue(), "Attack players");
    public BooleanValue<Boolean> dead = new BooleanValue<>("Deads", true, this, () -> targetingSettings.getValue(), "Attack dead niggas");
    public BooleanValue<Boolean> mobs = new BooleanValue<>("Mobs", false, this, () -> targetingSettings.getValue(), "Attack monsters");
    public BooleanValue<Boolean> teams = new BooleanValue<>("Teams", false, this, () -> targetingSettings.getValue(), "Ignore team members");

    public double targetedArea;
    public boolean changingArea, blocking, reverse, shouldCritical, fuckCheckVLs;
    public int delay, index, maxYaw, reachVL, hitCounter, maxPitch, targetIndex, rotationSwap, timesAttacked, offset, waitTicks;
    public float currentYaw, currentPitch, pitchIncrease, animated = 20F, blockPosValue;
    //Begin static abuse
    public static Entity lastAimedTarget;
    public static EntityLivingBase target;
    public static EntityLivingBase currentEntity;
    //End static abuse
    public TimerUtils clientRaper;
    public TimerUtils critStopwatch;
    public TimerUtils clickStopwatch;
    public ArrayList<EntityLivingBase> targetList;
    public List<EntityLivingBase> targets = new ArrayList<>();
    public AngleUtility angleUtility;
    public enum Mode {SWITCH}//, MULTI}
    public enum LockMode {YAW, PITCH, BOTH}
    public enum AimMode {ASSIST, ADVANCED, BASIC}
    public enum BlockMode {OFF, NCP, OFFSET, FALCON, FAKE}
    public Killaura(String racism) {
        super("KillAura", ModuleCategory.COMBAT, racism);
        angleUtility = new AngleUtility(70, 250, 70, 200);
        clickStopwatch = new TimerUtils();
        clientRaper = new TimerUtils();
        targetList = new ArrayList<>();
        critStopwatch = new TimerUtils();
    }

    @Override
    public void onEnable() {
        blockPosValue = 1;
        waitTicks = 0;
        delay = 73;
        unBlock();
        critStopwatch.reset();
        super.onEnable();
    }

    @Override
    public void onDisable() {
        unBlock();
        super.onDisable();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof EventUpdate) {
            setMode(modeValue.getValue().name());
            EventUpdate eu = (EventUpdate) e;
            if (!PlayerUtils.isHoldingSword() && this.blocking) {
                this.blocking = false;
            }
            if (sprint.getValue() && Math.abs(mc.thePlayer.rotationYaw - eu.getYaw()) > 20 && mc.thePlayer.isSprinting()) {
                mc.thePlayer.setSprinting(false);
            }
            if (Eris.getInstance().moduleManager.isEnabled(Longjump.class)) {
                waitTicks = 3;
                return;
            }
            if (waitTicks > 0) {
                waitTicks--;
                return;
            }
            boolean scaffoldCheck = Eris.getInstance().moduleManager.getModuleByClass(Scaffold.class).isToggled();
            if (modeValue.getValue() == Mode.SWITCH) {
                updateTargetList();
                if (targetList.isEmpty() || targetList.size() - 1 < targetIndex) {
                    reset(-1, eu);
                    return;
                }

                if (targetIndex == -1) {
                    reset(0, eu);
                    return;
                }
                if (!PlayerUtils.isValid(targetList.get(targetIndex), targetingDist.getValue(), invisible.getValue(), teams.getValue(), dead.getValue(), players.getValue(), animals.getValue(), mobs.getValue(), rayCastDist.getValue())) {
                    reset(-1, eu);
                    return;
                }
                Criticals criticals = ((Criticals)Eris.getInstance().getModuleManager().getModuleByClass(Criticals.class));
                target = currentEntity = targetList.get(targetIndex); 
                shouldCritical = critStopwatch.hasReached(50) && mc.thePlayer.isCollidedVertically && mc.thePlayer.onGround && criticals.isToggled() && !Eris.getInstance().moduleManager.getModuleByClass(Speed.class).isToggled() && !Eris.getInstance().moduleManager.getModuleByClass(Flight.class).isToggled() &&  (criticals.modeValue.getValue().equals(Criticals.Mode.WATCHDOG) && !mc.thePlayer.isMoving());
                if (eu.isPre()) {
                    if (Eris.getInstance().moduleManager.getModuleByClass(Scaffold.class).isToggled()) {
                        index = 3;
                    } 
                    aim(eu);
                    if (scaffoldCheck) {
                        unBlock();
                        return;
                    }
                    if (criticals.isToggled()) {
                        criticals.doUpdate(eu);
                    }
                    unBlock();
                    prepareAttack(eu, false);
                } else if (!eu.isPre()) {
                    if (!scaffoldCheck ) {
                        block();
                    }
                }
            }
           /* if (modeValue.getValue() == Mode.MULTI) {
                for (EntityPlayer entity : mc.theWorld.playerEntities) {
                    if (entity.getDistanceSqToEntity(mc.thePlayer) <= range.getValue()) {
                        // TODO: stuff
                    }
                }
            }*/
        }
    }

    public void aim(EventUpdate e) {
        AxisAlignedBB bb = getBoundingBox(target);
        double targetMinX = bb.minX;
        double targetMaxX = bb.maxX;
        double targetMinZ = bb.minZ;
        double targetMaxZ = bb.maxZ;
        AxisAlignedBB pbb = getBoundingBox(mc.thePlayer);
        double playerMinX = pbb.minX;
        double playerMaxX = pbb.maxX;
        double playerMinZ = pbb.minZ;
        double playerMaxZ = pbb.maxZ;
        Vector.Vector3<Double> enemyCords = new Vector.Vector3<>(targetMinX + (targetMaxX - targetMinX) / 2,target.getEntityBoundingBox().minY ,targetMinZ + (targetMaxZ - targetMinZ) / 2);
        Vector.Vector3<Double> myCords = new Vector.Vector3<>(playerMinX + (playerMaxX - playerMinX) / 2, mc.thePlayer.posY + pitchIncrease, playerMinZ + (playerMaxZ - playerMinZ) / 2);
        AngleUtility.Angle srcAngle = new AngleUtility.Angle(!lockView.getValue() ? mc.thePlayer.rotationYaw : e.getYaw(), !lockView.getValue() ? mc.thePlayer.rotationPitch : e.getPitch());
        AngleUtility.Angle dstAngle = angleUtility.calculateAngle(enemyCords, myCords, target, rotationSwap);
        AngleUtility.Angle newSmoothing = angleUtility.smoothAngle(dstAngle, srcAngle, 300, 40 * 30);
        double x = target.posX - mc.thePlayer.posX + (target.lastTickPosX - target.posX) / 2;
        double z = target.posZ - mc.thePlayer.posZ + (target.lastTickPosZ - target.posZ) / 2;
        if (lastAimedTarget != target) {
            index = 3;
            fuckCheckVLs = true;
            changingArea = false;
            targetedArea = rotationSwap = 0;
        }
        lastAimedTarget = target;
        float destinationPitch = newSmoothing.getPitch();
        if (destinationPitch > 90) destinationPitch = 90;
        if (destinationPitch < -90) destinationPitch = -90;
        float destinationYaw = (float) (currentYaw - RotationUtils.constrainAngle(currentYaw - (float) -(Math.atan2(x, z) * (58 + targetedArea))) / (1 + ((angleSmoothing.getValue()) * .035)) + (mc.thePlayer.getDistanceToEntity(target) * .05 + (mc.thePlayer.ticksExisted % 4 == 0 ? 40 * .001 : 0)));
        boolean ticks = mc.thePlayer.ticksExisted % 20 == 0;
        if (mc.thePlayer.ticksExisted % 15 == 0) {
            if (rotationSwap++ >= 3) {
                rotationSwap = 0;
            }
            pitchIncrease += changingArea ? MathUtils.getRandomInRange(-.055, -.075) : MathUtils.getRandomInRange(.055, .075);
        }
        if (pitchIncrease >= .9) {
            pitchIncrease = .9f;
            changingArea = true;
        }
        if (pitchIncrease <= -.15) {
            pitchIncrease = -.15f;
            changingArea = false;
        }
        if (aimMode.getValue().equals(AimMode.ASSIST)) {
            float playerYaw = mc.thePlayer.rotationYaw;
            float playerPitch = mc.thePlayer.rotationPitch;
            float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f1 = f * f * f * 8.0F;
            float f2 = (float) maxYaw * f1;
            float f3 = (float) maxPitch * f1;
            if (Math.abs(playerYaw - destinationYaw) > 2) {
                if (RotationUtils.rayTrace(playerYaw, playerPitch, range.getValue()) == null) {
                    if (playerYaw > destinationYaw) {
                        maxYaw -= MathUtils.getRandomInRange(5, 7);
                    } else {
                        maxYaw += MathUtils.getRandomInRange(5, 7);
                    }
                } else {
                    maxYaw *= .5;
                }
            } else {
                maxYaw *= .5;
            }
            if (Math.abs(playerPitch - AngleUtility.getRotations(target)[1]) > 2) {
                if (RotationUtils.rayTrace(playerYaw, playerPitch, range.getValue()) == null) {
                    if (playerPitch > AngleUtility.getRotations(target)[1]) {
                        maxPitch += MathUtils.getRandomInRange(1, 3);
                    } else {
                        maxPitch -= MathUtils.getRandomInRange(1, 3);
                    }
                } else {
                    maxPitch *= .5;
                }
            } else {
                maxPitch *= .5;
            }
            
            mc.thePlayer.rotationPitch = MathHelper.clamp_float((float) ((double) playerPitch - (double) f3 * 0.15D), -90.0F, 90.0F);
            mc.thePlayer.rotationYaw = (float) ((double) playerYaw + (double) f2 * 0.15D);
        } else {
            float destPitch = aimMode.getValue().equals(AimMode.ADVANCED) ? destinationPitch : AngleUtility.getRotations(target)[1];
            float destYaw =  aimMode.getValue().equals(AimMode.ADVANCED) ? destinationYaw :  AngleUtility.getRotations(target)[0];
            float gcdValue = ticks ? aimMode.getValue().ordinal() * .1f + .0337f : aimMode.getValue().ordinal() * .1f + .042069f;
            float yawToSet = (float) MathUtils.preciseRound(destYaw, 1)+ (gcdValue * 2) ;
            float pitchToSet = (float) MathUtils.preciseRound(destPitch, 1) + gcdValue;
            if (!lockView.getValue()) {
                switch (lockMode.getValue()) {
                    case PITCH:
                        mc.thePlayer.rotationPitch = pitchToSet;
                        e.setYaw(yawToSet);
                    case YAW:
                        mc.thePlayer.rotationYaw = yawToSet;
                        e.setPitch(pitchToSet);
                        break;
                    case BOTH:
                        mc.thePlayer.rotationYaw = yawToSet;
                        mc.thePlayer.rotationPitch = pitchToSet;
                        break;

                }
            } else {
                e.setPitch(pitchToSet);
                e.setYaw(yawToSet);
            }
        }
        currentPitch = e.getPitch();
        currentYaw = e.getYaw();
    }

    public AxisAlignedBB getBoundingBox(Entity ent) {
        return ent.getEntityBoundingBox();
    }

    public void prepareAttack(EventUpdate e, boolean scaffoldCheck) {
        if (scaffoldCheck) return;
        Criticals crits = ((Criticals) Eris.getInstance().moduleManager.getModuleByClass(Criticals.class));
        if (autoBlock.getValue().equals(BlockMode.FALCON) && PlayerUtils.isHoldingSword()) {
            if (mc.thePlayer.ticksExisted % 6 == 0) {
                if (blocking) {
                    mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
                    blocking = false;
                } else {
                    mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(blockPosValue, -1, blockPosValue), 255, mc.thePlayer.getHeldItem(), 0, 0, 0));
                    blocking = true;
                }
            }
            if (clickStopwatch.hasReached(delay)) {
                if (!blocking) {
                    attackPrepare(e);
                    delay = Math.max(50, (1000 / clicksPerSecond.getValue()) + offset);
                } else {
                    delay = 51;
                }
                clickStopwatch.reset();
            }
        } else {
            if (clickStopwatch.hasReached(dynamicAttack.getValue() ? index > 0 ? 60 : (crits.airTime > 2 || shouldCritical || mc.thePlayer.fallDistance >= .626 && mc.thePlayer.ticksExisted % 2 != 0 || target.timesAttacked < 1) ? 50  : timesAttacked % 20 == 0 ? 52 :  50 : delay)) {
                attackPrepare(e);
                clickStopwatch.reset();
                delay = Math.max(50, (1000 / clicksPerSecond.getValue()) + offset);
                if (offset > clicksPerSecondRandom.getValue())   reverse = true;
                if (offset <= -clicksPerSecondRandom.getValue())  reverse = false;
            }
        }
        offset += reverse ? -MathUtils.getRandomInRange(1, 3) : -MathUtils.getRandomInRange(1, 3);
    }

    public void attackPrepare(EventUpdate e) {
        boolean armorBreaker = armorBreak.getValue() && mc.thePlayer.ticksExisted % 4 == 0;
        if (armorBreaker) PlayerUtils.swapToItem();

        if (hitbox.getValue() || index > 0) {
            if (RotationUtils.rayTrace(e.getYaw(), e.getPitch(), range.getValue()) != null) {
                attackExecute(e, RotationUtils.rayTrace(e.getYaw(), e.getPitch(), range.getValue()), reachVL > 0 ? range.getValue() + 1 : range.getValue() + 1, targetingDist.getValue(), !attackTarget.getValue());
            } else {
                mc.thePlayer.swingItem();
            }
            index--;
        } else {
            attackExecute(e, target, reachVL > 0 ? range.getValue() + 1 : range.getValue(), targetingDist.getValue(), !attackTarget.getValue());
        }
        if (MathUtils.round(mc.thePlayer.getDistanceToEntity(target), 4f) <= range.getValue() + 1) {
            if (reachVL > 0) reachVL--;
        } else {
            reachVL = abusiveAura.getValue();
        }
        if (armorBreaker) PlayerUtils.swapBackToItem();
    }

    public void attackExecute(EventUpdate e, EntityLivingBase target, double range, double targetRange, boolean dontAttack) {
        if (dontAttack || target.getDistanceToEntity(mc.thePlayer) > range) return;
        boolean flag = mc.thePlayer.fallDistance > 0.0F && !mc.thePlayer.onGround && !mc.thePlayer.isOnLadder() && !mc.thePlayer.isInWater() && !mc.thePlayer.isPotionActive(Potion.blindness) && mc.thePlayer.ridingEntity == null && target instanceof EntityLivingBase;
        float f = (float) mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
        if (EnchantmentHelper.func_152377_a(mc.thePlayer.getHeldItem(), target.getCreatureAttribute()) > 0)  mc.thePlayer.onEnchantmentCritical(target);

        if (((skiddedNoCheat.getValue() && target.timesAttacked < 1) || fuckCheckVLs) && !Eris.getInstance().getServerIntegration().getGameMode().equals(Gamemode.DUELS)) {
            int beforeHeldItem = mc.thePlayer.inventory.currentItem;
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = 8));
            mc.thePlayer.sendQueue.addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem = beforeHeldItem));
            fuckCheckVLs = false;
        }
        mc.thePlayer.swingItem();
        if (shouldCritical) {
            for (double offset : ((Criticals) Eris.getInstance().moduleManager.getModuleByClass(Criticals.class)).getOffsets()) {
                mc.thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.boundingBox.minY + offset, mc.thePlayer.posZ, false));
            }
            critStopwatch.reset();
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));
        target.timesAttacked++;//Times attacked will be very useful for bypassing future anticheats on jah
        timesAttacked += 1;
    }

    public void unBlock() {
        if (!PlayerUtils.isHoldingSword() || !blocking || autoBlock.getValue().equals(BlockMode.FALCON) || autoBlock.getValue().equals(BlockMode.OFF)) return;
        double value = autoBlock.getValue().equals(BlockMode.OFFSET) && mc.thePlayer.hurtTime > 2 ?  -.8f : -1;
        if (Eris.getInstance().getServerIntegration().getGameMode().equals(Gamemode.DUELS) || !mc.thePlayer.isMoving()) value = ThreadLocalRandom.current().nextDouble(Double.MIN_VALUE, Double.MAX_VALUE);
        mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(value, value, value), EnumFacing.DOWN));
        blocking = false;
    }

    public void block() { 
        if (!PlayerUtils.isHoldingSword() || blocking || autoBlock.getValue().equals(BlockMode.FALCON) || autoBlock.getValue().equals(BlockMode.OFF)) return;
        mc.getNetHandler().addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.inventory.getCurrentItem(), 0, 0, 0));
        blocking = true;
    }

    private void reset(int i, EventUpdate event) {
        unBlock();
        index = 0;
        currentEntity = null;
        targetIndex = i;
    }

    private void updateTargetList() {
        target = null;
        targetList.clear();
        mc.theWorld.getLoadedEntityList().forEach(entity -> {
            if (entity instanceof EntityLivingBase) {
                if (PlayerUtils.isValid((EntityLivingBase) entity, targetingDist.getValue(), invisible.getValue(), teams.getValue(), dead.getValue(), players.getValue(), animals.getValue(), mobs.getValue(), rayCastDist.getValue())) {
                    targetList.add((EntityLivingBase) entity);
                } else targetList.remove(entity);
            }
        });
        if (targetList.size() > 1) {
            targetList.sort(Comparator.comparingDouble(mc.thePlayer::getDistanceToEntity));
            targetList.sort((e1, e2) -> Boolean.compare(e2 instanceof EntityPlayer, e1 instanceof EntityPlayer));
        }
    }

    public static Entity getTarget() {
        return target;
    }
}
