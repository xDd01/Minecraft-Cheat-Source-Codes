/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.self;

import cafe.corrosion.social.rank.ClientRank;
import cafe.corrosion.social.user.ClientUser;
import java.util.ArrayList;
import java.util.List;

public class SelfUser
extends ClientUser {
    private final List<ClientUser> friends = new ArrayList<ClientUser>();
    private final int userId;
    private final String userName;
    private final ClientRank clientRank;

    public SelfUser(int userId, String userName, ClientRank clientRank) {
        super(userName, userId);
        this.userId = userId;
        this.userName = userName;
        this.clientRank = clientRank;
    }

    public void addFriend(ClientUser clientUser) {
        this.friends.add(clientUser);
    }

    public List<ClientUser> getFriends() {
        return this.friends;
    }

    @Override
    public int getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public ClientRank getClientRank() {
        return this.clientRank;
    }
}

