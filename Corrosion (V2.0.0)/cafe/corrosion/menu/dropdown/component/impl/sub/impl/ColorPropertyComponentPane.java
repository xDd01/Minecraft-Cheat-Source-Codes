/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.dropdown.component.impl.sub.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.dropdown.component.impl.sub.PropertyComponentPane;
import cafe.corrosion.property.type.ColorProperty;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.GuiUtils;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.Gui;

public class ColorPropertyComponentPane
extends PropertyComponentPane<ColorProperty> {
    private static final int GUI_COLOR = new Color(20, 20, 20).getRGB();
    private static final Color TRANSLUCENT = new Color(0, 0, 0, 0);
    private static final Color BLACK = Color.BLACK;
    private static final Color[] COLORS = new Color[]{Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA, Color.RED};
    private float lastHue;
    private float lastSaturation;
    private float lastBrightness;
    private static final TTFFontRenderer RENDERER = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 16.0f);

    public ColorPropertyComponentPane(ColorProperty property) {
        super(property, 0, 0, 110, 118);
        Color color = (Color)property.getValue();
        float[] components = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        this.lastHue = components[0];
        this.lastSaturation = components[1];
        this.lastBrightness = components[2];
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Gui.drawRect(this.posX, this.posY, this.posX + this.expandX, this.posY + this.expandY, GUI_COLOR);
        Color color = (Color)((ColorProperty)this.property).getValue();
        float[] components = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
        Color value = Color.getHSBColor(components[0], 1.0f, 1.0f);
        int posX = this.posX + 5;
        int posY = this.posY + 30;
        int expandX = this.expandX - 10;
        int expandY = 75;
        RenderUtil.drawGradientRect(posX, posY, expandX, expandY, Color.WHITE, value);
        RenderUtil.drawGradientRectVertical(posX, posY, expandX, expandY, TRANSLUCENT, BLACK);
        this.drawHueBar(posX - 15, this.posY + 2, expandX);
        float boxY = (float)posY + (float)(posY + expandY - posY) / 2.0f - 50.0f;
        RENDERER.drawString(((ColorProperty)this.property).getName(), posX + 2, boxY, Color.WHITE.getRGB());
        RenderUtil.drawRoundedRect(posX + 60, boxY, posX + 98, boxY + 8.0f, color.getRGB());
    }

    private void drawHueBar(float x2, float y2, float width) {
        float increment = width / 6.0f;
        for (int i2 = 1; i2 < 7; ++i2) {
            RenderUtil.drawGradientRect(x2 + (increment * (float)i2 - 1.0f), y2, width / 6.0f, 10.0f, COLORS[i2 - 1], COLORS[i2]);
        }
    }

    @Override
    public void onClickReleased(int mouseX, int mouseY, int mouseButton) {
        int posX = this.posX + 5;
        int posY = this.posY + 30;
        int expandX = this.expandX - 10;
        int expandY = 75;
        if (GuiUtils.isHoveringPos(mouseX, mouseY, posX, this.posY + 2, posX + expandX, posY - 10)) {
            float relativeX = mouseX - posX;
            float hue = relativeX / 100.0f;
            ((ColorProperty)this.property).setValue(Color.getHSBColor(hue, this.lastSaturation, this.lastBrightness));
            this.lastHue = hue;
        } else if (GuiUtils.isHoveringPos(mouseX, mouseY, posX, posY, posX + expandX, posY + expandY)) {
            float relativeX = mouseX - posX;
            float relativeY = mouseY - posY;
            float brightness = 1.0f - relativeY / (float)expandY;
            float saturation = relativeX / 100.0f;
            ((ColorProperty)this.property).setValue(Color.getHSBColor(this.lastHue, saturation, brightness));
            this.lastBrightness = brightness;
            this.lastSaturation = saturation;
        }
    }
}

