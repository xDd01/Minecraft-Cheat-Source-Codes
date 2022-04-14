/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.CacheBase;
import com.ibm.icu.impl.Normalizer2Impl;
import com.ibm.icu.impl.SoftCache;
import com.ibm.icu.text.Normalizer;
import com.ibm.icu.text.Normalizer2;
import java.io.IOException;
import java.io.InputStream;

public final class Norm2AllModes {
    public final Normalizer2Impl impl;
    public final ComposeNormalizer2 comp;
    public final DecomposeNormalizer2 decomp;
    public final FCDNormalizer2 fcd;
    public final ComposeNormalizer2 fcc;
    private static CacheBase<String, Norm2AllModes, InputStream> cache = new SoftCache<String, Norm2AllModes, InputStream>(){

        @Override
        protected Norm2AllModes createInstance(String key, InputStream data) {
            Normalizer2Impl impl = data == null ? new Normalizer2Impl().load("data/icudt51b/" + key + ".nrm") : new Normalizer2Impl().load(data);
            return new Norm2AllModes(impl);
        }
    };
    public static final NoopNormalizer2 NOOP_NORMALIZER2 = new NoopNormalizer2();

    private Norm2AllModes(Normalizer2Impl ni2) {
        this.impl = ni2;
        this.comp = new ComposeNormalizer2(ni2, false);
        this.decomp = new DecomposeNormalizer2(ni2);
        this.fcd = new FCDNormalizer2(ni2);
        this.fcc = new ComposeNormalizer2(ni2, true);
    }

    private static Norm2AllModes getInstanceFromSingleton(Norm2AllModesSingleton singleton) {
        if (singleton.exception != null) {
            throw singleton.exception;
        }
        return singleton.allModes;
    }

    public static Norm2AllModes getNFCInstance() {
        return Norm2AllModes.getInstanceFromSingleton(NFCSingleton.INSTANCE);
    }

    public static Norm2AllModes getNFKCInstance() {
        return Norm2AllModes.getInstanceFromSingleton(NFKCSingleton.INSTANCE);
    }

    public static Norm2AllModes getNFKC_CFInstance() {
        return Norm2AllModes.getInstanceFromSingleton(NFKC_CFSingleton.INSTANCE);
    }

    public static Normalizer2WithImpl getN2WithImpl(int index) {
        switch (index) {
            case 0: {
                return Norm2AllModes.getNFCInstance().decomp;
            }
            case 1: {
                return Norm2AllModes.getNFKCInstance().decomp;
            }
            case 2: {
                return Norm2AllModes.getNFCInstance().comp;
            }
            case 3: {
                return Norm2AllModes.getNFKCInstance().comp;
            }
        }
        return null;
    }

    public static Norm2AllModes getInstance(InputStream data, String name) {
        Norm2AllModesSingleton singleton;
        if (data == null && (singleton = name.equals("nfc") ? NFCSingleton.INSTANCE : (name.equals("nfkc") ? NFKCSingleton.INSTANCE : (name.equals("nfkc_cf") ? NFKC_CFSingleton.INSTANCE : null))) != null) {
            if (singleton.exception != null) {
                throw singleton.exception;
            }
            return singleton.allModes;
        }
        return cache.getInstance(name, data);
    }

    public static Normalizer2 getFCDNormalizer2() {
        return Norm2AllModes.getNFCInstance().fcd;
    }

    private static final class NFKC_CFSingleton {
        private static final Norm2AllModesSingleton INSTANCE = new Norm2AllModesSingleton("nfkc_cf");

        private NFKC_CFSingleton() {
        }
    }

    private static final class NFKCSingleton {
        private static final Norm2AllModesSingleton INSTANCE = new Norm2AllModesSingleton("nfkc");

        private NFKCSingleton() {
        }
    }

    private static final class NFCSingleton {
        private static final Norm2AllModesSingleton INSTANCE = new Norm2AllModesSingleton("nfc");

        private NFCSingleton() {
        }
    }

    private static final class Norm2AllModesSingleton {
        private Norm2AllModes allModes;
        private RuntimeException exception;

        private Norm2AllModesSingleton(String name) {
            try {
                Normalizer2Impl impl = new Normalizer2Impl().load("data/icudt51b/" + name + ".nrm");
                this.allModes = new Norm2AllModes(impl);
            }
            catch (RuntimeException e2) {
                this.exception = e2;
            }
        }
    }

