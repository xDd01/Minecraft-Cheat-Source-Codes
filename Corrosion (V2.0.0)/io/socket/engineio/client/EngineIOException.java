/*
 * Decompiled with CFR 0.152.
 */
package io.socket.engineio.client;

public class EngineIOException
extends Exception {
    public String transport;
    public Object code;

    public EngineIOException() {
    }

    public EngineIOException(String message) {
        super(message);
    }

    public EngineIOException(String message, Throwable cause) {
        super(message, cause);
    }

    public EngineIOException(Throwable cause) {
        super(cause);
    }
}

