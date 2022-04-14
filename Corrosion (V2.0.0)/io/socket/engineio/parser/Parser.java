/*
 * Decompiled with CFR 0.152.
 */
package io.socket.engineio.parser;

import io.socket.engineio.parser.Base64;
import io.socket.engineio.parser.Packet;
import java.util.HashMap;
import java.util.Map;

public class Parser {
    public static final int PROTOCOL = 4;
    private static final char SEPARATOR = '\u001e';
    private static final Map<String, Integer> packets = new HashMap<String, Integer>(){
        {
            this.put("open", 0);
            this.put("close", 1);
            this.put("ping", 2);
            this.put("pong", 3);
            this.put("message", 4);
            this.put("upgrade", 5);
            this.put("noop", 6);
        }
    };
    private static final Map<Integer, String> packetslist = new HashMap<Integer, String>();
    private static final Packet<String> err;

    private Parser() {
    }

    public static void encodePacket(Packet packet, EncodeCallback callback) {
        if (packet.data instanceof byte[]) {
            callback.call(packet.data);
        } else {
            String type = String.valueOf(packets.get(packet.type));
            String content = packet.data != null ? String.valueOf(packet.data) : "";
            callback.call(type + content);
        }
    }

    private static void encodePacketAsBase64(Packet packet, EncodeCallback<String> callback) {
        if (packet.data instanceof byte[]) {
            byte[] data = (byte[])packet.data;
            String value = "b" + Base64.encodeToString(data, 0);
            callback.call(value);
        } else {
            Parser.encodePacket(packet, callback);
        }
    }

    public static Packet<String> decodePacket(String data) {
        int type;
        if (data == null) {
            return err;
        }
        try {
            type = Character.getNumericValue(data.charAt(0));
        }
        catch (IndexOutOfBoundsException e2) {
            type = -1;
        }
        if (type < 0 || type >= packetslist.size()) {
            return err;
        }
        if (data.length() > 1) {
            return new Packet<String>(packetslist.get(type), data.substring(1));
        }
        return new Packet<String>(packetslist.get(type));
    }

    public static Packet decodeBase64Packet(String data) {
        if (data == null) {
            return err;
        }
        if (data.charAt(0) == 'b') {
            return new Packet<byte[]>("message", Base64.decode(data.substring(1), 0));
        }
        return Parser.decodePacket(data);
    }

    public static Packet<byte[]> decodePacket(byte[] data) {
        return new Packet<byte[]>("message", data);
    }

    public static void encodePayload(Packet[] packets, EncodeCallback<String> callback) {
        if (packets.length == 0) {
            callback.call("0:");
            return;
        }
        final StringBuilder result = new StringBuilder();
        int l2 = packets.length;
        for (int i2 = 0; i2 < l2; ++i2) {
            final boolean isLast = i2 == l2 - 1;
            Parser.encodePacketAsBase64(packets[i2], new EncodeCallback<String>(){

                @Override
                public void call(String message) {
                    result.append(message);
                    if (!isLast) {
                        result.append('\u001e');
                    }
                }
            });
        }
        callback.call(result.toString());
    }

    public static void decodePayload(String data, DecodePayloadCallback<String> callback) {
        if (data == null || data.length() == 0) {
            callback.call(err, 0, 1);
            return;
        }
        String[] messages = data.split(String.valueOf('\u001e'));
        int l2 = messages.length;
        for (int i2 = 0; i2 < l2; ++i2) {
            Packet packet = Parser.decodeBase64Packet(messages[i2]);
            if (Parser.err.type.equals(packet.type) && ((String)Parser.err.data).equals(packet.data)) {
                callback.call(err, 0, 1);
                return;
            }
            boolean ret = callback.call(packet, i2, l2);
            if (ret) continue;
            return;
        }
    }

    static {
        for (Map.Entry<String, Integer> entry : packets.entrySet()) {
            packetslist.put(entry.getValue(), entry.getKey());
        }
        err = new Packet<String>("error", "parser error");
    }

    public static interface DecodePayloadCallback<T> {
        public boolean call(Packet<T> var1, int var2, int var3);
    }

    public static interface EncodeCallback<T> {
        public void call(T var1);
    }
}

