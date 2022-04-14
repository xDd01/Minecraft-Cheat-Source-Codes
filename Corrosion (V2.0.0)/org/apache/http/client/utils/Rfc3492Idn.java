/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.utils;

import java.util.StringTokenizer;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.utils.Idn;

@Immutable
public class Rfc3492Idn
implements Idn {
    private static final int base = 36;
    private static final int tmin = 1;
    private static final int tmax = 26;
    private static final int skew = 38;
    private static final int damp = 700;
    private static final int initial_bias = 72;
    private static final int initial_n = 128;
    private static final char delimiter = '-';
    private static final String ACE_PREFIX = "xn--";

    private int adapt(int delta, int numpoints, boolean firsttime) {
        int d2 = delta;
        d2 = firsttime ? (d2 /= 700) : (d2 /= 2);
        d2 += d2 / numpoints;
        int k2 = 0;
        while (d2 > 455) {
            d2 /= 35;
            k2 += 36;
        }
        return k2 + 36 * d2 / (d2 + 38);
    }

    private int digit(char c2) {
        if (c2 >= 'A' && c2 <= 'Z') {
            return c2 - 65;
        }
        if (c2 >= 'a' && c2 <= 'z') {
            return c2 - 97;
        }
        if (c2 >= '0' && c2 <= '9') {
            return c2 - 48 + 26;
        }
        throw new IllegalArgumentException("illegal digit: " + c2);
    }

    public String toUnicode(String punycode) {
        StringBuilder unicode = new StringBuilder(punycode.length());
        StringTokenizer tok = new StringTokenizer(punycode, ".");
        while (tok.hasMoreTokens()) {
            String t2 = tok.nextToken();
            if (unicode.length() > 0) {
                unicode.append('.');
            }
            if (t2.startsWith(ACE_PREFIX)) {
                t2 = this.decode(t2.substring(4));
            }
            unicode.append(t2);
        }
        return unicode.toString();
    }

    protected String decode(String s2) {
        String input = s2;
        int n2 = 128;
        int i2 = 0;
        int bias = 72;
        StringBuilder output = new StringBuilder(input.length());
        int lastdelim = input.lastIndexOf(45);
        if (lastdelim != -1) {
            output.append(input.subSequence(0, lastdelim));
            input = input.substring(lastdelim + 1);
        }
        while (input.length() > 0) {
            int oldi = i2;
            int w2 = 1;
            int k2 = 36;
            while (input.length() != 0) {
                char c2 = input.charAt(0);
                input = input.substring(1);
                int digit = this.digit(c2);
                i2 += digit * w2;
                int t2 = k2 <= bias + 1 ? 1 : (k2 >= bias + 26 ? 26 : k2 - bias);
                if (digit < t2) break;
                w2 *= 36 - t2;
                k2 += 36;
            }
            bias = this.adapt(i2 - oldi, output.length() + 1, oldi == 0);
            n2 += i2 / (output.length() + 1);
            output.insert(i2 %= output.length() + 1, (char)n2);
            ++i2;
        }
        return output.toString();
    }
}

