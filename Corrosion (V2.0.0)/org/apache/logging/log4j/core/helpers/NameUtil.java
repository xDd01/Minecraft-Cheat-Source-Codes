/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.helpers;

import java.security.MessageDigest;

public final class NameUtil {
    private static final int MASK = 255;

    private NameUtil() {
    }

    public static String getSubName(String name) {
        if (name.isEmpty()) {
            return null;
        }
        int i2 = name.lastIndexOf(46);
        return i2 > 0 ? name.substring(0, i2) : "";
    }

    public static String md5(String string) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(string.getBytes());
            byte[] bytes = digest.digest();
            StringBuilder md5 = new StringBuilder();
            for (byte b2 : bytes) {
                String hex = Integer.toHexString(0xFF & b2);
                if (hex.length() == 1) {
                    md5.append('0');
                }
                md5.append(hex);
            }
            return md5.toString();
        }
        catch (Exception ex2) {
            return string;
        }
    }
}

