package io.github.nevalackin.client.impl.module.misc.player;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.event.player.ServerSetPosLookEvent;
import io.github.nevalackin.client.impl.event.player.UpdatePositionEvent;
import io.github.nevalackin.client.util.player.RotationUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.util.ChatComponentText;
import org.apache.commons.lang3.RandomUtils;

public final class NoRotate extends Module {

    public NoRotate() {
        super("No Rotate", Category.MISC, Category.SubCategory.MISC_PLAYER);
    }

    @EventLink
    private final Listener<ServerSetPosLookEvent> onSetPosLook = event -> {
        // Set the player yaw and pitch
        event.setYaw(this.mc.thePlayer.rotationYaw);
        event.setPitch(this.mc.thePlayer.rotationPitch);
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}