/*
 * Decompiled with CFR 0.152.
 */
package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum StartFlags {
    None(0),
    TTV_Start_BandwidthTest(1);

    private static Map<Integer, StartFlags> s_Map;
    private int m_Value;

    public static StartFlags lookupValue(int n2) {
        StartFlags startFlags = s_Map.get(n2);
        return startFlags;
    }

    private StartFlags(int n3) {
        this.m_Value = n3;
    }

    public int getValue() {
        return this.m_Value;
    }

    static {
        s_Map = new HashMap<Integer, StartFlags>();
        EnumSet<StartFlags> enumSet = EnumSet.allOf(StartFlags.class);
        for (StartFlags startFlags : enumSet) {
            s_Map.put(startFlags.getValue(), startFlags);
        }
    }
}

