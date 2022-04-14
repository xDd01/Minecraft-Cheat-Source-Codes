/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.duration.impl;

import com.ibm.icu.impl.duration.TimeUnit;
import com.ibm.icu.impl.duration.impl.DataRecord;
import com.ibm.icu.impl.duration.impl.Utils;
import java.util.Arrays;

public class PeriodFormatterData {
    final DataRecord dr;
    String localeName;
    public static boolean trace = false;
    private static final int FORM_PLURAL = 0;
    private static final int FORM_SINGULAR = 1;
    private static final int FORM_DUAL = 2;
    private static final int FORM_PAUCAL = 3;
    private static final int FORM_SINGULAR_SPELLED = 4;
    private static final int FORM_SINGULAR_NO_OMIT = 5;
    private static final int FORM_HALF_SPELLED = 6;

    public PeriodFormatterData(String localeName, DataRecord dr2) {
        this.dr = dr2;
        this.localeName = localeName;
        if (localeName == null) {
            throw new NullPointerException("localename is null");
        }
        if (dr2 == null) {
            throw new NullPointerException("data record is null");
        }
    }

    public int pluralization() {
        return this.dr.pl;
    }

    public boolean allowZero() {
        return this.dr.allowZero;
    }

    public boolean weeksAloneOnly() {
        return this.dr.weeksAloneOnly;
    }

    public int useMilliseconds() {
        return this.dr.useMilliseconds;
    }

    public boolean appendPrefix(int tl, int td, StringBuffer sb2) {
        String prefix;
        int ix2;
        DataRecord.ScopeData sd2;
        if (this.dr.scopeData != null && (sd2 = this.dr.scopeData[ix2 = tl * 3 + td]) != null && (prefix = sd2.prefix) != null) {
            sb2.append(prefix);
            return sd2.requiresDigitPrefix;
        }
        return false;
    }

    public void appendSuffix(int tl, int td, StringBuffer sb2) {
        String suffix;
        int ix2;
        DataRecord.ScopeData sd2;
        if (this.dr.scopeData != null && (sd2 = this.dr.scopeData[ix2 = tl * 3 + td]) != null && (suffix = sd2.suffix) != null) {
            if (trace) {
                System.out.println("appendSuffix '" + suffix + "'");
            }
            sb2.append(suffix);
        }
    }

    public boolean appendUnit(TimeUnit unit, int count, int cv2, int uv2, boolean useCountSep, boolean useDigitPrefix, boolean multiple, boolean last, boolean wasSkipped, StringBuffer sb2) {
        int px2 = unit.ordinal();
        boolean willRequireSkipMarker = false;
        if (this.dr.requiresSkipMarker != null && this.dr.requiresSkipMarker[px2] && this.dr.skippedUnitMarker != null) {
            if (!wasSkipped && last) {
                sb2.append(this.dr.skippedUnitMarker);
            }
            willRequireSkipMarker = true;
        }
        if (uv2 != 0) {
            String[] names;
            boolean useMedium = uv2 == 1;
            String[] stringArray = names = useMedium ? this.dr.mediumNames : this.dr.shortNames;
            if (names == null || names[px2] == null) {
                String[] stringArray2 = names = useMedium ? this.dr.shortNames : this.dr.mediumNames;
            }
            if (names != null && names[px2] != null) {
                this.appendCount(unit, false, false, count, cv2, useCountSep, names[px2], last, sb2);
                return false;
            }
        }
        if (cv2 == 2 && this.dr.halfSupport != null) {
            switch (this.dr.halfSupport[px2]) {
                case 0: {
                    break;
                }
                case 2: {
                    if (count > 1000) break;
                }
                case 1: {
                    count = count / 500 * 500;
                    cv2 = 3;
                }
            }
        }
        String name = null;
        int form = this.computeForm(unit, count, cv2, multiple && last);
        if (form == 4) {
            if (this.dr.singularNames == null) {
                form = 1;
                name = this.dr.pluralNames[px2][form];
            } else {
                name = this.dr.singularNames[px2];
            }
        } else if (form == 5) {
            name = this.dr.pluralNames[px2][1];
        } else if (form == 6) {
            name = this.dr.halfNames[px2];
        } else {
            try {
                name = this.dr.pluralNames[px2][form];
            }
            catch (NullPointerException e2) {
                System.out.println("Null Pointer in PeriodFormatterData[" + this.localeName + "].au px: " + px2 + " form: " + form + " pn: " + Arrays.toString((Object[])this.dr.pluralNames));
                throw e2;
            }
        }
        if (name == null) {
            form = 0;
            name = this.dr.pluralNames[px2][form];
        }
        boolean omitCount = form == 4 || form == 6 || this.dr.omitSingularCount && form == 1 || this.dr.omitDualCount && form == 2;
        int suffixIndex = this.appendCount(unit, omitCount, useDigitPrefix, count, cv2, useCountSep, name, last, sb2);
        if (last && suffixIndex >= 0) {
            String suffix = null;
            if (this.dr.rqdSuffixes != null && suffixIndex < this.dr.rqdSuffixes.length) {
                suffix = this.dr.rqdSuffixes[suffixIndex];
            }
            if (suffix == null && this.dr.optSuffixes != null && suffixIndex < this.dr.optSuffixes.length) {
                suffix = this.dr.optSuffixes[suffixIndex];
            }
            if (suffix != null) {
                sb2.append(suffix);
            }
        }
        return willRequireSkipMarker;
    }

