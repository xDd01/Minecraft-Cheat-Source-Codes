package org.apache.commons.compress.compressors.bzip2;

import org.apache.commons.compress.compressors.*;
import java.io.*;
import org.apache.commons.compress.utils.*;
import java.nio.*;
import java.util.*;

public class BZip2CompressorInputStream extends CompressorInputStream implements BZip2Constants, InputStreamStatistics
{
    private int last;
    private int origPtr;
    private int blockSize100k;
    private boolean blockRandomised;
    private final CRC crc;
    private int nInUse;
    private BitInputStream bin;
    private final boolean decompressConcatenated;
    private static final int EOF = 0;
    private static final int START_BLOCK_STATE = 1;
    private static final int RAND_PART_A_STATE = 2;
    private static final int RAND_PART_B_STATE = 3;
    private static final int RAND_PART_C_STATE = 4;
    private static final int NO_RAND_PART_A_STATE = 5;
    private static final int NO_RAND_PART_B_STATE = 6;
    private static final int NO_RAND_PART_C_STATE = 7;
    private int currentState;
    private int storedBlockCRC;
    private int storedCombinedCRC;
    private int computedBlockCRC;
    private int computedCombinedCRC;
    private int su_count;
    private int su_ch2;
    private int su_chPrev;
    private int su_i2;
    private int su_j2;
    private int su_rNToGo;
    private int su_rTPos;
    private int su_tPos;
    private char su_z;
    private Data data;
    
    public BZip2CompressorInputStream(final InputStream in) throws IOException {
        this(in, false);
    }
    
    public BZip2CompressorInputStream(final InputStream in, final boolean decompressConcatenated) throws IOException {
        this.crc = new CRC();
        this.currentState = 1;
        this.bin = new BitInputStream((in == System.in) ? new CloseShieldFilterInputStream(in) : in, ByteOrder.BIG_ENDIAN);
        this.decompressConcatenated = decompressConcatenated;
        this.init(true);
        this.initBlock();
    }
    
    @Override
    public int read() throws IOException {
        if (this.bin != null) {
            final int r = this.read0();
            this.count((r < 0) ? -1 : 1);
            return r;
        }
        throw new IOException("stream closed");
    }
    
    @Override
    public int read(final byte[] dest, final int offs, final int len) throws IOException {
        if (offs < 0) {
            throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
        }
        if (len < 0) {
            throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
        }
        if (offs + len > dest.length) {
            throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > dest.length(" + dest.length + ").");
        }
        if (this.bin == null) {
            throw new IOException("stream closed");
        }
        if (len == 0) {
            return 0;
        }
        final int hi = offs + len;
        int destOffs = offs;
        int b;
        while (destOffs < hi && (b = this.read0()) >= 0) {
            dest[destOffs++] = (byte)b;
            this.count(1);
        }
        return (destOffs == offs) ? -1 : (destOffs - offs);
    }
    
    @Override
    public long getCompressedCount() {
        return this.bin.getBytesRead();
    }
    
    private void makeMaps() {
        final boolean[] inUse = this.data.inUse;
        final byte[] seqToUnseq = this.data.seqToUnseq;
        int nInUseShadow = 0;
        for (int i = 0; i < 256; ++i) {
            if (inUse[i]) {
                seqToUnseq[nInUseShadow++] = (byte)i;
            }
        }
        this.nInUse = nInUseShadow;
    }
    
    private int read0() throws IOException {
        switch (this.currentState) {
            case 0: {
                return -1;
            }
            case 1: {
                return this.setupBlock();
            }
            case 2: {
                throw new IllegalStateException();
            }
            case 3: {
                return this.setupRandPartB();
            }
            case 4: {
                return this.setupRandPartC();
            }
            case 5: {
                throw new IllegalStateException();
            }
            case 6: {
                return this.setupNoRandPartB();
            }
            case 7: {
                return this.setupNoRandPartC();
            }
            default: {
                throw new IllegalStateException();
            }
        }
    }
    
    private int readNextByte(final BitInputStream in) throws IOException {
        final long b = in.readBits(8);
        return (int)b;
    }
    
