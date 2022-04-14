package net.optifine.util;

import java.lang.reflect.Array;
import java.util.ArrayDeque;

import net.minecraft.block.state.IBlockState;
import net.minecraft.src.Config;

public class CacheObjectArray {
    private static ArrayDeque<int[]> arrays = new ArrayDeque<int[]>();
    private static int maxCacheSize = 10;

    public static synchronized void freeArray(int[] ints) {
        if (arrays.size() < maxCacheSize) {
            arrays.add(ints);
        }
    }

    public static void main(String[] args) throws Exception {
        int i = 4096;
        int j = 500000;
        testNew(i, j);
        testClone(i, j);
        testNewObj(i, j);
        testCloneObj(i, j);
        testNewObjDyn(IBlockState.class, i, j);
        long k = testNew(i, j);
        long l = testClone(i, j);
        long i1 = testNewObj(i, j);
        long j1 = testCloneObj(i, j);
        long k1 = testNewObjDyn(IBlockState.class, i, j);
        Config.dbg("New: " + k);
        Config.dbg("Clone: " + l);
        Config.dbg("NewObj: " + i1);
        Config.dbg("CloneObj: " + j1);
        Config.dbg("NewObjDyn: " + k1);
    }

    private static long testClone(int size, int count) {
        long i = System.currentTimeMillis();
        int[] aint = new int[size];

        for (int j = 0; j < count; ++j) {
            aint.clone();
        }

        long k = System.currentTimeMillis();
        return k - i;
    }

    private static long testNew(int size, int count) {
        long i = System.currentTimeMillis();

        for (int j = 0; j < count; ++j) {
            Array.newInstance(Integer.TYPE, size);
        }

        long k = System.currentTimeMillis();
        return k - i;
    }

    private static long testCloneObj(int size, int count) {
        long i = System.currentTimeMillis();
        IBlockState[] aiblockstate = new IBlockState[size];

        for (int j = 0; j < count; ++j) {
            aiblockstate.clone();
        }

        long k = System.currentTimeMillis();
        return k - i;
    }

    private static long testNewObj(int size, int count) {
        long i = System.currentTimeMillis();

        for (int j = 0; j < count; ++j) {
        }

        long k = System.currentTimeMillis();
        return k - i;
    }

    private static long testNewObjDyn(Class<IBlockState> cls, int size, int count) {
        long i = System.currentTimeMillis();

        for (int j = 0; j < count; ++j) {
            Array.newInstance(cls, size);
        }

        long k = System.currentTimeMillis();
        return k - i;
    }
}
