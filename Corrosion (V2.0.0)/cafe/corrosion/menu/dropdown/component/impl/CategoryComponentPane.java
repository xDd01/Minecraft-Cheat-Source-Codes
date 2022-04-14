/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.dropdown.component.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.dropdown.component.GuiComponentPane;
import cafe.corrosion.menu.dropdown.component.impl.ModuleComponentPane;
import cafe.corrosion.module.Module;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.GuiUtils;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;

public class CategoryComponentPane
extends GuiComponentPane {
    private static final int BASE_POS_X = 10;
    private static final int X_TITLE_WIDTH = 114;
    private static final int X_TITLE_EXPANDER = 130;
    private static final int X_SPACING = 10;
    private static final int BASE_POS_Y = 5;
    private static final int PANE_HEIGHT = 20;
    private static final int PER_MODULE_HEIGHT = 20;
    private static final int GUI_COLOR = new Color(33, 33, 33).getRGB();
    private static final int TOP_COLOR = new Color(88, 2, 2).getRGB();
    private static final TTFFontRenderer ROBOTO = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 22.0f);
    private final List<ModuleComponentPane> moduleComponents = new ArrayList<ModuleComponentPane>();
    private final Module.Category category;
    private boolean selected;

    public CategoryComponentPane(Module.Category category, int position) {
        super(10 + position * 130 - 130, 5, 114, 20);
        this.category = category;
        this.moduleComponents.addAll(Corrosion.INSTANCE.getModuleManager().getIf(module -> module.getAttributes().category().equals((Object)category)).stream().map(ModuleComponentPane::new).sorted(Comparator.comparingInt(a2 -> a2.getModuleAttributes().name().charAt(0))).collect(Collectors.toList()));
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        ResourceLocation texture = this.category.getTextureLocation();
        RenderUtil.drawRoundedRect(this.posX - 4, this.posY, this.posX + this.expandX, this.posY + this.expandY, GUI_COLOR);
        GuiUtils.drawStraightLine(this.posX - 4, this.posX + this.expandX, this.posY, 2, TOP_COLOR);
        GuiUtils.drawImage(texture, this.posX, this.posY + 2, 20.0f, 18.0f, -1);
        this.drawCenteredString(ROBOTO, this.category.getName(), Color.WHITE.getRGB(), 1);
        String toDraw = this.selected ? "-" : "+";
        ROBOTO.drawString(toDraw, (float)this.posX + ((float)this.expandX - ROBOTO.getWidth(toDraw) - 3.0f), (float)this.posY + (float)this.expandY / 3.5f, Color.WHITE.getRGB());
        if (this.selected) {
            int posY = this.posY + this.expandY;
            for (ModuleComponentPane component : this.moduleComponents) {
                component.reposition(this.posX, posY);
                component.draw(mouseX, mouseY);
                posY += 20 + component.getAdditionalHeight();
            }
            GuiUtils.drawStraightLine(this.posX, this.posX + this.expandX - 4, posY - 1, 3, TOP_COLOR);
        }
    }

    @Override
    public void onClickReleased(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtils.isHoveringPos(mouseX, mouseY, this.posX, this.posY, this.posX + this.expandX, this.posY + this.expandY) && mouseButton == 1) {
            this.selected = !this.selected;
            return;
        }
        if (this.selected) {
            this.moduleComponents.forEach(component -> component.onClickReleased(mouseX, mouseY, mouseButton));
        }
    }

    @Override
    public void onClickBegin(int mouseX, int mouseY, int mouseButton) {
        if (this.selected) {
            this.moduleComponents.forEach(component -> component.onClickBegin(mouseX, mouseY, mouseButton));
        }
        if (!GuiUtils.isHoveringPos(mouseX, mouseY, this.posX, this.posY, this.posX + this.expandX, this.posY + this.expandY) || mouseButton == 0) {
            // empty if block
        }
    }

    public List<ModuleComponentPane> getModuleComponents() {
        return this.moduleComponents;
    }

    public Module.Category getCategory() {
        return this.category;
    }

    public boolean isSelected() {
        return this.selected;
    }
}

