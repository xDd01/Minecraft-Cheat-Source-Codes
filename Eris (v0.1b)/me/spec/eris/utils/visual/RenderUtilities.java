package me.spec.eris.utils.visual;

import java.text.DecimalFormat;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ResourceLocation;

/**
 * Author: Ice Created: 17:39, 11-Jun-20 Project: Client
 */
public class RenderUtilities extends GuiScreen {

    public static final RenderUtilities INSTANCE = new RenderUtilities();
    public static DecimalFormat decimalFormat = new DecimalFormat("#.#");
    public static Minecraft mc = Minecraft.getMinecraft();
 
    public static void drawGradientRect(double left, double top, double right, double bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        worldrenderer.func_181662_b(right, top, 0.0D).func_181666_a(f1, f2, f3, f).func_181675_d();
        worldrenderer.func_181662_b(left, top, 0.0D).func_181666_a(f1, f2, f3, f).func_181675_d();
        worldrenderer.func_181662_b(left, bottom, 0.0D).func_181666_a(f5, f6, f7, f4).func_181675_d();
        worldrenderer.func_181662_b(right, bottom, 0.0D).func_181666_a(f5, f6, f7, f4).func_181675_d();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void drawImage(final ResourceLocation image, final int x, final int y, final int width, final int height) {
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0f, 0.0f, width, height, (float) width, (float) height);
    }

