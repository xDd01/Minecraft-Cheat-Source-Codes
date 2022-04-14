package io.github.nevalackin.client.impl.module.misc.world;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.event.game.GetTimerSpeedEvent;
import io.github.nevalackin.client.impl.property.DoubleProperty;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;

public final class Timer extends Module {

    private final DoubleProperty timerSpeedProperty = new DoubleProperty("Timer Speed", 1.3, 0.1, 3.0, 0.1);

    public Timer() {
        super("Timer", Category.MISC, Category.SubCategory.MISC_WORLD);
        this.setSuffix(this.timerSpeedProperty::getDisplayString);
        this.register(this.timerSpeedProperty);
    }

    @EventLink
    private final Listener<GetTimerSpeedEvent> onGetTimerSpeed = event -> {
        event.setTimerSpeed(this.timerSpeedProperty.getValue());
    };

    @Override
    public void onEnable() {
    }

    @Override
    public void onDisable() {

    }
}
