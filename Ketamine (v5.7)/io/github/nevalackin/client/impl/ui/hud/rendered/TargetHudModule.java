package io.github.nevalackin.client.impl.ui.hud.rendered;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.api.ui.cfont.CustomFontRenderer;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.event.render.overlay.RenderGameOverlayEvent;
import io.github.nevalackin.client.impl.module.combat.rage.Aura;
import io.github.nevalackin.client.impl.ui.hud.components.HudComponent;
import io.github.nevalackin.client.util.render.BloomUtil;
import io.github.nevalackin.client.util.render.BlurUtil;
import io.github.nevalackin.client.util.render.ColourUtil;
import io.github.nevalackin.client.util.render.DrawUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import java.text.DecimalFormat;

import static net.minecraft.client.gui.Gui.drawScaledCustomSizeModalRect;
import static org.lwjgl.opengl.GL11.*;

public class TargetHudModule extends Module implements HudComponent {

    private Aura aura;
    private double xPos, yPos;
    private double width, height;
    private double healthBarWidth;
    private boolean dragon;
    private DecimalFormat format = new DecimalFormat("0.0");
    private DecimalFormat format0 = new DecimalFormat("0");


    public TargetHudModule() {
        super("TargetHud", Category.RENDER, Category.SubCategory.RENDER_OVERLAY);
        this.setX(405.0f);
        this.setY(390.0f);
    }

    @EventLink
    private final Listener<RenderGameOverlayEvent> onRenderOverlay = event -> {
        this.render(event.getScaledResolution().getScaledWidth(), event.getScaledResolution().getScaledHeight(), event.getPartialTicks());
    };

    @Override
    public boolean isDragging() {
        return dragon;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragon = dragging;
    }

    @Override
    public void render(int scaledWidth, int scaledHeight, double tickDelta) {
        EntityLivingBase target = aura.getTarget();

        if (target == null && mc.currentScreen == KetamineClient.getInstance().getDropdownGUI()) {
            target = mc.thePlayer;
        }

        if (target == null) {
            return;
        }

        this.fitInScreen(scaledWidth, scaledHeight);

        double hpClamped = target.getHealth() / target.getMaxHealth();
        hpClamped = MathHelper.clamp_double(hpClamped, 0.0, 1.0);

        final double hpWidth = 80 * hpClamped;
        healthBarWidth = DrawUtil.animateProgress(healthBarWidth, hpWidth, 75.f);
        double healthAnimatedPercent = 20.0 * (healthBarWidth / 80.0) * 5.0;

        final CustomFontRenderer fontRenderer = KetamineClient.getInstance().getFontRenderer();
        final int startColour = ColourUtil.fadeBetween(ColourUtil.getClientColour(), ColourUtil.getSecondaryColour(), 0);
        final int endColour = ColourUtil.fadeBetween(ColourUtil.getSecondaryColour(), ColourUtil.getClientColour(), 250);

        double x = this.getX();
        double y = this.getY();
        double margin = 2.0f;

        String text = "Distance: " + format0.format(target.getDistanceToEntity(mc.thePlayer)) + " | Hurt: " + format.format(target.hurtTime);

        double heightBounds = fontRenderer.getHeight(text);

        double width = 85 + margin * 2.0f;
        double height = heightBounds + margin * 2.0f;

        this.width = width + 70;
        this.height = height + 30;

        setWidth(this.width);
        setHeight(this.height);

        // Draw TargetHud
        {
            // Blur Background Rect
            BlurUtil.blurArea(x, y + 1, width + 70, height + 28);

            // Top Gradient Bar
            BloomUtil.drawAndBloom(() -> DrawUtil.glDrawSidewaysGradientRect(x, y, width + 70, 1, startColour, endColour));

            // Background Rounded Rect - 60% Opacity
            DrawUtil.glDrawFilledQuad(x, y + 1, (float) width + 70, (float) (height + 28), 0x60 << 24);

            // Draw Face
            if (target instanceof EntityPlayer) {
                drawFace(x + 3, y + 5, 33, 33, (AbstractClientPlayer) target);
            }

            // Target Name
            mc.fontRendererObj.drawStringWithShadow(target.getName(), x + 39f, y + 6f, 0xFFFFFFFF);
            // Target Distance
            fontRenderer.drawWithShadow(text, x + 39f, y + 17f, .5, 0xFF808080);

            // Health Bar
            BloomUtil.drawAndBloom(() -> DrawUtil.glDrawSidewaysGradientRect(x + 39, y + 30, (float) healthBarWidth, 6.75f, startColour, endColour));
            // Draw Health Value
            fontRenderer.drawWithShadow(format.format(healthAnimatedPercent) + "%", x + healthBarWidth + hpClamped + 41, y + 28.5, .5, 0xFFFFFFFF);
        }
    }

