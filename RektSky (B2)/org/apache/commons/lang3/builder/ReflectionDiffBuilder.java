package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.reflect.*;
import java.lang.reflect.*;

public class ReflectionDiffBuilder implements Builder<DiffResult>
{
    private final Object left;
    private final Object right;
    private final DiffBuilder diffBuilder;
    
    public <T> ReflectionDiffBuilder(final T lhs, final T rhs, final ToStringStyle style) {
        this.left = lhs;
        this.right = rhs;
        this.diffBuilder = new DiffBuilder(lhs, rhs, style);
    }
    
    @Override
    public DiffResult build() {
        if (this.left.equals(this.right)) {
            return this.diffBuilder.build();
        }
        this.appendFields(this.left.getClass());
        return this.diffBuilder.build();
    }
    
    private void appendFields(final Class<?> clazz) {
        for (final Field field : FieldUtils.getAllFields(clazz)) {
            if (this.accept(field)) {
                try {
                    this.diffBuilder.append(field.getName(), FieldUtils.readField(field, this.left, true), FieldUtils.readField(field, this.right, true));
                }
                catch (IllegalAccessException ex) {
                    throw new InternalError("Unexpected IllegalAccessException: " + ex.getMessage());
                }
            }
        }
    }
    
    private boolean accept(final Field field) {
        return field.getName().indexOf(36) == -1 && !Modifier.isTransient(field.getModifiers()) && !Modifier.isStatic(field.getModifiers());
    }
}
