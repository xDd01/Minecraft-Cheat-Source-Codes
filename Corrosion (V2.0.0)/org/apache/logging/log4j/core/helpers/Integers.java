/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.helpers;

import org.apache.logging.log4j.core.helpers.Strings;

public class Integers {
    public static int parseInt(String s2, int defaultValue) {
        return Strings.isEmpty(s2) ? defaultValue : Integer.parseInt(s2);
    }

    public static int parseInt(String s2) {
        return Integers.parseInt(s2, 0);
    }
}

