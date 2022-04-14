/*
 * Decompiled with CFR 0.152.
 */
package org.objectweb.asm.tree;

import java.util.ArrayList;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
final class Util {
    private Util() {
    }

    static <T> List<T> add(List<T> list, T element) {
        ArrayList<T> newList = list == null ? new ArrayList<T>(1) : list;
        newList.add(element);
        return newList;
    }

    static <T> List<T> asArrayList(int length) {
        ArrayList<Object> list = new ArrayList<Object>(length);
        for (int i2 = 0; i2 < length; ++i2) {
            list.add(null);
        }
        return list;
    }

    static <T> List<T> asArrayList(T[] array) {
        if (array == null) {
            return new ArrayList();
        }
        ArrayList<T> list = new ArrayList<T>(array.length);
        for (T t2 : array) {
            list.add(t2);
        }
        return list;
    }

    static List<Byte> asArrayList(byte[] byteArray) {
        if (byteArray == null) {
            return new ArrayList<Byte>();
        }
        ArrayList<Byte> byteList = new ArrayList<Byte>(byteArray.length);
        for (byte b2 : byteArray) {
            byteList.add(b2);
        }
        return byteList;
    }

    static List<Boolean> asArrayList(boolean[] booleanArray) {
        if (booleanArray == null) {
            return new ArrayList<Boolean>();
        }
        ArrayList<Boolean> booleanList = new ArrayList<Boolean>(booleanArray.length);
        for (boolean b2 : booleanArray) {
            booleanList.add(b2);
        }
        return booleanList;
    }

    static List<Short> asArrayList(short[] shortArray) {
        if (shortArray == null) {
            return new ArrayList<Short>();
        }
        ArrayList<Short> shortList = new ArrayList<Short>(shortArray.length);
        for (short s2 : shortArray) {
            shortList.add(s2);
        }
        return shortList;
    }

    static List<Character> asArrayList(char[] charArray) {
        if (charArray == null) {
            return new ArrayList<Character>();
        }
        ArrayList<Character> charList = new ArrayList<Character>(charArray.length);
        for (char c2 : charArray) {
            charList.add(Character.valueOf(c2));
        }
        return charList;
    }

    static List<Integer> asArrayList(int[] intArray) {
        if (intArray == null) {
            return new ArrayList<Integer>();
        }
        ArrayList<Integer> intList = new ArrayList<Integer>(intArray.length);
        for (int i2 : intArray) {
            intList.add(i2);
        }
        return intList;
    }

    static List<Float> asArrayList(float[] floatArray) {
        if (floatArray == null) {
            return new ArrayList<Float>();
        }
        ArrayList<Float> floatList = new ArrayList<Float>(floatArray.length);
        for (float f2 : floatArray) {
            floatList.add(Float.valueOf(f2));
        }
        return floatList;
    }

    static List<Long> asArrayList(long[] longArray) {
        if (longArray == null) {
            return new ArrayList<Long>();
        }
        ArrayList<Long> longList = new ArrayList<Long>(longArray.length);
        for (long l2 : longArray) {
            longList.add(l2);
        }
        return longList;
    }

    static List<Double> asArrayList(double[] doubleArray) {
        if (doubleArray == null) {
            return new ArrayList<Double>();
        }
        ArrayList<Double> doubleList = new ArrayList<Double>(doubleArray.length);
        for (double d2 : doubleArray) {
            doubleList.add(d2);
        }
        return doubleList;
    }

    static <T> List<T> asArrayList(int length, T[] array) {
        ArrayList<T> list = new ArrayList<T>(length);
        for (int i2 = 0; i2 < length; ++i2) {
            list.add(array[i2]);
        }
        return list;
    }
}

