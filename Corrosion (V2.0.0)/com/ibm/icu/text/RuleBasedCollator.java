/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.impl.BOCU;
import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.ImplicitCEGenerator;
import com.ibm.icu.impl.IntTrie;
import com.ibm.icu.impl.StringUCharacterIterator;
import com.ibm.icu.impl.Trie;
import com.ibm.icu.impl.TrieIterator;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.CollationElementIterator;
import com.ibm.icu.text.CollationKey;
import com.ibm.icu.text.CollationParsedRuleBuilder;
import com.ibm.icu.text.CollationRuleParser;
import com.ibm.icu.text.Collator;
import com.ibm.icu.text.CollatorReader;
import com.ibm.icu.text.Normalizer;
import com.ibm.icu.text.RawCollationKey;
import com.ibm.icu.text.UCharacterIterator;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.Output;
import com.ibm.icu.util.RangeValueIterator;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.VersionInfo;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.CharacterIterator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class RuleBasedCollator
extends Collator {
    static final byte BYTE_FIRST_TAILORED_ = 4;
    static final byte BYTE_COMMON_ = 5;
    static final int COMMON_TOP_2_ = 134;
    static final int COMMON_BOTTOM_2_ = 5;
    static final int COMMON_BOTTOM_3 = 5;
    static final int CE_CASE_BIT_MASK_ = 192;
    static final int CE_TAG_SHIFT_ = 24;
    static final int CE_TAG_MASK_ = 0xF000000;
    static final int CE_SPECIAL_FLAG_ = -268435456;
    static final int CE_SURROGATE_TAG_ = 5;
    static final int CE_PRIMARY_MASK_ = -65536;
    static final int CE_SECONDARY_MASK_ = 65280;
    static final int CE_TERTIARY_MASK_ = 255;
    static final int CE_PRIMARY_SHIFT_ = 16;
    static final int CE_SECONDARY_SHIFT_ = 8;
    static final int CE_CONTINUATION_MARKER_ = 192;
    int m_expansionOffset_;
    int m_contractionOffset_;
    boolean m_isJamoSpecial_;
    int m_defaultVariableTopValue_;
    boolean m_defaultIsFrenchCollation_;
    boolean m_defaultIsAlternateHandlingShifted_;
    int m_defaultCaseFirst_;
    boolean m_defaultIsCaseLevel_;
    int m_defaultDecomposition_;
    int m_defaultStrength_;
    boolean m_defaultIsHiragana4_;
    boolean m_defaultIsNumericCollation_;
    int[] m_defaultReorderCodes_;
    int m_variableTopValue_;
    boolean m_isHiragana4_;
    int m_caseFirst_;
    boolean m_isNumericCollation_;
    int[] m_reorderCodes_;
    int[] m_expansion_;
    char[] m_contractionIndex_;
    int[] m_contractionCE_;
    IntTrie m_trie_;
    int[] m_expansionEndCE_;
    byte[] m_expansionEndCEMaxSize_;
    byte[] m_unsafe_;
    byte[] m_contractionEnd_;
    String m_rules_;
    char m_minUnsafe_;
    char m_minContractionEnd_;
    VersionInfo m_version_;
    VersionInfo m_UCA_version_;
    VersionInfo m_UCD_version_;
    int m_leadByteToScripts;
    int m_scriptToLeadBytes;
    static final RuleBasedCollator UCA_;
    static final UCAConstants UCA_CONSTANTS_;
    static LeadByteConstants LEADBYTE_CONSTANTS_;
    static final char[] UCA_CONTRACTIONS_;
    static final int MAX_UCA_CONTRACTION_LENGTH;
    private static boolean UCA_INIT_COMPLETE;
    static final ImplicitCEGenerator impCEGen_;
    static final byte SORT_LEVEL_TERMINATOR_ = 1;
    static final int maxRegularPrimary = 122;
    static final int minImplicitPrimary = 224;
    static final int maxImplicitPrimary = 228;
    private static final int DEFAULT_MIN_HEURISTIC_ = 768;
    private static final char HEURISTIC_SIZE_ = '\u0420';
    private static final char HEURISTIC_OVERFLOW_MASK_ = '\u1fff';
    private static final int HEURISTIC_SHIFT_ = 3;
    private static final char HEURISTIC_OVERFLOW_OFFSET_ = '\u0100';
    private static final char HEURISTIC_MASK_ = '\u0007';
    private int m_caseSwitch_;
    private int m_common3_;
    private int m_mask3_;
    private int m_addition3_;
    private int m_top3_;
    private int m_bottom3_;
    private int m_topCount3_;
    private int m_bottomCount3_;
    private byte[] m_leadBytePermutationTable_;
    private static final int CASE_SWITCH_ = 192;
    private static final int NO_CASE_SWITCH_ = 0;
    private static final int CE_REMOVE_CASE_ = 63;
    private static final int CE_KEEP_CASE_ = 255;
    private static final int CE_CASE_MASK_3_ = 255;
    private static final double PROPORTION_2_ = 0.5;
    private static final double PROPORTION_3_ = 0.667;
    private static final byte BYTE_SHIFT_PREFIX_ = 3;
    static final byte BYTE_UNSHIFTED_MIN_ = 3;
    static final byte CODAN_PLACEHOLDER = 18;
    private static final byte BYTE_FIRST_NON_LATIN_PRIMARY_ = 91;
    private static final byte BYTE_UNSHIFTED_MAX_ = -1;
    private static final int TOTAL_2_ = 128;
    private static final int FLAG_BIT_MASK_CASE_SWITCH_OFF_ = 128;
    private static final int FLAG_BIT_MASK_CASE_SWITCH_ON_ = 64;
    private static final int COMMON_TOP_CASE_SWITCH_OFF_3_ = 133;
    private static final int COMMON_TOP_CASE_SWITCH_LOWER_3_ = 69;
    private static final int COMMON_TOP_CASE_SWITCH_UPPER_3_ = 197;
    private static final int COMMON_BOTTOM_3_ = 5;
    private static final int COMMON_BOTTOM_CASE_SWITCH_UPPER_3_ = 134;
    private static final int COMMON_BOTTOM_CASE_SWITCH_LOWER_3_ = 5;
    private static final int TOP_COUNT_2_ = 64;
    private static final int BOTTOM_COUNT_2_ = 64;
    private static final int COMMON_2_ = 5;
    private static final int COMMON_UPPER_FIRST_3_ = 197;
    private static final int COMMON_NORMAL_3_ = 5;
    private boolean m_isSimple3_;
    private boolean m_isFrenchCollation_;
    private boolean m_isAlternateHandlingShifted_;
    private boolean m_isCaseLevel_;
    private Lock frozenLock;
    private static final int SORT_BUFFER_INIT_SIZE_ = 128;
    private static final int SORT_BUFFER_INIT_SIZE_1_ = 1024;
    private static final int SORT_BUFFER_INIT_SIZE_2_ = 128;
    private static final int SORT_BUFFER_INIT_SIZE_3_ = 128;
    private static final int SORT_BUFFER_INIT_SIZE_CASE_ = 32;
    private static final int SORT_BUFFER_INIT_SIZE_4_ = 128;
    private static final int CE_CONTINUATION_TAG_ = 192;
    private static final int CE_REMOVE_CONTINUATION_MASK_ = -193;
    private static final int LAST_BYTE_MASK_ = 255;
    private static final byte SORT_CASE_BYTE_START_ = -128;
    private static final byte SORT_CASE_SHIFT_START_ = 7;
    private static final int CE_BUFFER_SIZE_ = 512;
    boolean latinOneUse_;
    boolean latinOneRegenTable_;
    boolean latinOneFailed_;
    int latinOneTableLen_;
    int[] latinOneCEs_;
    private static final int UCOL_REORDER_CODE_IGNORE = 4102;
    private static final int ENDOFLATINONERANGE_ = 255;
    private static final int LATINONETABLELEN_ = 305;
    private static final int BAIL_OUT_CE_ = -16777216;
    ContractionInfo m_ContInfo_;
    private transient boolean m_reallocLatinOneCEs_;
    private CollationBuffer collationBuffer;

    public RuleBasedCollator(String rules) throws Exception {
        this.latinOneUse_ = false;
        this.latinOneRegenTable_ = false;
        this.latinOneFailed_ = false;
        this.latinOneTableLen_ = 0;
        this.latinOneCEs_ = null;
        RuleBasedCollator.checkUCA();
        if (rules == null) {
            throw new IllegalArgumentException("Collation rules can not be null");
        }
        this.init(rules);
    }

    public Object clone() throws CloneNotSupportedException {
        return this.clone(this.isFrozen());
    }

    private Object clone(boolean frozen) throws CloneNotSupportedException {
        RuleBasedCollator result = (RuleBasedCollator)super.clone();
        if (this.latinOneCEs_ != null) {
            result.m_reallocLatinOneCEs_ = true;
            result.m_ContInfo_ = new ContractionInfo();
        }
        result.collationBuffer = null;
        result.frozenLock = frozen ? new ReentrantLock() : null;
        return result;
    }

    public CollationElementIterator getCollationElementIterator(String source) {
        return new CollationElementIterator(source, this);
    }

    public CollationElementIterator getCollationElementIterator(CharacterIterator source) {
        CharacterIterator newsource = (CharacterIterator)source.clone();
        return new CollationElementIterator(newsource, this);
    }

    public CollationElementIterator getCollationElementIterator(UCharacterIterator source) {
        return new CollationElementIterator(source, this);
    }

    public boolean isFrozen() {
        return this.frozenLock != null;
    }

    public Collator freeze() {
        if (!this.isFrozen()) {
            this.frozenLock = new ReentrantLock();
        }
        return this;
    }

    public RuleBasedCollator cloneAsThawed() {
        RuleBasedCollator clone = null;
        try {
            clone = (RuleBasedCollator)this.clone(false);
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
        return clone;
    }

    public void setHiraganaQuaternary(boolean flag) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
    }

    public void setHiraganaQuaternaryDefault() {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
    }

    public void setUpperCaseFirst(boolean upperfirst) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        if (upperfirst) {
            if (this.m_caseFirst_ != 25) {
                this.latinOneRegenTable_ = true;
            }
            this.m_caseFirst_ = 25;
        } else {
            if (this.m_caseFirst_ != 16) {
                this.latinOneRegenTable_ = true;
            }
            this.m_caseFirst_ = 16;
        }
        this.updateInternalState();
    }

    public void setLowerCaseFirst(boolean lowerfirst) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        if (lowerfirst) {
            if (this.m_caseFirst_ != 24) {
                this.latinOneRegenTable_ = true;
            }
            this.m_caseFirst_ = 24;
        } else {
            if (this.m_caseFirst_ != 16) {
                this.latinOneRegenTable_ = true;
            }
            this.m_caseFirst_ = 16;
        }
        this.updateInternalState();
    }

    public final void setCaseFirstDefault() {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        if (this.m_caseFirst_ != this.m_defaultCaseFirst_) {
            this.latinOneRegenTable_ = true;
        }
        this.m_caseFirst_ = this.m_defaultCaseFirst_;
        this.updateInternalState();
    }

    public void setAlternateHandlingDefault() {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        this.m_isAlternateHandlingShifted_ = this.m_defaultIsAlternateHandlingShifted_;
        this.updateInternalState();
    }

    public void setCaseLevelDefault() {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        this.m_isCaseLevel_ = this.m_defaultIsCaseLevel_;
        this.updateInternalState();
    }

    public void setDecompositionDefault() {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        this.setDecomposition(this.m_defaultDecomposition_);
        this.updateInternalState();
    }

    public void setFrenchCollationDefault() {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        if (this.m_isFrenchCollation_ != this.m_defaultIsFrenchCollation_) {
            this.latinOneRegenTable_ = true;
        }
        this.m_isFrenchCollation_ = this.m_defaultIsFrenchCollation_;
        this.updateInternalState();
    }

    public void setStrengthDefault() {
        this.setStrength(this.m_defaultStrength_);
        this.updateInternalState();
    }

    public void setNumericCollationDefault() {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        this.setNumericCollation(this.m_defaultIsNumericCollation_);
        this.updateInternalState();
    }

    public void setFrenchCollation(boolean flag) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        if (this.m_isFrenchCollation_ != flag) {
            this.latinOneRegenTable_ = true;
        }
        this.m_isFrenchCollation_ = flag;
        this.updateInternalState();
    }

    public void setAlternateHandlingShifted(boolean shifted) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        this.m_isAlternateHandlingShifted_ = shifted;
        this.updateInternalState();
    }

    public void setCaseLevel(boolean flag) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        this.m_isCaseLevel_ = flag;
        this.updateInternalState();
    }

    public void setStrength(int newStrength) {
        super.setStrength(newStrength);
        this.updateInternalState();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int setVariableTop(String varTop) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        if (varTop == null || varTop.length() == 0) {
            throw new IllegalArgumentException("Variable top argument string can not be null or zero in length.");
        }
        CollationBuffer buffer = null;
        try {
            buffer = this.getCollationBuffer();
            int n2 = this.setVariableTop(varTop, buffer);
            return n2;
        }
        finally {
            this.releaseCollationBuffer(buffer);
        }
    }

    private int setVariableTop(String varTop, CollationBuffer buffer) {
        buffer.m_srcUtilColEIter_.setText(varTop);
        int ce2 = buffer.m_srcUtilColEIter_.next();
        if (buffer.m_srcUtilColEIter_.getOffset() != varTop.length() || ce2 == -1) {
            throw new IllegalArgumentException("Variable top argument string is a contraction that does not exist in the Collation order");
        }
        int nextCE = buffer.m_srcUtilColEIter_.next();
        if (!(nextCE == -1 || RuleBasedCollator.isContinuation(nextCE) && (nextCE & 0xFFFF0000) == 0)) {
            throw new IllegalArgumentException("Variable top argument string can only have a single collation element that has less than or equal to two PRIMARY strength bytes");
        }
        this.m_variableTopValue_ = (ce2 & 0xFFFF0000) >> 16;
        return ce2 & 0xFFFF0000;
    }

    public void setVariableTop(int varTop) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        this.m_variableTopValue_ = (varTop & 0xFFFF0000) >> 16;
    }

    public void setNumericCollation(boolean flag) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        this.m_isNumericCollation_ = flag;
        this.updateInternalState();
    }

    public void setReorderCodes(int ... order) {
        if (this.isFrozen()) {
            throw new UnsupportedOperationException("Attempt to modify frozen object");
        }
        this.m_reorderCodes_ = (int[])(order != null && order.length > 0 ? (int[])order.clone() : null);
        this.buildPermutationTable();
    }

    public String getRules() {
        return this.m_rules_;
    }

    public String getRules(boolean fullrules) {
        if (!fullrules) {
            return this.m_rules_;
        }
        return RuleBasedCollator.UCA_.m_rules_.concat(this.m_rules_);
    }

    public UnicodeSet getTailoredSet() {
        try {
            CollationRuleParser src = new CollationRuleParser(this.getRules());
            return src.getTailoredSet();
        }
        catch (Exception e2) {
            throw new IllegalStateException("A tailoring rule should not have errors. Something is quite wrong!");
        }
    }

    private void addSpecial(contContext c2, StringBuilder buffer, int CE) {
        block16: {
            int newCE;
            int offset;
            StringBuilder b2;
            block15: {
                b2 = new StringBuilder();
                offset = (CE & 0xFFFFFF) - c2.coll.m_contractionOffset_;
                newCE = c2.coll.m_contractionCE_[offset];
                if (newCE != -268435456) {
                    if (RuleBasedCollator.isSpecial(CE) && RuleBasedCollator.getTag(CE) == 2 && RuleBasedCollator.isSpecial(newCE) && RuleBasedCollator.getTag(newCE) == 11 && c2.addPrefixes) {
                        this.addSpecial(c2, buffer, newCE);
                    }
                    if (buffer.length() > 1) {
                        if (c2.contractions != null) {
                            c2.contractions.add(buffer.toString());
                        }
                        if (c2.expansions != null && RuleBasedCollator.isSpecial(CE) && RuleBasedCollator.getTag(CE) == 1) {
                            c2.expansions.add(buffer.toString());
                        }
                    }
                }
                ++offset;
                if (RuleBasedCollator.getTag(CE) != 11 || !c2.addPrefixes) break block15;
                while (c2.coll.m_contractionIndex_[offset] != '\uffff') {
                    b2.delete(0, b2.length());
                    b2.append((CharSequence)buffer);
                    newCE = c2.coll.m_contractionCE_[offset];
                    b2.insert(0, c2.coll.m_contractionIndex_[offset]);
                    if (RuleBasedCollator.isSpecial(newCE) && (RuleBasedCollator.getTag(newCE) == 2 || RuleBasedCollator.getTag(newCE) == 11)) {
                        this.addSpecial(c2, b2, newCE);
                    } else {
                        if (c2.contractions != null) {
                            c2.contractions.add(b2.toString());
                        }
                        if (c2.expansions != null && RuleBasedCollator.isSpecial(newCE) && RuleBasedCollator.getTag(newCE) == 1) {
                            c2.expansions.add(b2.toString());
                        }
                    }
                    ++offset;
                }
                break block16;
            }
            if (RuleBasedCollator.getTag(CE) != 2) break block16;
            while (c2.coll.m_contractionIndex_[offset] != '\uffff') {
                b2.delete(0, b2.length());
                b2.append((CharSequence)buffer);
                newCE = c2.coll.m_contractionCE_[offset];
                b2.append(c2.coll.m_contractionIndex_[offset]);
                if (RuleBasedCollator.isSpecial(newCE) && (RuleBasedCollator.getTag(newCE) == 2 || RuleBasedCollator.getTag(newCE) == 11)) {
                    this.addSpecial(c2, b2, newCE);
                } else {
                    if (c2.contractions != null) {
                        c2.contractions.add(b2.toString());
                    }
                    if (c2.expansions != null && RuleBasedCollator.isSpecial(newCE) && RuleBasedCollator.getTag(newCE) == 1) {
                        c2.expansions.add(b2.toString());
                    }
                }
                ++offset;
            }
        }
    }

    private void processSpecials(contContext c2) {
        int internalBufferSize = 512;
        TrieIterator trieiterator = new TrieIterator(c2.coll.m_trie_);
        RangeValueIterator.Element element = new RangeValueIterator.Element();
        while (trieiterator.next(element)) {
            int start = element.start;
            int limit = element.limit;
            int CE = element.value;
            StringBuilder contraction = new StringBuilder(internalBufferSize);
            if (!RuleBasedCollator.isSpecial(CE)) continue;
            if (RuleBasedCollator.getTag(CE) == 11 && c2.addPrefixes || RuleBasedCollator.getTag(CE) == 2) {
                while (start < limit) {
                    if (c2.removedContractions != null && c2.removedContractions.contains(start)) {
                        ++start;
                        continue;
                    }
                    contraction.append((char)start);
                    this.addSpecial(c2, contraction, CE);
                    ++start;
                }
                continue;
            }
            if (c2.expansions == null || RuleBasedCollator.getTag(CE) != 1) continue;
            while (start < limit) {
                c2.expansions.add(start++);
            }
        }
    }

    public void getContractionsAndExpansions(UnicodeSet contractions, UnicodeSet expansions, boolean addPrefixes) throws Exception {
        if (contractions != null) {
            contractions.clear();
        }
        if (expansions != null) {
            expansions.clear();
        }
        String rules = this.getRules();
        CollationRuleParser src = new CollationRuleParser(rules);
        contContext c2 = new contContext(UCA_, contractions, expansions, src.m_removeSet_, addPrefixes);
        this.processSpecials(c2);
        c2.coll = this;
        c2.removedContractions = null;
        this.processSpecials(c2);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public CollationKey getCollationKey(String source) {
        if (source == null) {
            return null;
        }
        CollationBuffer buffer = null;
        try {
            buffer = this.getCollationBuffer();
            CollationKey collationKey = this.getCollationKey(source, buffer);
            return collationKey;
        }
        finally {
            this.releaseCollationBuffer(buffer);
        }
    }

    private CollationKey getCollationKey(String source, CollationBuffer buffer) {
        buffer.m_utilRawCollationKey_ = this.getRawCollationKey(source, buffer.m_utilRawCollationKey_, buffer);
        return new CollationKey(source, buffer.m_utilRawCollationKey_);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public RawCollationKey getRawCollationKey(String source, RawCollationKey key) {
        if (source == null) {
            return null;
        }
        CollationBuffer buffer = null;
        try {
            buffer = this.getCollationBuffer();
            RawCollationKey rawCollationKey = this.getRawCollationKey(source, key, buffer);
            return rawCollationKey;
        }
        finally {
            this.releaseCollationBuffer(buffer);
        }
    }

    private RawCollationKey getRawCollationKey(String source, RawCollationKey key, CollationBuffer buffer) {
        int strength = this.getStrength();
        buffer.m_utilCompare0_ = this.m_isCaseLevel_;
        buffer.m_utilCompare2_ = strength >= 1;
        buffer.m_utilCompare3_ = strength >= 2;
        buffer.m_utilCompare4_ = strength >= 3;
        buffer.m_utilCompare5_ = strength == 15;
        boolean doFrench = this.m_isFrenchCollation_ && buffer.m_utilCompare2_;
        int commonBottom4 = (this.m_variableTopValue_ >>> 8) + 1 & 0xFF;
        byte hiragana4 = 0;
        if (this.m_isHiragana4_ && buffer.m_utilCompare4_) {
            hiragana4 = (byte)commonBottom4;
            ++commonBottom4;
        }
        int bottomCount4 = 255 - commonBottom4;
        if (buffer.m_utilCompare5_ && Normalizer.quickCheck(source, Normalizer.NFD, 0) != Normalizer.YES) {
            source = Normalizer.decompose(source, false);
        } else if (this.getDecomposition() != 16 && Normalizer.quickCheck(source, Normalizer.FCD, 0) != Normalizer.YES) {
            source = Normalizer.normalize(source, Normalizer.FCD);
        }
        this.getSortKeyBytes(source, doFrench, hiragana4, commonBottom4, bottomCount4, buffer);
        if (key == null) {
            key = new RawCollationKey();
        }
        this.getSortKey(source, doFrench, commonBottom4, bottomCount4, key, buffer);
        return key;
    }

    public boolean isUpperCaseFirst() {
        return this.m_caseFirst_ == 25;
    }

    public boolean isLowerCaseFirst() {
        return this.m_caseFirst_ == 24;
    }

    public boolean isAlternateHandlingShifted() {
        return this.m_isAlternateHandlingShifted_;
    }

    public boolean isCaseLevel() {
        return this.m_isCaseLevel_;
    }

    public boolean isFrenchCollation() {
        return this.m_isFrenchCollation_;
    }

    public boolean isHiraganaQuaternary() {
        return this.m_isHiragana4_;
    }

    public int getVariableTop() {
        return this.m_variableTopValue_ << 16;
    }

    public boolean getNumericCollation() {
        return this.m_isNumericCollation_;
    }

    public int[] getReorderCodes() {
        if (this.m_reorderCodes_ != null) {
            return (int[])this.m_reorderCodes_.clone();
        }
        return LeadByteConstants.EMPTY_INT_ARRAY;
    }

    public static int[] getEquivalentReorderCodes(int reorderCode) {
        int[] leadBytes;
        HashSet<Integer> equivalentCodesSet = new HashSet<Integer>();
        for (int leadByte : leadBytes = LEADBYTE_CONSTANTS_.getLeadBytesForReorderCode(reorderCode)) {
            int[] codes;
            for (int code : codes = LEADBYTE_CONSTANTS_.getReorderCodesForLeadByte(leadByte)) {
                equivalentCodesSet.add(code);
            }
        }
        int[] equivalentCodes = new int[equivalentCodesSet.size()];
        int i2 = 0;
        Iterator i$ = equivalentCodesSet.iterator();
        while (i$.hasNext()) {
            int code = (Integer)i$.next();
            equivalentCodes[i2++] = code;
        }
        return equivalentCodes;
    }

    public boolean equals(Object obj) {
        int i2;
        boolean rules;
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        RuleBasedCollator other = (RuleBasedCollator)obj;
        if (this.getStrength() != other.getStrength() || this.getDecomposition() != other.getDecomposition() || other.m_caseFirst_ != this.m_caseFirst_ || other.m_caseSwitch_ != this.m_caseSwitch_ || other.m_isAlternateHandlingShifted_ != this.m_isAlternateHandlingShifted_ || other.m_isCaseLevel_ != this.m_isCaseLevel_ || other.m_isFrenchCollation_ != this.m_isFrenchCollation_ || other.m_isHiragana4_ != this.m_isHiragana4_) {
            return false;
        }
        if (this.m_reorderCodes_ != null ^ other.m_reorderCodes_ != null) {
            return false;
        }
        if (this.m_reorderCodes_ != null) {
            if (this.m_reorderCodes_.length != other.m_reorderCodes_.length) {
                return false;
            }
            for (int i3 = 0; i3 < this.m_reorderCodes_.length; ++i3) {
                if (this.m_reorderCodes_[i3] == other.m_reorderCodes_[i3]) continue;
                return false;
            }
        }
        boolean bl2 = rules = this.m_rules_ == other.m_rules_;
        if (!rules && this.m_rules_ != null && other.m_rules_ != null) {
            rules = this.m_rules_.equals(other.m_rules_);
        }
        if (!rules || !ICUDebug.enabled("collation")) {
            return rules;
        }
        if (this.m_addition3_ != other.m_addition3_ || this.m_bottom3_ != other.m_bottom3_ || this.m_bottomCount3_ != other.m_bottomCount3_ || this.m_common3_ != other.m_common3_ || this.m_isSimple3_ != other.m_isSimple3_ || this.m_mask3_ != other.m_mask3_ || this.m_minContractionEnd_ != other.m_minContractionEnd_ || this.m_minUnsafe_ != other.m_minUnsafe_ || this.m_top3_ != other.m_top3_ || this.m_topCount3_ != other.m_topCount3_ || !Arrays.equals(this.m_unsafe_, other.m_unsafe_)) {
            return false;
        }
        if (!this.m_trie_.equals(other.m_trie_)) {
            for (i2 = 0x10FFFF; i2 >= 0; --i2) {
                int otherv;
                int v2 = this.m_trie_.getCodePointValue(i2);
                if (v2 == (otherv = other.m_trie_.getCodePointValue(i2))) continue;
                int mask = v2 & 0xFF000000;
                if (mask == (otherv & 0xFF000000)) {
                    v2 &= 0xFFFFFF;
                    otherv &= 0xFFFFFF;
                    if (mask == -251658240) {
                        v2 -= this.m_expansionOffset_ << 4;
                        otherv -= other.m_expansionOffset_ << 4;
                    } else if (mask == -234881024) {
                        v2 -= this.m_contractionOffset_;
                        otherv -= other.m_contractionOffset_;
                    }
                    if (v2 == otherv) continue;
                }
                return false;
            }
        }
        if (!(Arrays.equals(this.m_contractionCE_, other.m_contractionCE_) && Arrays.equals(this.m_contractionEnd_, other.m_contractionEnd_) && Arrays.equals(this.m_contractionIndex_, other.m_contractionIndex_) && Arrays.equals(this.m_expansion_, other.m_expansion_) && Arrays.equals(this.m_expansionEndCE_, other.m_expansionEndCE_))) {
            return false;
        }
        for (i2 = 0; i2 < this.m_expansionEndCE_.length; ++i2) {
            if (this.m_expansionEndCEMaxSize_[i2] == other.m_expansionEndCEMaxSize_[i2]) continue;
            return false;
        }
        return true;
    }

    public int hashCode() {
        String rules = this.getRules();
        if (rules == null) {
            rules = "";
        }
        return rules.hashCode();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int compare(String source, String target) {
        if (source.equals(target)) {
            return 0;
        }
        CollationBuffer buffer = null;
        try {
            buffer = this.getCollationBuffer();
            int n2 = this.compare(source, target, buffer);
            return n2;
        }
        finally {
            this.releaseCollationBuffer(buffer);
        }
    }

    private int compare(String source, String target, CollationBuffer buffer) {
        int offset = this.getFirstUnmatchedOffset(source, target);
        if (this.latinOneUse_) {
            if (offset < source.length() && source.charAt(offset) > '\u00ff' || offset < target.length() && target.charAt(offset) > '\u00ff') {
                return this.compareRegular(source, target, offset, buffer);
            }
            return this.compareUseLatin1(source, target, offset, buffer);
        }
        return this.compareRegular(source, target, offset, buffer);
    }

    private static void checkUCA() throws MissingResourceException {
        if (UCA_INIT_COMPLETE && UCA_ == null) {
            throw new MissingResourceException("Collator UCA data unavailable", "", "");
        }
    }

    RuleBasedCollator() {
        this.latinOneUse_ = false;
        this.latinOneRegenTable_ = false;
        this.latinOneFailed_ = false;
        this.latinOneTableLen_ = 0;
        this.latinOneCEs_ = null;
        RuleBasedCollator.checkUCA();
    }

    RuleBasedCollator(ULocale locale) {
        block11: {
            this.latinOneUse_ = false;
            this.latinOneRegenTable_ = false;
            this.latinOneFailed_ = false;
            this.latinOneTableLen_ = 0;
            this.latinOneCEs_ = null;
            RuleBasedCollator.checkUCA();
            try {
                ICUResourceBundle rb2 = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/coll", locale);
                if (rb2 == null) break block11;
                ICUResourceBundle elements = null;
                String collkey = locale.getKeywordValue("collation");
                if (collkey != null) {
                    try {
                        elements = rb2.getWithFallback("collations/" + collkey);
                    }
                    catch (MissingResourceException e2) {
                        // empty catch block
                    }
                }
                if (elements == null) {
                    collkey = rb2.getStringWithFallback("collations/default");
                    elements = rb2.getWithFallback("collations/" + collkey);
                }
                ULocale uloc = rb2.getULocale();
                this.setLocale(uloc, uloc);
                this.m_rules_ = elements.getString("Sequence");
                ByteBuffer buf = elements.get("%%CollationBin").getBinary();
                if (buf != null) {
                    CollatorReader.initRBC(this, buf);
                    if (!this.m_UCA_version_.equals(RuleBasedCollator.UCA_.m_UCA_version_) || !this.m_UCD_version_.equals(RuleBasedCollator.UCA_.m_UCD_version_)) {
                        this.init(this.m_rules_);
                        return;
                    }
                    this.init();
                    try {
                        UResourceBundle reorderRes = elements.get("%%ReorderCodes");
                        if (reorderRes != null) {
                            int[] reorderCodes = reorderRes.getIntVector();
                            this.setReorderCodes(reorderCodes);
                            this.m_defaultReorderCodes_ = (int[])reorderCodes.clone();
                        }
                    }
                    catch (MissingResourceException e3) {
                        // empty catch block
                    }
                    return;
                }
                this.init(this.m_rules_);
                return;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.setWithUCAData();
    }

    final void setWithUCATables() {
        this.m_contractionOffset_ = RuleBasedCollator.UCA_.m_contractionOffset_;
        this.m_expansionOffset_ = RuleBasedCollator.UCA_.m_expansionOffset_;
        this.m_expansion_ = RuleBasedCollator.UCA_.m_expansion_;
        this.m_contractionIndex_ = RuleBasedCollator.UCA_.m_contractionIndex_;
        this.m_contractionCE_ = RuleBasedCollator.UCA_.m_contractionCE_;
        this.m_trie_ = RuleBasedCollator.UCA_.m_trie_;
        this.m_expansionEndCE_ = RuleBasedCollator.UCA_.m_expansionEndCE_;
        this.m_expansionEndCEMaxSize_ = RuleBasedCollator.UCA_.m_expansionEndCEMaxSize_;
        this.m_unsafe_ = RuleBasedCollator.UCA_.m_unsafe_;
        this.m_contractionEnd_ = RuleBasedCollator.UCA_.m_contractionEnd_;
        this.m_minUnsafe_ = RuleBasedCollator.UCA_.m_minUnsafe_;
        this.m_minContractionEnd_ = RuleBasedCollator.UCA_.m_minContractionEnd_;
    }

    final void setWithUCAData() {
        this.latinOneFailed_ = true;
        this.m_addition3_ = RuleBasedCollator.UCA_.m_addition3_;
        this.m_bottom3_ = RuleBasedCollator.UCA_.m_bottom3_;
        this.m_bottomCount3_ = RuleBasedCollator.UCA_.m_bottomCount3_;
        this.m_caseFirst_ = RuleBasedCollator.UCA_.m_caseFirst_;
        this.m_caseSwitch_ = RuleBasedCollator.UCA_.m_caseSwitch_;
        this.m_common3_ = RuleBasedCollator.UCA_.m_common3_;
        this.m_contractionOffset_ = RuleBasedCollator.UCA_.m_contractionOffset_;
        this.setDecomposition(UCA_.getDecomposition());
        this.m_defaultCaseFirst_ = RuleBasedCollator.UCA_.m_defaultCaseFirst_;
        this.m_defaultDecomposition_ = RuleBasedCollator.UCA_.m_defaultDecomposition_;
        this.m_defaultIsAlternateHandlingShifted_ = RuleBasedCollator.UCA_.m_defaultIsAlternateHandlingShifted_;
        this.m_defaultIsCaseLevel_ = RuleBasedCollator.UCA_.m_defaultIsCaseLevel_;
        this.m_defaultIsFrenchCollation_ = RuleBasedCollator.UCA_.m_defaultIsFrenchCollation_;
        this.m_defaultIsHiragana4_ = RuleBasedCollator.UCA_.m_defaultIsHiragana4_;
        this.m_defaultStrength_ = RuleBasedCollator.UCA_.m_defaultStrength_;
        this.m_defaultVariableTopValue_ = RuleBasedCollator.UCA_.m_defaultVariableTopValue_;
        this.m_defaultIsNumericCollation_ = RuleBasedCollator.UCA_.m_defaultIsNumericCollation_;
        this.m_expansionOffset_ = RuleBasedCollator.UCA_.m_expansionOffset_;
        this.m_isAlternateHandlingShifted_ = RuleBasedCollator.UCA_.m_isAlternateHandlingShifted_;
        this.m_isCaseLevel_ = RuleBasedCollator.UCA_.m_isCaseLevel_;
        this.m_isFrenchCollation_ = RuleBasedCollator.UCA_.m_isFrenchCollation_;
        this.m_isHiragana4_ = RuleBasedCollator.UCA_.m_isHiragana4_;
        this.m_isJamoSpecial_ = RuleBasedCollator.UCA_.m_isJamoSpecial_;
        this.m_isSimple3_ = RuleBasedCollator.UCA_.m_isSimple3_;
        this.m_mask3_ = RuleBasedCollator.UCA_.m_mask3_;
        this.m_minContractionEnd_ = RuleBasedCollator.UCA_.m_minContractionEnd_;
        this.m_minUnsafe_ = RuleBasedCollator.UCA_.m_minUnsafe_;
        this.m_rules_ = RuleBasedCollator.UCA_.m_rules_;
        this.setStrength(UCA_.getStrength());
        this.m_top3_ = RuleBasedCollator.UCA_.m_top3_;
        this.m_topCount3_ = RuleBasedCollator.UCA_.m_topCount3_;
        this.m_variableTopValue_ = RuleBasedCollator.UCA_.m_variableTopValue_;
        this.m_isNumericCollation_ = RuleBasedCollator.UCA_.m_isNumericCollation_;
        this.setWithUCATables();
        this.latinOneFailed_ = false;
    }

    final boolean isUnsafe(char ch) {
        byte value;
        if (ch < this.m_minUnsafe_) {
            return false;
        }
        if (ch >= '\u2100') {
            if (UTF16.isLeadSurrogate(ch) || UTF16.isTrailSurrogate(ch)) {
                return true;
            }
            ch = (char)(ch & 0x1FFF);
            ch = (char)(ch + 256);
        }
        return ((value = this.m_unsafe_[ch >> 3]) >> (ch & 7) & 1) != 0;
    }

    final boolean isContractionEnd(char ch) {
        byte value;
        if (UTF16.isTrailSurrogate(ch)) {
            return true;
        }
        if (ch < this.m_minContractionEnd_) {
            return false;
        }
        if (ch >= '\u2100') {
            ch = (char)(ch & 0x1FFF);
            ch = (char)(ch + 256);
        }
        return ((value = this.m_contractionEnd_[ch >> 3]) >> (ch & 7) & 1) != 0;
    }

    static int getTag(int ce2) {
        return (ce2 & 0xF000000) >> 24;
    }

    static boolean isSpecial(int ce2) {
        return (ce2 & 0xF0000000) == -268435456;
    }

    static final boolean isContinuation(int ce2) {
        return ce2 != -1 && (ce2 & 0xC0) == 192;
    }

    private void init(String rules) throws Exception {
        this.setWithUCAData();
        CollationParsedRuleBuilder builder = new CollationParsedRuleBuilder(rules);
        builder.setRules(this);
        this.m_rules_ = rules;
        this.init();
        this.buildPermutationTable();
    }

    private final int compareRegular(String source, String target, int offset, CollationBuffer buffer) {
        boolean doHiragana4;
        buffer.resetBuffers();
        int strength = this.getStrength();
        buffer.m_utilCompare0_ = this.m_isCaseLevel_;
        buffer.m_utilCompare2_ = strength >= 1;
        buffer.m_utilCompare3_ = strength >= 2;
        buffer.m_utilCompare4_ = strength >= 3;
        buffer.m_utilCompare5_ = strength == 15;
        boolean doFrench = this.m_isFrenchCollation_ && buffer.m_utilCompare2_;
        boolean doShift4 = this.m_isAlternateHandlingShifted_ && buffer.m_utilCompare4_;
        boolean bl2 = doHiragana4 = this.m_isHiragana4_ && buffer.m_utilCompare4_;
        if (doHiragana4 && doShift4) {
            String sourcesub = source.substring(offset);
            String targetsub = target.substring(offset);
            return this.compareBySortKeys(sourcesub, targetsub, buffer);
        }
        int lowestpvalue = this.m_isAlternateHandlingShifted_ ? this.m_variableTopValue_ << 16 : 0;
        buffer.m_srcUtilCEBufferSize_ = 0;
        buffer.m_tgtUtilCEBufferSize_ = 0;
        int result = this.doPrimaryCompare(doHiragana4, lowestpvalue, source, target, offset, buffer);
        if (buffer.m_srcUtilCEBufferSize_ == -1 && buffer.m_tgtUtilCEBufferSize_ == -1) {
            return result;
        }
        int hiraganaresult = result;
        if (buffer.m_utilCompare2_ && (result = RuleBasedCollator.doSecondaryCompare(doFrench, buffer)) != 0) {
            return result;
        }
        if (buffer.m_utilCompare0_ && (result = this.doCaseCompare(buffer)) != 0) {
            return result;
        }
        if (buffer.m_utilCompare3_ && (result = this.doTertiaryCompare(buffer)) != 0) {
            return result;
        }
        if (doShift4) {
            result = this.doQuaternaryCompare(lowestpvalue, buffer);
            if (result != 0) {
                return result;
            }
        } else if (doHiragana4 && hiraganaresult != 0) {
            return hiraganaresult;
        }
        if (buffer.m_utilCompare5_) {
            return RuleBasedCollator.doIdenticalCompare(source, target, offset, true);
        }
        return 0;
    }

    static boolean isCompressible(int primary1) {
        return 91 <= primary1 && primary1 <= 122;
    }

    private final int doPrimaryBytes(int ce2, boolean notIsContinuation, boolean doShift, int leadPrimary, int commonBottom4, int bottomCount4, CollationBuffer buffer) {
        int p1;
        int p2 = (ce2 >>>= 16) & 0xFF;
        int originalP1 = p1 = ce2 >>> 8;
        if (notIsContinuation && this.m_leadBytePermutationTable_ != null) {
            p1 = 0xFF & this.m_leadBytePermutationTable_[p1];
        }
        if (doShift) {
            if (buffer.m_utilCount4_ > 0) {
                while (buffer.m_utilCount4_ > bottomCount4) {
                    buffer.m_utilBytes4_ = RuleBasedCollator.append(buffer.m_utilBytes4_, buffer.m_utilBytesCount4_, (byte)(commonBottom4 + bottomCount4));
                    ++buffer.m_utilBytesCount4_;
                    buffer.m_utilCount4_ -= bottomCount4;
                }
                buffer.m_utilBytes4_ = RuleBasedCollator.append(buffer.m_utilBytes4_, buffer.m_utilBytesCount4_, (byte)(commonBottom4 + (buffer.m_utilCount4_ - 1)));
                ++buffer.m_utilBytesCount4_;
                buffer.m_utilCount4_ = 0;
            }
            if (p1 != 0) {
                buffer.m_utilBytes4_ = RuleBasedCollator.append(buffer.m_utilBytes4_, buffer.m_utilBytesCount4_, (byte)p1);
                ++buffer.m_utilBytesCount4_;
            }
            if (p2 != 0) {
                buffer.m_utilBytes4_ = RuleBasedCollator.append(buffer.m_utilBytes4_, buffer.m_utilBytesCount4_, (byte)p2);
                ++buffer.m_utilBytesCount4_;
            }
        } else if (p1 != 0) {
            if (notIsContinuation) {
                if (leadPrimary == p1) {
                    buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)p2);
                    ++buffer.m_utilBytesCount1_;
                } else {
                    if (leadPrimary != 0) {
                        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, p1 > leadPrimary ? (byte)-1 : 3);
                        ++buffer.m_utilBytesCount1_;
                    }
                    if (p2 == 0) {
                        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)p1);
                        ++buffer.m_utilBytesCount1_;
                        leadPrimary = 0;
                    } else if (RuleBasedCollator.isCompressible(originalP1)) {
                        leadPrimary = p1;
                        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)p1);
                        ++buffer.m_utilBytesCount1_;
                        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)p2);
                        ++buffer.m_utilBytesCount1_;
                    } else {
                        leadPrimary = 0;
                        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)p1);
                        ++buffer.m_utilBytesCount1_;
                        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)p2);
                        ++buffer.m_utilBytesCount1_;
                    }
                }
            } else {
                buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)p1);
                ++buffer.m_utilBytesCount1_;
                if (p2 != 0) {
                    buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)p2);
                    ++buffer.m_utilBytesCount1_;
                }
            }
        }
        return leadPrimary;
    }

    private final void doSecondaryBytes(int ce2, boolean notIsContinuation, boolean doFrench, CollationBuffer buffer) {
        int s2 = ce2 >> 8 & 0xFF;
        if (s2 != 0) {
            if (!doFrench) {
                if (s2 == 5 && notIsContinuation) {
                    ++buffer.m_utilCount2_;
                } else {
                    if (buffer.m_utilCount2_ > 0) {
                        if (s2 > 5) {
                            while (buffer.m_utilCount2_ > 64) {
                                buffer.m_utilBytes2_ = RuleBasedCollator.append(buffer.m_utilBytes2_, buffer.m_utilBytesCount2_, (byte)70);
                                ++buffer.m_utilBytesCount2_;
                                buffer.m_utilCount2_ -= 64;
                            }
                            buffer.m_utilBytes2_ = RuleBasedCollator.append(buffer.m_utilBytes2_, buffer.m_utilBytesCount2_, (byte)(134 - (buffer.m_utilCount2_ - 1)));
                            ++buffer.m_utilBytesCount2_;
                        } else {
                            while (buffer.m_utilCount2_ > 64) {
                                buffer.m_utilBytes2_ = RuleBasedCollator.append(buffer.m_utilBytes2_, buffer.m_utilBytesCount2_, (byte)69);
                                ++buffer.m_utilBytesCount2_;
                                buffer.m_utilCount2_ -= 64;
                            }
                            buffer.m_utilBytes2_ = RuleBasedCollator.append(buffer.m_utilBytes2_, buffer.m_utilBytesCount2_, (byte)(5 + (buffer.m_utilCount2_ - 1)));
                            ++buffer.m_utilBytesCount2_;
                        }
                        buffer.m_utilCount2_ = 0;
                    }
                    buffer.m_utilBytes2_ = RuleBasedCollator.append(buffer.m_utilBytes2_, buffer.m_utilBytesCount2_, (byte)s2);
                    ++buffer.m_utilBytesCount2_;
                }
            } else {
                buffer.m_utilBytes2_ = RuleBasedCollator.append(buffer.m_utilBytes2_, buffer.m_utilBytesCount2_, (byte)s2);
                ++buffer.m_utilBytesCount2_;
                if (notIsContinuation) {
                    if (buffer.m_utilFrenchStart_ != -1) {
                        RuleBasedCollator.reverseBuffer(buffer.m_utilBytes2_, buffer.m_utilFrenchStart_, buffer.m_utilFrenchEnd_);
                        buffer.m_utilFrenchStart_ = -1;
                    }
                } else {
                    if (buffer.m_utilFrenchStart_ == -1) {
                        buffer.m_utilFrenchStart_ = buffer.m_utilBytesCount2_ - 2;
                    }
                    buffer.m_utilFrenchEnd_ = buffer.m_utilBytesCount2_ - 1;
                }
            }
        }
    }

    private static void reverseBuffer(byte[] buffer, int start, int end) {
        while (start < end) {
            byte b2 = buffer[start];
            buffer[start++] = buffer[end];
            buffer[end--] = b2;
        }
    }

    private final int doCaseShift(int caseshift, CollationBuffer buffer) {
        if (caseshift == 0) {
            buffer.m_utilBytes0_ = RuleBasedCollator.append(buffer.m_utilBytes0_, buffer.m_utilBytesCount0_, (byte)-128);
            ++buffer.m_utilBytesCount0_;
            caseshift = 7;
        }
        return caseshift;
    }

    private final int doCaseBytes(int tertiary, boolean notIsContinuation, int caseshift, CollationBuffer buffer) {
        caseshift = this.doCaseShift(caseshift, buffer);
        if (notIsContinuation && tertiary != 0) {
            byte casebits = (byte)(tertiary & 0xC0);
            if (this.m_caseFirst_ == 25) {
                if (casebits == 0) {
                    int n2 = buffer.m_utilBytesCount0_ - 1;
                    buffer.m_utilBytes0_[n2] = (byte)(buffer.m_utilBytes0_[n2] | 1 << --caseshift);
                } else {
                    caseshift = this.doCaseShift(caseshift - 1, buffer);
                    int n3 = buffer.m_utilBytesCount0_ - 1;
                    buffer.m_utilBytes0_[n3] = (byte)(buffer.m_utilBytes0_[n3] | (casebits >> 6 & 1) << --caseshift);
                }
            } else if (casebits != 0) {
                int n4 = buffer.m_utilBytesCount0_ - 1;
                buffer.m_utilBytes0_[n4] = (byte)(buffer.m_utilBytes0_[n4] | 1 << --caseshift);
                caseshift = this.doCaseShift(caseshift, buffer);
                int n5 = buffer.m_utilBytesCount0_ - 1;
                buffer.m_utilBytes0_[n5] = (byte)(buffer.m_utilBytes0_[n5] | (casebits >> 7 & 1) << --caseshift);
            } else {
                --caseshift;
            }
        }
        return caseshift;
    }

    private final void doTertiaryBytes(int tertiary, boolean notIsContinuation, CollationBuffer buffer) {
        if (tertiary != 0) {
            if (tertiary == this.m_common3_ && notIsContinuation) {
                ++buffer.m_utilCount3_;
            } else {
                int common3 = this.m_common3_ & 0xFF;
                if (tertiary > common3 && this.m_common3_ == 5) {
                    tertiary += this.m_addition3_;
                } else if (tertiary <= common3 && this.m_common3_ == 197) {
                    tertiary -= this.m_addition3_;
                }
                if (buffer.m_utilCount3_ > 0) {
                    if (tertiary > common3) {
                        while (buffer.m_utilCount3_ > this.m_topCount3_) {
                            buffer.m_utilBytes3_ = RuleBasedCollator.append(buffer.m_utilBytes3_, buffer.m_utilBytesCount3_, (byte)(this.m_top3_ - this.m_topCount3_));
                            ++buffer.m_utilBytesCount3_;
                            buffer.m_utilCount3_ -= this.m_topCount3_;
                        }
                        buffer.m_utilBytes3_ = RuleBasedCollator.append(buffer.m_utilBytes3_, buffer.m_utilBytesCount3_, (byte)(this.m_top3_ - (buffer.m_utilCount3_ - 1)));
                        ++buffer.m_utilBytesCount3_;
                    } else {
                        while (buffer.m_utilCount3_ > this.m_bottomCount3_) {
                            buffer.m_utilBytes3_ = RuleBasedCollator.append(buffer.m_utilBytes3_, buffer.m_utilBytesCount3_, (byte)(this.m_bottom3_ + this.m_bottomCount3_));
                            ++buffer.m_utilBytesCount3_;
                            buffer.m_utilCount3_ -= this.m_bottomCount3_;
                        }
                        buffer.m_utilBytes3_ = RuleBasedCollator.append(buffer.m_utilBytes3_, buffer.m_utilBytesCount3_, (byte)(this.m_bottom3_ + (buffer.m_utilCount3_ - 1)));
                        ++buffer.m_utilBytesCount3_;
                    }
                    buffer.m_utilCount3_ = 0;
                }
                buffer.m_utilBytes3_ = RuleBasedCollator.append(buffer.m_utilBytes3_, buffer.m_utilBytesCount3_, (byte)tertiary);
                ++buffer.m_utilBytesCount3_;
            }
        }
    }

    private final void doQuaternaryBytes(boolean isCodePointHiragana, int commonBottom4, int bottomCount4, byte hiragana4, CollationBuffer buffer) {
        if (isCodePointHiragana) {
            if (buffer.m_utilCount4_ > 0) {
                while (buffer.m_utilCount4_ > bottomCount4) {
                    buffer.m_utilBytes4_ = RuleBasedCollator.append(buffer.m_utilBytes4_, buffer.m_utilBytesCount4_, (byte)(commonBottom4 + bottomCount4));
                    ++buffer.m_utilBytesCount4_;
                    buffer.m_utilCount4_ -= bottomCount4;
                }
                buffer.m_utilBytes4_ = RuleBasedCollator.append(buffer.m_utilBytes4_, buffer.m_utilBytesCount4_, (byte)(commonBottom4 + (buffer.m_utilCount4_ - 1)));
                ++buffer.m_utilBytesCount4_;
                buffer.m_utilCount4_ = 0;
            }
            buffer.m_utilBytes4_ = RuleBasedCollator.append(buffer.m_utilBytes4_, buffer.m_utilBytesCount4_, hiragana4);
            ++buffer.m_utilBytesCount4_;
        } else {
            ++buffer.m_utilCount4_;
        }
    }

    private final void getSortKeyBytes(String source, boolean doFrench, byte hiragana4, int commonBottom4, int bottomCount4, CollationBuffer buffer) {
        int ce2;
        int backupDecomposition = this.getDecomposition();
        this.internalSetDecomposition(16);
        buffer.m_srcUtilIter_.setText(source);
        buffer.m_srcUtilColEIter_.setText(buffer.m_srcUtilIter_);
        buffer.m_utilFrenchStart_ = -1;
        buffer.m_utilFrenchEnd_ = -1;
        boolean doShift = false;
        boolean notIsContinuation = false;
        int leadPrimary = 0;
        int caseShift = 0;
        while ((ce2 = buffer.m_srcUtilColEIter_.next()) != -1) {
            if (ce2 == 0) continue;
            notIsContinuation = !RuleBasedCollator.isContinuation(ce2);
            boolean isPrimaryByteIgnorable = (ce2 & 0xFFFF0000) == 0;
            boolean isSmallerThanVariableTop = ce2 >>> 16 <= this.m_variableTopValue_;
            boolean bl2 = doShift = this.m_isAlternateHandlingShifted_ && (notIsContinuation && isSmallerThanVariableTop && !isPrimaryByteIgnorable || !notIsContinuation && doShift) || doShift && isPrimaryByteIgnorable;
            if (doShift && isPrimaryByteIgnorable) continue;
            leadPrimary = this.doPrimaryBytes(ce2, notIsContinuation, doShift, leadPrimary, commonBottom4, bottomCount4, buffer);
            if (doShift) continue;
            if (buffer.m_utilCompare2_) {
                this.doSecondaryBytes(ce2, notIsContinuation, doFrench, buffer);
            }
            int t2 = ce2 & 0xFF;
            if (!notIsContinuation) {
                t2 = ce2 & 0xFFFFFF3F;
            }
            if (buffer.m_utilCompare0_ && (!isPrimaryByteIgnorable || buffer.m_utilCompare2_)) {
                caseShift = this.doCaseBytes(t2, notIsContinuation, caseShift, buffer);
            } else if (notIsContinuation) {
                t2 ^= this.m_caseSwitch_;
            }
            t2 &= this.m_mask3_;
            if (buffer.m_utilCompare3_) {
                this.doTertiaryBytes(t2, notIsContinuation, buffer);
            }
            if (!buffer.m_utilCompare4_ || !notIsContinuation) continue;
            this.doQuaternaryBytes(buffer.m_srcUtilColEIter_.m_isCodePointHiragana_, commonBottom4, bottomCount4, hiragana4, buffer);
        }
        this.internalSetDecomposition(backupDecomposition);
        if (buffer.m_utilFrenchStart_ != -1) {
            RuleBasedCollator.reverseBuffer(buffer.m_utilBytes2_, buffer.m_utilFrenchStart_, buffer.m_utilFrenchEnd_);
        }
    }

    private final void getSortKey(String source, boolean doFrench, int commonBottom4, int bottomCount4, RawCollationKey key, CollationBuffer buffer) {
        if (buffer.m_utilCompare2_) {
            RuleBasedCollator.doSecondary(doFrench, buffer);
        }
        if (buffer.m_utilCompare0_) {
            RuleBasedCollator.doCase(buffer);
        }
        if (buffer.m_utilCompare3_) {
            this.doTertiary(buffer);
            if (buffer.m_utilCompare4_) {
                this.doQuaternary(commonBottom4, bottomCount4, buffer);
                if (buffer.m_utilCompare5_) {
                    RuleBasedCollator.doIdentical(source, buffer);
                }
            }
        }
        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)0);
        ++buffer.m_utilBytesCount1_;
        key.set(buffer.m_utilBytes1_, 0, buffer.m_utilBytesCount1_);
    }

    private static final void doFrench(CollationBuffer buffer) {
        for (int i2 = 0; i2 < buffer.m_utilBytesCount2_; ++i2) {
            byte s2 = buffer.m_utilBytes2_[buffer.m_utilBytesCount2_ - i2 - 1];
            if (s2 == 5) {
                ++buffer.m_utilCount2_;
                continue;
            }
            if (buffer.m_utilCount2_ > 0) {
                if ((s2 & 0xFF) > 5) {
                    while (buffer.m_utilCount2_ > 64) {
                        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)70);
                        ++buffer.m_utilBytesCount1_;
                        buffer.m_utilCount2_ -= 64;
                    }
                    buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)(134 - (buffer.m_utilCount2_ - 1)));
                    ++buffer.m_utilBytesCount1_;
                } else {
                    while (buffer.m_utilCount2_ > 64) {
                        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)69);
                        ++buffer.m_utilBytesCount1_;
                        buffer.m_utilCount2_ -= 64;
                    }
                    buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)(5 + (buffer.m_utilCount2_ - 1)));
                    ++buffer.m_utilBytesCount1_;
                }
                buffer.m_utilCount2_ = 0;
            }
            buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, s2);
            ++buffer.m_utilBytesCount1_;
        }
        if (buffer.m_utilCount2_ > 0) {
            while (buffer.m_utilCount2_ > 64) {
                buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)69);
                ++buffer.m_utilBytesCount1_;
                buffer.m_utilCount2_ -= 64;
            }
            buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)(5 + (buffer.m_utilCount2_ - 1)));
            ++buffer.m_utilBytesCount1_;
        }
    }

    private static final void doSecondary(boolean doFrench, CollationBuffer buffer) {
        if (buffer.m_utilCount2_ > 0) {
            while (buffer.m_utilCount2_ > 64) {
                buffer.m_utilBytes2_ = RuleBasedCollator.append(buffer.m_utilBytes2_, buffer.m_utilBytesCount2_, (byte)69);
                ++buffer.m_utilBytesCount2_;
                buffer.m_utilCount2_ -= 64;
            }
            buffer.m_utilBytes2_ = RuleBasedCollator.append(buffer.m_utilBytes2_, buffer.m_utilBytesCount2_, (byte)(5 + (buffer.m_utilCount2_ - 1)));
            ++buffer.m_utilBytesCount2_;
        }
        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)1);
        ++buffer.m_utilBytesCount1_;
        if (doFrench) {
            RuleBasedCollator.doFrench(buffer);
        } else {
            if (buffer.m_utilBytes1_.length <= buffer.m_utilBytesCount1_ + buffer.m_utilBytesCount2_) {
                buffer.m_utilBytes1_ = RuleBasedCollator.increase(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, buffer.m_utilBytesCount2_);
            }
            System.arraycopy(buffer.m_utilBytes2_, 0, buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, buffer.m_utilBytesCount2_);
            buffer.m_utilBytesCount1_ += buffer.m_utilBytesCount2_;
        }
    }

    private static final byte[] increase(byte[] buffer, int size, int incrementsize) {
        byte[] result = new byte[buffer.length + incrementsize];
        System.arraycopy(buffer, 0, result, 0, size);
        return result;
    }

    private static final int[] increase(int[] buffer, int size, int incrementsize) {
        int[] result = new int[buffer.length + incrementsize];
        System.arraycopy(buffer, 0, result, 0, size);
        return result;
    }

    private static final void doCase(CollationBuffer buffer) {
        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)1);
        ++buffer.m_utilBytesCount1_;
        if (buffer.m_utilBytes1_.length <= buffer.m_utilBytesCount1_ + buffer.m_utilBytesCount0_) {
            buffer.m_utilBytes1_ = RuleBasedCollator.increase(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, buffer.m_utilBytesCount0_);
        }
        System.arraycopy(buffer.m_utilBytes0_, 0, buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, buffer.m_utilBytesCount0_);
        buffer.m_utilBytesCount1_ += buffer.m_utilBytesCount0_;
    }

    private final void doTertiary(CollationBuffer buffer) {
        if (buffer.m_utilCount3_ > 0) {
            if (this.m_common3_ != 5) {
                while (buffer.m_utilCount3_ >= this.m_topCount3_) {
                    buffer.m_utilBytes3_ = RuleBasedCollator.append(buffer.m_utilBytes3_, buffer.m_utilBytesCount3_, (byte)(this.m_top3_ - this.m_topCount3_));
                    ++buffer.m_utilBytesCount3_;
                    buffer.m_utilCount3_ -= this.m_topCount3_;
                }
                buffer.m_utilBytes3_ = RuleBasedCollator.append(buffer.m_utilBytes3_, buffer.m_utilBytesCount3_, (byte)(this.m_top3_ - buffer.m_utilCount3_));
                ++buffer.m_utilBytesCount3_;
            } else {
                while (buffer.m_utilCount3_ > this.m_bottomCount3_) {
                    buffer.m_utilBytes3_ = RuleBasedCollator.append(buffer.m_utilBytes3_, buffer.m_utilBytesCount3_, (byte)(this.m_bottom3_ + this.m_bottomCount3_));
                    ++buffer.m_utilBytesCount3_;
                    buffer.m_utilCount3_ -= this.m_bottomCount3_;
                }
                buffer.m_utilBytes3_ = RuleBasedCollator.append(buffer.m_utilBytes3_, buffer.m_utilBytesCount3_, (byte)(this.m_bottom3_ + (buffer.m_utilCount3_ - 1)));
                ++buffer.m_utilBytesCount3_;
            }
        }
        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)1);
        ++buffer.m_utilBytesCount1_;
        if (buffer.m_utilBytes1_.length <= buffer.m_utilBytesCount1_ + buffer.m_utilBytesCount3_) {
            buffer.m_utilBytes1_ = RuleBasedCollator.increase(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, buffer.m_utilBytesCount3_);
        }
        System.arraycopy(buffer.m_utilBytes3_, 0, buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, buffer.m_utilBytesCount3_);
        buffer.m_utilBytesCount1_ += buffer.m_utilBytesCount3_;
    }

    private final void doQuaternary(int commonbottom4, int bottomcount4, CollationBuffer buffer) {
        if (buffer.m_utilCount4_ > 0) {
            while (buffer.m_utilCount4_ > bottomcount4) {
                buffer.m_utilBytes4_ = RuleBasedCollator.append(buffer.m_utilBytes4_, buffer.m_utilBytesCount4_, (byte)(commonbottom4 + bottomcount4));
                ++buffer.m_utilBytesCount4_;
                buffer.m_utilCount4_ -= bottomcount4;
            }
            buffer.m_utilBytes4_ = RuleBasedCollator.append(buffer.m_utilBytes4_, buffer.m_utilBytesCount4_, (byte)(commonbottom4 + (buffer.m_utilCount4_ - 1)));
            ++buffer.m_utilBytesCount4_;
        }
        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)1);
        ++buffer.m_utilBytesCount1_;
        if (buffer.m_utilBytes1_.length <= buffer.m_utilBytesCount1_ + buffer.m_utilBytesCount4_) {
            buffer.m_utilBytes1_ = RuleBasedCollator.increase(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, buffer.m_utilBytesCount4_);
        }
        System.arraycopy(buffer.m_utilBytes4_, 0, buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, buffer.m_utilBytesCount4_);
        buffer.m_utilBytesCount1_ += buffer.m_utilBytesCount4_;
    }

    private static final void doIdentical(String source, CollationBuffer buffer) {
        int isize = BOCU.getCompressionLength(source);
        buffer.m_utilBytes1_ = RuleBasedCollator.append(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, (byte)1);
        ++buffer.m_utilBytesCount1_;
        if (buffer.m_utilBytes1_.length <= buffer.m_utilBytesCount1_ + isize) {
            buffer.m_utilBytes1_ = RuleBasedCollator.increase(buffer.m_utilBytes1_, buffer.m_utilBytesCount1_, 1 + isize);
        }
        buffer.m_utilBytesCount1_ = BOCU.compress(source, buffer.m_utilBytes1_, buffer.m_utilBytesCount1_);
    }

    private final int getFirstUnmatchedOffset(String source, String target) {
        int tlength;
        int result = 0;
        int slength = source.length();
        int minlength = slength;
        if (minlength > (tlength = target.length())) {
            minlength = tlength;
        }
        while (result < minlength && source.charAt(result) == target.charAt(result)) {
            ++result;
        }
        if (result > 0) {
            char schar = '\u0000';
            char tchar = '\u0000';
            if (result < minlength) {
                schar = source.charAt(result);
                tchar = target.charAt(result);
            } else {
                schar = source.charAt(minlength - 1);
                if (this.isUnsafe(schar)) {
                    tchar = schar;
                } else {
                    if (slength == tlength) {
                        return result;
                    }
                    if (slength < tlength) {
                        tchar = target.charAt(result);
                    } else {
                        schar = source.charAt(result);
                    }
                }
            }
            if (this.isUnsafe(schar) || this.isUnsafe(tchar)) {
                while (--result > 0 && this.isUnsafe(source.charAt(result))) {
                }
            }
        }
        return result;
    }

    private static final byte[] append(byte[] array, int appendindex, byte value) {
        try {
            array[appendindex] = value;
        }
        catch (ArrayIndexOutOfBoundsException e2) {
            array = RuleBasedCollator.increase(array, appendindex, 128);
            array[appendindex] = value;
        }
        return array;
    }

    private final int compareBySortKeys(String source, String target, CollationBuffer buffer) {
        buffer.m_utilRawCollationKey_ = this.getRawCollationKey(source, buffer.m_utilRawCollationKey_);
        RawCollationKey targetkey = this.getRawCollationKey(target, null);
        return buffer.m_utilRawCollationKey_.compareTo(targetkey);
    }

    private final int doPrimaryCompare(boolean doHiragana4, int lowestpvalue, String source, String target, int textoffset, CollationBuffer buffer) {
        block11: {
            int torder;
            int sorder;
            buffer.m_srcUtilIter_.setText(source);
            buffer.m_srcUtilColEIter_.setText(buffer.m_srcUtilIter_, textoffset);
            buffer.m_tgtUtilIter_.setText(target);
            buffer.m_tgtUtilColEIter_.setText(buffer.m_tgtUtilIter_, textoffset);
            if (!this.m_isAlternateHandlingShifted_) {
                int hiraganaresult;
                block10: {
                    int torder2;
                    int sorder2;
                    hiraganaresult = 0;
                    while (true) {
                        sorder2 = 0;
                        do {
                            sorder2 = buffer.m_srcUtilColEIter_.next();
                            buffer.m_srcUtilCEBuffer_ = RuleBasedCollator.append(buffer.m_srcUtilCEBuffer_, buffer.m_srcUtilCEBufferSize_, sorder2);
                            ++buffer.m_srcUtilCEBufferSize_;
                        } while ((sorder2 &= 0xFFFF0000) == 0);
                        torder2 = 0;
                        do {
                            torder2 = buffer.m_tgtUtilColEIter_.next();
                            buffer.m_tgtUtilCEBuffer_ = RuleBasedCollator.append(buffer.m_tgtUtilCEBuffer_, buffer.m_tgtUtilCEBufferSize_, torder2);
                            ++buffer.m_tgtUtilCEBufferSize_;
                        } while ((torder2 &= 0xFFFF0000) == 0);
                        if (!RuleBasedCollator.isContinuation(sorder2) && this.m_leadBytePermutationTable_ != null) {
                            sorder2 = this.m_leadBytePermutationTable_[((sorder2 >> 24) + 256) % 256] << 24 | sorder2 & 0xFFFFFF;
                            torder2 = this.m_leadBytePermutationTable_[((torder2 >> 24) + 256) % 256] << 24 | torder2 & 0xFFFFFF;
                        }
                        if (sorder2 != torder2) break;
                        if (buffer.m_srcUtilCEBuffer_[buffer.m_srcUtilCEBufferSize_ - 1] == -1) {
                            if (buffer.m_tgtUtilCEBuffer_[buffer.m_tgtUtilCEBufferSize_ - 1] != -1) {
                                return -1;
                            }
                            break block10;
                        }
                        if (buffer.m_tgtUtilCEBuffer_[buffer.m_tgtUtilCEBufferSize_ - 1] == -1) {
                            return 1;
                        }
                        if (!doHiragana4 || hiraganaresult != 0 || buffer.m_srcUtilColEIter_.m_isCodePointHiragana_ == buffer.m_tgtUtilColEIter_.m_isCodePointHiragana_) continue;
                        if (buffer.m_srcUtilColEIter_.m_isCodePointHiragana_) {
                            hiraganaresult = -1;
                            continue;
                        }
                        hiraganaresult = 1;
                    }
                    return RuleBasedCollator.endPrimaryCompare(sorder2, torder2, buffer);
                }
                return hiraganaresult;
            }
            while ((sorder = RuleBasedCollator.getPrimaryShiftedCompareCE(buffer.m_srcUtilColEIter_, lowestpvalue, true, buffer)) == (torder = RuleBasedCollator.getPrimaryShiftedCompareCE(buffer.m_tgtUtilColEIter_, lowestpvalue, false, buffer))) {
                if (buffer.m_srcUtilCEBuffer_[buffer.m_srcUtilCEBufferSize_ - 1] != -1) continue;
                break block11;
            }
            return RuleBasedCollator.endPrimaryCompare(sorder, torder, buffer);
        }
        return 0;
    }

    private static final int endPrimaryCompare(int sorder, int torder, CollationBuffer buffer) {
        boolean isSourceNullOrder = buffer.m_srcUtilCEBuffer_[buffer.m_srcUtilCEBufferSize_ - 1] == -1;
        boolean isTargetNullOrder = buffer.m_tgtUtilCEBuffer_[buffer.m_tgtUtilCEBufferSize_ - 1] == -1;
        buffer.m_srcUtilCEBufferSize_ = -1;
        buffer.m_tgtUtilCEBufferSize_ = -1;
        if (isSourceNullOrder) {
            return -1;
        }
        if (isTargetNullOrder) {
            return 1;
        }
        if ((sorder >>>= 16) < (torder >>>= 16)) {
            return -1;
        }
        return 1;
    }

    private static final int getPrimaryShiftedCompareCE(CollationElementIterator coleiter, int lowestpvalue, boolean isSrc, CollationBuffer buffer) {
        boolean shifted = false;
        int result = 0;
        int[] cebuffer = buffer.m_srcUtilCEBuffer_;
        int cebuffersize = buffer.m_srcUtilCEBufferSize_;
        if (!isSrc) {
            cebuffer = buffer.m_tgtUtilCEBuffer_;
            cebuffersize = buffer.m_tgtUtilCEBufferSize_;
        }
        while (true) {
            if ((result = coleiter.next()) == -1) {
                cebuffer = RuleBasedCollator.append(cebuffer, cebuffersize, result);
                ++cebuffersize;
                break;
            }
            if (result == 0 || shifted && (result & 0xFFFF0000) == 0) continue;
            if (RuleBasedCollator.isContinuation(result)) {
                if ((result & 0xFFFF0000) != 0) {
                    if (shifted) {
                        result = result & 0xFFFF0000 | 0xC0;
                        cebuffer = RuleBasedCollator.append(cebuffer, cebuffersize, result);
                        ++cebuffersize;
                        continue;
                    }
                    cebuffer = RuleBasedCollator.append(cebuffer, cebuffersize, result);
                    ++cebuffersize;
                    break;
                }
                if (shifted) continue;
                cebuffer = RuleBasedCollator.append(cebuffer, cebuffersize, result);
                ++cebuffersize;
                continue;
            }
            if (Utility.compareUnsigned(result & 0xFFFF0000, lowestpvalue) > 0) {
                cebuffer = RuleBasedCollator.append(cebuffer, cebuffersize, result);
                ++cebuffersize;
                break;
            }
            if ((result & 0xFFFF0000) != 0) {
                shifted = true;
                cebuffer = RuleBasedCollator.append(cebuffer, cebuffersize, result &= 0xFFFF0000);
                ++cebuffersize;
                continue;
            }
            cebuffer = RuleBasedCollator.append(cebuffer, cebuffersize, result);
            ++cebuffersize;
            shifted = false;
        }
        if (isSrc) {
            buffer.m_srcUtilCEBuffer_ = cebuffer;
            buffer.m_srcUtilCEBufferSize_ = cebuffersize;
        } else {
            buffer.m_tgtUtilCEBuffer_ = cebuffer;
            buffer.m_tgtUtilCEBufferSize_ = cebuffersize;
        }
        return result &= 0xFFFF0000;
    }

    private static final int[] append(int[] array, int appendindex, int value) {
        if (appendindex + 1 >= array.length) {
            array = RuleBasedCollator.increase(array, appendindex, 512);
        }
        array[appendindex] = value;
        return array;
    }

    private static final int doSecondaryCompare(boolean doFrench, CollationBuffer buffer) {
        block9: {
            int torder;
            int sorder;
            if (!doFrench) {
                int torder2;
                int sorder2;
                int toffset;
                int soffset;
                block8: {
                    soffset = 0;
                    toffset = 0;
                    do {
                        sorder2 = 0;
                        while (sorder2 == 0) {
                            sorder2 = buffer.m_srcUtilCEBuffer_[soffset++] & 0xFF00;
                        }
                        torder2 = 0;
                        while (torder2 == 0) {
                            torder2 = buffer.m_tgtUtilCEBuffer_[toffset++] & 0xFF00;
                        }
                        if (sorder2 != torder2) break block8;
                        if (buffer.m_srcUtilCEBuffer_[soffset - 1] != -1) continue;
                        if (buffer.m_tgtUtilCEBuffer_[toffset - 1] != -1) {
                            return -1;
                        }
                        break block9;
                    } while (buffer.m_tgtUtilCEBuffer_[toffset - 1] != -1);
                    return 1;
                }
                if (buffer.m_srcUtilCEBuffer_[soffset - 1] == -1) {
                    return -1;
                }
                if (buffer.m_tgtUtilCEBuffer_[toffset - 1] == -1) {
                    return 1;
                }
                return sorder2 < torder2 ? -1 : 1;
            }
            buffer.m_srcUtilContOffset_ = 0;
            buffer.m_tgtUtilContOffset_ = 0;
            buffer.m_srcUtilOffset_ = buffer.m_srcUtilCEBufferSize_ - 2;
            buffer.m_tgtUtilOffset_ = buffer.m_tgtUtilCEBufferSize_ - 2;
            while ((sorder = RuleBasedCollator.getSecondaryFrenchCE(true, buffer)) == (torder = RuleBasedCollator.getSecondaryFrenchCE(false, buffer))) {
                if ((buffer.m_srcUtilOffset_ >= 0 || buffer.m_tgtUtilOffset_ >= 0) && (buffer.m_srcUtilOffset_ < 0 || buffer.m_srcUtilCEBuffer_[buffer.m_srcUtilOffset_] != -1)) continue;
                break block9;
            }
            return sorder < torder ? -1 : 1;
        }
        return 0;
    }

    private static final int getSecondaryFrenchCE(boolean isSrc, CollationBuffer buffer) {
        int result = 0;
        int offset = buffer.m_srcUtilOffset_;
        int continuationoffset = buffer.m_srcUtilContOffset_;
        int[] cebuffer = buffer.m_srcUtilCEBuffer_;
        if (!isSrc) {
            offset = buffer.m_tgtUtilOffset_;
            continuationoffset = buffer.m_tgtUtilContOffset_;
            cebuffer = buffer.m_tgtUtilCEBuffer_;
        }
        while (result == 0 && offset >= 0) {
            if (continuationoffset == 0) {
                result = cebuffer[offset];
                while (RuleBasedCollator.isContinuation(cebuffer[offset--])) {
                }
                if (RuleBasedCollator.isContinuation(cebuffer[offset + 1])) {
                    continuationoffset = offset;
                    offset += 2;
                }
            } else if (!RuleBasedCollator.isContinuation(result = cebuffer[offset++])) {
                offset = continuationoffset;
                continuationoffset = 0;
                continue;
            }
            result &= 0xFF00;
        }
        if (isSrc) {
            buffer.m_srcUtilOffset_ = offset;
            buffer.m_srcUtilContOffset_ = continuationoffset;
        } else {
            buffer.m_tgtUtilOffset_ = offset;
            buffer.m_tgtUtilContOffset_ = continuationoffset;
        }
        return result;
    }

    private final int doCaseCompare(CollationBuffer buffer) {
        block9: {
            int torder;
            int sorder;
            int soffset;
            block8: {
                soffset = 0;
                int toffset = 0;
                do {
                    sorder = 0;
                    torder = 0;
                    while ((sorder & 0x3F) == 0) {
                        if (!RuleBasedCollator.isContinuation(sorder = buffer.m_srcUtilCEBuffer_[soffset++]) && ((sorder & 0xFFFF0000) != 0 || buffer.m_utilCompare2_)) {
                            sorder &= 0xFF;
                            sorder ^= this.m_caseSwitch_;
                            continue;
                        }
                        sorder = 0;
                    }
                    while ((torder & 0x3F) == 0) {
                        if (!RuleBasedCollator.isContinuation(torder = buffer.m_tgtUtilCEBuffer_[toffset++]) && ((torder & 0xFFFF0000) != 0 || buffer.m_utilCompare2_)) {
                            torder &= 0xFF;
                            torder ^= this.m_caseSwitch_;
                            continue;
                        }
                        torder = 0;
                    }
                    if ((sorder &= 0xC0) != (torder &= 0xC0)) break block8;
                    if (buffer.m_srcUtilCEBuffer_[soffset - 1] != -1) continue;
                    if (buffer.m_tgtUtilCEBuffer_[toffset - 1] != -1) {
                        return -1;
                    }
                    break block9;
                } while (buffer.m_tgtUtilCEBuffer_[toffset - 1] != -1);
                return 1;
            }
            if (buffer.m_srcUtilCEBuffer_[soffset - 1] == -1) {
                return -1;
            }
            if (buffer.m_tgtUtilCEBuffer_[soffset - 1] == -1) {
                return 1;
            }
            return sorder < torder ? -1 : 1;
        }
        return 0;
    }

    private final int doTertiaryCompare(CollationBuffer buffer) {
        block9: {
            int torder;
            int sorder;
            int toffset;
            int soffset;
            block8: {
                soffset = 0;
                toffset = 0;
                do {
                    sorder = 0;
                    torder = 0;
                    while ((sorder & 0x3F) == 0) {
                        if (!RuleBasedCollator.isContinuation(sorder = buffer.m_srcUtilCEBuffer_[soffset++] & this.m_mask3_)) {
                            sorder ^= this.m_caseSwitch_;
                            continue;
                        }
                        sorder &= 0x3F;
                    }
                    while ((torder & 0x3F) == 0) {
                        if (!RuleBasedCollator.isContinuation(torder = buffer.m_tgtUtilCEBuffer_[toffset++] & this.m_mask3_)) {
                            torder ^= this.m_caseSwitch_;
                            continue;
                        }
                        torder &= 0x3F;
                    }
                    if (sorder != torder) break block8;
                    if (buffer.m_srcUtilCEBuffer_[soffset - 1] != -1) continue;
                    if (buffer.m_tgtUtilCEBuffer_[toffset - 1] != -1) {
                        return -1;
                    }
                    break block9;
                } while (buffer.m_tgtUtilCEBuffer_[toffset - 1] != -1);
                return 1;
            }
            if (buffer.m_srcUtilCEBuffer_[soffset - 1] == -1) {
                return -1;
            }
            if (buffer.m_tgtUtilCEBuffer_[toffset - 1] == -1) {
                return 1;
            }
            return sorder < torder ? -1 : 1;
        }
        return 0;
    }

    private final int doQuaternaryCompare(int lowestpvalue, CollationBuffer buffer) {
        block11: {
            int torder;
            int sorder;
            int toffset;
            int soffset;
            block10: {
                boolean sShifted = true;
                boolean tShifted = true;
                soffset = 0;
                toffset = 0;
                do {
                    sorder = 0;
                    torder = 0;
                    while (sorder == 0 || RuleBasedCollator.isContinuation(sorder) && !sShifted) {
                        if (RuleBasedCollator.isContinuation(sorder = buffer.m_srcUtilCEBuffer_[soffset++])) {
                            if (sShifted) continue;
                            continue;
                        }
                        if (Utility.compareUnsigned(sorder, lowestpvalue) > 0 || (sorder & 0xFFFF0000) == 0) {
                            sorder = -65536;
                            sShifted = false;
                            continue;
                        }
                        sShifted = true;
                    }
                    sorder >>>= 16;
                    while (torder == 0 || RuleBasedCollator.isContinuation(torder) && !tShifted) {
                        if (RuleBasedCollator.isContinuation(torder = buffer.m_tgtUtilCEBuffer_[toffset++])) {
                            if (tShifted) continue;
                            continue;
                        }
                        if (Utility.compareUnsigned(torder, lowestpvalue) > 0 || (torder & 0xFFFF0000) == 0) {
                            torder = -65536;
                            tShifted = false;
                            continue;
                        }
                        tShifted = true;
                    }
                    if (sorder != (torder >>>= 16)) break block10;
                    if (buffer.m_srcUtilCEBuffer_[soffset - 1] != -1) continue;
                    if (buffer.m_tgtUtilCEBuffer_[toffset - 1] != -1) {
                        return -1;
                    }
                    break block11;
                } while (buffer.m_tgtUtilCEBuffer_[toffset - 1] != -1);
                return 1;
            }
            if (buffer.m_srcUtilCEBuffer_[soffset - 1] == -1) {
                return -1;
            }
            if (buffer.m_tgtUtilCEBuffer_[toffset - 1] == -1) {
                return 1;
            }
            return sorder < torder ? -1 : 1;
        }
        return 0;
    }

    private static final int doIdenticalCompare(String source, String target, int offset, boolean normalize) {
        if (normalize) {
            if (Normalizer.quickCheck(source, Normalizer.NFD, 0) != Normalizer.YES) {
                source = Normalizer.decompose(source, false);
            }
            if (Normalizer.quickCheck(target, Normalizer.NFD, 0) != Normalizer.YES) {
                target = Normalizer.decompose(target, false);
            }
            offset = 0;
        }
        return RuleBasedCollator.doStringCompare(source, target, offset);
    }

    private static final int doStringCompare(String source, String target, int offset) {
        char schar = '\u0000';
        char tchar = '\u0000';
        int slength = source.length();
        int tlength = target.length();
        int minlength = Math.min(slength, tlength);
        while (offset < minlength && (schar = source.charAt(offset)) == (tchar = target.charAt(offset++))) {
        }
        if (schar == tchar && offset == minlength) {
            if (slength > minlength) {
                return 1;
            }
            if (tlength > minlength) {
                return -1;
            }
            return 0;
        }
        if (schar >= '\ud800' && tchar >= '\ud800') {
            schar = RuleBasedCollator.fixupUTF16(schar);
            tchar = RuleBasedCollator.fixupUTF16(tchar);
        }
        return schar < tchar ? -1 : 1;
    }

    private static final char fixupUTF16(char ch) {
        ch = ch >= '\ue000' ? (char)(ch - 2048) : (char)(ch + 8192);
        return ch;
    }

    private void buildPermutationTable() {
        int codeIndex;
        if (this.m_reorderCodes_ == null || this.m_reorderCodes_.length == 0 || this.m_reorderCodes_.length == 1 && this.m_reorderCodes_[0] == 103) {
            this.m_leadBytePermutationTable_ = null;
            return;
        }
        if (this.m_reorderCodes_[0] == -1) {
            if (this.m_reorderCodes_.length != 1) {
                throw new IllegalArgumentException("Illegal collation reorder codes - default reorder code must be the only code in the list.");
            }
            if (this.m_defaultReorderCodes_ == null || this.m_defaultReorderCodes_.length == 0) {
                this.m_leadBytePermutationTable_ = null;
                return;
            }
            this.m_reorderCodes_ = (int[])this.m_defaultReorderCodes_.clone();
        }
        int toBottom = 3;
        int toTop = 228;
        boolean[] permutationSlotFilled = new boolean[256];
        boolean[] newLeadByteUsed = new boolean[256];
        if (this.m_leadBytePermutationTable_ == null) {
            this.m_leadBytePermutationTable_ = new byte[256];
        }
        int[] internalReorderCodes = new int[this.m_reorderCodes_.length + 5];
        for (codeIndex = 0; codeIndex < 5; ++codeIndex) {
            internalReorderCodes[codeIndex] = 4096 + codeIndex;
        }
        for (codeIndex = 0; codeIndex < this.m_reorderCodes_.length; ++codeIndex) {
            internalReorderCodes[codeIndex + 5] = this.m_reorderCodes_[codeIndex];
            if (this.m_reorderCodes_[codeIndex] < 4096 || this.m_reorderCodes_[codeIndex] >= 4101) continue;
            internalReorderCodes[this.m_reorderCodes_[codeIndex] - 4096] = 4102;
        }
        boolean fromTheBottom = true;
        int reorderCodesIndex = -1;
        for (int reorderCodesCount = 0; reorderCodesCount < internalReorderCodes.length; ++reorderCodesCount) {
            int next = internalReorderCodes[reorderCodesIndex += fromTheBottom ? 1 : -1];
            if (next == 4102) continue;
            if (next == 103) {
                if (!fromTheBottom) {
                    this.m_leadBytePermutationTable_ = null;
                    throw new IllegalArgumentException("Illegal collation reorder codes - two \"from the end\" markers.");
                }
                fromTheBottom = false;
                reorderCodesIndex = internalReorderCodes.length;
                continue;
            }
            int[] leadBytes = LEADBYTE_CONSTANTS_.getLeadBytesForReorderCode(next);
            if (fromTheBottom) {
                for (int leadByte : leadBytes) {
                    if (permutationSlotFilled[leadByte]) {
                        this.m_leadBytePermutationTable_ = null;
                        throw new IllegalArgumentException("Illegal reorder codes specified - multiple codes with the same lead byte.");
                    }
                    this.m_leadBytePermutationTable_[leadByte] = (byte)toBottom;
                    newLeadByteUsed[toBottom] = true;
                    permutationSlotFilled[leadByte] = true;
                    ++toBottom;
                }
                continue;
            }
            for (int leadByteIndex = leadBytes.length - 1; leadByteIndex >= 0; --leadByteIndex) {
                int leadByte = leadBytes[leadByteIndex];
                if (permutationSlotFilled[leadByte]) {
                    this.m_leadBytePermutationTable_ = null;
                    throw new IllegalArgumentException("Illegal reorder codes specified - multiple codes with the same lead byte.");
                }
                this.m_leadBytePermutationTable_[leadByte] = (byte)toTop;
                newLeadByteUsed[toTop] = true;
                permutationSlotFilled[leadByte] = true;
                --toTop;
            }
        }
        int reorderCode = 0;
        for (int i2 = 0; i2 < 256; ++i2) {
            if (permutationSlotFilled[i2]) continue;
            while (newLeadByteUsed[reorderCode]) {
                if (reorderCode > 255) {
                    throw new IllegalArgumentException("Unable to fill collation reordering table slots - no available reordering code.");
                }
                ++reorderCode;
            }
            this.m_leadBytePermutationTable_[i2] = (byte)reorderCode;
            permutationSlotFilled[i2] = true;
            newLeadByteUsed[reorderCode] = true;
        }
        this.latinOneRegenTable_ = true;
        this.updateInternalState();
    }

    private void updateInternalState() {
        this.m_caseSwitch_ = this.m_caseFirst_ == 25 ? 192 : 0;
        if (this.m_isCaseLevel_ || this.m_caseFirst_ == 16) {
            this.m_mask3_ = 63;
            this.m_common3_ = 5;
            this.m_addition3_ = 128;
            this.m_top3_ = 133;
            this.m_bottom3_ = 5;
        } else {
            this.m_mask3_ = 255;
            this.m_addition3_ = 64;
            if (this.m_caseFirst_ == 25) {
                this.m_common3_ = 197;
                this.m_top3_ = 197;
                this.m_bottom3_ = 134;
            } else {
                this.m_common3_ = 5;
                this.m_top3_ = 69;
                this.m_bottom3_ = 5;
            }
        }
        int total3 = this.m_top3_ - this.m_bottom3_ - 1;
        this.m_topCount3_ = (int)(0.667 * (double)total3);
        this.m_bottomCount3_ = total3 - this.m_topCount3_;
        this.m_isSimple3_ = !this.m_isCaseLevel_ && this.getStrength() == 2 && !this.m_isFrenchCollation_ && !this.m_isAlternateHandlingShifted_;
        if (!(this.m_isCaseLevel_ || this.getStrength() > 2 || this.m_isNumericCollation_ || this.m_isAlternateHandlingShifted_ || this.latinOneFailed_)) {
            if (this.latinOneCEs_ == null || this.latinOneRegenTable_) {
                if (this.setUpLatinOne()) {
                    this.latinOneUse_ = true;
                } else {
                    this.latinOneUse_ = false;
                    this.latinOneFailed_ = true;
                }
                this.latinOneRegenTable_ = false;
            } else {
                this.latinOneUse_ = true;
            }
        } else {
            this.latinOneUse_ = false;
        }
    }

    private final void init() {
        this.m_minUnsafe_ = '\u0000';
        while (this.m_minUnsafe_ < '\u0300' && !this.isUnsafe(this.m_minUnsafe_)) {
            this.m_minUnsafe_ = (char)(this.m_minUnsafe_ + '\u0001');
        }
        this.m_minContractionEnd_ = '\u0000';
        while (this.m_minContractionEnd_ < '\u0300' && !this.isContractionEnd(this.m_minContractionEnd_)) {
            this.m_minContractionEnd_ = (char)(this.m_minContractionEnd_ + '\u0001');
        }
        this.latinOneFailed_ = true;
        this.setStrength(this.m_defaultStrength_);
        this.setDecomposition(this.m_defaultDecomposition_);
        this.m_variableTopValue_ = this.m_defaultVariableTopValue_;
        this.m_isFrenchCollation_ = this.m_defaultIsFrenchCollation_;
        this.m_isAlternateHandlingShifted_ = this.m_defaultIsAlternateHandlingShifted_;
        this.m_isCaseLevel_ = this.m_defaultIsCaseLevel_;
        this.m_caseFirst_ = this.m_defaultCaseFirst_;
        this.m_isHiragana4_ = this.m_defaultIsHiragana4_;
        this.m_isNumericCollation_ = this.m_defaultIsNumericCollation_;
        this.latinOneFailed_ = false;
        this.m_reorderCodes_ = (int[])(this.m_defaultReorderCodes_ != null ? (int[])this.m_defaultReorderCodes_.clone() : null);
        this.updateInternalState();
    }

    private final void addLatinOneEntry(char ch, int CE, shiftValues sh2) {
        int primary1 = 0;
        int primary2 = 0;
        int secondary = 0;
        int tertiary = 0;
        boolean continuation = RuleBasedCollator.isContinuation(CE);
        boolean reverseSecondary = false;
        if (!continuation) {
            tertiary = CE & this.m_mask3_;
            tertiary ^= this.m_caseSwitch_;
            reverseSecondary = true;
        } else {
            tertiary = (byte)(CE & 0xFFFFFF3F);
            tertiary &= 0x3F;
            reverseSecondary = false;
        }
        secondary = (CE >>>= 8) & 0xFF;
        primary2 = (CE >>>= 8) & 0xFF;
        primary1 = CE >>> 8;
        if (primary1 != 0) {
            if (this.m_leadBytePermutationTable_ != null && !continuation) {
                primary1 = this.m_leadBytePermutationTable_[primary1];
            }
            char c2 = ch;
            this.latinOneCEs_[c2] = this.latinOneCEs_[c2] | primary1 << sh2.primShift;
            sh2.primShift -= 8;
        }
        if (primary2 != 0) {
            if (sh2.primShift < 0) {
                this.latinOneCEs_[ch] = -16777216;
                this.latinOneCEs_[this.latinOneTableLen_ + ch] = -16777216;
                this.latinOneCEs_[2 * this.latinOneTableLen_ + ch] = -16777216;
                return;
            }
            char c3 = ch;
            this.latinOneCEs_[c3] = this.latinOneCEs_[c3] | primary2 << sh2.primShift;
            sh2.primShift -= 8;
        }
        if (secondary != 0) {
            if (reverseSecondary && this.m_isFrenchCollation_) {
                int n2 = this.latinOneTableLen_ + ch;
                this.latinOneCEs_[n2] = this.latinOneCEs_[n2] >>> 8;
                int n3 = this.latinOneTableLen_ + ch;
                this.latinOneCEs_[n3] = this.latinOneCEs_[n3] | secondary << 24;
            } else {
                int n4 = this.latinOneTableLen_ + ch;
                this.latinOneCEs_[n4] = this.latinOneCEs_[n4] | secondary << sh2.secShift;
            }
            sh2.secShift -= 8;
        }
        if (tertiary != 0) {
            int n5 = 2 * this.latinOneTableLen_ + ch;
            this.latinOneCEs_[n5] = this.latinOneCEs_[n5] | tertiary << sh2.terShift;
            sh2.terShift -= 8;
        }
    }

    private final void resizeLatinOneTable(int newSize) {
        int[] newTable = new int[3 * newSize];
        int sizeToCopy = newSize < this.latinOneTableLen_ ? newSize : this.latinOneTableLen_;
        System.arraycopy(this.latinOneCEs_, 0, newTable, 0, sizeToCopy);
        System.arraycopy(this.latinOneCEs_, this.latinOneTableLen_, newTable, newSize, sizeToCopy);
        System.arraycopy(this.latinOneCEs_, 2 * this.latinOneTableLen_, newTable, 2 * newSize, sizeToCopy);
        this.latinOneTableLen_ = newSize;
        this.latinOneCEs_ = newTable;
    }

    /*
     * Unable to fully structure code
     */
    private final boolean setUpLatinOne() {
        if (this.latinOneCEs_ == null || this.m_reallocLatinOneCEs_) {
            this.latinOneCEs_ = new int[915];
            this.latinOneTableLen_ = 305;
            this.m_reallocLatinOneCEs_ = false;
        } else {
            Arrays.fill(this.latinOneCEs_, 0);
        }
        if (this.m_ContInfo_ == null) {
            this.m_ContInfo_ = new ContractionInfo();
        }
        ch = '\u0000';
        it = this.getCollationElementIterator("");
        s = new shiftValues();
        CE = 0;
        contractionOffset = '\u0100';
        block5: for (ch = '\u0000'; ch <= '\u00ff'; ch = (char)(ch + '\u0001')) {
            s.primShift = 24;
            s.secShift = 24;
            s.terShift = 24;
            if (ch < '\u0100') {
                CE = this.m_trie_.getLatin1LinearValue(ch);
            } else {
                CE = this.m_trie_.getLeadValue(ch);
                if (CE == -268435456) {
                    CE = RuleBasedCollator.UCA_.m_trie_.getLeadValue(ch);
                }
            }
            if (!RuleBasedCollator.isSpecial(CE)) {
                this.addLatinOneEntry(ch, CE, s);
                continue;
            }
            switch (RuleBasedCollator.getTag(CE)) {
                case 1: 
                case 13: {
                    it.setText(UCharacter.toString(ch));
                    while ((CE = it.next()) != -1) {
                        if (s.primShift < 0 || s.secShift < 0 || s.terShift < 0) {
                            this.latinOneCEs_[ch] = -16777216;
                            this.latinOneCEs_[this.latinOneTableLen_ + ch] = -16777216;
                            this.latinOneCEs_[2 * this.latinOneTableLen_ + ch] = -16777216;
                            continue block5;
                        }
                        this.addLatinOneEntry(ch, CE, s);
                    }
                    continue block5;
                }
                case 2: {
                    if ((CE & 0xFFF000) != 0) {
                        this.latinOneFailed_ = true;
                        return false;
                    }
                    UCharOffset = (CE & 0xFFFFFF) - this.m_contractionOffset_;
                    this.latinOneCEs_[ch] = CE |= (contractionOffset & 4095) << 12;
                    this.latinOneCEs_[this.latinOneTableLen_ + ch] = CE;
                    this.latinOneCEs_[2 * this.latinOneTableLen_ + ch] = CE;
                    do {
                        if (!RuleBasedCollator.isSpecial(CE = this.m_contractionCE_[UCharOffset]) || RuleBasedCollator.getTag(CE) != 1) ** GOTO lbl71
                        offset = ((CE & 0xFFFFF0) >> 4) - this.m_expansionOffset_;
                        size = CE & 15;
                        if (size == 0) ** GOTO lbl61
                        for (i = 0; i < size; ++i) {
                            if (s.primShift >= 0 && s.secShift >= 0 && s.terShift >= 0) ** GOTO lbl58
                            this.latinOneCEs_[contractionOffset] = -16777216;
                            this.latinOneCEs_[this.latinOneTableLen_ + contractionOffset] = -16777216;
                            this.latinOneCEs_[2 * this.latinOneTableLen_ + contractionOffset] = -16777216;
                            ** GOTO lbl69
lbl58:
                            // 1 sources

                            this.addLatinOneEntry(contractionOffset, this.m_expansion_[offset + i], s);
                        }
                        ** GOTO lbl69
lbl61:
                        // 2 sources

                        while (this.m_expansion_[offset] != 0) {
                            if (s.primShift >= 0 && s.secShift >= 0 && s.terShift >= 0) ** GOTO lbl67
                            this.latinOneCEs_[contractionOffset] = -16777216;
                            this.latinOneCEs_[this.latinOneTableLen_ + contractionOffset] = -16777216;
                            this.latinOneCEs_[2 * this.latinOneTableLen_ + contractionOffset] = -16777216;
                            break;
lbl67:
                            // 1 sources

                            this.addLatinOneEntry(contractionOffset, this.m_expansion_[offset++], s);
                        }
lbl69:
                        // 4 sources

                        contractionOffset = (char)(contractionOffset + '\u0001');
                        ** GOTO lbl80
lbl71:
                        // 1 sources

                        if (!RuleBasedCollator.isSpecial(CE)) {
                            v0 = contractionOffset;
                            contractionOffset = (char)(contractionOffset + 1);
                            this.addLatinOneEntry(v0, CE, s);
                        } else {
                            this.latinOneCEs_[contractionOffset] = -16777216;
                            this.latinOneCEs_[this.latinOneTableLen_ + contractionOffset] = -16777216;
                            this.latinOneCEs_[2 * this.latinOneTableLen_ + contractionOffset] = -16777216;
                            contractionOffset = (char)(contractionOffset + '\u0001');
                        }
lbl80:
                        // 3 sources

                        ++UCharOffset;
                        s.primShift = 24;
                        s.secShift = 24;
                        s.terShift = 24;
                        if (contractionOffset != this.latinOneTableLen_) continue;
                        this.resizeLatinOneTable(2 * this.latinOneTableLen_);
                    } while (this.m_contractionIndex_[UCharOffset] != '\uffff');
                    continue block5;
                }
                case 11: {
                    if (ch == '\u00b7') {
                        this.addLatinOneEntry(ch, CE, s);
                        continue block5;
                    }
                    this.latinOneFailed_ = true;
                    return false;
                }
                default: {
                    this.latinOneFailed_ = true;
                    return false;
                }
            }
        }
        if (contractionOffset < this.latinOneTableLen_) {
            this.resizeLatinOneTable(contractionOffset);
        }
        return true;
    }

    private int getLatinOneContraction(int strength, int CE, String s2) {
        int len = s2.length();
        int UCharOffset = (CE & 0xFFF) - this.m_contractionOffset_;
        int offset = 1;
        int latinOneOffset = (CE & 0xFFF000) >>> 12;
        char schar = '\u0000';
        char tchar = '\u0000';
        while (true) {
            if (this.m_ContInfo_.index == len) {
                return this.latinOneCEs_[strength * this.latinOneTableLen_ + latinOneOffset];
            }
            schar = s2.charAt(this.m_ContInfo_.index);
            while (schar > (tchar = this.m_contractionIndex_[UCharOffset + offset])) {
                ++offset;
            }
            if (schar == tchar) {
                ++this.m_ContInfo_.index;
                return this.latinOneCEs_[strength * this.latinOneTableLen_ + latinOneOffset + offset];
            }
            if (schar > '\u00ff') {
                return -16777216;
            }
            int isZeroCE = this.m_trie_.getLeadValue(schar);
            if (isZeroCE != 0) break;
            ++this.m_ContInfo_.index;
        }
        return this.latinOneCEs_[strength * this.latinOneTableLen_ + latinOneOffset];
    }

    private final int compareUseLatin1(String source, String target, int startOffset, CollationBuffer buffer) {
        int offset;
        boolean endOfSource;
        int tOrder;
        int sOrder;
        char tChar;
        char sChar;
        int tIndex;
        int sIndex;
        int strength;
        int tLen;
        int sLen;
        block50: {
            sLen = source.length();
            tLen = target.length();
            strength = this.getStrength();
            sIndex = startOffset;
            tIndex = startOffset;
            sChar = '\u0000';
            tChar = '\u0000';
            sOrder = 0;
            tOrder = 0;
            endOfSource = false;
            boolean haveContractions = false;
            offset = this.latinOneTableLen_;
            block0: while (true) {
                if (sOrder == 0) {
                    if (sIndex == sLen) {
                        endOfSource = true;
                    } else {
                        if ((sChar = source.charAt(sIndex++)) > '\u00ff') {
                            return this.compareRegular(source, target, startOffset, buffer);
                        }
                        sOrder = this.latinOneCEs_[sChar];
                        if (!RuleBasedCollator.isSpecial(sOrder)) continue;
                        if (RuleBasedCollator.getTag(sOrder) == 2) {
                            this.m_ContInfo_.index = sIndex;
                            sOrder = this.getLatinOneContraction(0, sOrder, source);
                            sIndex = this.m_ContInfo_.index;
                            haveContractions = true;
                        }
                        if (!RuleBasedCollator.isSpecial(sOrder)) continue;
                        return this.compareRegular(source, target, startOffset, buffer);
                    }
                }
                while (tOrder == 0) {
                    if (tIndex == tLen) {
                        if (endOfSource) break block0;
                        return 1;
                    }
                    if ((tChar = target.charAt(tIndex++)) > '\u00ff') {
                        return this.compareRegular(source, target, startOffset, buffer);
                    }
                    tOrder = this.latinOneCEs_[tChar];
                    if (!RuleBasedCollator.isSpecial(tOrder)) continue;
                    if (RuleBasedCollator.getTag(tOrder) == 2) {
                        this.m_ContInfo_.index = tIndex;
                        tOrder = this.getLatinOneContraction(0, tOrder, target);
                        tIndex = this.m_ContInfo_.index;
                        haveContractions = true;
                    }
                    if (!RuleBasedCollator.isSpecial(tOrder)) continue;
                    return this.compareRegular(source, target, startOffset, buffer);
                }
                if (endOfSource) {
                    return -1;
                }
                if (sOrder == tOrder) {
                    sOrder = 0;
                    tOrder = 0;
                    continue;
                }
                if (((sOrder ^ tOrder) & 0xFF000000) != 0) {
                    if (sOrder >>> 8 < tOrder >>> 8) {
                        return -1;
                    }
                    return 1;
                }
                sOrder <<= 8;
                tOrder <<= 8;
            }
            if (strength >= 1) {
                endOfSource = false;
                if (!this.m_isFrenchCollation_) {
                    sIndex = startOffset;
                    tIndex = startOffset;
                    while (true) {
                        if (sOrder == 0) {
                            if (sIndex == sLen) {
                                endOfSource = true;
                            } else {
                                if (!RuleBasedCollator.isSpecial(sOrder = this.latinOneCEs_[offset + (sChar = source.charAt(sIndex++))])) continue;
                                this.m_ContInfo_.index = sIndex;
                                sOrder = this.getLatinOneContraction(1, sOrder, source);
                                sIndex = this.m_ContInfo_.index;
                                continue;
                            }
                        }
                        while (tOrder == 0) {
                            if (tIndex == tLen) {
                                if (!endOfSource) {
                                    return 1;
                                }
                                break block50;
                            }
                            if (!RuleBasedCollator.isSpecial(tOrder = this.latinOneCEs_[offset + (tChar = target.charAt(tIndex++))])) continue;
                            this.m_ContInfo_.index = tIndex;
                            tOrder = this.getLatinOneContraction(1, tOrder, target);
                            tIndex = this.m_ContInfo_.index;
                        }
                        if (endOfSource) {
                            return -1;
                        }
                        if (sOrder == tOrder) {
                            sOrder = 0;
                            tOrder = 0;
                            continue;
                        }
                        if (((sOrder ^ tOrder) & 0xFF000000) != 0) {
                            if (sOrder >>> 8 < tOrder >>> 8) {
                                return -1;
                            }
                            return 1;
                        }
                        sOrder <<= 8;
                        tOrder <<= 8;
                    }
                }
                if (haveContractions) {
                    return this.compareRegular(source, target, startOffset, buffer);
                }
                sIndex = sLen;
                tIndex = tLen;
                block4: while (true) {
                    if (sOrder == 0) {
                        if (sIndex == startOffset) {
                            endOfSource = true;
                        } else {
                            sChar = source.charAt(--sIndex);
                            sOrder = this.latinOneCEs_[offset + sChar];
                            continue;
                        }
                    }
                    while (tOrder == 0) {
                        if (tIndex == startOffset) {
                            if (endOfSource) break block4;
                            return 1;
                        }
                        tChar = target.charAt(--tIndex);
                        tOrder = this.latinOneCEs_[offset + tChar];
                    }
                    if (endOfSource) {
                        return -1;
                    }
                    if (sOrder == tOrder) {
                        sOrder = 0;
                        tOrder = 0;
                        continue;
                    }
                    if (((sOrder ^ tOrder) & 0xFF000000) != 0) {
                        if (sOrder >>> 8 < tOrder >>> 8) {
                            return -1;
                        }
                        return 1;
                    }
                    sOrder <<= 8;
                    tOrder <<= 8;
                }
            }
        }
        if (strength >= 2) {
            offset += this.latinOneTableLen_;
            sIndex = startOffset;
            tIndex = startOffset;
            endOfSource = false;
            while (true) {
                if (sOrder == 0) {
                    if (sIndex == sLen) {
                        endOfSource = true;
                    } else {
                        if (!RuleBasedCollator.isSpecial(sOrder = this.latinOneCEs_[offset + (sChar = source.charAt(sIndex++))])) continue;
                        this.m_ContInfo_.index = sIndex;
                        sOrder = this.getLatinOneContraction(2, sOrder, source);
                        sIndex = this.m_ContInfo_.index;
                        continue;
                    }
                }
                while (tOrder == 0) {
                    if (tIndex == tLen) {
                        if (endOfSource) {
                            return 0;
                        }
                        return 1;
                    }
                    if (!RuleBasedCollator.isSpecial(tOrder = this.latinOneCEs_[offset + (tChar = target.charAt(tIndex++))])) continue;
                    this.m_ContInfo_.index = tIndex;
                    tOrder = this.getLatinOneContraction(2, tOrder, target);
                    tIndex = this.m_ContInfo_.index;
                }
                if (endOfSource) {
                    return -1;
                }
                if (sOrder == tOrder) {
                    sOrder = 0;
                    tOrder = 0;
                    continue;
                }
                if (((sOrder ^ tOrder) & 0xFF000000) != 0) {
                    if (sOrder >>> 8 < tOrder >>> 8) {
                        return -1;
                    }
                    return 1;
                }
                sOrder <<= 8;
                tOrder <<= 8;
            }
        }
        return 0;
    }

    public VersionInfo getVersion() {
        int rtVersion = VersionInfo.UCOL_RUNTIME_VERSION.getMajor();
        int bdVersion = this.m_version_.getMajor();
        int csVersion = 0;
        int cmbVersion = (rtVersion << 11 | bdVersion << 6 | csVersion) & 0xFFFF;
        return VersionInfo.getInstance(cmbVersion >> 8, cmbVersion & 0xFF, this.m_version_.getMinor(), RuleBasedCollator.UCA_.m_UCA_version_.getMajor());
    }

    public VersionInfo getUCAVersion() {
        return RuleBasedCollator.UCA_.m_UCA_version_;
    }

    private final CollationBuffer getCollationBuffer() {
        if (this.isFrozen()) {
            this.frozenLock.lock();
        }
        if (this.collationBuffer == null) {
            this.collationBuffer = new CollationBuffer();
        } else {
            this.collationBuffer.resetBuffers();
        }
        return this.collationBuffer;
    }

    private final void releaseCollationBuffer(CollationBuffer buffer) {
        if (this.isFrozen()) {
            this.frozenLock.unlock();
        }
    }

    static {
        RuleBasedCollator iUCA_ = null;
        UCAConstants iUCA_CONSTANTS_ = null;
        LeadByteConstants iLEADBYTE_CONSTANTS = null;
        char[] iUCA_CONTRACTIONS_ = null;
        Output<Integer> maxUCAContractionLength = new Output<Integer>();
        ImplicitCEGenerator iimpCEGen_ = null;
        try {
            iUCA_ = new RuleBasedCollator();
            iUCA_CONSTANTS_ = new UCAConstants();
            iLEADBYTE_CONSTANTS = new LeadByteConstants();
            iUCA_CONTRACTIONS_ = CollatorReader.read(iUCA_, iUCA_CONSTANTS_, iLEADBYTE_CONSTANTS, maxUCAContractionLength);
            iimpCEGen_ = new ImplicitCEGenerator(224, 228);
            iUCA_.init();
            ICUResourceBundle rb2 = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/coll", ULocale.ENGLISH);
            iUCA_.m_rules_ = (String)rb2.getObject("UCARules");
        }
        catch (MissingResourceException ex2) {
        }
        catch (IOException e2) {
            // empty catch block
        }
        UCA_ = iUCA_;
        UCA_CONSTANTS_ = iUCA_CONSTANTS_;
        LEADBYTE_CONSTANTS_ = iLEADBYTE_CONSTANTS;
        UCA_CONTRACTIONS_ = iUCA_CONTRACTIONS_;
        MAX_UCA_CONTRACTION_LENGTH = (Integer)maxUCAContractionLength.value;
        impCEGen_ = iimpCEGen_;
        UCA_INIT_COMPLETE = true;
    }

    private static class ContractionInfo {
        int index;

        private ContractionInfo() {
        }
    }

    private static class shiftValues {
        int primShift = 24;
        int secShift = 24;
        int terShift = 24;

        private shiftValues() {
        }
    }

    private final class CollationBuffer {
        protected StringUCharacterIterator m_srcUtilIter_;
        protected CollationElementIterator m_srcUtilColEIter_;
        protected StringUCharacterIterator m_tgtUtilIter_;
        protected CollationElementIterator m_tgtUtilColEIter_;
        protected boolean m_utilCompare0_;
        protected boolean m_utilCompare2_;
        protected boolean m_utilCompare3_;
        protected boolean m_utilCompare4_;
        protected boolean m_utilCompare5_;
        protected byte[] m_utilBytes0_;
        protected byte[] m_utilBytes1_;
        protected byte[] m_utilBytes2_;
        protected byte[] m_utilBytes3_;
        protected byte[] m_utilBytes4_;
        protected RawCollationKey m_utilRawCollationKey_;
        protected int m_utilBytesCount0_;
        protected int m_utilBytesCount1_;
        protected int m_utilBytesCount2_;
        protected int m_utilBytesCount3_;
        protected int m_utilBytesCount4_;
        protected int m_utilCount2_;
        protected int m_utilCount3_;
        protected int m_utilCount4_;
        protected int m_utilFrenchStart_;
        protected int m_utilFrenchEnd_;
        protected int[] m_srcUtilCEBuffer_;
        protected int[] m_tgtUtilCEBuffer_;
        protected int m_srcUtilCEBufferSize_;
        protected int m_tgtUtilCEBufferSize_;
        protected int m_srcUtilContOffset_;
        protected int m_tgtUtilContOffset_;
        protected int m_srcUtilOffset_;
        protected int m_tgtUtilOffset_;

        private CollationBuffer() {
            this.initBuffers();
        }

        protected final void initBuffers() {
            this.resetBuffers();
            this.m_srcUtilIter_ = new StringUCharacterIterator();
            this.m_srcUtilColEIter_ = new CollationElementIterator(this.m_srcUtilIter_, RuleBasedCollator.this);
            this.m_tgtUtilIter_ = new StringUCharacterIterator();
            this.m_tgtUtilColEIter_ = new CollationElementIterator(this.m_tgtUtilIter_, RuleBasedCollator.this);
            this.m_utilBytes0_ = new byte[32];
            this.m_utilBytes1_ = new byte[1024];
            this.m_utilBytes2_ = new byte[128];
            this.m_utilBytes3_ = new byte[128];
            this.m_utilBytes4_ = new byte[128];
            this.m_srcUtilCEBuffer_ = new int[512];
            this.m_tgtUtilCEBuffer_ = new int[512];
        }

        protected final void resetBuffers() {
            this.m_utilCompare0_ = false;
            this.m_utilCompare2_ = false;
            this.m_utilCompare3_ = false;
            this.m_utilCompare4_ = false;
            this.m_utilCompare5_ = false;
            this.m_utilBytesCount0_ = 0;
            this.m_utilBytesCount1_ = 0;
            this.m_utilBytesCount2_ = 0;
            this.m_utilBytesCount3_ = 0;
            this.m_utilBytesCount4_ = 0;
            this.m_utilCount2_ = 0;
            this.m_utilCount3_ = 0;
            this.m_utilCount4_ = 0;
            this.m_utilFrenchStart_ = 0;
            this.m_utilFrenchEnd_ = 0;
            this.m_srcUtilContOffset_ = 0;
            this.m_tgtUtilContOffset_ = 0;
            this.m_srcUtilOffset_ = 0;
            this.m_tgtUtilOffset_ = 0;
        }
    }

    static final class LeadByteConstants {
        private static final int DATA_MASK_FOR_INDEX = 32768;
        private static final int[] EMPTY_INT_ARRAY = new int[0];
        private int serializedSize = 0;
        private Map<Integer, Integer> SCRIPT_TO_LEAD_BYTES_INDEX;
        private byte[] SCRIPT_TO_LEAD_BYTES_DATA;
        private int[] LEAD_BYTE_TO_SCRIPTS_INDEX;
        private byte[] LEAD_BYTE_TO_SCRIPTS_DATA;

        LeadByteConstants() {
        }

        void read(DataInputStream dis) throws IOException {
            int index;
            int readcount = 0;
            int indexCount = dis.readShort();
            readcount += 2;
            short dataSize = dis.readShort();
            readcount += 2;
            this.SCRIPT_TO_LEAD_BYTES_INDEX = new HashMap<Integer, Integer>();
            for (index = 0; index < indexCount; ++index) {
                short reorderCode = dis.readShort();
                readcount += 2;
                int dataOffset = 0xFFFF & dis.readShort();
                readcount += 2;
                this.SCRIPT_TO_LEAD_BYTES_INDEX.put(Integer.valueOf(reorderCode), dataOffset);
            }
            this.SCRIPT_TO_LEAD_BYTES_DATA = new byte[dataSize * 2];
            dis.readFully(this.SCRIPT_TO_LEAD_BYTES_DATA, 0, this.SCRIPT_TO_LEAD_BYTES_DATA.length);
            readcount += this.SCRIPT_TO_LEAD_BYTES_DATA.length;
            indexCount = dis.readShort();
            readcount += 2;
            dataSize = dis.readShort();
            readcount += 2;
            this.LEAD_BYTE_TO_SCRIPTS_INDEX = new int[indexCount];
            for (index = 0; index < indexCount; ++index) {
                this.LEAD_BYTE_TO_SCRIPTS_INDEX[index] = 0xFFFF & dis.readShort();
                readcount += 2;
            }
            this.LEAD_BYTE_TO_SCRIPTS_DATA = new byte[dataSize * 2];
            dis.readFully(this.LEAD_BYTE_TO_SCRIPTS_DATA, 0, this.LEAD_BYTE_TO_SCRIPTS_DATA.length);
            this.serializedSize = readcount += this.LEAD_BYTE_TO_SCRIPTS_DATA.length;
        }

        int getSerializedDataSize() {
            return this.serializedSize;
        }

        int[] getReorderCodesForLeadByte(int leadByte) {
            int[] reorderCodes;
            if (leadByte >= this.LEAD_BYTE_TO_SCRIPTS_INDEX.length) {
                return EMPTY_INT_ARRAY;
            }
            int offset = this.LEAD_BYTE_TO_SCRIPTS_INDEX[leadByte];
            if (offset == 0) {
                return EMPTY_INT_ARRAY;
            }
            if ((offset & 0x8000) == 32768) {
                reorderCodes = new int[]{offset & 0xFFFF7FFF};
            } else {
                int length = LeadByteConstants.readShort(this.LEAD_BYTE_TO_SCRIPTS_DATA, offset);
                ++offset;
                reorderCodes = new int[length];
                int code = 0;
                while (code < length) {
                    reorderCodes[code] = LeadByteConstants.readShort(this.LEAD_BYTE_TO_SCRIPTS_DATA, offset);
                    ++code;
                    ++offset;
                }
            }
            return reorderCodes;
        }

        int[] getLeadBytesForReorderCode(int reorderCode) {
            int[] leadBytes;
            if (!this.SCRIPT_TO_LEAD_BYTES_INDEX.containsKey(reorderCode)) {
                return EMPTY_INT_ARRAY;
            }
            int offset = this.SCRIPT_TO_LEAD_BYTES_INDEX.get(reorderCode);
            if (offset == 0) {
                return EMPTY_INT_ARRAY;
            }
            if ((offset & 0x8000) == 32768) {
                leadBytes = new int[]{offset & 0xFFFF7FFF};
            } else {
                int length = LeadByteConstants.readShort(this.SCRIPT_TO_LEAD_BYTES_DATA, offset);
                ++offset;
                leadBytes = new int[length];
                int leadByte = 0;
                while (leadByte < length) {
                    leadBytes[leadByte] = LeadByteConstants.readShort(this.SCRIPT_TO_LEAD_BYTES_DATA, offset);
                    ++leadByte;
                    ++offset;
                }
            }
            return leadBytes;
        }

        private static int readShort(byte[] data, int offset) {
            return (0xFF & data[offset * 2]) << 8 | data[offset * 2 + 1] & 0xFF;
        }
    }

    static final class UCAConstants {
        int[] FIRST_TERTIARY_IGNORABLE_ = new int[2];
        int[] LAST_TERTIARY_IGNORABLE_ = new int[2];
        int[] FIRST_PRIMARY_IGNORABLE_ = new int[2];
        int[] FIRST_SECONDARY_IGNORABLE_ = new int[2];
        int[] LAST_SECONDARY_IGNORABLE_ = new int[2];
        int[] LAST_PRIMARY_IGNORABLE_ = new int[2];
        int[] FIRST_VARIABLE_ = new int[2];
        int[] LAST_VARIABLE_ = new int[2];
        int[] FIRST_NON_VARIABLE_ = new int[2];
        int[] LAST_NON_VARIABLE_ = new int[2];
        int[] RESET_TOP_VALUE_ = new int[2];
        int[] FIRST_IMPLICIT_ = new int[2];
        int[] LAST_IMPLICIT_ = new int[2];
        int[] FIRST_TRAILING_ = new int[2];
        int[] LAST_TRAILING_ = new int[2];
        int PRIMARY_TOP_MIN_;
        int PRIMARY_IMPLICIT_MIN_;
        int PRIMARY_IMPLICIT_MAX_;
        int PRIMARY_TRAILING_MIN_;
        int PRIMARY_TRAILING_MAX_;
        int PRIMARY_SPECIAL_MIN_;
        int PRIMARY_SPECIAL_MAX_;

        UCAConstants() {
        }
    }

    static class DataManipulate
    implements Trie.DataManipulate {
        private static DataManipulate m_instance_;

        public final int getFoldingOffset(int ce2) {
            if (RuleBasedCollator.isSpecial(ce2) && RuleBasedCollator.getTag(ce2) == 5) {
                return ce2 & 0xFFFFFF;
            }
            return 0;
        }

        public static final DataManipulate getInstance() {
            if (m_instance_ == null) {
                m_instance_ = new DataManipulate();
            }
            return m_instance_;
        }

        private DataManipulate() {
        }
    }

    static interface Attribute {
        public static final int FRENCH_COLLATION_ = 0;
        public static final int ALTERNATE_HANDLING_ = 1;
        public static final int CASE_FIRST_ = 2;
        public static final int CASE_LEVEL_ = 3;
        public static final int NORMALIZATION_MODE_ = 4;
        public static final int STRENGTH_ = 5;
        public static final int HIRAGANA_QUATERNARY_MODE_ = 6;
        public static final int LIMIT_ = 7;
    }

    static interface AttributeValue {
        public static final int DEFAULT_ = -1;
        public static final int PRIMARY_ = 0;
        public static final int SECONDARY_ = 1;
        public static final int TERTIARY_ = 2;
        public static final int DEFAULT_STRENGTH_ = 2;
        public static final int CE_STRENGTH_LIMIT_ = 3;
        public static final int QUATERNARY_ = 3;
        public static final int IDENTICAL_ = 15;
        public static final int STRENGTH_LIMIT_ = 16;
        public static final int OFF_ = 16;
        public static final int ON_ = 17;
        public static final int SHIFTED_ = 20;
        public static final int NON_IGNORABLE_ = 21;
        public static final int LOWER_FIRST_ = 24;
        public static final int UPPER_FIRST_ = 25;
        public static final int LIMIT_ = 29;
    }

    private static class contContext {
        RuleBasedCollator coll;
        UnicodeSet contractions;
        UnicodeSet expansions;
        UnicodeSet removedContractions;
        boolean addPrefixes;

        contContext(RuleBasedCollator coll, UnicodeSet contractions, UnicodeSet expansions, UnicodeSet removedContractions, boolean addPrefixes) {
            this.coll = coll;
            this.contractions = contractions;
            this.expansions = expansions;
            this.removedContractions = removedContractions;
            this.addPrefixes = addPrefixes;
        }
    }
}