    private void drawFace(double x, double y, double width, double height, AbstractClientPlayer target) {
        ResourceLocation skin = target.getLocationSkin();
        mc.getTextureManager().bindTexture(skin);
        glEnable(GL_BLEND);
        glColor4f(255, 255, 255, 1);

        final float hurtTimePercentage = (target.hurtTime - mc.getTimer().renderPartialTicks) / target.maxHurtTime;

        if (hurtTimePercentage > 0) {
            x += 1 * hurtTimePercentage;
            y += 1 * hurtTimePercentage;
            height -= 2 * hurtTimePercentage;
            width -= 2 * hurtTimePercentage;
        }

        drawScaledCustomSizeModalRect(x, y, 8, 8, 8, 8, width, height, 64, 64);

        if (hurtTimePercentage > 0.0) {
            glTranslated(x, y, 0);
            glDisable(GL_TEXTURE_2D);
            final boolean restore = DrawUtil.glEnableBlend();
            glShadeModel(GL_SMOOTH);
            glDisable(GL_ALPHA_TEST);

            final float lineWidth = 10.f;
            glLineWidth(lineWidth);

            final int fadeOutColour = ColourUtil.fadeTo(0x00000000, ColourUtil.blendHealthColours(target.getHealth() / target.getMaxHealth()), hurtTimePercentage);

            glBegin(GL_QUADS);
            {
                // Left
                DrawUtil.glColour(fadeOutColour);
                glVertex2d(0, 0);
                glVertex2d(0, height);
                DrawUtil.glColour(0x00FF0000);
                glVertex2d(lineWidth, height - lineWidth);
                glVertex2d(lineWidth, lineWidth);

                // Right
                DrawUtil.glColour(0x00FF0000);
                glVertex2d(width - lineWidth, lineWidth);
                glVertex2d(width - lineWidth, height - lineWidth);
                DrawUtil.glColour(fadeOutColour);
                glVertex2d(width, height);
                glVertex2d(width, 0);

                // Top
                DrawUtil.glColour(fadeOutColour);
                glVertex2d(0, 0);
                DrawUtil.glColour(0x00FF0000);
                glVertex2d(lineWidth, lineWidth);
                glVertex2d(width - lineWidth, lineWidth);
                DrawUtil.glColour(fadeOutColour);
                glVertex2d(width, 0);

                // Bottom
                DrawUtil.glColour(0x00FF0000);
                glVertex2d(lineWidth, height - lineWidth);
                DrawUtil.glColour(fadeOutColour);
                glVertex2d(0, height);
                glVertex2d(width, height);
                DrawUtil.glColour(0x00FF0000);
                glVertex2d(width - lineWidth, height - lineWidth);
            }
            glEnd();

            glEnable(GL_ALPHA_TEST);
            glShadeModel(GL_FLAT);
            DrawUtil.glRestoreBlend(restore);
            glEnable(GL_TEXTURE_2D);
            glTranslated(-x, -y, 0);
        }
    }

    @Override
    public void onEnable() {
        if (aura == null) {
            aura = KetamineClient.getInstance().getModuleManager().getModule(Aura.class);
        }
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void setX(double x) {
        this.xPos = x;
    }

    @Override
    public void setY(double y) {
        this.yPos = y;
    }

    @Override
    public double getX() {
        return this.xPos;
    }

    @Override
    public double getY() {
        return this.yPos;
    }


    @Override
    public double setWidth(double width) {
        return this.width = width;
    }

    @Override
    public double setHeight(double height) {
        return this.height = height;
    }

    @Override
    public double getWidth() {
        return this.width;
    }

    @Override
    public double getHeight() {
        return this.height;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {

    }
}
