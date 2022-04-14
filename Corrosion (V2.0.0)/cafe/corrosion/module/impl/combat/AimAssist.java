/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.combat;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.combat.AngleHelper;
import cafe.corrosion.util.math.RandomUtil;
import cafe.corrosion.util.player.RotationUtil;
import cafe.corrosion.util.player.TargetFilter;
import cafe.corrosion.util.player.TargetOptions;
import cafe.corrosion.util.vector.impl.Vector3F;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;

@ModuleAttributes(name="AimAssist", description="Automatically aims for you", category=Module.Category.COMBAT)
public class AimAssist
extends Module {
    private final BooleanProperty players = new BooleanProperty(this, "Players");
    private final BooleanProperty animals = new BooleanProperty(this, "Animals");
    private final BooleanProperty hostile = new BooleanProperty(this, "Mobs");
    private final NumberProperty targetRange = new NumberProperty(this, "Target Range", 3.0, 1.0, 6.0, 1.0);
    private final NumberProperty smoothing = new NumberProperty(this, "Smoothing", 20, 0, 100, 1);
    private final AngleHelper angleHelper = new AngleHelper(70.0f, 250.0f, 70.0f, 200.0f);
    private boolean changingArea;
    private int rotationSwap;
    private int maxYaw;
    private int maxPitch;
    private float pitchIncrease;

    public AimAssist() {
        this.registerEventHandler(EventUpdate.class, eventUpdate -> {
            boolean ticks;
            Object targetOptions = ((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)TargetOptions.builder().hostile((Boolean)this.hostile.getValue())).invisible(true)).players((Boolean)this.players.getValue())).animals((Boolean)this.animals.getValue())).range(((Number)this.targetRange.getValue()).doubleValue())).hitbox(true)).build();
            List entityList = AimAssist.mc.theWorld.getLoadedEntityList().stream().filter(entity -> entity instanceof EntityLivingBase).map(entity -> (EntityLivingBase)entity).filter(TargetFilter.targetFilter(targetOptions)).collect(Collectors.toList());
            if (entityList.size() == 0) {
                return;
            }
            EntityLivingBase target = (EntityLivingBase)entityList.get(0);
            AxisAlignedBB[] boxes = new AxisAlignedBB[]{target.getEntityBoundingBox(), AimAssist.mc.thePlayer.getEntityBoundingBox()};
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
            AngleHelper.Angle srcAngle = new AngleHelper.Angle(Float.valueOf(AimAssist.mc.thePlayer.rotationYaw), Float.valueOf(AimAssist.mc.thePlayer.rotationPitch));
            AngleHelper.Angle dstAngle = this.angleHelper.calculateAngle(vectors[0], vectors[1], target, this.rotationSwap);
            AngleHelper.Angle newSmoothing = this.angleHelper.smoothAngle(dstAngle, srcAngle, 300.0f, 1200.0f);
            double x2 = target.posX - AimAssist.mc.thePlayer.posX + (target.lastTickPosX - target.posX) / 2.0;
            double z2 = target.posZ - AimAssist.mc.thePlayer.posZ + (target.lastTickPosZ - target.posZ) / 2.0;
            float currentYaw = AimAssist.mc.thePlayer.rotationYaw;
            float destinationYaw = 0.0f;
            double smooth = 1.0 + ((Number)this.smoothing.getValue()).doubleValue() * 0.035;
            destinationYaw = RotationUtil.clampAngle(currentYaw - (float)(-(Math.atan2(x2, z2) * 58.0)));
            destinationYaw = (float)((double)currentYaw - (double)destinationYaw / (smooth += (double)AimAssist.mc.thePlayer.getDistanceToEntity(target) * 0.1 + (AimAssist.mc.thePlayer.ticksExisted % 4 == 0 ? 0.04 : 0.0)));
            boolean bl2 = ticks = AimAssist.mc.thePlayer.ticksExisted % 20 == 0;
            if (AimAssist.mc.thePlayer.ticksExisted % 15 == 0) {
                if (this.rotationSwap++ >= 3) {
                    this.rotationSwap = 0;
                }
                this.pitchIncrease = (float)((double)this.pitchIncrease + (this.changingArea ? RandomUtil.random(-0.255, -0.075) : RandomUtil.random(0.055, 0.075)));
            }
            if ((double)this.pitchIncrease >= 0.9) {
                this.changingArea = true;
            }
            if ((double)this.pitchIncrease <= -0.15) {
                this.changingArea = false;
            }
            float playerYaw = (float)RotationUtil.preciseRound(destinationYaw, 1.0) + (ticks ? 0.243437f : 0.14357f);
            float playerPitch = (float)RotationUtil.preciseRound(newSmoothing.getPitch().floatValue(), 1.0) + (ticks ? 0.1335f : 0.13351f);
            float f2 = AimAssist.mc.gameSettings.mouseSensitivity * 0.6f + 0.2f;
            float f1 = f2 * f2 * f2 * 8.0f;
            float f22 = (float)this.maxYaw * f1;
            float f3 = (float)this.maxPitch * f1;
            if (Math.abs(playerYaw - destinationYaw) < 2.0f) {
                this.maxYaw = (int)((double)this.maxYaw * 0.5);
            } else if (RotationUtil.rayCast(playerYaw, playerPitch, ((Number)this.targetRange.getValue()).doubleValue()) == null) {
                this.maxYaw = playerYaw > destinationYaw ? RandomUtil.random(5, 7) : (int)((double)this.maxYaw * 0.5);
            }
            float[] rotations = AngleHelper.getRotations(target);
            if (Math.abs(playerPitch - rotations[1]) < 2.0f) {
                this.maxPitch = (int)((double)this.maxPitch * 0.5);
            } else if (RotationUtil.rayCast(playerYaw, playerPitch, ((Number)this.targetRange.getValue()).doubleValue()) == null) {
                this.maxPitch = playerPitch > rotations[1] ? (int)((double)this.maxPitch + RandomUtil.random(1.0, 3.0)) : (int)((double)this.maxPitch * 0.5);
            }
            AimAssist.mc.getRenderViewEntity().rotationPitch = MathHelper.clamp_float((float)((double)playerPitch - (double)f3 * 0.15), -90.0f, 90.0f);
            AimAssist.mc.getRenderViewEntity().rotationYaw = (float)((double)playerYaw + (double)f22 * 0.15);
            eventUpdate.setRotationYaw(AimAssist.mc.getRenderViewEntity().rotationYaw);
            eventUpdate.setRotationPitch(AimAssist.mc.getRenderViewEntity().rotationPitch);
        });
    }
}

