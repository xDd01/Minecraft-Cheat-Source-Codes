/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.csgo.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.csgo.pane.CSComponentPane;
import cafe.corrosion.module.Module;
import cafe.corrosion.util.font.type.FontType;
import java.awt.Color;

public class CSCategoryComponentPane
extends CSComponentPane {
    private final Module.Category category;

    public CSCategoryComponentPane(Module.Category category, int identifier) {
        this.category = category;
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        TTFFontRenderer renderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 16.0f);
        renderer.drawString(this.category.getName(), this.posX, this.posY, Color.WHITE.getRGB());
    }
}