    private boolean init(final boolean isFirstStream) throws IOException {
        if (null == this.bin) {
            throw new IOException("No InputStream");
        }
        if (!isFirstStream) {
            this.bin.clearBitCache();
        }
        final int magic0 = this.readNextByte(this.bin);
        if (magic0 == -1 && !isFirstStream) {
            return false;
        }
        final int magic2 = this.readNextByte(this.bin);
        final int magic3 = this.readNextByte(this.bin);
        if (magic0 != 66 || magic2 != 90 || magic3 != 104) {
            throw new IOException(isFirstStream ? "Stream is not in the BZip2 format" : "Garbage after a valid BZip2 stream");
        }
        final int blockSize = this.readNextByte(this.bin);
        if (blockSize < 49 || blockSize > 57) {
            throw new IOException("BZip2 block size is invalid");
        }
        this.blockSize100k = blockSize - 48;
        this.computedCombinedCRC = 0;
        return true;
    }
    
    private void initBlock() throws IOException {
        final BitInputStream bin = this.bin;
        do {
            final char magic0 = bsGetUByte(bin);
            final char magic2 = bsGetUByte(bin);
            final char magic3 = bsGetUByte(bin);
            final char magic4 = bsGetUByte(bin);
            final char magic5 = bsGetUByte(bin);
            final char magic6 = bsGetUByte(bin);
            if (magic0 == '\u0017' && magic2 == 'r' && magic3 == 'E' && magic4 == '8' && magic5 == 'P' && magic6 == '\u0090') {
                continue;
            }
            if (magic0 != '1' || magic2 != 'A' || magic3 != 'Y' || magic4 != '&' || magic5 != 'S' || magic6 != 'Y') {
                this.currentState = 0;
                throw new IOException("bad block header");
            }
            this.storedBlockCRC = bsGetInt(bin);
            this.blockRandomised = (bsR(bin, 1) == 1);
            if (this.data == null) {
                this.data = new Data(this.blockSize100k);
            }
            this.getAndMoveToFrontDecode();
            this.crc.initialiseCRC();
            this.currentState = 1;
        } while (!this.complete());
    }
    
    private void endBlock() throws IOException {
        this.computedBlockCRC = this.crc.getFinalCRC();
        if (this.storedBlockCRC != this.computedBlockCRC) {
            this.computedCombinedCRC = (this.storedCombinedCRC << 1 | this.storedCombinedCRC >>> 31);
            this.computedCombinedCRC ^= this.storedBlockCRC;
            throw new IOException("BZip2 CRC error");
        }
        this.computedCombinedCRC = (this.computedCombinedCRC << 1 | this.computedCombinedCRC >>> 31);
        this.computedCombinedCRC ^= this.computedBlockCRC;
    }
    
    private boolean complete() throws IOException {
        this.storedCombinedCRC = bsGetInt(this.bin);
        this.currentState = 0;
        this.data = null;
        if (this.storedCombinedCRC != this.computedCombinedCRC) {
            throw new IOException("BZip2 CRC error");
        }
        return !this.decompressConcatenated || !this.init(false);
    }
    
    @Override
    public void close() throws IOException {
        final BitInputStream inShadow = this.bin;
        if (inShadow != null) {
            try {
                inShadow.close();
            }
            finally {
                this.data = null;
                this.bin = null;
            }
        }
    }
    
    private static int bsR(final BitInputStream bin, final int n) throws IOException {
        final long thech = bin.readBits(n);
        if (thech < 0L) {
            throw new IOException("unexpected end of stream");
        }
        return (int)thech;
    }
    
    private static boolean bsGetBit(final BitInputStream bin) throws IOException {
        return bsR(bin, 1) != 0;
    }
    
    private static char bsGetUByte(final BitInputStream bin) throws IOException {
        return (char)bsR(bin, 8);
    }
    
    private static int bsGetInt(final BitInputStream bin) throws IOException {
        return bsR(bin, 32);
    }
    
    private static void checkBounds(final int checkVal, final int limitExclusive, final String name) throws IOException {
        if (checkVal < 0) {
            throw new IOException("Corrupted input, " + name + " value negative");
        }
        if (checkVal >= limitExclusive) {
            throw new IOException("Corrupted input, " + name + " value too big");
        }
    }
    
