/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.component.tabgui;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.module.Module;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;

public class TabModuleComponent {
    private static final int BACKGROUND = new Color(20, 20, 20, 200).getRGB();
    private static final int GRAY = new Color(105, 105, 105).getRGB();
    private static final int WHITE = Color.WHITE.getRGB();
    private final Module.Category category;
    private boolean selected;
    private boolean expanded;
    private Module[] modules;
    private int selectedIndex;

    public TabModuleComponent(Module.Category category) {
        this.category = category;
    }

    public void onPostLoad() {
        this.modules = Corrosion.INSTANCE.getModuleManager().getIf(module -> module.getAttributes().category() == this.category).toArray(new Module[0]);
    }

    public void renderExpansion(int posX, int posY, int selectedColor) {
        int maxY = this.modules.length * 20 + posY;
        int maxX = posX + 85;
        TTFFontRenderer renderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 19.0f);
        RenderUtil.drawRoundedRect(posX, posY, maxX, maxY, BACKGROUND);
        for (int i2 = 0; i2 < this.modules.length; ++i2) {
            Module module = this.modules[i2];
            if (this.selectedIndex == i2) {
                RenderUtil.drawRoundedRect(posX, posY + i2 * 20, posX + 85, posY + 17 + i2 * 20, selectedColor, selectedColor);
            }
            renderer.drawStringWithShadow(module.getAttributes().name(), posX + 5, posY + 5 + i2 * 20, module.isEnabled() || this.selectedIndex == i2 ? WHITE : GRAY);
        }
    }

    public void onKeyPress(int pressedKey) {
        int length = this.modules.length;
        switch (pressedKey) {
            case 208: {
                if (++this.selectedIndex != length) break;
                this.selectedIndex = 0;
                break;
            }
            case 200: {
                if (--this.selectedIndex != -1) break;
                this.selectedIndex = length - 1;
                break;
            }
            case 28: {
                if (this.selectedIndex >= this.modules.length) {
                    this.selectedIndex = 0;
                }
                if (!this.expanded) break;
                this.modules[this.selectedIndex].toggle();
            }
        }
    }

    public Module.Category getCategory() {
        return this.category;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public boolean isExpanded() {
        return this.expanded;
    }

    public Module[] getModules() {
        return this.modules;
    }

    public int getSelectedIndex() {
        return this.selectedIndex;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}

