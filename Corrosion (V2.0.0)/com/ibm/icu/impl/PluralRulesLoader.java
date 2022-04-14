/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.text.PluralRules;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PluralRulesLoader {
    private final Map<String, PluralRules> rulesIdToRules = new HashMap<String, PluralRules>();
    private Map<String, String> localeIdToCardinalRulesId;
    private Map<String, String> localeIdToOrdinalRulesId;
    private Map<String, ULocale> rulesIdToEquivalentULocale;
    public static final PluralRulesLoader loader = new PluralRulesLoader();

    private PluralRulesLoader() {
    }

    public ULocale[] getAvailableULocales() {
        Set<String> keys = this.getLocaleIdToRulesIdMap(PluralRules.PluralType.CARDINAL).keySet();
        ULocale[] locales = new ULocale[keys.size()];
        int n2 = 0;
        Iterator<String> iter = keys.iterator();
        while (iter.hasNext()) {
            locales[n2++] = ULocale.createCanonical(iter.next());
        }
        return locales;
    }

    public ULocale getFunctionalEquivalent(ULocale locale, boolean[] isAvailable) {
        String rulesId;
        if (isAvailable != null && isAvailable.length > 0) {
            String localeId = ULocale.canonicalize(locale.getBaseName());
            Map<String, String> idMap = this.getLocaleIdToRulesIdMap(PluralRules.PluralType.CARDINAL);
            isAvailable[0] = idMap.containsKey(localeId);
        }
        if ((rulesId = this.getRulesIdForLocale(locale, PluralRules.PluralType.CARDINAL)) == null || rulesId.trim().length() == 0) {
            return ULocale.ROOT;
        }
        ULocale result = this.getRulesIdToEquivalentULocaleMap().get(rulesId);
        if (result == null) {
            return ULocale.ROOT;
        }
        return result;
    }

    private Map<String, String> getLocaleIdToRulesIdMap(PluralRules.PluralType type) {
        this.checkBuildRulesIdMaps();
        return type == PluralRules.PluralType.CARDINAL ? this.localeIdToCardinalRulesId : this.localeIdToOrdinalRulesId;
    }

    private Map<String, ULocale> getRulesIdToEquivalentULocaleMap() {
        this.checkBuildRulesIdMaps();
        return this.rulesIdToEquivalentULocale;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void checkBuildRulesIdMaps() {
        boolean haveMap;
        PluralRulesLoader pluralRulesLoader = this;
        synchronized (pluralRulesLoader) {
            haveMap = this.localeIdToCardinalRulesId != null;
        }
        if (!haveMap) {
            Map<String, String> tempLocaleIdToOrdinalRulesId;
            Map<String, ULocale> tempRulesIdToEquivalentULocale;
            Map<String, String> tempLocaleIdToCardinalRulesId;
            try {
                String value;
                String id2;
                UResourceBundle b2;
                int i2;
                UResourceBundle pluralb = this.getPluralBundle();
                UResourceBundle localeb = pluralb.get("locales");
                tempLocaleIdToCardinalRulesId = new TreeMap();
                tempRulesIdToEquivalentULocale = new HashMap();
                for (i2 = 0; i2 < localeb.getSize(); ++i2) {
                    b2 = localeb.get(i2);
                    id2 = b2.getKey();
                    value = b2.getString().intern();
                    tempLocaleIdToCardinalRulesId.put(id2, value);
                    if (tempRulesIdToEquivalentULocale.containsKey(value)) continue;
                    tempRulesIdToEquivalentULocale.put(value, new ULocale(id2));
                }
                localeb = pluralb.get("locales_ordinals");
                tempLocaleIdToOrdinalRulesId = new TreeMap();
                for (i2 = 0; i2 < localeb.getSize(); ++i2) {
                    b2 = localeb.get(i2);
                    id2 = b2.getKey();
                    value = b2.getString().intern();
                    tempLocaleIdToOrdinalRulesId.put(id2, value);
                }
            }
            catch (MissingResourceException e2) {
                tempLocaleIdToCardinalRulesId = Collections.emptyMap();
                tempLocaleIdToOrdinalRulesId = Collections.emptyMap();
                tempRulesIdToEquivalentULocale = Collections.emptyMap();
            }
            PluralRulesLoader pluralRulesLoader2 = this;
            synchronized (pluralRulesLoader2) {
                if (this.localeIdToCardinalRulesId == null) {
                    this.localeIdToCardinalRulesId = tempLocaleIdToCardinalRulesId;
                    this.localeIdToOrdinalRulesId = tempLocaleIdToOrdinalRulesId;
                    this.rulesIdToEquivalentULocale = tempRulesIdToEquivalentULocale;
                }
            }
        }
    }

    public String getRulesIdForLocale(ULocale locale, PluralRules.PluralType type) {
        int ix2;
        Map<String, String> idMap = this.getLocaleIdToRulesIdMap(type);
        String localeId = ULocale.canonicalize(locale.getBaseName());
        String rulesId = null;
        while (null == (rulesId = idMap.get(localeId)) && (ix2 = localeId.lastIndexOf("_")) != -1) {
            localeId = localeId.substring(0, ix2);
        }
        return rulesId;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PluralRules getRulesForRulesId(String rulesId) {
        boolean hasRules;
        PluralRules rules = null;
        Map<String, PluralRules> map = this.rulesIdToRules;
        synchronized (map) {
            hasRules = this.rulesIdToRules.containsKey(rulesId);
            if (hasRules) {
                rules = this.rulesIdToRules.get(rulesId);
            }
        }
        if (!hasRules) {
            try {
                UResourceBundle pluralb = this.getPluralBundle();
                UResourceBundle rulesb = pluralb.get("rules");
                UResourceBundle setb = rulesb.get(rulesId);
                StringBuilder sb2 = new StringBuilder();
                for (int i2 = 0; i2 < setb.getSize(); ++i2) {
                    UResourceBundle b2 = setb.get(i2);
                    if (i2 > 0) {
                        sb2.append("; ");
                    }
                    sb2.append(b2.getKey());
                    sb2.append(": ");
                    sb2.append(b2.getString());
                }
                rules = PluralRules.parseDescription(sb2.toString());
            }
            catch (ParseException e2) {
            }
            catch (MissingResourceException e3) {
                // empty catch block
            }
            map = this.rulesIdToRules;
            synchronized (map) {
                if (this.rulesIdToRules.containsKey(rulesId)) {
                    rules = this.rulesIdToRules.get(rulesId);
                } else {
                    this.rulesIdToRules.put(rulesId, rules);
                }
            }
        }
        return rules;
    }

    public UResourceBundle getPluralBundle() throws MissingResourceException {
        return ICUResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "plurals", ICUResourceBundle.ICU_DATA_CLASS_LOADER, true);
    }

    public PluralRules forLocale(ULocale locale, PluralRules.PluralType type) {
        String rulesId = this.getRulesIdForLocale(locale, type);
        if (rulesId == null || rulesId.trim().length() == 0) {
            return PluralRules.DEFAULT;
        }
        PluralRules rules = this.getRulesForRulesId(rulesId);
        if (rules == null) {
            rules = PluralRules.DEFAULT;
        }
        return rules;
    }
}

