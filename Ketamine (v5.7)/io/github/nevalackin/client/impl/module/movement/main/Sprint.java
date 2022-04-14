package io.github.nevalackin.client.impl.module.movement.main;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.event.player.SendSprintStateEvent;
import io.github.nevalackin.client.impl.property.BooleanProperty;
import io.github.nevalackin.client.util.movement.MovementUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;

public final class Sprint extends Module {

    private final BooleanProperty omniProperty = new BooleanProperty("Multi-Directional", true);

    public Sprint() {
        super("Sprint", Category.MOVEMENT, Category.SubCategory.MOVEMENT_MAIN);
        register(omniProperty);
    }

    @EventLink
    private final Listener<SendSprintStateEvent> onSendSprint = event -> {
        if (MovementUtil.canSprint(mc.thePlayer, omniProperty.getValue())) {
            event.setSprintState(true);
            mc.thePlayer.setSprinting(true);
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
