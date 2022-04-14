/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.dto;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.PendingInvite;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PendingInvitesList
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public List<PendingInvite> pendingInvites = Lists.newArrayList();

    public static PendingInvitesList parse(String json) {
        PendingInvitesList list = new PendingInvitesList();
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
            if (jsonObject.get("invites").isJsonArray()) {
                Iterator<JsonElement> it2 = jsonObject.get("invites").getAsJsonArray().iterator();
                while (it2.hasNext()) {
                    list.pendingInvites.add(PendingInvite.parse(it2.next().getAsJsonObject()));
                }
            }
        }
        catch (Exception e2) {
            LOGGER.error("Could not parse PendingInvitesList: " + e2.getMessage());
        }
        return list;
    }
}

