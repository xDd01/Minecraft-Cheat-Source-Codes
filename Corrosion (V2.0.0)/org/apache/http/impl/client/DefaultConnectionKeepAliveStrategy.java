/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client;

import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.Immutable;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Immutable
public class DefaultConnectionKeepAliveStrategy
implements ConnectionKeepAliveStrategy {
    public static final DefaultConnectionKeepAliveStrategy INSTANCE = new DefaultConnectionKeepAliveStrategy();

    public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
        Args.notNull(response, "HTTP response");
        BasicHeaderElementIterator it2 = new BasicHeaderElementIterator(response.headerIterator("Keep-Alive"));
        while (it2.hasNext()) {
            HeaderElement he2 = it2.nextElement();
            String param = he2.getName();
            String value = he2.getValue();
            if (value == null || !param.equalsIgnoreCase("timeout")) continue;
            try {
                return Long.parseLong(value) * 1000L;
            }
            catch (NumberFormatException ignore) {
            }
        }
        return -1L;
    }
}

