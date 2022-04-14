/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.compress.compressors.bzip2;

import java.util.BitSet;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;

class BlockSort {
    private static final int QSORT_STACK_SIZE = 1000;
    private static final int FALLBACK_QSORT_STACK_SIZE = 100;
    private static final int STACK_SIZE = 1000;
    private int workDone;
    private int workLimit;
    private boolean firstAttempt;
    private final int[] stack_ll = new int[1000];
    private final int[] stack_hh = new int[1000];
    private final int[] stack_dd = new int[1000];
    private final int[] mainSort_runningOrder = new int[256];
    private final int[] mainSort_copy = new int[256];
    private final boolean[] mainSort_bigDone = new boolean[256];
    private final int[] ftab = new int[65537];
    private final char[] quadrant;
    private static final int FALLBACK_QSORT_SMALL_THRESH = 10;
    private int[] eclass;
    private static final int[] INCS = new int[]{1, 4, 13, 40, 121, 364, 1093, 3280, 9841, 29524, 88573, 265720, 797161, 2391484};
    private static final int SMALL_THRESH = 20;
    private static final int DEPTH_THRESH = 10;
    private static final int WORK_FACTOR = 30;
    private static final int SETMASK = 0x200000;
    private static final int CLEARMASK = -2097153;

    BlockSort(BZip2CompressorOutputStream.Data data) {
        this.quadrant = data.sfmap;
    }

    void blockSort(BZip2CompressorOutputStream.Data data, int last) {
        this.workLimit = 30 * last;
        this.workDone = 0;
        this.firstAttempt = true;
        if (last + 1 < 10000) {
            this.fallbackSort(data, last);
        } else {
            this.mainSort(data, last);
            if (this.firstAttempt && this.workDone > this.workLimit) {
                this.fallbackSort(data, last);
            }
        }
        int[] fmap = data.fmap;
        data.origPtr = -1;
        for (int i2 = 0; i2 <= last; ++i2) {
            if (fmap[i2] != 0) continue;
            data.origPtr = i2;
            break;
        }
    }

    final void fallbackSort(BZip2CompressorOutputStream.Data data, int last) {
        data.block[0] = data.block[last + 1];
        this.fallbackSort(data.fmap, data.block, last + 1);
        int i2 = 0;
        while (i2 < last + 1) {
            int n2 = i2++;
            data.fmap[n2] = data.fmap[n2] - 1;
        }
        for (i2 = 0; i2 < last + 1; ++i2) {
            if (data.fmap[i2] != -1) continue;
            data.fmap[i2] = last;
            break;
        }
    }

    private void fallbackSimpleSort(int[] fmap, int[] eclass, int lo2, int hi2) {
        int j2;
        int ec_tmp;
        int tmp;
        int i2;
        if (lo2 == hi2) {
            return;
        }
        if (hi2 - lo2 > 3) {
            for (i2 = hi2 - 4; i2 >= lo2; --i2) {
                tmp = fmap[i2];
                ec_tmp = eclass[tmp];
                for (j2 = i2 + 4; j2 <= hi2 && ec_tmp > eclass[fmap[j2]]; j2 += 4) {
                    fmap[j2 - 4] = fmap[j2];
                }
                fmap[j2 - 4] = tmp;
            }
        }
        for (i2 = hi2 - 1; i2 >= lo2; --i2) {
            tmp = fmap[i2];
            ec_tmp = eclass[tmp];
            for (j2 = i2 + 1; j2 <= hi2 && ec_tmp > eclass[fmap[j2]]; ++j2) {
                fmap[j2 - 1] = fmap[j2];
            }
            fmap[j2 - 1] = tmp;
        }
    }

    private void fswap(int[] fmap, int zz1, int zz2) {
        int zztmp = fmap[zz1];
        fmap[zz1] = fmap[zz2];
        fmap[zz2] = zztmp;
    }

