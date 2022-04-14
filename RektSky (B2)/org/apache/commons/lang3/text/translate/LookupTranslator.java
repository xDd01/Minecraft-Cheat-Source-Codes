package org.apache.commons.lang3.text.translate;

import java.util.*;
import java.io.*;

@Deprecated
public class LookupTranslator extends CharSequenceTranslator
{
    private final HashMap<String, String> lookupMap;
    private final HashSet<Character> prefixSet;
    private final int shortest;
    private final int longest;
    
    public LookupTranslator(final CharSequence[]... lookup) {
        this.lookupMap = new HashMap<String, String>();
        this.prefixSet = new HashSet<Character>();
        int _shortest = Integer.MAX_VALUE;
        int _longest = 0;
        if (lookup != null) {
            for (final CharSequence[] seq : lookup) {
                this.lookupMap.put(seq[0].toString(), seq[1].toString());
                this.prefixSet.add(seq[0].charAt(0));
                final int sz = seq[0].length();
                if (sz < _shortest) {
                    _shortest = sz;
                }
                if (sz > _longest) {
                    _longest = sz;
                }
            }
        }
        this.shortest = _shortest;
        this.longest = _longest;
    }
    
    @Override
    public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
        if (this.prefixSet.contains(input.charAt(index))) {
            int max = this.longest;
            if (index + this.longest > input.length()) {
                max = input.length() - index;
            }
            for (int i = max; i >= this.shortest; --i) {
                final CharSequence subSeq = input.subSequence(index, index + i);
                final String result = this.lookupMap.get(subSeq.toString());
                if (result != null) {
                    out.write(result);
                    return i;
                }
            }
        }
        return 0;
    }
}
