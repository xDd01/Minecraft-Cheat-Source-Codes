/*
 * Decompiled with CFR 0.152.
 */
package io.socket.yeast;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public final class Yeast {
    private static char[] alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz-_".toCharArray();
    private static int length = alphabet.length;
    private static int seed = 0;
    private static String prev;
    private static Map<Character, Integer> map;

    private Yeast() {
    }

    public static String encode(long num) {
        StringBuilder encoded = new StringBuilder();
        long dividedNum = num;
        do {
            encoded.insert(0, alphabet[(int)(dividedNum % (long)length)]);
        } while ((dividedNum /= (long)length) > 0L);
        return encoded.toString();
    }

    public static long decode(String str) {
        long decoded = 0L;
        for (char c2 : str.toCharArray()) {
            decoded = decoded * (long)length + (long)map.get(Character.valueOf(c2)).intValue();
        }
        return decoded;
    }

    public static String yeast() {
        String now = Yeast.encode(new Date().getTime());
        if (!now.equals(prev)) {
            seed = 0;
            prev = now;
            return now;
        }
        return now + "." + Yeast.encode(seed++);
    }

    static {
        map = new HashMap<Character, Integer>(length);
        for (int i2 = 0; i2 < length; ++i2) {
            map.put(Character.valueOf(alphabet[i2]), i2);
        }
    }
}

