/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.rank;

import java.awt.Color;

public enum ClientRank {
    USER("User", new Color(223, 240, 227).getRGB()),
    BETA("Beta", new Color(0, 149, 255).getRGB()),
    STAFF("Staff", new Color(0, 107, 207).getRGB()),
    DEVELOPER("Developer", new Color(219, 183, 0).getRGB());

    private final String name;
    private final int color;

    private ClientRank(String name, int color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public int getColor() {
        return this.color;
    }
}

