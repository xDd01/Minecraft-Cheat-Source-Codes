/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.math;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public final class RandomUtil {
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    public static double random(double min, double max) {
        return RANDOM.nextDouble(Math.min(min, max), Math.max(min, max));
    }

    public static int random(int min, int max) {
        return RANDOM.nextInt(Math.min(min, max), Math.max(min, max));
    }

    public static boolean bool() {
        return RANDOM.nextBoolean();
    }

    public static <T> T choice(List<T> list) {
        return list.get(RandomUtil.random(0, list.size()));
    }

    public static <T> T choice(T[] array) {
        return array[RandomUtil.random(0, array.length)];
    }

    private RandomUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}

