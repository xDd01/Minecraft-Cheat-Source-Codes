package me.spec.eris.api.friend;

import net.minecraft.entity.player.EntityPlayer;

public class Friend {

    private String friendName;
    private EntityPlayer entityPlayer;

    public Friend(String friendName, EntityPlayer entityPlayer) {
        this.friendName = friendName;
        this.entityPlayer = entityPlayer;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public EntityPlayer getEntityPlayer() {
        return entityPlayer;
    }

    public void setEntityPlayer(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }
}
