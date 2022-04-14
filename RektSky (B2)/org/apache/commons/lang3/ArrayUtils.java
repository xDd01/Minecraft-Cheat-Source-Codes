package org.apache.commons.lang3;

import org.apache.commons.lang3.builder.*;
import java.lang.reflect.*;
import org.apache.commons.lang3.mutable.*;
import org.apache.commons.lang3.math.*;
import java.util.*;

public class ArrayUtils
{
    public static final Object[] EMPTY_OBJECT_ARRAY;
    public static final Class<?>[] EMPTY_CLASS_ARRAY;
    public static final String[] EMPTY_STRING_ARRAY;
    public static final long[] EMPTY_LONG_ARRAY;
    public static final Long[] EMPTY_LONG_OBJECT_ARRAY;
    public static final int[] EMPTY_INT_ARRAY;
    public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY;
    public static final short[] EMPTY_SHORT_ARRAY;
    public static final Short[] EMPTY_SHORT_OBJECT_ARRAY;
    public static final byte[] EMPTY_BYTE_ARRAY;
    public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY;
    public static final double[] EMPTY_DOUBLE_ARRAY;
    public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY;
    public static final float[] EMPTY_FLOAT_ARRAY;
    public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY;
    public static final boolean[] EMPTY_BOOLEAN_ARRAY;
    public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY;
    public static final char[] EMPTY_CHAR_ARRAY;
    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY;
    public static final int INDEX_NOT_FOUND = -1;
    
    public static String toString(final Object array) {
        return toString(array, "{}");
    }
    
    public static String toString(final Object array, final String stringIfNull) {
        if (array == null) {
            return stringIfNull;
        }
        return new ToStringBuilder(array, ToStringStyle.SIMPLE_STYLE).append(array).toString();
    }
    
    public static int hashCode(final Object array) {
        return new HashCodeBuilder().append(array).toHashCode();
    }
    
    @Deprecated
    public static boolean isEquals(final Object array1, final Object array2) {
        return new EqualsBuilder().append(array1, array2).isEquals();
    }
    
    public static Map<Object, Object> toMap(final Object[] array) {
        if (array == null) {
            return null;
        }
        final Map<Object, Object> map = new HashMap<Object, Object>((int)(array.length * 1.5));
        for (int i = 0; i < array.length; ++i) {
            final Object object = array[i];
            if (object instanceof Map.Entry) {
                final Map.Entry<?, ?> entry = (Map.Entry<?, ?>)object;
                map.put(entry.getKey(), entry.getValue());
            }
            else {
                if (!(object instanceof Object[])) {
                    throw new IllegalArgumentException("Array element " + i + ", '" + object + "', is neither of type Map.Entry nor an Array");
                }
                final Object[] entry2 = (Object[])object;
                if (entry2.length < 2) {
                    throw new IllegalArgumentException("Array element " + i + ", '" + object + "', has a length less than 2");
                }
                map.put(entry2[0], entry2[1]);
            }
        }
        return map;
    }
    
    public static <T> T[] toArray(final T... items) {
        return items;
    }
    
