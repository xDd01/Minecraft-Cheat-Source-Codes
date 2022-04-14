/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.ibm.icu.text.NFRuleSet;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.RBNFPostProcessor;
import com.ibm.icu.text.RbnfLenientScanner;
import com.ibm.icu.text.RbnfLenientScannerProvider;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.UResourceBundleIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

public class RuleBasedNumberFormat
extends NumberFormat {
    static final long serialVersionUID = -7664252765575395068L;
    public static final int SPELLOUT = 1;
    public static final int ORDINAL = 2;
    public static final int DURATION = 3;
    public static final int NUMBERING_SYSTEM = 4;
    private transient NFRuleSet[] ruleSets = null;
    private transient String[] ruleSetDescriptions = null;
    private transient NFRuleSet defaultRuleSet = null;
    private ULocale locale = null;
    private transient RbnfLenientScannerProvider scannerProvider = null;
    private transient boolean lookedForScanner;
    private transient DecimalFormatSymbols decimalFormatSymbols = null;
    private transient DecimalFormat decimalFormat = null;
    private boolean lenientParse = false;
    private transient String lenientParseRules;
    private transient String postProcessRules;
    private transient RBNFPostProcessor postProcessor;
    private Map<String, String[]> ruleSetDisplayNames;
    private String[] publicRuleSetNames;
    private static final boolean DEBUG = ICUDebug.enabled("rbnf");
    private static final String[] rulenames = new String[]{"SpelloutRules", "OrdinalRules", "DurationRules", "NumberingSystemRules"};
    private static final String[] locnames = new String[]{"SpelloutLocalizations", "OrdinalLocalizations", "DurationLocalizations", "NumberingSystemLocalizations"};

    public RuleBasedNumberFormat(String description) {
        this.locale = ULocale.getDefault(ULocale.Category.FORMAT);
        this.init(description, null);
    }

    public RuleBasedNumberFormat(String description, String[][] localizations) {
        this.locale = ULocale.getDefault(ULocale.Category.FORMAT);
        this.init(description, localizations);
    }

    public RuleBasedNumberFormat(String description, Locale locale) {
        this(description, ULocale.forLocale(locale));
    }

    public RuleBasedNumberFormat(String description, ULocale locale) {
        this.locale = locale;
        this.init(description, null);
    }

    public RuleBasedNumberFormat(String description, String[][] localizations, ULocale locale) {
        this.locale = locale;
        this.init(description, localizations);
    }

    public RuleBasedNumberFormat(Locale locale, int format) {
        this(ULocale.forLocale(locale), format);
    }

    public RuleBasedNumberFormat(ULocale locale, int format) {
        this.locale = locale;
        ICUResourceBundle bundle = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/rbnf", locale);
        ULocale uloc = bundle.getULocale();
        this.setLocale(uloc, uloc);
        String description = "";
        String[][] localizations = null;
        try {
            description = bundle.getString(rulenames[format - 1]);
        }
        catch (MissingResourceException e2) {
            try {
                ICUResourceBundle rules = bundle.getWithFallback("RBNFRules/" + rulenames[format - 1]);
                UResourceBundleIterator it2 = rules.getIterator();
                while (it2.hasNext()) {
                    description = description.concat(it2.nextString());
                }
            }
            catch (MissingResourceException e1) {
                // empty catch block
            }
        }
        try {
            UResourceBundle locb = bundle.get(locnames[format - 1]);
            localizations = new String[locb.getSize()][];
            for (int i2 = 0; i2 < localizations.length; ++i2) {
                localizations[i2] = locb.get(i2).getStringArray();
            }
        }
        catch (MissingResourceException e3) {
            // empty catch block
        }
        this.init(description, localizations);
    }

    public RuleBasedNumberFormat(int format) {
        this(ULocale.getDefault(ULocale.Category.FORMAT), format);
    }

    public Object clone() {
        return super.clone();
    }

    public boolean equals(Object that) {
        if (!(that instanceof RuleBasedNumberFormat)) {
            return false;
        }
        RuleBasedNumberFormat that2 = (RuleBasedNumberFormat)that;
        if (!this.locale.equals(that2.locale) || this.lenientParse != that2.lenientParse) {
            return false;
        }
        if (this.ruleSets.length != that2.ruleSets.length) {
            return false;
        }
        for (int i2 = 0; i2 < this.ruleSets.length; ++i2) {
            if (this.ruleSets[i2].equals(that2.ruleSets[i2])) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (int i2 = 0; i2 < this.ruleSets.length; ++i2) {
            result.append(this.ruleSets[i2].toString());
        }
        return result.toString();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.writeUTF(this.toString());
        out.writeObject(this.locale);
    }

    private void readObject(ObjectInputStream in2) throws IOException {
        ULocale loc;
        String description = in2.readUTF();
        try {
            loc = (ULocale)in2.readObject();
        }
        catch (Exception e2) {
            loc = ULocale.getDefault(ULocale.Category.FORMAT);
        }
        RuleBasedNumberFormat temp = new RuleBasedNumberFormat(description, loc);
        this.ruleSets = temp.ruleSets;
        this.defaultRuleSet = temp.defaultRuleSet;
        this.publicRuleSetNames = temp.publicRuleSetNames;
        this.decimalFormatSymbols = temp.decimalFormatSymbols;
        this.decimalFormat = temp.decimalFormat;
        this.locale = temp.locale;
    }

    public String[] getRuleSetNames() {
        return (String[])this.publicRuleSetNames.clone();
    }

    public ULocale[] getRuleSetDisplayNameLocales() {
        if (this.ruleSetDisplayNames != null) {
            Set<String> s2 = this.ruleSetDisplayNames.keySet();
            String[] locales = s2.toArray(new String[s2.size()]);
            Arrays.sort(locales, String.CASE_INSENSITIVE_ORDER);
            ULocale[] result = new ULocale[locales.length];
            for (int i2 = 0; i2 < locales.length; ++i2) {
                result[i2] = new ULocale(locales[i2]);
            }
            return result;
        }
        return null;
    }

    private String[] getNameListForLocale(ULocale loc) {
        if (loc != null && this.ruleSetDisplayNames != null) {
            String[] localeNames = new String[]{loc.getBaseName(), ULocale.getDefault(ULocale.Category.DISPLAY).getBaseName()};
            for (int i2 = 0; i2 < localeNames.length; ++i2) {
                String lname = localeNames[i2];
                while (lname.length() > 0) {
                    String[] names = this.ruleSetDisplayNames.get(lname);
                    if (names != null) {
                        return names;
                    }
                    lname = ULocale.getFallback(lname);
                }
            }
        }
        return null;
    }

    public String[] getRuleSetDisplayNames(ULocale loc) {
        String[] names = this.getNameListForLocale(loc);
        if (names != null) {
            return (String[])names.clone();
        }
        names = this.getRuleSetNames();
        for (int i2 = 0; i2 < names.length; ++i2) {
            names[i2] = names[i2].substring(1);
        }
        return names;
    }

    public String[] getRuleSetDisplayNames() {
        return this.getRuleSetDisplayNames(ULocale.getDefault(ULocale.Category.DISPLAY));
    }

    public String getRuleSetDisplayName(String ruleSetName, ULocale loc) {
        String[] rsnames = this.publicRuleSetNames;
        for (int ix2 = 0; ix2 < rsnames.length; ++ix2) {
            if (!rsnames[ix2].equals(ruleSetName)) continue;
            String[] names = this.getNameListForLocale(loc);
            if (names != null) {
                return names[ix2];
            }
            return rsnames[ix2].substring(1);
        }
        throw new IllegalArgumentException("unrecognized rule set name: " + ruleSetName);
    }

    public String getRuleSetDisplayName(String ruleSetName) {
        return this.getRuleSetDisplayName(ruleSetName, ULocale.getDefault(ULocale.Category.DISPLAY));
    }

    public String format(double number, String ruleSet) throws IllegalArgumentException {
        if (ruleSet.startsWith("%%")) {
            throw new IllegalArgumentException("Can't use internal rule set");
        }
        return this.format(number, this.findRuleSet(ruleSet));
    }

    public String format(long number, String ruleSet) throws IllegalArgumentException {
        if (ruleSet.startsWith("%%")) {
            throw new IllegalArgumentException("Can't use internal rule set");
        }
        return this.format(number, this.findRuleSet(ruleSet));
    }

    public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition ignore) {
        toAppendTo.append(this.format(number, this.defaultRuleSet));
        return toAppendTo;
    }

    public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition ignore) {
        toAppendTo.append(this.format(number, this.defaultRuleSet));
        return toAppendTo;
    }

    public StringBuffer format(BigInteger number, StringBuffer toAppendTo, FieldPosition pos) {
        return this.format(new com.ibm.icu.math.BigDecimal(number), toAppendTo, pos);
    }

    public StringBuffer format(BigDecimal number, StringBuffer toAppendTo, FieldPosition pos) {
        return this.format(new com.ibm.icu.math.BigDecimal(number), toAppendTo, pos);
    }

    public StringBuffer format(com.ibm.icu.math.BigDecimal number, StringBuffer toAppendTo, FieldPosition pos) {
        return this.format(number.doubleValue(), toAppendTo, pos);
    }

    public Number parse(String text, ParsePosition parsePosition) {
        String workingText = text.substring(parsePosition.getIndex());
        ParsePosition workingPos = new ParsePosition(0);
        Number tempResult = null;
        Number result = 0L;
        ParsePosition highWaterMark = new ParsePosition(workingPos.getIndex());
        for (int i2 = this.ruleSets.length - 1; i2 >= 0; --i2) {
            if (!this.ruleSets[i2].isPublic() || !this.ruleSets[i2].isParseable()) continue;
            tempResult = this.ruleSets[i2].parse(workingText, workingPos, Double.MAX_VALUE);
            if (workingPos.getIndex() > highWaterMark.getIndex()) {
                result = tempResult;
                highWaterMark.setIndex(workingPos.getIndex());
            }
            if (highWaterMark.getIndex() == workingText.length()) break;
            workingPos.setIndex(0);
        }
        parsePosition.setIndex(parsePosition.getIndex() + highWaterMark.getIndex());
        return result;
    }

    public void setLenientParseMode(boolean enabled) {
        this.lenientParse = enabled;
    }

    public boolean lenientParseEnabled() {
        return this.lenientParse;
    }

    public void setLenientScannerProvider(RbnfLenientScannerProvider scannerProvider) {
        this.scannerProvider = scannerProvider;
    }

    public RbnfLenientScannerProvider getLenientScannerProvider() {
        if (this.scannerProvider == null && this.lenientParse && !this.lookedForScanner) {
            try {
                this.lookedForScanner = true;
                Class<?> cls = Class.forName("com.ibm.icu.text.RbnfScannerProviderImpl");
                RbnfLenientScannerProvider provider = (RbnfLenientScannerProvider)cls.newInstance();
                this.setLenientScannerProvider(provider);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return this.scannerProvider;
    }

    public void setDefaultRuleSet(String ruleSetName) {
        if (ruleSetName == null) {
            if (this.publicRuleSetNames.length > 0) {
                this.defaultRuleSet = this.findRuleSet(this.publicRuleSetNames[0]);
            } else {
                this.defaultRuleSet = null;
                int n2 = this.ruleSets.length;
                while (--n2 >= 0) {
                    String currentName = this.ruleSets[n2].getName();
                    if (!currentName.equals("%spellout-numbering") && !currentName.equals("%digits-ordinal") && !currentName.equals("%duration")) continue;
                    this.defaultRuleSet = this.ruleSets[n2];
                    return;
                }
                n2 = this.ruleSets.length;
                while (--n2 >= 0) {
                    if (!this.ruleSets[n2].isPublic()) continue;
                    this.defaultRuleSet = this.ruleSets[n2];
                    break;
                }
            }
        } else {
            if (ruleSetName.startsWith("%%")) {
                throw new IllegalArgumentException("cannot use private rule set: " + ruleSetName);
            }
            this.defaultRuleSet = this.findRuleSet(ruleSetName);
        }
    }

    public String getDefaultRuleSetName() {
        if (this.defaultRuleSet != null && this.defaultRuleSet.isPublic()) {
            return this.defaultRuleSet.getName();
        }
        return "";
    }

    public void setDecimalFormatSymbols(DecimalFormatSymbols newSymbols) {
        if (newSymbols != null) {
            this.decimalFormatSymbols = (DecimalFormatSymbols)newSymbols.clone();
            if (this.decimalFormat != null) {
                this.decimalFormat.setDecimalFormatSymbols(this.decimalFormatSymbols);
            }
            for (int i2 = 0; i2 < this.ruleSets.length; ++i2) {
                this.ruleSets[i2].parseRules(this.ruleSetDescriptions[i2], this);
            }
        }
    }

    NFRuleSet getDefaultRuleSet() {
        return this.defaultRuleSet;
    }

    RbnfLenientScanner getLenientScanner() {
        RbnfLenientScannerProvider provider;
        if (this.lenientParse && (provider = this.getLenientScannerProvider()) != null) {
            return provider.get(this.locale, this.lenientParseRules);
        }
        return null;
    }

    DecimalFormatSymbols getDecimalFormatSymbols() {
        if (this.decimalFormatSymbols == null) {
            this.decimalFormatSymbols = new DecimalFormatSymbols(this.locale);
        }
        return this.decimalFormatSymbols;
    }

    DecimalFormat getDecimalFormat() {
        if (this.decimalFormat == null) {
            this.decimalFormat = (DecimalFormat)NumberFormat.getInstance(this.locale);
            if (this.decimalFormatSymbols != null) {
                this.decimalFormat.setDecimalFormatSymbols(this.decimalFormatSymbols);
            }
        }
        return this.decimalFormat;
    }

    private String extractSpecial(StringBuilder description, String specialName) {
        String result = null;
        int lp2 = description.indexOf(specialName);
        if (lp2 != -1 && (lp2 == 0 || description.charAt(lp2 - 1) == ';')) {
            int lpStart;
            int lpEnd = description.indexOf(";%", lp2);
            if (lpEnd == -1) {
                lpEnd = description.length() - 1;
            }
            for (lpStart = lp2 + specialName.length(); lpStart < lpEnd && PatternProps.isWhiteSpace(description.charAt(lpStart)); ++lpStart) {
            }
            result = description.substring(lpStart, lpEnd);
            description.delete(lp2, lpEnd + 1);
        }
        return result;
    }

    private void init(String description, String[][] localizations) {
        int i2;
        this.initLocalizations(localizations);
        StringBuilder descBuf = this.stripWhitespace(description);
        this.lenientParseRules = this.extractSpecial(descBuf, "%%lenient-parse:");
        this.postProcessRules = this.extractSpecial(descBuf, "%%post-process:");
        int numRuleSets = 0;
        int p2 = descBuf.indexOf(";%");
        while (p2 != -1) {
            ++numRuleSets;
            ++p2;
            p2 = descBuf.indexOf(";%", p2);
        }
        this.ruleSets = new NFRuleSet[++numRuleSets];
        this.ruleSetDescriptions = new String[numRuleSets];
        int curRuleSet = 0;
        int start = 0;
        int p3 = descBuf.indexOf(";%");
        while (p3 != -1) {
            this.ruleSetDescriptions[curRuleSet] = descBuf.substring(start, p3 + 1);
            this.ruleSets[curRuleSet] = new NFRuleSet(this.ruleSetDescriptions, curRuleSet);
            ++curRuleSet;
            start = p3 + 1;
            p3 = descBuf.indexOf(";%", start);
        }
        this.ruleSetDescriptions[curRuleSet] = descBuf.substring(start);
        this.ruleSets[curRuleSet] = new NFRuleSet(this.ruleSetDescriptions, curRuleSet);
        boolean defaultNameFound = false;
        int n2 = this.ruleSets.length;
        this.defaultRuleSet = this.ruleSets[this.ruleSets.length - 1];
        while (--n2 >= 0) {
            String currentName = this.ruleSets[n2].getName();
            if (!currentName.equals("%spellout-numbering") && !currentName.equals("%digits-ordinal") && !currentName.equals("%duration")) continue;
            this.defaultRuleSet = this.ruleSets[n2];
            defaultNameFound = true;
            break;
        }
        if (!defaultNameFound) {
            for (int i3 = this.ruleSets.length - 1; i3 >= 0; --i3) {
                if (this.ruleSets[i3].getName().startsWith("%%")) continue;
                this.defaultRuleSet = this.ruleSets[i3];
                break;
            }
        }
        for (int i4 = 0; i4 < this.ruleSets.length; ++i4) {
            this.ruleSets[i4].parseRules(this.ruleSetDescriptions[i4], this);
        }
        int publicRuleSetCount = 0;
        for (int i5 = 0; i5 < this.ruleSets.length; ++i5) {
            if (this.ruleSets[i5].getName().startsWith("%%")) continue;
            ++publicRuleSetCount;
        }
        String[] publicRuleSetTemp = new String[publicRuleSetCount];
        publicRuleSetCount = 0;
        for (i2 = this.ruleSets.length - 1; i2 >= 0; --i2) {
            if (this.ruleSets[i2].getName().startsWith("%%")) continue;
            publicRuleSetTemp[publicRuleSetCount++] = this.ruleSets[i2].getName();
        }
        if (this.publicRuleSetNames != null) {
            block7: for (i2 = 0; i2 < this.publicRuleSetNames.length; ++i2) {
                String name = this.publicRuleSetNames[i2];
                for (int j2 = 0; j2 < publicRuleSetTemp.length; ++j2) {
                    if (name.equals(publicRuleSetTemp[j2])) continue block7;
                }
                throw new IllegalArgumentException("did not find public rule set: " + name);
            }
            this.defaultRuleSet = this.findRuleSet(this.publicRuleSetNames[0]);
        } else {
            this.publicRuleSetNames = publicRuleSetTemp;
        }
    }

    private void initLocalizations(String[][] localizations) {
        if (localizations != null) {
            this.publicRuleSetNames = (String[])localizations[0].clone();
            HashMap<String, String[]> m2 = new HashMap<String, String[]>();
            for (int i2 = 1; i2 < localizations.length; ++i2) {
                String[] data = localizations[i2];
                String loc = data[0];
                String[] names = new String[data.length - 1];
                if (names.length != this.publicRuleSetNames.length) {
                    throw new IllegalArgumentException("public name length: " + this.publicRuleSetNames.length + " != localized names[" + i2 + "] length: " + names.length);
                }
                System.arraycopy(data, 1, names, 0, names.length);
                m2.put(loc, names);
            }
            if (!m2.isEmpty()) {
                this.ruleSetDisplayNames = m2;
            }
        }
    }

    private StringBuilder stripWhitespace(String description) {
        StringBuilder result = new StringBuilder();
        int start = 0;
        while (start != -1 && start < description.length()) {
            while (start < description.length() && PatternProps.isWhiteSpace(description.charAt(start))) {
                ++start;
            }
            if (start < description.length() && description.charAt(start) == ';') {
                ++start;
                continue;
            }
            int p2 = description.indexOf(59, start);
            if (p2 == -1) {
                result.append(description.substring(start));
                start = -1;
                continue;
            }
            if (p2 < description.length()) {
                result.append(description.substring(start, p2 + 1));
                start = p2 + 1;
                continue;
            }
            start = -1;
        }
        return result;
    }

    private String format(double number, NFRuleSet ruleSet) {
        StringBuffer result = new StringBuffer();
        ruleSet.format(number, result, 0);
        this.postProcess(result, ruleSet);
        return result.toString();
    }

    private String format(long number, NFRuleSet ruleSet) {
        StringBuffer result = new StringBuffer();
        ruleSet.format(number, result, 0);
        this.postProcess(result, ruleSet);
        return result.toString();
    }

    private void postProcess(StringBuffer result, NFRuleSet ruleSet) {
        if (this.postProcessRules != null) {
            if (this.postProcessor == null) {
                int ix2 = this.postProcessRules.indexOf(";");
                if (ix2 == -1) {
                    ix2 = this.postProcessRules.length();
                }
                String ppClassName = this.postProcessRules.substring(0, ix2).trim();
                try {
                    Class<?> cls = Class.forName(ppClassName);
                    this.postProcessor = (RBNFPostProcessor)cls.newInstance();
                    this.postProcessor.init(this, this.postProcessRules);
                }
                catch (Exception e2) {
                    if (DEBUG) {
                        System.out.println("could not locate " + ppClassName + ", error " + e2.getClass().getName() + ", " + e2.getMessage());
                    }
                    this.postProcessor = null;
                    this.postProcessRules = null;
                    return;
                }
            }
            this.postProcessor.process(result, ruleSet);
        }
    }

    NFRuleSet findRuleSet(String name) throws IllegalArgumentException {
        for (int i2 = 0; i2 < this.ruleSets.length; ++i2) {
            if (!this.ruleSets[i2].getName().equals(name)) continue;
            return this.ruleSets[i2];
        }
        throw new IllegalArgumentException("No rule set named " + name);
    }
}

