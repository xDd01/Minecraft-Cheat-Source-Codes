/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class StrUtils {
    public static boolean equalsMask(String p_equalsMask_0_, String p_equalsMask_1_, char p_equalsMask_2_, char p_equalsMask_3_) {
        if (p_equalsMask_1_ != null && p_equalsMask_0_ != null) {
            String s1;
            if (p_equalsMask_1_.indexOf(p_equalsMask_2_) < 0) {
                return p_equalsMask_1_.indexOf(p_equalsMask_3_) < 0 ? p_equalsMask_1_.equals(p_equalsMask_0_) : StrUtils.equalsMaskSingle(p_equalsMask_0_, p_equalsMask_1_, p_equalsMask_3_);
            }
            ArrayList<String> list = new ArrayList<String>();
            String s2 = "" + p_equalsMask_2_;
            if (p_equalsMask_1_.startsWith(s2)) {
                list.add("");
            }
            StringTokenizer stringtokenizer = new StringTokenizer(p_equalsMask_1_, s2);
            while (stringtokenizer.hasMoreElements()) {
                list.add(stringtokenizer.nextToken());
            }
            if (p_equalsMask_1_.endsWith(s2)) {
                list.add("");
            }
            if (!StrUtils.startsWithMaskSingle(p_equalsMask_0_, s1 = (String)list.get(0), p_equalsMask_3_)) {
                return false;
            }
            String s22 = (String)list.get(list.size() - 1);
            if (!StrUtils.endsWithMaskSingle(p_equalsMask_0_, s22, p_equalsMask_3_)) {
                return false;
            }
            int i2 = 0;
            for (int j2 = 0; j2 < list.size(); ++j2) {
                String s3 = (String)list.get(j2);
                if (s3.length() <= 0) continue;
                int k2 = StrUtils.indexOfMaskSingle(p_equalsMask_0_, s3, i2, p_equalsMask_3_);
                if (k2 < 0) {
                    return false;
                }
                i2 = k2 + s3.length();
            }
            return true;
        }
        return p_equalsMask_1_ == p_equalsMask_0_;
    }

    private static boolean equalsMaskSingle(String p_equalsMaskSingle_0_, String p_equalsMaskSingle_1_, char p_equalsMaskSingle_2_) {
        if (p_equalsMaskSingle_0_ != null && p_equalsMaskSingle_1_ != null) {
            if (p_equalsMaskSingle_0_.length() != p_equalsMaskSingle_1_.length()) {
                return false;
            }
            for (int i2 = 0; i2 < p_equalsMaskSingle_1_.length(); ++i2) {
                char c0 = p_equalsMaskSingle_1_.charAt(i2);
                if (c0 == p_equalsMaskSingle_2_ || p_equalsMaskSingle_0_.charAt(i2) == c0) continue;
                return false;
            }
            return true;
        }
        return p_equalsMaskSingle_0_ == p_equalsMaskSingle_1_;
    }

    private static int indexOfMaskSingle(String p_indexOfMaskSingle_0_, String p_indexOfMaskSingle_1_, int p_indexOfMaskSingle_2_, char p_indexOfMaskSingle_3_) {
        if (p_indexOfMaskSingle_0_ != null && p_indexOfMaskSingle_1_ != null) {
            if (p_indexOfMaskSingle_2_ >= 0 && p_indexOfMaskSingle_2_ <= p_indexOfMaskSingle_0_.length()) {
                if (p_indexOfMaskSingle_0_.length() < p_indexOfMaskSingle_2_ + p_indexOfMaskSingle_1_.length()) {
                    return -1;
                }
                int i2 = p_indexOfMaskSingle_2_;
                while (i2 + p_indexOfMaskSingle_1_.length() <= p_indexOfMaskSingle_0_.length()) {
                    String s2 = p_indexOfMaskSingle_0_.substring(i2, i2 + p_indexOfMaskSingle_1_.length());
                    if (StrUtils.equalsMaskSingle(s2, p_indexOfMaskSingle_1_, p_indexOfMaskSingle_3_)) {
                        return i2;
                    }
                    ++i2;
                }
                return -1;
            }
            return -1;
        }
        return -1;
    }

    private static boolean endsWithMaskSingle(String p_endsWithMaskSingle_0_, String p_endsWithMaskSingle_1_, char p_endsWithMaskSingle_2_) {
        if (p_endsWithMaskSingle_0_ != null && p_endsWithMaskSingle_1_ != null) {
            if (p_endsWithMaskSingle_0_.length() < p_endsWithMaskSingle_1_.length()) {
                return false;
            }
            String s2 = p_endsWithMaskSingle_0_.substring(p_endsWithMaskSingle_0_.length() - p_endsWithMaskSingle_1_.length(), p_endsWithMaskSingle_0_.length());
            return StrUtils.equalsMaskSingle(s2, p_endsWithMaskSingle_1_, p_endsWithMaskSingle_2_);
        }
        return p_endsWithMaskSingle_0_ == p_endsWithMaskSingle_1_;
    }

    private static boolean startsWithMaskSingle(String p_startsWithMaskSingle_0_, String p_startsWithMaskSingle_1_, char p_startsWithMaskSingle_2_) {
        if (p_startsWithMaskSingle_0_ != null && p_startsWithMaskSingle_1_ != null) {
            if (p_startsWithMaskSingle_0_.length() < p_startsWithMaskSingle_1_.length()) {
                return false;
            }
            String s2 = p_startsWithMaskSingle_0_.substring(0, p_startsWithMaskSingle_1_.length());
            return StrUtils.equalsMaskSingle(s2, p_startsWithMaskSingle_1_, p_startsWithMaskSingle_2_);
        }
        return p_startsWithMaskSingle_0_ == p_startsWithMaskSingle_1_;
    }

    public static boolean equalsMask(String p_equalsMask_0_, String[] p_equalsMask_1_, char p_equalsMask_2_) {
        for (int i2 = 0; i2 < p_equalsMask_1_.length; ++i2) {
            String s2 = p_equalsMask_1_[i2];
            if (!StrUtils.equalsMask(p_equalsMask_0_, s2, p_equalsMask_2_)) continue;
            return true;
        }
        return false;
    }

    public static boolean equalsMask(String p_equalsMask_0_, String p_equalsMask_1_, char p_equalsMask_2_) {
        if (p_equalsMask_1_ != null && p_equalsMask_0_ != null) {
            String s1;
            if (p_equalsMask_1_.indexOf(p_equalsMask_2_) < 0) {
                return p_equalsMask_1_.equals(p_equalsMask_0_);
            }
            ArrayList<String> list = new ArrayList<String>();
            String s2 = "" + p_equalsMask_2_;
            if (p_equalsMask_1_.startsWith(s2)) {
                list.add("");
            }
            StringTokenizer stringtokenizer = new StringTokenizer(p_equalsMask_1_, s2);
            while (stringtokenizer.hasMoreElements()) {
                list.add(stringtokenizer.nextToken());
            }
            if (p_equalsMask_1_.endsWith(s2)) {
                list.add("");
            }
            if (!p_equalsMask_0_.startsWith(s1 = (String)list.get(0))) {
                return false;
            }
            String s22 = (String)list.get(list.size() - 1);
            if (!p_equalsMask_0_.endsWith(s22)) {
                return false;
            }
            int i2 = 0;
            for (int j2 = 0; j2 < list.size(); ++j2) {
                String s3 = (String)list.get(j2);
                if (s3.length() <= 0) continue;
                int k2 = p_equalsMask_0_.indexOf(s3, i2);
                if (k2 < 0) {
                    return false;
                }
                i2 = k2 + s3.length();
            }
            return true;
        }
        return p_equalsMask_1_ == p_equalsMask_0_;
    }

    public static String[] split(String p_split_0_, String p_split_1_) {
        if (p_split_0_ != null && p_split_0_.length() > 0) {
            if (p_split_1_ == null) {
                return new String[]{p_split_0_};
            }
            ArrayList<String> list = new ArrayList<String>();
            int i2 = 0;
            for (int j2 = 0; j2 < p_split_0_.length(); ++j2) {
                char c0 = p_split_0_.charAt(j2);
                if (!StrUtils.equals(c0, p_split_1_)) continue;
                list.add(p_split_0_.substring(i2, j2));
                i2 = j2 + 1;
            }
            list.add(p_split_0_.substring(i2, p_split_0_.length()));
            return list.toArray(new String[list.size()]);
        }
        return new String[0];
    }

    private static boolean equals(char p_equals_0_, String p_equals_1_) {
        for (int i2 = 0; i2 < p_equals_1_.length(); ++i2) {
            if (p_equals_1_.charAt(i2) != p_equals_0_) continue;
            return true;
        }
        return false;
    }

    public static boolean equalsTrim(String p_equalsTrim_0_, String p_equalsTrim_1_) {
        if (p_equalsTrim_0_ != null) {
            p_equalsTrim_0_ = p_equalsTrim_0_.trim();
        }
        if (p_equalsTrim_1_ != null) {
            p_equalsTrim_1_ = p_equalsTrim_1_.trim();
        }
        return StrUtils.equals(p_equalsTrim_0_, (Object)p_equalsTrim_1_);
    }

    public static boolean isEmpty(String p_isEmpty_0_) {
        return p_isEmpty_0_ == null ? true : p_isEmpty_0_.trim().length() <= 0;
    }

    public static String stringInc(String p_stringInc_0_) {
        int i2 = StrUtils.parseInt(p_stringInc_0_, -1);
        if (i2 == -1) {
            return "";
        }
        String s2 = "" + ++i2;
        return s2.length() > p_stringInc_0_.length() ? "" : StrUtils.fillLeft("" + i2, p_stringInc_0_.length(), '0');
    }

    public static int parseInt(String p_parseInt_0_, int p_parseInt_1_) {
        if (p_parseInt_0_ == null) {
            return p_parseInt_1_;
        }
        try {
            return Integer.parseInt(p_parseInt_0_);
        }
        catch (NumberFormatException var3) {
            return p_parseInt_1_;
        }
    }

    public static boolean isFilled(String p_isFilled_0_) {
        return !StrUtils.isEmpty(p_isFilled_0_);
    }

    public static String addIfNotContains(String p_addIfNotContains_0_, String p_addIfNotContains_1_) {
        for (int i2 = 0; i2 < p_addIfNotContains_1_.length(); ++i2) {
            if (p_addIfNotContains_0_.indexOf(p_addIfNotContains_1_.charAt(i2)) >= 0) continue;
            p_addIfNotContains_0_ = p_addIfNotContains_0_ + p_addIfNotContains_1_.charAt(i2);
        }
        return p_addIfNotContains_0_;
    }

    public static String fillLeft(String p_fillLeft_0_, int p_fillLeft_1_, char p_fillLeft_2_) {
        if (p_fillLeft_0_ == null) {
            p_fillLeft_0_ = "";
        }
        if (p_fillLeft_0_.length() >= p_fillLeft_1_) {
            return p_fillLeft_0_;
        }
        StringBuffer stringbuffer = new StringBuffer(p_fillLeft_0_);
        while (stringbuffer.length() < p_fillLeft_1_) {
            stringbuffer.insert(0, p_fillLeft_2_);
        }
        return stringbuffer.toString();
    }

    public static String fillRight(String p_fillRight_0_, int p_fillRight_1_, char p_fillRight_2_) {
        if (p_fillRight_0_ == null) {
            p_fillRight_0_ = "";
        }
        if (p_fillRight_0_.length() >= p_fillRight_1_) {
            return p_fillRight_0_;
        }
        StringBuffer stringbuffer = new StringBuffer(p_fillRight_0_);
        while (stringbuffer.length() < p_fillRight_1_) {
            stringbuffer.append(p_fillRight_2_);
        }
        return stringbuffer.toString();
    }

    public static boolean equals(Object p_equals_0_, Object p_equals_1_) {
        return p_equals_0_ == p_equals_1_ ? true : (p_equals_0_ != null && p_equals_0_.equals(p_equals_1_) ? true : p_equals_1_ != null && p_equals_1_.equals(p_equals_0_));
    }

    public static boolean startsWith(String p_startsWith_0_, String[] p_startsWith_1_) {
        if (p_startsWith_0_ == null) {
            return false;
        }
        if (p_startsWith_1_ == null) {
            return false;
        }
        for (int i2 = 0; i2 < p_startsWith_1_.length; ++i2) {
            String s2 = p_startsWith_1_[i2];
            if (!p_startsWith_0_.startsWith(s2)) continue;
            return true;
        }
        return false;
    }

    public static boolean endsWith(String p_endsWith_0_, String[] p_endsWith_1_) {
        if (p_endsWith_0_ == null) {
            return false;
        }
        if (p_endsWith_1_ == null) {
            return false;
        }
        for (int i2 = 0; i2 < p_endsWith_1_.length; ++i2) {
            String s2 = p_endsWith_1_[i2];
            if (!p_endsWith_0_.endsWith(s2)) continue;
            return true;
        }
        return false;
    }

    public static String removePrefix(String p_removePrefix_0_, String p_removePrefix_1_) {
        if (p_removePrefix_0_ != null && p_removePrefix_1_ != null) {
            if (p_removePrefix_0_.startsWith(p_removePrefix_1_)) {
                p_removePrefix_0_ = p_removePrefix_0_.substring(p_removePrefix_1_.length());
            }
            return p_removePrefix_0_;
        }
        return p_removePrefix_0_;
    }

    public static String removeSuffix(String p_removeSuffix_0_, String p_removeSuffix_1_) {
        if (p_removeSuffix_0_ != null && p_removeSuffix_1_ != null) {
            if (p_removeSuffix_0_.endsWith(p_removeSuffix_1_)) {
                p_removeSuffix_0_ = p_removeSuffix_0_.substring(0, p_removeSuffix_0_.length() - p_removeSuffix_1_.length());
            }
            return p_removeSuffix_0_;
        }
        return p_removeSuffix_0_;
    }

    public static String replaceSuffix(String p_replaceSuffix_0_, String p_replaceSuffix_1_, String p_replaceSuffix_2_) {
        if (p_replaceSuffix_0_ != null && p_replaceSuffix_1_ != null) {
            if (p_replaceSuffix_2_ == null) {
                p_replaceSuffix_2_ = "";
            }
            if (p_replaceSuffix_0_.endsWith(p_replaceSuffix_1_)) {
                p_replaceSuffix_0_ = p_replaceSuffix_0_.substring(0, p_replaceSuffix_0_.length() - p_replaceSuffix_1_.length());
            }
            return p_replaceSuffix_0_ + p_replaceSuffix_2_;
        }
        return p_replaceSuffix_0_;
    }

    public static int findPrefix(String[] p_findPrefix_0_, String p_findPrefix_1_) {
        if (p_findPrefix_0_ != null && p_findPrefix_1_ != null) {
            for (int i2 = 0; i2 < p_findPrefix_0_.length; ++i2) {
                String s2 = p_findPrefix_0_[i2];
                if (!s2.startsWith(p_findPrefix_1_)) continue;
                return i2;
            }
            return -1;
        }
        return -1;
    }

    public static int findSuffix(String[] p_findSuffix_0_, String p_findSuffix_1_) {
        if (p_findSuffix_0_ != null && p_findSuffix_1_ != null) {
            for (int i2 = 0; i2 < p_findSuffix_0_.length; ++i2) {
                String s2 = p_findSuffix_0_[i2];
                if (!s2.endsWith(p_findSuffix_1_)) continue;
                return i2;
            }
            return -1;
        }
        return -1;
    }

    public static String[] remove(String[] p_remove_0_, int p_remove_1_, int p_remove_2_) {
        if (p_remove_0_ == null) {
            return p_remove_0_;
        }
        if (p_remove_2_ > 0 && p_remove_1_ < p_remove_0_.length) {
            if (p_remove_1_ >= p_remove_2_) {
                return p_remove_0_;
            }
            ArrayList<String> list = new ArrayList<String>(p_remove_0_.length);
            for (int i2 = 0; i2 < p_remove_0_.length; ++i2) {
                String s2 = p_remove_0_[i2];
                if (i2 >= p_remove_1_ && i2 < p_remove_2_) continue;
                list.add(s2);
            }
            String[] astring = list.toArray(new String[list.size()]);
            return astring;
        }
        return p_remove_0_;
    }

    public static String removeSuffix(String p_removeSuffix_0_, String[] p_removeSuffix_1_) {
        if (p_removeSuffix_0_ != null && p_removeSuffix_1_ != null) {
            String s2;
            int i2 = p_removeSuffix_0_.length();
            for (int j2 = 0; j2 < p_removeSuffix_1_.length && (p_removeSuffix_0_ = StrUtils.removeSuffix(p_removeSuffix_0_, s2 = p_removeSuffix_1_[j2])).length() == i2; ++j2) {
            }
            return p_removeSuffix_0_;
        }
        return p_removeSuffix_0_;
    }

    public static String removePrefix(String p_removePrefix_0_, String[] p_removePrefix_1_) {
        if (p_removePrefix_0_ != null && p_removePrefix_1_ != null) {
            String s2;
            int i2 = p_removePrefix_0_.length();
            for (int j2 = 0; j2 < p_removePrefix_1_.length && (p_removePrefix_0_ = StrUtils.removePrefix(p_removePrefix_0_, s2 = p_removePrefix_1_[j2])).length() == i2; ++j2) {
            }
            return p_removePrefix_0_;
        }
        return p_removePrefix_0_;
    }

    public static String removePrefixSuffix(String p_removePrefixSuffix_0_, String[] p_removePrefixSuffix_1_, String[] p_removePrefixSuffix_2_) {
        p_removePrefixSuffix_0_ = StrUtils.removePrefix(p_removePrefixSuffix_0_, p_removePrefixSuffix_1_);
        p_removePrefixSuffix_0_ = StrUtils.removeSuffix(p_removePrefixSuffix_0_, p_removePrefixSuffix_2_);
        return p_removePrefixSuffix_0_;
    }

    public static String removePrefixSuffix(String p_removePrefixSuffix_0_, String p_removePrefixSuffix_1_, String p_removePrefixSuffix_2_) {
        return StrUtils.removePrefixSuffix(p_removePrefixSuffix_0_, new String[]{p_removePrefixSuffix_1_}, new String[]{p_removePrefixSuffix_2_});
    }

    public static String getSegment(String p_getSegment_0_, String p_getSegment_1_, String p_getSegment_2_) {
        if (p_getSegment_0_ != null && p_getSegment_1_ != null && p_getSegment_2_ != null) {
            int i2 = p_getSegment_0_.indexOf(p_getSegment_1_);
            if (i2 < 0) {
                return null;
            }
            int j2 = p_getSegment_0_.indexOf(p_getSegment_2_, i2);
            return j2 < 0 ? null : p_getSegment_0_.substring(i2, j2 + p_getSegment_2_.length());
        }
        return null;
    }
}

