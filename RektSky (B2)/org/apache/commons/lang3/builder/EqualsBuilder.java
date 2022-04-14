package org.apache.commons.lang3.builder;

import org.apache.commons.lang3.tuple.*;
import java.util.*;
import java.lang.annotation.*;
import java.lang.reflect.*;
import org.apache.commons.lang3.*;

public class EqualsBuilder implements Builder<Boolean>
{
    private static final ThreadLocal<Set<Pair<IDKey, IDKey>>> REGISTRY;
    private boolean isEquals;
    private boolean testTransients;
    private boolean testRecursive;
    private List<Class<?>> bypassReflectionClasses;
    private Class<?> reflectUpToClass;
    private String[] excludeFields;
    
    static Set<Pair<IDKey, IDKey>> getRegistry() {
        return EqualsBuilder.REGISTRY.get();
    }
    
    static Pair<IDKey, IDKey> getRegisterPair(final Object lhs, final Object rhs) {
        final IDKey left = new IDKey(lhs);
        final IDKey right = new IDKey(rhs);
        return Pair.of(left, right);
    }
    
    static boolean isRegistered(final Object lhs, final Object rhs) {
        final Set<Pair<IDKey, IDKey>> registry = getRegistry();
        final Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
        final Pair<IDKey, IDKey> swappedPair = Pair.of(pair.getRight(), pair.getLeft());
        return registry != null && (registry.contains(pair) || registry.contains(swappedPair));
    }
    
    private static void register(final Object lhs, final Object rhs) {
        Set<Pair<IDKey, IDKey>> registry = getRegistry();
        if (registry == null) {
            registry = new HashSet<Pair<IDKey, IDKey>>();
            EqualsBuilder.REGISTRY.set(registry);
        }
        final Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
        registry.add(pair);
    }
    
    private static void unregister(final Object lhs, final Object rhs) {
        final Set<Pair<IDKey, IDKey>> registry = getRegistry();
        if (registry != null) {
            final Pair<IDKey, IDKey> pair = getRegisterPair(lhs, rhs);
            registry.remove(pair);
            if (registry.isEmpty()) {
                EqualsBuilder.REGISTRY.remove();
            }
        }
    }
    
    public EqualsBuilder() {
        this.isEquals = true;
        this.testTransients = false;
        this.testRecursive = false;
        this.reflectUpToClass = null;
        this.excludeFields = null;
        (this.bypassReflectionClasses = new ArrayList<Class<?>>()).add(String.class);
    }
    
    public EqualsBuilder setTestTransients(final boolean testTransients) {
        this.testTransients = testTransients;
        return this;
    }
    
    public EqualsBuilder setTestRecursive(final boolean testRecursive) {
        this.testRecursive = testRecursive;
        return this;
    }
    
    public EqualsBuilder setBypassReflectionClasses(final List<Class<?>> bypassReflectionClasses) {
        this.bypassReflectionClasses = bypassReflectionClasses;
        return this;
    }
    
    public EqualsBuilder setReflectUpToClass(final Class<?> reflectUpToClass) {
        this.reflectUpToClass = reflectUpToClass;
        return this;
    }
    
    public EqualsBuilder setExcludeFields(final String... excludeFields) {
        this.excludeFields = excludeFields;
        return this;
    }
    
    public static boolean reflectionEquals(final Object lhs, final Object rhs, final Collection<String> excludeFields) {
        return reflectionEquals(lhs, rhs, ReflectionToStringBuilder.toNoNullStringArray(excludeFields));
    }
    
    public static boolean reflectionEquals(final Object lhs, final Object rhs, final String... excludeFields) {
        return reflectionEquals(lhs, rhs, false, null, excludeFields);
    }
    
    public static boolean reflectionEquals(final Object lhs, final Object rhs, final boolean testTransients) {
        return reflectionEquals(lhs, rhs, testTransients, null, new String[0]);
    }
    
    public static boolean reflectionEquals(final Object lhs, final Object rhs, final boolean testTransients, final Class<?> reflectUpToClass, final String... excludeFields) {
        return reflectionEquals(lhs, rhs, testTransients, reflectUpToClass, false, excludeFields);
    }
    
    public static boolean reflectionEquals(final Object lhs, final Object rhs, final boolean testTransients, final Class<?> reflectUpToClass, final boolean testRecursive, final String... excludeFields) {
        return lhs == rhs || (lhs != null && rhs != null && new EqualsBuilder().setExcludeFields(excludeFields).setReflectUpToClass(reflectUpToClass).setTestTransients(testTransients).setTestRecursive(testRecursive).reflectionAppend(lhs, rhs).isEquals());
    }
    
