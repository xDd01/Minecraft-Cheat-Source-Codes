package me.spec.eris.client.ui.click;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import me.spec.eris.Eris;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.modules.render.HUD;
import me.spec.eris.client.ui.click.panels.Panel;
import me.spec.eris.client.ui.fonts.TTFFontRenderer;
import me.spec.eris.client.ui.hud.CustomHUD;
import me.spec.eris.utils.visual.RenderUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

public class ClickGui extends GuiScreen {
    public static String toolTip = "no information to display";
    public static ArrayList<Panel> panels = new ArrayList<Panel>();
    public static boolean dragging = false;
    public boolean createdPanels;

    public void reload(boolean reloadUserInterface) {
        for (Panel p : ClickGui.panels) {
            p.reload();
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    public ClickGui() {
        int x = 3;
        int y = 5;
        int count = 0;
        if (!createdPanels && panels.size() != ModuleCategory.values().length) {
            for (ModuleCategory c : ModuleCategory.values()) {
                Panel p = new Panel(x, y, c);
                panels.add(p);
                x += p.getWidth() + 5;
                count++;
                if (count % 3 == 0) {
                    y += 50;
                    x = 3;
                }
                createdPanels = (panels.size() == ModuleCategory.values().length);
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        ScaledResolution scalRes = new ScaledResolution(Minecraft.getMinecraft());

        //Config button -

        //Hud Button

        //backdrop
		drawGradientRect(0, 0, scalRes.getScaledWidth(), scalRes.getScaledHeight(), 0x00001215, new Color(0,0,0).getRGB());
        drawGradientRect(scalRes.getScaledWidth(), scalRes.getScaledHeight(),0,0, 0x00001215, new Color(0,0,0, 220).getRGB());

        RenderUtilities.drawRoundedRect(3, scalRes.getScaledHeight() , 3 + mc.fontRendererObj.getStringWidth("Hud Customization") + 3, scalRes.getScaledHeight() - mc.fontRendererObj.FONT_HEIGHT * 1.2f, Eris.getInstance().getClientColor(),Eris.getInstance().getClientColor());

        Eris.getInstance().getFontRenderer().drawStringWithShadow("Custom HUD", 5, scalRes.getScaledHeight() - 10, new Color(255,255,255).getRGB());

        for (int i = 0; i < panels.size(); i++) {
            panels.get(i).onTop = i == 0;
            panels.get(i).drawScreen(mouseX, mouseY);
        }

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        for (Panel p : ClickGui.panels) {
            p.mouseClicked(mouseX, mouseY, mouseButton);
        }

        ScaledResolution scalRes = new ScaledResolution(Minecraft.getMinecraft());
        int hudXPos1 = 3;
        int hudXPos2 = (int) (3 + Eris.getInstance().getFontRenderer().getStringWidth("Custom HUD") + 3);
        int hudYPos1 = scalRes.getScaledHeight();
        int hudYPos2 = scalRes.getScaledHeight() - 15;

        if (mouseY <= hudYPos1 && mouseY >= hudYPos2 && mouseX >= hudXPos1 && mouseX <= hudXPos2) {
            this.onGuiClosed();
            mc.displayGuiScreen(new CustomHUD(true));
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        for (Panel p : ClickGui.panels) {
            p.mouseReleased(mouseX, mouseY, state);
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (Panel p : ClickGui.panels) {
            p.keyTyped(typedChar, keyCode);
        }
        super.keyTyped(typedChar, keyCode);
    }

    public static Color getSecondaryColor(boolean setting) {
        return setting ? new Color(0, 0, 0, 160) : new Color(25, 25, 25, 200);
    }

    private static TTFFontRenderer fontRender;

    public static TTFFontRenderer getFont() {
        if (fontRender == null) {
            fontRender = Eris.INSTANCE.fontManager.getFont("SFUI 18");
        }
        return fontRender;
    }

    public static Color getPrimaryColor() {
        return Eris.getInstance().getClientColor2();
    }
}
