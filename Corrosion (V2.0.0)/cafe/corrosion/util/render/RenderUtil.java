/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.render;

import cafe.corrosion.util.render.scope.Scope;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import org.lwjgl.opengl.GL11;

public class RenderUtil {
    public static void drawRect(float left, float top, float right, float bottom, int color) {
        if (left < right) {
            float i2 = left;
            left = right;
            right = i2;
        }
        if (top < bottom) {
            float j2 = top;
            top = bottom;
            bottom = j2;
        }
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        RenderUtil.begin2D();
        GlStateManager.color(red, green, blue, alpha);
        GL11.glBegin(7);
        GL11.glVertex2f(left, bottom);
        GL11.glVertex2f(right, bottom);
        GL11.glVertex2f(right, top);
        GL11.glVertex2f(left, top);
        GL11.glEnd();
        RenderUtil.end2D();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
    }

    public static void drawRoundedRect(float x2, float y2, float x1, float y1, int borderC, int insideC) {
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RenderUtil.drawVLine(x2 *= 2.0f, (y2 *= 2.0f) + 1.0f, (y1 *= 2.0f) - 2.0f, borderC);
        RenderUtil.drawVLine((x1 *= 2.0f) - 1.0f, y2 + 1.0f, y1 - 2.0f, borderC);
        RenderUtil.drawHLine(x2 + 2.0f, x1 - 3.0f, y2, borderC);
        RenderUtil.drawHLine(x2 + 2.0f, x1 - 3.0f, y1 - 1.0f, borderC);
        RenderUtil.drawHLine(x2 + 1.0f, x2 + 1.0f, y2 + 1.0f, borderC);
        RenderUtil.drawHLine(x1 - 2.0f, x1 - 2.0f, y2 + 1.0f, borderC);
        RenderUtil.drawHLine(x1 - 2.0f, x1 - 2.0f, y1 - 2.0f, borderC);
        RenderUtil.drawHLine(x2 + 1.0f, x2 + 1.0f, y1 - 2.0f, borderC);
        RenderUtil.drawRect(x2 + 1.0f, y2 + 1.0f, x1 - 1.0f, y1 - 1.0f, insideC);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRoundedRect(float x2, float y2, float x1, float y1, int color) {
        RenderUtil.drawRoundedRect(x2, y2, x1, y1, color, color);
    }

    public static void drawHLine(float x2, float y2, float x1, int y1) {
        if (y2 < x2) {
            float var5 = x2;
            x2 = y2;
            y2 = var5;
        }
        RenderUtil.drawRect(x2, x1, y2 + 1.0f, x1 + 1.0f, y1);
    }

    public static void drawVLine(float x2, float y2, float x1, int y1) {
        if (x1 < y2) {
            float var5 = y2;
            y2 = x1;
            x1 = var5;
        }
        RenderUtil.drawRect(x2, y2 + 1.0f, x2 + 1.0f, x1, y1);
    }

    public static void drawTriangle(double cx2, double cy2, float g2, float theta, float h2, int c2) {
        GL11.glTranslated(cx2, cy2, 0.0);
        GL11.glRotatef(180.0f + theta, 0.0f, 0.0f, 1.0f);
        float f2 = (float)(c2 >> 24 & 0xFF) / 255.0f;
        float f22 = (float)(c2 >> 16 & 0xFF) / 255.0f;
        float f3 = (float)(c2 >> 8 & 0xFF) / 255.0f;
        float f4 = (float)(c2 & 0xFF) / 255.0f;
        GL11.glColor4f(f22, f3, f4, f2);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(h2);
        GL11.glBegin(6);
        GL11.glVertex2d(0.0, 1.0f * g2);
        GL11.glVertex2d(1.0f * g2, -1.0f * g2);
        GL11.glVertex2d(-1.0f * g2, -1.0f * g2);
        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glRotatef(-180.0f - theta, 0.0f, 0.0f, 1.0f);
        GL11.glTranslated(-cx2, -cy2, 0.0);
    }

    public static void drawRect(float x2, float y2, float x1, float y1) {
        GL11.glBegin(7);
        GL11.glVertex2f(x2, y1);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x1, y2);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();
    }

