/*
 * Decompiled with CFR 0.152.
 */
package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum PixelFormat {
    TTV_PF_BGRA(66051),
    TTV_PF_ABGR(16909056),
    TTV_PF_RGBA(33619971),
    TTV_PF_ARGB(50462976);

    private static Map<Integer, PixelFormat> s_Map;
    private int m_Value;

    public static PixelFormat lookupValue(int n2) {
        PixelFormat pixelFormat = s_Map.get(n2);
        return pixelFormat;
    }

    private PixelFormat(int n3) {
        this.m_Value = n3;
    }

    public int getValue() {
        return this.m_Value;
    }

    static {
        s_Map = new HashMap<Integer, PixelFormat>();
        EnumSet<PixelFormat> enumSet = EnumSet.allOf(PixelFormat.class);
        for (PixelFormat pixelFormat : enumSet) {
            s_Map.put(pixelFormat.getValue(), pixelFormat);
        }
    }
}

