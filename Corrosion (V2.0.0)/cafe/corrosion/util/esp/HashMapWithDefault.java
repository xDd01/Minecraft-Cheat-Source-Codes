/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.esp;

import java.util.HashMap;

public class HashMapWithDefault<K, V>
extends HashMap<K, V> {
    private static final long serialVersionUID = 5995791692010816132L;
    private V defaultValue;

    public void setDefault(V value) {
        this.defaultValue = value;
    }

    public V getDefault() {
        return this.defaultValue;
    }

    @Override
    public V get(Object key) {
        if (this.containsKey(key)) {
            return super.get(key);
        }
        return this.defaultValue;
    }
}

