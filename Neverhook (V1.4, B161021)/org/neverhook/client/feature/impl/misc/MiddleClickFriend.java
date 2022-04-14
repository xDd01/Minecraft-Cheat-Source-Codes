package org.neverhook.client.feature.impl.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.EntityLivingBase;
import org.neverhook.client.NeverHook;
import org.neverhook.client.event.EventTarget;
import org.neverhook.client.event.events.impl.input.EventMouse;
import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.friend.Friend;
import org.neverhook.client.helpers.misc.ChatHelper;
import org.neverhook.client.ui.notification.NotificationManager;
import org.neverhook.client.ui.notification.NotificationType;

public class MiddleClickFriend extends Feature {

    public MiddleClickFriend() {
        super("MiddleClickFriend", "Добавляет игрока в френд лист при нажатии на кнопку мыши", Type.Misc);
    }

    @EventTarget
    public void onMouseEvent(EventMouse event) {
        if (event.getKey() == 2 && mc.pointedEntity instanceof EntityLivingBase) {
            if (NeverHook.instance.friendManager.getFriends().stream().anyMatch(friend -> friend.getName().equals(mc.pointedEntity.getName()))) {
                NeverHook.instance.friendManager.getFriends().remove(NeverHook.instance.friendManager.getFriend(mc.pointedEntity.getName()));
                ChatHelper.addChatMessage(ChatFormatting.RED + "Removed " + ChatFormatting.RESET + "'" + mc.pointedEntity.getName() + "'" + " as Friend!");
                NotificationManager.publicity("MCF", "Removed " + "'" + mc.pointedEntity.getName() + "'" + " as Friend!", 4, NotificationType.INFO);
            } else {
                NeverHook.instance.friendManager.addFriend(new Friend(mc.pointedEntity.getName()));
                ChatHelper.addChatMessage(ChatFormatting.GREEN + "Added " + ChatFormatting.RESET + mc.pointedEntity.getName() + " as Friend!");
                NotificationManager.publicity("MCF", "Added " + mc.pointedEntity.getName() + " as Friend!", 4, NotificationType.SUCCESS);
            }
        }
    }
}