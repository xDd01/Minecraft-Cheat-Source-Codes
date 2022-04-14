/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.property.registry;

import cafe.corrosion.module.Module;
import cafe.corrosion.property.Property;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyRegistry {
    private final Map<Module, List<Property<?>>> propertyRegistry = new HashMap();

    public void register(Module module, Property<?> property) {
        List properties = this.propertyRegistry.getOrDefault(module, new ArrayList());
        properties.add(property);
        this.propertyRegistry.put(module, properties);
    }

    public List<Property<?>> getProperties(Module module) {
        return this.propertyRegistry.getOrDefault(module, new ArrayList());
    }
}

