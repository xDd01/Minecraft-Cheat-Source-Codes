package javassist.compiler;

import java.util.*;

public final class KeywordTable extends HashMap<String, Integer>
{
    private static final long serialVersionUID = 1L;
    
    public int lookup(final String name) {
        return this.containsKey(name) ? ((HashMap<K, Integer>)this).get(name) : -1;
    }
    
    public void append(final String name, final int t) {
        this.put(name, t);
    }
}
