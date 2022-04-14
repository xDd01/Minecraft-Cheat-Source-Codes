/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.combat;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.packet.PacketUtil;
import cafe.corrosion.util.player.RotationUtil;
import cafe.corrosion.util.player.TargetFilter;
import cafe.corrosion.util.player.TargetOptions;
import cafe.corrosion.util.player.extra.Rotation;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleAttributes(name="FastBow", description="Shoots nearby enemies.... really, really quickly", category=Module.Category.COMBAT)
public class FastBow
extends Module {
    private final NumberProperty targets = new NumberProperty(this, "Targets", 4, 1, 20, 1);
    private final NumberProperty packets = new NumberProperty(this, "Packets", 20, 1, 25, 1);
    private final BooleanProperty timerBypass = new BooleanProperty(this, "Timer Bypass");
    private final NumberProperty range = new NumberProperty(this, "Range", 40, 10, 60, 0.5);
    private final BooleanProperty invisible = new BooleanProperty(this, "Invisibles");
    private final BooleanProperty players = new BooleanProperty(this, "Players");
    private final BooleanProperty animals = new BooleanProperty(this, "Animals");
    private final BooleanProperty hostile = new BooleanProperty(this, "Mobs");

    public FastBow() {
        this.registerEventHandler(EventUpdate.class, eventUpdate -> {
            if (eventUpdate.isPre() || !FastBow.mc.thePlayer.isCollidedVertically) {
                return;
            }
            if (FastBow.mc.thePlayer.getHeldItem() == null || !(FastBow.mc.thePlayer.getHeldItem().getItem() instanceof ItemBow) || !FastBow.mc.gameSettings.keyBindUseItem.isKeyDown()) {
                return;
            }
            Object targetOptions = ((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)((TargetOptions.TargetOptionsBuilder)TargetOptions.builder().hostile((Boolean)this.hostile.getValue())).invisible((Boolean)this.invisible.getValue())).players((Boolean)this.players.getValue())).animals((Boolean)this.animals.getValue())).range(((Number)this.range.getValue()).doubleValue())).build();
            List entityList = FastBow.mc.theWorld.getLoadedEntityList().stream().filter(entity -> entity instanceof EntityLivingBase).map(entity -> (EntityLivingBase)entity).filter(TargetFilter.targetFilter(targetOptions)).limit(((Number)this.targets.getValue()).intValue()).collect(Collectors.toList());
            for (EntityLivingBase target : entityList) {
                Rotation rotation = RotationUtil.getBowAngles(target);
                PacketUtil.sendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(FastBow.mc.thePlayer.posX, FastBow.mc.thePlayer.posY, FastBow.mc.thePlayer.posZ, rotation.getRotationYaw(), rotation.getRotationPitch(), true));
                PacketUtil.sendNoEvent(new C08PacketPlayerBlockPlacement(FastBow.mc.thePlayer.getHeldItem()));
                if (((Boolean)this.timerBypass.getValue()).booleanValue()) {
                    PacketUtil.sendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(FastBow.mc.thePlayer.posX, FastBow.mc.thePlayer.posY - 0.5, FastBow.mc.thePlayer.posZ, rotation.getRotationYaw(), rotation.getRotationPitch(), true));
                }
                for (int i2 = 0; i2 < ((Number)this.packets.getValue()).intValue(); ++i2) {
                    PacketUtil.sendNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(FastBow.mc.thePlayer.posX, FastBow.mc.thePlayer.posY, FastBow.mc.thePlayer.posZ, rotation.getRotationYaw(), rotation.getRotationPitch(), true));
                }
                this.swap(9, FastBow.mc.thePlayer.inventory.currentItem);
                this.swap(9, FastBow.mc.thePlayer.inventory.currentItem);
                PacketUtil.sendNoEvent(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
            }
        });
    }

    public void swap(int slot, int hotBarNumber) {
        FastBow.mc.playerController.windowClick(FastBow.mc.thePlayer.inventoryContainer.windowId, slot, hotBarNumber, 2, FastBow.mc.thePlayer);
    }
}

