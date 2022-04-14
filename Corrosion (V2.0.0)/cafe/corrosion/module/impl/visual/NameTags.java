/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.visual;

import cafe.corrosion.Corrosion;
import cafe.corrosion.event.impl.Event3DRender;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.ColorProperty;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.GuiUtils;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

@ModuleAttributes(name="NameTags", description="Renders a larger nametag for players", category=Module.Category.VISUAL)
public class NameTags
extends Module {
    private static final TTFFontRenderer RENDERER = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 33.0f);
    private static final int BACKGROUND = new Color(20, 20, 20, 150).getRGB();
    private static final int TEXT_COLOR = new Color(148, 148, 148).getRGB();
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.#");
    private final BooleanProperty showHealth = new BooleanProperty((Module)this, "Show Health Bar", true);
    private final ColorProperty healthBarColor = (ColorProperty)new ColorProperty((Module)this, "Health Bar", new Color(232, 53, 53)).setHidden(() -> (Boolean)this.showHealth.getValue() == false);

    public NameTags() {
        this.registerEventHandler(Event3DRender.class, event -> {
            if (NameTags.mc.theWorld == null || NameTags.mc.thePlayer == null) {
                return;
            }
            boolean showHealth = (Boolean)this.showHealth.getValue();
            NameTags.mc.theWorld.loadedEntityList.stream().filter(entity -> entity instanceof EntityPlayer && !entity.equals(NameTags.mc.thePlayer)).forEach(entity -> {
                float distance;
                EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                double x2 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)NameTags.mc.timer.renderPartialTicks - NameTags.mc.getRenderManager().renderPosX;
                double y2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)NameTags.mc.timer.renderPartialTicks - NameTags.mc.getRenderManager().renderPosY;
                double z2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)NameTags.mc.timer.renderPartialTicks - NameTags.mc.getRenderManager().renderPosZ;
                GL11.glPushMatrix();
                GL11.glBlendFunc(770, 771);
                GL11.glDisable(2929);
                GlStateManager.translate(x2, y2, z2);
                String name = entity.getName();
                double health = entityLivingBase.getHealth() / 2.0f;
                double maxHealth = entityLivingBase.getMaxHealth() / 2.0f;
                double percentage = health / maxHealth;
                String healthColor = percentage > 0.075 ? "2" : (percentage > 0.05 ? "e" : (percentage > 0.025 ? "6" : "4"));
                String healthDisplay = DECIMAL_FORMAT.format(health * 2.0);
                if (showHealth) {
                    name = String.format("%s \u00a7%s%s", name, healthColor, healthDisplay);
                }
                float var13 = (distance = NameTags.mc.thePlayer.getDistanceToEntity((Entity)entity)) / 5.0f <= 2.0f ? 2.0f : distance / 4.0f;
                float var14 = 0.016666668f * var13;
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0f, entity.height + 0.5f, 0.0f);
                if (NameTags.mc.gameSettings.thirdPersonView == 2) {
                    GlStateManager.rotate(-NameTags.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                    GlStateManager.rotate(NameTags.mc.getRenderManager().playerViewX, -1.0f, 0.0f, 0.0f);
                } else {
                    GlStateManager.rotate(-NameTags.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
                    GlStateManager.rotate(NameTags.mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
                }
                GlStateManager.scale(-var14 / 4.0f, -var14 / 4.0f, var14 / 4.0f);
                int heightOffset = 0;
                if (entity.isSneaking()) {
                    heightOffset += 4;
                }
                if ((heightOffset = (int)((float)heightOffset - distance / 5.0f)) < -8) {
                    heightOffset = -8;
                }
                int width = (int)(RENDERER.getWidth(name) / 2.0f) + 10;
                int height = heightOffset + 30;
                int posX = -width - 1;
                int expandY = heightOffset;
                int lineExpand = (int)((double)width * percentage) * 2;
                RenderUtil.drawRoundedRect(posX, height, width, expandY, BACKGROUND);
                if (showHealth) {
                    GuiUtils.drawStraightLine(posX, posX + lineExpand, height, 2, ((Color)this.healthBarColor.getValue()).getRGB());
                }
                int textWidth = (int)RENDERER.getWidth(name);
                int textHeight = (int)RENDERER.getHeight(name);
                int centerX = (int)((float)posX + ((float)width / 2.0f - (float)textWidth / 4.0f));
                int centerY = (int)((float)height + (float)expandY / 2.0f);
                RENDERER.drawString(name, centerX, (float)centerY - (float)textHeight * 1.5f, TEXT_COLOR);
                GL11.glPopMatrix();
                GL11.glEnable(2929);
                GL11.glColor3f(1.0f, 1.0f, 1.0f);
                GL11.glPopMatrix();
            });
        });
    }
}

