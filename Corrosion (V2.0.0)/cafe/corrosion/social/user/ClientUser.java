/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.user;

import cafe.corrosion.social.message.Message;
import cafe.corrosion.social.message.history.MessageHistory;
import java.util.HashMap;
import java.util.Map;

public class ClientUser {
    private final Map<Integer, MessageHistory> messageHistory = new HashMap<Integer, MessageHistory>();
    private final String name;
    private final int userId;

    public ClientUser(String name, int userId) {
        this.name = name;
        this.userId = userId;
    }

    public void addMessageToHistory(ClientUser target, Message message) {
        int id2 = target.getUserId();
        MessageHistory history = this.messageHistory.get(id2);
        if (history == null) {
            return;
        }
        history.push(message);
    }

    public Map<Integer, MessageHistory> getMessageHistory() {
        return this.messageHistory;
    }

    public String getName() {
        return this.name;
    }

    public int getUserId() {
        return this.userId;
    }
}

