/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;

public class ServerActivity {
    public String profileUuid;
    public long joinTime;
    public long leaveTime;

    public static ServerActivity parse(JsonObject element) {
        ServerActivity sa2 = new ServerActivity();
        try {
            sa2.profileUuid = JsonUtils.getStringOr("profileUuid", element, null);
            sa2.joinTime = JsonUtils.getLongOr("joinTime", element, Long.MIN_VALUE);
            sa2.leaveTime = JsonUtils.getLongOr("leaveTime", element, Long.MIN_VALUE);
        }
        catch (Exception exception) {
            // empty catch block
        }
        return sa2;
    }
}

