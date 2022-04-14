/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.account;

import java.awt.Color;
import java.util.Arrays;

public enum AltAccountType {
    CRACKED("Cracked", new Color(128, 128, 128).getRGB()),
    LEGACY("Legacy", new Color(0, 194, 97).getRGB()),
    MOJANG("Mojang", new Color(250, 97, 97).getRGB()),
    MICROSOFT("Microsoft", new Color(0, 143, 240).getRGB());

    private final String name;
    private final int rgbColor;

    public static AltAccountType of(String name) {
        return Arrays.stream(AltAccountType.values()).filter(value -> value.getName().toLowerCase().equalsIgnoreCase(name)).findFirst().orElse(CRACKED);
    }

    private AltAccountType(String name, int rgbColor) {
        this.name = name;
        this.rgbColor = rgbColor;
    }

    public String getName() {
        return this.name;
    }

    public int getRgbColor() {
        return this.rgbColor;
    }
}