    private static void hbCreateDecodeTables(final int[] limit, final int[] base, final int[] perm, final char[] length, final int minLen, final int maxLen, final int alphaSize) throws IOException {
        int i = minLen;
        int pp = 0;
        while (i <= maxLen) {
            for (int j = 0; j < alphaSize; ++j) {
                if (length[j] == i) {
                    perm[pp++] = j;
                }
            }
            ++i;
        }
        i = 23;
        while (--i > 0) {
            limit[i] = (base[i] = 0);
        }
        for (i = 0; i < alphaSize; ++i) {
            final int l = length[i];
            checkBounds(l, 258, "length");
            final int n = l + 1;
            ++base[n];
        }
        i = 1;
        int b = base[0];
        while (i < 23) {
            b += base[i];
            base[i] = b;
            ++i;
        }
        i = minLen;
        int vec = 0;
        int b2 = base[i];
        while (i <= maxLen) {
            final int nb = base[i + 1];
            vec += nb - b2;
            b2 = nb;
            limit[i] = vec - 1;
            vec <<= 1;
            ++i;
        }
        for (i = minLen + 1; i <= maxLen; ++i) {
            base[i] = (limit[i - 1] + 1 << 1) - base[i];
        }
    }
    
    private void recvDecodingTables() throws IOException {
        final BitInputStream bin = this.bin;
        final Data dataShadow = this.data;
        final boolean[] inUse = dataShadow.inUse;
        final byte[] pos = dataShadow.recvDecodingTables_pos;
        final byte[] selector = dataShadow.selector;
        final byte[] selectorMtf = dataShadow.selectorMtf;
        int inUse2 = 0;
        for (int i = 0; i < 16; ++i) {
            if (bsGetBit(bin)) {
                inUse2 |= 1 << i;
            }
        }
        Arrays.fill(inUse, false);
        for (int i = 0; i < 16; ++i) {
            if ((inUse2 & 1 << i) != 0x0) {
                final int i2 = i << 4;
                for (int j = 0; j < 16; ++j) {
                    if (bsGetBit(bin)) {
                        inUse[i2 + j] = true;
                    }
                }
            }
        }
        this.makeMaps();
        final int alphaSize = this.nInUse + 2;
        final int nGroups = bsR(bin, 3);
        final int nSelectors = bsR(bin, 15);
        checkBounds(alphaSize, 259, "alphaSize");
        checkBounds(nGroups, 7, "nGroups");
        checkBounds(nSelectors, 18003, "nSelectors");
        for (int k = 0; k < nSelectors; ++k) {
            int l = 0;
            while (bsGetBit(bin)) {
                ++l;
            }
            selectorMtf[k] = (byte)l;
        }
        int v = nGroups;
        while (--v >= 0) {
            pos[v] = (byte)v;
        }
        for (int k = 0; k < nSelectors; ++k) {
            int v2 = selectorMtf[k] & 0xFF;
            checkBounds(v2, 6, "selectorMtf");
            final byte tmp = pos[v2];
            while (v2 > 0) {
                pos[v2] = pos[v2 - 1];
                --v2;
            }
            selector[k] = (pos[0] = tmp);
        }
        final char[][] len = dataShadow.temp_charArray2d;
        for (int t = 0; t < nGroups; ++t) {
            int curr = bsR(bin, 5);
            final char[] len_t = len[t];
            for (int m = 0; m < alphaSize; ++m) {
                while (bsGetBit(bin)) {
                    curr += (bsGetBit(bin) ? -1 : 1);
                }
                len_t[m] = (char)curr;
            }
        }
        this.createHuffmanDecodingTables(alphaSize, nGroups);
    }
    
    private void createHuffmanDecodingTables(final int alphaSize, final int nGroups) throws IOException {
        final Data dataShadow = this.data;
        final char[][] len = dataShadow.temp_charArray2d;
        final int[] minLens = dataShadow.minLens;
        final int[][] limit = dataShadow.limit;
        final int[][] base = dataShadow.base;
        final int[][] perm = dataShadow.perm;
        for (int t = 0; t < nGroups; ++t) {
            int minLen = 32;
            int maxLen = 0;
            final char[] len_t = len[t];
            int i = alphaSize;
            while (--i >= 0) {
                final char lent = len_t[i];
                if (lent > maxLen) {
                    maxLen = lent;
                }
                if (lent < minLen) {
                    minLen = lent;
                }
            }
            hbCreateDecodeTables(limit[t], base[t], perm[t], len[t], minLen, maxLen, alphaSize);
            minLens[t] = minLen;
        }
    }
    
