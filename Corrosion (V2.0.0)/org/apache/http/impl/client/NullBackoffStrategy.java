/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client;

import org.apache.http.HttpResponse;
import org.apache.http.client.ConnectionBackoffStrategy;

public class NullBackoffStrategy
implements ConnectionBackoffStrategy {
    public boolean shouldBackoff(Throwable t2) {
        return false;
    }

    public boolean shouldBackoff(HttpResponse resp) {
        return false;
    }
}