    private void fvswap(int[] fmap, int yyp1, int yyp2, int yyn) {
        while (yyn > 0) {
            this.fswap(fmap, yyp1, yyp2);
            ++yyp1;
            ++yyp2;
            --yyn;
        }
    }

    private int fmin(int a2, int b2) {
        return a2 < b2 ? a2 : b2;
    }

    private void fpush(int sp2, int lz2, int hz2) {
        this.stack_ll[sp2] = lz2;
        this.stack_hh[sp2] = hz2;
    }

    private int[] fpop(int sp2) {
        return new int[]{this.stack_ll[sp2], this.stack_hh[sp2]};
    }

    private void fallbackQSort3(int[] fmap, int[] eclass, int loSt, int hiSt) {
        long r2 = 0L;
        int sp2 = 0;
        this.fpush(sp2++, loSt, hiSt);
        while (sp2 > 0) {
            int n2;
            int gtHi;
            int ltLo;
            int lo2;
            int[] s2;
            int hi2;
            if ((hi2 = (s2 = this.fpop(--sp2))[1]) - (lo2 = s2[0]) < 10) {
                this.fallbackSimpleSort(fmap, eclass, lo2, hi2);
                continue;
            }
            long r3 = (r2 = (r2 * 7621L + 1L) % 32768L) % 3L;
            long med = r3 == 0L ? (long)eclass[fmap[lo2]] : (r3 == 1L ? (long)eclass[fmap[lo2 + hi2 >>> 1]] : (long)eclass[fmap[hi2]]);
            int unLo = ltLo = lo2;
            int unHi = gtHi = hi2;
            while (true) {
                if (unLo <= unHi) {
                    n2 = eclass[fmap[unLo]] - (int)med;
                    if (n2 == 0) {
                        this.fswap(fmap, unLo, ltLo);
                        ++ltLo;
                        ++unLo;
                        continue;
                    }
                    if (n2 <= 0) {
                        ++unLo;
                        continue;
                    }
                }
                while (unLo <= unHi) {
                    n2 = eclass[fmap[unHi]] - (int)med;
                    if (n2 == 0) {
                        this.fswap(fmap, unHi, gtHi);
                        --gtHi;
                        --unHi;
                        continue;
                    }
                    if (n2 < 0) break;
                    --unHi;
                }
                if (unLo > unHi) break;
                this.fswap(fmap, unLo, unHi);
                ++unLo;
                --unHi;
            }
            if (gtHi < ltLo) continue;
            n2 = this.fmin(ltLo - lo2, unLo - ltLo);
            this.fvswap(fmap, lo2, unLo - n2, n2);
            int m2 = this.fmin(hi2 - gtHi, gtHi - unHi);
            this.fvswap(fmap, unHi + 1, hi2 - m2 + 1, m2);
            n2 = lo2 + unLo - ltLo - 1;
            m2 = hi2 - (gtHi - unHi) + 1;
            if (n2 - lo2 > hi2 - m2) {
                this.fpush(sp2++, lo2, n2);
                this.fpush(sp2++, m2, hi2);
                continue;
            }
            this.fpush(sp2++, m2, hi2);
            this.fpush(sp2++, lo2, n2);
        }
    }

    private int[] getEclass() {
        return this.eclass == null ? (this.eclass = new int[this.quadrant.length / 2]) : this.eclass;
    }

