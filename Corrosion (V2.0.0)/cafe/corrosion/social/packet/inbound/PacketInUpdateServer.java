/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.packet.inbound;

import cafe.corrosion.social.packet.SocialPacketIn;
import com.google.gson.JsonObject;

public class PacketInUpdateServer
extends SocialPacketIn {
    private final int id;
    private final String ip;

    public PacketInUpdateServer(JsonObject jsonObject) {
        this.id = jsonObject.get("id").getAsInt();
        this.ip = jsonObject.get("ip").getAsString();
    }

    public int getId() {
        return this.id;
    }

    public String getIp() {
        return this.ip;
    }
}

