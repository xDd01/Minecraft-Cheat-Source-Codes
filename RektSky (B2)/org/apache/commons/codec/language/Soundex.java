package org.apache.commons.codec.language;

import org.apache.commons.codec.*;

public class Soundex implements StringEncoder
{
    public static final char SILENT_MARKER = '-';
    public static final String US_ENGLISH_MAPPING_STRING = "01230120022455012623010202";
    private static final char[] US_ENGLISH_MAPPING;
    public static final Soundex US_ENGLISH;
    public static final Soundex US_ENGLISH_SIMPLIFIED;
    public static final Soundex US_ENGLISH_GENEALOGY;
    @Deprecated
    private int maxLength;
    private final char[] soundexMapping;
    private final boolean specialCaseHW;
    
    public Soundex() {
        this.maxLength = 4;
        this.soundexMapping = Soundex.US_ENGLISH_MAPPING;
        this.specialCaseHW = true;
    }
    
    public Soundex(final char[] mapping) {
        this.maxLength = 4;
        System.arraycopy(mapping, 0, this.soundexMapping = new char[mapping.length], 0, mapping.length);
        this.specialCaseHW = !this.hasMarker(this.soundexMapping);
    }
    
    private boolean hasMarker(final char[] mapping) {
        for (final char ch : mapping) {
            if (ch == '-') {
                return true;
            }
        }
        return false;
    }
    
    public Soundex(final String mapping) {
        this.maxLength = 4;
        this.soundexMapping = mapping.toCharArray();
        this.specialCaseHW = !this.hasMarker(this.soundexMapping);
    }
    
    public Soundex(final String mapping, final boolean specialCaseHW) {
        this.maxLength = 4;
        this.soundexMapping = mapping.toCharArray();
        this.specialCaseHW = specialCaseHW;
    }
    
    public int difference(final String s1, final String s2) throws EncoderException {
        return SoundexUtils.difference(this, s1, s2);
    }
    
    @Override
    public Object encode(final Object obj) throws EncoderException {
        if (!(obj instanceof String)) {
            throw new EncoderException("Parameter supplied to Soundex encode is not of type java.lang.String");
        }
        return this.soundex((String)obj);
    }
    
    @Override
    public String encode(final String str) {
        return this.soundex(str);
    }
    
    @Deprecated
    public int getMaxLength() {
        return this.maxLength;
    }
    
    private char map(final char ch) {
        final int index = ch - 'A';
        if (index < 0 || index >= this.soundexMapping.length) {
            throw new IllegalArgumentException("The character is not mapped: " + ch + " (index=" + index + ")");
        }
        return this.soundexMapping[index];
    }
    
    @Deprecated
    public void setMaxLength(final int maxLength) {
        this.maxLength = maxLength;
    }
    
    public String soundex(String str) {
        if (str == null) {
            return null;
        }
        str = SoundexUtils.clean(str);
        if (str.length() == 0) {
            return str;
        }
        final char[] out = { '0', '0', '0', '0' };
        int count = 0;
        final char first = str.charAt(0);
        out[count++] = first;
        char lastDigit = this.map(first);
        for (int i = 1; i < str.length() && count < out.length; ++i) {
            final char ch = str.charAt(i);
            if (this.specialCaseHW) {
                if (ch == 'H') {
                    continue;
                }
                if (ch == 'W') {
                    continue;
                }
            }
            final char digit = this.map(ch);
            if (digit != '-') {
                if (digit != '0' && digit != lastDigit) {
                    out[count++] = digit;
                }
                lastDigit = digit;
            }
        }
        return new String(out);
    }
    
    static {
        US_ENGLISH_MAPPING = "01230120022455012623010202".toCharArray();
        US_ENGLISH = new Soundex();
        US_ENGLISH_SIMPLIFIED = new Soundex("01230120022455012623010202", false);
        US_ENGLISH_GENEALOGY = new Soundex("-123-12--22455-12623-1-2-2");
    }
}
