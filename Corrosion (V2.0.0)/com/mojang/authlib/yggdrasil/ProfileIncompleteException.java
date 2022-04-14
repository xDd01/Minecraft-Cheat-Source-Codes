/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.authlib.yggdrasil;

public class ProfileIncompleteException
extends RuntimeException {
    public ProfileIncompleteException() {
    }

    public ProfileIncompleteException(String message) {
        super(message);
    }

    public ProfileIncompleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProfileIncompleteException(Throwable cause) {
        super(cause);
    }
}

