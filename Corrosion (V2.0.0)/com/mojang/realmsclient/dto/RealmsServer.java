/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.ComparisonChain;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsOptions;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.realms.RealmsServerPing;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServer
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public long id;
    public String remoteSubscriptionId;
    public String name;
    public String motd;
    public State state;
    public String owner;
    public String ownerUUID;
    public List<PlayerInfo> players;
    public Map<Integer, RealmsOptions> slots;
    public String ip;
    public boolean expired;
    public int daysLeft;
    public WorldType worldType;
    public int activeSlot;
    public String minigameName;
    public int minigameId;
    public int protocol;
    public String status = "";
    public String minigameImage;
    public String resourcePackUrl;
    public String resourcePackHash;
    public RealmsServerPing serverPing = new RealmsServerPing();

    public String getDescription() {
        return this.motd;
    }

    public String getName() {
        return this.name;
    }

    public String getMinigameName() {
        return this.minigameName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String motd) {
        this.motd = motd;
    }

    public void latestStatFrom(RealmsServer oldServer) {
        this.status = oldServer.status;
        this.protocol = oldServer.protocol;
        this.serverPing.nrOfPlayers = oldServer.serverPing.nrOfPlayers;
        this.serverPing.lastPingSnapshot = oldServer.serverPing.lastPingSnapshot;
        this.serverPing.playerList = oldServer.serverPing.playerList;
    }

    public static RealmsServer parse(JsonObject node) {
        RealmsServer server = new RealmsServer();
        try {
            server.id = JsonUtils.getLongOr("id", node, -1L);
            server.remoteSubscriptionId = JsonUtils.getStringOr("remoteSubscriptionId", node, null);
            server.name = JsonUtils.getStringOr("name", node, null);
            server.motd = JsonUtils.getStringOr("motd", node, null);
            server.state = RealmsServer.getState(JsonUtils.getStringOr("state", node, State.CLOSED.name()));
            server.owner = JsonUtils.getStringOr("owner", node, null);
            if (node.get("players") != null && node.get("players").isJsonArray()) {
                server.players = RealmsServer.parseInvited(node.get("players").getAsJsonArray());
                RealmsServer.sortInvited(server);
            } else {
                server.players = new ArrayList<PlayerInfo>();
            }
            server.daysLeft = JsonUtils.getIntOr("daysLeft", node, 0);
            server.ip = JsonUtils.getStringOr("ip", node, null);
            server.expired = JsonUtils.getBooleanOr("expired", node, false);
            server.worldType = RealmsServer.getWorldType(JsonUtils.getStringOr("worldType", node, WorldType.NORMAL.name()));
            server.ownerUUID = JsonUtils.getStringOr("ownerUUID", node, "");
            server.slots = node.get("slots") != null && node.get("slots").isJsonArray() ? RealmsServer.parseSlots(node.get("slots").getAsJsonArray()) : RealmsServer.getEmptySlots();
            server.minigameName = JsonUtils.getStringOr("minigameName", node, null);
            server.activeSlot = JsonUtils.getIntOr("activeSlot", node, -1);
            server.minigameId = JsonUtils.getIntOr("minigameId", node, -1);
            server.minigameImage = JsonUtils.getStringOr("minigameImage", node, null);
            server.resourcePackUrl = JsonUtils.getStringOr("resourcePackUrl", node, null);
            server.resourcePackHash = JsonUtils.getStringOr("resourcePackHash", node, null);
        }
        catch (Exception e2) {
            LOGGER.error("Could not parse McoServer: " + e2.getMessage());
        }
        return server;
    }

    private static void sortInvited(RealmsServer server) {
        Collections.sort(server.players, new Comparator<PlayerInfo>(){

            @Override
            public int compare(PlayerInfo o1, PlayerInfo o2) {
                return ComparisonChain.start().compare(Boolean.valueOf(o2.getAccepted()), Boolean.valueOf(o1.getAccepted())).compare((Comparable<?>)((Object)o1.getName().toLowerCase()), (Comparable<?>)((Object)o2.getName().toLowerCase())).result();
            }
        });
    }

    private static List<PlayerInfo> parseInvited(JsonArray jsonArray) {
        ArrayList<PlayerInfo> invited = new ArrayList<PlayerInfo>();
        for (JsonElement aJsonArray : jsonArray) {
            try {
                JsonObject node = aJsonArray.getAsJsonObject();
                PlayerInfo playerInfo = new PlayerInfo();
                playerInfo.setName(JsonUtils.getStringOr("name", node, null));
                playerInfo.setUuid(JsonUtils.getStringOr("uuid", node, null));
                playerInfo.setOperator(JsonUtils.getBooleanOr("operator", node, false));
                playerInfo.setAccepted(JsonUtils.getBooleanOr("accepted", node, false));
                invited.add(playerInfo);
            }
            catch (Exception e2) {}
        }
        return invited;
    }

    private static Map<Integer, RealmsOptions> parseSlots(JsonArray jsonArray) {
        HashMap<Integer, RealmsOptions> slots = new HashMap<Integer, RealmsOptions>();
        for (JsonElement aJsonArray : jsonArray) {
            try {
                JsonObject node = aJsonArray.getAsJsonObject();
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(node.get("options").getAsString());
                RealmsOptions options = element == null ? RealmsOptions.getDefaults() : RealmsOptions.parse(element.getAsJsonObject());
                int slot = JsonUtils.getIntOr("slotId", node, -1);
                slots.put(slot, options);
            }
            catch (Exception e2) {}
        }
        for (int i2 = 1; i2 <= 3; ++i2) {
            if (slots.containsKey(i2)) continue;
            slots.put(i2, RealmsOptions.getEmptyDefaults());
        }
        return slots;
    }

    private static Map<Integer, RealmsOptions> getEmptySlots() {
        HashMap<Integer, RealmsOptions> slots = new HashMap<Integer, RealmsOptions>();
        slots.put(1, RealmsOptions.getEmptyDefaults());
        slots.put(2, RealmsOptions.getEmptyDefaults());
        slots.put(3, RealmsOptions.getEmptyDefaults());
        return slots;
    }

    public static RealmsServer parse(String json) {
        RealmsServer server = new RealmsServer();
        try {
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(json).getAsJsonObject();
            server = RealmsServer.parse(object);
        }
        catch (Exception e2) {
            LOGGER.error("Could not parse McoServer: " + e2.getMessage());
        }
        return server;
    }

    private static State getState(String state) {
        try {
            return State.valueOf(state);
        }
        catch (Exception e2) {
            return State.CLOSED;
        }
    }

    private static WorldType getWorldType(String state) {
        try {
            return WorldType.valueOf(state);
        }
        catch (Exception e2) {
            return WorldType.NORMAL;
        }
    }

    public boolean shouldPing(long now) {
        return now - this.serverPing.lastPingSnapshot >= 6000L;
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(this.id).append(this.name).append(this.motd).append((Object)this.state).append(this.owner).append(this.expired).toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        RealmsServer rhs = (RealmsServer)obj;
        return new EqualsBuilder().append(this.id, rhs.id).append(this.name, rhs.name).append(this.motd, rhs.motd).append((Object)this.state, (Object)rhs.state).append(this.owner, rhs.owner).append(this.expired, rhs.expired).append((Object)this.worldType, (Object)this.worldType).isEquals();
    }

    public RealmsServer clone() {
        RealmsServer server = new RealmsServer();
        server.id = this.id;
        server.remoteSubscriptionId = this.remoteSubscriptionId;
        server.name = this.name;
        server.motd = this.motd;
        server.state = this.state;
        server.owner = this.owner;
        server.players = this.players;
        server.slots = this.cloneSlots(this.slots);
        server.ip = this.ip;
        server.expired = this.expired;
        server.daysLeft = this.daysLeft;
        server.protocol = this.protocol;
        server.status = this.status;
        server.serverPing = new RealmsServerPing();
        server.serverPing.nrOfPlayers = this.serverPing.nrOfPlayers;
        server.serverPing.lastPingSnapshot = this.serverPing.lastPingSnapshot;
        server.serverPing.playerList = this.serverPing.playerList;
        server.worldType = this.worldType;
        server.ownerUUID = this.ownerUUID;
        server.minigameName = this.minigameName;
        server.activeSlot = this.activeSlot;
        server.minigameId = this.minigameId;
        server.minigameImage = this.minigameImage;
        server.resourcePackUrl = this.resourcePackUrl;
        server.resourcePackHash = this.resourcePackHash;
        return server;
    }

    public Map<Integer, RealmsOptions> cloneSlots(Map<Integer, RealmsOptions> slots) {
        HashMap<Integer, RealmsOptions> newSlots = new HashMap<Integer, RealmsOptions>();
        for (Map.Entry<Integer, RealmsOptions> entry : slots.entrySet()) {
            newSlots.put(entry.getKey(), entry.getValue().clone());
        }
        return newSlots;
    }

    public static enum WorldType {
        NORMAL,
        MINIGAME,
        ADVENTUREMAP;

    }

    public static enum State {
        CLOSED,
        OPEN,
        ADMIN_LOCK,
        UNINITIALIZED;

    }

    public static class McoServerComparator
    implements Comparator<RealmsServer> {
        private final String refOwner;

        public McoServerComparator(String owner) {
            this.refOwner = owner;
        }

        @Override
        public int compare(RealmsServer server1, RealmsServer server2) {
            return ComparisonChain.start().compareTrueFirst(server1.state.equals((Object)State.UNINITIALIZED), server2.state.equals((Object)State.UNINITIALIZED)).compareFalseFirst(server1.expired, server2.expired).compareTrueFirst(server1.owner.equals(this.refOwner), server2.owner.equals(this.refOwner)).compareTrueFirst(server1.state.equals((Object)State.OPEN), server2.state.equals((Object)State.OPEN)).compare(server1.id, server2.id).result();
        }
    }
}

