/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.util;

public class Pair<A, B> {
    private final A first;
    private final B second;

    protected Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> Pair<A, B> of(A a2, B b2) {
        return new Pair<A, B>(a2, b2);
    }

    public A first() {
        return this.first;
    }

    public B second() {
        return this.second;
    }

    public String mkString(String separator) {
        return String.format("%s%s%s", this.first, separator, this.second);
    }
}

