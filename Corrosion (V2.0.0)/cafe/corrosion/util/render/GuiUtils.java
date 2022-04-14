/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiUtils {
    public static void drawRect1(double x2, double y2, double width, double height, int color) {
        float f2 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 16 & 0xFF) / 255.0f;
        float f22 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f3 = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f(f1, f22, f3, f2);
        Gui.drawRect(x2, y2, x2 + width, y2 + height, color);
    }

    public static boolean isHovering(int mouseX, int mouseY, int posX, int posY, int width, int height) {
        return mouseX >= posX && mouseX < posX + width && mouseY >= posY && mouseY < posY + height;
    }

    public static boolean isHoveringPos(int mouseX, int mouseY, int posX, int posY, int maxX, int maxY) {
        posX = Math.min(posX, maxX);
        posY = Math.min(posY, maxY);
        return mouseX >= posX && mouseX < maxX && mouseY >= posY && mouseY < maxY;
    }

    public static void glResets() {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    public static void drawStraightLine(int startPos, int endPos, int yPos, int thickness, int color) {
        Gui.drawRect(startPos, yPos + thickness, endPos, yPos, color);
    }

    public static void glStartFontRenderer() {
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    public static void drawImage(ResourceLocation resourceLocation, float x2, float y2, float width, float height, int rgba) {
        float a2 = (float)(rgba >> 24 & 0xFF) / 255.0f;
        float r2 = (float)(rgba >> 16 & 0xFF) / 255.0f;
        float g2 = (float)(rgba >> 8 & 0xFF) / 255.0f;
        float b2 = (float)(rgba & 0xFF) / 255.0f;
        GL11.glPushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GlStateManager.color(r2, g2, b2, a2);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        Gui.drawScaledCustomSizeModalRect(x2, y2, 0.0f, 0.0f, width, height, width, height, width, height);
        GL11.glPopMatrix();
    }

    public static void drawRect(double left, double top, double right, double bottom, float opacity) {
        if (left < right) {
            double i2 = left;
            left = right;
            right = i2;
        }
        if (top < bottom) {
            double j2 = top;
            top = bottom;
            bottom = j2;
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(0.1f, 0.1f, 0.1f, opacity);
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(left, bottom, 0.0).endVertex();
        worldrenderer.pos(right, bottom, 0.0).endVertex();
        worldrenderer.pos(right, top, 0.0).endVertex();
        worldrenderer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
}

