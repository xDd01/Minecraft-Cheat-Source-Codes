/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.packet.outbound;

import cafe.corrosion.social.packet.SocialPacketOut;
import com.google.gson.JsonObject;

public class PacketOutSendMessage
extends SocialPacketOut {
    private final int channel;
    private final String message;

    @Override
    public JsonObject serialize() {
        return null;
    }

    public PacketOutSendMessage(int channel, String message) {
        this.channel = channel;
        this.message = message;
    }
}

