package org.apache.commons.lang3.text.translate;

import java.io.*;

@Deprecated
public class UnicodeEscaper extends CodePointTranslator
{
    private final int below;
    private final int above;
    private final boolean between;
    
    public UnicodeEscaper() {
        this(0, Integer.MAX_VALUE, true);
    }
    
    protected UnicodeEscaper(final int below, final int above, final boolean between) {
        this.below = below;
        this.above = above;
        this.between = between;
    }
    
    public static UnicodeEscaper below(final int codepoint) {
        return outsideOf(codepoint, Integer.MAX_VALUE);
    }
    
    public static UnicodeEscaper above(final int codepoint) {
        return outsideOf(0, codepoint);
    }
    
    public static UnicodeEscaper outsideOf(final int codepointLow, final int codepointHigh) {
        return new UnicodeEscaper(codepointLow, codepointHigh, false);
    }
    
    public static UnicodeEscaper between(final int codepointLow, final int codepointHigh) {
        return new UnicodeEscaper(codepointLow, codepointHigh, true);
    }
    
    @Override
    public boolean translate(final int codepoint, final Writer out) throws IOException {
        if (this.between) {
            if (codepoint < this.below || codepoint > this.above) {
                return false;
            }
        }
        else if (codepoint >= this.below && codepoint <= this.above) {
            return false;
        }
        if (codepoint > 65535) {
            out.write(this.toUtf16Escape(codepoint));
        }
        else {
            out.write("\\u");
            out.write(UnicodeEscaper.HEX_DIGITS[codepoint >> 12 & 0xF]);
            out.write(UnicodeEscaper.HEX_DIGITS[codepoint >> 8 & 0xF]);
            out.write(UnicodeEscaper.HEX_DIGITS[codepoint >> 4 & 0xF]);
            out.write(UnicodeEscaper.HEX_DIGITS[codepoint & 0xF]);
        }
        return true;
    }
    
    protected String toUtf16Escape(final int codepoint) {
        return "\\u" + CharSequenceTranslator.hex(codepoint);
    }
}
