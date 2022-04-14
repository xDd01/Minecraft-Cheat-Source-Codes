/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.visual;

import cafe.corrosion.event.impl.EventPlayerRender;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.ColorProperty;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.util.esp.mc.TessellatorModel;
import cafe.corrosion.util.nameable.INameable;
import java.awt.Color;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import org.lwjgl.opengl.GL11;

@ModuleAttributes(name="ESP", description="Allows you to always see entities", tMobileName="Coinbase Log Finder", category=Module.Category.VISUAL)
public class ESP
extends Module {
    private final EnumProperty<ESPMode> modeProperty = new EnumProperty((Module)this, "Mode", (INameable[])ESPMode.values());
    private final ColorProperty colorProperty = new ColorProperty((Module)this, "Color", Color.RED);
    private final BooleanProperty chams = new BooleanProperty(this, "Chams");
    private final TessellatorModel sussyModel = new TessellatorModel("/assets/minecraft/corrosion/esp/sussy/sussy.obj");
    private final TessellatorModel hitlerHead = new TessellatorModel("/assets/minecraft/corrosion/esp/hitler/head.obj");
    private final TessellatorModel hitlerBody = new TessellatorModel("/assets/minecraft/corrosion/esp/hitler/body.obj");

    public ESP() {
        this.colorProperty.setHidden(() -> this.modeProperty.getValue() != ESPMode.SUSSY);
        this.registerEventHandler(EventPlayerRender.class, event -> {
            switch ((ESPMode)this.modeProperty.getValue()) {
                case SUSSY: {
                    float yaw;
                    GlStateManager.pushAttrib();
                    GlStateManager.pushMatrix();
                    AbstractClientPlayer entity = event.getPlayer();
                    event.setCancelled(true);
                    RenderManager manager = mc.getRenderManager();
                    double x2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)ESP.mc.timer.renderPartialTicks - manager.renderPosX;
                    double y2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)ESP.mc.timer.renderPartialTicks - manager.renderPosY;
                    double z2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)ESP.mc.timer.renderPartialTicks - manager.renderPosZ;
                    float f2 = yaw = entity == ESP.mc.thePlayer ? ESP.mc.thePlayer.prevRotationYawHead + (ESP.mc.thePlayer.rotationYawHead - ESP.mc.thePlayer.prevRotationYawHead) * 1.0f : entity.rotationYaw;
                    if (((Boolean)this.chams.getValue()).booleanValue()) {
                        GlStateManager.doPolygonOffset(1.0f, -999999.0f);
                        GL11.glEnable(32823);
                    }
                    GL11.glTranslated(x2, y2, z2);
                    GL11.glRotatef(-yaw, 0.0f, entity.height, 0.0f);
                    boolean sneak = ESP.mc.thePlayer.isSneaking();
                    GlStateManager.scale(0.7, sneak ? 0.6 : 0.7, 0.7);
                    ESP.glColor((Color)this.colorProperty.getValue());
                    if (entity.hurtTime > 0) {
                        GlStateManager.color(1.0f, 0.3f, 0.3f, 1.0f);
                    }
                    this.sussyModel.render();
                    if (((Boolean)this.chams.getValue()).booleanValue()) {
                        GlStateManager.doPolygonOffset(1.0f, 999999.0f);
                        GL11.glDisable(32823);
                    }
                    GlStateManager.enableLighting();
                    GlStateManager.popAttrib();
                    GlStateManager.popMatrix();
                    GlStateManager.resetColor();
                    break;
                }
                case HITLER: {
                    float yaw;
                    GlStateManager.pushAttrib();
                    GlStateManager.pushMatrix();
                    AbstractClientPlayer entity = event.getPlayer();
                    event.setCancelled(true);
                    RenderManager manager = mc.getRenderManager();
                    double x3 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)ESP.mc.timer.renderPartialTicks - manager.renderPosX;
                    double y3 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)ESP.mc.timer.renderPartialTicks - manager.renderPosY;
                    double z3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)ESP.mc.timer.renderPartialTicks - manager.renderPosZ;
                    float f3 = yaw = entity == ESP.mc.thePlayer ? ESP.mc.thePlayer.prevRotationYawHead + (ESP.mc.thePlayer.rotationYawHead - ESP.mc.thePlayer.prevRotationYawHead) * 1.0f : entity.rotationYaw;
                    if (((Boolean)this.chams.getValue()).booleanValue()) {
                        GlStateManager.doPolygonOffset(1.0f, -999999.0f);
                        GL11.glEnable(32823);
                    }
                    GL11.glTranslated(x3, y3, z3);
                    GL11.glRotatef(-yaw, 0.0f, entity.height, 0.0f);
                    boolean sneak = ESP.mc.thePlayer.isSneaking();
                    GlStateManager.scale(0.03, sneak ? 0.02 : 0.03, 0.03);
                    GlStateManager.disableLighting();
                    if (entity.hurtTime > 0) {
                        GlStateManager.color(1.0f, 0.1f, 0.1f, 1.0f);
                    }
                    this.hitlerHead.render();
                    this.hitlerBody.render();
                    if (((Boolean)this.chams.getValue()).booleanValue()) {
                        GlStateManager.doPolygonOffset(1.0f, 999999.0f);
                        GL11.glDisable(32823);
                    }
                    GlStateManager.enableLighting();
                    GlStateManager.popAttrib();
                    GlStateManager.popMatrix();
                    GlStateManager.resetColor();
                    break;
                }
            }
        });
    }

    private static void glColor(Color color) {
        float red = (float)color.getRed() / 255.0f;
        float green = (float)color.getGreen() / 255.0f;
        float blue = (float)color.getBlue() / 255.0f;
        float alpha = (float)color.getAlpha() / 255.0f;
        GL11.glColor4f(red, green, blue, alpha);
    }

    private static Color rainbow(int delay) {
        double state = Math.ceil((double)(System.currentTimeMillis() + (long)delay) / 20.0);
        return Color.getHSBColor((float)((state %= 360.0) / 360.0), 1.0f, 1.0f);
    }

    @Override
    public String getMode() {
        return ((ESPMode)this.modeProperty.getValue()).getName();
    }

    public static enum ESPMode implements INameable
    {
        HITLER("Hitler"),
        SUSSY("Sussy Amongus");

        private final String name;

        @Override
        public String getName() {
            return this.name;
        }

        private ESPMode(String name) {
            this.name = name;
        }
    }
}

