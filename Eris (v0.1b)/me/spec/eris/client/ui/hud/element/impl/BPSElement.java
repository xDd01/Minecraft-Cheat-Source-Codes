package me.spec.eris.client.ui.hud.element.impl;


import me.spec.eris.Eris;
import me.spec.eris.client.modules.render.HUD;
import me.spec.eris.client.ui.click.ClickGui;
import me.spec.eris.client.ui.click.panels.component.Component;
import me.spec.eris.client.ui.fonts.TTFFontRenderer;
import me.spec.eris.client.ui.hud.element.Element;
import me.spec.eris.utils.math.MathUtils;
import me.spec.eris.utils.player.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class BPSElement extends Element {

    private boolean hovered, clickable, open;

    public BPSElement(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (dragging) {
            ScaledResolution scalRes = new ScaledResolution(Minecraft.getMinecraft());
            int predictX = mouseX - (width / 2) + xOffset - 1;
            int predictY = mouseY - (height / 2) + yOffset - 1;
            if (predictX > 0 && predictX < scalRes.getScaledWidth() - width + 1) {
                x = predictX;
            }
            if (predictY > 0 && predictY < scalRes.getScaledHeight() - 5) {
                y = predictY;
            }
        }
        HUD hud = ((HUD)Eris.getInstance().getModuleManager().getModuleByClass(HUD.class));
        String bps = hud.bpsType.getValue().equals(HUD.BPSMode.BPS) ? "BPS" + EnumChatFormatting.GRAY + ": " + EnumChatFormatting.RESET + MathUtils.round(PlayerUtils.getDistTraveled(hud.distances), hud.bpsPlaces.getValue() != null ? hud.bpsPlaces.getValue() : 3) : "Blocks per/s";

        width = (int) getFont().getStringWidth(bps);
        height = (int) getFont().getHeight(bps);
        hud.renderBPS(x,y);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isHovered(mouseX, mouseY)) {
            if (mouseButton == 0 && !dragged) {
                dragging = true;
                dragged = true;
                int xPos = this.x + (width / 2);
                int yPos = this.y + (height / 2);
                this.xOffset = xPos - x;
                this.yOffset = yPos - y;
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (dragging && state == 0) {
            dragging = false;
            dragged = false;
        }
    }

    @Override
    public void actionPerformed(GuiButton button) throws IOException {

    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {

    }
    private static TTFFontRenderer fontRender;
    public static TTFFontRenderer getFont() {
        if (fontRender == null) {
            fontRender = Eris.INSTANCE.fontManager.getFont("SFUI 18");
        }

        return fontRender;
    }
}