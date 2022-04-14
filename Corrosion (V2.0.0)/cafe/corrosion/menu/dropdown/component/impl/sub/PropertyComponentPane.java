/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.dropdown.component.impl.sub;

import cafe.corrosion.menu.animation.impl.CubicEaseAnimation;
import cafe.corrosion.menu.dropdown.component.GuiComponentPane;
import net.minecraft.client.Minecraft;

public abstract class PropertyComponentPane<T>
extends GuiComponentPane {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    private final CubicEaseAnimation animation = new CubicEaseAnimation(350L);
    protected final T property;

    public PropertyComponentPane(T property, int posX, int posY, int expandX, int expandY) {
        super(posX, posY, expandX, expandY);
        this.property = property;
    }

    public void reposition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int getAdditionalHeight() {
        return this.expandY;
    }

    @Override
    public void onClickReleased(int mouseX, int mouseY, int mouseButton) {
    }

    @Override
    public void onClickBegin(int mouseX, int mouseY, int mouseButton) {
    }

    public CubicEaseAnimation getAnimation() {
        return this.animation;
    }

    public T getProperty() {
        return this.property;
    }
}