    private void getAndMoveToFrontDecode() throws IOException {
        final BitInputStream bin = this.bin;
        this.origPtr = bsR(bin, 24);
        this.recvDecodingTables();
        final Data dataShadow = this.data;
        final byte[] ll8 = dataShadow.ll8;
        final int[] unzftab = dataShadow.unzftab;
        final byte[] selector = dataShadow.selector;
        final byte[] seqToUnseq = dataShadow.seqToUnseq;
        final char[] yy = dataShadow.getAndMoveToFrontDecode_yy;
        final int[] minLens = dataShadow.minLens;
        final int[][] limit = dataShadow.limit;
        final int[][] base = dataShadow.base;
        final int[][] perm = dataShadow.perm;
        final int limitLast = this.blockSize100k * 100000;
        int i = 256;
        while (--i >= 0) {
            yy[i] = (char)i;
            unzftab[i] = 0;
        }
        int groupNo = 0;
        int groupPos = 49;
        final int eob = this.nInUse + 1;
        int nextSym = this.getAndMoveToFrontDecode0();
        int lastShadow = -1;
        int zt = selector[groupNo] & 0xFF;
        checkBounds(zt, 6, "zt");
        int[] base_zt = base[zt];
        int[] limit_zt = limit[zt];
        int[] perm_zt = perm[zt];
        int minLens_zt = minLens[zt];
        while (nextSym != eob) {
            if (nextSym == 0 || nextSym == 1) {
                int s = -1;
                int n = 1;
                while (true) {
                    if (nextSym == 0) {
                        s += n;
                    }
                    else {
                        if (nextSym != 1) {
                            break;
                        }
                        s += n << 1;
                    }
                    if (groupPos == 0) {
                        groupPos = 49;
                        checkBounds(++groupNo, 18002, "groupNo");
                        zt = (selector[groupNo] & 0xFF);
                        checkBounds(zt, 6, "zt");
                        base_zt = base[zt];
                        limit_zt = limit[zt];
                        perm_zt = perm[zt];
                        minLens_zt = minLens[zt];
                    }
                    else {
                        --groupPos;
                    }
                    int zn = minLens_zt;
                    checkBounds(zn, 258, "zn");
                    int zvec;
                    for (zvec = bsR(bin, zn); zvec > limit_zt[zn]; zvec = (zvec << 1 | bsR(bin, 1))) {
                        checkBounds(++zn, 258, "zn");
                    }
                    final int tmp = zvec - base_zt[zn];
                    checkBounds(tmp, 258, "zvec");
                    nextSym = perm_zt[tmp];
                    n <<= 1;
                }
                final int yy2 = yy[0];
                checkBounds(yy2, 256, "yy");
                final byte ch = seqToUnseq[yy2];
                final int[] array = unzftab;
                final int n2 = ch & 0xFF;
                array[n2] += s + 1;
                final int from = ++lastShadow;
                lastShadow += s;
                Arrays.fill(ll8, from, lastShadow + 1, ch);
                if (lastShadow >= limitLast) {
                    throw new IOException("block overrun while expanding RLE in MTF, " + lastShadow + " exceeds " + limitLast);
                }
                continue;
            }
            else {
                if (++lastShadow >= limitLast) {
                    throw new IOException("block overrun in MTF, " + lastShadow + " exceeds " + limitLast);
                }
                checkBounds(nextSym, 257, "nextSym");
                final char tmp2 = yy[nextSym - 1];
                checkBounds(tmp2, 256, "yy");
                final int[] array2 = unzftab;
                final int n3 = seqToUnseq[tmp2] & 0xFF;
                ++array2[n3];
                ll8[lastShadow] = seqToUnseq[tmp2];
                if (nextSym <= 16) {
                    for (int j = nextSym - 1; j > 0; yy[j] = yy[--j]) {}
                }
                else {
                    System.arraycopy(yy, 0, yy, 1, nextSym - 1);
                }
                yy[0] = tmp2;
                if (groupPos == 0) {
                    groupPos = 49;
                    checkBounds(++groupNo, 18002, "groupNo");
                    zt = (selector[groupNo] & 0xFF);
                    checkBounds(zt, 6, "zt");
                    base_zt = base[zt];
                    limit_zt = limit[zt];
                    perm_zt = perm[zt];
                    minLens_zt = minLens[zt];
                }
                else {
                    --groupPos;
                }
                int zn2 = minLens_zt;
                checkBounds(zn2, 258, "zn");
                int zvec2;
                for (zvec2 = bsR(bin, zn2); zvec2 > limit_zt[zn2]; zvec2 = (zvec2 << 1 | bsR(bin, 1))) {
                    checkBounds(++zn2, 258, "zn");
                }
                final int idx = zvec2 - base_zt[zn2];
                checkBounds(idx, 258, "zvec");
                nextSym = perm_zt[idx];
            }
        }
        this.last = lastShadow;
    }
    