    /*
     * Unable to fully structure code
     */
    final void fallbackSort(int[] fmap, byte[] block, int nblock) {
        ftab = new int[257];
        eclass = this.getEclass();
        for (i = 0; i < nblock; ++i) {
            eclass[i] = 0;
        }
        for (i = 0; i < nblock; ++i) {
            v0 = block[i] & 255;
            ftab[v0] = ftab[v0] + 1;
        }
        for (i = 1; i < 257; ++i) {
            v1 = i;
            ftab[v1] = ftab[v1] + ftab[i - 1];
        }
        i = 0;
        while (i < nblock) {
            j = block[i] & 255;
            ftab[j] = k = ftab[j] - 1;
            fmap[k] = i++;
        }
        nBhtab = 64 + nblock;
        bhtab = new BitSet(nBhtab);
        for (i = 0; i < 256; ++i) {
            bhtab.set(ftab[i]);
        }
        for (i = 0; i < 32; ++i) {
            bhtab.set(nblock + 2 * i);
            bhtab.clear(nblock + 2 * i + 1);
        }
        H = 1;
        block6: do {
            j = 0;
            for (i = 0; i < nblock; ++i) {
                if (bhtab.get(i)) {
                    j = i;
                }
                if ((k = fmap[i] - H) < 0) {
                    k += nblock;
                }
                eclass[k] = j;
            }
            nNotDone = 0;
            r = -1;
            block8: while (true) {
                k = r + 1;
                l = (k = bhtab.nextClearBit(k)) - 1;
                if (l >= nblock || (r = (k = bhtab.nextSetBit(k + 1)) - 1) >= nblock) continue block6;
                if (r <= l) continue;
                nNotDone += r - l + 1;
                this.fallbackQSort3(fmap, eclass, l, r);
                cc = -1;
                i = l;
                while (true) {
                    if (i <= r) ** break;
                    continue block8;
                    cc1 = eclass[fmap[i]];
                    if (cc != cc1) {
                        bhtab.set(i);
                        cc = cc1;
                    }
                    ++i;
                }
                break;
            }
        } while ((H *= 2) <= nblock && nNotDone != 0);
    }

