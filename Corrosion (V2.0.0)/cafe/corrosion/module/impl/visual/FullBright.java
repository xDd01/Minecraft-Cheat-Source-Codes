/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.visual;

import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;

@ModuleAttributes(name="FullBright", category=Module.Category.VISUAL, description="Makes your game brighter", defaultModule=true)
public class FullBright
extends Module {
    private float lastBrightness;

    @Override
    public void onEnable() {
        this.lastBrightness = FullBright.mc.gameSettings.gammaSetting;
        FullBright.mc.gameSettings.gammaSetting = 999.0f;
    }

    @Override
    public void onDisable() {
        FullBright.mc.gameSettings.gammaSetting = this.lastBrightness;
    }
}

