package org.apache.commons.lang3.builder;

import java.lang.annotation.*;
import java.lang.reflect.*;
import org.apache.commons.lang3.*;
import java.util.*;

public class HashCodeBuilder implements Builder<Integer>
{
    private static final int DEFAULT_INITIAL_VALUE = 17;
    private static final int DEFAULT_MULTIPLIER_VALUE = 37;
    private static final ThreadLocal<Set<IDKey>> REGISTRY;
    private final int iConstant;
    private int iTotal;
    
    static Set<IDKey> getRegistry() {
        return HashCodeBuilder.REGISTRY.get();
    }
    
    static boolean isRegistered(final Object value) {
        final Set<IDKey> registry = getRegistry();
        return registry != null && registry.contains(new IDKey(value));
    }
    
    private static void reflectionAppend(final Object object, final Class<?> clazz, final HashCodeBuilder builder, final boolean useTransients, final String[] excludeFields) {
        if (isRegistered(object)) {
            return;
        }
        try {
            register(object);
            final Field[] fields = clazz.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            for (final Field field : fields) {
                if (!ArrayUtils.contains(excludeFields, field.getName()) && !field.getName().contains("$") && (useTransients || !Modifier.isTransient(field.getModifiers())) && !Modifier.isStatic(field.getModifiers()) && !field.isAnnotationPresent(HashCodeExclude.class)) {
                    try {
                        final Object fieldValue = field.get(object);
                        builder.append(fieldValue);
                    }
                    catch (IllegalAccessException e) {
                        throw new InternalError("Unexpected IllegalAccessException");
                    }
                }
            }
        }
        finally {
            unregister(object);
        }
    }
    
    public static int reflectionHashCode(final int initialNonZeroOddNumber, final int multiplierNonZeroOddNumber, final Object object) {
        return reflectionHashCode(initialNonZeroOddNumber, multiplierNonZeroOddNumber, object, false, null, new String[0]);
    }
    
    public static int reflectionHashCode(final int initialNonZeroOddNumber, final int multiplierNonZeroOddNumber, final Object object, final boolean testTransients) {
        return reflectionHashCode(initialNonZeroOddNumber, multiplierNonZeroOddNumber, object, testTransients, null, new String[0]);
    }
    
    public static <T> int reflectionHashCode(final int initialNonZeroOddNumber, final int multiplierNonZeroOddNumber, final T object, final boolean testTransients, final Class<? super T> reflectUpToClass, final String... excludeFields) {
        Validate.isTrue(object != null, "The object to build a hash code for must not be null", new Object[0]);
        final HashCodeBuilder builder = new HashCodeBuilder(initialNonZeroOddNumber, multiplierNonZeroOddNumber);
        Class<?> clazz = object.getClass();
        reflectionAppend(object, clazz, builder, testTransients, excludeFields);
        while (clazz.getSuperclass() != null && clazz != reflectUpToClass) {
            clazz = clazz.getSuperclass();
            reflectionAppend(object, clazz, builder, testTransients, excludeFields);
        }
        return builder.toHashCode();
    }
    
    public static int reflectionHashCode(final Object object, final boolean testTransients) {
        return reflectionHashCode(17, 37, object, testTransients, null, new String[0]);
    }
    
    public static int reflectionHashCode(final Object object, final Collection<String> excludeFields) {
        return reflectionHashCode(object, ReflectionToStringBuilder.toNoNullStringArray(excludeFields));
    }
    
    public static int reflectionHashCode(final Object object, final String... excludeFields) {
        return reflectionHashCode(17, 37, object, false, null, excludeFields);
    }
    
    private static void register(final Object value) {
        Set<IDKey> registry = getRegistry();
        if (registry == null) {
            registry = new HashSet<IDKey>();
            HashCodeBuilder.REGISTRY.set(registry);
        }
        registry.add(new IDKey(value));
    }
    
    private static void unregister(final Object value) {
        final Set<IDKey> registry = getRegistry();
        if (registry != null) {
            registry.remove(new IDKey(value));
            if (registry.isEmpty()) {
                HashCodeBuilder.REGISTRY.remove();
            }
        }
    }
    
