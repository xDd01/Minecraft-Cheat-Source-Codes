package me.spec.eris.client.ui.alts.gui;

import java.awt.Color;

import me.spec.eris.Eris;
import me.spec.eris.client.ui.alts.AltManager;
import me.spec.eris.utils.visual.RenderUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;

public class AltButton extends GuiButton {

    private int alt;
    private boolean selected = false;
    private int originalY;

    private GuiAltManager parent;

    public AltButton(int x, int y, int alt, GuiAltManager parent) {
        super(0, x, y, "");
        this.alt = alt;
        this.parent = parent;
        this.originalY = y;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!this.parent.isAltInArea(this.yPosition)) {
            return;
        }
        ScaledResolution scalRes = new ScaledResolution(Minecraft.getMinecraft());
        this.hovered = mouseX >= 10 && mouseY >= this.yPosition && mouseX < scalRes.getScaledWidth() - 170 && mouseY < this.yPosition + this.height;

        Color color = new Color(0, 0, 0, 150);
        if (this.hovered) {
            color = new Color(0, 0, 0, 200);
        }
        RenderUtilities.drawRoundedRect(10, yPosition, scalRes.getScaledWidth() - 170, yPosition + height, new Color(255,0,0).getRGB(), color.getRGB());
        if (this.selected) {
            RenderUtilities.drawRoundedRect(10, yPosition, scalRes.getScaledWidth() - 170, yPosition + height, new Color(255,0,0).getRGB(), color.getRGB());
        }
        String toDraw = Eris.getInstance().altManager.getManagerArraylist().get(alt).getUser();
        Eris.getInstance().getFontRenderer().drawCenteredString(toDraw, (Math.abs((scalRes.getScaledWidth() - 170) - 5) / 2), this.yPosition + (height / 2) - Eris.getInstance().getFontRenderer().getHeight(toDraw) / 2, -1);
       // mc.fontRendererObj.drawStringWithShadow(Eris.getInstance().altManager.getManagerArraylist().get(alt).getUser(), this.xPosition + (this.width / 2) - (mc.fontRendererObj.getStringWidth(Eris.getInstance().altManager.getManagerArraylist().get(alt).getUser()) / 2), this.yPosition + (this.height / 2) - (mc.fontRendererObj.FONT_HEIGHT / 2), -1);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public int getOrigY() {
        return this.originalY;
    }

    public int getAlt() {
        return this.alt;
    }

    @Override
    public void mouseClicked(int x, int y) {
        if (this.selected) {
            this.parent.login(Eris.getInstance().altManager.getManagerArraylist().get(alt));
        }
        this.parent.setSelected(this.alt);
    }
}
