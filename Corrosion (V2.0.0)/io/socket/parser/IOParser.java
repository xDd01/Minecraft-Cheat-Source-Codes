/*
 * Decompiled with CFR 0.152.
 */
package io.socket.parser;

import io.socket.hasbinary.HasBinary;
import io.socket.parser.Binary;
import io.socket.parser.DecodingException;
import io.socket.parser.Packet;
import io.socket.parser.Parser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public final class IOParser
implements Parser {
    private static final Logger logger = Logger.getLogger(IOParser.class.getName());

    private IOParser() {
    }

    static class BinaryReconstructor {
        public Packet reconPack;
        List<byte[]> buffers;

        BinaryReconstructor(Packet packet) {
            this.reconPack = packet;
            this.buffers = new ArrayList<byte[]>();
        }

        public Packet takeBinaryData(byte[] binData) {
            this.buffers.add(binData);
            if (this.buffers.size() == this.reconPack.attachments) {
                Packet packet = Binary.reconstructPacket(this.reconPack, (byte[][])this.buffers.toArray((T[])new byte[this.buffers.size()][]));
                this.finishReconstruction();
                return packet;
            }
            return null;
        }

        public void finishReconstruction() {
            this.reconPack = null;
            this.buffers = new ArrayList<byte[]>();
        }
    }

    public static final class Decoder
    implements Parser.Decoder {
        BinaryReconstructor reconstructor = null;
        private Parser.Decoder.Callback onDecodedCallback;

        @Override
        public void add(String obj) {
            Packet packet = Decoder.decodeString(obj);
            if (5 == packet.type || 6 == packet.type) {
                this.reconstructor = new BinaryReconstructor(packet);
                if (this.reconstructor.reconPack.attachments == 0 && this.onDecodedCallback != null) {
                    this.onDecodedCallback.call(packet);
                }
            } else if (this.onDecodedCallback != null) {
                this.onDecodedCallback.call(packet);
            }
        }

        @Override
        public void add(byte[] obj) {
            if (this.reconstructor == null) {
                throw new RuntimeException("got binary data when not reconstructing a packet");
            }
            Packet packet = this.reconstructor.takeBinaryData(obj);
            if (packet != null) {
                this.reconstructor = null;
                if (this.onDecodedCallback != null) {
                    this.onDecodedCallback.call(packet);
                }
            }
        }

        private static Packet decodeString(String str) {
            Character next;
            int i2 = 0;
            int length = str.length();
            Packet p2 = new Packet(Character.getNumericValue(str.charAt(0)));
            if (p2.type < 0 || p2.type > Parser.types.length - 1) {
                throw new DecodingException("unknown packet type " + p2.type);
            }
            if (5 == p2.type || 6 == p2.type) {
                if (!str.contains("-") || length <= i2 + 1) {
                    throw new DecodingException("illegal attachments");
                }
                StringBuilder attachments = new StringBuilder();
                while (str.charAt(++i2) != '-') {
                    attachments.append(str.charAt(i2));
                }
                p2.attachments = Integer.parseInt(attachments.toString());
            }
            if (length > i2 + 1 && '/' == str.charAt(i2 + 1)) {
                char c2;
                StringBuilder nsp = new StringBuilder();
                while (',' != (c2 = str.charAt(++i2))) {
                    nsp.append(c2);
                    if (i2 + 1 != length) continue;
                    break;
                }
                p2.nsp = nsp.toString();
            } else {
                p2.nsp = "/";
            }
            if (length > i2 + 1 && Character.getNumericValue((next = Character.valueOf(str.charAt(i2 + 1))).charValue()) > -1) {
                StringBuilder id2 = new StringBuilder();
                do {
                    char c3;
                    if (Character.getNumericValue(c3 = str.charAt(++i2)) < 0) {
                        --i2;
                        break;
                    }
                    id2.append(c3);
                } while (i2 + 1 != length);
                try {
                    p2.id = Integer.parseInt(id2.toString());
                }
                catch (NumberFormatException e2) {
                    throw new DecodingException("invalid payload");
                }
            }
            if (length > i2 + 1) {
                try {
                    str.charAt(++i2);
                    p2.data = new JSONTokener(str.substring(i2)).nextValue();
                }
                catch (JSONException e3) {
                    logger.log(Level.WARNING, "An error occured while retrieving data from JSONTokener", e3);
                    throw new DecodingException("invalid payload");
                }
                if (!Decoder.isPayloadValid(p2.type, p2.data)) {
                    throw new DecodingException("invalid payload");
                }
            }
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("decoded %s as %s", str, p2));
            }
            return p2;
        }

        private static boolean isPayloadValid(int type, Object payload) {
            switch (type) {
                case 0: 
                case 4: {
                    return payload instanceof JSONObject;
                }
                case 1: {
                    return payload == null;
                }
                case 2: 
                case 5: {
                    return payload instanceof JSONArray && ((JSONArray)payload).length() > 0 && !((JSONArray)payload).isNull(0);
                }
                case 3: 
                case 6: {
                    return payload instanceof JSONArray;
                }
            }
            return false;
        }

        @Override
        public void destroy() {
            if (this.reconstructor != null) {
                this.reconstructor.finishReconstruction();
            }
            this.onDecodedCallback = null;
        }

        @Override
        public void onDecoded(Parser.Decoder.Callback callback) {
            this.onDecodedCallback = callback;
        }
    }

    public static final class Encoder
    implements Parser.Encoder {
        @Override
        public void encode(Packet obj, Parser.Encoder.Callback callback) {
            if ((obj.type == 2 || obj.type == 3) && HasBinary.hasBinary(obj.data)) {
                int n2 = obj.type = obj.type == 2 ? 5 : 6;
            }
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("encoding packet %s", obj));
            }
            if (5 == obj.type || 6 == obj.type) {
                this.encodeAsBinary(obj, callback);
            } else {
                String encoding = this.encodeAsString(obj);
                callback.call(new String[]{encoding});
            }
        }

        private String encodeAsString(Packet obj) {
            StringBuilder str = new StringBuilder("" + obj.type);
            if (5 == obj.type || 6 == obj.type) {
                str.append(obj.attachments);
                str.append("-");
            }
            if (obj.nsp != null && obj.nsp.length() != 0 && !"/".equals(obj.nsp)) {
                str.append(obj.nsp);
                str.append(",");
            }
            if (obj.id >= 0) {
                str.append(obj.id);
            }
            if (obj.data != null) {
                str.append(obj.data);
            }
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(String.format("encoded %s as %s", obj, str));
            }
            return str.toString();
        }

        private void encodeAsBinary(Packet obj, Parser.Encoder.Callback callback) {
            Binary.DeconstructedPacket deconstruction = Binary.deconstructPacket(obj);
            String pack = this.encodeAsString(deconstruction.packet);
            ArrayList buffers = new ArrayList(Arrays.asList(deconstruction.buffers));
            buffers.add(0, pack);
            callback.call(buffers.toArray());
        }
    }
}

