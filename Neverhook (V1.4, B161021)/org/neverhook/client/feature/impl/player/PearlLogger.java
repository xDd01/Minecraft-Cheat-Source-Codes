package org.neverhook.client.feature.impl.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import org.neverhook.client.NeverHook;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.player.EventUpdate;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.helpers.misc.ChatHelper;

public class PearlLogger extends Feature {

    private boolean canSend;

    public PearlLogger() {
        super("PearlLogger", "Показывает координаты эндер-перла игроков", Type.Player);
    }

    @Override
    public void onEnable() {
        canSend = true;
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (mc.world == null || mc.player == null)
            return;
        Entity enderPearl = null;
        for (Entity e : mc.world.loadedEntityList) {
            if (e instanceof EntityEnderPearl) {
                enderPearl = e;
                break;
            }
        }
        if (enderPearl == null) {
            canSend = true;
            return;
        }
        EntityPlayer throwerEntity = null;
        for (EntityPlayer entity : mc.world.playerEntities) {
            if (throwerEntity != null) {
                if (throwerEntity.getDistanceToEntity(enderPearl) <= entity.getDistanceToEntity(enderPearl)) {
                    continue;
                }
            }
            throwerEntity = entity;
        }
        String facing = enderPearl.getHorizontalFacing().toString();
        if (facing.equals("west")) {
            facing = "east";
        } else if (facing.equals("east")) {
            facing = "west";
        }
        if (throwerEntity == mc.player) {
            return;
        }
        String pos = ChatFormatting.GOLD + facing + ChatFormatting.WHITE + " | " + ChatFormatting.LIGHT_PURPLE + enderPearl.getPosition().getX() + " " + enderPearl.getPosition().getY() + " " + enderPearl.getPosition().getZ();
        if (throwerEntity != null && canSend) {
            ChatHelper.addChatMessage(NeverHook.instance.friendManager.isFriend(throwerEntity.getName()) ? ChatFormatting.GREEN + throwerEntity.getName() + ChatFormatting.WHITE + " thrown pearl on " + pos : ChatFormatting.RED + throwerEntity.getName() + ChatFormatting.WHITE + " thrown pearl on " + pos);
            canSend = false;
        }
    }
}
