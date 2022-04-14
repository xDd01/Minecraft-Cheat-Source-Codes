/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.pattern;

public final class FormattingInfo {
    private static final char[] SPACES = new char[]{' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
    private static final FormattingInfo DEFAULT = new FormattingInfo(false, 0, Integer.MAX_VALUE);
    private final int minLength;
    private final int maxLength;
    private final boolean leftAlign;

    public FormattingInfo(boolean leftAlign, int minLength, int maxLength) {
        this.leftAlign = leftAlign;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    public static FormattingInfo getDefault() {
        return DEFAULT;
    }

    public boolean isLeftAligned() {
        return this.leftAlign;
    }

    public int getMinLength() {
        return this.minLength;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public void format(int fieldStart, StringBuilder buffer) {
        int rawLength = buffer.length() - fieldStart;
        if (rawLength > this.maxLength) {
            buffer.delete(fieldStart, buffer.length() - this.maxLength);
        } else if (rawLength < this.minLength) {
            if (this.leftAlign) {
                int fieldEnd = buffer.length();
                buffer.setLength(fieldStart + this.minLength);
                for (int i2 = fieldEnd; i2 < buffer.length(); ++i2) {
                    buffer.setCharAt(i2, ' ');
                }
            } else {
                int padLength;
                for (padLength = this.minLength - rawLength; padLength > SPACES.length; padLength -= SPACES.length) {
                    buffer.insert(fieldStart, SPACES);
                }
                buffer.insert(fieldStart, SPACES, 0, padLength);
            }
        }
    }

    public String toString() {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(super.toString());
        sb2.append("[leftAlign=");
        sb2.append(this.leftAlign);
        sb2.append(", maxLength=");
        sb2.append(this.maxLength);
        sb2.append(", minLength=");
        sb2.append(this.minLength);
        sb2.append("]");
        return sb2.toString();
    }
}

