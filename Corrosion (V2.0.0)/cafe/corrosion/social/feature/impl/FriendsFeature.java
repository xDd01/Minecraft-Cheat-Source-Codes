/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.feature.impl;

import cafe.corrosion.event.impl.social.EventSocialPacketReceive;
import cafe.corrosion.social.CorrosionSocket;
import cafe.corrosion.social.feature.Feature;
import cafe.corrosion.social.packet.inbound.PacketInFriendAdd;

public class FriendsFeature
extends Feature {
    public FriendsFeature(CorrosionSocket corrosionSocket) {
        this.registerEventHandler(EventSocialPacketReceive.class, event -> {
            if (event.getPacket() instanceof PacketInFriendAdd) {
                PacketInFriendAdd packetInFriendAdd = (PacketInFriendAdd)event.getPacket();
            }
        });
    }
}

