package org.apache.commons.lang3.text.translate;

import java.io.*;
import java.util.*;

@Deprecated
public abstract class CharSequenceTranslator
{
    static final char[] HEX_DIGITS;
    
    public abstract int translate(final CharSequence p0, final int p1, final Writer p2) throws IOException;
    
    public final String translate(final CharSequence input) {
        if (input == null) {
            return null;
        }
        try {
            final StringWriter writer = new StringWriter(input.length() * 2);
            this.translate(input, writer);
            return writer.toString();
        }
        catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
    
    public final void translate(final CharSequence input, final Writer out) throws IOException {
        if (out == null) {
            throw new IllegalArgumentException("The Writer must not be null");
        }
        if (input == null) {
            return;
        }
        int pos = 0;
        final int len = input.length();
        while (pos < len) {
            final int consumed = this.translate(input, pos, out);
            if (consumed == 0) {
                final char c1 = input.charAt(pos);
                out.write(c1);
                ++pos;
                if (!Character.isHighSurrogate(c1) || pos >= len) {
                    continue;
                }
                final char c2 = input.charAt(pos);
                if (!Character.isLowSurrogate(c2)) {
                    continue;
                }
                out.write(c2);
                ++pos;
            }
            else {
                for (int pt = 0; pt < consumed; ++pt) {
                    pos += Character.charCount(Character.codePointAt(input, pos));
                }
            }
        }
    }
    
    public final CharSequenceTranslator with(final CharSequenceTranslator... translators) {
        final CharSequenceTranslator[] newArray = new CharSequenceTranslator[translators.length + 1];
        newArray[0] = this;
        System.arraycopy(translators, 0, newArray, 1, translators.length);
        return new AggregateTranslator(newArray);
    }
    
    public static String hex(final int codepoint) {
        return Integer.toHexString(codepoint).toUpperCase(Locale.ENGLISH);
    }
    
    static {
        HEX_DIGITS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
}