    private boolean mainSimpleSort(BZip2CompressorOutputStream.Data dataShadow, int lo2, int hi2, int d2, int lastShadow) {
        int bigN = hi2 - lo2 + 1;
        if (bigN < 2) {
            return this.firstAttempt && this.workDone > this.workLimit;
        }
        int hp2 = 0;
        while (INCS[hp2] < bigN) {
            ++hp2;
        }
        int[] fmap = dataShadow.fmap;
        char[] quadrant = this.quadrant;
        byte[] block = dataShadow.block;
        int lastPlus1 = lastShadow + 1;
        boolean firstAttemptShadow = this.firstAttempt;
        int workLimitShadow = this.workLimit;
        int workDoneShadow = this.workDone;
        block1: while (--hp2 >= 0) {
            int h2 = INCS[hp2];
            int mj2 = lo2 + h2 - 1;
            int i2 = lo2 + h2;
            while (i2 <= hi2) {
                int k2 = 3;
                while (i2 <= hi2 && --k2 >= 0) {
                    int v2 = fmap[i2];
                    int vd2 = v2 + d2;
                    int j2 = i2;
                    boolean onceRunned = false;
                    int a2 = 0;
                    block4: while (true) {
                        int i22;
                        int i1;
                        if (onceRunned) {
                            fmap[j2] = a2;
                            if ((j2 -= h2) <= mj2) {
                                break;
                            }
                        } else {
                            onceRunned = true;
                        }
                        if (block[(i1 = (a2 = fmap[j2 - h2]) + d2) + 1] == block[(i22 = vd2) + 1]) {
                            if (block[i1 + 2] == block[i22 + 2]) {
                                if (block[i1 + 3] == block[i22 + 3]) {
                                    if (block[i1 + 4] == block[i22 + 4]) {
                                        if (block[i1 + 5] == block[i22 + 5]) {
                                            if (block[i1 += 6] == block[i22 += 6]) {
                                                int x2 = lastShadow;
                                                while (x2 > 0) {
                                                    x2 -= 4;
                                                    if (block[i1 + 1] == block[i22 + 1]) {
                                                        if (quadrant[i1] == quadrant[i22]) {
                                                            if (block[i1 + 2] == block[i22 + 2]) {
                                                                if (quadrant[i1 + 1] == quadrant[i22 + 1]) {
                                                                    if (block[i1 + 3] == block[i22 + 3]) {
                                                                        if (quadrant[i1 + 2] == quadrant[i22 + 2]) {
                                                                            if (block[i1 + 4] == block[i22 + 4]) {
                                                                                if (quadrant[i1 + 3] == quadrant[i22 + 3]) {
                                                                                    if ((i1 += 4) >= lastPlus1) {
                                                                                        i1 -= lastPlus1;
                                                                                    }
                                                                                    if ((i22 += 4) >= lastPlus1) {
                                                                                        i22 -= lastPlus1;
                                                                                    }
                                                                                    ++workDoneShadow;
                                                                                    continue;
                                                                                }
                                                                                if (quadrant[i1 + 3] <= quadrant[i22 + 3]) break block4;
                                                                                continue block4;
                                                                            }
                                                                            if ((block[i1 + 4] & 0xFF) <= (block[i22 + 4] & 0xFF)) break block4;
                                                                            continue block4;
                                                                        }
                                                                        if (quadrant[i1 + 2] <= quadrant[i22 + 2]) break block4;
                                                                        continue block4;
                                                                    }
                                                                    if ((block[i1 + 3] & 0xFF) <= (block[i22 + 3] & 0xFF)) break block4;
                                                                    continue block4;
                                                                }
                                                                if (quadrant[i1 + 1] <= quadrant[i22 + 1]) break block4;
                                                                continue block4;
                                                            }
                                                            if ((block[i1 + 2] & 0xFF) <= (block[i22 + 2] & 0xFF)) break block4;
                                                            continue block4;
                                                        }
                                                        if (quadrant[i1] <= quadrant[i22]) break block4;
                                                        continue block4;
                                                    }
                                                    if ((block[i1 + 1] & 0xFF) <= (block[i22 + 1] & 0xFF)) break block4;
                                                    continue block4;
                                                }
                                                break;
                                            }
                                            if ((block[i1] & 0xFF) <= (block[i22] & 0xFF)) break;
                                            continue;
                                        }
                                        if ((block[i1 + 5] & 0xFF) <= (block[i22 + 5] & 0xFF)) break;
                                        continue;
                                    }
                                    if ((block[i1 + 4] & 0xFF) <= (block[i22 + 4] & 0xFF)) break;
                                    continue;
                                }
                                if ((block[i1 + 3] & 0xFF) <= (block[i22 + 3] & 0xFF)) break;
                                continue;
                            }
                            if ((block[i1 + 2] & 0xFF) <= (block[i22 + 2] & 0xFF)) break;
                            continue;
                        }
                        if ((block[i1 + 1] & 0xFF) <= (block[i22 + 1] & 0xFF)) break;
                    }
                    fmap[j2] = v2;
                    ++i2;
                }
                if (!firstAttemptShadow || i2 > hi2 || workDoneShadow <= workLimitShadow) continue;
                break block1;
            }
        }
        this.workDone = workDoneShadow;
        return firstAttemptShadow && workDoneShadow > workLimitShadow;
    }

    private static void vswap(int[] fmap, int p1, int p2, int n2) {
        n2 += p1;
        while (p1 < n2) {
            int t2 = fmap[p1];
            fmap[p1++] = fmap[p2];
            fmap[p2++] = t2;
        }
    }

    private static byte med3(byte a2, byte b2, byte c2) {
        return a2 < b2 ? (b2 < c2 ? b2 : (a2 < c2 ? c2 : a2)) : (b2 > c2 ? b2 : (a2 > c2 ? c2 : a2));
    }