    public int appendCount(TimeUnit unit, boolean omitCount, boolean useDigitPrefix, int count, int cv2, boolean useSep, String name, boolean last, StringBuffer sb2) {
        String measure;
        if (cv2 == 2 && this.dr.halves == null) {
            cv2 = 0;
        }
        if (!omitCount && useDigitPrefix && this.dr.digitPrefix != null) {
            sb2.append(this.dr.digitPrefix);
        }
        int index = unit.ordinal();
        block0 : switch (cv2) {
            case 0: {
                if (omitCount) break;
                this.appendInteger(count / 1000, 1, 10, sb2);
                break;
            }
            case 1: {
                int val = count / 1000;
                if (unit == TimeUnit.MINUTE && (this.dr.fiveMinutes != null || this.dr.fifteenMinutes != null) && val != 0 && val % 5 == 0) {
                    if (this.dr.fifteenMinutes != null && (val == 15 || val == 45)) {
                        int n2 = val = val == 15 ? 1 : 3;
                        if (!omitCount) {
                            this.appendInteger(val, 1, 10, sb2);
                        }
                        name = this.dr.fifteenMinutes;
                        index = 8;
                        break;
                    }
                    if (this.dr.fiveMinutes != null) {
                        val /= 5;
                        if (!omitCount) {
                            this.appendInteger(val, 1, 10, sb2);
                        }
                        name = this.dr.fiveMinutes;
                        index = 9;
                        break;
                    }
                }
                if (omitCount) break;
                this.appendInteger(val, 1, 10, sb2);
                break;
            }
            case 2: {
                int solox;
                int v2 = count / 500;
                if (v2 != 1 && !omitCount) {
                    this.appendCountValue(count, 1, 0, sb2);
                }
                if ((v2 & 1) != 1) break;
                if (v2 == 1 && this.dr.halfNames != null && this.dr.halfNames[index] != null) {
                    sb2.append(name);
                    return last ? index : -1;
                }
                int n3 = solox = v2 == 1 ? 0 : 1;
                if (this.dr.genders != null && this.dr.halves.length > 2 && this.dr.genders[index] == 1) {
                    solox += 2;
                }
                byte hp2 = this.dr.halfPlacements == null ? (byte)0 : this.dr.halfPlacements[solox & 1];
                String half = this.dr.halves[solox];
                String measure2 = this.dr.measures == null ? null : this.dr.measures[index];
                switch (hp2) {
                    case 0: {
                        sb2.append(half);
                        break block0;
                    }
                    case 1: {
                        if (measure2 != null) {
                            sb2.append(measure2);
                            sb2.append(half);
                            if (useSep && !omitCount) {
                                sb2.append(this.dr.countSep);
                            }
                        } else {
                            sb2.append(name);
                            sb2.append(half);
                            return last ? index : -1;
                        }
                        sb2.append(name);
                        return -1;
                    }
                    case 2: {
                        if (measure2 != null) {
                            sb2.append(measure2);
                        }
                        if (useSep && !omitCount) {
                            sb2.append(this.dr.countSep);
                        }
                        sb2.append(name);
                        sb2.append(half);
                        return last ? index : -1;
                    }
                }
                break;
            }
            default: {
                int decimals = 1;
                switch (cv2) {
                    case 4: {
                        decimals = 2;
                        break;
                    }
                    case 5: {
                        decimals = 3;
                        break;
                    }
                }
                if (omitCount) break;
                this.appendCountValue(count, 1, decimals, sb2);
            }
        }
        if (!omitCount && useSep) {
            sb2.append(this.dr.countSep);
        }
        if (!omitCount && this.dr.measures != null && index < this.dr.measures.length && (measure = this.dr.measures[index]) != null) {
            sb2.append(measure);
        }
        sb2.append(name);
        return last ? index : -1;
    }

