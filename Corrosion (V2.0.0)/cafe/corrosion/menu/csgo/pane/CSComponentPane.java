/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.csgo.pane;

public abstract class CSComponentPane {
    protected int posX;
    protected int posY;
    protected int expandX;
    protected int expandY;

    public void move(int offsetX, int offsetY) {
        this.posX += offsetX;
        this.posY += offsetY;
    }

    public abstract void draw(int var1, int var2);

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
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

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setExpandX(int expandX) {
        this.expandX = expandX;
    }

    public void setExpandY(int expandY) {
        this.expandY = expandY;
    }
}

