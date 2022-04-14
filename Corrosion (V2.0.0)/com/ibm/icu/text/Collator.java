/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.Norm2AllModes;
import com.ibm.icu.text.CollationKey;
import com.ibm.icu.text.RawCollationKey;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.Freezable;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.VersionInfo;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class Collator
implements Comparator<Object>,
Freezable<Collator> {
    public static final int PRIMARY = 0;
    public static final int SECONDARY = 1;
    public static final int TERTIARY = 2;
    public static final int QUATERNARY = 3;
    public static final int IDENTICAL = 15;
    public static final int FULL_DECOMPOSITION = 15;
    public static final int NO_DECOMPOSITION = 16;
    public static final int CANONICAL_DECOMPOSITION = 17;
    private static ServiceShim shim;
    private static final String[] KEYWORDS;
    private static final String RESOURCE = "collations";
    private static final String BASE = "com/ibm/icu/impl/data/icudt51b/coll";
    private int m_strength_ = 2;
    private int m_decomposition_ = 17;
    private static final boolean DEBUG;
    private ULocale validLocale;
    private ULocale actualLocale;

    public void setStrength(int newStrength) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        if (newStrength != 0 && newStrength != 1 && newStrength != 2 && newStrength != 3 && newStrength != 15) {
            throw new IllegalArgumentException("Incorrect comparison level.");
        }
        this.m_strength_ = newStrength;
    }

    public Collator setStrength2(int newStrength) {
        this.setStrength(newStrength);
        return this;
    }

    public void setDecomposition(int decomposition) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        this.internalSetDecomposition(decomposition);
    }

    protected void internalSetDecomposition(int decomposition) {
        if (decomposition != 16 && decomposition != 17) {
            throw new IllegalArgumentException("Wrong decomposition mode.");
        }
        this.m_decomposition_ = decomposition;
        if (decomposition != 16) {
            Norm2AllModes.getFCDNormalizer2();
        }
    }

    public void setReorderCodes(int ... order) {
        throw new UnsupportedOperationException();
    }

    public static final Collator getInstance() {
        return Collator.getInstance(ULocale.getDefault());
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private static ServiceShim getShim() {
        if (shim == null) {
            try {
                Class<?> cls = Class.forName("com.ibm.icu.text.CollatorServiceShim");
                shim = (ServiceShim)cls.newInstance();
            }
            catch (MissingResourceException e2) {
                throw e2;
            }
            catch (Exception e3) {
                if (DEBUG) {
                    e3.printStackTrace();
                }
                throw new RuntimeException(e3.getMessage());
            }
        }
        return shim;
    }

    public static final Collator getInstance(ULocale locale) {
        return Collator.getShim().getInstance(locale);
    }

    public static final Collator getInstance(Locale locale) {
        return Collator.getInstance(ULocale.forLocale(locale));
    }

    public static final Object registerInstance(Collator collator, ULocale locale) {
        return Collator.getShim().registerInstance(collator, locale);
    }

    public static final Object registerFactory(CollatorFactory factory) {
        return Collator.getShim().registerFactory(factory);
    }

    public static final boolean unregister(Object registryKey) {
        if (shim == null) {
            return false;
        }
        return shim.unregister(registryKey);
    }

    public static Locale[] getAvailableLocales() {
        if (shim == null) {
            return ICUResourceBundle.getAvailableLocales(BASE, ICUResourceBundle.ICU_DATA_CLASS_LOADER);
        }
        return shim.getAvailableLocales();
    }

    public static final ULocale[] getAvailableULocales() {
        if (shim == null) {
            return ICUResourceBundle.getAvailableULocales(BASE, ICUResourceBundle.ICU_DATA_CLASS_LOADER);
        }
        return shim.getAvailableULocales();
    }

    public static final String[] getKeywords() {
        return KEYWORDS;
    }

    public static final String[] getKeywordValues(String keyword) {
        if (!keyword.equals(KEYWORDS[0])) {
            throw new IllegalArgumentException("Invalid keyword: " + keyword);
        }
        return ICUResourceBundle.getKeywordValues(BASE, RESOURCE);
    }

    public static final String[] getKeywordValuesForLocale(String key, ULocale locale, boolean commonlyUsed) {
        String baseLoc = locale.getBaseName();
        LinkedList<String> values = new LinkedList<String>();
        UResourceBundle bundle = UResourceBundle.getBundleInstance(BASE, baseLoc);
        String defcoll = null;
        while (bundle != null) {
            UResourceBundle collations = bundle.get(RESOURCE);
            Enumeration<String> collEnum = collations.getKeys();
            while (collEnum.hasMoreElements()) {
                String collkey = collEnum.nextElement();
                if (collkey.equals("default")) {
                    if (defcoll != null) continue;
                    defcoll = collations.getString("default");
                    continue;
                }
                if (values.contains(collkey)) continue;
                values.add(collkey);
            }
            bundle = ((ICUResourceBundle)bundle).getParent();
        }
        Iterator itr = values.iterator();
        String[] result = new String[values.size()];
        result[0] = defcoll;
        int idx = 1;
        while (itr.hasNext()) {
            String collKey = (String)itr.next();
            if (collKey.equals(defcoll)) continue;
            result[idx++] = collKey;
        }
        return result;
    }

    public static final ULocale getFunctionalEquivalent(String keyword, ULocale locID, boolean[] isAvailable) {
        return ICUResourceBundle.getFunctionalEquivalent(BASE, ICUResourceBundle.ICU_DATA_CLASS_LOADER, RESOURCE, keyword, locID, isAvailable, true);
    }

    public static final ULocale getFunctionalEquivalent(String keyword, ULocale locID) {
        return Collator.getFunctionalEquivalent(keyword, locID, null);
    }

    public static String getDisplayName(Locale objectLocale, Locale displayLocale) {
        return Collator.getShim().getDisplayName(ULocale.forLocale(objectLocale), ULocale.forLocale(displayLocale));
    }

    public static String getDisplayName(ULocale objectLocale, ULocale displayLocale) {
        return Collator.getShim().getDisplayName(objectLocale, displayLocale);
    }

    public static String getDisplayName(Locale objectLocale) {
        return Collator.getShim().getDisplayName(ULocale.forLocale(objectLocale), ULocale.getDefault(ULocale.Category.DISPLAY));
    }

    public static String getDisplayName(ULocale objectLocale) {
        return Collator.getShim().getDisplayName(objectLocale, ULocale.getDefault(ULocale.Category.DISPLAY));
    }

    public int getStrength() {
        return this.m_strength_;
    }

    public int getDecomposition() {
        return this.m_decomposition_;
    }

    public boolean equals(String source, String target) {
        return this.compare(source, target) == 0;
    }

    public UnicodeSet getTailoredSet() {
        return new UnicodeSet(0, 0x10FFFF);
    }

    @Override
    public abstract int compare(String var1, String var2);

    @Override
    public int compare(Object source, Object target) {
        return this.compare((String)source, (String)target);
    }

    public abstract CollationKey getCollationKey(String var1);

    public abstract RawCollationKey getRawCollationKey(String var1, RawCollationKey var2);

    public abstract int setVariableTop(String var1);

    public abstract int getVariableTop();

    public abstract void setVariableTop(int var1);

    public abstract VersionInfo getVersion();

    public abstract VersionInfo getUCAVersion();

    public int[] getReorderCodes() {
        throw new UnsupportedOperationException();
    }

    public static int[] getEquivalentReorderCodes(int reorderCode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFrozen() {
        return false;
    }

    @Override
    public Collator freeze() {
        throw new UnsupportedOperationException("Needs to be implemented by the subclass.");
    }

    @Override
    public Collator cloneAsThawed() {
        throw new UnsupportedOperationException("Needs to be implemented by the subclass.");
    }

    protected Collator() {
    }

    public final ULocale getLocale(ULocale.Type type) {
        return type == ULocale.ACTUAL_LOCALE ? this.actualLocale : this.validLocale;
    }

    final void setLocale(ULocale valid, ULocale actual) {
        if (valid == null != (actual == null)) {
            throw new IllegalArgumentException();
        }
        this.validLocale = valid;
        this.actualLocale = actual;
    }

    static {
        KEYWORDS = new String[]{"collation"};
        DEBUG = ICUDebug.enabled("collator");
    }

    static abstract class ServiceShim {
        ServiceShim() {
        }

        abstract Collator getInstance(ULocale var1);

        abstract Object registerInstance(Collator var1, ULocale var2);

        abstract Object registerFactory(CollatorFactory var1);

        abstract boolean unregister(Object var1);

        abstract Locale[] getAvailableLocales();

        abstract ULocale[] getAvailableULocales();

        abstract String getDisplayName(ULocale var1, ULocale var2);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class CollatorFactory {
        public boolean visible() {
            return true;
        }

        public Collator createCollator(ULocale loc) {
            return this.createCollator(loc.toLocale());
        }

        public Collator createCollator(Locale loc) {
            return this.createCollator(ULocale.forLocale(loc));
        }

        public String getDisplayName(Locale objectLocale, Locale displayLocale) {
            return this.getDisplayName(ULocale.forLocale(objectLocale), ULocale.forLocale(displayLocale));
        }

        public String getDisplayName(ULocale objectLocale, ULocale displayLocale) {
            String name;
            Set<String> supported;
            if (this.visible() && (supported = this.getSupportedLocaleIDs()).contains(name = objectLocale.getBaseName())) {
                return objectLocale.getDisplayName(displayLocale);
            }
            return null;
        }

        public abstract Set<String> getSupportedLocaleIDs();

        protected CollatorFactory() {
        }
    }

    public static interface ReorderCodes {
        public static final int DEFAULT = -1;
        public static final int NONE = 103;
        public static final int OTHERS = 103;
        public static final int SPACE = 4096;
        public static final int FIRST = 4096;
        public static final int PUNCTUATION = 4097;
        public static final int SYMBOL = 4098;
        public static final int CURRENCY = 4099;
        public static final int DIGIT = 4100;
        public static final int LIMIT = 4101;
    }
}

