package me.spec.eris.client.ui.main;

import java.awt.Color;
import java.io.IOException;

import me.spec.eris.Eris;
import me.spec.eris.client.ui.alts.gui.GuiAltManager;
import me.spec.eris.client.ui.login.GuiLogin;
import me.spec.eris.client.ui.main.buttons.ExpandButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.MathHelper;

public class MainMenu extends GuiMainMenu
{
    public void initGui() {
    	
        if (Eris.getInstance() == null) {
            mc.displayGuiScreen(new GuiLogin(this));
            return;
        }
        viewportTexture = new DynamicTexture(256, 256);
        backgroundTexture = mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture);
        backgroundTexture = mc.getTextureManager().getDynamicTextureLocation("background", viewportTexture);

    	//TODO: Discord integration, properly!
        Eris.getInstance().discordIntegration.update( "Idling", "In Main Menu");
        buttonList.add(new ExpandButton(2, width / 2 - 100, height / 2 - 32, 200, 20, "Singleplayer"));
        buttonList.add(new ExpandButton(0, width / 2 - 100, height / 2 - 11, 200, 20, "Multiplayer"));
        buttonList.add(new ExpandButton(1, width / 2 - 100, height / 2 + 11, 200, 20, "Accounts"));
        buttonList.add(new ExpandButton(3, width / 2 - 100, height / 2 + 33, 100, 20, "Options"));
        buttonList.add(new ExpandButton(4, width / 2, height / 2 + 33, 100, 20, "Quit"));
    }
    
    public void actionPerformed(final GuiButton button) throws IOException {
        if (Eris.getInstance() == null) {
            mc.displayGuiScreen(new GuiLogin(this));
            return;
        }
        switch (button.id) {
        	case 0:
            	mc.displayGuiScreen(new GuiMultiplayer(this));
        	break;
        	case 1:
            	mc.displayGuiScreen(new GuiAltManager());
        	break;
        	case 2:
            	mc.displayGuiScreen(new GuiSelectWorld(this));
        	break;
        	case 3:
            	mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        	break;
        	case 4:
            	mc.shutdown();
        	break;
        }
    }
     
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
    	drawGradientRect(0, 0, width, height, 0, -1610612736);
    	drawGradientRect(0, 0, width, height, -2130750123, 16764108);

        String s = "Eris" + " | 1.8.9";
        Eris.getInstance().getFontRenderer().drawString(s, 2, this.height - 10, -1);

        String s2 = "Developed with love - Spec";
        Eris.getInstance().getFontRenderer().drawString(s2, (int) (this.width - Eris.getInstance().getFontRenderer().getStringWidth(s2) - 2), this.height - 10, -1);
 
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) (this.width / 2), this.height / 2 - 70, 0.0F);
        GlStateManager.rotate(0, 0.0F, 0.0F, 1.0F);
        float f = 1.8F - MathHelper.abs(MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L) / 1000.0F * (float) Math.PI * 2.0F) * .4F);
        f = f * 100.0F / (float) (Eris.getInstance().getFontRenderer().getStringWidth("IN DEVELOPMENT") + 32);
        GlStateManager.scale(f, f, f);

        Eris.getInstance().getFontRenderer().drawCenteredString( "IN DEVELOPMENT", 0, 0, -256);
        GlStateManager.popMatrix();
        
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) (this.width / 2), this.height / 2 - 70, 0.0F);
        GlStateManager.rotate(0, 0.0F, 0.0F, 1.0F);
        float f2 = 4F;
        f = f * 100.0F / (float) (Eris.getInstance().getFontRenderer().getStringWidth("ERIS") + 32);
        GlStateManager.scale(f2, f2, f2);
        this.drawCenteredString(this.fontRendererObj, "ERIS", 0, -10, new Color(255,20,20).getRGB());
        GlStateManager.popMatrix();
    	super.drawScreen(mouseX, mouseY, partialTicks);
    }
}