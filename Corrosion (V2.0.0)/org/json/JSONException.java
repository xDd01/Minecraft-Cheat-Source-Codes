/*
 * Decompiled with CFR 0.152.
 */
package org.json;

public class JSONException
extends Exception {
    private Throwable cause;

    public JSONException(String string) {
        super(string);
    }

    public JSONException(Throwable throwable) {
        super(throwable.getMessage());
        this.cause = throwable;
    }

    @Override
    public Throwable getCause() {
        return this.cause;
    }
}

