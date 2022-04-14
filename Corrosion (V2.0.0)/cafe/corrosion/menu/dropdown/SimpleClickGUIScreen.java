/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.dropdown;

import cafe.corrosion.menu.animation.Animation;
import cafe.corrosion.menu.dropdown.component.impl.CategoryComponentPane;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.impl.visual.GUI;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.EnumProperty;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

public class SimpleClickGUIScreen
extends GuiScreen {
    private final List<CategoryComponentPane> menuComponents = new ArrayList<CategoryComponentPane>();
    private final EnumProperty<GUI.AnimationStyle> animationStyle;
    private final BooleanProperty enabled;
    private Animation animation;

    public SimpleClickGUIScreen(EnumProperty<GUI.AnimationStyle> animationStyle, BooleanProperty enabled) {
        for (int i2 = 0; i2 < Module.Category.values().length; ++i2) {
            Module.Category category = Module.Category.values()[i2];
            int position = i2 + 1;
            this.menuComponents.add(new CategoryComponentPane(category, position));
        }
        this.animationStyle = animationStyle;
        this.enabled = enabled;
    }

    @Override
    public void initGui() {
        this.animation = ((GUI.AnimationStyle)this.animationStyle.getValue()).getAnimationSupplier().get();
        if (((Boolean)this.enabled.getValue()).booleanValue()) {
            this.animation.start(false);
        }
    }

    @Override
    public void drawScreen(int posX, int posY, float partialTicks) {
        float progress = 1.0f;
        float transformX = 0.0f;
        float transformY = 0.0f;
        if (this.animation.isAnimating()) {
            progress = (float)this.animation.calculate();
            GL11.glScalef(progress, progress, 1.0f);
            transformX = (float)this.mc.displayWidth * (1.0f - progress) / 2.0f;
            transformY = (float)this.mc.displayHeight * (1.0f - progress) / 2.0f;
            GL11.glTranslatef(transformX, transformY, 0.0f);
        }
        this.menuComponents.forEach(component -> component.draw(posX, posY));
        GL11.glScalef(1.0f / progress, 1.0f / progress, 1.0f);
        GL11.glTranslatef(-transformX, -transformY, 0.0f);
        super.drawScreen(posX, posY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.menuComponents.forEach(component -> component.onKeyPress(keyCode));
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        this.menuComponents.forEach(component -> component.onClickReleased(mouseX, mouseY, state));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int state) throws IOException {
        this.menuComponents.forEach(component -> component.onClickBegin(mouseX, mouseY, state));
        super.mouseClicked(mouseX, mouseY, state);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

