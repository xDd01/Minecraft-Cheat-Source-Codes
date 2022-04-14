/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.packet.outbound;

import cafe.corrosion.social.packet.SocialPacketOut;
import cafe.corrosion.util.json.JsonChain;
import com.google.gson.JsonObject;

public class PacketOutAcceptFriendRequest
extends SocialPacketOut {
    private final int targetId;

    @Override
    public JsonObject serialize() {
        return new JsonChain().addProperty("id", this.targetId).getJsonObject();
    }

    public PacketOutAcceptFriendRequest(int targetId) {
        this.targetId = targetId;
    }
}

