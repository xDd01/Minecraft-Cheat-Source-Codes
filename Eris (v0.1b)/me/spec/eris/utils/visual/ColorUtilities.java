package me.spec.eris.utils.visual;

import java.awt.*;
import java.util.Random;

public class ColorUtilities {

    public static int createGermanColor() {
        Random random = new Random();
        return Color.getHSBColor(random.nextFloat(), (random.nextInt(2000) + 4000) / 10000f, 1.0f).getRGB();
    }

    public static int getRainbow(int speed, int offset, double rainSpeed, double rainOffset, float saturation, float brightness) {
        float hue = (float) ((System.currentTimeMillis() * rainSpeed + offset / rainOffset) % speed * 2);
        hue /= speed;
        return Color.getHSBColor(hue, saturation, brightness).getRGB();
    }

    public static Color fade(long offset, float fade) {
        float hue = (float) (System.nanoTime() + offset) / 1.0E10F % 1.0F;
        long color = Long.parseLong(Integer.toHexString(Color.HSBtoRGB(hue, 1.0f, 1.0F)),
                16);
        Color c = new Color((int) color);
        return new Color(c.getRed() / 255.0F * fade, c.getGreen() / 255.0F * fade, c.getBlue() / 255.0F * fade,
                c.getAlpha() / 155.0F);
    }
}
