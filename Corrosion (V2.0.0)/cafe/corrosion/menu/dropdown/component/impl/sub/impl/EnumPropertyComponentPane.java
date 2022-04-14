/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.dropdown.component.impl.sub.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.dropdown.component.impl.sub.PropertyComponentPane;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.nameable.INameable;
import cafe.corrosion.util.render.GuiUtils;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.Gui;

public class EnumPropertyComponentPane<E extends INameable>
extends PropertyComponentPane<EnumProperty<E>> {
    public EnumPropertyComponentPane(EnumProperty<E> object) {
        super(object, 0, 0, 110, 20);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        int guiColor = new Color(15, 15, 15).getRGB();
        Gui.drawRect(this.posX, this.posY, this.posX + this.expandX, this.posY + this.expandY, guiColor);
        TTFFontRenderer renderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 16.0f);
        String name = ((EnumProperty)this.property).getName();
        String value = ((INameable)((EnumProperty)this.property).getValue()).getName();
        float posY = (float)(this.posY + this.expandY) - (float)this.expandY / 2.0f - Math.max(renderer.getHeight(name), renderer.getHeight(value)) / 2.0f;
        float posX = (float)(this.posX + this.expandX) - renderer.getWidth(value) - 3.0f;
        renderer.drawString(name, this.posX + 3, posY, Color.WHITE.getRGB());
        renderer.drawString(value, posX, posY, Color.WHITE.getRGB());
    }

    @Override
    public void onClickReleased(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtils.isHoveringPos(mouseX, mouseY, this.posX, this.posY, this.posX + this.expandX, this.posY + this.expandY)) {
            List<INameable> values = Arrays.asList(((EnumProperty)this.property).getValues());
            INameable value = (INameable)((EnumProperty)this.property).getValue();
            int size = values.size();
            int index = values.indexOf(value);
            if (mouseButton == 0) {
                ((EnumProperty)this.property).setValue(index <= size - 2 ? values.get(index + 1) : values.get(0));
            } else {
                ((EnumProperty)this.property).setValue(index >= 1 ? values.get(index - 1) : values.get(size - 1));
            }
        }
    }
}

