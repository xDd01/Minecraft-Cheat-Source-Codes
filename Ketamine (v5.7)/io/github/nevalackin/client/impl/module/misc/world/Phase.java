package io.github.nevalackin.client.impl.module.misc.world;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;

public class Phase extends Module {

    public Phase() {
        super("Phase", Category.MISC, Category.SubCategory.MISC_WORLD);
    }

    @Override
    public void onEnable() {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 5 - Math.random(), mc.thePlayer.posZ);
        setEnabled(false);
    }

    @Override
    public void onDisable() {

    }
}