    private int getAndMoveToFrontDecode0() throws IOException {
        final Data dataShadow = this.data;
        final int zt = dataShadow.selector[0] & 0xFF;
        checkBounds(zt, 6, "zt");
        final int[] limit_zt = dataShadow.limit[zt];
        int zn = dataShadow.minLens[zt];
        checkBounds(zn, 258, "zn");
        int zvec;
        for (zvec = bsR(this.bin, zn); zvec > limit_zt[zn]; zvec = (zvec << 1 | bsR(this.bin, 1))) {
            checkBounds(++zn, 258, "zn");
        }
        final int tmp = zvec - dataShadow.base[zt][zn];
        checkBounds(tmp, 258, "zvec");
        return dataShadow.perm[zt][tmp];
    }
    
    private int setupBlock() throws IOException {
        if (this.currentState == 0 || this.data == null) {
            return -1;
        }
        final int[] cftab = this.data.cftab;
        final int ttLen = this.last + 1;
        final int[] tt = this.data.initTT(ttLen);
        final byte[] ll8 = this.data.ll8;
        cftab[0] = 0;
        System.arraycopy(this.data.unzftab, 0, cftab, 1, 256);
        int i = 1;
        int c = cftab[0];
        while (i <= 256) {
            c += cftab[i];
            cftab[i] = c;
            ++i;
        }
        i = 0;
        for (int lastShadow = this.last; i <= lastShadow; ++i) {
            final int tmp = cftab[ll8[i] & 0xFF]++;
            checkBounds(tmp, ttLen, "tt index");
            tt[tmp] = i;
        }
        if (this.origPtr < 0 || this.origPtr >= tt.length) {
            throw new IOException("stream corrupted");
        }
        this.su_tPos = tt[this.origPtr];
        this.su_count = 0;
        this.su_i2 = 0;
        this.su_ch2 = 256;
        if (this.blockRandomised) {
            this.su_rNToGo = 0;
            this.su_rTPos = 0;
            return this.setupRandPartA();
        }
        return this.setupNoRandPartA();
    }
    
    private int setupRandPartA() throws IOException {
        if (this.su_i2 <= this.last) {
            this.su_chPrev = this.su_ch2;
            int su_ch2Shadow = this.data.ll8[this.su_tPos] & 0xFF;
            checkBounds(this.su_tPos, this.data.tt.length, "su_tPos");
            this.su_tPos = this.data.tt[this.su_tPos];
            if (this.su_rNToGo == 0) {
                this.su_rNToGo = Rand.rNums(this.su_rTPos) - 1;
                if (++this.su_rTPos == 512) {
                    this.su_rTPos = 0;
                }
            }
            else {
                --this.su_rNToGo;
            }
            su_ch2Shadow = (this.su_ch2 = (su_ch2Shadow ^ ((this.su_rNToGo == 1) ? 1 : 0)));
            ++this.su_i2;
            this.currentState = 3;
            this.crc.updateCRC(su_ch2Shadow);
            return su_ch2Shadow;
        }
        this.endBlock();
        this.initBlock();
        return this.setupBlock();
    }
    
