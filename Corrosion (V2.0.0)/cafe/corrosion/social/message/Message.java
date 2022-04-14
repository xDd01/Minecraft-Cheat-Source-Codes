/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.message;

import com.google.gson.JsonObject;

public class Message {
    private final String rawContent;
    private final long timestamp;

    public Message(String rawContent, long timestamp) {
        this.rawContent = rawContent;
        this.timestamp = timestamp;
    }

    public Message(JsonObject object) {
        this.rawContent = object.get("content").getAsString();
        this.timestamp = object.get("timestamp").getAsLong();
    }

    public String getRawContent() {
        return this.rawContent;
    }

    public long getTimestamp() {
        return this.timestamp;
    }
}

