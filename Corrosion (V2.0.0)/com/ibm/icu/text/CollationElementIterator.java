/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.impl.CharacterIteratorWrapper;
import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.Norm2AllModes;
import com.ibm.icu.impl.Normalizer2Impl;
import com.ibm.icu.impl.StringUCharacterIterator;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.RuleBasedCollator;
import com.ibm.icu.text.UCharacterIterator;
import com.ibm.icu.text.UTF16;
import java.text.CharacterIterator;
import java.util.MissingResourceException;

public final class CollationElementIterator {
    public static final int NULLORDER = -1;
    public static final int IGNORABLE = 0;
    boolean m_isCodePointHiragana_;
    int m_FCDStart_;
    int m_CEBufferOffset_;
    int m_CEBufferSize_;
    static final int CE_NOT_FOUND_ = -268435456;
    static final int CE_EXPANSION_TAG_ = 1;
    static final int CE_CONTRACTION_TAG_ = 2;
    static final int CE_DIGIT_TAG_ = 13;
    private boolean m_isForwards_;
    private UCharacterIterator m_source_;
    private int m_bufferOffset_;
    private StringBuilder m_buffer_;
    private int m_FCDLimit_;
    private RuleBasedCollator m_collator_;
    private int[] m_CEBuffer_;
    private static final int CE_BUFFER_INIT_SIZE_ = 512;
    private Backup m_utilSpecialBackUp_;
    private Backup m_utilSpecialEntryBackUp_;
    private Backup m_utilSpecialDiscontiguousBackUp_;
    private StringUCharacterIterator m_srcUtilIter_;
    private StringBuilder m_utilStringBuffer_ = new StringBuilder();
    private StringBuilder m_utilSkippedBuffer_;
    private CollationElementIterator m_utilColEIter_;
    private static final Normalizer2Impl m_nfcImpl_ = Norm2AllModes.getNFCInstance().impl;
    private StringBuilder m_unnormalized_;
    private Normalizer2Impl.ReorderingBuffer m_n2Buffer_;
    private static final int FULL_ZERO_COMBINING_CLASS_FAST_LIMIT_ = 192;
    private static final int LEAD_ZERO_COMBINING_CLASS_FAST_LIMIT_ = 768;
    private static final int LAST_BYTE_MASK_ = 255;
    private static final int SECOND_LAST_BYTE_SHIFT_ = 8;
    private static final int CE_CONTRACTION_ = -234881024;
    private static final int CE_NOT_FOUND_TAG_ = 0;
    private static final int CE_CHARSET_TAG_ = 4;
    private static final int CE_HANGUL_SYLLABLE_TAG_ = 6;
    private static final int CE_LEAD_SURROGATE_TAG_ = 7;
    private static final int CE_TRAIL_SURROGATE_TAG_ = 8;
    private static final int CE_CJK_IMPLICIT_TAG_ = 9;
    private static final int CE_IMPLICIT_TAG_ = 10;
    static final int CE_SPEC_PROC_TAG_ = 11;
    private static final int CE_LONG_PRIMARY_TAG_ = 12;
    private static final int CE_BYTE_COMMON_ = 5;
    private static final int HANGUL_SBASE_ = 44032;
    private static final int HANGUL_LBASE_ = 4352;
    private static final int HANGUL_VBASE_ = 4449;
    private static final int HANGUL_TBASE_ = 4519;
    private static final int HANGUL_VCOUNT_ = 21;
    private static final int HANGUL_TCOUNT_ = 28;
    private static final boolean DEBUG = ICUDebug.enabled("collator");

    public int getOffset() {
        if (this.m_bufferOffset_ != -1) {
            if (this.m_isForwards_) {
                return this.m_FCDLimit_;
            }
            return this.m_FCDStart_;
        }
        return this.m_source_.getIndex();
    }

    public int getMaxExpansion(int ce2) {
        int start = 0;
        int limit = this.m_collator_.m_expansionEndCE_.length;
        long unsignedce = (long)ce2 & 0xFFFFFFFFL;
        while (start < limit - 1) {
            int mid = start + (limit - start >> 1);
            long midce = (long)this.m_collator_.m_expansionEndCE_[mid] & 0xFFFFFFFFL;
            if (unsignedce <= midce) {
                limit = mid;
                continue;
            }
            start = mid;
        }
        int result = 1;
        if (this.m_collator_.m_expansionEndCE_[start] == ce2) {
            result = this.m_collator_.m_expansionEndCEMaxSize_[start];
        } else if (limit < this.m_collator_.m_expansionEndCE_.length && this.m_collator_.m_expansionEndCE_[limit] == ce2) {
            result = this.m_collator_.m_expansionEndCEMaxSize_[limit];
        } else if ((ce2 & 0xFFFF) == 192) {
            result = 2;
        }
        return result;
    }

    public void reset() {
        this.m_source_.setToStart();
        this.updateInternalState();
    }

    public int next() {
        this.m_isForwards_ = true;
        if (this.m_CEBufferSize_ > 0) {
            if (this.m_CEBufferOffset_ < this.m_CEBufferSize_) {
                return this.m_CEBuffer_[this.m_CEBufferOffset_++];
            }
            this.m_CEBufferSize_ = 0;
            this.m_CEBufferOffset_ = 0;
        }
        int result = -1;
        char ch = '\u0000';
        do {
            int ch_int;
            if ((ch_int = this.nextChar()) == -1) {
                return -1;
            }
            ch = (char)ch_int;
            if (this.m_collator_.m_isHiragana4_) {
                this.m_isCodePointHiragana_ = this.m_isCodePointHiragana_ && ch >= '\u3099' && ch <= '\u309c' || ch >= '\u3040' && ch <= '\u309e' && (ch <= '\u3094' || ch >= '\u309d');
            }
            if (!RuleBasedCollator.isSpecial(result = ch <= '\u00ff' ? this.m_collator_.m_trie_.getLatin1LinearValue(ch) : this.m_collator_.m_trie_.getLeadValue(ch))) {
                return result;
            }
            if (result != -268435456) {
                result = this.nextSpecial(this.m_collator_, result, ch);
            }
            if (result != -268435456) continue;
            if (RuleBasedCollator.UCA_ != null && RuleBasedCollator.isSpecial(result = RuleBasedCollator.UCA_.m_trie_.getLeadValue(ch))) {
                result = this.nextSpecial(RuleBasedCollator.UCA_, result, ch);
            }
            if (result != -268435456) continue;
            result = this.nextImplicit(ch);
        } while (result == 0 && ch >= '\uac00' && ch <= '\ud7af');
        return result;
    }

