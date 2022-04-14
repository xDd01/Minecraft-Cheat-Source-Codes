/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.drag.screen;

import cafe.corrosion.Corrosion;
import cafe.corrosion.component.draggable.IDraggable;
import cafe.corrosion.menu.drag.GuiComponentManager;
import cafe.corrosion.menu.drag.data.HudComponentProxy;
import cafe.corrosion.module.Module;
import cafe.corrosion.util.render.GuiUtils;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

public class GuiDragScreen
extends GuiScreen {
    private final GuiComponentManager guiComponentManager;
    private IDraggable selectedComponent;
    private int startPosX;
    private int startPosY;
    private int startComponentX;
    private int startComponentY;

    public GuiDragScreen(GuiComponentManager guiComponentManager) {
        this.guiComponentManager = guiComponentManager;
    }

    @Override
    public void drawScreen(int posX, int posY, float partialTicks) {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        Corrosion.INSTANCE.getBlurrer().blur(0.0, 0.0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), true);
        if (this.selectedComponent != null) {
            int mouseX = Mouse.getX();
            int mouseY = Mouse.getY();
            Module iDraggable = (Module)((Object)this.selectedComponent);
            RenderUtil.drawRoundedRect(mouseX, mouseY, mouseX + 20, mouseY + 20, Color.RED.getRGB());
            HudComponentProxy proxy = this.guiComponentManager.getProxiedComponents().get(iDraggable);
            proxy.setX(mouseX / 2);
            proxy.setY(scaledResolution.getScaledHeight() - Mouse.getY() / 2);
        }
        int color = new Color(255, 255, 255, 175).getRGB();
        this.guiComponentManager.getProxiedComponents().forEach((module, component) -> {
            IDraggable draggable = (IDraggable)((Object)module);
            int componentX = component.getX();
            int componentY = component.getY();
            int componentExpandX = component.getExpandX();
            int componentExpandY = component.getExpandY();
            draggable.renderBackground(scaledResolution, componentX, componentY, componentExpandX, componentExpandY, color);
            draggable.render((HudComponentProxy)component, scaledResolution, componentX, componentY, componentExpandX, componentExpandY);
        });
        super.drawScreen(posX, posY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.guiComponentManager.getProxiedComponents().forEach((key, value) -> {
            if (!GuiUtils.isHoveringPos(mouseX, mouseY, value.getX(), value.getY(), value.getX() + value.getExpandX(), value.getY() + value.getExpandY())) {
                return;
            }
            this.selectedComponent = (IDraggable)((Object)key);
            this.startPosX = mouseX;
            this.startPosY = mouseY;
            this.startComponentX = value.getX();
            this.startComponentY = value.getY();
        });
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (this.selectedComponent != null) {
            this.selectedComponent = null;
        }
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

