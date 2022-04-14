/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.codec.language.bm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.commons.codec.language.bm.NameType;
import org.apache.commons.codec.language.bm.RuleType;

public class Rule {
    public static final RPattern ALL_STRINGS_RMATCHER = new RPattern(){

        @Override
        public boolean isMatch(CharSequence input) {
            return true;
        }
    };
    public static final String ALL = "ALL";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String HASH_INCLUDE = "#include";
    private static final Map<NameType, Map<RuleType, Map<String, Map<String, List<Rule>>>>> RULES = new EnumMap<NameType, Map<RuleType, Map<String, Map<String, List<Rule>>>>>(NameType.class);
    private final RPattern lContext;
    private final String pattern;
    private final PhonemeExpr phoneme;
    private final RPattern rContext;

    private static boolean contains(CharSequence chars, char input) {
        for (int i2 = 0; i2 < chars.length(); ++i2) {
            if (chars.charAt(i2) != input) continue;
            return true;
        }
        return false;
    }

    private static String createResourceName(NameType nameType, RuleType rt2, String lang) {
        return String.format("org/apache/commons/codec/language/bm/%s_%s_%s.txt", nameType.getName(), rt2.getName(), lang);
    }

    private static Scanner createScanner(NameType nameType, RuleType rt2, String lang) {
        String resName = Rule.createResourceName(nameType, rt2, lang);
        InputStream rulesIS = Languages.class.getClassLoader().getResourceAsStream(resName);
        if (rulesIS == null) {
            throw new IllegalArgumentException("Unable to load resource: " + resName);
        }
        return new Scanner(rulesIS, "UTF-8");
    }

    private static Scanner createScanner(String lang) {
        String resName = String.format("org/apache/commons/codec/language/bm/%s.txt", lang);
        InputStream rulesIS = Languages.class.getClassLoader().getResourceAsStream(resName);
        if (rulesIS == null) {
            throw new IllegalArgumentException("Unable to load resource: " + resName);
        }
        return new Scanner(rulesIS, "UTF-8");
    }

    private static boolean endsWith(CharSequence input, CharSequence suffix) {
        if (suffix.length() > input.length()) {
            return false;
        }
        int i2 = input.length() - 1;
        for (int j2 = suffix.length() - 1; j2 >= 0; --j2) {
            if (input.charAt(i2) != suffix.charAt(j2)) {
                return false;
            }
            --i2;
        }
        return true;
    }

    public static List<Rule> getInstance(NameType nameType, RuleType rt2, Languages.LanguageSet langs) {
        Map<String, List<Rule>> ruleMap = Rule.getInstanceMap(nameType, rt2, langs);
        ArrayList<Rule> allRules = new ArrayList<Rule>();
        for (List<Rule> rules : ruleMap.values()) {
            allRules.addAll(rules);
        }
        return allRules;
    }

    public static List<Rule> getInstance(NameType nameType, RuleType rt2, String lang) {
        return Rule.getInstance(nameType, rt2, Languages.LanguageSet.from(new HashSet<String>(Arrays.asList(lang))));
    }

    public static Map<String, List<Rule>> getInstanceMap(NameType nameType, RuleType rt2, Languages.LanguageSet langs) {
        return langs.isSingleton() ? Rule.getInstanceMap(nameType, rt2, langs.getAny()) : Rule.getInstanceMap(nameType, rt2, "any");
    }

    public static Map<String, List<Rule>> getInstanceMap(NameType nameType, RuleType rt2, String lang) {
        Map<String, List<Rule>> rules = RULES.get((Object)nameType).get((Object)rt2).get(lang);
        if (rules == null) {
            throw new IllegalArgumentException(String.format("No rules found for %s, %s, %s.", nameType.getName(), rt2.getName(), lang));
        }
        return rules;
    }

