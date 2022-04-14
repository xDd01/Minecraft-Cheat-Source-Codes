package me.spec.eris.utils.player;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;

import me.spec.eris.Eris;
import me.spec.eris.client.events.player.EventUpdate;
import me.spec.eris.client.modules.combat.AntiBot;
import me.spec.eris.utils.math.rotation.RotationUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;

public class PlayerUtils {

    public static void sendPosition(double x, double y, double z, boolean ground, boolean movement) {
        if (movement) {
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(Minecraft.getMinecraft().thePlayer.posX + x, Minecraft.getMinecraft().thePlayer.posY + y, Minecraft.getMinecraft().thePlayer.posZ + z, EventUpdate.lastYaw, EventUpdate.lastPitch, ground));
        } else {
            Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueueNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX + x, Minecraft.getMinecraft().thePlayer.posY + y, Minecraft.getMinecraft().thePlayer.posZ + z, ground));
        }
    }

    public static boolean isValid(EntityLivingBase entity, double range, boolean invisible, boolean teams, boolean dead, boolean players, boolean animals, boolean monsters, double rayCastDist) {
        if (entity == Minecraft.getMinecraft().thePlayer)
            return false;

        if(Eris.getInstance().friendManager.isFriend(entity))
            return false;

        if (!RotationUtils.rayCast(entity) && Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity) > rayCastDist && !entity.canEntityBeSeen(Minecraft.getMinecraft().thePlayer)) return false;
        if (entity instanceof EntityArmorStand)   return false;
        if (!invisible && entity.isInvisible()) return false;
        if (!dead && (entity.isDead || entity.getHealth() <= 0)) return false;
        if (teams && entity != null && entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) entity;
            if (isOnSameTeam(player)) return false; 
        }
        return (entity != null) && entity != Minecraft.getMinecraft().thePlayer
                && (entity instanceof EntityPlayer && players || entity instanceof EntityAnimal && animals
                || entity instanceof EntityMob && monsters || entity instanceof EntityVillager && animals)

                && entity.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) <= range
                && !entity.getDisplayName().getFormattedText().toLowerCase().contains("[npc]")
                && !AntiBot.bots.contains(entity);
    }

    public static int getHealthColor(final EntityLivingBase player) {
        final float f = player.getHealth();
        final float f2 = player.getMaxHealth();
        final float f3 = Math.max(0.0f, Math.min(f, f2) / f2);
        return Color.HSBtoRGB(f3 / 3.0f, 1.0f, 0.75f) | 0xFF000000;
    }

    public static ArrayList<EntityPlayer> getPlayersInDistanceForPlayerList(int distance, int nameLengthLimit, int arrayLengthLimit) {
        ArrayList<EntityPlayer> list = new ArrayList<>();
        for(Entity e : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if(e instanceof EntityPlayer) {
                if(Minecraft.getMinecraft().thePlayer.getDistanceToEntity(e) < 5 && e.getName().length() <= nameLengthLimit && !e.getName().equalsIgnoreCase(Minecraft.getMinecraft().thePlayer.getName())) {
                   list.add((EntityPlayer) e);
                   if(list.size() > arrayLengthLimit) {
                       list.remove(e);
                   }
                }
            }
        }
        return list;
    }

    public static void swapBackToItem() {
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, 9, Minecraft.getMinecraft().thePlayer.inventory.currentItem, 2, Minecraft.getMinecraft().thePlayer);
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, Minecraft.getMinecraft().thePlayer.inventory.currentItem, 9, 2, Minecraft.getMinecraft().thePlayer);
    }

    public static void swapToItem() {
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, Minecraft.getMinecraft().thePlayer.inventory.currentItem, 9, 2, Minecraft.getMinecraft().thePlayer);
        Minecraft.getMinecraft().playerController.windowClick(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId, 9, Minecraft.getMinecraft().thePlayer.inventory.currentItem, 2, Minecraft.getMinecraft().thePlayer);
    }

    public static boolean isOnSameTeam(EntityPlayer entity) {
        if (!(entity.getTeam() != null && Minecraft.getMinecraft().thePlayer.getTeam() != null))
            return false;
        return entity.getDisplayName().getFormattedText().charAt(1) == Minecraft.getMinecraft().thePlayer.getDisplayName().getFormattedText().charAt(1);
    }

    public static boolean isHoldingSword() {
        if (Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null && Minecraft.getMinecraft().thePlayer.getCurrentEquippedItem() != null && Minecraft.getMinecraft().thePlayer.inventory.getCurrentItem().getItem() instanceof ItemSword) {
            return true;
        }
        return false;
    }

    public static boolean isBad(final ItemStack item) {
        return !(item.getItem() instanceof ItemArmor || item.getItem() instanceof ItemTool || item.getItem() instanceof ItemBlock || item.getItem() instanceof ItemSword || item.getItem() instanceof ItemEnderPearl || item.getItem() instanceof ItemFood || (item.getItem() instanceof ItemPotion && !isBadPotion(item))) && !item.getDisplayName().toLowerCase().contains(EnumChatFormatting.GRAY + "(right click)");
    }

    public static boolean isBadPotion(final ItemStack stack) {
        if (stack != null && stack.getItem() instanceof ItemPotion) {
            final ItemPotion potion = (ItemPotion) stack.getItem();
            if (ItemPotion.isSplash(stack.getItemDamage())) {
                for (final Object o : potion.getEffects(stack)) {
                    final PotionEffect effect = (PotionEffect) o;
                    if (effect.getPotionID() == Potion.poison.getId() || effect.getPotionID() == Potion.harm.getId() || effect.getPotionID() == Potion.moveSlowdown.getId() || effect.getPotionID() == Potion.weakness.getId()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isOnLiquid() {
        Minecraft mc = Minecraft.getMinecraft();
        AxisAlignedBB boundingBox = mc.thePlayer.getEntityBoundingBox();
        if (boundingBox == null) {
            return false;
        }
        boundingBox = boundingBox.contract(0.01D, 0.0D, 0.01D).offset(0.0D, -0.01D, 0.0D);
        boolean onLiquid = false;
        int y = (int) boundingBox.minY;
        for (int x = MathHelper.floor_double(boundingBox.minX); x < MathHelper
                .floor_double(boundingBox.maxX + 1.0D); x++) {
            for (int z = MathHelper.floor_double(boundingBox.minZ); z < MathHelper
                    .floor_double(boundingBox.maxZ + 1.0D); z++) {
                Block block = mc.theWorld.getBlockState((new BlockPos(x, y, z))).getBlock();
                if (block != Blocks.air) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    onLiquid = true;
                }
            }
        }
        return onLiquid;
    }


    public static boolean isInLiquid() {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.thePlayer == null) {
            return false;
        }
        for (int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; x++) {
            for (int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; z++) {
                BlockPos pos = new BlockPos(x, (int) mc.thePlayer.getEntityBoundingBox().minY, z);
                Block block = mc.theWorld.getBlockState(pos).getBlock();
                if ((block != null) && (!(block instanceof BlockAir))) {
                    return block instanceof BlockLiquid;
                }
            }
        }
        return false;
    }


    public static String getBPSOldUselessBye() {
        final double xDiff = Minecraft.getMinecraft().thePlayer.posX - Minecraft.getMinecraft().thePlayer.prevPosX;
        final double zDiff = Minecraft.getMinecraft().thePlayer.posZ - Minecraft.getMinecraft().thePlayer.prevPosZ;
        final float minecraftTIckRate = (Minecraft.getMinecraft().timer.ticksPerSecond / 1000.0f);
        final DecimalFormat formatter = new DecimalFormat("#.#");
        String bps = (formatter.format(MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff) / minecraftTIckRate / 2 - 1));
        if(Minecraft.getMinecraft().thePlayer == null) {
            return "0";
        } else {
            return bps.equalsIgnoreCase("-1") ? "0" : bps;
        }
    }

    public static double getDistTraveled(ArrayList<Double> distances) {
        double total = 0.0;
        for (final double d : distances) {
            total += d;
        }
        return total * Minecraft.getMinecraft().timer.timerSpeed;
    }

    public static int getPlayerPing() {
        int ping = 0;
        if(Minecraft.getMinecraft().thePlayer != null) {
            ping = new NetworkPlayerInfo(Minecraft.getMinecraft().thePlayer.getGameProfile()).getResponseTime();
        }
        return ping != 0 ? ping : 0;
    }

    public static void tellUser(String message) {
        if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null
                && Minecraft.getMinecraft().theWorld != null) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.WHITE
                    + "[" + EnumChatFormatting.RED + Eris.getInstance().getClientName() + EnumChatFormatting.WHITE + "]" + message));
        } else {
            System.out.println("[" + Eris.getInstance().getClientName() + "]>>" + message);
        }
    }

    public static void tellUserUnformatted(String message) {
        if (Minecraft.getMinecraft() != null && Minecraft.getMinecraft().thePlayer != null
                && Minecraft.getMinecraft().theWorld != null) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(message));
        } else {
            System.out.println("[" + Eris.getInstance().getClientName() + "]>>" + message);
        }
    }
}