    public static final class FCDNormalizer2
    extends Normalizer2WithImpl {
        public FCDNormalizer2(Normalizer2Impl ni2) {
            super(ni2);
        }

        protected void normalize(CharSequence src, Normalizer2Impl.ReorderingBuffer buffer) {
            this.impl.makeFCD(src, 0, src.length(), buffer);
        }

        protected void normalizeAndAppend(CharSequence src, boolean doNormalize, Normalizer2Impl.ReorderingBuffer buffer) {
            this.impl.makeFCDAndAppend(src, doNormalize, buffer);
        }

        public int spanQuickCheckYes(CharSequence s2) {
            return this.impl.makeFCD(s2, 0, s2.length(), null);
        }

        public int getQuickCheck(int c2) {
            return this.impl.isDecompYes(this.impl.getNorm16(c2)) ? 1 : 0;
        }

        public boolean hasBoundaryBefore(int c2) {
            return this.impl.hasFCDBoundaryBefore(c2);
        }

        public boolean hasBoundaryAfter(int c2) {
            return this.impl.hasFCDBoundaryAfter(c2);
        }

        public boolean isInert(int c2) {
            return this.impl.isFCDInert(c2);
        }
    }

    public static final class ComposeNormalizer2
    extends Normalizer2WithImpl {
        private final boolean onlyContiguous;

        public ComposeNormalizer2(Normalizer2Impl ni2, boolean fcc) {
            super(ni2);
            this.onlyContiguous = fcc;
        }

        protected void normalize(CharSequence src, Normalizer2Impl.ReorderingBuffer buffer) {
            this.impl.compose(src, 0, src.length(), this.onlyContiguous, true, buffer);
        }

        protected void normalizeAndAppend(CharSequence src, boolean doNormalize, Normalizer2Impl.ReorderingBuffer buffer) {
            this.impl.composeAndAppend(src, doNormalize, this.onlyContiguous, buffer);
        }

        public boolean isNormalized(CharSequence s2) {
            return this.impl.compose(s2, 0, s2.length(), this.onlyContiguous, false, new Normalizer2Impl.ReorderingBuffer(this.impl, new StringBuilder(), 5));
        }

        public Normalizer.QuickCheckResult quickCheck(CharSequence s2) {
            int spanLengthAndMaybe = this.impl.composeQuickCheck(s2, 0, s2.length(), this.onlyContiguous, false);
            if ((spanLengthAndMaybe & 1) != 0) {
                return Normalizer.MAYBE;
            }
            if (spanLengthAndMaybe >>> 1 == s2.length()) {
                return Normalizer.YES;
            }
            return Normalizer.NO;
        }

        public int spanQuickCheckYes(CharSequence s2) {
            return this.impl.composeQuickCheck(s2, 0, s2.length(), this.onlyContiguous, true) >>> 1;
        }

        public int getQuickCheck(int c2) {
            return this.impl.getCompQuickCheck(this.impl.getNorm16(c2));
        }

        public boolean hasBoundaryBefore(int c2) {
            return this.impl.hasCompBoundaryBefore(c2);
        }

        public boolean hasBoundaryAfter(int c2) {
            return this.impl.hasCompBoundaryAfter(c2, this.onlyContiguous, false);
        }

        public boolean isInert(int c2) {
            return this.impl.hasCompBoundaryAfter(c2, this.onlyContiguous, true);
        }
    }

    public static final class DecomposeNormalizer2
    extends Normalizer2WithImpl {
        public DecomposeNormalizer2(Normalizer2Impl ni2) {
            super(ni2);
        }

        protected void normalize(CharSequence src, Normalizer2Impl.ReorderingBuffer buffer) {
            this.impl.decompose(src, 0, src.length(), buffer);
        }

        protected void normalizeAndAppend(CharSequence src, boolean doNormalize, Normalizer2Impl.ReorderingBuffer buffer) {
            this.impl.decomposeAndAppend(src, doNormalize, buffer);
        }

        public int spanQuickCheckYes(CharSequence s2) {
            return this.impl.decompose(s2, 0, s2.length(), null);
        }

        public int getQuickCheck(int c2) {
            return this.impl.isDecompYes(this.impl.getNorm16(c2)) ? 1 : 0;
        }

        public boolean hasBoundaryBefore(int c2) {
            return this.impl.hasDecompBoundary(c2, true);
        }

        public boolean hasBoundaryAfter(int c2) {
            return this.impl.hasDecompBoundary(c2, false);
        }

        public boolean isInert(int c2) {
            return this.impl.isDecompInert(c2);
        }
    }

