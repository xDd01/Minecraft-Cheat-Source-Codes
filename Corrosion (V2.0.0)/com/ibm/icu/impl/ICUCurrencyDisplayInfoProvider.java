/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.CurrencyData;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;

public class ICUCurrencyDisplayInfoProvider
implements CurrencyData.CurrencyDisplayInfoProvider {
    public CurrencyData.CurrencyDisplayInfo getInstance(ULocale locale, boolean withFallback) {
        int status;
        ICUResourceBundle rb2 = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/curr", locale);
        if (!(withFallback || (status = rb2.getLoadingStatus()) != 3 && status != 2)) {
            return null;
        }
        return new ICUCurrencyDisplayInfo(rb2, withFallback);
    }

    public boolean hasData() {
        return true;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    static class ICUCurrencyDisplayInfo
    extends CurrencyData.CurrencyDisplayInfo {
        private final boolean fallback;
        private final ICUResourceBundle rb;
        private final ICUResourceBundle currencies;
        private final ICUResourceBundle plurals;
        private SoftReference<Map<String, String>> _symbolMapRef;
        private SoftReference<Map<String, String>> _nameMapRef;

        public ICUCurrencyDisplayInfo(ICUResourceBundle rb2, boolean fallback) {
            this.fallback = fallback;
            this.rb = rb2;
            this.currencies = rb2.findTopLevel("Currencies");
            this.plurals = rb2.findTopLevel("CurrencyPlurals");
        }

        @Override
        public ULocale getULocale() {
            return this.rb.getULocale();
        }

        @Override
        public String getName(String isoCode) {
            return this.getName(isoCode, false);
        }

        @Override
        public String getSymbol(String isoCode) {
            return this.getName(isoCode, true);
        }

        private String getName(String isoCode, boolean symbolName) {
            ICUResourceBundle result;
            if (this.currencies != null && (result = this.currencies.findWithFallback(isoCode)) != null) {
                int status;
                if (!(this.fallback || (status = result.getLoadingStatus()) != 3 && status != 2)) {
                    return null;
                }
                return result.getString(symbolName ? 0 : 1);
            }
            return this.fallback ? isoCode : null;
        }

        @Override
        public String getPluralName(String isoCode, String pluralKey) {
            ICUResourceBundle pluralsBundle;
            if (this.plurals != null && (pluralsBundle = this.plurals.findWithFallback(isoCode)) != null) {
                ICUResourceBundle pluralBundle = pluralsBundle.findWithFallback(pluralKey);
                if (pluralBundle == null) {
                    if (!this.fallback) {
                        return null;
                    }
                    pluralBundle = pluralsBundle.findWithFallback("other");
                    if (pluralBundle == null) {
                        return this.getName(isoCode);
                    }
                }
                return pluralBundle.getString();
            }
            return this.fallback ? this.getName(isoCode) : null;
        }

        @Override
        public Map<String, String> symbolMap() {
            Map<String, String> map;
            Map<String, String> map2 = map = this._symbolMapRef == null ? null : this._symbolMapRef.get();
            if (map == null) {
                map = this._createSymbolMap();
                this._symbolMapRef = new SoftReference<Map<String, String>>(map);
            }
            return map;
        }

        @Override
        public Map<String, String> nameMap() {
            Map<String, String> map;
            Map<String, String> map2 = map = this._nameMapRef == null ? null : this._nameMapRef.get();
            if (map == null) {
                map = this._createNameMap();
                this._nameMapRef = new SoftReference<Map<String, String>>(map);
            }
            return map;
        }

        @Override
        public Map<String, String> getUnitPatterns() {
            HashMap<String, String> result = new HashMap<String, String>();
            for (ULocale locale = this.rb.getULocale(); locale != null; locale = locale.getFallback()) {
                ICUResourceBundle cr2;
                ICUResourceBundle r2 = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/curr", locale);
                if (r2 == null || (cr2 = r2.findWithFallback("CurrencyUnitPatterns")) == null) continue;
                int size = cr2.getSize();
                for (int index = 0; index < size; ++index) {
                    ICUResourceBundle b2 = (ICUResourceBundle)cr2.get(index);
                    String key = b2.getKey();
                    if (result.containsKey(key)) continue;
                    result.put(key, b2.getString());
                }
            }
            return Collections.unmodifiableMap(result);
        }

        @Override
        public CurrencyData.CurrencyFormatInfo getFormatInfo(String isoCode) {
            ICUResourceBundle crb = this.currencies.findWithFallback(isoCode);
            if (crb != null && crb.getSize() > 2 && (crb = crb.at(2)) != null) {
                String pattern = crb.getString(0);
                char separator = crb.getString(1).charAt(0);
                char groupingSeparator = crb.getString(2).charAt(0);
                return new CurrencyData.CurrencyFormatInfo(pattern, separator, groupingSeparator);
            }
            return null;
        }

        @Override
        public CurrencyData.CurrencySpacingInfo getSpacingInfo() {
            ICUResourceBundle srb = this.rb.findWithFallback("currencySpacing");
            if (srb != null) {
                ICUResourceBundle brb = srb.findWithFallback("beforeCurrency");
                ICUResourceBundle arb2 = srb.findWithFallback("afterCurrency");
                if (arb2 != null && brb != null) {
                    String beforeCurrencyMatch = brb.findWithFallback("currencyMatch").getString();
                    String beforeContextMatch = brb.findWithFallback("surroundingMatch").getString();
                    String beforeInsert = brb.findWithFallback("insertBetween").getString();
                    String afterCurrencyMatch = arb2.findWithFallback("currencyMatch").getString();
                    String afterContextMatch = arb2.findWithFallback("surroundingMatch").getString();
                    String afterInsert = arb2.findWithFallback("insertBetween").getString();
                    return new CurrencyData.CurrencySpacingInfo(beforeCurrencyMatch, beforeContextMatch, beforeInsert, afterCurrencyMatch, afterContextMatch, afterInsert);
                }
            }
            return this.fallback ? CurrencyData.CurrencySpacingInfo.DEFAULT : null;
        }

        private Map<String, String> _createSymbolMap() {
            HashMap<String, String> result = new HashMap<String, String>();
            for (ULocale locale = this.rb.getULocale(); locale != null; locale = locale.getFallback()) {
                ICUResourceBundle bundle = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/curr", locale);
                ICUResourceBundle curr = bundle.findTopLevel("Currencies");
                if (curr == null) continue;
                for (int i2 = 0; i2 < curr.getSize(); ++i2) {
                    ICUResourceBundle item = curr.at(i2);
                    String isoCode = item.getKey();
                    if (result.containsKey(isoCode)) continue;
                    result.put(isoCode, isoCode);
                    String symbol = item.getString(0);
                    result.put(symbol, isoCode);
                }
            }
            return Collections.unmodifiableMap(result);
        }

        private Map<String, String> _createNameMap() {
            TreeMap<String, String> result = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
            HashSet<String> visited = new HashSet<String>();
            HashMap visitedPlurals = new HashMap();
            for (ULocale locale = this.rb.getULocale(); locale != null; locale = locale.getFallback()) {
                ICUResourceBundle plurals;
                ICUResourceBundle bundle = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/curr", locale);
                ICUResourceBundle curr = bundle.findTopLevel("Currencies");
                if (curr != null) {
                    for (int i2 = 0; i2 < curr.getSize(); ++i2) {
                        ICUResourceBundle item = curr.at(i2);
                        String isoCode = item.getKey();
                        if (visited.contains(isoCode)) continue;
                        visited.add(isoCode);
                        String name = item.getString(1);
                        result.put(name, isoCode);
                    }
                }
                if ((plurals = bundle.findTopLevel("CurrencyPlurals")) == null) continue;
                for (int i3 = 0; i3 < plurals.getSize(); ++i3) {
                    ICUResourceBundle item = plurals.at(i3);
                    String isoCode = item.getKey();
                    HashSet<String> pluralSet = (HashSet<String>)visitedPlurals.get(isoCode);
                    if (pluralSet == null) {
                        pluralSet = new HashSet<String>();
                        visitedPlurals.put(isoCode, pluralSet);
                    }
                    for (int j2 = 0; j2 < item.getSize(); ++j2) {
                        ICUResourceBundle plural = item.at(j2);
                        String pluralType = plural.getKey();
                        if (pluralSet.contains(pluralType)) continue;
                        String pluralName = plural.getString();
                        result.put(pluralName, isoCode);
                        pluralSet.add(pluralType);
                    }
                }
            }
            return Collections.unmodifiableMap(result);
        }
    }
}

