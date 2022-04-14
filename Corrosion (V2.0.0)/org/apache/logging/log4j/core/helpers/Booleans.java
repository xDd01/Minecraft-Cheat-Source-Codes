/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.helpers;

public class Booleans {
    public static boolean parseBoolean(String s2, boolean defaultValue) {
        return "true".equalsIgnoreCase(s2) || defaultValue && !"false".equalsIgnoreCase(s2);
    }
}

