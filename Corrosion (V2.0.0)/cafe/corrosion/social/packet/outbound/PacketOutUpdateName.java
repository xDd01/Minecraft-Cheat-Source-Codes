/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.packet.outbound;

import cafe.corrosion.social.packet.SocialPacketOut;
import cafe.corrosion.util.json.JsonChain;
import com.google.gson.JsonObject;

public class PacketOutUpdateName
extends SocialPacketOut {
    private final String name;

    @Override
    public JsonObject serialize() {
        return new JsonChain().addProperty("name", this.name).getJsonObject();
    }

    public PacketOutUpdateName(String name) {
        this.name = name;
    }
}