    public HashCodeBuilder() {
        this.iTotal = 0;
        this.iConstant = 37;
        this.iTotal = 17;
    }
    
    public HashCodeBuilder(final int initialOddNumber, final int multiplierOddNumber) {
        this.iTotal = 0;
        Validate.isTrue(initialOddNumber % 2 != 0, "HashCodeBuilder requires an odd initial value", new Object[0]);
        Validate.isTrue(multiplierOddNumber % 2 != 0, "HashCodeBuilder requires an odd multiplier", new Object[0]);
        this.iConstant = multiplierOddNumber;
        this.iTotal = initialOddNumber;
    }
    
    public HashCodeBuilder append(final boolean value) {
        this.iTotal = this.iTotal * this.iConstant + (value ? 0 : 1);
        return this;
    }
    
    public HashCodeBuilder append(final boolean[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (final boolean element : array) {
                this.append(element);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final byte value) {
        this.iTotal = this.iTotal * this.iConstant + value;
        return this;
    }
    
    public HashCodeBuilder append(final byte[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (final byte element : array) {
                this.append(element);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final char value) {
        this.iTotal = this.iTotal * this.iConstant + value;
        return this;
    }
    
    public HashCodeBuilder append(final char[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (final char element : array) {
                this.append(element);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final double value) {
        return this.append(Double.doubleToLongBits(value));
    }
    
    public HashCodeBuilder append(final double[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (final double element : array) {
                this.append(element);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final float value) {
        this.iTotal = this.iTotal * this.iConstant + Float.floatToIntBits(value);
        return this;
    }
    
    public HashCodeBuilder append(final float[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (final float element : array) {
                this.append(element);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final int value) {
        this.iTotal = this.iTotal * this.iConstant + value;
        return this;
    }
    
    public HashCodeBuilder append(final int[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (final int element : array) {
                this.append(element);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final long value) {
        this.iTotal = this.iTotal * this.iConstant + (int)(value ^ value >> 32);
        return this;
    }
    
    public HashCodeBuilder append(final long[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (final long element : array) {
                this.append(element);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final Object object) {
        if (object == null) {
            this.iTotal *= this.iConstant;
        }
        else if (object.getClass().isArray()) {
            this.appendArray(object);
        }
        else {
            this.iTotal = this.iTotal * this.iConstant + object.hashCode();
        }
        return this;
    }
    
    private void appendArray(final Object object) {
        if (object instanceof long[]) {
            this.append((long[])object);
        }
        else if (object instanceof int[]) {
            this.append((int[])object);
        }
        else if (object instanceof short[]) {
            this.append((short[])object);
        }
        else if (object instanceof char[]) {
            this.append((char[])object);
        }
        else if (object instanceof byte[]) {
            this.append((byte[])object);
        }
        else if (object instanceof double[]) {
            this.append((double[])object);
        }
        else if (object instanceof float[]) {
            this.append((float[])object);
        }
        else if (object instanceof boolean[]) {
            this.append((boolean[])object);
        }
        else {
            this.append((Object[])object);
        }
    }
    
    public HashCodeBuilder append(final Object[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (final Object element : array) {
                this.append(element);
            }
        }
        return this;
    }
    
    public HashCodeBuilder append(final short value) {
        this.iTotal = this.iTotal * this.iConstant + value;
        return this;
    }
    
    public HashCodeBuilder append(final short[] array) {
        if (array == null) {
            this.iTotal *= this.iConstant;
        }
        else {
            for (final short element : array) {
                this.append(element);
            }
        }
        return this;
    }
    
    public HashCodeBuilder appendSuper(final int superHashCode) {
        this.iTotal = this.iTotal * this.iConstant + superHashCode;
        return this;
    }
    
    public int toHashCode() {
        return this.iTotal;
    }
    
    @Override
    public Integer build() {
        return this.toHashCode();
    }
    
    @Override
    public int hashCode() {
        return this.toHashCode();
    }
    
    static {
        REGISTRY = new ThreadLocal<Set<IDKey>>();
    }
}
