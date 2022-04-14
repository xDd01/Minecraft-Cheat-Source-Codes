/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.packet.inbound;

import cafe.corrosion.social.packet.SocialPacketIn;
import cafe.corrosion.social.rank.ClientRank;
import com.google.gson.JsonObject;

public class PacketInFriendAdd
extends SocialPacketIn {
    private final int friendId;
    private final String friendName;
    private final ClientRank friendRank;

    public PacketInFriendAdd(JsonObject jsonObject) {
        this.friendId = jsonObject.get("id").getAsInt();
        this.friendName = jsonObject.get("name").getAsString();
        this.friendRank = ClientRank.valueOf(jsonObject.get("rank").getAsString());
    }

    public int getFriendId() {
        return this.friendId;
    }

    public String getFriendName() {
        return this.friendName;
    }

    public ClientRank getFriendRank() {
        return this.friendRank;
    }
}

