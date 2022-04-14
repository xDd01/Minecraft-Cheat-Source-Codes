/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

class VecMathUtil {
    static int floatToIntBits(float f2) {
        if (f2 == 0.0f) {
            return 0;
        }
        return Float.floatToIntBits(f2);
    }

    static long doubleToLongBits(double d2) {
        if (d2 == 0.0) {
            return 0L;
        }
        return Double.doubleToLongBits(d2);
    }

    private VecMathUtil() {
    }
}