    public EqualsBuilder reflectionAppend(final Object lhs, final Object rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.isEquals = false;
            return this;
        }
        final Class<?> lhsClass = lhs.getClass();
        final Class<?> rhsClass = rhs.getClass();
        Class<?> testClass;
        if (lhsClass.isInstance(rhs)) {
            testClass = lhsClass;
            if (!rhsClass.isInstance(lhs)) {
                testClass = rhsClass;
            }
        }
        else {
            if (!rhsClass.isInstance(lhs)) {
                this.isEquals = false;
                return this;
            }
            testClass = rhsClass;
            if (!lhsClass.isInstance(rhs)) {
                testClass = lhsClass;
            }
        }
        try {
            if (testClass.isArray()) {
                this.append(lhs, rhs);
            }
            else if (this.bypassReflectionClasses != null && (this.bypassReflectionClasses.contains(lhsClass) || this.bypassReflectionClasses.contains(rhsClass))) {
                this.isEquals = lhs.equals(rhs);
            }
            else {
                this.reflectionAppend(lhs, rhs, testClass);
                while (testClass.getSuperclass() != null && testClass != this.reflectUpToClass) {
                    testClass = testClass.getSuperclass();
                    this.reflectionAppend(lhs, rhs, testClass);
                }
            }
        }
        catch (IllegalArgumentException e) {
            this.isEquals = false;
            return this;
        }
        return this;
    }
    
    private void reflectionAppend(final Object lhs, final Object rhs, final Class<?> clazz) {
        if (isRegistered(lhs, rhs)) {
            return;
        }
        try {
            register(lhs, rhs);
            final Field[] fields = clazz.getDeclaredFields();
            AccessibleObject.setAccessible(fields, true);
            for (int i = 0; i < fields.length && this.isEquals; ++i) {
                final Field f = fields[i];
                if (!ArrayUtils.contains(this.excludeFields, f.getName()) && !f.getName().contains("$") && (this.testTransients || !Modifier.isTransient(f.getModifiers())) && !Modifier.isStatic(f.getModifiers()) && !f.isAnnotationPresent(EqualsExclude.class)) {
                    try {
                        this.append(f.get(lhs), f.get(rhs));
                    }
                    catch (IllegalAccessException e) {
                        throw new InternalError("Unexpected IllegalAccessException");
                    }
                }
            }
        }
        finally {
            unregister(lhs, rhs);
        }
    }
    
    public EqualsBuilder appendSuper(final boolean superEquals) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = superEquals;
        return this;
    }
    
    public EqualsBuilder append(final Object lhs, final Object rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.setEquals(false);
            return this;
        }
        final Class<?> lhsClass = lhs.getClass();
        if (lhsClass.isArray()) {
            this.appendArray(lhs, rhs);
        }
        else if (this.testRecursive && !ClassUtils.isPrimitiveOrWrapper(lhsClass)) {
            this.reflectionAppend(lhs, rhs);
        }
        else {
            this.isEquals = lhs.equals(rhs);
        }
        return this;
    }
    
    private void appendArray(final Object lhs, final Object rhs) {
        if (lhs.getClass() != rhs.getClass()) {
            this.setEquals(false);
        }
        else if (lhs instanceof long[]) {
            this.append((long[])lhs, (long[])rhs);
        }
        else if (lhs instanceof int[]) {
            this.append((int[])lhs, (int[])rhs);
        }
        else if (lhs instanceof short[]) {
            this.append((short[])lhs, (short[])rhs);
        }
        else if (lhs instanceof char[]) {
            this.append((char[])lhs, (char[])rhs);
        }
        else if (lhs instanceof byte[]) {
            this.append((byte[])lhs, (byte[])rhs);
        }
        else if (lhs instanceof double[]) {
            this.append((double[])lhs, (double[])rhs);
        }
        else if (lhs instanceof float[]) {
            this.append((float[])lhs, (float[])rhs);
        }
        else if (lhs instanceof boolean[]) {
            this.append((boolean[])lhs, (boolean[])rhs);
        }
        else {
            this.append((Object[])lhs, (Object[])rhs);
        }
    }
    
    public EqualsBuilder append(final long lhs, final long rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = (lhs == rhs);
        return this;
    }
    
    public EqualsBuilder append(final int lhs, final int rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = (lhs == rhs);
        return this;
    }
    
    public EqualsBuilder append(final short lhs, final short rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = (lhs == rhs);
        return this;
    }
    
    public EqualsBuilder append(final char lhs, final char rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = (lhs == rhs);
        return this;
    }
    
    public EqualsBuilder append(final byte lhs, final byte rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = (lhs == rhs);
        return this;
    }
    
    public EqualsBuilder append(final double lhs, final double rhs) {
        if (!this.isEquals) {
            return this;
        }
        return this.append(Double.doubleToLongBits(lhs), Double.doubleToLongBits(rhs));
    }
    
    public EqualsBuilder append(final float lhs, final float rhs) {
        if (!this.isEquals) {
            return this;
        }
        return this.append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
    }
    
    public EqualsBuilder append(final boolean lhs, final boolean rhs) {
        if (!this.isEquals) {
            return this;
        }
        this.isEquals = (lhs == rhs);
        return this;
    }
    
    public EqualsBuilder append(final Object[] lhs, final Object[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final long[] lhs, final long[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final int[] lhs, final int[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final short[] lhs, final short[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final char[] lhs, final char[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final byte[] lhs, final byte[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final double[] lhs, final double[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final float[] lhs, final float[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public EqualsBuilder append(final boolean[] lhs, final boolean[] rhs) {
        if (!this.isEquals) {
            return this;
        }
        if (lhs == rhs) {
            return this;
        }
        if (lhs == null || rhs == null) {
            this.setEquals(false);
            return this;
        }
        if (lhs.length != rhs.length) {
            this.setEquals(false);
            return this;
        }
        for (int i = 0; i < lhs.length && this.isEquals; ++i) {
            this.append(lhs[i], rhs[i]);
        }
        return this;
    }
    
    public boolean isEquals() {
        return this.isEquals;
    }
    
    @Override
    public Boolean build() {
        return this.isEquals();
    }
    
    protected void setEquals(final boolean isEquals) {
        this.isEquals = isEquals;
    }
    
    public void reset() {
        this.isEquals = true;
    }
    
    static {
        REGISTRY = new ThreadLocal<Set<Pair<IDKey, IDKey>>>();
    }
}
