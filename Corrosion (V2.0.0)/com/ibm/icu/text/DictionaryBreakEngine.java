/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.text.LanguageBreakEngine;
import com.ibm.icu.text.UCharacterIterator;
import com.ibm.icu.text.UnicodeSet;
import java.text.CharacterIterator;
import java.util.Stack;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
abstract class DictionaryBreakEngine
implements LanguageBreakEngine {
    protected UnicodeSet fSet = new UnicodeSet();
    private final int fTypes;

    public DictionaryBreakEngine(int breakTypes) {
        this.fTypes = breakTypes;
    }

    @Override
    public boolean handles(int c2, int breakType) {
        return breakType >= 0 && breakType < 32 && (1 << breakType & this.fTypes) != 0 && this.fSet.contains(c2);
    }

    @Override
    public int findBreaks(CharacterIterator text_, int startPos, int endPos, boolean reverse, int breakType, Stack<Integer> foundBreaks) {
        int rangeEnd;
        int rangeStart;
        int current;
        if (breakType < 0 || breakType >= 32 || (1 << breakType & this.fTypes) == 0) {
            return 0;
        }
        int result = 0;
        UCharacterIterator text = UCharacterIterator.getInstance(text_);
        int start = text.getIndex();
        int c2 = text.current();
        if (reverse) {
            boolean isDict = this.fSet.contains(c2);
            while ((current = text.getIndex()) > startPos && isDict) {
                c2 = text.previous();
                isDict = this.fSet.contains(c2);
            }
            rangeStart = current < startPos ? startPos : current + (isDict ? 0 : 1);
            rangeEnd = start + 1;
        } else {
            while ((current = text.getIndex()) < endPos && this.fSet.contains(c2)) {
                c2 = text.next();
            }
            rangeStart = start;
            rangeEnd = current;
        }
        result = this.divideUpDictionaryRange(text, rangeStart, rangeEnd, foundBreaks);
        text.setIndex(current);
        return result;
    }

    protected abstract int divideUpDictionaryRange(UCharacterIterator var1, int var2, int var3, Stack<Integer> var4);
}