    public static void enableGL2D() {
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glDepthMask(true);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
    }

    public static void disableGL2D() {
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glHint(3154, 4352);
        GL11.glHint(3155, 4352);
    }

    public static void glColor(int hex) {
        float alpha = (float)(hex >> 24 & 0xFF) / 255.0f;
        float red = (float)(hex >> 16 & 0xFF) / 255.0f;
        float green = (float)(hex >> 8 & 0xFF) / 255.0f;
        float blue = (float)(hex & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    public static void drawOutlinedRectNoColor(float left, float top, float right, float bottom) {
        float var5;
        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        worldRenderer.begin(2, worldRenderer.getVertexFormat());
        worldRenderer.pos(left, bottom, 0.0);
        worldRenderer.pos(right, bottom, 0.0);
        worldRenderer.pos(right, top, 0.0);
        worldRenderer.pos(left, top, 0.0);
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawRectNoColor(double left, double top, double right, double bottom) {
        double var5;
        if (left < right) {
            var5 = left;
            left = right;
            right = var5;
        }
        if (top < bottom) {
            var5 = top;
            top = bottom;
            bottom = var5;
        }
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        worldRenderer.begin(7, worldRenderer.getVertexFormat());
        worldRenderer.pos(left, bottom, 0.0);
        worldRenderer.pos(right, bottom, 0.0);
        worldRenderer.pos(right, top, 0.0);
        worldRenderer.pos(left, top, 0.0);
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void begin2D() {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
    }

    public static void end2D() {
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    public static void drawMultiRainbowRect(float left, float top, float right, float bottom) {
        int i2 = 0;
        while ((float)i2 < right - left) {
            RenderUtil.drawRect(left + (float)i2, top, left + (float)i2 + 1.0f, bottom, RenderUtil.rainbow(i2 * 10).getRGB());
            ++i2;
        }
    }

    public static Color rainbow(int delay) {
        double state = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 20.0);
        return Color.getHSBColor((float)((state %= 360.0) / 360.0), 1.0f, 1.0f);
    }

    public static void drawCircle(double x2, double y2, float radius, int color) {
        float alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        float red = (float)(color >> 16 & 0xFF) / 255.0f;
        float green = (float)(color >> 8 & 0xFF) / 255.0f;
        float blue = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
        RenderUtil.begin2D();
        GL11.glBegin(9);
        for (int i2 = 0; i2 <= 360; ++i2) {
            GL11.glVertex2d(x2 + Math.sin((double)i2 * 3.141526 / 180.0) * (double)radius, y2 + Math.cos((double)i2 * 3.141526 / 180.0) * (double)radius);
        }
        GL11.glEnd();
        RenderUtil.end2D();
    }

    public static void drawGradientRect(float x2, float y2, float width, float height, Color left, Color right) {
        Scope.enable(3042).scope(() -> Scope.disable(2929, 3553, 3008).scope(() -> {
            GL11.glBlendFunc(770, 771);
            GL11.glShadeModel(7425);
            GL11.glPushMatrix();
            GL11.glBegin(6);
            GL11.glColor4f((float)left.getRed() / 255.0f, (float)left.getGreen() / 255.0f, (float)left.getBlue() / 255.0f, left.getAlpha());
            GL11.glVertex2f(x2, y2);
            GL11.glVertex2f(x2, y2 + height);
            GL11.glColor4f((float)right.getRed() / 255.0f, (float)right.getGreen() / 255.0f, (float)right.getBlue() / 255.0f, right.getAlpha());
            GL11.glVertex2f(x2 + width, y2 + height);
            GL11.glVertex2f(x2 + width, y2);
            GL11.glEnd();
            GL11.glPopMatrix();
        }));
    }

    public static void drawGradientRectVertical(float x2, float y2, float width, float height, Color up2, Color down) {
        Scope.enable(3042, 3008).scope(() -> Scope.disable(2929, 3553).scope(() -> {
            GL11.glBlendFunc(770, 771);
            GL11.glShadeModel(7425);
            GL11.glPushMatrix();
            GL11.glBegin(6);
            GL11.glColor4f((float)up2.getRed() / 255.0f, (float)up2.getGreen() / 255.0f, (float)up2.getBlue() / 255.0f, up2.getAlpha());
            GL11.glVertex2f(x2, y2);
            GL11.glColor4f((float)down.getRed() / 255.0f, (float)down.getGreen() / 255.0f, (float)down.getBlue() / 255.0f, down.getAlpha());
            GL11.glVertex2f(x2, y2 + height);
            GL11.glVertex2f(x2 + width, y2 + height);
            GL11.glColor4f((float)up2.getRed() / 255.0f, (float)up2.getGreen() / 255.0f, (float)up2.getBlue() / 255.0f, up2.getAlpha());
            GL11.glVertex2f(x2 + width, y2);
            GL11.glEnd();
            GL11.glPopMatrix();
        }));
    }

    public static Color fade(Color color, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0f + 100.0f / (float)count * 2.0f) % 2.0f - 1.0f);
        brightness = 0.5f + 0.5f * brightness;
        hsb[2] = brightness % 2.0f;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static void drawFullCircle(float cx2, float cy2, float r2, int c2) {
        r2 *= 2.0f;
        cx2 *= 2.0f;
        cy2 *= 2.0f;
        float theta = 0.19634953f;
        float p2 = (float)Math.cos(theta);
        float s2 = (float)Math.sin(theta);
        float x2 = r2;
        float y2 = 0.0f;
        RenderUtil.enableGL2D();
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glEnable(3024);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RenderUtil.glColor(c2);
        GL11.glBegin(9);
        for (int ii2 = 0; ii2 < 32; ++ii2) {
            GL11.glVertex2f(x2 + cx2, y2 + cy2);
            float t2 = x2;
            x2 = p2 * x2 - s2 * y2;
            y2 = s2 * t2 + p2 * y2;
        }
        GL11.glEnd();
        GL11.glScalef(2.0f, 2.0f, 2.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        RenderUtil.disableGL2D();
    }

    public static void pre3D() {
        GL11.glPushMatrix();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glShadeModel(7425);
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glDisable(2929);
        GL11.glDisable(2896);
        GL11.glDepthMask(false);
        GL11.glHint(3154, 4354);
    }

    public static void post3D() {
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public static void drawLine3D(float x2, float y2, float z2, float x1, float y1, float z1, int color) {
        RenderUtil.pre3D();
        GL11.glLoadIdentity();
        Minecraft.getMinecraft().entityRenderer.orientCamera(Minecraft.getMinecraft().timer.renderPartialTicks);
        float var11 = (float)(color >> 24 & 0xFF) / 255.0f;
        float var6 = (float)(color >> 16 & 0xFF) / 255.0f;
        float var7 = (float)(color >> 8 & 0xFF) / 255.0f;
        float var8 = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f(var6, var7, var8, var11);
        GL11.glLineWidth(0.5f);
        GL11.glBegin(3);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glVertex3d(x1, y1, z1);
        GL11.glEnd();
        RenderUtil.post3D();
    }

    public static void draw3DLine(float x2, float y2, float z2, int color) {
        RenderUtil.pre3D();
        GL11.glLoadIdentity();
        Minecraft.getMinecraft().entityRenderer.orientCamera(Minecraft.getMinecraft().timer.renderPartialTicks);
        float var11 = (float)(color >> 24 & 0xFF) / 255.0f;
        float var6 = (float)(color >> 16 & 0xFF) / 255.0f;
        float var7 = (float)(color >> 8 & 0xFF) / 255.0f;
        float var8 = (float)(color & 0xFF) / 255.0f;
        GL11.glColor4f(var6, var7, var8, var11);
        GL11.glLineWidth(1.5f);
        GL11.glBegin(3);
        GL11.glVertex3d(0.0, Minecraft.getMinecraft().thePlayer.getEyeHeight(), 0.0);
        GL11.glVertex3d(x2, y2, z2);
        GL11.glEnd();
        RenderUtil.post3D();
    }

    public static void filledBox(AxisAlignedBB boundingBox, int color, boolean shouldColor) {
        GlStateManager.pushMatrix();
        float var11 = (float)(color >> 24 & 0xFF) / 255.0f;
        float var6 = (float)(color >> 16 & 0xFF) / 255.0f;
        float var7 = (float)(color >> 8 & 0xFF) / 255.0f;
        float var8 = (float)(color & 0xFF) / 255.0f;
        WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
        if (shouldColor) {
            GlStateManager.color(var6, var7, var8, var11);
        }
        int draw = 7;
        worldRenderer.begin(draw, worldRenderer.getVertexFormat());
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        Tessellator.getInstance().draw();
        worldRenderer.begin(draw, worldRenderer.getVertexFormat());
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        Tessellator.getInstance().draw();
        worldRenderer.begin(draw, worldRenderer.getVertexFormat());
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        Tessellator.getInstance().draw();
        worldRenderer.begin(draw, worldRenderer.getVertexFormat());
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        Tessellator.getInstance().draw();
        worldRenderer.begin(draw, worldRenderer.getVertexFormat());
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        Tessellator.getInstance().draw();
        worldRenderer.begin(draw, worldRenderer.getVertexFormat());
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        Tessellator.getInstance().draw();
        GlStateManager.depthMask(true);
        GlStateManager.popMatrix();
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB boundingBox) {
        Tessellator var1 = Tessellator.getInstance();
        WorldRenderer var2 = var1.getWorldRenderer();
        var2.begin(3, var2.getVertexFormat());
        var2.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        var2.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        var2.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        var2.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        var2.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        var1.draw();
        var2.begin(3, var2.getVertexFormat());
        var2.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        var2.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        var2.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        var2.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        var2.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        var1.draw();
        var2.begin(1, var2.getVertexFormat());
        var2.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        var2.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        var2.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        var2.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        var2.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        var2.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        var2.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        var2.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        var1.draw();
    }

    public static void drawLines(AxisAlignedBB boundingBox) {
        GL11.glPushMatrix();
        GL11.glBegin(2);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.minY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.minY, boundingBox.maxZ);
        GL11.glVertex3d(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ);
        GL11.glVertex3d(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public static void drawEntityOnScreen(double d2, double e2, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)d2, (float)e2, 50.0f);
        GlStateManager.scale(-scale, scale, scale);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        float f2 = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f22 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;
        GlStateManager.rotate(135.0f, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-((float)Math.atan(mouseY / 40.0f)) * 20.0f, 1.0f, 0.0f, 0.0f);
        ent.renderYawOffset = (float)Math.atan(mouseX / 40.0f) * 20.0f;
        ent.rotationYaw = (float)Math.atan(mouseX / 40.0f) * 40.0f;
        ent.rotationPitch = -((float)Math.atan(mouseY / 40.0f)) * 20.0f;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        GlStateManager.translate(0.0f, 0.0f, 0.0f);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0f);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(ent, 0.0, 0.0, 0.0, 0.0f, 1.0f);
        rendermanager.setRenderShadow(true);
        ent.renderYawOffset = f2;
        ent.rotationYaw = f1;
        ent.rotationPitch = f22;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    public static void prepareScissorBox(float x2, float y2, float x22, float y22) {
        ScaledResolution scale = new ScaledResolution(Minecraft.getMinecraft());
        int factor = scale.getScaleFactor();
        GL11.glScissor((int)(x2 * (float)factor), (int)(((float)scale.getScaledHeight() - y22) * (float)factor), (int)((x22 - x2) * (float)factor), (int)((y22 - y2) * (float)factor));
    }
}

