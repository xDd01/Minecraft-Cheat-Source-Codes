/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleService;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.ICUService;
import com.ibm.icu.text.Collator;
import com.ibm.icu.text.RuleBasedCollator;
import com.ibm.icu.util.ULocale;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

final class CollatorServiceShim
extends Collator.ServiceShim {
    private static ICULocaleService service = new CService();

    CollatorServiceShim() {
    }

    Collator getInstance(ULocale locale) {
        try {
            ULocale[] actualLoc = new ULocale[1];
            Collator coll = (Collator)service.get(locale, actualLoc);
            if (coll == null) {
                throw new MissingResourceException("Could not locate Collator data", "", "");
            }
            coll = (Collator)coll.clone();
            coll.setLocale(actualLoc[0], actualLoc[0]);
            return coll;
        }
        catch (CloneNotSupportedException e2) {
            throw new IllegalStateException(e2.getMessage());
        }
    }

    Object registerInstance(Collator collator, ULocale locale) {
        return service.registerObject((Object)collator, locale);
    }

    Object registerFactory(Collator.CollatorFactory f2) {
        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        class CFactory
        extends ICULocaleService.LocaleKeyFactory {
            Collator.CollatorFactory delegate;

            CFactory(Collator.CollatorFactory fctry) {
                super(fctry.visible());
                this.delegate = fctry;
            }

            @Override
            public Object handleCreate(ULocale loc, int kind, ICUService srvc) {
                Collator coll = this.delegate.createCollator(loc);
                return coll;
            }

            @Override
            public String getDisplayName(String id2, ULocale displayLocale) {
                ULocale objectLocale = new ULocale(id2);
                return this.delegate.getDisplayName(objectLocale, displayLocale);
            }

            @Override
            public Set<String> getSupportedIDs() {
                return this.delegate.getSupportedLocaleIDs();
            }
        }
        return service.registerFactory(new CFactory(f2));
    }

    boolean unregister(Object registryKey) {
        return service.unregisterFactory((ICUService.Factory)registryKey);
    }

    Locale[] getAvailableLocales() {
        Locale[] result = service.isDefault() ? ICUResourceBundle.getAvailableLocales("com/ibm/icu/impl/data/icudt51b/coll", ICUResourceBundle.ICU_DATA_CLASS_LOADER) : service.getAvailableLocales();
        return result;
    }

    ULocale[] getAvailableULocales() {
        ULocale[] result = service.isDefault() ? ICUResourceBundle.getAvailableULocales("com/ibm/icu/impl/data/icudt51b/coll", ICUResourceBundle.ICU_DATA_CLASS_LOADER) : service.getAvailableULocales();
        return result;
    }

    String getDisplayName(ULocale objectLocale, ULocale displayLocale) {
        String id2 = objectLocale.getName();
        return service.getDisplayName(id2, displayLocale);
    }

    private static class CService
    extends ICULocaleService {
        CService() {
            super("Collator");
            class CollatorFactory
            extends ICULocaleService.ICUResourceBundleFactory {
                CollatorFactory() {
                    super("com/ibm/icu/impl/data/icudt51b/coll");
                }

                protected Object handleCreate(ULocale uloc, int kind, ICUService srvc) {
                    return new RuleBasedCollator(uloc);
                }
            }
            this.registerFactory(new CollatorFactory());
            this.markDefault();
        }

        protected Object handleDefault(ICUService.Key key, String[] actualIDReturn) {
            if (actualIDReturn != null) {
                actualIDReturn[0] = "root";
            }
            try {
                return new RuleBasedCollator(ULocale.ROOT);
            }
            catch (MissingResourceException e2) {
                return null;
            }
        }
    }
}

