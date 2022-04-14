package org.yaml.snakeyaml.introspector;

import java.beans.*;
import org.yaml.snakeyaml.error.*;

public class MethodProperty extends GenericProperty
{
    private final PropertyDescriptor property;
    private final boolean readable;
    private final boolean writable;
    
    public MethodProperty(final PropertyDescriptor property) {
        super(property.getName(), property.getPropertyType(), (property.getReadMethod() == null) ? null : property.getReadMethod().getGenericReturnType());
        this.property = property;
        this.readable = (property.getReadMethod() != null);
        this.writable = (property.getWriteMethod() != null);
    }
    
    @Override
    public void set(final Object object, final Object value) throws Exception {
        this.property.getWriteMethod().invoke(object, value);
    }
    
    @Override
    public Object get(final Object object) {
        try {
            this.property.getReadMethod().setAccessible(true);
            return this.property.getReadMethod().invoke(object, new Object[0]);
        }
        catch (Exception e) {
            throw new YAMLException("Unable to find getter for property '" + this.property.getName() + "' on object " + object + ":" + e);
        }
    }
    
    @Override
    public boolean isWritable() {
        return this.writable;
    }
    
    @Override
    public boolean isReadable() {
        return this.readable;
    }
}