    private void mainQSort3(BZip2CompressorOutputStream.Data dataShadow, int loSt, int hiSt, int dSt, int last) {
        int[] stack_ll = this.stack_ll;
        int[] stack_hh = this.stack_hh;
        int[] stack_dd = this.stack_dd;
        int[] fmap = dataShadow.fmap;
        byte[] block = dataShadow.block;
        stack_ll[0] = loSt;
        stack_hh[0] = hiSt;
        stack_dd[0] = dSt;
        int sp2 = 1;
        while (--sp2 >= 0) {
            int n2;
            int lo2 = stack_ll[sp2];
            int hi2 = stack_hh[sp2];
            int d2 = stack_dd[sp2];
            if (hi2 - lo2 < 20 || d2 > 10) {
                if (!this.mainSimpleSort(dataShadow, lo2, hi2, d2, last)) continue;
                return;
            }
            int d1 = d2 + 1;
            int med = BlockSort.med3(block[fmap[lo2] + d1], block[fmap[hi2] + d1], block[fmap[lo2 + hi2 >>> 1] + d1]) & 0xFF;
            int unLo = lo2;
            int unHi = hi2;
            int ltLo = lo2;
            int gtHi = hi2;
            while (true) {
                int temp;
                if (unLo <= unHi) {
                    n2 = (block[fmap[unLo] + d1] & 0xFF) - med;
                    if (n2 == 0) {
                        temp = fmap[unLo];
                        fmap[unLo++] = fmap[ltLo];
                        fmap[ltLo++] = temp;
                        continue;
                    }
                    if (n2 < 0) {
                        ++unLo;
                        continue;
                    }
                }
                while (unLo <= unHi) {
                    n2 = (block[fmap[unHi] + d1] & 0xFF) - med;
                    if (n2 == 0) {
                        temp = fmap[unHi];
                        fmap[unHi--] = fmap[gtHi];
                        fmap[gtHi--] = temp;
                        continue;
                    }
                    if (n2 <= 0) break;
                    --unHi;
                }
                if (unLo > unHi) break;
                int temp2 = fmap[unLo];
                fmap[unLo++] = fmap[unHi];
                fmap[unHi--] = temp2;
            }
            if (gtHi < ltLo) {
                stack_ll[sp2] = lo2;
                stack_hh[sp2] = hi2;
                stack_dd[sp2] = d1;
                ++sp2;
                continue;
            }
            n2 = ltLo - lo2 < unLo - ltLo ? ltLo - lo2 : unLo - ltLo;
            BlockSort.vswap(fmap, lo2, unLo - n2, n2);
            int m2 = hi2 - gtHi < gtHi - unHi ? hi2 - gtHi : gtHi - unHi;
            BlockSort.vswap(fmap, unLo, hi2 - m2 + 1, m2);
            n2 = lo2 + unLo - ltLo - 1;
            m2 = hi2 - (gtHi - unHi) + 1;
            stack_ll[sp2] = lo2;
            stack_hh[sp2] = n2;
            stack_dd[sp2] = d2;
            stack_ll[++sp2] = n2 + 1;
            stack_hh[sp2] = m2 - 1;
            stack_dd[sp2] = d1;
            stack_ll[++sp2] = m2;
            stack_hh[sp2] = hi2;
            stack_dd[sp2] = d2;
            ++sp2;
        }
    }

