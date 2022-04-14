/*
 * Decompiled with CFR 0.152.
 */
package io.socket.global;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

public class Global {
    private Global() {
    }

    public static String encodeURIComponent(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8").replace("+", "%20").replace("%21", "!").replace("%27", "'").replace("%28", "(").replace("%29", ")").replace("%7E", "~");
        }
        catch (UnsupportedEncodingException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static String decodeURIComponent(String str) {
        try {
            return URLDecoder.decode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e2) {
            throw new RuntimeException(e2);
        }
    }
}

