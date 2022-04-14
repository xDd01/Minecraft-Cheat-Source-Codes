/*
 * Decompiled with CFR 0.152.
 */
package io.socket.engineio.client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HandshakeData {
    public String sid;
    public String[] upgrades;
    public long pingInterval;
    public long pingTimeout;

    HandshakeData(String data) throws JSONException {
        this(new JSONObject(data));
    }

    HandshakeData(JSONObject data) throws JSONException {
        JSONArray upgrades = data.getJSONArray("upgrades");
        int length = upgrades.length();
        String[] tempUpgrades = new String[length];
        for (int i2 = 0; i2 < length; ++i2) {
            tempUpgrades[i2] = upgrades.getString(i2);
        }
        this.sid = data.getString("sid");
        this.upgrades = tempUpgrades;
        this.pingInterval = data.getLong("pingInterval");
        this.pingTimeout = data.getLong("pingTimeout");
    }
}