    final void mainSort(BZip2CompressorOutputStream.Data dataShadow, int lastShadow) {
        int j2;
        int c2;
        int i2;
        int[] runningOrder = this.mainSort_runningOrder;
        int[] copy = this.mainSort_copy;
        boolean[] bigDone = this.mainSort_bigDone;
        int[] ftab = this.ftab;
        byte[] block = dataShadow.block;
        int[] fmap = dataShadow.fmap;
        char[] quadrant = this.quadrant;
        int workLimitShadow = this.workLimit;
        boolean firstAttemptShadow = this.firstAttempt;
        int i3 = 65537;
        while (--i3 >= 0) {
            ftab[i3] = 0;
        }
        for (i3 = 0; i3 < 20; ++i3) {
            block[lastShadow + i3 + 2] = block[i3 % (lastShadow + 1) + 1];
        }
        i3 = lastShadow + 20 + 1;
        while (--i3 >= 0) {
            quadrant[i3] = '\u0000';
        }
        block[0] = block[lastShadow + 1];
        int c1 = block[0] & 0xFF;
        for (i2 = 0; i2 <= lastShadow; ++i2) {
            c2 = block[i2 + 1] & 0xFF;
            int n2 = (c1 << 8) + c2;
            ftab[n2] = ftab[n2] + 1;
            c1 = c2;
        }
        for (i2 = 1; i2 <= 65536; ++i2) {
            int n3 = i2;
            ftab[n3] = ftab[n3] + ftab[i2 - 1];
        }
        c1 = block[1] & 0xFF;
        i2 = 0;
        while (i2 < lastShadow) {
            c2 = block[i2 + 2] & 0xFF;
            int n4 = (c1 << 8) + c2;
            int n5 = ftab[n4] - 1;
            ftab[n4] = n5;
            fmap[n5] = i2++;
            c1 = c2;
        }
        int n6 = ((block[lastShadow + 1] & 0xFF) << 8) + (block[1] & 0xFF);
        int n7 = ftab[n6] - 1;
        ftab[n6] = n7;
        fmap[n7] = lastShadow;
        i2 = 256;
        while (--i2 >= 0) {
            bigDone[i2] = false;
            runningOrder[i2] = i2;
        }
        int h2 = 364;
        while (h2 != 1) {
            for (int i4 = h2 /= 3; i4 <= 255; ++i4) {
                int vv2 = runningOrder[i4];
                int a2 = ftab[vv2 + 1 << 8] - ftab[vv2 << 8];
                int b2 = h2 - 1;
                j2 = i4;
                int ro2 = runningOrder[j2 - h2];
                while (ftab[ro2 + 1 << 8] - ftab[ro2 << 8] > a2) {
                    runningOrder[j2] = ro2;
                    if ((j2 -= h2) <= b2) break;
                    ro2 = runningOrder[j2 - h2];
                }
                runningOrder[j2] = vv2;
            }
        }
        for (i2 = 0; i2 <= 255; ++i2) {
            int j3;
            int ss2 = runningOrder[i2];
            for (j3 = 0; j3 <= 255; ++j3) {
                int sb2 = (ss2 << 8) + j3;
                int ftab_sb = ftab[sb2];
                if ((ftab_sb & 0x200000) == 0x200000) continue;
                int hi2 = (ftab[sb2 + 1] & 0xFFDFFFFF) - 1;
                int lo2 = ftab_sb & 0xFFDFFFFF;
                if (hi2 > lo2) {
                    this.mainQSort3(dataShadow, lo2, hi2, 2, lastShadow);
                    if (firstAttemptShadow && this.workDone > workLimitShadow) {
                        return;
                    }
                }
                ftab[sb2] = ftab_sb | 0x200000;
            }
            for (j3 = 0; j3 <= 255; ++j3) {
                copy[j3] = ftab[(j3 << 8) + ss2] & 0xFFDFFFFF;
            }
            int hj2 = ftab[ss2 + 1 << 8] & 0xFFDFFFFF;
            for (j3 = ftab[ss2 << 8] & 0xFFDFFFFF; j3 < hj2; ++j3) {
                int fmap_j = fmap[j3];
                c1 = block[fmap_j] & 0xFF;
                if (bigDone[c1]) continue;
                fmap[copy[c1]] = fmap_j == 0 ? lastShadow : fmap_j - 1;
                int n8 = c1;
                copy[n8] = copy[n8] + 1;
            }
            j3 = 256;
            while (--j3 >= 0) {
                int n9 = (j3 << 8) + ss2;
                ftab[n9] = ftab[n9] | 0x200000;
            }
            bigDone[ss2] = true;
            if (i2 >= 255) continue;
            int bbStart = ftab[ss2 << 8] & 0xFFDFFFFF;
            int bbSize = (ftab[ss2 + 1 << 8] & 0xFFDFFFFF) - bbStart;
            int shifts = 0;
            while (bbSize >> shifts > 65534) {
                ++shifts;
            }
            for (j2 = 0; j2 < bbSize; ++j2) {
                char qVal;
                int a2update = fmap[bbStart + j2];
                quadrant[a2update] = qVal = (char)(j2 >> shifts);
                if (a2update >= 20) continue;
                quadrant[a2update + lastShadow + 1] = qVal;
            }
        }
    }
}

