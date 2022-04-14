package tv.twitch.chat;

import java.util.*;

public enum ChatUserSubscription
{
    TTV_CHAT_USERSUB_NONE(0), 
    TTV_CHAT_USERSUB_SUBSCRIBER(1), 
    TTV_CHAT_USERSUB_TURBO(2);
    
    private static Map<Integer, ChatUserSubscription> s_Map;
    private int m_Value;
    
    public static ChatUserSubscription lookupValue(final int n) {
        return ChatUserSubscription.s_Map.get(n);
    }
    
    private ChatUserSubscription(final int value) {
        this.m_Value = value;
    }
    
    public int getValue() {
        return this.m_Value;
    }
    
    static {
        ChatUserSubscription.s_Map = new HashMap<Integer, ChatUserSubscription>();
        for (final ChatUserSubscription chatUserSubscription : EnumSet.allOf(ChatUserSubscription.class)) {
            ChatUserSubscription.s_Map.put(chatUserSubscription.getValue(), chatUserSubscription);
        }
    }
}
