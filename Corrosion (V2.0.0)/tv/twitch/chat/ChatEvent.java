/*
 * Decompiled with CFR 0.152.
 */
package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ChatEvent {
    TTV_CHAT_JOINED_CHANNEL(0),
    TTV_CHAT_LEFT_CHANNEL(1);

    private static Map<Integer, ChatEvent> s_Map;
    private int m_Value;

    public static ChatEvent lookupValue(int n2) {
        ChatEvent chatEvent = s_Map.get(n2);
        return chatEvent;
    }

    private ChatEvent(int n3) {
        this.m_Value = n3;
    }

    public int getValue() {
        return this.m_Value;
    }

    static {
        s_Map = new HashMap<Integer, ChatEvent>();
        EnumSet<ChatEvent> enumSet = EnumSet.allOf(ChatEvent.class);
        for (ChatEvent chatEvent : enumSet) {
            s_Map.put(chatEvent.getValue(), chatEvent);
        }
    }
}

