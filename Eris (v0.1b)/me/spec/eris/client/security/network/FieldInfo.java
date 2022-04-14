package me.spec.eris.client.security.network;

import java.lang.reflect.Field;

/**
 * Records information about fields of serializable types
 * whose readers/writers have been auto-generated.
 *
 * @author Jonathan
 */
class FieldInfo {
    private final Field field;
    private final boolean omitTypeInfo;

    /**
     * @param field        The described field.
     * @param omitTypeInfo True if the type info can be omitted for this field (runtime type known at compile time).
     */
    public FieldInfo(Field field, boolean omitTypeInfo) {
        this.field = field;
        this.omitTypeInfo = omitTypeInfo;
    }

    /**
     * @return The described field
     */
    public Field getField() {
        return field;
    }

    /**
     * @return True if the type info can be omitted for this field (runtime type known at compile time).
     */
    public boolean canOmitTypeInfo() {
        return omitTypeInfo;
    }
}