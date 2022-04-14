package tv.twitch.chat;

import java.util.*;

public enum ChatTokenizationOption
{
    TTV_CHAT_TOKENIZATION_OPTION_NONE(0), 
    TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_URLS(1), 
    TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_TEXTURES(2);
    
    private static Map<Integer, ChatTokenizationOption> s_Map;
    private int m_Value;
    
    public static ChatTokenizationOption lookupValue(final int n) {
        return ChatTokenizationOption.s_Map.get(n);
    }
    
    public static int getNativeValue(final HashSet<ChatTokenizationOption> set) {
        if (set == null) {
            return ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_NONE.getValue();
        }
        int value = ChatTokenizationOption.TTV_CHAT_TOKENIZATION_OPTION_NONE.getValue();
        for (final ChatTokenizationOption chatTokenizationOption : set) {
            if (chatTokenizationOption != null) {
                value |= chatTokenizationOption.getValue();
            }
        }
        return value;
    }
    
    private ChatTokenizationOption(final int value) {
        this.m_Value = value;
    }
    
    public int getValue() {
        return this.m_Value;
    }
    
    static {
        ChatTokenizationOption.s_Map = new HashMap<Integer, ChatTokenizationOption>();
        for (final ChatTokenizationOption chatTokenizationOption : EnumSet.allOf(ChatTokenizationOption.class)) {
            ChatTokenizationOption.s_Map.put(chatTokenizationOption.getValue(), chatTokenizationOption);
        }
    }
}
