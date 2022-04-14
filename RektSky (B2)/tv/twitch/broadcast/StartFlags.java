package tv.twitch.broadcast;

import java.util.*;

public enum StartFlags
{
    None(0), 
    TTV_Start_BandwidthTest(1);
    
    private static Map<Integer, StartFlags> s_Map;
    private int m_Value;
    
    public static StartFlags lookupValue(final int n) {
        return StartFlags.s_Map.get(n);
    }
    
    private StartFlags(final int value) {
        this.m_Value = value;
    }
    
    public int getValue() {
        return this.m_Value;
    }
    
    static {
        StartFlags.s_Map = new HashMap<Integer, StartFlags>();
        for (final StartFlags startFlags : EnumSet.allOf(StartFlags.class)) {
            StartFlags.s_Map.put(startFlags.getValue(), startFlags);
        }
    }
}
