/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.dropdown.component.impl.sub.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.dropdown.component.impl.sub.PropertyComponentPane;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.GuiUtils;
import java.awt.Color;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class BooleanPropertyComponentPane
extends PropertyComponentPane<BooleanProperty> {
    private static final TTFFontRenderer RENDERER = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 16.0f);
    private static final ResourceLocation CHECKED = new ResourceLocation("meme/settings/checked.png");
    private static final ResourceLocation UNCHECKED = new ResourceLocation("meme/settings/unchecked.png");
    private static final int ENABLED_TEXT_COLOR = Color.WHITE.getRGB();
    private static final int DISABLED_TEXT_COLOR = new Color(77, 77, 77).getRGB();
    private static final int GUI_COLOR = new Color(20, 20, 20).getRGB();
    private static final int BOX_COLOR = new Color(33, 33, 33, 200).getRGB();

    public BooleanPropertyComponentPane(BooleanProperty booleanProperty) {
        super(booleanProperty, 0, 0, 110, 20);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Gui.drawRect(this.posX, this.posY, this.posX + this.expandX, this.posY + this.expandY, GUI_COLOR);
        GuiUtils.drawImage((Boolean)((BooleanProperty)this.property).getValue() != false ? CHECKED : UNCHECKED, this.posX + 4, this.posY + 2, 13.0f, 13.0f, (Boolean)((BooleanProperty)this.property).getValue() != false ? -1 : BOX_COLOR);
        RENDERER.drawString(((BooleanProperty)this.property).getName(), this.posX + 20, this.posY + 5, (Boolean)((BooleanProperty)this.property).getValue() != false ? ENABLED_TEXT_COLOR : DISABLED_TEXT_COLOR);
    }

    @Override
    public void onClickReleased(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtils.isHoveringPos(mouseX, mouseY, this.posX + 4, this.posY + 2, this.posX + 17, this.posY + 17)) {
            ((BooleanProperty)this.property).setValue((Boolean)((BooleanProperty)this.property).getValue() == false);
        }
    }
}

