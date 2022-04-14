/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.packet.adapter;

import cafe.corrosion.social.packet.SocialPacketIn;
import cafe.corrosion.social.packet.SocialPacketOut;
import cafe.corrosion.social.packet.inbound.PacketInFriendAdd;
import cafe.corrosion.social.packet.inbound.PacketInFriendRemove;
import cafe.corrosion.social.packet.inbound.PacketInReceiveMessage;
import cafe.corrosion.social.packet.inbound.PacketInReceiveUser;
import cafe.corrosion.social.packet.inbound.PacketInUpdateName;
import cafe.corrosion.social.packet.inbound.PacketInUpdateServer;
import cafe.corrosion.social.packet.outbound.PacketOutAcceptFriendRequest;
import cafe.corrosion.social.packet.outbound.PacketOutAddFriend;
import cafe.corrosion.social.packet.outbound.PacketOutRemoveFriend;
import cafe.corrosion.social.packet.outbound.PacketOutSendMessage;
import cafe.corrosion.social.packet.outbound.PacketOutUpdateName;
import cafe.corrosion.social.packet.outbound.PacketOutUpdateServer;
import com.google.gson.JsonObject;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class PacketAdapter {
    private static final Map<Class<? extends SocialPacketOut>, String> OUTBOUND_PACKETS = new HashMap<Class<? extends SocialPacketOut>, String>();
    private static final Map<String, Constructor<? extends SocialPacketIn>> INBOUND_PACKETS = new HashMap<String, Constructor<? extends SocialPacketIn>>();

    private static <T extends SocialPacketIn> Constructor<T> getConstructor(Class<T> clazz) {
        return clazz.getDeclaredConstructor(JsonObject.class);
    }

    public static <T extends SocialPacketIn> T adaptInbound(JsonObject jsonObject) {
        String packetType = jsonObject.get("packet-type").getAsString();
        Constructor<? extends SocialPacketIn> clazz = INBOUND_PACKETS.get(packetType);
        return (T)clazz.newInstance(jsonObject);
    }

    public static String getType(SocialPacketOut packet) {
        return OUTBOUND_PACKETS.get(packet.getClass());
    }

    static {
        OUTBOUND_PACKETS.put(PacketOutAcceptFriendRequest.class, "accept-friend-request");
        OUTBOUND_PACKETS.put(PacketOutAddFriend.class, "add-friend");
        OUTBOUND_PACKETS.put(PacketOutRemoveFriend.class, "remove-friend");
        OUTBOUND_PACKETS.put(PacketOutSendMessage.class, "send-message");
        OUTBOUND_PACKETS.put(PacketOutUpdateName.class, "update-name");
        OUTBOUND_PACKETS.put(PacketOutUpdateServer.class, "update-server");
        INBOUND_PACKETS.put("friend-request-receive", PacketAdapter.getConstructor(PacketInFriendAdd.class));
        INBOUND_PACKETS.put("friend-remove", PacketAdapter.getConstructor(PacketInFriendRemove.class));
        INBOUND_PACKETS.put("message-receive", PacketAdapter.getConstructor(PacketInReceiveMessage.class));
        INBOUND_PACKETS.put("receive-user", PacketAdapter.getConstructor(PacketInReceiveUser.class));
        INBOUND_PACKETS.put("update-name", PacketAdapter.getConstructor(PacketInUpdateName.class));
        INBOUND_PACKETS.put("update-server", PacketAdapter.getConstructor(PacketInUpdateServer.class));
    }
}

