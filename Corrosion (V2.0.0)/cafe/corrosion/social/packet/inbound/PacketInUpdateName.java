/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.packet.inbound;

import cafe.corrosion.social.packet.SocialPacketIn;
import com.google.gson.JsonObject;

public class PacketInUpdateName
extends SocialPacketIn {
    private final int userId;
    private final String name;

    public PacketInUpdateName(JsonObject jsonObject) {
        this.userId = jsonObject.get("id").getAsInt();
        this.name = jsonObject.get("name").getAsString();
    }

    public int getUserId() {
        return this.userId;
    }

    public String getName() {
        return this.name;
    }
}

