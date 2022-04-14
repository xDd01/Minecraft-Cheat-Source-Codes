/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.manager;

import cafe.corrosion.social.friend.Friend;
import cafe.corrosion.social.rank.ClientRank;
import cafe.corrosion.util.Manager;
import java.util.List;
import java.util.stream.Collectors;

public class FriendManager
extends Manager<Friend> {
    public FriendManager() {
        for (int i2 = 0; i2 < 10; ++i2) {
            this.add(new Friend(0, ClientRank.BETA, "" + i2, "a", false));
        }
    }

    @Override
    public List<Friend> getObjects() {
        return super.getObjects().stream().filter(Friend::isAccepted).collect(Collectors.toList());
    }

    public List<Friend> getInboundFriendRequests() {
        return super.getObjects().stream().filter(friend -> !friend.isAccepted()).collect(Collectors.toList());
    }
}

