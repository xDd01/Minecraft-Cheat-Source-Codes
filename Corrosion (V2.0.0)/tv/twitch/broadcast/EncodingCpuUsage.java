/*
 * Decompiled with CFR 0.152.
 */
package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EncodingCpuUsage {
    TTV_ECU_LOW(0),
    TTV_ECU_MEDIUM(1),
    TTV_ECU_HIGH(2);

    private static Map<Integer, EncodingCpuUsage> s_Map;
    private int m_Value;

    public static EncodingCpuUsage lookupValue(int n2) {
        EncodingCpuUsage encodingCpuUsage = s_Map.get(n2);
        return encodingCpuUsage;
    }

    private EncodingCpuUsage(int n3) {
        this.m_Value = n3;
    }

    public int getValue() {
        return this.m_Value;
    }

    static {
        s_Map = new HashMap<Integer, EncodingCpuUsage>();
        EnumSet<EncodingCpuUsage> enumSet = EnumSet.allOf(EncodingCpuUsage.class);
        for (EncodingCpuUsage encodingCpuUsage : enumSet) {
            s_Map.put(encodingCpuUsage.getValue(), encodingCpuUsage);
        }
    }
}

