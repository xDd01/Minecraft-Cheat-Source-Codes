/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.util;

public abstract class Option<A> {
    public abstract A get();

    public static <A> Some<A> some(A a2) {
        return new Some<A>(a2);
    }

    public static <A> None<A> none() {
        return new None();
    }

    public static final class None<A>
    extends Option<A> {
        @Override
        public A get() {
            throw new RuntimeException("None has no value");
        }
    }

    public static final class Some<A>
    extends Option<A> {
        private final A a;

        public Some(A a2) {
            this.a = a2;
        }

        @Override
        public A get() {
            return this.a;
        }

        public static <A> Option<A> of(A value) {
            return new Some<A>(value);
        }
    }
}

