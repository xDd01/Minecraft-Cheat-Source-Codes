/*
 * Decompiled with CFR 0.152.
 */
package org.apache.logging.log4j.core.pattern;

import java.util.ArrayList;
import java.util.List;

public abstract class NameAbbreviator {
    private static final NameAbbreviator DEFAULT = new NOPAbbreviator();

    public static NameAbbreviator getAbbreviator(String pattern) {
        if (pattern.length() > 0) {
            int i2;
            String trimmed = pattern.trim();
            if (trimmed.isEmpty()) {
                return DEFAULT;
            }
            for (i2 = 0; i2 < trimmed.length() && trimmed.charAt(i2) >= '0' && trimmed.charAt(i2) <= '9'; ++i2) {
            }
            if (i2 == trimmed.length()) {
                return new MaxElementAbbreviator(Integer.parseInt(trimmed));
            }
            ArrayList<PatternAbbreviatorFragment> fragments = new ArrayList<PatternAbbreviatorFragment>(5);
            for (int pos = 0; pos < trimmed.length() && pos >= 0; ++pos) {
                int charCount;
                int ellipsisPos = pos;
                if (trimmed.charAt(pos) == '*') {
                    charCount = Integer.MAX_VALUE;
                    ++ellipsisPos;
                } else if (trimmed.charAt(pos) >= '0' && trimmed.charAt(pos) <= '9') {
                    charCount = trimmed.charAt(pos) - 48;
                    ++ellipsisPos;
                } else {
                    charCount = 0;
                }
                char ellipsis = '\u0000';
                if (ellipsisPos < trimmed.length() && (ellipsis = trimmed.charAt(ellipsisPos)) == '.') {
                    ellipsis = '\u0000';
                }
                fragments.add(new PatternAbbreviatorFragment(charCount, ellipsis));
                pos = trimmed.indexOf(46, pos);
                if (pos == -1) break;
            }
            return new PatternAbbreviator(fragments);
        }
        return DEFAULT;
    }

    public static NameAbbreviator getDefaultAbbreviator() {
        return DEFAULT;
    }

    public abstract String abbreviate(String var1);

    private static class PatternAbbreviator
    extends NameAbbreviator {
        private final PatternAbbreviatorFragment[] fragments;

        public PatternAbbreviator(List<PatternAbbreviatorFragment> fragments) {
            if (fragments.size() == 0) {
                throw new IllegalArgumentException("fragments must have at least one element");
            }
            this.fragments = new PatternAbbreviatorFragment[fragments.size()];
            fragments.toArray(this.fragments);
        }

        @Override
        public String abbreviate(String buf) {
            int pos = 0;
            StringBuilder sb2 = new StringBuilder(buf);
            for (int i2 = 0; i2 < this.fragments.length - 1 && pos < buf.length(); ++i2) {
                pos = this.fragments[i2].abbreviate(sb2, pos);
            }
            PatternAbbreviatorFragment terminalFragment = this.fragments[this.fragments.length - 1];
            while (pos < buf.length() && pos >= 0) {
                pos = terminalFragment.abbreviate(sb2, pos);
            }
            return sb2.toString();
        }
    }

    private static class PatternAbbreviatorFragment {
        private final int charCount;
        private final char ellipsis;

        public PatternAbbreviatorFragment(int charCount, char ellipsis) {
            this.charCount = charCount;
            this.ellipsis = ellipsis;
        }

        public int abbreviate(StringBuilder buf, int startPos) {
            int nextDot = buf.toString().indexOf(46, startPos);
            if (nextDot != -1) {
                if (nextDot - startPos > this.charCount) {
                    buf.delete(startPos + this.charCount, nextDot);
                    nextDot = startPos + this.charCount;
                    if (this.ellipsis != '\u0000') {
                        buf.insert(nextDot, this.ellipsis);
                        ++nextDot;
                    }
                }
                ++nextDot;
            }
            return nextDot;
        }
    }

    private static class MaxElementAbbreviator
    extends NameAbbreviator {
        private final int count;

        public MaxElementAbbreviator(int count) {
            this.count = count < 1 ? 1 : count;
        }

        @Override
        public String abbreviate(String buf) {
            int end = buf.length() - 1;
            for (int i2 = this.count; i2 > 0; --i2) {
                if ((end = buf.lastIndexOf(46, end - 1)) != -1) continue;
                return buf;
            }
            return buf.substring(end + 1);
        }
    }

    private static class NOPAbbreviator
    extends NameAbbreviator {
        @Override
        public String abbreviate(String buf) {
            return buf;
        }
    }
}

