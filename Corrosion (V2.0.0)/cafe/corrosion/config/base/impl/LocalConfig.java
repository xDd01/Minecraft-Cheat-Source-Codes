/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.config.base.impl;

import cafe.corrosion.config.base.Config;
import cafe.corrosion.task.LoadModulesTask;
import cafe.corrosion.util.player.PlayerUtil;
import cafe.corrosion.util.security.SecurityUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class LocalConfig
extends Config {
    private final Path path;

    public LocalConfig(Path path) {
        byte[] bytes = Files.readAllBytes(path);
        JsonArray configData = GSON.fromJson(new String(bytes), JsonArray.class);
        ArrayList objects = new ArrayList();
        configData.forEach(element -> {
            JsonObject object = element.getAsJsonObject();
            if (object.has("author") && object.has("version")) {
                this.clientVersion = object.get("version").getAsString();
                this.author = object.get("author").getAsString();
            } else {
                objects.add(object);
            }
        });
        this.configData = objects;
        this.checksum = SecurityUtil.hash(bytes);
        this.path = path;
    }

    @Override
    public void load(String name) {
        if (!this.path.toFile().exists()) {
            PlayerUtil.sendMessage("An error occurred while attempting to load " + name + "!");
            return;
        }
        byte[] bytes = Files.readAllBytes(this.path);
        String checksum = SecurityUtil.hash(bytes);
        if (!checksum.equals(this.checksum)) {
            JsonArray configData = GSON.fromJson(new String(bytes), JsonArray.class);
            ArrayList objects = new ArrayList();
            configData.forEach(element -> {
                JsonObject object = element.getAsJsonObject();
                if (object.has("author") && object.has("version")) {
                    this.clientVersion = object.get("version").getAsString();
                    this.author = object.get("author").getAsString();
                } else {
                    objects.add(object);
                }
            });
            this.configData = objects;
        }
        new LoadModulesTask(this.configData).run();
    }
}

