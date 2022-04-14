package io.github.nevalackin.client.impl.module.misc.inventory;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.event.player.SendSprintStateEvent;
import io.github.nevalackin.client.impl.property.BooleanProperty;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;

public final class Inventory extends Module {

    private final BooleanProperty moveInsideGUIProperty = new BooleanProperty("Move Inside GUIs", true);
    // TODO :: More Inventory settings

    public Inventory() {
        super("Inventory+", Category.MISC, Category.SubCategory.MISC_INVENTORY);

        this.register(this.moveInsideGUIProperty);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