    public int previous() {
        if (this.m_source_.getIndex() <= 0 && this.m_isForwards_) {
            this.m_source_.setToLimit();
            this.updateInternalState();
        }
        this.m_isForwards_ = false;
        if (this.m_CEBufferSize_ > 0) {
            if (this.m_CEBufferOffset_ > 0) {
                return this.m_CEBuffer_[--this.m_CEBufferOffset_];
            }
            this.m_CEBufferSize_ = 0;
            this.m_CEBufferOffset_ = 0;
        }
        int result = -1;
        char ch = '\u0000';
        do {
            int ch_int;
            if ((ch_int = this.previousChar()) == -1) {
                return -1;
            }
            ch = (char)ch_int;
            if (this.m_collator_.m_isHiragana4_) {
                boolean bl2 = this.m_isCodePointHiragana_ = ch >= '\u3040' && ch <= '\u309f';
            }
            if (this.m_collator_.isContractionEnd(ch) && !this.isBackwardsStart()) {
                result = this.previousSpecial(this.m_collator_, -234881024, ch);
                continue;
            }
            result = ch <= '\u00ff' ? this.m_collator_.m_trie_.getLatin1LinearValue(ch) : this.m_collator_.m_trie_.getLeadValue(ch);
            if (RuleBasedCollator.isSpecial(result)) {
                result = this.previousSpecial(this.m_collator_, result, ch);
            }
            if (result != -268435456) continue;
            if (!this.isBackwardsStart() && this.m_collator_.isContractionEnd(ch)) {
                result = -234881024;
            } else if (RuleBasedCollator.UCA_ != null) {
                result = RuleBasedCollator.UCA_.m_trie_.getLeadValue(ch);
            }
            if (!RuleBasedCollator.isSpecial(result) || RuleBasedCollator.UCA_ == null) continue;
            result = this.previousSpecial(RuleBasedCollator.UCA_, result, ch);
        } while (result == 0 && ch >= '\uac00' && ch <= '\ud7af');
        if (result == -268435456) {
            result = this.previousImplicit(ch);
        }
        return result;
    }

    public static final int primaryOrder(int ce2) {
        return (ce2 & 0xFFFF0000) >>> 16;
    }

    public static final int secondaryOrder(int ce2) {
        return (ce2 & 0xFF00) >> 8;
    }

    public static final int tertiaryOrder(int ce2) {
        return ce2 & 0xFF;
    }

    public void setOffset(int offset) {
        this.m_source_.setIndex(offset);
        int ch_int = this.m_source_.current();
        char ch = (char)ch_int;
        if (ch_int != -1 && this.m_collator_.isUnsafe(ch)) {
            if (UTF16.isTrailSurrogate(ch)) {
                char prevch = (char)this.m_source_.previous();
                if (!UTF16.isLeadSurrogate(prevch)) {
                    this.m_source_.setIndex(offset);
                }
            } else {
                while (this.m_source_.getIndex() > 0 && this.m_collator_.isUnsafe(ch)) {
                    ch = (char)this.m_source_.previous();
                }
                this.updateInternalState();
                int prevoffset = 0;
                while (this.m_source_.getIndex() <= offset) {
                    prevoffset = this.m_source_.getIndex();
                    this.next();
                }
                this.m_source_.setIndex(prevoffset);
            }
        }
        this.updateInternalState();
        offset = this.m_source_.getIndex();
        if (offset == 0) {
            this.m_isForwards_ = false;
        } else if (offset == this.m_source_.getLength()) {
            this.m_isForwards_ = true;
        }
    }

    public void setText(String source) {
        this.m_srcUtilIter_.setText(source);
        this.m_source_ = this.m_srcUtilIter_;
        this.updateInternalState();
    }

    public void setText(UCharacterIterator source) {
        this.m_srcUtilIter_.setText(source.getText());
        this.m_source_ = this.m_srcUtilIter_;
        this.updateInternalState();
    }

    public void setText(CharacterIterator source) {
        this.m_source_ = new CharacterIteratorWrapper(source);
        this.m_source_.setToStart();
        this.updateInternalState();
    }

