package org.apache.commons.lang3;

public class CharSequenceUtils
{
    private static final int NOT_FOUND = -1;
    
    public static CharSequence subSequence(final CharSequence cs, final int start) {
        return (cs == null) ? null : cs.subSequence(start, cs.length());
    }
    
    static int indexOf(final CharSequence cs, final int searchChar, int start) {
        if (cs instanceof String) {
            return ((String)cs).indexOf(searchChar, start);
        }
        final int sz = cs.length();
        if (start < 0) {
            start = 0;
        }
        if (searchChar < 65536) {
            for (int i = start; i < sz; ++i) {
                if (cs.charAt(i) == searchChar) {
                    return i;
                }
            }
        }
        if (searchChar <= 1114111) {
            final char[] chars = Character.toChars(searchChar);
            for (int j = start; j < sz - 1; ++j) {
                final char high = cs.charAt(j);
                final char low = cs.charAt(j + 1);
                if (high == chars[0] && low == chars[1]) {
                    return j;
                }
            }
        }
        return -1;
    }
    
    static int indexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
        return cs.toString().indexOf(searchChar.toString(), start);
    }
    
    static int lastIndexOf(final CharSequence cs, final int searchChar, int start) {
        if (cs instanceof String) {
            return ((String)cs).lastIndexOf(searchChar, start);
        }
        final int sz = cs.length();
        if (start < 0) {
            return -1;
        }
        if (start >= sz) {
            start = sz - 1;
        }
        if (searchChar < 65536) {
            for (int i = start; i >= 0; --i) {
                if (cs.charAt(i) == searchChar) {
                    return i;
                }
            }
        }
        if (searchChar <= 1114111) {
            final char[] chars = Character.toChars(searchChar);
            if (start == sz - 1) {
                return -1;
            }
            for (int j = start; j >= 0; --j) {
                final char high = cs.charAt(j);
                final char low = cs.charAt(j + 1);
                if (chars[0] == high && chars[1] == low) {
                    return j;
                }
            }
        }
        return -1;
    }
    
    static int lastIndexOf(final CharSequence cs, final CharSequence searchChar, final int start) {
        return cs.toString().lastIndexOf(searchChar.toString(), start);
    }
    
    static char[] toCharArray(final CharSequence cs) {
        if (cs instanceof String) {
            return ((String)cs).toCharArray();
        }
        final int sz = cs.length();
        final char[] array = new char[cs.length()];
        for (int i = 0; i < sz; ++i) {
            array[i] = cs.charAt(i);
        }
        return array;
    }
    
    static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart, final CharSequence substring, final int start, final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String)cs).regionMatches(ignoreCase, thisStart, (String)substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;
        final int srcLen = cs.length() - thisStart;
        final int otherLen = substring.length() - start;
        if (thisStart < 0 || start < 0 || length < 0) {
            return false;
        }
        if (srcLen < length || otherLen < length) {
            return false;
        }
        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);
            if (c1 == c2) {
                continue;
            }
            if (!ignoreCase) {
                return false;
            }
            if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }
        return true;
    }
}
