/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.render;

import cafe.corrosion.util.nameable.INameable;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import net.minecraft.entity.EntityLivingBase;

public final class ColorUtil {
    public static int getColor(ColorMode colorMode, Color defaultValue, int position) {
        int color = 0;
        switch (colorMode) {
            case STATIC: {
                color = defaultValue.getRGB();
                break;
            }
            case RAINBOW_FAST: {
                color = ColorUtil.astolfoColors(-6000, position * 6);
                break;
            }
            case SLOW_RAINBOW: {
                color = ColorUtil.astolfoColors(-6000, position * 3);
                break;
            }
            case RAINBOW_SIMPLE: {
                color = ColorUtil.rainbow(6000, position * 15);
                break;
            }
            case FADE: {
                color = RenderUtil.fade(defaultValue, position / 11 * 2 + 10).getRGB();
            }
        }
        return color;
    }

    public static int rainbow(int speed, int timeOffset) {
        float hue = (System.currentTimeMillis() + (long)timeOffset) % (long)speed;
        return Color.HSBtoRGB(hue / ((float)speed / 2.0f), 0.5f, 1.0f);
    }

    public static int astolfoColors(int timeOffset, int yTotal) {
        float hue;
        float speed = 4000.0f;
        for (hue = (float)(System.currentTimeMillis() % 4000L) + (float)((yTotal - timeOffset) * 9); hue > 4000.0f; hue -= 4000.0f) {
        }
        if ((double)(hue /= 4000.0f) > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        return Color.HSBtoRGB(hue += 0.5f, 0.5f, 1.0f);
    }

    public static int getHealthColor(EntityLivingBase entityLivingBase) {
        float percentage = 100.0f * (entityLivingBase.getHealth() / 2.0f / (entityLivingBase.getMaxHealth() / 2.0f));
        return percentage > 75.0f ? 0x19FF19 : (percentage > 50.0f ? 0xFFFF00 : (percentage > 25.0f ? 0xFF5500 : 0xFF0900));
    }

    private ColorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static enum ColorMode implements INameable
    {
        STATIC("Static", false),
        RAINBOW_SIMPLE("Simple Rainbow", true),
        RAINBOW_FAST("Fast Rainbow", true),
        SLOW_RAINBOW("Slow Rainbow", true),
        FADE("Fade", false);

        private final String name;
        private final boolean rainbow;

        private ColorMode(String name, boolean rainbow) {
            this.name = name;
            this.rainbow = rainbow;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public boolean isRainbow() {
            return this.rainbow;
        }
    }
}

