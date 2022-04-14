package me.spec.eris.client.managers;

import me.spec.eris.Eris;
import me.spec.eris.api.friend.Friend;
import me.spec.eris.api.manager.Manager;
import net.minecraft.entity.EntityLivingBase;

public class FriendManager extends Manager<Friend> {

    public Friend getFriendByName(String friendName) {
        return getManagerArraylist().stream().filter(friend -> friend.getFriendName().equalsIgnoreCase(friendName)).findFirst().orElse(null);
    }

    public boolean isFriend(EntityLivingBase elb) {
        return Eris.getInstance().friendManager.getManagerArraylist().stream().anyMatch(friend -> friend.getFriendName().equalsIgnoreCase(elb.getName()));
    }
}