    public void appendCountValue(int count, int integralDigits, int decimalDigits, StringBuffer sb2) {
        int ival = count / 1000;
        if (decimalDigits == 0) {
            this.appendInteger(ival, integralDigits, 10, sb2);
            return;
        }
        if (this.dr.requiresDigitSeparator && sb2.length() > 0) {
            sb2.append(' ');
        }
        this.appendDigits(ival, integralDigits, 10, sb2);
        int dval = count % 1000;
        if (decimalDigits == 1) {
            dval /= 100;
        } else if (decimalDigits == 2) {
            dval /= 10;
        }
        sb2.append(this.dr.decimalSep);
        this.appendDigits(dval, decimalDigits, decimalDigits, sb2);
        if (this.dr.requiresDigitSeparator) {
            sb2.append(' ');
        }
    }

    public void appendInteger(int num, int mindigits, int maxdigits, StringBuffer sb2) {
        String name;
        if (this.dr.numberNames != null && num < this.dr.numberNames.length && (name = this.dr.numberNames[num]) != null) {
            sb2.append(name);
            return;
        }
        if (this.dr.requiresDigitSeparator && sb2.length() > 0) {
            sb2.append(' ');
        }
        switch (this.dr.numberSystem) {
            case 0: {
                this.appendDigits(num, mindigits, maxdigits, sb2);
                break;
            }
            case 1: {
                sb2.append(Utils.chineseNumber(num, Utils.ChineseDigits.TRADITIONAL));
                break;
            }
            case 2: {
                sb2.append(Utils.chineseNumber(num, Utils.ChineseDigits.SIMPLIFIED));
                break;
            }
            case 3: {
                sb2.append(Utils.chineseNumber(num, Utils.ChineseDigits.KOREAN));
            }
        }
        if (this.dr.requiresDigitSeparator) {
            sb2.append(' ');
        }
    }

    public void appendDigits(long num, int mindigits, int maxdigits, StringBuffer sb2) {
        char[] buf = new char[maxdigits];
        int ix2 = maxdigits;
        while (ix2 > 0 && num > 0L) {
            buf[--ix2] = (char)((long)this.dr.zero + num % 10L);
            num /= 10L;
        }
        int e2 = maxdigits - mindigits;
        while (ix2 > e2) {
            buf[--ix2] = this.dr.zero;
        }
        sb2.append(buf, ix2, maxdigits - ix2);
    }

