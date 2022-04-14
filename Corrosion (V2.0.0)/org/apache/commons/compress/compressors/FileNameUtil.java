/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.compressors;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class FileNameUtil {
    private final Map<String, String> compressSuffix = new HashMap<String, String>();
    private final Map<String, String> uncompressSuffix;
    private final int longestCompressedSuffix;
    private final int shortestCompressedSuffix;
    private final int longestUncompressedSuffix;
    private final int shortestUncompressedSuffix;
    private final String defaultExtension;

    public FileNameUtil(Map<String, String> uncompressSuffix, String defaultExtension) {
        this.uncompressSuffix = Collections.unmodifiableMap(uncompressSuffix);
        int lc2 = Integer.MIN_VALUE;
        int sc2 = Integer.MAX_VALUE;
        int lu2 = Integer.MIN_VALUE;
        int su = Integer.MAX_VALUE;
        for (Map.Entry<String, String> ent : uncompressSuffix.entrySet()) {
            String u2;
            int ul;
            int cl2 = ent.getKey().length();
            if (cl2 > lc2) {
                lc2 = cl2;
            }
            if (cl2 < sc2) {
                sc2 = cl2;
            }
            if ((ul = (u2 = ent.getValue()).length()) <= 0) continue;
            if (!this.compressSuffix.containsKey(u2)) {
                this.compressSuffix.put(u2, ent.getKey());
            }
            if (ul > lu2) {
                lu2 = ul;
            }
            if (ul >= su) continue;
            su = ul;
        }
        this.longestCompressedSuffix = lc2;
        this.longestUncompressedSuffix = lu2;
        this.shortestCompressedSuffix = sc2;
        this.shortestUncompressedSuffix = su;
        this.defaultExtension = defaultExtension;
    }

    public boolean isCompressedFilename(String filename) {
        String lower = filename.toLowerCase(Locale.ENGLISH);
        int n2 = lower.length();
        for (int i2 = this.shortestCompressedSuffix; i2 <= this.longestCompressedSuffix && i2 < n2; ++i2) {
            if (!this.uncompressSuffix.containsKey(lower.substring(n2 - i2))) continue;
            return true;
        }
        return false;
    }

    public String getUncompressedFilename(String filename) {
        String lower = filename.toLowerCase(Locale.ENGLISH);
        int n2 = lower.length();
        for (int i2 = this.shortestCompressedSuffix; i2 <= this.longestCompressedSuffix && i2 < n2; ++i2) {
            String suffix = this.uncompressSuffix.get(lower.substring(n2 - i2));
            if (suffix == null) continue;
            return filename.substring(0, n2 - i2) + suffix;
        }
        return filename;
    }

    public String getCompressedFilename(String filename) {
        String lower = filename.toLowerCase(Locale.ENGLISH);
        int n2 = lower.length();
        for (int i2 = this.shortestUncompressedSuffix; i2 <= this.longestUncompressedSuffix && i2 < n2; ++i2) {
            String suffix = this.compressSuffix.get(lower.substring(n2 - i2));
            if (suffix == null) continue;
            return filename.substring(0, n2 - i2) + suffix;
        }
        return filename + this.defaultExtension;
    }
}

