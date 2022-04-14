package me.spec.eris.client.ui;

import java.io.IOException;

import me.spec.eris.Eris;
import org.lwjgl.opengl.GL11;

import me.spec.eris.client.ui.alts.gui.GuiAltManager;
import me.spec.eris.client.ui.main.buttons.ExpandButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class MainMenu extends GuiMainMenu
{
    public void initGui() {
            this.viewportTexture = new DynamicTexture(256, 256);
            this.buttonList.add(new ExpandButton(0, this.width / 2 - 100, this.height / 2 - 11, 200, 20, "Multiplayer"));
            this.buttonList.add(new ExpandButton(1, this.width / 2 - 100, this.height / 2 + 11, 200, 20, "Alt Manager"));
            this.buttonList.add(new ExpandButton(2, this.width / 2 - 100, this.height / 2 + 33, 99, 20, "Singleplayer"));
            this.buttonList.add(new ExpandButton(3, this.width / 2 + 1, this.height / 2 + 33, 99, 20, "Realms"));
            this.buttonList.add(new ExpandButton(4, this.width / 2 - 100, this.height / 2 + 55, 65, 20, "Language"));
            this.buttonList.add(new ExpandButton(5, this.width / 2 - 32, this.height / 2 + 55, 64, 20, "Options"));
            this.buttonList.add(new ExpandButton(6, this.width / 2 + 35, this.height / 2 + 55, 65, 20, "Quit"));
         
    }
    
    public void actionPerformed(final GuiButton button) throws IOException {
 
            if (button.id == 0) {
                this.mc.displayGuiScreen(new GuiMultiplayer(this));
            }
            if (button.id == 1) {
                this.mc.displayGuiScreen(new GuiAltManager());
            }
            if (button.id == 2) {
                this.mc.displayGuiScreen(new GuiSelectWorld(this));
            }
            if (button.id == 5) {
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
            }
            if (button.id == 6) {
                this.mc.shutdown();
            }
 
    }
    
    public void drawScreen(final int mouseX, final int mouseY, final float partialTicks) {
            GlStateManager.disableAlpha();
            this.renderSkybox(mouseX, mouseY, partialTicks);
            GlStateManager.enableAlpha();
            this.drawGradientRect(0, 0, this.width, this.height, 0, -1610612736);
            this.drawGradientRect(0, 0, this.width, this.height, -2130750123, 16764108);
            this.mc.getTextureManager().bindTexture(MainMenu.minecraftTitleTextures);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPushMatrix();
            GL11.glTranslatef((float)(this.width / 2), (float)(this.height - 50), 0.0f);
            float f = 1.8f - MathHelper.abs(MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L / 1000.0f * Math.PI * 2.0f)) * 0.1f);
            f = f * 100.0f / (this.fontRendererObj.getStringWidth(this.splashText) + 32);
            GL11.glScalef(f + 1.0f, f + 1.0f, f + 1.0f);
            GL11.glPopMatrix();
            drawRect(this.width / 2 - 104, this.height / 2 - 79, this.width / 2 + 104, this.height / 2 + 79, 1073741824);
            final float scale = 7.0f;
            GL11.glScalef(scale, scale, scale);
            this.drawString(this.fontRendererObj, EnumChatFormatting.DARK_RED + Eris.getInstance().getClientName(), this.width / 2 / (int) scale - (int) 12.0f, this.height / 2 / (int) scale - (int) 10.3f, 16777215);
            GL11.glScalef(1.0f / scale, 1.0f / scale, 1.0f / scale);
            this.drawString(this.fontRendererObj, "Minecraft §c1.8", (int) 2.0f, (int)(this.height - 10), 16777215);
 
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}