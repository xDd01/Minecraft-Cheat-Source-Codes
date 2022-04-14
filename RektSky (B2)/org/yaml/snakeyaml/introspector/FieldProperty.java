package org.yaml.snakeyaml.introspector;

import java.lang.reflect.*;
import org.yaml.snakeyaml.error.*;

public class FieldProperty extends GenericProperty
{
    private final Field field;
    
    public FieldProperty(final Field field) {
        super(field.getName(), field.getType(), field.getGenericType());
        (this.field = field).setAccessible(true);
    }
    
    @Override
    public void set(final Object object, final Object value) throws Exception {
        this.field.set(object, value);
    }
    
    @Override
    public Object get(final Object object) {
        try {
            return this.field.get(object);
        }
        catch (Exception e) {
            throw new YAMLException("Unable to access field " + this.field.getName() + " on object " + object + " : " + e);
        }
    }
}
