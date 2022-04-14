/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButton
extends Gui {
    protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    protected int width = 200;
    public int height = 20;
    public int xPosition;
    public int yPosition;
    public String displayString;
    public int id;
    public boolean enabled = true;
    public boolean visible = true;
    protected boolean hovered;
    private final int hoveredColor;
    private final int notHoveredColor;
    private final int fontColor;

    public GuiButton(int buttonId, int x2, int y2, String buttonText) {
        this(buttonId, x2, y2, 200, 20, buttonText);
    }

    public GuiButton(int buttonId, int x2, int y2, int widthIn, int heightIn, String buttonText, Color unhovered, Color hovered, Color fontColor) {
        this.id = buttonId;
        this.xPosition = x2;
        this.yPosition = y2;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
        this.notHoveredColor = unhovered.getRGB();
        this.hoveredColor = hovered.getRGB();
        this.fontColor = fontColor.getRGB();
    }

    public GuiButton(int buttonId, int x2, int y2, int widthIn, int heightIn, String buttonText) {
        this(buttonId, x2, y2, widthIn, heightIn, buttonText, new Color(20, 20, 20), new Color(10, 10, 10, 75), new Color(184, 184, 184));
    }

    public GuiButton(int buttonId, int x2, int y2, int widthIn, int heightIn, String buttonText, Color fontText) {
        this(buttonId, x2, y2, widthIn, heightIn, buttonText, new Color(20, 20, 20), new Color(10, 10, 10, 75), fontText);
    }

    protected int getHoverState(boolean mouseOver) {
        int i2 = 1;
        if (!this.enabled) {
            i2 = 0;
        } else if (mouseOver) {
            i2 = 2;
        }
        return i2;
    }

    public void drawButton(Minecraft mc2, int mouseX, int mouseY) {
        if (this.visible) {
            boolean mouseHovered;
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            boolean hoveredState = this.hovered;
            this.hovered = mouseHovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i2 = this.getHoverState(this.hovered);
            int modifier = 0;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            int minX = this.xPosition - modifier;
            int minY = this.yPosition - modifier;
            int maxX = minX + this.width;
            int maxY = minY + this.height + (int)((double)modifier * 1.5);
            int colorModifier = this.enabled ? 0 : 40;
            int renderColor = i2 == 2 ? this.hoveredColor : this.notHoveredColor;
            RenderUtil.drawRoundedRect(minX, minY, maxX, maxY, renderColor, renderColor);
            Color color = new Color(this.fontColor);
            Color fixed = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() - colorModifier);
            this.mouseDragged(mc2, mouseX, mouseY);
            this.drawCenteredString(this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, fixed.getRGB());
        }
    }

    public void drawCenteredString(String text, int x2, int y2, int color) {
        TTFFontRenderer renderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 18.0f);
        renderer.drawString(text, (float)x2 - renderer.getWidth(text) / 2.0f, (float)y2 - 1.0f, color);
    }

    protected void mouseDragged(Minecraft mc2, int mouseX, int mouseY) {
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public boolean mousePressed(Minecraft mc2, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void drawBorder(int borderColor) {
        RenderUtil.drawRect(this.xPosition - 1, this.yPosition + this.height + 1, this.xPosition, this.yPosition - 1, borderColor);
        RenderUtil.drawRect(this.xPosition, this.yPosition, this.xPosition + this.getButtonWidth(), this.yPosition - 1, borderColor);
        RenderUtil.drawRect(this.xPosition, this.yPosition + this.height, this.xPosition + this.getButtonWidth(), this.yPosition + this.height + 1, borderColor);
        RenderUtil.drawRect(this.xPosition + this.getButtonWidth(), this.yPosition + this.height + 1, this.xPosition + this.getButtonWidth() + 1, this.yPosition - 1, borderColor);
    }
}