    public static abstract class Normalizer2WithImpl
    extends Normalizer2 {
        public final Normalizer2Impl impl;

        public Normalizer2WithImpl(Normalizer2Impl ni2) {
            this.impl = ni2;
        }

        public StringBuilder normalize(CharSequence src, StringBuilder dest) {
            if (dest == src) {
                throw new IllegalArgumentException();
            }
            dest.setLength(0);
            this.normalize(src, new Normalizer2Impl.ReorderingBuffer(this.impl, dest, src.length()));
            return dest;
        }

        public Appendable normalize(CharSequence src, Appendable dest) {
            if (dest == src) {
                throw new IllegalArgumentException();
            }
            Normalizer2Impl.ReorderingBuffer buffer = new Normalizer2Impl.ReorderingBuffer(this.impl, dest, src.length());
            this.normalize(src, buffer);
            buffer.flush();
            return dest;
        }

        protected abstract void normalize(CharSequence var1, Normalizer2Impl.ReorderingBuffer var2);

        public StringBuilder normalizeSecondAndAppend(StringBuilder first, CharSequence second) {
            return this.normalizeSecondAndAppend(first, second, true);
        }

        public StringBuilder append(StringBuilder first, CharSequence second) {
            return this.normalizeSecondAndAppend(first, second, false);
        }

        public StringBuilder normalizeSecondAndAppend(StringBuilder first, CharSequence second, boolean doNormalize) {
            if (first == second) {
                throw new IllegalArgumentException();
            }
            this.normalizeAndAppend(second, doNormalize, new Normalizer2Impl.ReorderingBuffer(this.impl, first, first.length() + second.length()));
            return first;
        }

        protected abstract void normalizeAndAppend(CharSequence var1, boolean var2, Normalizer2Impl.ReorderingBuffer var3);

        public String getDecomposition(int c2) {
            return this.impl.getDecomposition(c2);
        }

        public String getRawDecomposition(int c2) {
            return this.impl.getRawDecomposition(c2);
        }

        public int composePair(int a2, int b2) {
            return this.impl.composePair(a2, b2);
        }

        public int getCombiningClass(int c2) {
            return this.impl.getCC(this.impl.getNorm16(c2));
        }

        public boolean isNormalized(CharSequence s2) {
            return s2.length() == this.spanQuickCheckYes(s2);
        }

        public Normalizer.QuickCheckResult quickCheck(CharSequence s2) {
            return this.isNormalized(s2) ? Normalizer.YES : Normalizer.NO;
        }

        public int getQuickCheck(int c2) {
            return 1;
        }
    }

    public static final class NoopNormalizer2
    extends Normalizer2 {
        public StringBuilder normalize(CharSequence src, StringBuilder dest) {
            if (dest != src) {
                dest.setLength(0);
                return dest.append(src);
            }
            throw new IllegalArgumentException();
        }

        public Appendable normalize(CharSequence src, Appendable dest) {
            if (dest != src) {
                try {
                    return dest.append(src);
                }
                catch (IOException e2) {
                    throw new RuntimeException(e2);
                }
            }
            throw new IllegalArgumentException();
        }

        public StringBuilder normalizeSecondAndAppend(StringBuilder first, CharSequence second) {
            if (first != second) {
                return first.append(second);
            }
            throw new IllegalArgumentException();
        }

        public StringBuilder append(StringBuilder first, CharSequence second) {
            if (first != second) {
                return first.append(second);
            }
            throw new IllegalArgumentException();
        }

        public String getDecomposition(int c2) {
            return null;
        }

        public boolean isNormalized(CharSequence s2) {
            return true;
        }

        public Normalizer.QuickCheckResult quickCheck(CharSequence s2) {
            return Normalizer.YES;
        }

        public int spanQuickCheckYes(CharSequence s2) {
            return s2.length();
        }

        public boolean hasBoundaryBefore(int c2) {
            return true;
        }

        public boolean hasBoundaryAfter(int c2) {
            return true;
        }

        public boolean isInert(int c2) {
            return true;
        }
    }
}

