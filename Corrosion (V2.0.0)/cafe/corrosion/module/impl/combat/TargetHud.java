/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.combat;

import cafe.corrosion.Corrosion;
import cafe.corrosion.component.draggable.IDraggable;
import cafe.corrosion.event.impl.EventPacketOut;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.animation.Animation;
import cafe.corrosion.menu.animation.impl.CubicEaseAnimation;
import cafe.corrosion.menu.animation.impl.ExpandAnimation;
import cafe.corrosion.menu.drag.data.HudComponentProxy;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.Blurrer;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import org.lwjgl.opengl.GL11;

@ModuleAttributes(name="TargetHUD", description="Displays information about the person you're fighting", category=Module.Category.VISUAL, defaultModule=true)
public class TargetHud
extends Module
implements IDraggable {
    private final TTFFontRenderer renderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.PRODUCT_SANS, 20.0f);
    private final Blurrer blurrer = Corrosion.INSTANCE.getBlurrer();
    private final Animation animation = new CubicEaseAnimation(500L);
    private final Animation inAnimation = new ExpandAnimation(0.0f, 0.0f);
    private EntityLivingBase lastTarget;
    private long lastAttack;
    private double lastRatio;
    private int lastBarWidth;

    public TargetHud() {
        this.registerEventHandler(EventPacketOut.class, event -> {
            if (event.getPacket() instanceof C02PacketUseEntity) {
                C02PacketUseEntity packet = (C02PacketUseEntity)event.getPacket();
                Entity entity = TargetHud.mc.theWorld.getEntityByID(packet.getEntityId());
                if (!(entity instanceof EntityLivingBase)) {
                    return;
                }
                if (packet.getAction() != C02PacketUseEntity.Action.ATTACK) {
                    return;
                }
                this.lastTarget = (EntityLivingBase)entity;
                this.lastAttack = System.currentTimeMillis();
            } else if (event.getPacket() instanceof C03PacketPlayer && System.currentTimeMillis() - this.lastAttack > 7500L) {
                this.lastTarget = null;
            }
        });
        Corrosion.INSTANCE.getGuiComponentManager().register(this, 250, 200, 250, 50);
    }

    @Override
    public void render(HudComponentProxy component, ScaledResolution scaledResolution, int posX, int posY, int expandX, int expandY) {
        int totalLength;
        if (this.lastTarget == null || this.lastTarget.isDead) {
            this.lastBarWidth = 140;
            return;
        }
        double healthRatio = this.lastTarget.getHealth() / this.lastTarget.getMaxHealth();
        if (healthRatio < 0.05) {
            return;
        }
        GL11.glPushMatrix();
        this.blurrer.bloom(posX, posY, expandX, expandY, 15, 200);
        this.blurrer.blur(posX, posY, expandX, expandY, true);
        RenderUtil.drawRoundedRect(posX, posY, posX + expandX, posY + expandY, new Color(20, 20, 20, 175).getRGB());
        RenderUtil.drawEntityOnScreen(posX + 17, posY + 45, 25, 20.0f, 20.0f, this.lastTarget);
        String name = this.lastTarget.getName();
        if (healthRatio != this.lastRatio && !this.animation.isAnimating()) {
            this.animation.start(this.lastRatio < healthRatio);
        }
        int length = totalLength = 140;
        if (this.animation.isAnimating()) {
            double previousTotal = this.lastBarWidth;
            double currentTotal = (double)totalLength * healthRatio;
            if (!this.animation.isInverted()) {
                double progress = 1.0 - this.animation.calculate();
                int offset = (int)Math.abs(previousTotal - currentTotal);
                int min = (int)Math.min(previousTotal, currentTotal);
                length = Math.min(totalLength, min + (int)((double)offset * progress));
            } else {
                double progress = this.animation.calculate();
                int offset = (int)Math.abs(previousTotal - currentTotal);
                int min = (int)Math.min(previousTotal, currentTotal);
                length = Math.min(totalLength, min + (int)((double)offset * progress));
            }
        } else {
            this.lastBarWidth = length = (int)Math.min((double)totalLength, (double)length * healthRatio);
        }
        int red = Math.min(255, (int)(255.0 * Math.abs(1.0 - healthRatio)));
        int green = Math.min(255, (int)(255.0 * healthRatio));
        int color = new Color(red, green, 0).getRGB();
        RenderUtil.drawRoundedRect(posX + 100, posY + 25, posX + 100 + totalLength, posY + 35, new Color(10, 10, 10, 200).getRGB());
        RenderUtil.drawRoundedRect(posX + 100, posY + 25, posX + 100 + length, posY + 35, color);
        boolean winning = (double)(TargetHud.mc.thePlayer.getHealth() / TargetHud.mc.thePlayer.getMaxHealth()) >= healthRatio || !(this.lastTarget instanceof EntityPlayer) && !(this.lastTarget instanceof EntityMob);
        this.renderer.drawStringWithShadow(name, posX + 33, posY + 15, Color.WHITE.getRGB());
        this.renderer.drawStringWithShadow(winning ? "Winning" : "Losing", posX + 33, posY + 25, winning ? Color.GREEN.getRGB() : Color.RED.getRGB());
        GL11.glPopMatrix();
        this.lastRatio = healthRatio;
    }
}

