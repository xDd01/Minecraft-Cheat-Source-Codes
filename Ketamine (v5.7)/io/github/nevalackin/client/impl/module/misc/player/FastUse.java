package io.github.nevalackin.client.impl.module.misc.player;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.event.player.UpdatePositionEvent;
import io.github.nevalackin.client.impl.property.DoubleProperty;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.client.C03PacketPlayer;

public final class FastUse extends Module {

    private final DoubleProperty delayProperty = new DoubleProperty("Delay", 16, 1, 33, 1);

    public FastUse() {
        super("Fast Use", Category.MISC, Category.SubCategory.MISC_PLAYER);

        this.delayProperty.addValueAlias(1, "Instant");

        this.register(this.delayProperty);

        this.setSuffix(this.delayProperty::getDisplayString);
    }

    @EventLink
    private final Listener<UpdatePositionEvent> onUpdate = event -> {
        if (event.isPre() && event.isOnGround() && this.mc.thePlayer.getItemInUseDuration() == this.delayProperty.getValue()) {
            for (int i = 0; i < 33 - this.delayProperty.getValue(); i++) {
                this.mc.thePlayer.sendQueue.sendPacket(new C03PacketPlayer(true));
            }
        }
    };

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {

    }
}
