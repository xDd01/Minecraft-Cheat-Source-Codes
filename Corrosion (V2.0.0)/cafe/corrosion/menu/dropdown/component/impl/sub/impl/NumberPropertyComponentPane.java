/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.dropdown.component.impl.sub.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.dropdown.component.impl.sub.PropertyComponentPane;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.GuiUtils;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.client.gui.Gui;

public class NumberPropertyComponentPane
extends PropertyComponentPane<NumberProperty> {
    private static final int GUI_COLOR = new Color(20, 20, 20).getRGB();
    private static final int SLIDER_COLOR = new Color(88, 2, 2).getRGB();
    private static final int SLIDER_BACKGROUND_COLOR = new Color(33, 33, 33).getRGB();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.#");
    private boolean selected;

    public NumberPropertyComponentPane(NumberProperty object) {
        super(object, 0, 0, 110, 25);
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Gui.drawRect(this.posX, this.posY, this.posX + this.expandX, this.posY + this.expandY, GUI_COLOR);
        int startPos = this.posX + 10;
        int endPos = this.posX + 100;
        double value = ((Number)((NumberProperty)this.property).getValue()).doubleValue();
        double min = ((NumberProperty)this.property).getMin().doubleValue();
        double max = ((NumberProperty)this.property).getMax().doubleValue();
        int length = (int)((value - min) / (max - min) * (double)(endPos - startPos));
        int selectedLength = startPos + length;
        if (mouseX < startPos - 10 || mouseX > selectedLength + 10 || mouseY < this.posY + 5 || mouseY > this.posY + 25) {
            this.selected = false;
        }
        if (this.selected) {
            double increment = ((NumberProperty)this.property).getIncrement().doubleValue();
            ((NumberProperty)this.property).setValue((double)Math.round(this.handleMouseOffset(mouseX, endPos, startPos, max, min) / increment) * increment);
        }
        RenderUtil.drawRoundedRect(startPos, this.posY + 14, endPos, this.posY + 17, SLIDER_BACKGROUND_COLOR);
        RenderUtil.drawRoundedRect(startPos, this.posY + 14, selectedLength, this.posY + 17, SLIDER_COLOR);
        RenderUtil.drawCircle(selectedLength, this.posY + 15, 5.0f, SLIDER_COLOR);
        TTFFontRenderer renderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 16.0f);
        this.drawCenteredString(renderer, ((NumberProperty)this.property).getName() + " - " + DECIMAL_FORMAT.format(((NumberProperty)this.property).getValue()), Color.WHITE.getRGB(), -10);
    }

    public double handleMouseOffset(int mouseX, int end, double start, double max, double min) {
        double diff = (double)end - start;
        double length = end - mouseX;
        if (length < 0.0) {
            length = 0.0;
        }
        if (length > diff) {
            length = diff;
        }
        return max - (diff * min - length * min + length * max) / diff + min;
    }

    @Override
    public void onClickReleased(int mouseX, int mouseY, int mouseButton) {
        this.handleClick(mouseX, mouseY, mouseButton, false);
    }

    @Override
    public void onClickBegin(int mouseX, int mouseY, int mouseButton) {
        this.handleClick(mouseX, mouseY, mouseButton, true);
    }

    public void handleClick(int mouseX, int mouseY, int mouseButton, boolean begin) {
        double max;
        double min;
        int startPos = this.posX + 10;
        int endPos = this.posX + 100;
        double value = ((Number)((NumberProperty)this.property).getValue()).doubleValue();
        int length = (int)((value - (min = ((NumberProperty)this.property).getMin().doubleValue())) / ((max = ((NumberProperty)this.property).getMax().doubleValue()) - min) * (double)(endPos - startPos));
        int selectedLength = startPos + length;
        if (GuiUtils.isHoveringPos(mouseX, mouseY, selectedLength - 5, this.posY + 10, selectedLength + 5, this.posY + 20)) {
            this.selected = begin;
        }
    }
}