    public static void glColor(int hex) {
        float alpha = (hex >> 24 & 0xFF) / 255.0F;
        float red = (hex >> 16 & 0xFF) / 255.0F;
        float green = (hex >> 8 & 0xFF) / 255.0F;
        float blue = (hex & 0xFF) / 255.0F;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void drawRoundedRect(float x, float y, float x1, float y1, int borderC, int insideC) {
    	GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
        drawVLine(x *= 2.0f, (y *= 2.0f) + 1.0f, (y1 *= 2.0f) - 2.0f, borderC);
        drawVLine((x1 *= 2.0f) - 1.0f, y + 1.0f, y1 - 2.0f, borderC);
        drawHLine(x + 2.0f, x1 - 3.0f, y, borderC);
        drawHLine(x + 2.0f, x1 - 3.0f, y1 - 1.0f, borderC);
        drawHLine(x + 1.0f, x + 1.0f, y + 1.0f, borderC);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y + 1.0f, borderC);
        drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, borderC);
        drawHLine(x + 1.0f, x + 1.0f, y1 - 2.0f, borderC);
        drawRect(x + 1.0f, y + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
        GL11.glScalef((float)2.0f, (float)2.0f, (float)2.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    
    public static void drawHLine(float x, float y, float x1, int y1) {
        if (y < x) {
            float var5 = x;
            x = y;
            y = var5;
        }
        drawRect(x, x1, y + 1.0f, x1 + 1.0f, y1);
    }

    public static void drawVLine(float x, float y, float x1, int y1) {
        if (x1 < y) {
            float var5 = y;
            y = x1;
            x1 = var5;
        }
        drawRect(x, y + 1.0f, x + 1.0f, x1, y1);
    }

    public static void drawHLine(float x, float y, float x1, int y1, int y2) {
        if (y < x) {
            float var5 = x;
            x = y;
            y = var5;
        }
        drawGradientRect(x, x1, y + 1.0f, x1 + 1.0f, y1, y2);
    }

    public void drawBackground(ResourceLocation image) {
        ScaledResolution p_180480_2_ = new ScaledResolution(Minecraft.getMinecraft());
        Minecraft.getMinecraft().getTextureManager().bindTexture(image);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldrenderer.func_181662_b(0.0D, (double) p_180480_2_.getScaledHeight(), -90.0D).func_181673_a(0.0D, 1.0D).func_181675_d();
        worldrenderer.func_181662_b((double) p_180480_2_.getScaledWidth(), (double) p_180480_2_.getScaledHeight(), -90.0D)
                .func_181673_a(1.0D, 1.0D).func_181675_d();
        worldrenderer.func_181662_b((double) p_180480_2_.getScaledWidth(), 0.0D, -90.0D).func_181673_a(1.0D, 0.0D).func_181675_d();
        worldrenderer.func_181662_b(0.0D, 0.0D, -90.0D).func_181673_a(0.0D, 0.0D).func_181675_d();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

    }

    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        int zLevel = 0;
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldrenderer.func_181662_b((double) right, (double) top, (double) zLevel).func_181666_a(f1, f2, f3, f).func_181675_d();
        worldrenderer.func_181662_b((double) left, (double) top, (double) zLevel).func_181666_a(f1, f2, f3, f).func_181675_d();
        worldrenderer.func_181662_b((double) left, (double) bottom, (double) zLevel).func_181666_a(f5, f6, f7, f4).func_181675_d();
        worldrenderer.func_181662_b((double) right, (double) bottom, (double) zLevel).func_181666_a(f5, f6, f7, f4).func_181675_d();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public void drawBorderedRect(int x, int y, int x1, int y1, int size, int borderC, int insideC) {
        drawRect(x + size, y + size, x1 - size, y1 - size, insideC);
        drawRect(x + size, y + size, x1, y, borderC);
        drawRect(x, y, x + size, y1 - 1, borderC);
        drawRect(x1, y1 - 1, x1 - size, y + size, borderC);
        drawRect(x, y1 - size, x1, y1, borderC);
    }

    public void drawBorderedRect(double x, double y, double x1, double y1, double size, int borderC, int insideC) {
        drawRect(x + size, y + size, x1 - size, y1 - size, insideC);
        drawRect(x + size, y + size, x1, y, borderC);
        drawRect(x, y, x + size, y1 - 1, borderC);
        drawRect(x1, y1 - 1, x1 - size, y + size, borderC);
        drawRect(x, y1 - size, x1, y1, borderC);
    }

    public void drawBorderedRectSkeet(int x, int y, int x1, int y1, int size, int borderC, int insideC) {
        drawRect(x + size, y + size, x1 - size, y1 - size, insideC);
        drawRect(x + size, y + size, x1, y, borderC);
        drawRect(x, y, x + size, y1 - 1, borderC);
        drawRect(x1, y1 - 1, x1 - size, y + size, borderC);
        drawRect(x, y1 - size, x1, y1, borderC);
    }

    public static void drawChestEsp(TileEntity tileEntity, double x, double y, double z, int r, int g, int b, int a) {
        double xOff = 0;
        double zOff = 0;
        double xOff2 = 0;
        double zOff2 = 0;

        if (tileEntity instanceof TileEntityChest) {
            TileEntityChest tileEntityChest = (TileEntityChest) tileEntity;
            if (tileEntityChest.adjacentChestXPos != null) {
                xOff = -1;
                xOff2 = -1;
            } else if (tileEntityChest.adjacentChestXNeg != null) {
                xOff = -1 - 0.002;
                xOff2 = 0.0125;
            } else if (tileEntityChest.adjacentChestZPos != null) {
                zOff = -1;
                zOff2 = -1;
            } else if (tileEntityChest.adjacentChestZNeg != null) {
                zOff = -1 - 0.002;
                zOff2 = 0.0125;
            }
        }

        if (xOff == -1 || xOff2 == -1 || zOff == -1 || zOff2 == -1)
            return;

        GlStateManager.pushAttrib();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GL11.glBlendFunc(770, 771);

        GlStateManager.disableLighting();
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(false);
        GL11.glColor4f(r / 255f, g / 255f, b / 255f, a / 255f);
        GL11.glLineWidth(1.5F);

        GL11.glBegin(7);
        GL11.glVertex3d(x + 0.0625 + xOff, y, z + 0.0625 + zOff);
        GL11.glVertex3d(x + 0.0625 + xOff, y - (0.062f * 2) + 1.0D, z + 0.0625 + zOff);
        GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y - (0.062f * 2) + 1.0D, z + 0.0625 + zOff);
        GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y, z + 0.0625 + zOff);
        GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y, z + 0.0625 + zOff);
        GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y - (0.062f * 2) + 1.0D, z + 0.0625 + zOff);
        GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y - (0.062f * 2) + 1.0D, z - 0.0625 + zOff2 + 1.0D);
        GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y, z - 0.0625 + zOff2 + 1.0D);
        GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y, z - 0.0625 + zOff2 + 1.0D);
        GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y - (0.062f * 2) + 1.0D, z - 0.0625 + zOff2 + 1.0D);
        GL11.glVertex3d(x + 0.0625 + xOff, y - (0.062f * 2) + 1.0D, z - 0.0625 + zOff2 + 1.0D);
        GL11.glVertex3d(x + 0.0625 + xOff, y, z - 0.0625 + zOff2 + 1.0D);
        GL11.glVertex3d(x + 0.0625 + xOff, y, z - 0.0625 + zOff2 + 1.0D);
        GL11.glVertex3d(x + 0.0625 + xOff, y - (0.062f * 2) + 1.0D, z - 0.0625 + zOff2 + 1.0D);
        GL11.glVertex3d(x + 0.0625 + xOff, y - (0.062f * 2) + 1.0D, z + 0.0625 + zOff);
        GL11.glVertex3d(x + 0.0625 + xOff, y, z + 0.0625 + zOff);
        GL11.glVertex3d(x + 0.0625 + xOff, y, z + 0.0625 + zOff);
        GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y, z + 0.0625 + zOff);
        GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y, z - 0.0625 + zOff2 + 1.0D);
        GL11.glVertex3d(x + 0.0625 + xOff, y, z - 0.0625 + zOff2 + 1.0D);
        GL11.glVertex3d(x + 0.0625 + xOff, y - (0.062f * 2) + 1.0D, z + 0.0625 + zOff);
        GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y - (0.062f * 2) + 1.0D, z + 0.0625 + zOff);
        GL11.glVertex3d(x - 0.0625 + xOff2 + 1.0D, y - (0.062f * 2) + 1.0D, z - 0.0625 + zOff2 + 1.0D);
        GL11.glVertex3d(x + 0.0625 + xOff, y - (0.062f * 2) + 1.0D, z - 0.0625 + zOff2 + 1.0D);
        GL11.glEnd();

        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        GlStateManager.popAttrib();
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY,
                                          EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 255f);
        GlStateManager.translate((float) posX, (float) posY, 50.0F);
        GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(100.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-170.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float) Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        ent.renderYawOffset = mouseX;
        ent.rotationYaw = mouseX;
        ent.rotationPitch = -mouseY;
        ent.rotationYawHead = mouseX;
        ent.prevRotationYawHead = mouseX;
        GlStateManager.translate(0.0F, 0.0F, 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.renderEntityWithPosYaw(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

    }

    public static void drawRectangle(double x, double y, double width, double height, int color) {
        float f = (color >> 24 & 0xFF) / 255.0F;
        float f1 = (color >> 16 & 0xFF) / 255.0F;
        float f2 = (color >> 8 & 0xFF) / 255.0F;
        float f3 = (color & 0xFF) / 255.0F;
        GL11.glColor4f(f1, f2, f3, f);
        Gui.drawRect(x, y, x + width, y + height, color);

    }

    public static void drawBorderedRectangle(double left, double top, double right, double bottom, double borderWidth,
                                             int insideColor, int borderColor, boolean borderIncludedInBounds) {
        drawRectangleCustom(left - (!borderIncludedInBounds ? borderWidth : 0),
                top - (!borderIncludedInBounds ? borderWidth : 0), right + (!borderIncludedInBounds ? borderWidth : 0),
                bottom + (!borderIncludedInBounds ? borderWidth : 0), borderColor);
        drawRectangleCustom(left + (borderIncludedInBounds ? borderWidth : 0),
                top + (borderIncludedInBounds ? borderWidth : 0), right - ((borderIncludedInBounds ? borderWidth : 0)),
                bottom - ((borderIncludedInBounds ? borderWidth : 0)), insideColor);
    }

    public static void drawRectangleCustom(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float) (color >> 24 & 255) / 255.0F;
        float f = (float) (color >> 16 & 255) / 255.0F;
        float f1 = (float) (color >> 8 & 255) / 255.0F;
        float f2 = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(f, f1, f2, f3);
        worldrenderer.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        worldrenderer.func_181662_b(left, bottom, 0.0D).func_181675_d();
        worldrenderer.func_181662_b(right, bottom, 0.0D).func_181675_d();
        worldrenderer.func_181662_b(right, top, 0.0D).func_181675_d();
        worldrenderer.func_181662_b(left, top, 0.0D).func_181675_d();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

}
