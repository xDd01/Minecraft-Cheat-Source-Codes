package me.spec.eris.client.ui.hud.element.impl;


import me.spec.eris.Eris;
import me.spec.eris.client.modules.render.HUD;
import me.spec.eris.client.ui.fonts.TTFFontRenderer;
import me.spec.eris.client.ui.hud.element.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;

public class ModuleListElement extends Element {

    public ModuleListElement(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        if (dragging) {

            ScaledResolution scalRes = new ScaledResolution(Minecraft.getMinecraft());

            int predictX = mouseX - (width / 2);
            int predictY = mouseY - height / 2;
            if (!(predictX < 0 || predictX > scalRes.getScaledWidth() - (width / 2)))  x = (mouseX + width / 2) + xOffset;
            if (!(predictY < 0|| predictY > scalRes.getScaledHeight() - (height / 2)))  y = (mouseY - height / 2) + yOffset;

        }

        HUD hud = ((HUD)Eris.getInstance().getModuleManager().getModuleByClass(HUD.class));
        int[] rq = hud.renderModuleList(x,y);
        width = rq[0];
        height = rq[1];
    }



    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (isHovered(mouseX + width, mouseY)) {
            if (mouseButton == 0 && !dragged) {
                dragging = true;
                dragged = true;
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
