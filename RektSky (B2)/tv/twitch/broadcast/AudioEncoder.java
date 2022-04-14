package tv.twitch.broadcast;

import java.util.*;

public enum AudioEncoder
{
    TTV_AUD_ENC_DEFAULT(-1), 
    TTV_AUD_ENC_LAMEMP3(0), 
    TTV_AUD_ENC_APPLEAAC(1);
    
    private static Map<Integer, AudioEncoder> s_Map;
    private int m_Value;
    
    public static AudioEncoder lookupValue(final int n) {
        return AudioEncoder.s_Map.get(n);
    }
    
    private AudioEncoder(final int value) {
        this.m_Value = value;
    }
    
    public int getValue() {
        return this.m_Value;
    }
    
    static {
        AudioEncoder.s_Map = new HashMap<Integer, AudioEncoder>();
        for (final AudioEncoder audioEncoder : EnumSet.allOf(AudioEncoder.class)) {
            AudioEncoder.s_Map.put(audioEncoder.getValue(), audioEncoder);
        }
    }
}