    public void appendSkippedUnit(StringBuffer sb2) {
        if (this.dr.skippedUnitMarker != null) {
            sb2.append(this.dr.skippedUnitMarker);
        }
    }

    public boolean appendUnitSeparator(TimeUnit unit, boolean longSep, boolean afterFirst, boolean beforeLast, StringBuffer sb2) {
        if (longSep && this.dr.unitSep != null || this.dr.shortUnitSep != null) {
            if (longSep && this.dr.unitSep != null) {
                int ix2 = (afterFirst ? 2 : 0) + (beforeLast ? 1 : 0);
                sb2.append(this.dr.unitSep[ix2]);
                return this.dr.unitSepRequiresDP != null && this.dr.unitSepRequiresDP[ix2];
            }
            sb2.append(this.dr.shortUnitSep);
        }
        return false;
    }

    private int computeForm(TimeUnit unit, int count, int cv2, boolean lastOfMultiple) {
        if (trace) {
            System.err.println("pfd.cf unit: " + unit + " count: " + count + " cv: " + cv2 + " dr.pl: " + this.dr.pl);
            Thread.dumpStack();
        }
        if (this.dr.pl == 0) {
            return 0;
        }
        int val = count / 1000;
        block0 : switch (cv2) {
            case 0: 
            case 1: {
                break;
            }
            case 2: {
                switch (this.dr.fractionHandling) {
                    case 0: {
                        return 0;
                    }
                    case 1: 
                    case 2: {
                        int v2 = count / 500;
                        if (v2 == 1) {
                            if (this.dr.halfNames != null && this.dr.halfNames[unit.ordinal()] != null) {
                                return 6;
                            }
                            return 5;
                        }
                        if ((v2 & 1) != 1) break block0;
                        if (this.dr.pl == 5 && v2 > 21) {
                            return 5;
                        }
                        if (v2 != 3 || this.dr.pl != 1 || this.dr.fractionHandling == 2) break block0;
                        return 0;
                    }
                    case 3: {
                        int v2 = count / 500;
                        if (v2 != 1 && v2 != 3) break block0;
                        return 3;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
            }
            default: {
                switch (this.dr.decimalHandling) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        return 5;
                    }
                    case 2: {
                        if (count >= 1000) break;
                        return 5;
                    }
                    case 3: {
                        if (this.dr.pl != 3) break;
                        return 3;
                    }
                }
                return 0;
            }
        }
        if (trace && count == 0) {
            System.err.println("EZeroHandling = " + this.dr.zeroHandling);
        }
        if (count == 0 && this.dr.zeroHandling == 1) {
            return 4;
        }
        int form = 0;
        switch (this.dr.pl) {
            case 0: {
                break;
            }
            case 1: {
                if (val != 1) break;
                form = 4;
                break;
            }
            case 2: {
                if (val == 2) {
                    form = 2;
                    break;
                }
                if (val != 1) break;
                form = 1;
                break;
            }
            case 3: {
                int v3 = val;
                if ((v3 %= 100) > 20) {
                    v3 %= 10;
                }
                if (v3 == 1) {
                    form = 1;
                    break;
                }
                if (v3 <= 1 || v3 >= 5) break;
                form = 3;
                break;
            }
            case 4: {
                if (val == 2) {
                    form = 2;
                    break;
                }
                if (val == 1) {
                    if (lastOfMultiple) {
                        form = 4;
                        break;
                    }
                    form = 1;
                    break;
                }
                if (unit != TimeUnit.YEAR || val <= 11) break;
                form = 5;
                break;
            }
            case 5: {
                if (val == 2) {
                    form = 2;
                    break;
                }
                if (val == 1) {
                    form = 1;
                    break;
                }
                if (val <= 10) break;
                form = 5;
                break;
            }
            default: {
                System.err.println("dr.pl is " + this.dr.pl);
                throw new IllegalStateException();
            }
        }
        return form;
    }
}

