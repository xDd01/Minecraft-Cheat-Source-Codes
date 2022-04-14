package me.spec.eris.client.ui.hud;

import me.spec.eris.Eris;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.ui.click.ClickGui;
import me.spec.eris.client.ui.click.panels.Panel;
import me.spec.eris.client.ui.hud.element.impl.*;
import me.spec.eris.client.ui.hud.element.impl.LabelElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class CustomHUD extends GuiScreen {

    public boolean opened,createdPanels;
    public ScaledResolution scaledResolution;

    public CustomHUD(boolean opened) {
        this.opened = opened;
        scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        Eris.getInstance().customHUDManager.clearManagerArraylist();
        Eris.getInstance().customHUDManager.addToManagerArraylist(new ModuleListElement(scaledResolution.getScaledWidth(),0, 40 , 20));
        Eris.getInstance().customHUDManager.addToManagerArraylist(new LabelElement(2,2, 20 , 20));
        Eris.getInstance().customHUDManager.addToManagerArraylist(new BuildInfoElement(2,scaledResolution.getScaledHeight() - 35, 20 , 20));
        Eris.getInstance().customHUDManager.addToManagerArraylist(new CoordsElement(2,scaledResolution.getScaledHeight() - 15, 20 , 20));
        Eris.getInstance().customHUDManager.addToManagerArraylist(new PotionsElement(0 , 250, 100, 100));
        Eris.getInstance().customHUDManager.addToManagerArraylist(new BPSElement(0 , 300, 100, 100));
        Eris.getInstance().customHUDManager.addToManagerArraylist(new PingElement(0 , 250, 100, 100));
        Eris.getInstance().customHUDManager.addToManagerArraylist(new TargetElement(300, 400, 250, 75));
    }
    @Override
    public void onGuiClosed() {
        Eris.getInstance().fileManager.getCustomHUDFile().save();
        Eris.getInstance().customHUDManager.clearManagerArraylist();
        super.onGuiClosed();
    }

    @Override
    public void initGui() {
        Eris.getInstance().fileManager.getCustomHUDFile().load();
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        Gui.drawRect(0,(scaledResolution.getScaledHeight() / 2) - 1, scaledResolution.getScaledWidth(), (scaledResolution.getScaledHeight() / 2) + 1, Eris.getInstance().getClientColor());
        Gui.drawRect((scaledResolution.getScaledWidth() / 2) - 1,0,(scaledResolution.getScaledWidth() / 2) + 1, scaledResolution.getScaledHeight(), Eris.getInstance().getClientColor());
        Gui.drawRect(0,0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), new Color(0,0,0,135).getRGB());
        Eris.getInstance().customHUDManager.drawScreenForPanels(mouseX, mouseY);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Eris.getInstance().customHUDManager.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        Eris.getInstance().customHUDManager.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}
