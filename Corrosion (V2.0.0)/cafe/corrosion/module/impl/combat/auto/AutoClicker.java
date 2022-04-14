/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.combat.auto;

import cafe.corrosion.event.impl.EventUpdate;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.combat.ClickRandomizer;
import org.lwjgl.input.Mouse;

@ModuleAttributes(name="Auto Clicker", description="Automatically clicks for you while your LMB is held", category=Module.Category.COMBAT)
public class AutoClicker
extends Module {
    private final NumberProperty minCps = new NumberProperty(this, "Min CPS", 12, 0, 20, 1);
    private final NumberProperty maxCps = new NumberProperty(this, "Max CPS", 14, 1, 20, 1);
    private final NumberProperty randomizationLevel = new NumberProperty(this, "Randomization Level", 1, 0, 5, 1);
    private final BooleanProperty useRandomization = new BooleanProperty((Module)this, "Use Randomization", true);
    private final ClickRandomizer randomizer = new ClickRandomizer(this.minCps, this.maxCps, this.randomizationLevel, this.useRandomization);

    public AutoClicker() {
        this.registerEventHandler(EventUpdate.class, event -> {
            if (event.isPre() && AutoClicker.mc.currentScreen == null && Mouse.isButtonDown(0) && this.randomizer.hasElapsed()) {
                mc.clickMouse();
            }
        });
    }

    @Override
    public String getMode() {
        if (!((Boolean)this.useRandomization.getValue()).booleanValue()) {
            return Math.max(((Number)this.minCps.getValue()).intValue(), ((Number)this.maxCps.getValue()).intValue()) + " CPS";
        }
        return this.minCps.getValue() + "-" + this.maxCps.getValue() + " CPS";
    }
}

