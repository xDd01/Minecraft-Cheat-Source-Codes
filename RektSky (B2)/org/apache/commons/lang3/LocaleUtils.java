package org.apache.commons.lang3;

import java.util.concurrent.*;
import java.util.*;

public class LocaleUtils
{
    private static final ConcurrentMap<String, List<Locale>> cLanguagesByCountry;
    private static final ConcurrentMap<String, List<Locale>> cCountriesByLanguage;
    
    public static Locale toLocale(final String str) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) {
            return new Locale("", "");
        }
        if (str.contains("#")) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        final int len = str.length();
        if (len < 2) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        final char ch0 = str.charAt(0);
        if (ch0 != '_') {
            return parseLocale(str);
        }
        if (len < 3) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        final char ch2 = str.charAt(1);
        final char ch3 = str.charAt(2);
        if (!Character.isUpperCase(ch2) || !Character.isUpperCase(ch3)) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        if (len == 3) {
            return new Locale("", str.substring(1, 3));
        }
        if (len < 5) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        if (str.charAt(3) != '_') {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        return new Locale("", str.substring(1, 3), str.substring(4));
    }
    
    private static Locale parseLocale(final String str) {
        if (isISO639LanguageCode(str)) {
            return new Locale(str);
        }
        final String[] segments = str.split("_", -1);
        final String language = segments[0];
        if (segments.length == 2) {
            final String country = segments[1];
            if ((isISO639LanguageCode(language) && isISO3166CountryCode(country)) || isNumericAreaCode(country)) {
                return new Locale(language, country);
            }
        }
        else if (segments.length == 3) {
            final String country = segments[1];
            final String variant = segments[2];
            if (isISO639LanguageCode(language) && (country.isEmpty() || isISO3166CountryCode(country) || isNumericAreaCode(country)) && !variant.isEmpty()) {
                return new Locale(language, country, variant);
            }
        }
        throw new IllegalArgumentException("Invalid locale format: " + str);
    }
    
    private static boolean isISO639LanguageCode(final String str) {
        return StringUtils.isAllLowerCase(str) && (str.length() == 2 || str.length() == 3);
    }
    
    private static boolean isISO3166CountryCode(final String str) {
        return StringUtils.isAllUpperCase(str) && str.length() == 2;
    }
    
    private static boolean isNumericAreaCode(final String str) {
        return StringUtils.isNumeric(str) && str.length() == 3;
    }
    
    public static List<Locale> localeLookupList(final Locale locale) {
        return localeLookupList(locale, locale);
    }
    
    public static List<Locale> localeLookupList(final Locale locale, final Locale defaultLocale) {
        final List<Locale> list = new ArrayList<Locale>(4);
        if (locale != null) {
            list.add(locale);
            if (!locale.getVariant().isEmpty()) {
                list.add(new Locale(locale.getLanguage(), locale.getCountry()));
            }
            if (!locale.getCountry().isEmpty()) {
                list.add(new Locale(locale.getLanguage(), ""));
            }
            if (!list.contains(defaultLocale)) {
                list.add(defaultLocale);
            }
        }
        return Collections.unmodifiableList((List<? extends Locale>)list);
    }
    
    public static List<Locale> availableLocaleList() {
        return SyncAvoid.AVAILABLE_LOCALE_LIST;
    }
    
    public static Set<Locale> availableLocaleSet() {
        return SyncAvoid.AVAILABLE_LOCALE_SET;
    }
    
    public static boolean isAvailableLocale(final Locale locale) {
        return availableLocaleList().contains(locale);
    }
    
    public static List<Locale> languagesByCountry(final String countryCode) {
        if (countryCode == null) {
            return Collections.emptyList();
        }
        List<Locale> langs = LocaleUtils.cLanguagesByCountry.get(countryCode);
        if (langs == null) {
            langs = new ArrayList<Locale>();
            final List<Locale> locales = availableLocaleList();
            for (final Locale locale : locales) {
                if (countryCode.equals(locale.getCountry()) && locale.getVariant().isEmpty()) {
                    langs.add(locale);
                }
            }
            langs = Collections.unmodifiableList((List<? extends Locale>)langs);
            LocaleUtils.cLanguagesByCountry.putIfAbsent(countryCode, langs);
            langs = LocaleUtils.cLanguagesByCountry.get(countryCode);
        }
        return langs;
    }
    
    public static List<Locale> countriesByLanguage(final String languageCode) {
        if (languageCode == null) {
            return Collections.emptyList();
        }
        List<Locale> countries = LocaleUtils.cCountriesByLanguage.get(languageCode);
        if (countries == null) {
            countries = new ArrayList<Locale>();
            final List<Locale> locales = availableLocaleList();
            for (final Locale locale : locales) {
                if (languageCode.equals(locale.getLanguage()) && !locale.getCountry().isEmpty() && locale.getVariant().isEmpty()) {
                    countries.add(locale);
                }
            }
            countries = Collections.unmodifiableList((List<? extends Locale>)countries);
            LocaleUtils.cCountriesByLanguage.putIfAbsent(languageCode, countries);
            countries = LocaleUtils.cCountriesByLanguage.get(languageCode);
        }
        return countries;
    }
    
    static {
        cLanguagesByCountry = new ConcurrentHashMap<String, List<Locale>>();
        cCountriesByLanguage = new ConcurrentHashMap<String, List<Locale>>();
    }
    
    static class SyncAvoid
    {
        private static final List<Locale> AVAILABLE_LOCALE_LIST;
        private static final Set<Locale> AVAILABLE_LOCALE_SET;
        
        static {
            final List<Locale> list = new ArrayList<Locale>(Arrays.asList(Locale.getAvailableLocales()));
            AVAILABLE_LOCALE_LIST = Collections.unmodifiableList((List<? extends Locale>)list);
            AVAILABLE_LOCALE_SET = Collections.unmodifiableSet((Set<? extends Locale>)new HashSet<Locale>(list));
        }
    }
}
