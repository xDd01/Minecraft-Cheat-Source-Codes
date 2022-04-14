// 
// Decompiled by Procyon v0.6.0
// 

package javax.vecmath;

class VecMathUtil
{
    private VecMathUtil() {
    }
    
    static final long hashLongBits(long hash, final long l) {
        hash *= 31L;
        return hash + l;
    }
    
    static final long hashFloatBits(long hash, final float f) {
        hash *= 31L;
        if (f == 0.0f) {
            return hash;
        }
        return hash + Float.floatToIntBits(f);
    }
    
    static final long hashDoubleBits(long hash, final double d) {
        hash *= 31L;
        if (d == 0.0) {
            return hash;
        }
        return hash + Double.doubleToLongBits(d);
    }
    
    static final int hashFinish(final long hash) {
        return (int)(hash ^ hash >> 32);
    }
}
