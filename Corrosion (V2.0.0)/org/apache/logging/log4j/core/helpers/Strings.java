/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.helpers;

public class Strings {
    public static boolean isEmpty(CharSequence cs2) {
        return cs2 == null || cs2.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs2) {
        return !Strings.isEmpty(cs2);
    }
}

