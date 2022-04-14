/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.player;

import cafe.corrosion.event.impl.EventPacketOut;
import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.util.nameable.INameable;
import cafe.corrosion.util.packet.PacketUtil;
import cafe.corrosion.util.player.MovementUtil;
import cafe.corrosion.util.player.PlayerUtil;
import cafe.corrosion.util.player.RotationUtil;
import cafe.corrosion.util.player.ScaffoldUtil;
import cafe.corrosion.util.player.extra.Rotation;
import cafe.corrosion.util.timer.Stopwatch;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.vecmath.Vector2d;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

@ModuleAttributes(name="Scaffold", description="Places blocks below you", category=Module.Category.PLAYER)
public class Scaffold
extends Module {
    private final Map<EnumFacing, Float> directionYaws = ImmutableMap.of(EnumFacing.SOUTH, Float.valueOf(200.0f), EnumFacing.NORTH, Float.valueOf(20.0f), EnumFacing.EAST, Float.valueOf(110.0f), EnumFacing.WEST, Float.valueOf(-110.0f));
    private final EnumProperty<RotationMode> rotationMode = new EnumProperty((Module)this, "Rotation Mode", (INameable[])RotationMode.values());
    private final EnumProperty<SprintMode> sprintMode = new EnumProperty((Module)this, "Sprint Mode", (INameable[])SprintMode.values());
    private final EnumProperty<SwingMode> swingMode = new EnumProperty((Module)this, "Swing Mode", (INameable[])SwingMode.values());
    private final EnumProperty<TowerMode> towerMode = new EnumProperty((Module)this, "Tower Mode", (INameable[])TowerMode.values());
    private final BooleanProperty itemSpoof = new BooleanProperty((Module)this, "Item Spoof", true);
    private final BooleanProperty sprint = new BooleanProperty((Module)this, "Allow Sprint", true);
    private final BooleanProperty rayCast = new BooleanProperty((Module)this, "Ray Cast", false);
    private final BooleanProperty gcdFix = new BooleanProperty((Module)this, "GCD Fix", true);
    private final BooleanProperty rotate = new BooleanProperty((Module)this, "Rotate", true);
    private final BooleanProperty swing = new BooleanProperty((Module)this, "Swing", true);
    private final BooleanProperty tower = new BooleanProperty((Module)this, "Tower", true);
    private final Stopwatch towerStopwatch = new Stopwatch();
    private ScaffoldUtil.BlockInfo blockInfo;
    private float newYaw;
    private float newPitch;
    private int startSlot;

    public Scaffold() {
        this.registerEventHandler(EventUpdate.class, event -> {
            int oldSlot = Scaffold.mc.thePlayer.inventory.currentItem;
            if (!((Boolean)this.sprint.getValue()).booleanValue()) {
                Scaffold.mc.thePlayer.setSprinting(false);
            } else if (PlayerUtil.isMoving()) {
                switch ((SprintMode)this.sprintMode.getValue()) {
                    case SPAM: {
                        PacketUtil.send(new C0BPacketEntityAction(Scaffold.mc.thePlayer, Scaffold.mc.thePlayer.ticksExisted % 2 == 0 ? C0BPacketEntityAction.Action.STOP_SPRINTING : C0BPacketEntityAction.Action.START_SPRINTING));
                        break;
                    }
                    case WATCHDOG: {
                        Scaffold.mc.timer.timerSpeed = 1.35f;
                        Scaffold.mc.thePlayer.motionX *= 0.91;
                        Scaffold.mc.thePlayer.motionZ *= 0.91;
                    }
                }
            }
            if (event.isPre()) {
                this.blockInfo = ScaffoldUtil.getBlockInfo(new BlockPos(Scaffold.mc.thePlayer));
                if (ScaffoldUtil.invCheck()) {
                    for (int i2 = 9; i2 < 36; ++i2) {
                        if (!ScaffoldUtil.isStackValid(Scaffold.mc.thePlayer.inventoryContainer.getSlot(i2).getStack())) continue;
                        Scaffold.mc.playerController.windowClick(Scaffold.mc.thePlayer.inventoryContainer.windowId, i2, 1, 2, Scaffold.mc.thePlayer);
                        break;
                    }
                }
                if (((Boolean)this.rotate.getValue()).booleanValue()) {
                    event.setRotationYaw(this.newYaw);
                    event.setRotationPitch(this.newPitch);
                }
                if (this.blockInfo != null && ((Boolean)this.rotate.getValue()).booleanValue()) {
                    float random = (float)Math.random();
                    switch ((RotationMode)this.rotationMode.getValue()) {
                        case NORMAL: {
                            Rotation rotation = RotationUtil.getScaffoldRotations(this.blockInfo.getBlockPos());
                            this.newYaw = (Boolean)this.gcdFix.getValue() != false ? (float)((double)rotation.getRotationYaw() + Math.random() / 10.0) : rotation.getRotationYaw();
                            this.newPitch = rotation.getRotationPitch();
                            break;
                        }
                        case GODBRIDGE: {
                            float yaw = this.directionYaws.get(this.blockInfo.getDirection()).floatValue();
                            this.newYaw = (Boolean)this.gcdFix.getValue() != false ? yaw + random : yaw;
                            break;
                        }
                        case REVERSE: {
                            this.newYaw = Scaffold.mc.thePlayer.rotationYaw + 180.0f;
                            this.newPitch = 81.0f;
                            break;
                        }
                        case DOWN: {
                            this.newYaw = Scaffold.mc.thePlayer.rotationYaw;
                            this.newPitch = 81.0f;
                        }
                    }
                    event.setRotationYaw(this.newYaw);
                    event.setRotationPitch((float)((double)82.1f + Math.random()));
                }
            } else if (this.blockInfo != null && ScaffoldUtil.getSlot() != -1) {
                EntitySnowball entitySnowball;
                Vec3 vec3 = this.getVec3(this.blockInfo.getBlockPos(), this.blockInfo.getDirection());
                BlockPos pos = this.blockInfo.getBlockPos();
                if (((Boolean)this.rayCast.getValue()).booleanValue() && !Scaffold.mc.thePlayer.canEntityBeSeen(entitySnowball = new EntitySnowball(Scaffold.mc.theWorld, pos.getX(), pos.getY(), pos.getZ()))) {
                    return;
                }
                Scaffold.mc.thePlayer.inventory.currentItem = ScaffoldUtil.getSlot();
                if (((Boolean)this.tower.getValue()).booleanValue() && Scaffold.mc.thePlayer.movementInput.jump && !PlayerUtil.isMoving()) {
                    switch ((TowerMode)this.towerMode.getValue()) {
                        case NORMAL: {
                            Scaffold.mc.thePlayer.motionY = 0.42f;
                            Scaffold.mc.thePlayer.motionX = 0.0;
                            Scaffold.mc.thePlayer.motionZ = 0.0;
                            break;
                        }
                        case NCP: {
                            if (this.towerStopwatch.hasElapsed(150L)) {
                                Scaffold.mc.thePlayer.motionY = 0.42f;
                                this.towerStopwatch.reset();
                            }
                            Scaffold.mc.thePlayer.motionX = 0.0;
                            Scaffold.mc.thePlayer.motionZ = 0.0;
                        }
                    }
                }
                PacketUtil.send(new C0APacketAnimation());
                if (Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, Scaffold.mc.thePlayer.inventoryContainer.getSlot(36 + ScaffoldUtil.getSlot()).getStack(), this.blockInfo.getBlockPos(), this.blockInfo.getDirection(), vec3)) {
                    if (Scaffold.mc.thePlayer.onGround) {
                        Scaffold.mc.thePlayer.jump();
                        Vector2d vector2d = MovementUtil.getMotion(-0.063);
                        for (int i3 = 0; i3 < 4; ++i3) {
                            PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(Scaffold.mc.thePlayer.posX + vector2d.x, Scaffold.mc.thePlayer.posY, Scaffold.mc.thePlayer.posZ + vector2d.y, true));
                            PacketUtil.sendNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY, Scaffold.mc.thePlayer.posZ, true));
                        }
                        Scaffold.mc.thePlayer.motionY = 0.362f;
                    }
                    if (((Boolean)this.swing.getValue()).booleanValue()) {
                        switch ((SwingMode)this.swingMode.getValue()) {
                            case NORMAL: {
                                Scaffold.mc.thePlayer.swingItem();
                                break;
                            }
                            case SILENT: {
                                PacketUtil.send(new C0APacketAnimation());
                            }
                        }
                    }
                }
                if (((Boolean)this.itemSpoof.getValue()).booleanValue()) {
                    Scaffold.mc.thePlayer.inventory.currentItem = oldSlot;
                }
                this.blockInfo = null;
            }
        });
        this.registerEventHandler(EventPacketOut.class, event -> {
            if (!((Boolean)this.sprint.getValue()).booleanValue() || !((SprintMode)this.sprintMode.getValue()).isCancel()) {
                return;
            }
            if (!(event.getPacket() instanceof C0BPacketEntityAction)) {
                return;
            }
            C0BPacketEntityAction packet = (C0BPacketEntityAction)event.getPacket();
            C0BPacketEntityAction.Action action = packet.getAction();
            if (action == C0BPacketEntityAction.Action.START_SPRINTING || action == C0BPacketEntityAction.Action.STOP_SPRINTING) {
                event.setCancelled(true);
            }
        });
    }

    @Override
    public void onEnable() {
        if (Scaffold.mc.thePlayer != null) {
            if (((Boolean)this.sprint.getValue()).booleanValue() && ((SprintMode)this.sprintMode.getValue()).isCancel()) {
                PacketUtil.send(new C0BPacketEntityAction(Scaffold.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
            this.startSlot = Scaffold.mc.thePlayer.inventory.currentItem;
        }
        super.onEnable();
    }

    @Override
    public void onDisable() {
        if (!((Boolean)this.itemSpoof.getValue()).booleanValue()) {
            Scaffold.mc.thePlayer.inventory.currentItem = this.startSlot;
        }
        Scaffold.mc.timer.timerSpeed = 1.0f;
        this.towerStopwatch.reset();
        super.onDisable();
    }

    private Vec3 getVec3(BlockPos pos, EnumFacing face) {
        Vec3i vec3i = face.getDirectionVec();
        float x2 = (float)vec3i.getX() * 0.5f;
        float y2 = (float)vec3i.getY() * 0.5f;
        float z2 = (float)vec3i.getZ() * 0.5f;
        return new Vec3(pos).addVector(x2 + 0.5f, y2 + 0.5f, z2 + 0.5f);
    }

    @Override
    public String getMode() {
        return ((RotationMode)this.rotationMode.getValue()).getName() + " | " + ((SwingMode)this.swingMode.getValue()).getName() + " | " + ((SprintMode)this.sprintMode.getValue()).getName();
    }

    public Map<EnumFacing, Float> getDirectionYaws() {
        return this.directionYaws;
    }

    public EnumProperty<RotationMode> getRotationMode() {
        return this.rotationMode;
    }

    public EnumProperty<SprintMode> getSprintMode() {
        return this.sprintMode;
    }

    public EnumProperty<SwingMode> getSwingMode() {
        return this.swingMode;
    }

    public EnumProperty<TowerMode> getTowerMode() {
        return this.towerMode;
    }

    public BooleanProperty getItemSpoof() {
        return this.itemSpoof;
    }

    public BooleanProperty getSprint() {
        return this.sprint;
    }

    public BooleanProperty getRayCast() {
        return this.rayCast;
    }

    public BooleanProperty getGcdFix() {
        return this.gcdFix;
    }

    public BooleanProperty getRotate() {
        return this.rotate;
    }

    public BooleanProperty getSwing() {
        return this.swing;
    }

    public BooleanProperty getTower() {
        return this.tower;
    }

    public Stopwatch getTowerStopwatch() {
        return this.towerStopwatch;
    }

    public ScaffoldUtil.BlockInfo getBlockInfo() {
        return this.blockInfo;
    }

    public float getNewYaw() {
        return this.newYaw;
    }

    public float getNewPitch() {
        return this.newPitch;
    }

    public int getStartSlot() {
        return this.startSlot;
    }

    static enum RotationMode implements INameable
    {
        NORMAL("Normal"),
        GODBRIDGE("God Bridge"),
        REVERSE("Reverse"),
        DOWN("Down");

        private final String name;

        private RotationMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    static enum SwingMode implements INameable
    {
        NORMAL("Normal"),
        SILENT("Silent");

        private final String name;

        private SwingMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    static enum TowerMode implements INameable
    {
        NORMAL("Normal"),
        NCP("NCP");

        private final String name;

        private TowerMode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }

    static enum SprintMode implements INameable
    {
        NORMAL("Normal", false),
        CANCEL("Cancel", true),
        WATCHDOG("Watchdog", true),
        SPAM("Spam", false);

        private final String name;
        private final boolean cancel;

        private SprintMode(String name, boolean cancel) {
            this.name = name;
            this.cancel = cancel;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public boolean isCancel() {
            return this.cancel;
        }
    }
}