    private static Phoneme parsePhoneme(String ph2) {
        int open = ph2.indexOf("[");
        if (open >= 0) {
            if (!ph2.endsWith("]")) {
                throw new IllegalArgumentException("Phoneme expression contains a '[' but does not end in ']'");
            }
            String before = ph2.substring(0, open);
            String in2 = ph2.substring(open + 1, ph2.length() - 1);
            HashSet<String> langs = new HashSet<String>(Arrays.asList(in2.split("[+]")));
            return new Phoneme(before, Languages.LanguageSet.from(langs));
        }
        return new Phoneme(ph2, Languages.ANY_LANGUAGE);
    }

    private static PhonemeExpr parsePhonemeExpr(String ph2) {
        if (ph2.startsWith("(")) {
            if (!ph2.endsWith(")")) {
                throw new IllegalArgumentException("Phoneme starts with '(' so must end with ')'");
            }
            ArrayList<Phoneme> phs = new ArrayList<Phoneme>();
            String body = ph2.substring(1, ph2.length() - 1);
            for (String part : body.split("[|]")) {
                phs.add(Rule.parsePhoneme(part));
            }
            if (body.startsWith("|") || body.endsWith("|")) {
                phs.add(new Phoneme("", Languages.ANY_LANGUAGE));
            }
            return new PhonemeList(phs);
        }
        return Rule.parsePhoneme(ph2);
    }

