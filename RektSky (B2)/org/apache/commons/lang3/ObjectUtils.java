package org.apache.commons.lang3;

import org.apache.commons.lang3.text.*;
import org.apache.commons.lang3.mutable.*;
import java.util.*;
import org.apache.commons.lang3.exception.*;
import java.lang.reflect.*;
import java.io.*;

public class ObjectUtils
{
    private static final char AT_SIGN = '@';
    public static final Null NULL;
    
    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return (object != null) ? object : defaultValue;
    }
    
    @SafeVarargs
    public static <T> T firstNonNull(final T... values) {
        if (values != null) {
            for (final T val : values) {
                if (val != null) {
                    return val;
                }
            }
        }
        return null;
    }
    
    public static boolean anyNotNull(final Object... values) {
        return firstNonNull(values) != null;
    }
    
    public static boolean allNotNull(final Object... values) {
        if (values == null) {
            return false;
        }
        for (final Object val : values) {
            if (val == null) {
                return false;
            }
        }
        return true;
    }
    
    @Deprecated
    public static boolean equals(final Object object1, final Object object2) {
        return object1 == object2 || (object1 != null && object2 != null && object1.equals(object2));
    }
    
    public static boolean notEqual(final Object object1, final Object object2) {
        return !equals(object1, object2);
    }
    
    @Deprecated
    public static int hashCode(final Object obj) {
        return (obj == null) ? 0 : obj.hashCode();
    }
    
    @Deprecated
    public static int hashCodeMulti(final Object... objects) {
        int hash = 1;
        if (objects != null) {
            for (final Object object : objects) {
                final int tmpHash = hashCode(object);
                hash = hash * 31 + tmpHash;
            }
        }
        return hash;
    }
    
    public static String identityToString(final Object object) {
        if (object == null) {
            return null;
        }
        final String name = object.getClass().getName();
        final String hexString = Integer.toHexString(System.identityHashCode(object));
        final StringBuilder builder = new StringBuilder(name.length() + 1 + hexString.length());
        builder.append(name).append('@').append(hexString);
        return builder.toString();
    }
    
    public static void identityToString(final Appendable appendable, final Object object) throws IOException {
        Validate.notNull(object, "Cannot get the toString of a null object", new Object[0]);
        appendable.append(object.getClass().getName()).append('@').append(Integer.toHexString(System.identityHashCode(object)));
    }
    
    @Deprecated
    public static void identityToString(final StrBuilder builder, final Object object) {
        Validate.notNull(object, "Cannot get the toString of a null object", new Object[0]);
        final String name = object.getClass().getName();
        final String hexString = Integer.toHexString(System.identityHashCode(object));
        builder.ensureCapacity(builder.length() + name.length() + 1 + hexString.length());
        builder.append(name).append('@').append(hexString);
    }
    
    public static void identityToString(final StringBuffer buffer, final Object object) {
        Validate.notNull(object, "Cannot get the toString of a null object", new Object[0]);
        final String name = object.getClass().getName();
        final String hexString = Integer.toHexString(System.identityHashCode(object));
        buffer.ensureCapacity(buffer.length() + name.length() + 1 + hexString.length());
        buffer.append(name).append('@').append(hexString);
    }
    
    public static void identityToString(final StringBuilder builder, final Object object) {
        Validate.notNull(object, "Cannot get the toString of a null object", new Object[0]);
        final String name = object.getClass().getName();
        final String hexString = Integer.toHexString(System.identityHashCode(object));
        builder.ensureCapacity(builder.length() + name.length() + 1 + hexString.length());
        builder.append(name).append('@').append(hexString);
    }
    
    @Deprecated
    public static String toString(final Object obj) {
        return (obj == null) ? "" : obj.toString();
    }
    
    @Deprecated
    public static String toString(final Object obj, final String nullStr) {
        return (obj == null) ? nullStr : obj.toString();
    }
    
    @SafeVarargs
    public static <T extends Comparable<? super T>> T min(final T... values) {
        T result = null;
        if (values != null) {
            for (final T value : values) {
                if (compare(value, result, true) < 0) {
                    result = value;
                }
            }
        }
        return result;
    }
    
    @SafeVarargs
    public static <T extends Comparable<? super T>> T max(final T... values) {
        T result = null;
        if (values != null) {
            for (final T value : values) {
                if (compare(value, result, false) > 0) {
                    result = value;
                }
            }
        }
        return result;
    }
    
    public static <T extends Comparable<? super T>> int compare(final T c1, final T c2) {
        return compare(c1, c2, false);
    }
    
    public static <T extends Comparable<? super T>> int compare(final T c1, final T c2, final boolean nullGreater) {
        if (c1 == c2) {
            return 0;
        }
        if (c1 == null) {
            return nullGreater ? 1 : -1;
        }
        if (c2 == null) {
            return nullGreater ? -1 : 1;
        }
        return c1.compareTo((Object)c2);
    }
    
    @SafeVarargs
    public static <T extends Comparable<? super T>> T median(final T... items) {
        Validate.notEmpty(items);
        Validate.noNullElements(items);
        final TreeSet<T> sort = new TreeSet<T>();
        Collections.addAll(sort, items);
        final T result = (T)sort.toArray()[(sort.size() - 1) / 2];
        return result;
    }
    
    @SafeVarargs
    public static <T> T median(final Comparator<T> comparator, final T... items) {
        Validate.notEmpty(items, "null/empty items", new Object[0]);
        Validate.noNullElements(items);
        Validate.notNull(comparator, "null comparator", new Object[0]);
        final TreeSet<T> sort = new TreeSet<T>(comparator);
        Collections.addAll(sort, items);
        final T result = (T)sort.toArray()[(sort.size() - 1) / 2];
        return result;
    }
    
    @SafeVarargs
    public static <T> T mode(final T... items) {
        if (ArrayUtils.isNotEmpty(items)) {
            final HashMap<T, MutableInt> occurrences = new HashMap<T, MutableInt>(items.length);
            for (final T t : items) {
                final MutableInt count = occurrences.get(t);
                if (count == null) {
                    occurrences.put(t, new MutableInt(1));
                }
                else {
                    count.increment();
                }
            }
            T result = null;
            int max = 0;
            for (final Map.Entry<T, MutableInt> e : occurrences.entrySet()) {
                final int cmp = e.getValue().intValue();
                if (cmp == max) {
                    result = null;
                }
                else {
                    if (cmp <= max) {
                        continue;
                    }
                    max = cmp;
                    result = e.getKey();
                }
            }
            return result;
        }
        return null;
    }
    
    public static <T> T clone(final T obj) {
        if (obj instanceof Cloneable) {
            Object result;
            if (obj.getClass().isArray()) {
                final Class<?> componentType = obj.getClass().getComponentType();
                if (componentType.isPrimitive()) {
                    int length = Array.getLength(obj);
                    result = Array.newInstance(componentType, length);
                    while (length-- > 0) {
                        Array.set(result, length, Array.get(obj, length));
                    }
                }
                else {
                    result = ((Object[])(Object)obj).clone();
                }
            }
            else {
                try {
                    final Method clone = obj.getClass().getMethod("clone", (Class<?>[])new Class[0]);
                    result = clone.invoke(obj, new Object[0]);
                }
                catch (NoSuchMethodException e) {
                    throw new CloneFailedException("Cloneable type " + obj.getClass().getName() + " has no clone method", e);
                }
                catch (IllegalAccessException e2) {
                    throw new CloneFailedException("Cannot clone Cloneable type " + obj.getClass().getName(), e2);
                }
                catch (InvocationTargetException e3) {
                    throw new CloneFailedException("Exception cloning Cloneable type " + obj.getClass().getName(), e3.getCause());
                }
            }
            final T checked = (T)result;
            return checked;
        }
        return null;
    }
    
    public static <T> T cloneIfPossible(final T obj) {
        final T clone = (T)clone((Object)obj);
        return (clone == null) ? obj : clone;
    }
    
    public static boolean CONST(final boolean v) {
        return v;
    }
    
    public static byte CONST(final byte v) {
        return v;
    }
    
    public static byte CONST_BYTE(final int v) {
        if (v < -128 || v > 127) {
            throw new IllegalArgumentException("Supplied value must be a valid byte literal between -128 and 127: [" + v + "]");
        }
        return (byte)v;
    }
    
    public static char CONST(final char v) {
        return v;
    }
    
    public static short CONST(final short v) {
        return v;
    }
    
    public static short CONST_SHORT(final int v) {
        if (v < -32768 || v > 32767) {
            throw new IllegalArgumentException("Supplied value must be a valid byte literal between -32768 and 32767: [" + v + "]");
        }
        return (short)v;
    }
    
    public static int CONST(final int v) {
        return v;
    }
    
    public static long CONST(final long v) {
        return v;
    }
    
    public static float CONST(final float v) {
        return v;
    }
    
    public static double CONST(final double v) {
        return v;
    }
    
    public static <T> T CONST(final T v) {
        return v;
    }
    
    static {
        NULL = new Null();
    }
    
    public static class Null implements Serializable
    {
        private static final long serialVersionUID = 7092611880189329093L;
        
        Null() {
        }
        
        private Object readResolve() {
            return ObjectUtils.NULL;
        }
    }
}
