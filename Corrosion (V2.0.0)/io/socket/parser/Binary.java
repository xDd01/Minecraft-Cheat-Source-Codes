/*
 * Decompiled with CFR 0.152.
 */
package io.socket.parser;

import io.socket.parser.Packet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Binary {
    private static final String KEY_PLACEHOLDER = "_placeholder";
    private static final String KEY_NUM = "num";
    private static final Logger logger = Logger.getLogger(Binary.class.getName());

    public static DeconstructedPacket deconstructPacket(Packet packet) {
        ArrayList<byte[]> buffers = new ArrayList<byte[]>();
        packet.data = Binary._deconstructPacket(packet.data, buffers);
        packet.attachments = buffers.size();
        DeconstructedPacket result = new DeconstructedPacket();
        result.packet = packet;
        result.buffers = (byte[][])buffers.toArray((T[])new byte[buffers.size()][]);
        return result;
    }

    private static Object _deconstructPacket(Object data, List<byte[]> buffers) {
        if (data == null) {
            return null;
        }
        if (data instanceof byte[]) {
            JSONObject placeholder = new JSONObject();
            try {
                placeholder.put(KEY_PLACEHOLDER, true);
                placeholder.put(KEY_NUM, buffers.size());
            }
            catch (JSONException e2) {
                logger.log(Level.WARNING, "An error occured while putting data to JSONObject", e2);
                return null;
            }
            buffers.add((byte[])data);
            return placeholder;
        }
        if (data instanceof JSONArray) {
            JSONArray newData = new JSONArray();
            JSONArray _data = (JSONArray)data;
            int len = _data.length();
            for (int i2 = 0; i2 < len; ++i2) {
                try {
                    newData.put(i2, Binary._deconstructPacket(_data.get(i2), buffers));
                    continue;
                }
                catch (JSONException e3) {
                    logger.log(Level.WARNING, "An error occured while putting packet data to JSONObject", e3);
                    return null;
                }
            }
            return newData;
        }
        if (data instanceof JSONObject) {
            JSONObject newData = new JSONObject();
            JSONObject _data = (JSONObject)data;
            Iterator iterator = _data.keys();
            while (iterator.hasNext()) {
                String key = (String)iterator.next();
                try {
                    newData.put(key, Binary._deconstructPacket(_data.get(key), buffers));
                }
                catch (JSONException e4) {
                    logger.log(Level.WARNING, "An error occured while putting data to JSONObject", e4);
                    return null;
                }
            }
            return newData;
        }
        return data;
    }

    public static Packet reconstructPacket(Packet packet, byte[][] buffers) {
        packet.data = Binary._reconstructPacket(packet.data, buffers);
        packet.attachments = -1;
        return packet;
    }

    private static Object _reconstructPacket(Object data, byte[][] buffers) {
        if (data instanceof JSONArray) {
            JSONArray _data = (JSONArray)data;
            int len = _data.length();
            for (int i2 = 0; i2 < len; ++i2) {
                try {
                    _data.put(i2, Binary._reconstructPacket(_data.get(i2), buffers));
                    continue;
                }
                catch (JSONException e2) {
                    logger.log(Level.WARNING, "An error occured while putting packet data to JSONObject", e2);
                    return null;
                }
            }
            return _data;
        }
        if (data instanceof JSONObject) {
            JSONObject _data = (JSONObject)data;
            if (_data.optBoolean(KEY_PLACEHOLDER)) {
                int num = _data.optInt(KEY_NUM, -1);
                return num >= 0 && num < buffers.length ? buffers[num] : null;
            }
            Iterator iterator = _data.keys();
            while (iterator.hasNext()) {
                String key = (String)iterator.next();
                try {
                    _data.put(key, Binary._reconstructPacket(_data.get(key), buffers));
                }
                catch (JSONException e3) {
                    logger.log(Level.WARNING, "An error occured while putting data to JSONObject", e3);
                    return null;
                }
            }
            return _data;
        }
        return data;
    }

    public static class DeconstructedPacket {
        public Packet packet;
        public byte[][] buffers;
    }
}