    public boolean equals(Object that) {
        if (that == this) {
            return true;
        }
        if (that instanceof CollationElementIterator) {
            CollationElementIterator thatceiter = (CollationElementIterator)that;
            if (!this.m_collator_.equals(thatceiter.m_collator_)) {
                return false;
            }
            return this.m_source_.getIndex() == thatceiter.m_source_.getIndex() && this.m_source_.getText().equals(thatceiter.m_source_.getText());
        }
        return false;
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    private CollationElementIterator(RuleBasedCollator collator) {
        this.m_collator_ = collator;
        this.m_CEBuffer_ = new int[512];
        this.m_buffer_ = new StringBuilder();
        this.m_utilSpecialBackUp_ = new Backup();
    }

    CollationElementIterator(String source, RuleBasedCollator collator) {
        this(collator);
        this.m_srcUtilIter_ = new StringUCharacterIterator(source);
        this.m_source_ = this.m_srcUtilIter_;
        this.updateInternalState();
    }

    CollationElementIterator(CharacterIterator source, RuleBasedCollator collator) {
        this(collator);
        this.m_srcUtilIter_ = new StringUCharacterIterator();
        this.m_source_ = new CharacterIteratorWrapper(source);
        this.updateInternalState();
    }

    CollationElementIterator(UCharacterIterator source, RuleBasedCollator collator) {
        this(collator);
        this.m_srcUtilIter_ = new StringUCharacterIterator();
        this.m_srcUtilIter_.setText(source.getText());
        this.m_source_ = this.m_srcUtilIter_;
        this.updateInternalState();
    }

    void setCollator(RuleBasedCollator collator) {
        this.m_collator_ = collator;
        this.updateInternalState();
    }

    void setExactOffset(int offset) {
        this.m_source_.setIndex(offset);
        this.updateInternalState();
    }

    boolean isInBuffer() {
        return this.m_bufferOffset_ > 0;
    }

    void setText(UCharacterIterator source, int offset) {
        this.m_srcUtilIter_.setText(source.getText());
        this.m_source_ = this.m_srcUtilIter_;
        this.m_source_.setIndex(offset);
        this.updateInternalState();
    }

    private void updateInternalState() {
        this.m_isCodePointHiragana_ = false;
        this.m_buffer_.setLength(0);
        this.m_bufferOffset_ = -1;
        this.m_CEBufferOffset_ = 0;
        this.m_CEBufferSize_ = 0;
        this.m_FCDLimit_ = -1;
        this.m_FCDStart_ = this.m_source_.getLength();
        this.m_isForwards_ = true;
    }

    private void backupInternalState(Backup backup) {
        backup.m_offset_ = this.m_source_.getIndex();
        backup.m_FCDLimit_ = this.m_FCDLimit_;
        backup.m_FCDStart_ = this.m_FCDStart_;
        backup.m_isCodePointHiragana_ = this.m_isCodePointHiragana_;
        backup.m_bufferOffset_ = this.m_bufferOffset_;
        backup.m_buffer_.setLength(0);
        if (this.m_bufferOffset_ >= 0) {
            backup.m_buffer_.append((CharSequence)this.m_buffer_);
        }
    }

    private void updateInternalState(Backup backup) {
        this.m_source_.setIndex(backup.m_offset_);
        this.m_isCodePointHiragana_ = backup.m_isCodePointHiragana_;
        this.m_bufferOffset_ = backup.m_bufferOffset_;
        this.m_FCDLimit_ = backup.m_FCDLimit_;
        this.m_FCDStart_ = backup.m_FCDStart_;
        this.m_buffer_.setLength(0);
        if (this.m_bufferOffset_ >= 0) {
            this.m_buffer_.append(backup.m_buffer_);
        }
    }

    private int getCombiningClass(int ch) {
        if (ch >= 768 && this.m_collator_.isUnsafe((char)ch) || ch > 65535) {
            return m_nfcImpl_.getCC(m_nfcImpl_.getNorm16(ch));
        }
        return 0;
    }

    private void normalize() {
        if (this.m_unnormalized_ == null) {
            this.m_unnormalized_ = new StringBuilder();
            this.m_n2Buffer_ = new Normalizer2Impl.ReorderingBuffer(m_nfcImpl_, this.m_buffer_, 10);
        } else {
            this.m_unnormalized_.setLength(0);
            this.m_n2Buffer_.remove();
        }
        int size = this.m_FCDLimit_ - this.m_FCDStart_;
        this.m_source_.setIndex(this.m_FCDStart_);
        for (int i2 = 0; i2 < size; ++i2) {
            this.m_unnormalized_.append((char)this.m_source_.next());
        }
        m_nfcImpl_.decomposeShort(this.m_unnormalized_, 0, size, this.m_n2Buffer_);
    }

    private boolean FCDCheck(int ch, int offset) {
        int fcd;
        boolean result = true;
        this.m_FCDStart_ = offset - 1;
        this.m_source_.setIndex(offset);
        if (ch < 384) {
            fcd = m_nfcImpl_.getFCD16FromBelow180(ch);
        } else if (m_nfcImpl_.singleLeadMightHaveNonZeroFCD16(ch)) {
            if (Character.isHighSurrogate((char)ch)) {
                int c2 = this.m_source_.next();
                if (c2 < 0) {
                    fcd = 0;
                } else if (Character.isLowSurrogate((char)c2)) {
                    fcd = m_nfcImpl_.getFCD16FromNormData(Character.toCodePoint((char)ch, (char)c2));
                } else {
                    this.m_source_.moveIndex(-1);
                    fcd = 0;
                }
            } else {
                fcd = m_nfcImpl_.getFCD16FromNormData(ch);
            }
        } else {
            fcd = 0;
        }
        int prevTrailCC = fcd & 0xFF;
        if (prevTrailCC == 0) {
            offset = this.m_source_.getIndex();
        } else {
            while (true) {
                if ((ch = this.m_source_.nextCodePoint()) < 0) {
                    offset = this.m_source_.getIndex();
                    break;
                }
                fcd = m_nfcImpl_.getFCD16(ch);
                int leadCC = fcd >> 8;
                if (leadCC == 0) {
                    offset = this.m_source_.getIndex() - Character.charCount(ch);
                    break;
                }
                if (leadCC < prevTrailCC) {
                    result = false;
                }
                prevTrailCC = fcd & 0xFF;
            }
        }
        this.m_FCDLimit_ = offset;
        this.m_source_.setIndex(this.m_FCDStart_ + 1);
        return result;
    }

    private int nextChar() {
        int next;
        if (this.m_bufferOffset_ >= 0) {
            if (this.m_bufferOffset_ >= this.m_buffer_.length()) {
                this.m_source_.setIndex(this.m_FCDLimit_);
                this.m_bufferOffset_ = -1;
                this.m_buffer_.setLength(0);
                return this.nextChar();
            }
            return this.m_buffer_.charAt(this.m_bufferOffset_++);
        }
        int result = this.m_source_.next();
        int startoffset = this.m_source_.getIndex();
        if (result < 192 || this.m_collator_.getDecomposition() == 16 || this.m_bufferOffset_ >= 0 || this.m_FCDLimit_ >= startoffset) {
            return result;
        }
        if (result < 768 && ((next = this.m_source_.current()) == -1 || next < 768)) {
            return result;
        }
        if (!this.FCDCheck(result, startoffset)) {
            this.normalize();
            result = this.m_buffer_.charAt(0);
            this.m_bufferOffset_ = 1;
        }
        return result;
    }

    private void normalizeBackwards() {
        this.normalize();
        this.m_bufferOffset_ = this.m_buffer_.length();
    }

    private boolean FCDCheckBackwards(int ch, int offset) {
        boolean result;
        block12: {
            int fcd;
            this.m_FCDLimit_ = offset + 1;
            this.m_source_.setIndex(offset);
            if (ch < 384) {
                fcd = m_nfcImpl_.getFCD16FromBelow180(ch);
            } else if (!Character.isLowSurrogate((char)ch)) {
                fcd = m_nfcImpl_.singleLeadMightHaveNonZeroFCD16(ch) ? m_nfcImpl_.getFCD16FromNormData(ch) : 0;
            } else {
                int c2 = this.m_source_.previous();
                if (c2 < 0) {
                    fcd = 0;
                } else if (Character.isHighSurrogate((char)c2)) {
                    ch = Character.toCodePoint((char)c2, (char)ch);
                    fcd = m_nfcImpl_.getFCD16FromNormData(ch);
                    --offset;
                } else {
                    this.m_source_.moveIndex(1);
                    fcd = 0;
                }
            }
            result = true;
            if (fcd != 0) {
                while (true) {
                    int leadCC;
                    if ((leadCC = fcd >> 8) == 0 || (ch = this.m_source_.previousCodePoint()) < 0) {
                        offset = this.m_source_.getIndex();
                        break block12;
                    }
                    fcd = m_nfcImpl_.getFCD16(ch);
                    int prevTrailCC = fcd & 0xFF;
                    if (leadCC < prevTrailCC) {
                        result = false;
                        continue;
                    }
                    if (fcd == 0) break;
                }
                offset = this.m_source_.getIndex() + Character.charCount(ch);
            }
        }
        this.m_FCDStart_ = offset;
        this.m_source_.setIndex(this.m_FCDLimit_);
        return result;
    }

    private int previousChar() {
        if (this.m_bufferOffset_ >= 0) {
            --this.m_bufferOffset_;
            if (this.m_bufferOffset_ >= 0) {
                return this.m_buffer_.charAt(this.m_bufferOffset_);
            }
            this.m_buffer_.setLength(0);
            if (this.m_FCDStart_ == 0) {
                this.m_FCDStart_ = -1;
                this.m_source_.setIndex(0);
                return -1;
            }
            this.m_FCDLimit_ = this.m_FCDStart_;
            this.m_source_.setIndex(this.m_FCDStart_);
            return this.previousChar();
        }
        int result = this.m_source_.previous();
        int startoffset = this.m_source_.getIndex();
        if (result < 768 || this.m_collator_.getDecomposition() == 16 || this.m_FCDStart_ <= startoffset || this.m_source_.getIndex() == 0) {
            return result;
        }
        int ch = this.m_source_.previous();
        if (ch < 192) {
            this.m_source_.next();
            return result;
        }
        if (!this.FCDCheckBackwards(result, startoffset)) {
            this.normalizeBackwards();
            --this.m_bufferOffset_;
            result = this.m_buffer_.charAt(this.m_bufferOffset_);
        } else {
            this.m_source_.setIndex(startoffset);
        }
        return result;
    }

    private final boolean isBackwardsStart() {
        return this.m_bufferOffset_ < 0 && this.m_source_.getIndex() == 0 || this.m_bufferOffset_ == 0 && this.m_FCDStart_ <= 0;
    }

    private final boolean isEnd() {
        if (this.m_bufferOffset_ >= 0) {
            if (this.m_bufferOffset_ != this.m_buffer_.length()) {
                return false;
            }
            return this.m_FCDLimit_ == this.m_source_.getLength();
        }
        return this.m_source_.getLength() == this.m_source_.getIndex();
    }

    private final int nextSurrogate(RuleBasedCollator collator, int ce2, char trail) {
        if (!UTF16.isTrailSurrogate(trail)) {
            this.updateInternalState(this.m_utilSpecialBackUp_);
            return -268435456;
        }
        int result = collator.m_trie_.getTrailValue(ce2, trail);
        if (result == -268435456) {
            this.updateInternalState(this.m_utilSpecialBackUp_);
        }
        return result;
    }

    private int getExpansionOffset(RuleBasedCollator collator, int ce2) {
        return ((ce2 & 0xFFFFF0) >> 4) - collator.m_expansionOffset_;
    }

    private int getContractionOffset(RuleBasedCollator collator, int ce2) {
        return (ce2 & 0xFFFFFF) - collator.m_contractionOffset_;
    }

    private boolean isSpecialPrefixTag(int ce2) {
        return RuleBasedCollator.isSpecial(ce2) && RuleBasedCollator.getTag(ce2) == 11;
    }

    private int nextSpecialPrefix(RuleBasedCollator collator, int ce2, Backup entrybackup) {
        int entryoffset;
        int offset;
        char previous;
        this.backupInternalState(this.m_utilSpecialBackUp_);
        this.updateInternalState(entrybackup);
        this.previousChar();
        do {
            offset = entryoffset = this.getContractionOffset(collator, ce2);
            if (this.isBackwardsStart()) {
                ce2 = collator.m_contractionCE_[offset];
                break;
            }
            previous = (char)this.previousChar();
            while (previous > collator.m_contractionIndex_[offset]) {
                ++offset;
            }
        } while (this.isSpecialPrefixTag(ce2 = previous == collator.m_contractionIndex_[offset] ? collator.m_contractionCE_[offset] : collator.m_contractionCE_[entryoffset]));
        if (ce2 != -268435456) {
            this.updateInternalState(this.m_utilSpecialBackUp_);
        } else {
            this.updateInternalState(entrybackup);
        }
        return ce2;
    }

    private boolean isContractionTag(int ce2) {
        return RuleBasedCollator.isSpecial(ce2) && RuleBasedCollator.getTag(ce2) == 2;
    }

    private void setDiscontiguous(StringBuilder skipped) {
        if (this.m_bufferOffset_ >= 0) {
            this.m_buffer_.replace(0, this.m_bufferOffset_, skipped.toString());
        } else {
            this.m_FCDLimit_ = this.m_source_.getIndex();
            this.m_buffer_.setLength(0);
            this.m_buffer_.append(skipped.toString());
        }
        this.m_bufferOffset_ = 0;
    }

    private int currentChar() {
        if (this.m_bufferOffset_ < 0) {
            this.m_source_.previousCodePoint();
            return this.m_source_.nextCodePoint();
        }
        return UTF16.charAt(this.m_buffer_, this.m_bufferOffset_ - 1);
    }

    private int nextDiscontiguous(RuleBasedCollator collator, int entryoffset) {
        block17: {
            int ce2;
            int offset = entryoffset;
            boolean multicontraction = false;
            if (this.m_utilSkippedBuffer_ == null) {
                this.m_utilSkippedBuffer_ = new StringBuilder();
            } else {
                this.m_utilSkippedBuffer_.setLength(0);
            }
            int ch = this.currentChar();
            this.m_utilSkippedBuffer_.appendCodePoint(ch);
            int prevCC = 0;
            int cc2 = this.getCombiningClass(ch);
            if (this.m_utilSpecialDiscontiguousBackUp_ == null) {
                this.m_utilSpecialDiscontiguousBackUp_ = new Backup();
            }
            this.backupInternalState(this.m_utilSpecialDiscontiguousBackUp_);
            boolean prevWasLead = false;
            while (true) {
                int ch_int;
                char nextch;
                if (UTF16.isSurrogate(nextch = (char)(ch_int = this.nextChar()))) {
                    if (prevWasLead) {
                        prevWasLead = false;
                    } else {
                        prevCC = cc2;
                        cc2 = 0;
                        prevWasLead = false;
                        if (Character.isHighSurrogate(nextch)) {
                            int trail = this.nextChar();
                            if (Character.isLowSurrogate((char)trail)) {
                                cc2 = this.getCombiningClass(Character.toCodePoint(nextch, (char)trail));
                                prevWasLead = true;
                            }
                            if (trail >= 0) {
                                this.previousChar();
                            }
                        }
                    }
                } else {
                    prevCC = cc2;
                    cc2 = this.getCombiningClass(ch_int);
                    prevWasLead = false;
                }
                if (ch_int < 0 || cc2 == 0) {
                    if (multicontraction) {
                        if (ch_int >= 0) {
                            this.previousChar();
                        }
                        this.setDiscontiguous(this.m_utilSkippedBuffer_);
                        return collator.m_contractionCE_[offset];
                    }
                    break block17;
                }
                ++offset;
                while (offset < collator.m_contractionIndex_.length && nextch > collator.m_contractionIndex_[offset]) {
                    ++offset;
                }
                ce2 = -268435456;
                if (offset >= collator.m_contractionIndex_.length) break block17;
                if (nextch != collator.m_contractionIndex_[offset] || cc2 == prevCC) {
                    if (this.m_utilSkippedBuffer_.length() != 1 || this.m_utilSkippedBuffer_.charAt(0) != nextch && this.m_bufferOffset_ < 0) {
                        this.m_utilSkippedBuffer_.append(nextch);
                    }
                    offset = entryoffset;
                    continue;
                }
                ce2 = collator.m_contractionCE_[offset];
                if (ce2 == -268435456) break block17;
                if (!this.isContractionTag(ce2)) break;
                offset = this.getContractionOffset(collator, ce2);
                if (collator.m_contractionCE_[offset] == -268435456) continue;
                multicontraction = true;
                this.backupInternalState(this.m_utilSpecialDiscontiguousBackUp_);
            }
            this.setDiscontiguous(this.m_utilSkippedBuffer_);
            return ce2;
        }
        this.updateInternalState(this.m_utilSpecialDiscontiguousBackUp_);
        this.previousChar();
        return collator.m_contractionCE_[entryoffset];
    }

    private int nextContraction(RuleBasedCollator collator, int ce2) {
        this.backupInternalState(this.m_utilSpecialBackUp_);
        int entryce = collator.m_contractionCE_[this.getContractionOffset(collator, ce2)];
        while (true) {
            int entryoffset;
            int offset = entryoffset = this.getContractionOffset(collator, ce2);
            if (this.isEnd()) {
                ce2 = collator.m_contractionCE_[offset];
                if (ce2 != -268435456) break;
                ce2 = entryce;
                this.updateInternalState(this.m_utilSpecialBackUp_);
                break;
            }
            int maxCC = collator.m_contractionIndex_[offset] & 0xFF;
            byte allSame = (byte)(collator.m_contractionIndex_[offset] >> 8);
            int ch = this.nextChar();
            ++offset;
            while (ch > collator.m_contractionIndex_[offset]) {
                ++offset;
            }
            if (ch == collator.m_contractionIndex_[offset]) {
                ce2 = collator.m_contractionCE_[offset];
            } else {
                int sCC;
                int miss = ch;
                if (UTF16.isLeadSurrogate((char)ch) && !this.isEnd()) {
                    char surrNextChar = (char)this.nextChar();
                    if (UTF16.isTrailSurrogate(surrNextChar)) {
                        miss = UCharacterProperty.getRawSupplementary((char)ch, surrNextChar);
                    } else {
                        this.previousChar();
                    }
                }
                if (maxCC == 0 || (sCC = this.getCombiningClass(miss)) == 0 || sCC > maxCC || allSame != 0 && sCC == maxCC || this.isEnd()) {
                    this.previousChar();
                    if (miss > 65535) {
                        this.previousChar();
                    }
                    ce2 = collator.m_contractionCE_[entryoffset];
                } else {
                    char nextch;
                    int ch_int = this.nextChar();
                    if (ch_int != -1) {
                        this.previousChar();
                    }
                    if (this.getCombiningClass(nextch = (char)ch_int) == 0) {
                        this.previousChar();
                        if (miss > 65535) {
                            this.previousChar();
                        }
                        ce2 = collator.m_contractionCE_[entryoffset];
                    } else {
                        ce2 = this.nextDiscontiguous(collator, entryoffset);
                    }
                }
            }
            if (ce2 == -268435456) {
                this.updateInternalState(this.m_utilSpecialBackUp_);
                ce2 = entryce;
                break;
            }
            if (!this.isContractionTag(ce2)) break;
            if (collator.m_contractionCE_[entryoffset] == -268435456) continue;
            entryce = collator.m_contractionCE_[entryoffset];
            this.backupInternalState(this.m_utilSpecialBackUp_);
            if (this.m_utilSpecialBackUp_.m_bufferOffset_ >= 0) {
                --this.m_utilSpecialBackUp_.m_bufferOffset_;
                continue;
            }
            --this.m_utilSpecialBackUp_.m_offset_;
        }
        return ce2;
    }

    private int nextLongPrimary(int ce2) {
        this.m_CEBuffer_[1] = (ce2 & 0xFF) << 24 | 0xC0;
        this.m_CEBufferOffset_ = 1;
        this.m_CEBufferSize_ = 2;
        this.m_CEBuffer_[0] = (ce2 & 0xFFFF00) << 8 | 0x500 | 5;
        return this.m_CEBuffer_[0];
    }

    private int getExpansionCount(int ce2) {
        return ce2 & 0xF;
    }

    private int nextExpansion(RuleBasedCollator collator, int ce2) {
        int offset = this.getExpansionOffset(collator, ce2);
        this.m_CEBufferSize_ = this.getExpansionCount(ce2);
        this.m_CEBufferOffset_ = 1;
        this.m_CEBuffer_[0] = collator.m_expansion_[offset];
        if (this.m_CEBufferSize_ != 0) {
            for (int i2 = 1; i2 < this.m_CEBufferSize_; ++i2) {
                this.m_CEBuffer_[i2] = collator.m_expansion_[offset + i2];
            }
        } else {
            this.m_CEBufferSize_ = 1;
            while (collator.m_expansion_[offset] != 0) {
                this.m_CEBuffer_[this.m_CEBufferSize_++] = collator.m_expansion_[++offset];
            }
        }
        if (this.m_CEBufferSize_ == 1) {
            this.m_CEBufferSize_ = 0;
            this.m_CEBufferOffset_ = 0;
        }
        return this.m_CEBuffer_[0];
    }

    private int nextDigit(RuleBasedCollator collator, int ce2, int cp2) {
        if (this.m_collator_.m_isNumericCollation_) {
            int i2;
            int endIndex;
            int digIndx;
            boolean nonZeroValReached;
            int trailingZeroIndex;
            block19: {
                int char32;
                int collateVal = 0;
                trailingZeroIndex = 0;
                nonZeroValReached = false;
                this.m_utilStringBuffer_.setLength(3);
                int digVal = UCharacter.digit(cp2);
                digIndx = 1;
                do {
                    if (digIndx >= this.m_utilStringBuffer_.length() - 2 << 1) {
                        this.m_utilStringBuffer_.setLength(this.m_utilStringBuffer_.length() << 1);
                    }
                    if (digVal != 0 || nonZeroValReached) {
                        if (digVal != 0 && !nonZeroValReached) {
                            nonZeroValReached = true;
                        }
                        if (digIndx % 2 != 0) {
                            if ((collateVal += digVal) == 0 && trailingZeroIndex == 0) {
                                trailingZeroIndex = (digIndx - 1 >>> 1) + 2;
                            } else if (trailingZeroIndex != 0) {
                                trailingZeroIndex = 0;
                            }
                            this.m_utilStringBuffer_.setCharAt((digIndx - 1 >>> 1) + 2, (char)((collateVal << 1) + 6));
                            collateVal = 0;
                        } else {
                            collateVal = digVal * 10;
                            if (collateVal == 0) {
                                if (trailingZeroIndex != 0) {
                                    trailingZeroIndex = (digIndx >>> 1) + 2;
                                }
                            } else {
                                trailingZeroIndex = 0;
                            }
                            this.m_utilStringBuffer_.setCharAt((digIndx >>> 1) + 2, (char)((collateVal << 1) + 6));
                        }
                        ++digIndx;
                    }
                    if (this.isEnd()) break block19;
                    this.backupInternalState(this.m_utilSpecialBackUp_);
                    char32 = this.nextChar();
                    char ch = (char)char32;
                    if (!UTF16.isLeadSurrogate(ch) || this.isEnd()) continue;
                    char trail = (char)this.nextChar();
                    if (UTF16.isTrailSurrogate(trail)) {
                        char32 = UCharacterProperty.getRawSupplementary(ch, trail);
                        continue;
                    }
                    this.goBackOne();
                } while ((digVal = UCharacter.digit(char32)) != -1);
                this.updateInternalState(this.m_utilSpecialBackUp_);
            }
            if (!nonZeroValReached) {
                digIndx = 2;
                this.m_utilStringBuffer_.setCharAt(2, '\u0006');
            }
            int n2 = endIndex = trailingZeroIndex != 0 ? trailingZeroIndex : (digIndx >>> 1) + 2;
            if (digIndx % 2 != 0) {
                for (i2 = 2; i2 < endIndex; ++i2) {
                    this.m_utilStringBuffer_.setCharAt(i2, (char)(((this.m_utilStringBuffer_.charAt(i2) - 6 >>> 1) % 10 * 10 + (this.m_utilStringBuffer_.charAt(i2 + 1) - 6 >>> 1) / 10 << 1) + 6));
                }
                --digIndx;
            }
            this.m_utilStringBuffer_.setCharAt(endIndex - 1, (char)(this.m_utilStringBuffer_.charAt(endIndex - 1) - '\u0001'));
            this.m_utilStringBuffer_.setCharAt(0, '\u0012');
            this.m_utilStringBuffer_.setCharAt(1, (char)(128 + (digIndx >>> 1 & 0x7F)));
            ce2 = (this.m_utilStringBuffer_.charAt(0) << 8 | this.m_utilStringBuffer_.charAt(1)) << 16 | 0x500 | 5;
            i2 = 2;
            this.m_CEBuffer_[0] = ce2;
            this.m_CEBufferSize_ = 1;
            this.m_CEBufferOffset_ = 1;
            while (i2 < endIndex) {
                int primWeight = this.m_utilStringBuffer_.charAt(i2++) << 8;
                if (i2 < endIndex) {
                    primWeight |= this.m_utilStringBuffer_.charAt(i2++);
                }
                this.m_CEBuffer_[this.m_CEBufferSize_++] = primWeight << 16 | 0xC0;
            }
            return ce2;
        }
        return collator.m_expansion_[this.getExpansionOffset(collator, ce2)];
    }

    private int nextImplicit(int codepoint) {
        int result = RuleBasedCollator.impCEGen_.getImplicitFromCodePoint(codepoint);
        this.m_CEBuffer_[0] = result & 0xFFFF0000 | 0x505;
        this.m_CEBuffer_[1] = (result & 0xFFFF) << 16 | 0xC0;
        this.m_CEBufferOffset_ = 1;
        this.m_CEBufferSize_ = 2;
        return this.m_CEBuffer_[0];
    }

    private int nextSurrogate(char ch) {
        int ch_int = this.nextChar();
        char nextch = (char)ch_int;
        if (ch_int != 65535 && UTF16.isTrailSurrogate(nextch)) {
            int codepoint = UCharacterProperty.getRawSupplementary(ch, nextch);
            return this.nextImplicit(codepoint);
        }
        if (nextch != '\uffff') {
            this.previousChar();
        }
        return -268435456;
    }

    private int nextHangul(RuleBasedCollator collator, char ch) {
        char L = (char)(ch - 44032);
        char T = (char)(L % 28);
        L = (char)(L / 28);
        char V = (char)(L % 21);
        L = (char)(L / 21);
        L = (char)(L + 4352);
        V = (char)(V + 4449);
        T = (char)(T + 4519);
        this.m_CEBufferSize_ = 0;
        if (!this.m_collator_.m_isJamoSpecial_) {
            this.m_CEBuffer_[this.m_CEBufferSize_++] = collator.m_trie_.getLeadValue(L);
            this.m_CEBuffer_[this.m_CEBufferSize_++] = collator.m_trie_.getLeadValue(V);
            if (T != '\u11a7') {
                this.m_CEBuffer_[this.m_CEBufferSize_++] = collator.m_trie_.getLeadValue(T);
            }
            this.m_CEBufferOffset_ = 1;
            return this.m_CEBuffer_[0];
        }
        this.m_buffer_.append(L);
        this.m_buffer_.append(V);
        if (T != '\u11a7') {
            this.m_buffer_.append(T);
        }
        this.m_bufferOffset_ = 0;
        this.m_FCDLimit_ = this.m_source_.getIndex();
        this.m_FCDStart_ = this.m_FCDLimit_ - 1;
        return 0;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private int nextSpecial(RuleBasedCollator collator, int ce2, char ch) {
        int codepoint = ch;
        Backup entrybackup = this.m_utilSpecialEntryBackUp_;
        if (entrybackup != null) {
            this.m_utilSpecialEntryBackUp_ = null;
        } else {
            entrybackup = new Backup();
        }
        this.backupInternalState(entrybackup);
        try {
            do {
                switch (RuleBasedCollator.getTag(ce2)) {
                    case 0: {
                        int n2 = ce2;
                        return n2;
                    }
                    case 5: {
                        int n2;
                        if (this.isEnd()) {
                            n2 = -268435456;
                            return n2;
                        }
                        this.backupInternalState(this.m_utilSpecialBackUp_);
                        char trail = (char)this.nextChar();
                        ce2 = this.nextSurrogate(collator, ce2, trail);
                        codepoint = UCharacterProperty.getRawSupplementary((char)ch, trail);
                        break;
                    }
                    case 11: {
                        ce2 = this.nextSpecialPrefix(collator, ce2, entrybackup);
                        break;
                    }
                    case 2: {
                        ce2 = this.nextContraction(collator, ce2);
                        break;
                    }
                    case 12: {
                        int n3 = this.nextLongPrimary(ce2);
                        return n3;
                    }
                    case 1: {
                        int n4 = this.nextExpansion(collator, ce2);
                        return n4;
                    }
                    case 13: {
                        ce2 = this.nextDigit(collator, ce2, codepoint);
                        break;
                    }
                    case 9: {
                        int n5 = this.nextImplicit(codepoint);
                        return n5;
                    }
                    case 10: {
                        int n6 = this.nextImplicit(codepoint);
                        return n6;
                    }
                    case 8: {
                        int n7 = -268435456;
                        return n7;
                    }
                    case 7: {
                        int n8 = this.nextSurrogate((char)ch);
                        return n8;
                    }
                    case 6: {
                        int n9 = this.nextHangul(collator, (char)ch);
                        return n9;
                    }
                    case 4: {
                        int n10 = -268435456;
                        return n10;
                    }
                    default: {
                        ce2 = 0;
                    }
                }
            } while (RuleBasedCollator.isSpecial(ce2));
        }
        finally {
            this.m_utilSpecialEntryBackUp_ = entrybackup;
        }
        return ce2;
    }

    private int previousSpecialPrefix(RuleBasedCollator collator, int ce2) {
        this.backupInternalState(this.m_utilSpecialBackUp_);
        while (true) {
            int offset;
            int entryoffset = offset = this.getContractionOffset(collator, ce2);
            if (this.isBackwardsStart()) {
                ce2 = collator.m_contractionCE_[offset];
                break;
            }
            char prevch = (char)this.previousChar();
            while (prevch > collator.m_contractionIndex_[offset]) {
                ++offset;
            }
            if (prevch == collator.m_contractionIndex_[offset]) {
                ce2 = collator.m_contractionCE_[offset];
            } else {
                int isZeroCE = collator.m_trie_.getLeadValue(prevch);
                if (isZeroCE == 0) continue;
                if (UTF16.isTrailSurrogate(prevch) || UTF16.isLeadSurrogate(prevch)) {
                    if (this.isBackwardsStart()) continue;
                    char lead = (char)this.previousChar();
                    if (UTF16.isLeadSurrogate(lead)) {
                        int finalCE;
                        isZeroCE = collator.m_trie_.getLeadValue(lead);
                        if (RuleBasedCollator.getTag(isZeroCE) == 5 && (finalCE = collator.m_trie_.getTrailValue(isZeroCE, prevch)) == 0) {
                            continue;
                        }
                    } else {
                        this.nextChar();
                        continue;
                    }
                    this.nextChar();
                }
                ce2 = collator.m_contractionCE_[entryoffset];
            }
            if (!this.isSpecialPrefixTag(ce2)) break;
        }
        this.updateInternalState(this.m_utilSpecialBackUp_);
        return ce2;
    }

    private int previousContraction(RuleBasedCollator collator, int ce2, char ch) {
        this.m_utilStringBuffer_.setLength(0);
        char prevch = (char)this.previousChar();
        boolean atStart = false;
        while (collator.isUnsafe(ch)) {
            this.m_utilStringBuffer_.insert(0, ch);
            ch = prevch;
            if (this.isBackwardsStart()) {
                atStart = true;
                break;
            }
            prevch = (char)this.previousChar();
        }
        if (!atStart) {
            this.nextChar();
        }
        this.m_utilStringBuffer_.insert(0, ch);
        int originaldecomp = collator.getDecomposition();
        collator.setDecomposition(16);
        if (this.m_utilColEIter_ == null) {
            this.m_utilColEIter_ = new CollationElementIterator(this.m_utilStringBuffer_.toString(), collator);
        } else {
            this.m_utilColEIter_.m_collator_ = collator;
            this.m_utilColEIter_.setText(this.m_utilStringBuffer_.toString());
        }
        ce2 = this.m_utilColEIter_.next();
        this.m_CEBufferSize_ = 0;
        while (ce2 != -1) {
            if (this.m_CEBufferSize_ == this.m_CEBuffer_.length) {
                try {
                    int[] tempbuffer = new int[this.m_CEBuffer_.length + 50];
                    System.arraycopy(this.m_CEBuffer_, 0, tempbuffer, 0, this.m_CEBuffer_.length);
                    this.m_CEBuffer_ = tempbuffer;
                }
                catch (MissingResourceException e2) {
                    throw e2;
                }
                catch (Exception e3) {
                    if (DEBUG) {
                        e3.printStackTrace();
                    }
                    return -1;
                }
            }
            this.m_CEBuffer_[this.m_CEBufferSize_++] = ce2;
            ce2 = this.m_utilColEIter_.next();
        }
        collator.setDecomposition(originaldecomp);
        this.m_CEBufferOffset_ = this.m_CEBufferSize_ - 1;
        return this.m_CEBuffer_[this.m_CEBufferOffset_];
    }

    private int previousLongPrimary(int ce2) {
        this.m_CEBufferSize_ = 0;
        this.m_CEBuffer_[this.m_CEBufferSize_++] = (ce2 & 0xFFFF00) << 8 | 0x500 | 5;
        this.m_CEBuffer_[this.m_CEBufferSize_++] = (ce2 & 0xFF) << 24 | 0xC0;
        this.m_CEBufferOffset_ = this.m_CEBufferSize_ - 1;
        return this.m_CEBuffer_[this.m_CEBufferOffset_];
    }

    private int previousExpansion(RuleBasedCollator collator, int ce2) {
        int offset = this.getExpansionOffset(collator, ce2);
        this.m_CEBufferSize_ = this.getExpansionCount(ce2);
        if (this.m_CEBufferSize_ != 0) {
            for (int i2 = 0; i2 < this.m_CEBufferSize_; ++i2) {
                this.m_CEBuffer_[i2] = collator.m_expansion_[offset + i2];
            }
        } else {
            while (collator.m_expansion_[offset + this.m_CEBufferSize_] != 0) {
                this.m_CEBuffer_[this.m_CEBufferSize_] = collator.m_expansion_[offset + this.m_CEBufferSize_];
                ++this.m_CEBufferSize_;
            }
        }
        this.m_CEBufferOffset_ = this.m_CEBufferSize_ - 1;
        return this.m_CEBuffer_[this.m_CEBufferOffset_];
    }

    private int previousDigit(RuleBasedCollator collator, int ce2, char ch) {
        if (this.m_collator_.m_isNumericCollation_) {
            int digIndx;
            boolean nonZeroValReached;
            int collateVal;
            int leadingZeroIndex;
            block20: {
                leadingZeroIndex = 0;
                collateVal = 0;
                nonZeroValReached = false;
                this.m_utilStringBuffer_.setLength(3);
                int char32 = ch;
                if (UTF16.isTrailSurrogate((char)ch) && !this.isBackwardsStart()) {
                    char lead = (char)this.previousChar();
                    if (UTF16.isLeadSurrogate(lead)) {
                        char32 = UCharacterProperty.getRawSupplementary(lead, (char)ch);
                    } else {
                        this.goForwardOne();
                    }
                }
                int digVal = UCharacter.digit(char32);
                digIndx = 0;
                do {
                    if (digIndx >= this.m_utilStringBuffer_.length() - 2 << 1) {
                        this.m_utilStringBuffer_.setLength(this.m_utilStringBuffer_.length() << 1);
                    }
                    if (digVal != 0 || nonZeroValReached) {
                        if (digVal != 0 && !nonZeroValReached) {
                            nonZeroValReached = true;
                        }
                        if (digIndx % 2 != 0) {
                            if ((collateVal += digVal * 10) == 0 && leadingZeroIndex == 0) {
                                leadingZeroIndex = (digIndx - 1 >>> 1) + 2;
                            } else if (leadingZeroIndex != 0) {
                                leadingZeroIndex = 0;
                            }
                            this.m_utilStringBuffer_.setCharAt((digIndx - 1 >>> 1) + 2, (char)((collateVal << 1) + 6));
                            collateVal = 0;
                        } else {
                            collateVal = digVal;
                        }
                    }
                    ++digIndx;
                    if (this.isBackwardsStart()) break block20;
                    this.backupInternalState(this.m_utilSpecialBackUp_);
                    char32 = this.previousChar();
                    if (!UTF16.isTrailSurrogate((char)ch) || this.isBackwardsStart()) continue;
                    char lead = (char)this.previousChar();
                    if (UTF16.isLeadSurrogate(lead)) {
                        char32 = UCharacterProperty.getRawSupplementary(lead, (char)ch);
                        continue;
                    }
                    this.updateInternalState(this.m_utilSpecialBackUp_);
                } while ((digVal = UCharacter.digit(char32)) != -1);
                this.updateInternalState(this.m_utilSpecialBackUp_);
            }
            if (!nonZeroValReached) {
                digIndx = 2;
                this.m_utilStringBuffer_.setCharAt(2, '\u0006');
            }
            if (digIndx % 2 != 0) {
                if (collateVal == 0 && leadingZeroIndex == 0) {
                    leadingZeroIndex = (digIndx - 1 >>> 1) + 2;
                } else {
                    this.m_utilStringBuffer_.setCharAt((digIndx >>> 1) + 2, (char)((collateVal << 1) + 6));
                    ++digIndx;
                }
            }
            int endIndex = leadingZeroIndex != 0 ? leadingZeroIndex : (digIndx >>> 1) + 2;
            digIndx = (endIndex - 2 << 1) + 1;
            this.m_utilStringBuffer_.setCharAt(2, (char)(this.m_utilStringBuffer_.charAt(2) - '\u0001'));
            this.m_utilStringBuffer_.setCharAt(0, '\u0012');
            this.m_utilStringBuffer_.setCharAt(1, (char)(128 + (digIndx >>> 1 & 0x7F)));
            this.m_CEBufferSize_ = 0;
            this.m_CEBuffer_[this.m_CEBufferSize_++] = (this.m_utilStringBuffer_.charAt(0) << 8 | this.m_utilStringBuffer_.charAt(1)) << 16 | 0x500 | 5;
            int i2 = endIndex - 1;
            while (i2 >= 2) {
                int primWeight = this.m_utilStringBuffer_.charAt(i2--) << 8;
                if (i2 >= 2) {
                    primWeight |= this.m_utilStringBuffer_.charAt(i2--);
                }
                this.m_CEBuffer_[this.m_CEBufferSize_++] = primWeight << 16 | 0xC0;
            }
            this.m_CEBufferOffset_ = this.m_CEBufferSize_ - 1;
            return this.m_CEBuffer_[this.m_CEBufferOffset_];
        }
        return collator.m_expansion_[this.getExpansionOffset(collator, ce2)];
    }

    private int previousHangul(RuleBasedCollator collator, char ch) {
        char L = (char)(ch - 44032);
        char T = (char)(L % 28);
        L = (char)(L / 28);
        char V = (char)(L % 21);
        L = (char)(L / 21);
        L = (char)(L + 4352);
        V = (char)(V + 4449);
        T = (char)(T + 4519);
        this.m_CEBufferSize_ = 0;
        if (!this.m_collator_.m_isJamoSpecial_) {
            this.m_CEBuffer_[this.m_CEBufferSize_++] = collator.m_trie_.getLeadValue(L);
            this.m_CEBuffer_[this.m_CEBufferSize_++] = collator.m_trie_.getLeadValue(V);
            if (T != '\u11a7') {
                this.m_CEBuffer_[this.m_CEBufferSize_++] = collator.m_trie_.getLeadValue(T);
            }
            this.m_CEBufferOffset_ = this.m_CEBufferSize_ - 1;
            return this.m_CEBuffer_[this.m_CEBufferOffset_];
        }
        this.m_buffer_.append(L);
        this.m_buffer_.append(V);
        if (T != '\u11a7') {
            this.m_buffer_.append(T);
        }
        this.m_bufferOffset_ = this.m_buffer_.length();
        this.m_FCDStart_ = this.m_source_.getIndex();
        this.m_FCDLimit_ = this.m_FCDStart_ + 1;
        return 0;
    }

    private int previousImplicit(int codepoint) {
        int result = RuleBasedCollator.impCEGen_.getImplicitFromCodePoint(codepoint);
        this.m_CEBufferSize_ = 2;
        this.m_CEBufferOffset_ = 1;
        this.m_CEBuffer_[0] = result & 0xFFFF0000 | 0x505;
        this.m_CEBuffer_[1] = (result & 0xFFFF) << 16 | 0xC0;
        return this.m_CEBuffer_[1];
    }

    private int previousSurrogate(char ch) {
        if (this.isBackwardsStart()) {
            return -268435456;
        }
        char prevch = (char)this.previousChar();
        if (UTF16.isLeadSurrogate(prevch)) {
            return this.previousImplicit(UCharacterProperty.getRawSupplementary(prevch, ch));
        }
        if (prevch != '\uffff') {
            this.nextChar();
        }
        return -268435456;
    }

    private int previousSpecial(RuleBasedCollator collator, int ce2, char ch) {
        do {
            switch (RuleBasedCollator.getTag(ce2)) {
                case 0: {
                    return ce2;
                }
                case 5: {
                    return -268435456;
                }
                case 11: {
                    ce2 = this.previousSpecialPrefix(collator, ce2);
                    break;
                }
                case 2: {
                    if (this.isBackwardsStart()) {
                        ce2 = collator.m_contractionCE_[this.getContractionOffset(collator, ce2)];
                        break;
                    }
                    return this.previousContraction(collator, ce2, ch);
                }
                case 12: {
                    return this.previousLongPrimary(ce2);
                }
                case 1: {
                    return this.previousExpansion(collator, ce2);
                }
                case 13: {
                    ce2 = this.previousDigit(collator, ce2, ch);
                    break;
                }
                case 6: {
                    return this.previousHangul(collator, ch);
                }
                case 7: {
                    return -268435456;
                }
                case 8: {
                    return this.previousSurrogate(ch);
                }
                case 9: {
                    return this.previousImplicit(ch);
                }
                case 10: {
                    return this.previousImplicit(ch);
                }
                case 4: {
                    return -268435456;
                }
                default: {
                    ce2 = 0;
                }
            }
        } while (RuleBasedCollator.isSpecial(ce2));
        return ce2;
    }

    private void goBackOne() {
        if (this.m_bufferOffset_ >= 0) {
            --this.m_bufferOffset_;
        } else {
            this.m_source_.setIndex(this.m_source_.getIndex() - 1);
        }
    }

    private void goForwardOne() {
        if (this.m_bufferOffset_ < 0) {
            this.m_source_.setIndex(this.m_source_.getIndex() + 1);
        } else {
            ++this.m_bufferOffset_;
        }
    }

    private static final class Backup {
        protected int m_FCDLimit_;
        protected int m_FCDStart_;
        protected boolean m_isCodePointHiragana_;
        protected int m_bufferOffset_;
        protected int m_offset_;
        protected StringBuffer m_buffer_ = new StringBuffer();

        protected Backup() {
        }
    }
}

