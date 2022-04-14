/*
 * Decompiled with CFR 0.152.
 */
package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ChatUserMode {
    TTV_CHAT_USERMODE_VIEWER(0),
    TTV_CHAT_USERMODE_MODERATOR(1),
    TTV_CHAT_USERMODE_BROADCASTER(2),
    TTV_CHAT_USERMODE_ADMINSTRATOR(4),
    TTV_CHAT_USERMODE_STAFF(8),
    TTV_CHAT_USERMODE_BANNED(0x40000000);

    private static Map<Integer, ChatUserMode> s_Map;
    private int m_Value;

    public static ChatUserMode lookupValue(int n2) {
        ChatUserMode chatUserMode = s_Map.get(n2);
        return chatUserMode;
    }

    private ChatUserMode(int n3) {
        this.m_Value = n3;
    }

    public int getValue() {
        return this.m_Value;
    }

    static {
        s_Map = new HashMap<Integer, ChatUserMode>();
        EnumSet<ChatUserMode> enumSet = EnumSet.allOf(ChatUserMode.class);
        for (ChatUserMode chatUserMode : enumSet) {
            s_Map.put(chatUserMode.getValue(), chatUserMode);
        }
    }
}

