/*
 * Decompiled with CFR 0.152.
 */
package io.socket.client;

import io.socket.emitter.Emitter;

public class On {
    private On() {
    }

    public static Handle on(final Emitter obj, final String ev2, final Emitter.Listener fn2) {
        obj.on(ev2, fn2);
        return new Handle(){

            @Override
            public void destroy() {
                obj.off(ev2, fn2);
            }
        };
    }

    public static interface Handle {
        public void destroy();
    }
}

