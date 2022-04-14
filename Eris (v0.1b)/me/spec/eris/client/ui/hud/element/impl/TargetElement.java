package me.spec.eris.client.ui.hud.element.impl;

import me.spec.eris.Eris;
import me.spec.eris.client.modules.render.HUD;
import me.spec.eris.client.ui.fonts.TTFFontRenderer;
import me.spec.eris.client.ui.hud.element.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public class TargetElement extends Element {

    public TargetElement(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        width = 250;
        height = 75;
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
        HUD hud = ((HUD) Eris.getInstance().getModuleManager().getModuleByClass(HUD.class));
        hud.renderTarget(x,y);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        if (mouseX < x + width && mouseX > x - 5 && mouseY < y + 5 && mouseY > y - height) {
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