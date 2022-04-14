/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.cookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import org.apache.http.annotation.Immutable;
import org.apache.http.impl.cookie.PublicSuffixFilter;

@Immutable
public class PublicSuffixListParser {
    private static final int MAX_LINE_LEN = 256;
    private final PublicSuffixFilter filter;

    PublicSuffixListParser(PublicSuffixFilter filter) {
        this.filter = filter;
    }

    public void parse(Reader list) throws IOException {
        ArrayList<String> rules = new ArrayList<String>();
        ArrayList<String> exceptions = new ArrayList<String>();
        BufferedReader r2 = new BufferedReader(list);
        StringBuilder sb2 = new StringBuilder(256);
        boolean more = true;
        while (more) {
            boolean isException;
            more = this.readLine(r2, sb2);
            String line = sb2.toString();
            if (line.length() == 0 || line.startsWith("//")) continue;
            if (line.startsWith(".")) {
                line = line.substring(1);
            }
            if (isException = line.startsWith("!")) {
                line = line.substring(1);
            }
            if (isException) {
                exceptions.add(line);
                continue;
            }
            rules.add(line);
        }
        this.filter.setPublicSuffixes(rules);
        this.filter.setExceptions(exceptions);
    }

    private boolean readLine(Reader r2, StringBuilder sb2) throws IOException {
        char c2;
        int b2;
        sb2.setLength(0);
        boolean hitWhitespace = false;
        while ((b2 = r2.read()) != -1 && (c2 = (char)b2) != '\n') {
            if (Character.isWhitespace(c2)) {
                hitWhitespace = true;
            }
            if (!hitWhitespace) {
                sb2.append(c2);
            }
            if (sb2.length() <= 256) continue;
            throw new IOException("Line too long");
        }
        return b2 != -1;
    }
}

