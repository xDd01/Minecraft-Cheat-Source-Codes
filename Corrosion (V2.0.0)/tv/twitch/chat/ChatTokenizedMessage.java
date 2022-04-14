/*
 * Decompiled with CFR 0.152.
 */
package tv.twitch.chat;

import java.util.HashSet;
import tv.twitch.chat.ChatMessageToken;
import tv.twitch.chat.ChatUserMode;
import tv.twitch.chat.ChatUserSubscription;

public class ChatTokenizedMessage {
    public String displayName;
    public HashSet<ChatUserMode> modes = new HashSet();
    public HashSet<ChatUserSubscription> subscriptions = new HashSet();
    public int nameColorARGB;
    public ChatMessageToken[] tokenList;
    public boolean action;
}

