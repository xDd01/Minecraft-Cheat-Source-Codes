/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.dropdown.component;

import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.util.render.GuiUtils;

public abstract class GuiComponentPane {
    protected int posX;
    protected int posY;
    protected int expandX;
    protected int expandY;

    public GuiComponentPane(int posX, int posY, int expandX, int expandY) {
        this.posX = posX;
        this.posY = posY;
        this.expandX = expandX;
        this.expandY = expandY;
    }

    public abstract void draw(int var1, int var2);

    public void initializeComponent() {
    }

    public void onClickReleased(int mouseX, int mouseY, int mouseButton) {
    }

    public void onClickBegin(int mouseX, int mouseY, int mouseButton) {
    }

    public void onKeyPress(int pressedKey) {
    }

    public boolean mouseHovered(int mouseX, int mouseY) {
        return GuiUtils.isHoveringPos(mouseX, mouseY, this.posX, this.posY, this.posX + this.expandX, this.posY + this.expandY);
    }

    public void drawCenteredString(TTFFontRenderer renderer, String text, int color, int yOffset) {
        int centerX = this.posX + (this.posX + this.expandX - this.posX) / 2;
        int centerY = this.posY + (this.posY + this.expandY - this.posY) / 2;
        float textWidth = renderer.getWidth(text);
        float textHeight = renderer.getHeight(text);
        renderer.drawString(text, centerX - (int)(textWidth / 2.0f), (float)centerY - textHeight / 2.0f + (float)yOffset, color);
    }

    public int getPosX() {
        return this.posX;
    }

    public int getPosY() {
        return this.posY;
    }

    public int getExpandX() {
        return this.expandX;
    }

    public int getExpandY() {
        return this.expandY;
    }
}

