/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.notification;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.animation.Animation;
import cafe.corrosion.menu.animation.impl.CubicEaseAnimation;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.render.GuiUtils;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;

public class Notification {
    private static final ResourceLocation NOTIFICATION_LOCATION = new ResourceLocation("corrosion/logos/notification.png");
    private static final int GRAY = new Color(33, 33, 33).getRGB();
    private static final int BLUE = new Color(50, 168, 162).getRGB();
    private static final int LIGHT_GRAY = new Color(199, 199, 199).getRGB();
    private final Animation animation = new CubicEaseAnimation(400L);
    private final String header;
    private final String body;
    private long startTime = Long.MAX_VALUE;

    public boolean isCompleted() {
        return System.currentTimeMillis() - this.startTime > 5000L;
    }

    public boolean isActive() {
        return this.startTime != Long.MAX_VALUE && !this.isCompleted();
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        this.animation.start(false);
    }

    public double getProgress() {
        long timeElapsed = System.currentTimeMillis() - this.startTime;
        return (double)timeElapsed / 5000.0;
    }

    public void draw(int entry) {
        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
        int screenMaxX = scaledResolution.getScaledWidth();
        int screenMaxY = scaledResolution.getScaledHeight();
        int minX = screenMaxX - 250;
        int minY = screenMaxY - 65 * entry - 20;
        int maxX = minX + 200;
        int maxY = minY + 45;
        Gui.drawRect(minX, minY, maxX, maxY, GRAY);
        GuiUtils.drawImage(NOTIFICATION_LOCATION, minX + 3, minY + 3, 35.0f, 35.0f, LIGHT_GRAY);
        double percentCompleted = this.getProgress();
        int whole = maxX - minX;
        int toSubtract = (int)((double)whole * percentCompleted);
        GuiUtils.drawStraightLine(minX, maxX - toSubtract, maxY - 1, 1, BLUE);
        TTFFontRenderer fontRenderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 21.0f);
        TTFFontRenderer smallerRenderer = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 18.0f);
        int startPosX = Math.min(minX, maxX) + 85;
        int startPosY = Math.max(minY, maxY) - 36;
        fontRenderer.drawStringWithShadow(this.header, startPosX - 40, startPosY, LIGHT_GRAY);
        smallerRenderer.drawStringWithShadow(this.body, startPosX - 40, startPosY + 14, LIGHT_GRAY);
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public String getHeader() {
        return this.header;
    }

    public String getBody() {
        return this.body;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public Notification(String header, String body) {
        this.header = header;
        this.body = body;
    }
}

