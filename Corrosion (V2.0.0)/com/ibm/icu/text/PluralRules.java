/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.PluralRulesLoader;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.Output;
import com.ibm.icu.util.ULocale;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class PluralRules
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final RuleList rules;
    private final Set<String> keywords;
    private int repeatLimit;
    private transient int hashCode;
    private transient Map<String, List<Double>> _keySamplesMap;
    private transient Map<String, Boolean> _keyLimitedMap;
    public static final String KEYWORD_ZERO = "zero";
    public static final String KEYWORD_ONE = "one";
    public static final String KEYWORD_TWO = "two";
    public static final String KEYWORD_FEW = "few";
    public static final String KEYWORD_MANY = "many";
    public static final String KEYWORD_OTHER = "other";
    public static final double NO_UNIQUE_VALUE = -0.00123456777;
    private static final Constraint NO_CONSTRAINT = new Constraint(){
        private static final long serialVersionUID = 9163464945387899416L;

        public boolean isFulfilled(double n2) {
            return true;
        }

        public boolean isLimited() {
            return false;
        }

        public String toString() {
            return "n is any";
        }

        public int updateRepeatLimit(int limit) {
            return limit;
        }
    };
    private static final Rule DEFAULT_RULE = new Rule(){
        private static final long serialVersionUID = -5677499073940822149L;

        public String getKeyword() {
            return PluralRules.KEYWORD_OTHER;
        }

        public boolean appliesTo(double n2) {
            return true;
        }

        public boolean isLimited() {
            return false;
        }

        public String toString() {
            return "(other)";
        }

        public int updateRepeatLimit(int limit) {
            return limit;
        }
    };
    public static final PluralRules DEFAULT = new PluralRules(new RuleChain(DEFAULT_RULE));

    public static PluralRules parseDescription(String description) throws ParseException {
        if ((description = description.trim()).length() == 0) {
            return DEFAULT;
        }
        return new PluralRules(PluralRules.parseRuleChain(description));
    }

    public static PluralRules createRules(String description) {
        try {
            return PluralRules.parseDescription(description);
        }
        catch (ParseException e2) {
            return null;
        }
    }

    private static Constraint parseConstraint(String description) throws ParseException {
        description = description.trim().toLowerCase(Locale.ENGLISH);
        Constraint result = null;
        String[] or_together = Utility.splitString(description, "or");
        for (int i2 = 0; i2 < or_together.length; ++i2) {
            Constraint andConstraint = null;
            String[] and_together = Utility.splitString(or_together[i2], "and");
            for (int j2 = 0; j2 < and_together.length; ++j2) {
                String t2;
                Constraint newConstraint = NO_CONSTRAINT;
                String condition = and_together[j2].trim();
                String[] tokens = Utility.splitWhitespace(condition);
                int mod = 0;
                boolean inRange = true;
                boolean integersOnly = true;
                long lowBound = Long.MAX_VALUE;
                long highBound = Long.MIN_VALUE;
                long[] vals = null;
                boolean isRange = false;
                int x2 = 0;
                if (!"n".equals(t2 = tokens[x2++])) {
                    throw PluralRules.unexpected(t2, condition);
                }
                if (x2 < tokens.length) {
                    if ("mod".equals(t2 = tokens[x2++])) {
                        mod = Integer.parseInt(tokens[x2++]);
                        t2 = PluralRules.nextToken(tokens, x2++, condition);
                    }
                    if ("is".equals(t2)) {
                        if ("not".equals(t2 = PluralRules.nextToken(tokens, x2++, condition))) {
                            inRange = false;
                            t2 = PluralRules.nextToken(tokens, x2++, condition);
                        }
                    } else {
                        isRange = true;
                        if ("not".equals(t2)) {
                            inRange = false;
                            t2 = PluralRules.nextToken(tokens, x2++, condition);
                        }
                        if ("in".equals(t2)) {
                            t2 = PluralRules.nextToken(tokens, x2++, condition);
                        } else if ("within".equals(t2)) {
                            integersOnly = false;
                            t2 = PluralRules.nextToken(tokens, x2++, condition);
                        } else {
                            throw PluralRules.unexpected(t2, condition);
                        }
                    }
                    if (isRange) {
                        String[] range_list = Utility.splitString(t2, ",");
                        vals = new long[range_list.length * 2];
                        int k1 = 0;
                        int k2 = 0;
                        while (k1 < range_list.length) {
                            long high;
                            long low;
                            String range = range_list[k1];
                            String[] pair = Utility.splitString(range, "..");
                            if (pair.length == 2) {
                                low = Long.parseLong(pair[0]);
                                if (low > (high = Long.parseLong(pair[1]))) {
                                    throw PluralRules.unexpected(range, condition);
                                }
                            } else if (pair.length == 1) {
                                low = high = Long.parseLong(pair[0]);
                            } else {
                                throw PluralRules.unexpected(range, condition);
                            }
                            vals[k2] = low;
                            vals[k2 + 1] = high;
                            lowBound = Math.min(lowBound, low);
                            highBound = Math.max(highBound, high);
                            ++k1;
                            k2 += 2;
                        }
                        if (vals.length == 2) {
                            vals = null;
                        }
                    } else {
                        lowBound = highBound = Long.parseLong(t2);
                    }
                    if (x2 != tokens.length) {
                        throw PluralRules.unexpected(tokens[x2], condition);
                    }
                    newConstraint = new RangeConstraint(mod, inRange, integersOnly, lowBound, highBound, vals);
                }
                andConstraint = andConstraint == null ? newConstraint : new AndConstraint(andConstraint, newConstraint);
            }
            result = result == null ? andConstraint : new OrConstraint(result, andConstraint);
        }
        return result;
    }

    private static ParseException unexpected(String token, String context) {
        return new ParseException("unexpected token '" + token + "' in '" + context + "'", -1);
    }

    private static String nextToken(String[] tokens, int x2, String context) throws ParseException {
        if (x2 < tokens.length) {
            return tokens[x2];
        }
        throw new ParseException("missing token at end of '" + context + "'", -1);
    }

    private static Rule parseRule(String description) throws ParseException {
        int x2 = description.indexOf(58);
        if (x2 == -1) {
            throw new ParseException("missing ':' in rule description '" + description + "'", 0);
        }
        String keyword = description.substring(0, x2).trim();
        if (!PluralRules.isValidKeyword(keyword)) {
            throw new ParseException("keyword '" + keyword + " is not valid", 0);
        }
        if ((description = description.substring(x2 + 1).trim()).length() == 0) {
            throw new ParseException("missing constraint in '" + description + "'", x2 + 1);
        }
        Constraint constraint = PluralRules.parseConstraint(description);
        ConstrainedRule rule = new ConstrainedRule(keyword, constraint);
        return rule;
    }

    private static RuleChain parseRuleChain(String description) throws ParseException {
        RuleChain rc2 = null;
        String[] rules = Utility.split(description, ';');
        for (int i2 = 0; i2 < rules.length; ++i2) {
            Rule r2 = PluralRules.parseRule(rules[i2].trim());
            rc2 = rc2 == null ? new RuleChain(r2) : rc2.addRule(r2);
        }
        return rc2;
    }

    public static PluralRules forLocale(ULocale locale) {
        return PluralRulesLoader.loader.forLocale(locale, PluralType.CARDINAL);
    }

    public static PluralRules forLocale(ULocale locale, PluralType type) {
        return PluralRulesLoader.loader.forLocale(locale, type);
    }

    private static boolean isValidKeyword(String token) {
        return PatternProps.isIdentifier(token);
    }

    private PluralRules(RuleList rules) {
        this.rules = rules;
        this.keywords = Collections.unmodifiableSet(rules.getKeywords());
    }

    public String select(double number) {
        return this.rules.select(number);
    }

    public Set<String> getKeywords() {
        return this.keywords;
    }

    public double getUniqueKeywordValue(String keyword) {
        Collection<Double> values = this.getAllKeywordValues(keyword);
        if (values != null && values.size() == 1) {
            return values.iterator().next();
        }
        return -0.00123456777;
    }

    public Collection<Double> getAllKeywordValues(String keyword) {
        if (!this.keywords.contains(keyword)) {
            return Collections.emptyList();
        }
        Collection result = this.getKeySamplesMap().get(keyword);
        if (result.size() > 2 && !this.getKeyLimitedMap().get(keyword).booleanValue()) {
            return null;
        }
        return result;
    }

    public Collection<Double> getSamples(String keyword) {
        if (!this.keywords.contains(keyword)) {
            return null;
        }
        return this.getKeySamplesMap().get(keyword);
    }

    private Map<String, Boolean> getKeyLimitedMap() {
        this.initKeyMaps();
        return this._keyLimitedMap;
    }

    private Map<String, List<Double>> getKeySamplesMap() {
        this.initKeyMaps();
        return this._keySamplesMap;
    }

    private synchronized void initKeyMaps() {
        if (this._keySamplesMap == null) {
            int MAX_SAMPLES = 3;
            HashMap<String, Boolean> temp = new HashMap<String, Boolean>();
            for (String k2 : this.keywords) {
                temp.put(k2, this.rules.isLimited(k2));
            }
            this._keyLimitedMap = temp;
            HashMap<String, List<Double>> sampleMap = new HashMap<String, List<Double>>();
            int keywordsRemaining = this.keywords.size();
            int limit = Math.max(5, this.getRepeatLimit() * 3) * 2;
            for (int i2 = 0; keywordsRemaining > 0 && i2 < limit; ++i2) {
                double val = (double)i2 / 2.0;
                String keyword = this.select(val);
                boolean keyIsLimited = this._keyLimitedMap.get(keyword);
                ArrayList<Double> list = (ArrayList<Double>)sampleMap.get(keyword);
                if (list == null) {
                    list = new ArrayList<Double>(3);
                    sampleMap.put(keyword, list);
                } else if (!keyIsLimited && list.size() == 3) continue;
                list.add(val);
                if (keyIsLimited || list.size() != 3) continue;
                --keywordsRemaining;
            }
            if (keywordsRemaining > 0) {
                for (String k3 : this.keywords) {
                    if (sampleMap.containsKey(k3)) continue;
                    sampleMap.put(k3, Collections.emptyList());
                    if (--keywordsRemaining != 0) continue;
                    break;
                }
            }
            for (Map.Entry entry : sampleMap.entrySet()) {
                sampleMap.put((String)entry.getKey(), Collections.unmodifiableList((List)entry.getValue()));
            }
            this._keySamplesMap = sampleMap;
        }
    }

    public static ULocale[] getAvailableULocales() {
        return PluralRulesLoader.loader.getAvailableULocales();
    }

    public static ULocale getFunctionalEquivalent(ULocale locale, boolean[] isAvailable) {
        return PluralRulesLoader.loader.getFunctionalEquivalent(locale, isAvailable);
    }

    public String toString() {
        return "keywords: " + this.keywords + " limit: " + this.getRepeatLimit() + " rules: " + this.rules.toString();
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int newHashCode = this.keywords.hashCode();
            for (int i2 = 0; i2 < 12; ++i2) {
                newHashCode = newHashCode * 31 + this.select(i2).hashCode();
            }
            if (newHashCode == 0) {
                newHashCode = 1;
            }
            this.hashCode = newHashCode;
        }
        return this.hashCode;
    }

    public boolean equals(Object rhs) {
        return rhs instanceof PluralRules && this.equals((PluralRules)rhs);
    }

    public boolean equals(PluralRules rhs) {
        if (rhs == null) {
            return false;
        }
        if (rhs == this) {
            return true;
        }
        if (this.hashCode() != rhs.hashCode()) {
            return false;
        }
        if (!rhs.getKeywords().equals(this.keywords)) {
            return false;
        }
        int limit = Math.max(this.getRepeatLimit(), rhs.getRepeatLimit());
        for (int i2 = 0; i2 < limit * 2; ++i2) {
            if (this.select(i2).equals(rhs.select(i2))) continue;
            return false;
        }
        return true;
    }

    private int getRepeatLimit() {
        if (this.repeatLimit == 0) {
            this.repeatLimit = this.rules.getRepeatLimit() + 1;
        }
        return this.repeatLimit;
    }

    public KeywordStatus getKeywordStatus(String keyword, int offset, Set<Double> explicits, Output<Double> uniqueValue) {
        if (uniqueValue != null) {
            uniqueValue.value = null;
        }
        if (!this.rules.getKeywords().contains(keyword)) {
            return KeywordStatus.INVALID;
        }
        Collection<Double> values = this.getAllKeywordValues(keyword);
        if (values == null) {
            return KeywordStatus.UNBOUNDED;
        }
        int originalSize = values.size();
        if (explicits == null) {
            explicits = Collections.emptySet();
        }
        if (originalSize > explicits.size()) {
            if (originalSize == 1) {
                if (uniqueValue != null) {
                    uniqueValue.value = values.iterator().next();
                }
                return KeywordStatus.UNIQUE;
            }
            return KeywordStatus.BOUNDED;
        }
        HashSet<Double> subtractedSet = new HashSet<Double>(values);
        for (Double explicit : explicits) {
            subtractedSet.remove(explicit - (double)offset);
        }
        if (subtractedSet.size() == 0) {
            return KeywordStatus.SUPPRESSED;
        }
        if (uniqueValue != null && subtractedSet.size() == 1) {
            uniqueValue.value = subtractedSet.iterator().next();
        }
        return originalSize == 1 ? KeywordStatus.UNIQUE : KeywordStatus.BOUNDED;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum KeywordStatus {
        INVALID,
        SUPPRESSED,
        UNIQUE,
        BOUNDED,
        UNBOUNDED;

    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class RuleChain
    implements RuleList,
    Serializable {
        private static final long serialVersionUID = 1L;
        private final Rule rule;
        private final RuleChain next;

        public RuleChain(Rule rule) {
            this(rule, null);
        }

        private RuleChain(Rule rule, RuleChain next) {
            this.rule = rule;
            this.next = next;
        }

        public RuleChain addRule(Rule nextRule) {
            return new RuleChain(nextRule, this);
        }

        private Rule selectRule(double n2) {
            Rule r2 = null;
            if (this.next != null) {
                r2 = this.next.selectRule(n2);
            }
            if (r2 == null && this.rule.appliesTo(n2)) {
                r2 = this.rule;
            }
            return r2;
        }

        @Override
        public String select(double n2) {
            Rule r2 = this.selectRule(n2);
            if (r2 == null) {
                return PluralRules.KEYWORD_OTHER;
            }
            return r2.getKeyword();
        }

        @Override
        public Set<String> getKeywords() {
            HashSet<String> result = new HashSet<String>();
            result.add(PluralRules.KEYWORD_OTHER);
            RuleChain rc2 = this;
            while (rc2 != null) {
                result.add(rc2.rule.getKeyword());
                rc2 = rc2.next;
            }
            return result;
        }

        @Override
        public boolean isLimited(String keyword) {
            RuleChain rc2 = this;
            boolean result = false;
            while (rc2 != null) {
                if (keyword.equals(rc2.rule.getKeyword())) {
                    if (!rc2.rule.isLimited()) {
                        return false;
                    }
                    result = true;
                }
                rc2 = rc2.next;
            }
            return result;
        }

        @Override
        public int getRepeatLimit() {
            int result = 0;
            RuleChain rc2 = this;
            while (rc2 != null) {
                result = rc2.rule.updateRepeatLimit(result);
                rc2 = rc2.next;
            }
            return result;
        }

        public String toString() {
            String s2 = this.rule.toString();
            if (this.next != null) {
                s2 = this.next.toString() + "; " + s2;
            }
            return s2;
        }
    }

    private static class ConstrainedRule
    implements Rule,
    Serializable {
        private static final long serialVersionUID = 1L;
        private final String keyword;
        private final Constraint constraint;

        public ConstrainedRule(String keyword, Constraint constraint) {
            this.keyword = keyword;
            this.constraint = constraint;
        }

        public Rule and(Constraint c2) {
            return new ConstrainedRule(this.keyword, new AndConstraint(this.constraint, c2));
        }

        public Rule or(Constraint c2) {
            return new ConstrainedRule(this.keyword, new OrConstraint(this.constraint, c2));
        }

        public String getKeyword() {
            return this.keyword;
        }

        public boolean appliesTo(double n2) {
            return this.constraint.isFulfilled(n2);
        }

        public int updateRepeatLimit(int limit) {
            return this.constraint.updateRepeatLimit(limit);
        }

        public boolean isLimited() {
            return this.constraint.isLimited();
        }

        public String toString() {
            return this.keyword + ": " + this.constraint;
        }
    }

    private static class OrConstraint
    extends BinaryConstraint {
        private static final long serialVersionUID = 1405488568664762222L;

        OrConstraint(Constraint a2, Constraint b2) {
            super(a2, b2, " || ");
        }

        public boolean isFulfilled(double n2) {
            return this.a.isFulfilled(n2) || this.b.isFulfilled(n2);
        }

        public boolean isLimited() {
            return this.a.isLimited() && this.b.isLimited();
        }
    }

    private static class AndConstraint
    extends BinaryConstraint {
        private static final long serialVersionUID = 7766999779862263523L;

        AndConstraint(Constraint a2, Constraint b2) {
            super(a2, b2, " && ");
        }

        public boolean isFulfilled(double n2) {
            return this.a.isFulfilled(n2) && this.b.isFulfilled(n2);
        }

        public boolean isLimited() {
            return this.a.isLimited() || this.b.isLimited();
        }
    }

    private static abstract class BinaryConstraint
    implements Constraint,
    Serializable {
        private static final long serialVersionUID = 1L;
        protected final Constraint a;
        protected final Constraint b;
        private final String conjunction;

        protected BinaryConstraint(Constraint a2, Constraint b2, String c2) {
            this.a = a2;
            this.b = b2;
            this.conjunction = c2;
        }

        public int updateRepeatLimit(int limit) {
            return this.a.updateRepeatLimit(this.b.updateRepeatLimit(limit));
        }

        public String toString() {
            return this.a.toString() + this.conjunction + this.b.toString();
        }
    }

    private static class RangeConstraint
    implements Constraint,
    Serializable {
        private static final long serialVersionUID = 1L;
        private int mod;
        private boolean inRange;
        private boolean integersOnly;
        private long lowerBound;
        private long upperBound;
        private long[] range_list;

        RangeConstraint(int mod, boolean inRange, boolean integersOnly, long lowerBound, long upperBound, long[] range_list) {
            this.mod = mod;
            this.inRange = inRange;
            this.integersOnly = integersOnly;
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.range_list = range_list;
        }

        public boolean isFulfilled(double n2) {
            boolean test;
            if (this.integersOnly && n2 - (double)((long)n2) != 0.0) {
                return !this.inRange;
            }
            if (this.mod != 0) {
                n2 %= (double)this.mod;
            }
            boolean bl2 = test = n2 >= (double)this.lowerBound && n2 <= (double)this.upperBound;
            if (test && this.range_list != null) {
                test = false;
                for (int i2 = 0; !test && i2 < this.range_list.length; i2 += 2) {
                    test = n2 >= (double)this.range_list[i2] && n2 <= (double)this.range_list[i2 + 1];
                }
            }
            return this.inRange == test;
        }

        public boolean isLimited() {
            return this.integersOnly && this.inRange && this.mod == 0;
        }

        public int updateRepeatLimit(int limit) {
            int mylimit = this.mod == 0 ? (int)this.upperBound : this.mod;
            return Math.max(mylimit, limit);
        }

        public String toString() {
            class ListBuilder {
                StringBuilder sb = new StringBuilder("[");

                ListBuilder() {
                }

                ListBuilder add(String s2) {
                    return this.add(s2, null);
                }

                ListBuilder add(String s2, Object o2) {
                    if (this.sb.length() > 1) {
                        this.sb.append(", ");
                    }
                    this.sb.append(s2);
                    if (o2 != null) {
                        this.sb.append(": ").append(o2.toString());
                    }
                    return this;
                }

                public String toString() {
                    String s2 = this.sb.append(']').toString();
                    this.sb = null;
                    return s2;
                }
            }
            ListBuilder lb2 = new ListBuilder();
            if (this.mod > 1) {
                lb2.add("mod", this.mod);
            }
            if (this.inRange) {
                lb2.add("in");
            } else {
                lb2.add("except");
            }
            if (this.integersOnly) {
                lb2.add("ints");
            }
            if (this.lowerBound == this.upperBound) {
                lb2.add(String.valueOf(this.lowerBound));
            } else {
                lb2.add(String.valueOf(this.lowerBound) + "-" + String.valueOf(this.upperBound));
            }
            if (this.range_list != null) {
                lb2.add(Arrays.toString(this.range_list));
            }
            return lb2.toString();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static interface RuleList
    extends Serializable {
        public String select(double var1);

        public Set<String> getKeywords();

        public int getRepeatLimit();

        public boolean isLimited(String var1);
    }

    private static interface Rule
    extends Serializable {
        public String getKeyword();

        public boolean appliesTo(double var1);

        public boolean isLimited();

        public int updateRepeatLimit(int var1);
    }

    private static interface Constraint
    extends Serializable {
        public boolean isFulfilled(double var1);

        public boolean isLimited();

        public int updateRepeatLimit(int var1);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum PluralType {
        CARDINAL,
        ORDINAL;

    }
}

