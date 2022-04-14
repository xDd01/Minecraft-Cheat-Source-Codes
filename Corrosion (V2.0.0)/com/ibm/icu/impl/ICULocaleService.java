/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.ICUService;
import com.ibm.icu.impl.LocaleUtility;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.ULocale;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class ICULocaleService
extends ICUService {
    private ULocale fallbackLocale;
    private String fallbackLocaleName;

    public ICULocaleService() {
    }

    public ICULocaleService(String name) {
        super(name);
    }

    public Object get(ULocale locale) {
        return this.get(locale, -1, null);
    }

    public Object get(ULocale locale, int kind) {
        return this.get(locale, kind, null);
    }

    public Object get(ULocale locale, ULocale[] actualReturn) {
        return this.get(locale, -1, actualReturn);
    }

    public Object get(ULocale locale, int kind, ULocale[] actualReturn) {
        ICUService.Key key = this.createKey(locale, kind);
        if (actualReturn == null) {
            return this.getKey(key);
        }
        String[] temp = new String[1];
        Object result = this.getKey(key, temp);
        if (result != null) {
            int n2 = temp[0].indexOf("/");
            if (n2 >= 0) {
                temp[0] = temp[0].substring(n2 + 1);
            }
            actualReturn[0] = new ULocale(temp[0]);
        }
        return result;
    }

    public ICUService.Factory registerObject(Object obj, ULocale locale) {
        return this.registerObject(obj, locale, -1, true);
    }

    public ICUService.Factory registerObject(Object obj, ULocale locale, boolean visible) {
        return this.registerObject(obj, locale, -1, visible);
    }

    public ICUService.Factory registerObject(Object obj, ULocale locale, int kind) {
        return this.registerObject(obj, locale, kind, true);
    }

    public ICUService.Factory registerObject(Object obj, ULocale locale, int kind, boolean visible) {
        SimpleLocaleKeyFactory factory = new SimpleLocaleKeyFactory(obj, locale, kind, visible);
        return this.registerFactory(factory);
    }

    public Locale[] getAvailableLocales() {
        Set<String> visIDs = this.getVisibleIDs();
        Locale[] locales = new Locale[visIDs.size()];
        int n2 = 0;
        for (String id2 : visIDs) {
            Locale loc = LocaleUtility.getLocaleFromName(id2);
            locales[n2++] = loc;
        }
        return locales;
    }

    public ULocale[] getAvailableULocales() {
        Set<String> visIDs = this.getVisibleIDs();
        ULocale[] locales = new ULocale[visIDs.size()];
        int n2 = 0;
        for (String id2 : visIDs) {
            locales[n2++] = new ULocale(id2);
        }
        return locales;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String validateFallbackLocale() {
        ULocale loc = ULocale.getDefault();
        if (loc != this.fallbackLocale) {
            ICULocaleService iCULocaleService = this;
            synchronized (iCULocaleService) {
                if (loc != this.fallbackLocale) {
                    this.fallbackLocale = loc;
                    this.fallbackLocaleName = loc.getBaseName();
                    this.clearServiceCache();
                }
            }
        }
        return this.fallbackLocaleName;
    }

    public ICUService.Key createKey(String id2) {
        return LocaleKey.createWithCanonicalFallback(id2, this.validateFallbackLocale());
    }

    public ICUService.Key createKey(String id2, int kind) {
        return LocaleKey.createWithCanonicalFallback(id2, this.validateFallbackLocale(), kind);
    }

    public ICUService.Key createKey(ULocale l2, int kind) {
        return LocaleKey.createWithCanonical(l2, this.validateFallbackLocale(), kind);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ICUResourceBundleFactory
    extends LocaleKeyFactory {
        protected final String bundleName;

        public ICUResourceBundleFactory() {
            this("com/ibm/icu/impl/data/icudt51b");
        }

        public ICUResourceBundleFactory(String bundleName) {
            super(true);
            this.bundleName = bundleName;
        }

        @Override
        protected Set<String> getSupportedIDs() {
            return ICUResourceBundle.getFullLocaleNameSet(this.bundleName, this.loader());
        }

        @Override
        public void updateVisibleIDs(Map<String, ICUService.Factory> result) {
            Set<String> visibleIDs = ICUResourceBundle.getAvailableLocaleNameSet(this.bundleName, this.loader());
            for (String id2 : visibleIDs) {
                result.put(id2, this);
            }
        }

        @Override
        protected Object handleCreate(ULocale loc, int kind, ICUService service) {
            return ICUResourceBundle.getBundleInstance(this.bundleName, loc, this.loader());
        }

        protected ClassLoader loader() {
            ClassLoader cl2 = this.getClass().getClassLoader();
            if (cl2 == null) {
                cl2 = Utility.getFallbackClassLoader();
            }
            return cl2;
        }

        @Override
        public String toString() {
            return super.toString() + ", bundle: " + this.bundleName;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class SimpleLocaleKeyFactory
    extends LocaleKeyFactory {
        private final Object obj;
        private final String id;
        private final int kind;

        public SimpleLocaleKeyFactory(Object obj, ULocale locale, int kind, boolean visible) {
            this(obj, locale, kind, visible, null);
        }

        public SimpleLocaleKeyFactory(Object obj, ULocale locale, int kind, boolean visible, String name) {
            super(visible, name);
            this.obj = obj;
            this.id = locale.getBaseName();
            this.kind = kind;
        }

        @Override
        public Object create(ICUService.Key key, ICUService service) {
            if (!(key instanceof LocaleKey)) {
                return null;
            }
            LocaleKey lkey = (LocaleKey)key;
            if (this.kind != -1 && this.kind != lkey.kind()) {
                return null;
            }
            if (!this.id.equals(lkey.currentID())) {
                return null;
            }
            return this.obj;
        }

        @Override
        protected boolean isSupportedID(String idToCheck) {
            return this.id.equals(idToCheck);
        }

        @Override
        public void updateVisibleIDs(Map<String, ICUService.Factory> result) {
            if (this.visible) {
                result.put(this.id, this);
            } else {
                result.remove(this.id);
            }
        }

        @Override
        public String toString() {
            StringBuilder buf = new StringBuilder(super.toString());
            buf.append(", id: ");
            buf.append(this.id);
            buf.append(", kind: ");
            buf.append(this.kind);
            return buf.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class LocaleKeyFactory
    implements ICUService.Factory {
        protected final String name;
        protected final boolean visible;
        public static final boolean VISIBLE = true;
        public static final boolean INVISIBLE = false;

        protected LocaleKeyFactory(boolean visible) {
            this.visible = visible;
            this.name = null;
        }

        protected LocaleKeyFactory(boolean visible, String name) {
            this.visible = visible;
            this.name = name;
        }

        @Override
        public Object create(ICUService.Key key, ICUService service) {
            if (this.handlesKey(key)) {
                LocaleKey lkey = (LocaleKey)key;
                int kind = lkey.kind();
                ULocale uloc = lkey.currentLocale();
                return this.handleCreate(uloc, kind, service);
            }
            return null;
        }

        protected boolean handlesKey(ICUService.Key key) {
            if (key != null) {
                String id2 = key.currentID();
                Set<String> supported = this.getSupportedIDs();
                return supported.contains(id2);
            }
            return false;
        }

        @Override
        public void updateVisibleIDs(Map<String, ICUService.Factory> result) {
            Set<String> cache = this.getSupportedIDs();
            for (String id2 : cache) {
                if (this.visible) {
                    result.put(id2, this);
                    continue;
                }
                result.remove(id2);
            }
        }

        @Override
        public String getDisplayName(String id2, ULocale locale) {
            if (locale == null) {
                return id2;
            }
            ULocale loc = new ULocale(id2);
            return loc.getDisplayName(locale);
        }

        protected Object handleCreate(ULocale loc, int kind, ICUService service) {
            return null;
        }

        protected boolean isSupportedID(String id2) {
            return this.getSupportedIDs().contains(id2);
        }

        protected Set<String> getSupportedIDs() {
            return Collections.emptySet();
        }

        public String toString() {
            StringBuilder buf = new StringBuilder(super.toString());
            if (this.name != null) {
                buf.append(", name: ");
                buf.append(this.name);
            }
            buf.append(", visible: ");
            buf.append(this.visible);
            return buf.toString();
        }
    }

    public static class LocaleKey
    extends ICUService.Key {
        private int kind;
        private int varstart;
        private String primaryID;
        private String fallbackID;
        private String currentID;
        public static final int KIND_ANY = -1;

        public static LocaleKey createWithCanonicalFallback(String primaryID, String canonicalFallbackID) {
            return LocaleKey.createWithCanonicalFallback(primaryID, canonicalFallbackID, -1);
        }

        public static LocaleKey createWithCanonicalFallback(String primaryID, String canonicalFallbackID, int kind) {
            if (primaryID == null) {
                return null;
            }
            String canonicalPrimaryID = ULocale.getName(primaryID);
            return new LocaleKey(primaryID, canonicalPrimaryID, canonicalFallbackID, kind);
        }

        public static LocaleKey createWithCanonical(ULocale locale, String canonicalFallbackID, int kind) {
            if (locale == null) {
                return null;
            }
            String canonicalPrimaryID = locale.getName();
            return new LocaleKey(canonicalPrimaryID, canonicalPrimaryID, canonicalFallbackID, kind);
        }

        protected LocaleKey(String primaryID, String canonicalPrimaryID, String canonicalFallbackID, int kind) {
            super(primaryID);
            this.kind = kind;
            if (canonicalPrimaryID == null || canonicalPrimaryID.equalsIgnoreCase("root")) {
                this.primaryID = "";
                this.fallbackID = null;
            } else {
                int idx = canonicalPrimaryID.indexOf(64);
                if (idx == 4 && canonicalPrimaryID.regionMatches(true, 0, "root", 0, 4)) {
                    this.primaryID = canonicalPrimaryID.substring(4);
                    this.varstart = 0;
                    this.fallbackID = null;
                } else {
                    this.primaryID = canonicalPrimaryID;
                    this.varstart = idx;
                    this.fallbackID = canonicalFallbackID == null || this.primaryID.equals(canonicalFallbackID) ? "" : canonicalFallbackID;
                }
            }
            this.currentID = this.varstart == -1 ? this.primaryID : this.primaryID.substring(0, this.varstart);
        }

        public String prefix() {
            return this.kind == -1 ? null : Integer.toString(this.kind());
        }

        public int kind() {
            return this.kind;
        }

        public String canonicalID() {
            return this.primaryID;
        }

        public String currentID() {
            return this.currentID;
        }

        public String currentDescriptor() {
            String result = this.currentID();
            if (result != null) {
                StringBuilder buf = new StringBuilder();
                if (this.kind != -1) {
                    buf.append(this.prefix());
                }
                buf.append('/');
                buf.append(result);
                if (this.varstart != -1) {
                    buf.append(this.primaryID.substring(this.varstart, this.primaryID.length()));
                }
                result = buf.toString();
            }
            return result;
        }

        public ULocale canonicalLocale() {
            return new ULocale(this.primaryID);
        }

        public ULocale currentLocale() {
            if (this.varstart == -1) {
                return new ULocale(this.currentID);
            }
            return new ULocale(this.currentID + this.primaryID.substring(this.varstart));
        }

        public boolean fallback() {
            int x2 = this.currentID.lastIndexOf(95);
            if (x2 != -1) {
                while (--x2 >= 0 && this.currentID.charAt(x2) == '_') {
                }
                this.currentID = this.currentID.substring(0, x2 + 1);
                return true;
            }
            if (this.fallbackID != null) {
                this.currentID = this.fallbackID;
                this.fallbackID = this.fallbackID.length() == 0 ? null : "";
                return true;
            }
            this.currentID = null;
            return false;
        }

        public boolean isFallbackOf(String id2) {
            return LocaleUtility.isFallbackOf(this.canonicalID(), id2);
        }
    }
}

