/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.packet.inbound;

import cafe.corrosion.social.packet.SocialPacketIn;
import com.google.gson.JsonObject;
import java.util.Base64;

public class PacketInReceiveMessage
extends SocialPacketIn {
    private final int userId;
    private final String message;

    public PacketInReceiveMessage(JsonObject jsonObject) {
        this.userId = jsonObject.get("id").getAsInt();
        byte[] encodedContent = jsonObject.get("contents").getAsString().getBytes();
        byte[] decodedContent = Base64.getDecoder().decode(encodedContent);
        this.message = new String(decodedContent);
    }

    public int getUserId() {
        return this.userId;
    }

    public String getMessage() {
        return this.message;
    }
}

