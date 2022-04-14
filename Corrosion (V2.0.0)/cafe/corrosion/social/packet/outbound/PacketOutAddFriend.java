/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.packet.outbound;

import cafe.corrosion.social.packet.SocialPacketOut;
import cafe.corrosion.util.json.JsonChain;
import com.google.gson.JsonObject;

public class PacketOutAddFriend
extends SocialPacketOut {
    private final int id;

    @Override
    public JsonObject serialize() {
        return new JsonChain().addProperty("id", this.id).getJsonObject();
    }

    public PacketOutAddFriend(int id2) {
        this.id = id2;
    }
}