    private static Map<String, List<Rule>> parseRules(Scanner scanner, final String location) {
        HashMap<String, List<Rule>> lines = new HashMap<String, List<Rule>>();
        int currentLine = 0;
        boolean inMultilineComment = false;
        while (scanner.hasNextLine()) {
            String rawLine;
            ++currentLine;
            String line = rawLine = scanner.nextLine();
            if (inMultilineComment) {
                if (!line.endsWith("*/")) continue;
                inMultilineComment = false;
                continue;
            }
            if (line.startsWith("/*")) {
                inMultilineComment = true;
                continue;
            }
            int cmtI = line.indexOf("//");
            if (cmtI >= 0) {
                line = line.substring(0, cmtI);
            }
            if ((line = line.trim()).length() == 0) continue;
            if (line.startsWith(HASH_INCLUDE)) {
                String incl = line.substring(HASH_INCLUDE.length()).trim();
                if (incl.contains(" ")) {
                    throw new IllegalArgumentException("Malformed import statement '" + rawLine + "' in " + location);
                }
                lines.putAll(Rule.parseRules(Rule.createScanner(incl), location + "->" + incl));
                continue;
            }
            String[] parts = line.split("\\s+");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Malformed rule statement split into " + parts.length + " parts: " + rawLine + " in " + location);
            }
            try {
                String pat = Rule.stripQuotes(parts[0]);
                String lCon = Rule.stripQuotes(parts[1]);
                String rCon = Rule.stripQuotes(parts[2]);
                PhonemeExpr ph2 = Rule.parsePhonemeExpr(Rule.stripQuotes(parts[3]));
                final int cLine = currentLine;
                Rule r2 = new Rule(pat, lCon, rCon, ph2){
                    private final int myLine;
                    private final String loc;
                    {
                        super(x0, x1, x2, x3);
                        this.myLine = cLine;
                        this.loc = location;
                    }

                    public String toString() {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("Rule");
                        sb2.append("{line=").append(this.myLine);
                        sb2.append(", loc='").append(this.loc).append('\'');
                        sb2.append('}');
                        return sb2.toString();
                    }
                };
                String patternKey = r2.pattern.substring(0, 1);
                ArrayList<2> rules = (ArrayList<2>)lines.get(patternKey);
                if (rules == null) {
                    rules = new ArrayList<2>();
                    lines.put(patternKey, rules);
                }
                rules.add(r2);
            }
            catch (IllegalArgumentException e2) {
                throw new IllegalStateException("Problem parsing line '" + currentLine + "' in " + location, e2);
            }
        }
        return lines;
    }

    private static RPattern pattern(final String regex) {
        boolean endsWith;
        boolean startsWith = regex.startsWith("^");
        final String content = regex.substring(startsWith ? 1 : 0, (endsWith = regex.endsWith("$")) ? regex.length() - 1 : regex.length());
        boolean boxes = content.contains("[");
        if (!boxes) {
            if (startsWith && endsWith) {
                if (content.length() == 0) {
                    return new RPattern(){

                        @Override
                        public boolean isMatch(CharSequence input) {
                            return input.length() == 0;
                        }
                    };
                }
                return new RPattern(){

                    @Override
                    public boolean isMatch(CharSequence input) {
                        return input.equals(content);
                    }
                };
            }
            if ((startsWith || endsWith) && content.length() == 0) {
                return ALL_STRINGS_RMATCHER;
            }
            if (startsWith) {
                return new RPattern(){

                    @Override
                    public boolean isMatch(CharSequence input) {
                        return Rule.startsWith(input, content);
                    }
                };
            }
            if (endsWith) {
                return new RPattern(){

                    @Override
                    public boolean isMatch(CharSequence input) {
                        return Rule.endsWith(input, content);
                    }
                };
            }
        } else {
            String boxContent;
            boolean startsWithBox = content.startsWith("[");
            boolean endsWithBox = content.endsWith("]");
            if (startsWithBox && endsWithBox && !(boxContent = content.substring(1, content.length() - 1)).contains("[")) {
                boolean shouldMatch;
                boolean negate = boxContent.startsWith("^");
                if (negate) {
                    boxContent = boxContent.substring(1);
                }
                final String bContent = boxContent;
                boolean bl2 = shouldMatch = !negate;
                if (startsWith && endsWith) {
                    return new RPattern(){

                        @Override
                        public boolean isMatch(CharSequence input) {
                            return input.length() == 1 && Rule.contains(bContent, input.charAt(0)) == shouldMatch;
                        }
                    };
                }
                if (startsWith) {
                    return new RPattern(){

                        @Override
                        public boolean isMatch(CharSequence input) {
                            return input.length() > 0 && Rule.contains(bContent, input.charAt(0)) == shouldMatch;
                        }
                    };
                }
                if (endsWith) {
                    return new RPattern(){

                        @Override
                        public boolean isMatch(CharSequence input) {
                            return input.length() > 0 && Rule.contains(bContent, input.charAt(input.length() - 1)) == shouldMatch;
                        }
                    };
                }
            }
        }
        return new RPattern(){
            Pattern pattern;
            {
                this.pattern = Pattern.compile(regex);
            }

            @Override
            public boolean isMatch(CharSequence input) {
                Matcher matcher = this.pattern.matcher(input);
                return matcher.find();
            }
        };
    }

    private static boolean startsWith(CharSequence input, CharSequence prefix) {
        if (prefix.length() > input.length()) {
            return false;
        }
        for (int i2 = 0; i2 < prefix.length(); ++i2) {
            if (input.charAt(i2) == prefix.charAt(i2)) continue;
            return false;
        }
        return true;
    }

    private static String stripQuotes(String str) {
        if (str.startsWith(DOUBLE_QUOTE)) {
            str = str.substring(1);
        }
        if (str.endsWith(DOUBLE_QUOTE)) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }

    public Rule(String pattern, String lContext, String rContext, PhonemeExpr phoneme) {
        this.pattern = pattern;
        this.lContext = Rule.pattern(lContext + "$");
        this.rContext = Rule.pattern("^" + rContext);
        this.phoneme = phoneme;
    }

    public RPattern getLContext() {
        return this.lContext;
    }

    public String getPattern() {
        return this.pattern;
    }

    public PhonemeExpr getPhoneme() {
        return this.phoneme;
    }

    public RPattern getRContext() {
        return this.rContext;
    }

    public boolean patternAndContextMatches(CharSequence input, int i2) {
        if (i2 < 0) {
            throw new IndexOutOfBoundsException("Can not match pattern at negative indexes");
        }
        int patternLength = this.pattern.length();
        int ipl = i2 + patternLength;
        if (ipl > input.length()) {
            return false;
        }
        if (!input.subSequence(i2, ipl).equals(this.pattern)) {
            return false;
        }
        if (!this.rContext.isMatch(input.subSequence(ipl, input.length()))) {
            return false;
        }
        return this.lContext.isMatch(input.subSequence(0, i2));
    }

    static {
        for (NameType s2 : NameType.values()) {
            EnumMap rts = new EnumMap(RuleType.class);
            for (RuleType rt2 : RuleType.values()) {
                HashMap<String, Map<String, List<Rule>>> rs2 = new HashMap<String, Map<String, List<Rule>>>();
                Languages ls2 = Languages.getInstance(s2);
                for (String l2 : ls2.getLanguages()) {
                    try {
                        rs2.put(l2, Rule.parseRules(Rule.createScanner(s2, rt2, l2), Rule.createResourceName(s2, rt2, l2)));
                    }
                    catch (IllegalStateException e2) {
                        throw new IllegalStateException("Problem processing " + Rule.createResourceName(s2, rt2, l2), e2);
                    }
                }
                if (!rt2.equals((Object)RuleType.RULES)) {
                    rs2.put("common", Rule.parseRules(Rule.createScanner(s2, rt2, "common"), Rule.createResourceName(s2, rt2, "common")));
                }
                rts.put(rt2, Collections.unmodifiableMap(rs2));
            }
            RULES.put(s2, Collections.unmodifiableMap(rts));
        }
    }

    public static interface RPattern {
        public boolean isMatch(CharSequence var1);
    }

    public static final class PhonemeList
    implements PhonemeExpr {
        private final List<Phoneme> phonemes;

        public PhonemeList(List<Phoneme> phonemes) {
            this.phonemes = phonemes;
        }

        public List<Phoneme> getPhonemes() {
            return this.phonemes;
        }
    }

    public static interface PhonemeExpr {
        public Iterable<Phoneme> getPhonemes();
    }

    public static final class Phoneme
    implements PhonemeExpr {
        public static final Comparator<Phoneme> COMPARATOR = new Comparator<Phoneme>(){

            @Override
            public int compare(Phoneme o1, Phoneme o2) {
                for (int i2 = 0; i2 < o1.phonemeText.length(); ++i2) {
                    if (i2 >= o2.phonemeText.length()) {
                        return 1;
                    }
                    int c2 = o1.phonemeText.charAt(i2) - o2.phonemeText.charAt(i2);
                    if (c2 == 0) continue;
                    return c2;
                }
                if (o1.phonemeText.length() < o2.phonemeText.length()) {
                    return -1;
                }
                return 0;
            }
        };
        private final StringBuilder phonemeText;
        private final Languages.LanguageSet languages;

        public Phoneme(CharSequence phonemeText, Languages.LanguageSet languages) {
            this.phonemeText = new StringBuilder(phonemeText);
            this.languages = languages;
        }

        public Phoneme(Phoneme phonemeLeft, Phoneme phonemeRight) {
            this(phonemeLeft.phonemeText, phonemeLeft.languages);
            this.phonemeText.append((CharSequence)phonemeRight.phonemeText);
        }

        public Phoneme(Phoneme phonemeLeft, Phoneme phonemeRight, Languages.LanguageSet languages) {
            this(phonemeLeft.phonemeText, languages);
            this.phonemeText.append((CharSequence)phonemeRight.phonemeText);
        }

        public Phoneme append(CharSequence str) {
            this.phonemeText.append(str);
            return this;
        }

        public Languages.LanguageSet getLanguages() {
            return this.languages;
        }

        @Override
        public Iterable<Phoneme> getPhonemes() {
            return Collections.singleton(this);
        }

        public CharSequence getPhonemeText() {
            return this.phonemeText;
        }

        @Deprecated
        public Phoneme join(Phoneme right) {
            return new Phoneme(this.phonemeText.toString() + right.phonemeText.toString(), this.languages.restrictTo(right.languages));
        }
    }
}

