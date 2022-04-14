/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.message.history;

import cafe.corrosion.social.message.Message;
import java.util.ArrayList;
import java.util.List;

public class MessageHistory {
    private final List<Message> messages = new ArrayList<Message>();

    public void push(Message message) {
        this.messages.add(message);
    }

    public List<Message> getMessages() {
        return this.messages;
    }
}

