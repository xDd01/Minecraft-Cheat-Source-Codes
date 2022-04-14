/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.friend;

import cafe.corrosion.social.rank.ClientRank;

public class Friend {
    private final int id;
    private final ClientRank clientRank;
    private final String username;
    private final String minecraftUsername;
    private boolean isAccepted;

    public int getId() {
        return this.id;
    }

    public ClientRank getClientRank() {
        return this.clientRank;
    }

    public String getUsername() {
        return this.username;
    }

    public String getMinecraftUsername() {
        return this.minecraftUsername;
    }

    public boolean isAccepted() {
        return this.isAccepted;
    }

    public Friend(int id2, ClientRank clientRank, String username, String minecraftUsername, boolean isAccepted) {
        this.id = id2;
        this.clientRank = clientRank;
        this.username = username;
        this.minecraftUsername = minecraftUsername;
        this.isAccepted = isAccepted;
    }

    public void setAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }
}

