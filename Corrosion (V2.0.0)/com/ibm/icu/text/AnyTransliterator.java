/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.text;

import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.CompoundTransliterator;
import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.Transliterator;
import com.ibm.icu.text.TransliteratorIDParser;
import com.ibm.icu.text.UnicodeFilter;
import com.ibm.icu.text.UnicodeSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
class AnyTransliterator
extends Transliterator {
    static final char TARGET_SEP = '-';
    static final char VARIANT_SEP = '/';
    static final String ANY = "Any";
    static final String NULL_ID = "Null";
    static final String LATIN_PIVOT = "-Latin;Latin-";
    private Map<Integer, Transliterator> cache;
    private String target;
    private int targetScript;
    private Transliterator widthFix = Transliterator.getInstance("[[:dt=Nar:][:dt=Wide:]] nfkd");

    @Override
    protected void handleTransliterate(Replaceable text, Transliterator.Position pos, boolean isIncremental) {
        int allStart = pos.start;
        int allLimit = pos.limit;
        ScriptRunIterator it2 = new ScriptRunIterator(text, pos.contextStart, pos.contextLimit);
        while (it2.next()) {
            if (it2.limit <= allStart) continue;
            Transliterator t2 = this.getTransliterator(it2.scriptCode);
            if (t2 == null) {
                pos.start = it2.limit;
                continue;
            }
            boolean incremental = isIncremental && it2.limit >= allLimit;
            pos.start = Math.max(allStart, it2.start);
            int limit = pos.limit = Math.min(allLimit, it2.limit);
            t2.filteredTransliterate(text, pos, incremental);
            int delta = pos.limit - limit;
            it2.adjustLimit(delta);
            if (it2.limit < (allLimit += delta)) continue;
            break;
        }
        pos.limit = allLimit;
    }

    private AnyTransliterator(String id2, String theTarget, String theVariant, int theTargetScript) {
        super(id2, null);
        this.targetScript = theTargetScript;
        this.cache = new HashMap<Integer, Transliterator>();
        this.target = theTarget;
        if (theVariant.length() > 0) {
            this.target = theTarget + '/' + theVariant;
        }
    }

    public AnyTransliterator(String id2, UnicodeFilter filter, String target2, int targetScript2, Transliterator widthFix2, Map<Integer, Transliterator> cache2) {
        super(id2, filter);
        this.targetScript = targetScript2;
        this.cache = cache2;
        this.target = target2;
    }

    private Transliterator getTransliterator(int source) {
        if (source == this.targetScript || source == -1) {
            if (this.isWide(this.targetScript)) {
                return null;
            }
            return this.widthFix;
        }
        Integer key = source;
        Transliterator t2 = this.cache.get(key);
        if (t2 == null) {
            String sourceName = UScript.getName(source);
            String id2 = sourceName + '-' + this.target;
            try {
                t2 = Transliterator.getInstance(id2, 0);
            }
            catch (RuntimeException e2) {
                // empty catch block
            }
            if (t2 == null) {
                id2 = sourceName + LATIN_PIVOT + this.target;
                try {
                    t2 = Transliterator.getInstance(id2, 0);
                }
                catch (RuntimeException e3) {
                    // empty catch block
                }
            }
            if (t2 != null) {
                if (!this.isWide(this.targetScript)) {
                    ArrayList<Transliterator> v2 = new ArrayList<Transliterator>();
                    v2.add(this.widthFix);
                    v2.add(t2);
                    t2 = new CompoundTransliterator(v2);
                }
                this.cache.put(key, t2);
            } else if (!this.isWide(this.targetScript)) {
                return this.widthFix;
            }
        }
        return t2;
    }

    private boolean isWide(int script) {
        return script == 5 || script == 17 || script == 18 || script == 20 || script == 22;
    }

    static void register() {
        HashMap seen = new HashMap();
        Enumeration<String> s2 = Transliterator.getAvailableSources();
        while (s2.hasMoreElements()) {
            String source = s2.nextElement();
            if (source.equalsIgnoreCase(ANY)) continue;
            Enumeration<String> t2 = Transliterator.getAvailableTargets(source);
            while (t2.hasMoreElements()) {
                String target = t2.nextElement();
                int targetScript = AnyTransliterator.scriptNameToCode(target);
                if (targetScript == -1) continue;
                HashSet<String> seenVariants = (HashSet<String>)seen.get(target);
                if (seenVariants == null) {
                    seenVariants = new HashSet<String>();
                    seen.put(target, seenVariants);
                }
                Enumeration<String> v2 = Transliterator.getAvailableVariants(source, target);
                while (v2.hasMoreElements()) {
                    String variant = v2.nextElement();
                    if (seenVariants.contains(variant)) continue;
                    seenVariants.add(variant);
                    String id2 = TransliteratorIDParser.STVtoID(ANY, target, variant);
                    AnyTransliterator trans = new AnyTransliterator(id2, target, variant, targetScript);
                    Transliterator.registerInstance(trans);
                    Transliterator.registerSpecialInverse(target, NULL_ID, false);
                }
            }
        }
    }

    private static int scriptNameToCode(String name) {
        try {
            int[] codes = UScript.getCode(name);
            return codes != null ? codes[0] : -1;
        }
        catch (MissingResourceException e2) {
            return -1;
        }
    }

    public Transliterator safeClone() {
        UnicodeFilter filter = this.getFilter();
        if (filter != null && filter instanceof UnicodeSet) {
            filter = new UnicodeSet((UnicodeSet)filter);
        }
        return new AnyTransliterator(this.getID(), filter, this.target, this.targetScript, this.widthFix, this.cache);
    }

    @Override
    public void addSourceTargetSet(UnicodeSet inputFilter, UnicodeSet sourceSet, UnicodeSet targetSet) {
        UnicodeSet myFilter = this.getFilterAsUnicodeSet(inputFilter);
        sourceSet.addAll(myFilter);
        if (myFilter.size() != 0) {
            targetSet.addAll(0, 0x10FFFF);
        }
    }

    private static class ScriptRunIterator {
        private Replaceable text;
        private int textStart;
        private int textLimit;
        public int scriptCode;
        public int start;
        public int limit;

        public ScriptRunIterator(Replaceable text, int start, int limit) {
            this.text = text;
            this.textStart = start;
            this.textLimit = limit;
            this.limit = start;
        }

        public boolean next() {
            int ch;
            int s2;
            this.scriptCode = -1;
            this.start = this.limit;
            if (this.start == this.textLimit) {
                return false;
            }
            while (this.start > this.textStart && ((s2 = UScript.getScript(ch = this.text.char32At(this.start - 1))) == 0 || s2 == 1)) {
                --this.start;
            }
            while (this.limit < this.textLimit) {
                ch = this.text.char32At(this.limit);
                s2 = UScript.getScript(ch);
                if (s2 != 0 && s2 != 1) {
                    if (this.scriptCode == -1) {
                        this.scriptCode = s2;
                    } else if (s2 != this.scriptCode) break;
                }
                ++this.limit;
            }
            return true;
        }

        public void adjustLimit(int delta) {
            this.limit += delta;
            this.textLimit += delta;
        }
    }
}

