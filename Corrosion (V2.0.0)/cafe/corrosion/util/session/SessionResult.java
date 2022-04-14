/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.session;

import net.minecraft.util.Session;

public class SessionResult {
    private final String responseMessage;
    private final Session session;

    public String getResponseMessage() {
        return this.responseMessage;
    }

    public Session getSession() {
        return this.session;
    }

    public SessionResult(String responseMessage, Session session) {
        this.responseMessage = responseMessage;
        this.session = session;
    }
}

