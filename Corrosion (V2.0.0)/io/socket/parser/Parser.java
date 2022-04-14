/*
 * Decompiled with CFR 0.152.
 */
package io.socket.parser;

import io.socket.parser.Packet;

public interface Parser {
    public static final int CONNECT = 0;
    public static final int DISCONNECT = 1;
    public static final int EVENT = 2;
    public static final int ACK = 3;
    public static final int CONNECT_ERROR = 4;
    public static final int BINARY_EVENT = 5;
    public static final int BINARY_ACK = 6;
    public static final int protocol = 5;
    public static final String[] types = new String[]{"CONNECT", "DISCONNECT", "EVENT", "ACK", "ERROR", "BINARY_EVENT", "BINARY_ACK"};

    public static interface Decoder {
        public void add(String var1);

        public void add(byte[] var1);

        public void destroy();

        public void onDecoded(Callback var1);

        public static interface Callback {
            public void call(Packet var1);
        }
    }

    public static interface Encoder {
        public void encode(Packet var1, Callback var2);

        public static interface Callback {
            public void call(Object[] var1);
        }
    }
}

