package me.spec.eris.client.ui.click.panels.component.button.impl.config;

import java.awt.*;
import java.io.IOException;

import me.spec.eris.Eris;
import me.spec.eris.api.config.ClientConfig;
import me.spec.eris.client.ui.click.ClickGui;
import me.spec.eris.client.ui.click.panels.component.button.Button;
import me.spec.eris.utils.misc.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class ConfigButton extends Button {

    private ClientConfig host;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean hovered;
    public int animation;
    public boolean opened = false;
    private boolean clickable = false;


    public ConfigButton(ClientConfig host) {
        this.host = host;
    }

    public void keyTyped(char typedChar, int keyCode) {

    }

    public ClientConfig getMod() {
        return this.host;
    }

    private float lastRed = (float) ClickGui.getSecondaryColor(false).getRed() / 255F;
    private float lastGreen = (float) ClickGui.getSecondaryColor(false).getGreen() / 255F;
    private float lastBlue = (float) ClickGui.getSecondaryColor(false).getBlue() / 255F;

    public int drawScreen(int mouseX, int mouseY, int x, int y, int width, boolean open) {
        this.clickable = open;
        this.x = x;
        this.y = y;
        this.height = 15;
        this.width = width;
        this.hovered = this.isHovered(mouseX, mouseY);

        Color correctColor = ClickGui.getSecondaryColor(false);
        if (this.hovered) {
            int dark = 8;
            correctColor = new Color(Math.max(correctColor.getRed() - dark, 0), Math.max(correctColor.getGreen() - dark, 0), Math.max(correctColor.getBlue() - dark, 0));
        }

        float speed = 256F / (float) Minecraft.getMinecraft().getDebugFPS();
        lastRed += (((float) correctColor.getRed() / 255F) - lastRed) / speed;
        lastGreen += (((float) correctColor.getGreen() / 255F) - lastGreen) / speed;
        lastBlue += (((float) correctColor.getBlue() / 255F) - lastBlue) / speed;

        lastRed = Math.max(0, Math.min(1, lastRed));
        lastGreen = Math.max(0, Math.min(1, lastGreen));
        lastBlue = Math.max(0, Math.min(1, lastBlue));

        Gui.drawRect(x, y, x + width, y + height, new Color(lastRed, lastGreen, lastBlue, (float) ClickGui.getSecondaryColor(false).getAlpha() / 255F).getRGB());
        ClickGui.getFont().drawString(host.getConfigName().substring(0, 1).toUpperCase() + host.getConfigName().substring(1), this.x, this.y + (this.height / 2) - (ClickGui.getFont().getHeight(host.getConfigName()) / 2), new Color(175, 175, 175).getRGB());
        return this.height;
    }

    public void mouseClicked(int x, int y, int button) {
        if (!clickable) return;
        this.hovered = this.isHovered(x, y);

        if (this.hovered && button == 0) {
            try {
                Eris.getInstance().configManager.loadConfig(host);
                Helper.sendMessage("Loaded config " + host.getConfigName());
            } catch (IOException e) {
                Helper.sendMessage("Failed to load config " + host.getConfigName());
                e.printStackTrace();
            }
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (!clickable) return;

    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY > y && mouseY < y + height;
    }

    public int getWidth() {
        return this.width;
    }
}
