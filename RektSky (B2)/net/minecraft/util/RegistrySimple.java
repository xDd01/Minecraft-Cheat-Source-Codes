package net.minecraft.util;

import com.google.common.collect.*;
import org.apache.commons.lang3.*;
import java.util.*;
import org.apache.logging.log4j.*;

public class RegistrySimple<K, V> implements IRegistry<K, V>
{
    private static final Logger logger;
    protected final Map<K, V> registryObjects;
    
    public RegistrySimple() {
        this.registryObjects = this.createUnderlyingMap();
    }
    
    protected Map<K, V> createUnderlyingMap() {
        return (Map<K, V>)Maps.newHashMap();
    }
    
    @Override
    public V getObject(final K name) {
        return this.registryObjects.get(name);
    }
    
    @Override
    public void putObject(final K p_82595_1_, final V p_82595_2_) {
        Validate.notNull(p_82595_1_);
        Validate.notNull(p_82595_2_);
        if (this.registryObjects.containsKey(p_82595_1_)) {
            RegistrySimple.logger.debug("Adding duplicate key '" + p_82595_1_ + "' to registry");
        }
        this.registryObjects.put(p_82595_1_, p_82595_2_);
    }
    
    public Set<K> getKeys() {
        return Collections.unmodifiableSet((Set<? extends K>)this.registryObjects.keySet());
    }
    
    public boolean containsKey(final K p_148741_1_) {
        return this.registryObjects.containsKey(p_148741_1_);
    }
    
    @Override
    public Iterator<V> iterator() {
        return this.registryObjects.values().iterator();
    }
    
    static {
        logger = LogManager.getLogger();
    }
}
