/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.dto.WorldTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldTemplateList
extends ValueObject {
    private static final Logger LOGGER = LogManager.getLogger();
    public List<WorldTemplate> templates;

    public static WorldTemplateList parse(String json) {
        WorldTemplateList list = new WorldTemplateList();
        list.templates = new ArrayList<WorldTemplate>();
        try {
            JsonParser parser = new JsonParser();
            JsonObject object = parser.parse(json).getAsJsonObject();
            if (object.get("templates").isJsonArray()) {
                Iterator<JsonElement> it2 = object.get("templates").getAsJsonArray().iterator();
                while (it2.hasNext()) {
                    list.templates.add(WorldTemplate.parse(it2.next().getAsJsonObject()));
                }
            }
        }
        catch (Exception e2) {
            LOGGER.error("Could not parse WorldTemplateList: " + e2.getMessage());
        }
        return list;
    }
}

