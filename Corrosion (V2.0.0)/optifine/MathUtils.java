/*
 * Decompiled with CFR 0.152.
 */
package optifine;

public class MathUtils {
    public static int getAverage(int[] p_getAverage_0_) {
        if (p_getAverage_0_.length <= 0) {
            return 0;
        }
        int i2 = 0;
        for (int j2 = 0; j2 < p_getAverage_0_.length; ++j2) {
            int k2 = p_getAverage_0_[j2];
            i2 += k2;
        }
        int l2 = i2 / p_getAverage_0_.length;
        return l2;
    }
}