    public static <T> T[] clone(final T[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
    
    public static long[] clone(final long[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
    
    public static int[] clone(final int[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
    
    public static short[] clone(final short[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
    
    public static char[] clone(final char[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
    
    public static byte[] clone(final byte[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
    
    public static double[] clone(final double[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
    
    public static float[] clone(final float[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
    
    public static boolean[] clone(final boolean[] array) {
        if (array == null) {
            return null;
        }
        return array.clone();
    }
    
    public static <T> T[] nullToEmpty(final T[] array, final Class<T[]> type) {
        if (type == null) {
            throw new IllegalArgumentException("The type must not be null");
        }
        if (array == null) {
            return type.cast(Array.newInstance(type.getComponentType(), 0));
        }
        return array;
    }
    
    public static Object[] nullToEmpty(final Object[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_OBJECT_ARRAY;
        }
        return array;
    }
    
    public static Class<?>[] nullToEmpty(final Class<?>[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_CLASS_ARRAY;
        }
        return array;
    }
    
    public static String[] nullToEmpty(final String[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return array;
    }
    
    public static long[] nullToEmpty(final long[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_LONG_ARRAY;
        }
        return array;
    }
    
    public static int[] nullToEmpty(final int[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        return array;
    }
    
    public static short[] nullToEmpty(final short[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_SHORT_ARRAY;
        }
        return array;
    }
    
    public static char[] nullToEmpty(final char[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        return array;
    }
    
    public static byte[] nullToEmpty(final byte[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return array;
    }
    
    public static double[] nullToEmpty(final double[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_DOUBLE_ARRAY;
        }
        return array;
    }
    
    public static float[] nullToEmpty(final float[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_FLOAT_ARRAY;
        }
        return array;
    }
    
    public static boolean[] nullToEmpty(final boolean[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_BOOLEAN_ARRAY;
        }
        return array;
    }
    
    public static Long[] nullToEmpty(final Long[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_LONG_OBJECT_ARRAY;
        }
        return array;
    }
    
    public static Integer[] nullToEmpty(final Integer[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
        }
        return array;
    }
    
    public static Short[] nullToEmpty(final Short[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY;
        }
        return array;
    }
    
    public static Character[] nullToEmpty(final Character[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY;
        }
        return array;
    }
    
    public static Byte[] nullToEmpty(final Byte[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY;
        }
        return array;
    }
    
    public static Double[] nullToEmpty(final Double[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY;
        }
        return array;
    }
    
    public static Float[] nullToEmpty(final Float[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY;
        }
        return array;
    }
    
    public static Boolean[] nullToEmpty(final Boolean[] array) {
        if (isEmpty(array)) {
            return ArrayUtils.EMPTY_BOOLEAN_OBJECT_ARRAY;
        }
        return array;
    }
    
    public static <T> T[] subarray(final T[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        final int newSize = endIndexExclusive - startIndexInclusive;
        final Class<?> type = array.getClass().getComponentType();
        if (newSize <= 0) {
            final T[] emptyArray = (T[])Array.newInstance(type, 0);
            return emptyArray;
        }
        final T[] subarray = (T[])Array.newInstance(type, newSize);
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }
    
    public static long[] subarray(final long[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        final int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_LONG_ARRAY;
        }
        final long[] subarray = new long[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }
    
    public static int[] subarray(final int[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        final int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        final int[] subarray = new int[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }
    
    public static short[] subarray(final short[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        final int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_SHORT_ARRAY;
        }
        final short[] subarray = new short[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }
    
    public static char[] subarray(final char[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        final int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        final char[] subarray = new char[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }
    
    public static byte[] subarray(final byte[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        final int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        final byte[] subarray = new byte[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }
    
    public static double[] subarray(final double[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        final int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_DOUBLE_ARRAY;
        }
        final double[] subarray = new double[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }
    
    public static float[] subarray(final float[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        final int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_FLOAT_ARRAY;
        }
        final float[] subarray = new float[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }
    
    public static boolean[] subarray(final boolean[] array, int startIndexInclusive, int endIndexExclusive) {
        if (array == null) {
            return null;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive > array.length) {
            endIndexExclusive = array.length;
        }
        final int newSize = endIndexExclusive - startIndexInclusive;
        if (newSize <= 0) {
            return ArrayUtils.EMPTY_BOOLEAN_ARRAY;
        }
        final boolean[] subarray = new boolean[newSize];
        System.arraycopy(array, startIndexInclusive, subarray, 0, newSize);
        return subarray;
    }
    
    public static boolean isSameLength(final Object[] array1, final Object[] array2) {
        return getLength(array1) == getLength(array2);
    }
    
    public static boolean isSameLength(final long[] array1, final long[] array2) {
        return getLength(array1) == getLength(array2);
    }
    
    public static boolean isSameLength(final int[] array1, final int[] array2) {
        return getLength(array1) == getLength(array2);
    }
    
    public static boolean isSameLength(final short[] array1, final short[] array2) {
        return getLength(array1) == getLength(array2);
    }
    
    public static boolean isSameLength(final char[] array1, final char[] array2) {
        return getLength(array1) == getLength(array2);
    }
    
    public static boolean isSameLength(final byte[] array1, final byte[] array2) {
        return getLength(array1) == getLength(array2);
    }
    
    public static boolean isSameLength(final double[] array1, final double[] array2) {
        return getLength(array1) == getLength(array2);
    }
    
    public static boolean isSameLength(final float[] array1, final float[] array2) {
        return getLength(array1) == getLength(array2);
    }
    
    public static boolean isSameLength(final boolean[] array1, final boolean[] array2) {
        return getLength(array1) == getLength(array2);
    }
    
    public static int getLength(final Object array) {
        if (array == null) {
            return 0;
        }
        return Array.getLength(array);
    }
    
    public static boolean isSameType(final Object array1, final Object array2) {
        if (array1 == null || array2 == null) {
            throw new IllegalArgumentException("The Array must not be null");
        }
        return array1.getClass().getName().equals(array2.getClass().getName());
    }
    
    public static void reverse(final Object[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }
    
    public static void reverse(final long[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }
    
    public static void reverse(final int[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }
    
    public static void reverse(final short[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }
    
    public static void reverse(final char[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }
    
    public static void reverse(final byte[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }
    
    public static void reverse(final double[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }
    
    public static void reverse(final float[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }
    
    public static void reverse(final boolean[] array) {
        if (array == null) {
            return;
        }
        reverse(array, 0, array.length);
    }
    
    public static void reverse(final boolean[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (array == null) {
            return;
        }
        for (int i = (startIndexInclusive < 0) ? 0 : startIndexInclusive, j = Math.min(array.length, endIndexExclusive) - 1; j > i; --j, ++i) {
            final boolean tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }
    
    public static void reverse(final byte[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (array == null) {
            return;
        }
        for (int i = (startIndexInclusive < 0) ? 0 : startIndexInclusive, j = Math.min(array.length, endIndexExclusive) - 1; j > i; --j, ++i) {
            final byte tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }
    
    public static void reverse(final char[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (array == null) {
            return;
        }
        for (int i = (startIndexInclusive < 0) ? 0 : startIndexInclusive, j = Math.min(array.length, endIndexExclusive) - 1; j > i; --j, ++i) {
            final char tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }
    
    public static void reverse(final double[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (array == null) {
            return;
        }
        for (int i = (startIndexInclusive < 0) ? 0 : startIndexInclusive, j = Math.min(array.length, endIndexExclusive) - 1; j > i; --j, ++i) {
            final double tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }
    
    public static void reverse(final float[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (array == null) {
            return;
        }
        for (int i = (startIndexInclusive < 0) ? 0 : startIndexInclusive, j = Math.min(array.length, endIndexExclusive) - 1; j > i; --j, ++i) {
            final float tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }
    
    public static void reverse(final int[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (array == null) {
            return;
        }
        for (int i = (startIndexInclusive < 0) ? 0 : startIndexInclusive, j = Math.min(array.length, endIndexExclusive) - 1; j > i; --j, ++i) {
            final int tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }
    
    public static void reverse(final long[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (array == null) {
            return;
        }
        for (int i = (startIndexInclusive < 0) ? 0 : startIndexInclusive, j = Math.min(array.length, endIndexExclusive) - 1; j > i; --j, ++i) {
            final long tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }
    
    public static void reverse(final Object[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (array == null) {
            return;
        }
        for (int i = (startIndexInclusive < 0) ? 0 : startIndexInclusive, j = Math.min(array.length, endIndexExclusive) - 1; j > i; --j, ++i) {
            final Object tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }
    
    public static void reverse(final short[] array, final int startIndexInclusive, final int endIndexExclusive) {
        if (array == null) {
            return;
        }
        for (int i = (startIndexInclusive < 0) ? 0 : startIndexInclusive, j = Math.min(array.length, endIndexExclusive) - 1; j > i; --j, ++i) {
            final short tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
        }
    }
    
    public static void swap(final Object[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }
    
    public static void swap(final long[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }
    
    public static void swap(final int[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }
    
    public static void swap(final short[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }
    
    public static void swap(final char[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }
    
    public static void swap(final byte[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }
    
    public static void swap(final double[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }
    
    public static void swap(final float[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }
    
    public static void swap(final boolean[] array, final int offset1, final int offset2) {
        if (array == null || array.length == 0) {
            return;
        }
        swap(array, offset1, offset2, 1);
    }
    
    public static void swap(final boolean[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; ++i, ++offset1, ++offset2) {
            final boolean aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }
    
    public static void swap(final byte[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; ++i, ++offset1, ++offset2) {
            final byte aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }
    
    public static void swap(final char[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; ++i, ++offset1, ++offset2) {
            final char aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }
    
    public static void swap(final double[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; ++i, ++offset1, ++offset2) {
            final double aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }
    
    public static void swap(final float[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; ++i, ++offset1, ++offset2) {
            final float aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }
    
    public static void swap(final int[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; ++i, ++offset1, ++offset2) {
            final int aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }
    
    public static void swap(final long[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; ++i, ++offset1, ++offset2) {
            final long aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }
    
    public static void swap(final Object[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; ++i, ++offset1, ++offset2) {
            final Object aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }
    
    public static void swap(final short[] array, int offset1, int offset2, int len) {
        if (array == null || array.length == 0 || offset1 >= array.length || offset2 >= array.length) {
            return;
        }
        if (offset1 < 0) {
            offset1 = 0;
        }
        if (offset2 < 0) {
            offset2 = 0;
        }
        if (offset1 == offset2) {
            return;
        }
        len = Math.min(Math.min(len, array.length - offset1), array.length - offset2);
        for (int i = 0; i < len; ++i, ++offset1, ++offset2) {
            final short aux = array[offset1];
            array[offset1] = array[offset2];
            array[offset2] = aux;
        }
    }
    
    public static void shift(final Object[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }
    
    public static void shift(final long[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }
    
    public static void shift(final int[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }
    
    public static void shift(final short[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }
    
    public static void shift(final char[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }
    
    public static void shift(final byte[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }
    
    public static void shift(final double[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }
    
    public static void shift(final float[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }
    
    public static void shift(final boolean[] array, final int offset) {
        if (array == null) {
            return;
        }
        shift(array, 0, array.length, offset);
    }
    
    public static void shift(final boolean[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array == null) {
            return;
        }
        if (startIndexInclusive >= array.length - 1 || endIndexExclusive <= 0) {
            return;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive >= array.length) {
            endIndexExclusive = array.length;
        }
        int n = endIndexExclusive - startIndexInclusive;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;
            if (offset > n_offset) {
                swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            }
            else {
                if (offset >= n_offset) {
                    swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                    break;
                }
                swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                startIndexInclusive += offset;
                n = n_offset;
            }
        }
    }
    
    public static void shift(final byte[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array == null) {
            return;
        }
        if (startIndexInclusive >= array.length - 1 || endIndexExclusive <= 0) {
            return;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive >= array.length) {
            endIndexExclusive = array.length;
        }
        int n = endIndexExclusive - startIndexInclusive;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;
            if (offset > n_offset) {
                swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            }
            else {
                if (offset >= n_offset) {
                    swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                    break;
                }
                swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                startIndexInclusive += offset;
                n = n_offset;
            }
        }
    }
    
    public static void shift(final char[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array == null) {
            return;
        }
        if (startIndexInclusive >= array.length - 1 || endIndexExclusive <= 0) {
            return;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive >= array.length) {
            endIndexExclusive = array.length;
        }
        int n = endIndexExclusive - startIndexInclusive;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;
            if (offset > n_offset) {
                swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            }
            else {
                if (offset >= n_offset) {
                    swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                    break;
                }
                swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                startIndexInclusive += offset;
                n = n_offset;
            }
        }
    }
    
    public static void shift(final double[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array == null) {
            return;
        }
        if (startIndexInclusive >= array.length - 1 || endIndexExclusive <= 0) {
            return;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive >= array.length) {
            endIndexExclusive = array.length;
        }
        int n = endIndexExclusive - startIndexInclusive;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;
            if (offset > n_offset) {
                swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            }
            else {
                if (offset >= n_offset) {
                    swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                    break;
                }
                swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                startIndexInclusive += offset;
                n = n_offset;
            }
        }
    }
    
    public static void shift(final float[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array == null) {
            return;
        }
        if (startIndexInclusive >= array.length - 1 || endIndexExclusive <= 0) {
            return;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive >= array.length) {
            endIndexExclusive = array.length;
        }
        int n = endIndexExclusive - startIndexInclusive;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;
            if (offset > n_offset) {
                swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            }
            else {
                if (offset >= n_offset) {
                    swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                    break;
                }
                swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                startIndexInclusive += offset;
                n = n_offset;
            }
        }
    }
    
    public static void shift(final int[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array == null) {
            return;
        }
        if (startIndexInclusive >= array.length - 1 || endIndexExclusive <= 0) {
            return;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive >= array.length) {
            endIndexExclusive = array.length;
        }
        int n = endIndexExclusive - startIndexInclusive;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;
            if (offset > n_offset) {
                swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            }
            else {
                if (offset >= n_offset) {
                    swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                    break;
                }
                swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                startIndexInclusive += offset;
                n = n_offset;
            }
        }
    }
    
    public static void shift(final long[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array == null) {
            return;
        }
        if (startIndexInclusive >= array.length - 1 || endIndexExclusive <= 0) {
            return;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive >= array.length) {
            endIndexExclusive = array.length;
        }
        int n = endIndexExclusive - startIndexInclusive;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;
            if (offset > n_offset) {
                swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            }
            else {
                if (offset >= n_offset) {
                    swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                    break;
                }
                swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                startIndexInclusive += offset;
                n = n_offset;
            }
        }
    }
    
    public static void shift(final Object[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array == null) {
            return;
        }
        if (startIndexInclusive >= array.length - 1 || endIndexExclusive <= 0) {
            return;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive >= array.length) {
            endIndexExclusive = array.length;
        }
        int n = endIndexExclusive - startIndexInclusive;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;
            if (offset > n_offset) {
                swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            }
            else {
                if (offset >= n_offset) {
                    swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                    break;
                }
                swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                startIndexInclusive += offset;
                n = n_offset;
            }
        }
    }
    
    public static void shift(final short[] array, int startIndexInclusive, int endIndexExclusive, int offset) {
        if (array == null) {
            return;
        }
        if (startIndexInclusive >= array.length - 1 || endIndexExclusive <= 0) {
            return;
        }
        if (startIndexInclusive < 0) {
            startIndexInclusive = 0;
        }
        if (endIndexExclusive >= array.length) {
            endIndexExclusive = array.length;
        }
        int n = endIndexExclusive - startIndexInclusive;
        if (n <= 1) {
            return;
        }
        offset %= n;
        if (offset < 0) {
            offset += n;
        }
        while (n > 1 && offset > 0) {
            final int n_offset = n - offset;
            if (offset > n_offset) {
                swap(array, startIndexInclusive, startIndexInclusive + n - n_offset, n_offset);
                n = offset;
                offset -= n_offset;
            }
            else {
                if (offset >= n_offset) {
                    swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                    break;
                }
                swap(array, startIndexInclusive, startIndexInclusive + n_offset, offset);
                startIndexInclusive += offset;
                n = n_offset;
            }
        }
    }
    
    public static int indexOf(final Object[] array, final Object objectToFind) {
        return indexOf(array, objectToFind, 0);
    }
    
    public static int indexOf(final Object[] array, final Object objectToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i < array.length; ++i) {
                if (array[i] == null) {
                    return i;
                }
            }
        }
        else {
            for (int i = startIndex; i < array.length; ++i) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public static int lastIndexOf(final Object[] array, final Object objectToFind) {
        return lastIndexOf(array, objectToFind, Integer.MAX_VALUE);
    }
    
    public static int lastIndexOf(final Object[] array, final Object objectToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        if (objectToFind == null) {
            for (int i = startIndex; i >= 0; --i) {
                if (array[i] == null) {
                    return i;
                }
            }
        }
        else if (array.getClass().getComponentType().isInstance(objectToFind)) {
            for (int i = startIndex; i >= 0; --i) {
                if (objectToFind.equals(array[i])) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public static boolean contains(final Object[] array, final Object objectToFind) {
        return indexOf(array, objectToFind) != -1;
    }
    
    public static int indexOf(final long[] array, final long valueToFind) {
        return indexOf(array, valueToFind, 0);
    }
    
    public static int indexOf(final long[] array, final long valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static int lastIndexOf(final long[] array, final long valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }
    
    public static int lastIndexOf(final long[] array, final long valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean contains(final long[] array, final long valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }
    
    public static int indexOf(final int[] array, final int valueToFind) {
        return indexOf(array, valueToFind, 0);
    }
    
    public static int indexOf(final int[] array, final int valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static int lastIndexOf(final int[] array, final int valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }
    
    public static int lastIndexOf(final int[] array, final int valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean contains(final int[] array, final int valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }
    
    public static int indexOf(final short[] array, final short valueToFind) {
        return indexOf(array, valueToFind, 0);
    }
    
    public static int indexOf(final short[] array, final short valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static int lastIndexOf(final short[] array, final short valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }
    
    public static int lastIndexOf(final short[] array, final short valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean contains(final short[] array, final short valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }
    
    public static int indexOf(final char[] array, final char valueToFind) {
        return indexOf(array, valueToFind, 0);
    }
    
    public static int indexOf(final char[] array, final char valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static int lastIndexOf(final char[] array, final char valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }
    
    public static int lastIndexOf(final char[] array, final char valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean contains(final char[] array, final char valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }
    
    public static int indexOf(final byte[] array, final byte valueToFind) {
        return indexOf(array, valueToFind, 0);
    }
    
    public static int indexOf(final byte[] array, final byte valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static int lastIndexOf(final byte[] array, final byte valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }
    
    public static int lastIndexOf(final byte[] array, final byte valueToFind, int startIndex) {
        if (array == null) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean contains(final byte[] array, final byte valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }
    
    public static int indexOf(final double[] array, final double valueToFind) {
        return indexOf(array, valueToFind, 0);
    }
    
    public static int indexOf(final double[] array, final double valueToFind, final double tolerance) {
        return indexOf(array, valueToFind, 0, tolerance);
    }
    
    public static int indexOf(final double[] array, final double valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static int indexOf(final double[] array, final double valueToFind, int startIndex, final double tolerance) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        final double min = valueToFind - tolerance;
        final double max = valueToFind + tolerance;
        for (int i = startIndex; i < array.length; ++i) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return -1;
    }
    
    public static int lastIndexOf(final double[] array, final double valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }
    
    public static int lastIndexOf(final double[] array, final double valueToFind, final double tolerance) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE, tolerance);
    }
    
    public static int lastIndexOf(final double[] array, final double valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static int lastIndexOf(final double[] array, final double valueToFind, int startIndex, final double tolerance) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        final double min = valueToFind - tolerance;
        final double max = valueToFind + tolerance;
        for (int i = startIndex; i >= 0; --i) {
            if (array[i] >= min && array[i] <= max) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean contains(final double[] array, final double valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }
    
    public static boolean contains(final double[] array, final double valueToFind, final double tolerance) {
        return indexOf(array, valueToFind, 0, tolerance) != -1;
    }
    
    public static int indexOf(final float[] array, final float valueToFind) {
        return indexOf(array, valueToFind, 0);
    }
    
    public static int indexOf(final float[] array, final float valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static int lastIndexOf(final float[] array, final float valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }
    
    public static int lastIndexOf(final float[] array, final float valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean contains(final float[] array, final float valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }
    
    public static int indexOf(final boolean[] array, final boolean valueToFind) {
        return indexOf(array, valueToFind, 0);
    }
    
    public static int indexOf(final boolean[] array, final boolean valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            startIndex = 0;
        }
        for (int i = startIndex; i < array.length; ++i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static int lastIndexOf(final boolean[] array, final boolean valueToFind) {
        return lastIndexOf(array, valueToFind, Integer.MAX_VALUE);
    }
    
    public static int lastIndexOf(final boolean[] array, final boolean valueToFind, int startIndex) {
        if (isEmpty(array)) {
            return -1;
        }
        if (startIndex < 0) {
            return -1;
        }
        if (startIndex >= array.length) {
            startIndex = array.length - 1;
        }
        for (int i = startIndex; i >= 0; --i) {
            if (valueToFind == array[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean contains(final boolean[] array, final boolean valueToFind) {
        return indexOf(array, valueToFind) != -1;
    }
    
    public static char[] toPrimitive(final Character[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        final char[] result = new char[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static char[] toPrimitive(final Character[] array, final char valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_CHAR_ARRAY;
        }
        final char[] result = new char[array.length];
        for (int i = 0; i < array.length; ++i) {
            final Character b = array[i];
            result[i] = ((b == null) ? valueForNull : b);
        }
        return result;
    }
    
    public static Character[] toObject(final char[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_CHARACTER_OBJECT_ARRAY;
        }
        final Character[] result = new Character[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static long[] toPrimitive(final Long[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_LONG_ARRAY;
        }
        final long[] result = new long[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static long[] toPrimitive(final Long[] array, final long valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_LONG_ARRAY;
        }
        final long[] result = new long[array.length];
        for (int i = 0; i < array.length; ++i) {
            final Long b = array[i];
            result[i] = ((b == null) ? valueForNull : b);
        }
        return result;
    }
    
    public static Long[] toObject(final long[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_LONG_OBJECT_ARRAY;
        }
        final Long[] result = new Long[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static int[] toPrimitive(final Integer[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static int[] toPrimitive(final Integer[] array, final int valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_INT_ARRAY;
        }
        final int[] result = new int[array.length];
        for (int i = 0; i < array.length; ++i) {
            final Integer b = array[i];
            result[i] = ((b == null) ? valueForNull : b);
        }
        return result;
    }
    
    public static Integer[] toObject(final int[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
        }
        final Integer[] result = new Integer[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static short[] toPrimitive(final Short[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_SHORT_ARRAY;
        }
        final short[] result = new short[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static short[] toPrimitive(final Short[] array, final short valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_SHORT_ARRAY;
        }
        final short[] result = new short[array.length];
        for (int i = 0; i < array.length; ++i) {
            final Short b = array[i];
            result[i] = ((b == null) ? valueForNull : b);
        }
        return result;
    }
    
    public static Short[] toObject(final short[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_SHORT_OBJECT_ARRAY;
        }
        final Short[] result = new Short[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static byte[] toPrimitive(final Byte[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static byte[] toPrimitive(final Byte[] array, final byte valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        final byte[] result = new byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            final Byte b = array[i];
            result[i] = ((b == null) ? valueForNull : b);
        }
        return result;
    }
    
    public static Byte[] toObject(final byte[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_BYTE_OBJECT_ARRAY;
        }
        final Byte[] result = new Byte[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static double[] toPrimitive(final Double[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_DOUBLE_ARRAY;
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static double[] toPrimitive(final Double[] array, final double valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_DOUBLE_ARRAY;
        }
        final double[] result = new double[array.length];
        for (int i = 0; i < array.length; ++i) {
            final Double b = array[i];
            result[i] = ((b == null) ? valueForNull : b);
        }
        return result;
    }
    
    public static Double[] toObject(final double[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_DOUBLE_OBJECT_ARRAY;
        }
        final Double[] result = new Double[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static float[] toPrimitive(final Float[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_FLOAT_ARRAY;
        }
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static float[] toPrimitive(final Float[] array, final float valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_FLOAT_ARRAY;
        }
        final float[] result = new float[array.length];
        for (int i = 0; i < array.length; ++i) {
            final Float b = array[i];
            result[i] = ((b == null) ? valueForNull : b);
        }
        return result;
    }
    
    public static Float[] toObject(final float[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_FLOAT_OBJECT_ARRAY;
        }
        final Float[] result = new Float[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static Object toPrimitive(final Object array) {
        if (array == null) {
            return null;
        }
        final Class<?> ct = array.getClass().getComponentType();
        final Class<?> pt = ClassUtils.wrapperToPrimitive(ct);
        if (Integer.TYPE.equals(pt)) {
            return toPrimitive((Integer[])array);
        }
        if (Long.TYPE.equals(pt)) {
            return toPrimitive((Long[])array);
        }
        if (Short.TYPE.equals(pt)) {
            return toPrimitive((Short[])array);
        }
        if (Double.TYPE.equals(pt)) {
            return toPrimitive((Double[])array);
        }
        if (Float.TYPE.equals(pt)) {
            return toPrimitive((Float[])array);
        }
        return array;
    }
    
    public static boolean[] toPrimitive(final Boolean[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_BOOLEAN_ARRAY;
        }
        final boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i];
        }
        return result;
    }
    
    public static boolean[] toPrimitive(final Boolean[] array, final boolean valueForNull) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_BOOLEAN_ARRAY;
        }
        final boolean[] result = new boolean[array.length];
        for (int i = 0; i < array.length; ++i) {
            final Boolean b = array[i];
            result[i] = ((b == null) ? valueForNull : b);
        }
        return result;
    }
    
    public static Boolean[] toObject(final boolean[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_BOOLEAN_OBJECT_ARRAY;
        }
        final Boolean[] result = new Boolean[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = (array[i] ? Boolean.TRUE : Boolean.FALSE);
        }
        return result;
    }
    
    public static boolean isEmpty(final Object[] array) {
        return getLength(array) == 0;
    }
    
    public static boolean isEmpty(final long[] array) {
        return getLength(array) == 0;
    }
    
    public static boolean isEmpty(final int[] array) {
        return getLength(array) == 0;
    }
    
    public static boolean isEmpty(final short[] array) {
        return getLength(array) == 0;
    }
    
    public static boolean isEmpty(final char[] array) {
        return getLength(array) == 0;
    }
    
    public static boolean isEmpty(final byte[] array) {
        return getLength(array) == 0;
    }
    
    public static boolean isEmpty(final double[] array) {
        return getLength(array) == 0;
    }
    
    public static boolean isEmpty(final float[] array) {
        return getLength(array) == 0;
    }
    
    public static boolean isEmpty(final boolean[] array) {
        return getLength(array) == 0;
    }
    
    public static <T> boolean isNotEmpty(final T[] array) {
        return !isEmpty(array);
    }
    
    public static boolean isNotEmpty(final long[] array) {
        return !isEmpty(array);
    }
    
    public static boolean isNotEmpty(final int[] array) {
        return !isEmpty(array);
    }
    
    public static boolean isNotEmpty(final short[] array) {
        return !isEmpty(array);
    }
    
    public static boolean isNotEmpty(final char[] array) {
        return !isEmpty(array);
    }
    
    public static boolean isNotEmpty(final byte[] array) {
        return !isEmpty(array);
    }
    
    public static boolean isNotEmpty(final double[] array) {
        return !isEmpty(array);
    }
    
    public static boolean isNotEmpty(final float[] array) {
        return !isEmpty(array);
    }
    
    public static boolean isNotEmpty(final boolean[] array) {
        return !isEmpty(array);
    }
    
    public static <T> T[] addAll(final T[] array1, final T... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        final Class<?> type1 = array1.getClass().getComponentType();
        final T[] joinedArray = (T[])Array.newInstance(type1, array1.length + array2.length);
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        try {
            System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        }
        catch (ArrayStoreException ase) {
            final Class<?> type2 = array2.getClass().getComponentType();
            if (!type1.isAssignableFrom(type2)) {
                throw new IllegalArgumentException("Cannot store " + type2.getName() + " in an array of " + type1.getName(), ase);
            }
            throw ase;
        }
        return joinedArray;
    }
    
    public static boolean[] addAll(final boolean[] array1, final boolean... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        final boolean[] joinedArray = new boolean[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
    
    public static char[] addAll(final char[] array1, final char... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        final char[] joinedArray = new char[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
    
    public static byte[] addAll(final byte[] array1, final byte... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        final byte[] joinedArray = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
    
    public static short[] addAll(final short[] array1, final short... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        final short[] joinedArray = new short[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
    
    public static int[] addAll(final int[] array1, final int... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        final int[] joinedArray = new int[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
    
    public static long[] addAll(final long[] array1, final long... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        final long[] joinedArray = new long[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
    
    public static float[] addAll(final float[] array1, final float... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        final float[] joinedArray = new float[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
    
    public static double[] addAll(final double[] array1, final double... array2) {
        if (array1 == null) {
            return clone(array2);
        }
        if (array2 == null) {
            return clone(array1);
        }
        final double[] joinedArray = new double[array1.length + array2.length];
        System.arraycopy(array1, 0, joinedArray, 0, array1.length);
        System.arraycopy(array2, 0, joinedArray, array1.length, array2.length);
        return joinedArray;
    }
    
    public static <T> T[] add(final T[] array, final T element) {
        Class<?> type;
        if (array != null) {
            type = array.getClass().getComponentType();
        }
        else {
            if (element == null) {
                throw new IllegalArgumentException("Arguments cannot both be null");
            }
            type = element.getClass();
        }
        final T[] newArray = (T[])copyArrayGrow1(array, type);
        newArray[newArray.length - 1] = element;
        return newArray;
    }
    
    public static boolean[] add(final boolean[] array, final boolean element) {
        final boolean[] newArray = (boolean[])copyArrayGrow1(array, Boolean.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }
    
    public static byte[] add(final byte[] array, final byte element) {
        final byte[] newArray = (byte[])copyArrayGrow1(array, Byte.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }
    
    public static char[] add(final char[] array, final char element) {
        final char[] newArray = (char[])copyArrayGrow1(array, Character.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }
    
    public static double[] add(final double[] array, final double element) {
        final double[] newArray = (double[])copyArrayGrow1(array, Double.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }
    
    public static float[] add(final float[] array, final float element) {
        final float[] newArray = (float[])copyArrayGrow1(array, Float.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }
    
    public static int[] add(final int[] array, final int element) {
        final int[] newArray = (int[])copyArrayGrow1(array, Integer.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }
    
    public static long[] add(final long[] array, final long element) {
        final long[] newArray = (long[])copyArrayGrow1(array, Long.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }
    
    public static short[] add(final short[] array, final short element) {
        final short[] newArray = (short[])copyArrayGrow1(array, Short.TYPE);
        newArray[newArray.length - 1] = element;
        return newArray;
    }
    
    private static Object copyArrayGrow1(final Object array, final Class<?> newArrayComponentType) {
        if (array != null) {
            final int arrayLength = Array.getLength(array);
            final Object newArray = Array.newInstance(array.getClass().getComponentType(), arrayLength + 1);
            System.arraycopy(array, 0, newArray, 0, arrayLength);
            return newArray;
        }
        return Array.newInstance(newArrayComponentType, 1);
    }
    
    @Deprecated
    public static <T> T[] add(final T[] array, final int index, final T element) {
        Class<?> clss = null;
        if (array != null) {
            clss = array.getClass().getComponentType();
        }
        else {
            if (element == null) {
                throw new IllegalArgumentException("Array and element cannot both be null");
            }
            clss = element.getClass();
        }
        final T[] newArray = (T[])add(array, index, element, clss);
        return newArray;
    }
    
    @Deprecated
    public static boolean[] add(final boolean[] array, final int index, final boolean element) {
        return (boolean[])add(array, index, element, Boolean.TYPE);
    }
    
    @Deprecated
    public static char[] add(final char[] array, final int index, final char element) {
        return (char[])add(array, index, element, Character.TYPE);
    }
    
    @Deprecated
    public static byte[] add(final byte[] array, final int index, final byte element) {
        return (byte[])add(array, index, element, Byte.TYPE);
    }
    
    @Deprecated
    public static short[] add(final short[] array, final int index, final short element) {
        return (short[])add(array, index, element, Short.TYPE);
    }
    
    @Deprecated
    public static int[] add(final int[] array, final int index, final int element) {
        return (int[])add(array, index, element, Integer.TYPE);
    }
    
    @Deprecated
    public static long[] add(final long[] array, final int index, final long element) {
        return (long[])add(array, index, element, Long.TYPE);
    }
    
    @Deprecated
    public static float[] add(final float[] array, final int index, final float element) {
        return (float[])add(array, index, element, Float.TYPE);
    }
    
    @Deprecated
    public static double[] add(final double[] array, final int index, final double element) {
        return (double[])add(array, index, element, Double.TYPE);
    }
    
    private static Object add(final Object array, final int index, final Object element, final Class<?> clss) {
        if (array == null) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: 0");
            }
            final Object joinedArray = Array.newInstance(clss, 1);
            Array.set(joinedArray, 0, element);
            return joinedArray;
        }
        else {
            final int length = Array.getLength(array);
            if (index > length || index < 0) {
                throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
            }
            final Object result = Array.newInstance(clss, length + 1);
            System.arraycopy(array, 0, result, 0, index);
            Array.set(result, index, element);
            if (index < length) {
                System.arraycopy(array, index, result, index + 1, length - index);
            }
            return result;
        }
    }
    
    public static <T> T[] remove(final T[] array, final int index) {
        return (T[])remove((Object)array, index);
    }
    
    public static <T> T[] removeElement(final T[] array, final Object element) {
        final int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }
    
    public static boolean[] remove(final boolean[] array, final int index) {
        return (boolean[])remove((Object)array, index);
    }
    
    public static boolean[] removeElement(final boolean[] array, final boolean element) {
        final int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }
    
    public static byte[] remove(final byte[] array, final int index) {
        return (byte[])remove((Object)array, index);
    }
    
    public static byte[] removeElement(final byte[] array, final byte element) {
        final int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }
    
    public static char[] remove(final char[] array, final int index) {
        return (char[])remove((Object)array, index);
    }
    
    public static char[] removeElement(final char[] array, final char element) {
        final int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }
    
    public static double[] remove(final double[] array, final int index) {
        return (double[])remove((Object)array, index);
    }
    
    public static double[] removeElement(final double[] array, final double element) {
        final int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }
    
    public static float[] remove(final float[] array, final int index) {
        return (float[])remove((Object)array, index);
    }
    
    public static float[] removeElement(final float[] array, final float element) {
        final int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }
    
    public static int[] remove(final int[] array, final int index) {
        return (int[])remove((Object)array, index);
    }
    
    public static int[] removeElement(final int[] array, final int element) {
        final int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }
    
    public static long[] remove(final long[] array, final int index) {
        return (long[])remove((Object)array, index);
    }
    
    public static long[] removeElement(final long[] array, final long element) {
        final int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }
    
    public static short[] remove(final short[] array, final int index) {
        return (short[])remove((Object)array, index);
    }
    
    public static short[] removeElement(final short[] array, final short element) {
        final int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        return remove(array, index);
    }
    
    private static Object remove(final Object array, final int index) {
        final int length = getLength(array);
        if (index < 0 || index >= length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
        }
        final Object result = Array.newInstance(array.getClass().getComponentType(), length - 1);
        System.arraycopy(array, 0, result, 0, index);
        if (index < length - 1) {
            System.arraycopy(array, index + 1, result, index, length - index - 1);
        }
        return result;
    }
    
    public static <T> T[] removeAll(final T[] array, final int... indices) {
        return (T[])removeAll((Object)array, indices);
    }
    
    @SafeVarargs
    public static <T> T[] removeElements(final T[] array, final T... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<T, MutableInt> occurrences = new HashMap<T, MutableInt>(values.length);
        for (final T v : values) {
            final MutableInt count = occurrences.get(v);
            if (count == null) {
                occurrences.put(v, new MutableInt(1));
            }
            else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; ++i) {
            final T key = array[i];
            final MutableInt count2 = occurrences.get(key);
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        final T[] result = (T[])removeAll((Object)array, toRemove);
        return result;
    }
    
    public static byte[] removeAll(final byte[] array, final int... indices) {
        return (byte[])removeAll((Object)array, indices);
    }
    
    public static byte[] removeElements(final byte[] array, final byte... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final Map<Byte, MutableInt> occurrences = new HashMap<Byte, MutableInt>(values.length);
        for (final byte v : values) {
            final Byte boxed = v;
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            }
            else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; ++i) {
            final byte key = array[i];
            final MutableInt count2 = occurrences.get(key);
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (byte[])removeAll((Object)array, toRemove);
    }
    
    public static short[] removeAll(final short[] array, final int... indices) {
        return (short[])removeAll((Object)array, indices);
    }
    
    public static short[] removeElements(final short[] array, final short... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Short, MutableInt> occurrences = new HashMap<Short, MutableInt>(values.length);
        for (final short v : values) {
            final Short boxed = v;
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            }
            else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; ++i) {
            final short key = array[i];
            final MutableInt count2 = occurrences.get(key);
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (short[])removeAll((Object)array, toRemove);
    }
    
    public static int[] removeAll(final int[] array, final int... indices) {
        return (int[])removeAll((Object)array, indices);
    }
    
    public static int[] removeElements(final int[] array, final int... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Integer, MutableInt> occurrences = new HashMap<Integer, MutableInt>(values.length);
        for (final int v : values) {
            final Integer boxed = v;
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            }
            else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; ++i) {
            final int key = array[i];
            final MutableInt count2 = occurrences.get(key);
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (int[])removeAll((Object)array, toRemove);
    }
    
    public static char[] removeAll(final char[] array, final int... indices) {
        return (char[])removeAll((Object)array, indices);
    }
    
    public static char[] removeElements(final char[] array, final char... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Character, MutableInt> occurrences = new HashMap<Character, MutableInt>(values.length);
        for (final char v : values) {
            final Character boxed = v;
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            }
            else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; ++i) {
            final char key = array[i];
            final MutableInt count2 = occurrences.get(key);
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (char[])removeAll((Object)array, toRemove);
    }
    
    public static long[] removeAll(final long[] array, final int... indices) {
        return (long[])removeAll((Object)array, indices);
    }
    
    public static long[] removeElements(final long[] array, final long... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Long, MutableInt> occurrences = new HashMap<Long, MutableInt>(values.length);
        for (final long v : values) {
            final Long boxed = v;
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            }
            else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; ++i) {
            final long key = array[i];
            final MutableInt count2 = occurrences.get(key);
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (long[])removeAll((Object)array, toRemove);
    }
    
    public static float[] removeAll(final float[] array, final int... indices) {
        return (float[])removeAll((Object)array, indices);
    }
    
    public static float[] removeElements(final float[] array, final float... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Float, MutableInt> occurrences = new HashMap<Float, MutableInt>(values.length);
        for (final float v : values) {
            final Float boxed = v;
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            }
            else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; ++i) {
            final float key = array[i];
            final MutableInt count2 = occurrences.get(key);
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (float[])removeAll((Object)array, toRemove);
    }
    
    public static double[] removeAll(final double[] array, final int... indices) {
        return (double[])removeAll((Object)array, indices);
    }
    
    public static double[] removeElements(final double[] array, final double... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Double, MutableInt> occurrences = new HashMap<Double, MutableInt>(values.length);
        for (final double v : values) {
            final Double boxed = v;
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            }
            else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; ++i) {
            final double key = array[i];
            final MutableInt count2 = occurrences.get(key);
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (double[])removeAll((Object)array, toRemove);
    }
    
    public static boolean[] removeAll(final boolean[] array, final int... indices) {
        return (boolean[])removeAll((Object)array, indices);
    }
    
    public static boolean[] removeElements(final boolean[] array, final boolean... values) {
        if (isEmpty(array) || isEmpty(values)) {
            return clone(array);
        }
        final HashMap<Boolean, MutableInt> occurrences = new HashMap<Boolean, MutableInt>(2);
        for (final boolean v : values) {
            final Boolean boxed = v;
            final MutableInt count = occurrences.get(boxed);
            if (count == null) {
                occurrences.put(boxed, new MutableInt(1));
            }
            else {
                count.increment();
            }
        }
        final BitSet toRemove = new BitSet();
        for (int i = 0; i < array.length; ++i) {
            final boolean key = array[i];
            final MutableInt count2 = occurrences.get(key);
            if (count2 != null) {
                if (count2.decrementAndGet() == 0) {
                    occurrences.remove(key);
                }
                toRemove.set(i);
            }
        }
        return (boolean[])removeAll((Object)array, toRemove);
    }
    
    static Object removeAll(final Object array, final int... indices) {
        final int length = getLength(array);
        int diff = 0;
        final int[] clonedIndices = clone(indices);
        Arrays.sort(clonedIndices);
        if (isNotEmpty(clonedIndices)) {
            int i = clonedIndices.length;
            int prevIndex = length;
            while (--i >= 0) {
                final int index = clonedIndices[i];
                if (index < 0 || index >= length) {
                    throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + length);
                }
                if (index >= prevIndex) {
                    continue;
                }
                ++diff;
                prevIndex = index;
            }
        }
        final Object result = Array.newInstance(array.getClass().getComponentType(), length - diff);
        if (diff < length) {
            int end = length;
            int dest = length - diff;
            for (int j = clonedIndices.length - 1; j >= 0; --j) {
                final int index2 = clonedIndices[j];
                if (end - index2 > 1) {
                    final int cp = end - index2 - 1;
                    dest -= cp;
                    System.arraycopy(array, index2 + 1, result, dest, cp);
                }
                end = index2;
            }
            if (end > 0) {
                System.arraycopy(array, 0, result, 0, end);
            }
        }
        return result;
    }
    
    static Object removeAll(final Object array, final BitSet indices) {
        final int srcLength = getLength(array);
        final int removals = indices.cardinality();
        final Object result = Array.newInstance(array.getClass().getComponentType(), srcLength - removals);
        int srcIndex = 0;
        int destIndex = 0;
        int set;
        while ((set = indices.nextSetBit(srcIndex)) != -1) {
            final int count = set - srcIndex;
            if (count > 0) {
                System.arraycopy(array, srcIndex, result, destIndex, count);
                destIndex += count;
            }
            srcIndex = indices.nextClearBit(set);
        }
        final int count = srcLength - srcIndex;
        if (count > 0) {
            System.arraycopy(array, srcIndex, result, destIndex, count);
        }
        return result;
    }
    
    public static <T extends Comparable<? super T>> boolean isSorted(final T[] array) {
        return isSorted(array, new Comparator<T>() {
            @Override
            public int compare(final T o1, final T o2) {
                return o1.compareTo((Object)o2);
            }
        });
    }
    
    public static <T> boolean isSorted(final T[] array, final Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator should not be null.");
        }
        if (array == null || array.length < 2) {
            return true;
        }
        T previous = array[0];
        for (int n = array.length, i = 1; i < n; ++i) {
            final T current = array[i];
            if (comparator.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }
    
    public static boolean isSorted(final int[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        int previous = array[0];
        for (int n = array.length, i = 1; i < n; ++i) {
            final int current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }
    
    public static boolean isSorted(final long[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        long previous = array[0];
        for (int n = array.length, i = 1; i < n; ++i) {
            final long current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }
    
    public static boolean isSorted(final short[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        short previous = array[0];
        for (int n = array.length, i = 1; i < n; ++i) {
            final short current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }
    
    public static boolean isSorted(final double[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        double previous = array[0];
        for (int n = array.length, i = 1; i < n; ++i) {
            final double current = array[i];
            if (Double.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }
    
    public static boolean isSorted(final float[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        float previous = array[0];
        for (int n = array.length, i = 1; i < n; ++i) {
            final float current = array[i];
            if (Float.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }
    
    public static boolean isSorted(final byte[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        byte previous = array[0];
        for (int n = array.length, i = 1; i < n; ++i) {
            final byte current = array[i];
            if (NumberUtils.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }
    
    public static boolean isSorted(final char[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        char previous = array[0];
        for (int n = array.length, i = 1; i < n; ++i) {
            final char current = array[i];
            if (CharUtils.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }
    
    public static boolean isSorted(final boolean[] array) {
        if (array == null || array.length < 2) {
            return true;
        }
        boolean previous = array[0];
        for (int n = array.length, i = 1; i < n; ++i) {
            final boolean current = array[i];
            if (BooleanUtils.compare(previous, current) > 0) {
                return false;
            }
            previous = current;
        }
        return true;
    }
    
    public static boolean[] removeAllOccurences(final boolean[] array, final boolean element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count;
        for (count = 1; (index = indexOf(array, element, indices[count - 1] + 1)) != -1; indices[count++] = index) {}
        return removeAll(array, Arrays.copyOf(indices, count));
    }
    
    public static char[] removeAllOccurences(final char[] array, final char element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count;
        for (count = 1; (index = indexOf(array, element, indices[count - 1] + 1)) != -1; indices[count++] = index) {}
        return removeAll(array, Arrays.copyOf(indices, count));
    }
    
    public static byte[] removeAllOccurences(final byte[] array, final byte element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count;
        for (count = 1; (index = indexOf(array, element, indices[count - 1] + 1)) != -1; indices[count++] = index) {}
        return removeAll(array, Arrays.copyOf(indices, count));
    }
    
    public static short[] removeAllOccurences(final short[] array, final short element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count;
        for (count = 1; (index = indexOf(array, element, indices[count - 1] + 1)) != -1; indices[count++] = index) {}
        return removeAll(array, Arrays.copyOf(indices, count));
    }
    
    public static int[] removeAllOccurences(final int[] array, final int element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count;
        for (count = 1; (index = indexOf(array, element, indices[count - 1] + 1)) != -1; indices[count++] = index) {}
        return removeAll(array, Arrays.copyOf(indices, count));
    }
    
    public static long[] removeAllOccurences(final long[] array, final long element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count;
        for (count = 1; (index = indexOf(array, element, indices[count - 1] + 1)) != -1; indices[count++] = index) {}
        return removeAll(array, Arrays.copyOf(indices, count));
    }
    
    public static float[] removeAllOccurences(final float[] array, final float element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count;
        for (count = 1; (index = indexOf(array, element, indices[count - 1] + 1)) != -1; indices[count++] = index) {}
        return removeAll(array, Arrays.copyOf(indices, count));
    }
    
    public static double[] removeAllOccurences(final double[] array, final double element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count;
        for (count = 1; (index = indexOf(array, element, indices[count - 1] + 1)) != -1; indices[count++] = index) {}
        return removeAll(array, Arrays.copyOf(indices, count));
    }
    
    public static <T> T[] removeAllOccurences(final T[] array, final T element) {
        int index = indexOf(array, element);
        if (index == -1) {
            return clone(array);
        }
        final int[] indices = new int[array.length - index];
        indices[0] = index;
        int count;
        for (count = 1; (index = indexOf(array, element, indices[count - 1] + 1)) != -1; indices[count++] = index) {}
        return removeAll(array, Arrays.copyOf(indices, count));
    }
    
    public static String[] toStringArray(final Object[] array) {
        if (array == null) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final String[] result = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            result[i] = array[i].toString();
        }
        return result;
    }
    
    public static String[] toStringArray(final Object[] array, final String valueForNullElements) {
        if (null == array) {
            return null;
        }
        if (array.length == 0) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final String[] result = new String[array.length];
        for (int i = 0; i < array.length; ++i) {
            final Object object = array[i];
            result[i] = ((object == null) ? valueForNullElements : object.toString());
        }
        return result;
    }
    
    public static boolean[] insert(final int index, final boolean[] array, final boolean... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        final boolean[] result = new boolean[array.length + values.length];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }
    
    public static byte[] insert(final int index, final byte[] array, final byte... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        final byte[] result = new byte[array.length + values.length];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }
    
    public static char[] insert(final int index, final char[] array, final char... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        final char[] result = new char[array.length + values.length];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }
    
    public static double[] insert(final int index, final double[] array, final double... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        final double[] result = new double[array.length + values.length];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }
    
    public static float[] insert(final int index, final float[] array, final float... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        final float[] result = new float[array.length + values.length];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }
    
    public static int[] insert(final int index, final int[] array, final int... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        final int[] result = new int[array.length + values.length];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }
    
    public static long[] insert(final int index, final long[] array, final long... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        final long[] result = new long[array.length + values.length];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }
    
    public static short[] insert(final int index, final short[] array, final short... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        final short[] result = new short[array.length + values.length];
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }
    
    @SafeVarargs
    public static <T> T[] insert(final int index, final T[] array, final T... values) {
        if (array == null) {
            return null;
        }
        if (values == null || values.length == 0) {
            return clone(array);
        }
        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Length: " + array.length);
        }
        final Class<?> type = array.getClass().getComponentType();
        final T[] result = (T[])Array.newInstance(type, array.length + values.length);
        System.arraycopy(values, 0, result, index, values.length);
        if (index > 0) {
            System.arraycopy(array, 0, result, 0, index);
        }
        if (index < array.length) {
            System.arraycopy(array, index, result, index + values.length, array.length - index);
        }
        return result;
    }
    
    public static void shuffle(final Object[] array) {
        shuffle(array, new Random());
    }
    
    public static void shuffle(final Object[] array, final Random random) {
        for (int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }
    
    public static void shuffle(final boolean[] array) {
        shuffle(array, new Random());
    }
    
    public static void shuffle(final boolean[] array, final Random random) {
        for (int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }
    
    public static void shuffle(final byte[] array) {
        shuffle(array, new Random());
    }
    
    public static void shuffle(final byte[] array, final Random random) {
        for (int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }
    
    public static void shuffle(final char[] array) {
        shuffle(array, new Random());
    }
    
    public static void shuffle(final char[] array, final Random random) {
        for (int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }
    
    public static void shuffle(final short[] array) {
        shuffle(array, new Random());
    }
    
    public static void shuffle(final short[] array, final Random random) {
        for (int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }
    
    public static void shuffle(final int[] array) {
        shuffle(array, new Random());
    }
    
    public static void shuffle(final int[] array, final Random random) {
        for (int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }
    
    public static void shuffle(final long[] array) {
        shuffle(array, new Random());
    }
    
    public static void shuffle(final long[] array, final Random random) {
        for (int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }
    
    public static void shuffle(final float[] array) {
        shuffle(array, new Random());
    }
    
    public static void shuffle(final float[] array, final Random random) {
        for (int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }
    
    public static void shuffle(final double[] array) {
        shuffle(array, new Random());
    }
    
    public static void shuffle(final double[] array, final Random random) {
        for (int i = array.length; i > 1; --i) {
            swap(array, i - 1, random.nextInt(i), 1);
        }
    }
    
    public static <T> boolean isArrayIndexValid(final T[] array, final int index) {
        return getLength(array) != 0 && array.length > index && index >= 0;
    }
    
    static {
        EMPTY_OBJECT_ARRAY = new Object[0];
        EMPTY_CLASS_ARRAY = new Class[0];
        EMPTY_STRING_ARRAY = new String[0];
        EMPTY_LONG_ARRAY = new long[0];
        EMPTY_LONG_OBJECT_ARRAY = new Long[0];
        EMPTY_INT_ARRAY = new int[0];
        EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];
        EMPTY_SHORT_ARRAY = new short[0];
        EMPTY_SHORT_OBJECT_ARRAY = new Short[0];
        EMPTY_BYTE_ARRAY = new byte[0];
        EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];
        EMPTY_DOUBLE_ARRAY = new double[0];
        EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];
        EMPTY_FLOAT_ARRAY = new float[0];
        EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];
        EMPTY_BOOLEAN_ARRAY = new boolean[0];
        EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];
        EMPTY_CHAR_ARRAY = new char[0];
        EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];
    }
}
