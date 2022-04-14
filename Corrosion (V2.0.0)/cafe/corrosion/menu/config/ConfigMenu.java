/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.config;

import cafe.corrosion.Corrosion;
import cafe.corrosion.config.base.Config;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.animation.Animation;
import cafe.corrosion.menu.animation.impl.CubicEaseAnimation;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.Blurrer;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import java.util.Map;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.opengl.GL11;

public class ConfigMenu
extends GuiScreen {
    private static final TTFFontRenderer RENDERER = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.UBUNTU, 24.0f);
    private static final TTFFontRenderer SMALL_RENDERER = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 16.0f);
    private static final int BACKGROUND_COLOR = new Color(20, 20, 20).getRGB();
    private static final int WHITE = Color.WHITE.getRGB();
    private static final int CONFIG_BACKGROUND = new Color(33, 33, 33).getRGB();
    private static final int RED = new Color(48, 0, 92).getRGB();
    private final Animation animation = new CubicEaseAnimation(250L);

    public ConfigMenu() {
        this.animation.start(false);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float progress = 1.0f;
        float transformX = 0.0f;
        float transformY = 0.0f;
        if (this.animation.isAnimating()) {
            progress = (float)this.animation.calculate();
            GL11.glScalef(progress, progress, 1.0f);
            transformX = (float)this.mc.displayWidth * (1.0f - progress) / 2.0f;
            transformY = (float)this.mc.displayHeight * (1.0f - progress) / 2.0f;
            GL11.glTranslatef(transformX, transformY, 0.0f);
        }
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int width = 150;
        int height = 150;
        Blurrer blurrer = Corrosion.INSTANCE.getBlurrer();
        blurrer.blur(centerX - width, centerY - height, width, height, true);
        blurrer.bloom(centerX - width, centerY - height, width, height, 15, 200);
        RenderUtil.drawRoundedRect(centerX - width, centerY - height, centerX + width, centerY + height, BACKGROUND_COLOR);
        this.drawText(centerX, centerY - 130);
        Map<String, Config> configs = Corrosion.INSTANCE.getConfigManager().getConfigurations();
        int startX = centerX - width + 20;
        int startY = centerY - height + 40;
        for (Map.Entry<String, Config> entry : configs.entrySet()) {
            Config config = entry.getValue();
            this.drawConfig(startX, startY, entry.getKey(), config.getAuthor(), config.getClientVersion());
        }
        GL11.glScalef(1.0f / progress, 1.0f / progress, 1.0f);
        GL11.glTranslatef(-transformX, -transformY, 0.0f);
    }

    private void drawText(int centerX, int posY) {
        String text = "Manage Configs";
        float modifier = RENDERER.getWidth(text) / 2.0f;
        RENDERER.drawString(text, (float)centerX - modifier, posY, WHITE);
    }

    private void drawConfig(int posX, int posY, String name, String author, String version) {
        int expandX = 85;
        int expandY = 50;
        int secondBoxY = posY + expandY - 2;
        RenderUtil.drawRoundedRect(posX, posY, posX + expandX, posY + expandY, CONFIG_BACKGROUND);
        RenderUtil.drawRoundedRect(posX, secondBoxY, posX + expandX, secondBoxY + 20, RED);
        TTFFontRenderer renderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 19.0f);
        String text = "Select Config";
        float width = renderer.getWidth(text);
        float height = renderer.getHeight(text);
        int centerX = (int)((float)(posX + expandX / 2) - width / 2.0f);
        int centerY = (int)((float)(secondBoxY + 10) - height / 2.0f);
        renderer.drawString(text, centerX, centerY, WHITE);
        TTFFontRenderer boldRenderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.UBUNTU, 20.0f);
        float nameWidth = boldRenderer.getWidth(name);
        boldRenderer.drawStringWithShadow(name, (float)posX + (float)expandX / 2.0f - nameWidth / 2.0f, posY + 3, WHITE);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
    }
}

