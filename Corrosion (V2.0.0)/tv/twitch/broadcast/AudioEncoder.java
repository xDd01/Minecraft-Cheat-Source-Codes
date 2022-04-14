/*
 * Decompiled with CFR 0.152.
 */
package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum AudioEncoder {
    TTV_AUD_ENC_DEFAULT(-1),
    TTV_AUD_ENC_LAMEMP3(0),
    TTV_AUD_ENC_APPLEAAC(1);

    private static Map<Integer, AudioEncoder> s_Map;
    private int m_Value;

    public static AudioEncoder lookupValue(int n2) {
        AudioEncoder audioEncoder = s_Map.get(n2);
        return audioEncoder;
    }

    private AudioEncoder(int n3) {
        this.m_Value = n3;
    }

    public int getValue() {
        return this.m_Value;
    }

    static {
        s_Map = new HashMap<Integer, AudioEncoder>();
        EnumSet<AudioEncoder> enumSet = EnumSet.allOf(AudioEncoder.class);
        for (AudioEncoder audioEncoder : enumSet) {
            s_Map.put(audioEncoder.getValue(), audioEncoder);
        }
    }
}

