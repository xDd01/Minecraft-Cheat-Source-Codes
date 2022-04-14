/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.json;

import com.google.gson.JsonObject;

public class JsonChain {
    private final JsonObject jsonObject = new JsonObject();

    public JsonChain addProperty(String key, String value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonChain addProperty(String key, boolean value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonChain addProperty(String key, Number value) {
        this.jsonObject.addProperty(key, value);
        return this;
    }

    public JsonChain addProperty(Number key, String value) {
        this.jsonObject.addProperty(String.valueOf(key), value);
        return this;
    }

    public JsonObject getJsonObject() {
        return this.jsonObject;
    }

    public String toString() {
        return this.jsonObject.toString();
    }
}