    private int setupNoRandPartA() throws IOException {
        if (this.su_i2 <= this.last) {
            this.su_chPrev = this.su_ch2;
            final int su_ch2Shadow = this.data.ll8[this.su_tPos] & 0xFF;
            this.su_ch2 = su_ch2Shadow;
            checkBounds(this.su_tPos, this.data.tt.length, "su_tPos");
            this.su_tPos = this.data.tt[this.su_tPos];
            ++this.su_i2;
            this.currentState = 6;
            this.crc.updateCRC(su_ch2Shadow);
            return su_ch2Shadow;
        }
        this.currentState = 5;
        this.endBlock();
        this.initBlock();
        return this.setupBlock();
    }
    
    private int setupRandPartB() throws IOException {
        if (this.su_ch2 != this.su_chPrev) {
            this.currentState = 2;
            this.su_count = 1;
            return this.setupRandPartA();
        }
        if (++this.su_count >= 4) {
            this.su_z = (char)(this.data.ll8[this.su_tPos] & 0xFF);
            checkBounds(this.su_tPos, this.data.tt.length, "su_tPos");
            this.su_tPos = this.data.tt[this.su_tPos];
            if (this.su_rNToGo == 0) {
                this.su_rNToGo = Rand.rNums(this.su_rTPos) - 1;
                if (++this.su_rTPos == 512) {
                    this.su_rTPos = 0;
                }
            }
            else {
                --this.su_rNToGo;
            }
            this.su_j2 = 0;
            this.currentState = 4;
            if (this.su_rNToGo == 1) {
                this.su_z ^= '\u0001';
            }
            return this.setupRandPartC();
        }
        this.currentState = 2;
        return this.setupRandPartA();
    }
    
    private int setupRandPartC() throws IOException {
        if (this.su_j2 < this.su_z) {
            this.crc.updateCRC(this.su_ch2);
            ++this.su_j2;
            return this.su_ch2;
        }
        this.currentState = 2;
        ++this.su_i2;
        this.su_count = 0;
        return this.setupRandPartA();
    }
    
    private int setupNoRandPartB() throws IOException {
        if (this.su_ch2 != this.su_chPrev) {
            this.su_count = 1;
            return this.setupNoRandPartA();
        }
        if (++this.su_count >= 4) {
            checkBounds(this.su_tPos, this.data.ll8.length, "su_tPos");
            this.su_z = (char)(this.data.ll8[this.su_tPos] & 0xFF);
            this.su_tPos = this.data.tt[this.su_tPos];
            this.su_j2 = 0;
            return this.setupNoRandPartC();
        }
        return this.setupNoRandPartA();
    }
    
    private int setupNoRandPartC() throws IOException {
        if (this.su_j2 < this.su_z) {
            final int su_ch2Shadow = this.su_ch2;
            this.crc.updateCRC(su_ch2Shadow);
            ++this.su_j2;
            this.currentState = 7;
            return su_ch2Shadow;
        }
        ++this.su_i2;
        this.su_count = 0;
        return this.setupNoRandPartA();
    }
    
    public static boolean matches(final byte[] signature, final int length) {
        return length >= 3 && signature[0] == 66 && signature[1] == 90 && signature[2] == 104;
    }
    
    private static final class Data
    {
        final boolean[] inUse;
        final byte[] seqToUnseq;
        final byte[] selector;
        final byte[] selectorMtf;
        final int[] unzftab;
        final int[][] limit;
        final int[][] base;
        final int[][] perm;
        final int[] minLens;
        final int[] cftab;
        final char[] getAndMoveToFrontDecode_yy;
        final char[][] temp_charArray2d;
        final byte[] recvDecodingTables_pos;
        int[] tt;
        byte[] ll8;
        
        Data(final int blockSize100k) {
            this.inUse = new boolean[256];
            this.seqToUnseq = new byte[256];
            this.selector = new byte[18002];
            this.selectorMtf = new byte[18002];
            this.unzftab = new int[256];
            this.limit = new int[6][258];
            this.base = new int[6][258];
            this.perm = new int[6][258];
            this.minLens = new int[6];
            this.cftab = new int[257];
            this.getAndMoveToFrontDecode_yy = new char[256];
            this.temp_charArray2d = new char[6][258];
            this.recvDecodingTables_pos = new byte[6];
            this.ll8 = new byte[blockSize100k * 100000];
        }
        
        int[] initTT(final int length) {
            int[] ttShadow = this.tt;
            if (ttShadow == null || ttShadow.length < length) {
                ttShadow = (this.tt = new int[length]);
            }
            return ttShadow;
        }
    }
}
