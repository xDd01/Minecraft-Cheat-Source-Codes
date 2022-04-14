package tv.twitch.broadcast;

import java.util.*;

public enum AudioDeviceType
{
    TTV_PLAYBACK_DEVICE(0), 
    TTV_RECORDER_DEVICE(1), 
    TTV_PASSTHROUGH_DEVICE(2), 
    TTV_DEVICE_NUM(3);
    
    private static Map<Integer, AudioDeviceType> s_Map;
    private int m_Value;
    
    public static AudioDeviceType lookupValue(final int n) {
        return AudioDeviceType.s_Map.get(n);
    }
    
    private AudioDeviceType(final int value) {
        this.m_Value = value;
    }
    
    public int getValue() {
        return this.m_Value;
    }
    
    static {
        AudioDeviceType.s_Map = new HashMap<Integer, AudioDeviceType>();
        for (final AudioDeviceType audioDeviceType : EnumSet.allOf(AudioDeviceType.class)) {
            AudioDeviceType.s_Map.put(audioDeviceType.getValue(), audioDeviceType);
        }
    }
}
