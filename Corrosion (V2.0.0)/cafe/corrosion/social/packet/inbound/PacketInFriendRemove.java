/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.packet.inbound;

import cafe.corrosion.social.packet.SocialPacketIn;
import com.google.gson.JsonObject;

public class PacketInFriendRemove
extends SocialPacketIn {
    private final int userId;

    public PacketInFriendRemove(JsonObject jsonObject) {
        this.userId = jsonObject.get("id").getAsInt();
    }

    public int getUserId() {
        return this.userId;
    }
}

