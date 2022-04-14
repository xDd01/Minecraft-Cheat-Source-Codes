/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.component.draggable;

import cafe.corrosion.menu.drag.data.HudComponentProxy;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public interface IDraggable {
    public void render(HudComponentProxy var1, ScaledResolution var2, int var3, int var4, int var5, int var6);

    default public void renderBackground(ScaledResolution scaledResolution, int posX, int posY, int expandX, int expandY, int color) {
        Gui.drawRect(posX - 1, posY - 1, posX + expandX + 1, posY + expandY + 1, color);
    }
}

