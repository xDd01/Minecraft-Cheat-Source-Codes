/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.client;

import java.net.Proxy;

public class RealmsClientConfig {
    private static Proxy proxy;

    public static Proxy getProxy() {
        return proxy;
    }

    public static void setProxy(Proxy proxy) {
        if (RealmsClientConfig.proxy == null) {
            RealmsClientConfig.proxy = proxy;
        }
    }
}

