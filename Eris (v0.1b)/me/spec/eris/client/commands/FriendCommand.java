package me.spec.eris.client.commands;

import me.spec.eris.Eris;
import me.spec.eris.api.command.Command;
import me.spec.eris.api.config.ClientConfig;
import me.spec.eris.api.friend.Friend;
import me.spec.eris.utils.player.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.io.IOException;

public class FriendCommand extends Command {


    public FriendCommand() {
        super("friend", "friend <add, remove, list> <name>");
    }

    @Override
    public void execute(String[] commandArguments) {
        if(commandArguments.length == 3) {
            if(commandArguments[1].equalsIgnoreCase("add")) {
            for(Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
                if(entity instanceof EntityPlayer) {
                    EntityPlayer entityPlayer = (EntityPlayer) entity;
                    if(entityPlayer.getName().equalsIgnoreCase(commandArguments[2])) {
                        Eris.getInstance().friendManager.addToManagerArraylist(new Friend(entityPlayer.getName(), entityPlayer));
                        PlayerUtils.tellUser("Added friend " + entityPlayer.getName());
                    } else {
                        PlayerUtils.tellUser("That player doesnt exist!");
                    }
                }
            }
            } else if(commandArguments[1].equalsIgnoreCase("remove")) {
              if(Eris.getInstance().friendManager.getFriendByName(commandArguments[2]) != null) {
                  Eris.getInstance().friendManager.removeFromManagerArraylist(Eris.getInstance().friendManager.getFriendByName(commandArguments[2]));
                  PlayerUtils.tellUser("Removed friend " + Eris.getInstance().friendManager.getFriendByName(commandArguments[2]).getFriendName());
              } else {
                  PlayerUtils.tellUser("That friend doesnt exist!");
              }
            }
        } else if(commandArguments.length == 2) {
            if(commandArguments[1].equalsIgnoreCase("list")) {
                Eris.getInstance().friendManager.getManagerArraylist().forEach(friend -> PlayerUtils.tellUser("Friend: " + friend.getFriendName()));
            }
        } else {
            PlayerUtils.tellUser("Invalid command arguments!");
        }
    }
}
