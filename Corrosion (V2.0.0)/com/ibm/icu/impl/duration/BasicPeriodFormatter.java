/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.BasicPeriodFormatterFactory;
import com.ibm.icu.impl.duration.Period;
import com.ibm.icu.impl.duration.PeriodFormatter;
import com.ibm.icu.impl.duration.TimeUnit;
import com.ibm.icu.impl.duration.impl.PeriodFormatterData;

class BasicPeriodFormatter
implements PeriodFormatter {
    private BasicPeriodFormatterFactory factory;
    private String localeName;
    private PeriodFormatterData data;
    private BasicPeriodFormatterFactory.Customizations customs;

    BasicPeriodFormatter(BasicPeriodFormatterFactory factory, String localeName, PeriodFormatterData data, BasicPeriodFormatterFactory.Customizations customs) {
        this.factory = factory;
        this.localeName = localeName;
        this.data = data;
        this.customs = customs;
    }

    public String format(Period period) {
        if (!period.isSet()) {
            throw new IllegalArgumentException("period is not set");
        }
        return this.format(period.timeLimit, period.inFuture, period.counts);
    }

    public PeriodFormatter withLocale(String locName) {
        if (!this.localeName.equals(locName)) {
            PeriodFormatterData newData = this.factory.getData(locName);
            return new BasicPeriodFormatter(this.factory, locName, newData, this.customs);
        }
        return this;
    }

    private String format(int tl, boolean inFuture, int[] counts) {
        int i2;
        int first;
        int i3;
        int mask = 0;
        for (i3 = 0; i3 < counts.length; ++i3) {
            if (counts[i3] <= 0) continue;
            mask |= 1 << i3;
        }
        if (!this.data.allowZero()) {
            i3 = 0;
            int m2 = 1;
            while (i3 < counts.length) {
                if ((mask & m2) != 0 && counts[i3] == 1) {
                    mask &= ~m2;
                }
                ++i3;
                m2 <<= 1;
            }
            if (mask == 0) {
                return null;
            }
        }
        boolean forceD3Seconds = false;
        if (this.data.useMilliseconds() != 0 && (mask & 1 << TimeUnit.MILLISECOND.ordinal) != 0) {
            byte sx2 = TimeUnit.SECOND.ordinal;
            byte mx2 = TimeUnit.MILLISECOND.ordinal;
            int sf2 = 1 << sx2;
            int mf2 = 1 << mx2;
            switch (this.data.useMilliseconds()) {
                case 2: {
                    if ((mask & sf2) == 0) break;
                    byte by = sx2;
                    counts[by] = counts[by] + (counts[mx2] - 1) / 1000;
                    mask &= ~mf2;
                    forceD3Seconds = true;
                    break;
                }
                case 1: {
                    if ((mask & sf2) == 0) {
                        mask |= sf2;
                        counts[sx2] = 1;
                    }
                    byte by = sx2;
                    counts[by] = counts[by] + (counts[mx2] - 1) / 1000;
                    mask &= ~mf2;
                    forceD3Seconds = true;
                }
            }
        }
        int last = counts.length - 1;
        for (first = 0; first < counts.length && (mask & 1 << first) == 0; ++first) {
        }
        while (last > first && (mask & 1 << last) == 0) {
            --last;
        }
        boolean isZero = true;
        for (int i4 = first; i4 <= last; ++i4) {
            if ((mask & 1 << i4) == 0 || counts[i4] <= 1) continue;
            isZero = false;
            break;
        }
        StringBuffer sb2 = new StringBuffer();
        if (!this.customs.displayLimit || isZero) {
            tl = 0;
        }
        int td = !this.customs.displayDirection || isZero ? 0 : (inFuture ? 2 : 1);
        boolean useDigitPrefix = this.data.appendPrefix(tl, td, sb2);
        boolean multiple = first != last;
        boolean wasSkipped = true;
        boolean skipped = false;
        boolean countSep = this.customs.separatorVariant != 0;
        int j2 = i2 = first;
        while (i2 <= last) {
            if (skipped) {
                this.data.appendSkippedUnit(sb2);
                skipped = false;
                wasSkipped = true;
            }
            while (++j2 < last && (mask & 1 << j2) == 0) {
                skipped = true;
            }
            TimeUnit unit = TimeUnit.units[i2];
            int count = counts[i2] - 1;
            int cv2 = this.customs.countVariant;
            if (i2 == last) {
                if (forceD3Seconds) {
                    cv2 = 5;
                }
            } else {
                cv2 = 0;
            }
            boolean isLast = i2 == last;
            boolean mustSkip = this.data.appendUnit(unit, count, cv2, this.customs.unitVariant, countSep, useDigitPrefix, multiple, isLast, wasSkipped, sb2);
            skipped |= mustSkip;
            wasSkipped = false;
            if (this.customs.separatorVariant != 0 && j2 <= last) {
                boolean afterFirst = i2 == first;
                boolean beforeLast = j2 == last;
                boolean fullSep = this.customs.separatorVariant == 2;
                useDigitPrefix = this.data.appendUnitSeparator(unit, fullSep, afterFirst, beforeLast, sb2);
            } else {
                useDigitPrefix = false;
            }
            i2 = j2;
        }
        this.data.appendSuffix(tl, td, sb2);
        return sb2.toString();
    }
}

