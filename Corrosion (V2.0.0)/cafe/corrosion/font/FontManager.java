/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.font;

import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.util.font.type.FontType;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FontManager {
    private final Map<String, TTFFontRenderer> fonts = new HashMap<String, TTFFontRenderer>();

    public TTFFontRenderer getFontRenderer(FontType fontType, float size) {
        String combined = fontType.getPathName() + ":" + size;
        if (!this.fonts.containsKey(combined)) {
            this.fonts.put(combined, this.createRenderer(fontType.getPathName(), size));
        }
        return this.fonts.get(combined);
    }

    private TTFFontRenderer createRenderer(String path, float size) {
        return new TTFFontRenderer(Font.createFont(0, Objects.requireNonNull(this.getClass().getResourceAsStream("/assets/minecraft/corrosion/fonts/" + path + ".ttf"))).deriveFont(0, size), true);
    }
}

