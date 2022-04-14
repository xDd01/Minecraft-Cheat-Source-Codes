/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.Tuple4d;
import javax.vecmath.VecMathUtil;

public abstract class Tuple4f
implements Serializable,
Cloneable {
    static final long serialVersionUID = 7068460319248845763L;
    public float x;
    public float y;
    public float z;
    public float w;

    public Tuple4f(float x2, float y2, float z2, float w2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
        this.w = w2;
    }

    public Tuple4f(float[] t2) {
        this.x = t2[0];
        this.y = t2[1];
        this.z = t2[2];
        this.w = t2[3];
    }

    public Tuple4f(Tuple4f t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }

    public Tuple4f(Tuple4d t1) {
        this.x = (float)t1.x;
        this.y = (float)t1.y;
        this.z = (float)t1.z;
        this.w = (float)t1.w;
    }

    public Tuple4f() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.w = 0.0f;
    }

    public final void set(float x2, float y2, float z2, float w2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
        this.w = w2;
    }

    public final void set(float[] t2) {
        this.x = t2[0];
        this.y = t2[1];
        this.z = t2[2];
        this.w = t2[3];
    }

    public final void set(Tuple4f t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }

    public final void set(Tuple4d t1) {
        this.x = (float)t1.x;
        this.y = (float)t1.y;
        this.z = (float)t1.z;
        this.w = (float)t1.w;
    }

    public final void get(float[] t2) {
        t2[0] = this.x;
        t2[1] = this.y;
        t2[2] = this.z;
        t2[3] = this.w;
    }

    public final void get(Tuple4f t2) {
        t2.x = this.x;
        t2.y = this.y;
        t2.z = this.z;
        t2.w = this.w;
    }

    public final void add(Tuple4f t1, Tuple4f t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
        this.z = t1.z + t2.z;
        this.w = t1.w + t2.w;
    }

    public final void add(Tuple4f t1) {
        this.x += t1.x;
        this.y += t1.y;
        this.z += t1.z;
        this.w += t1.w;
    }

    public final void sub(Tuple4f t1, Tuple4f t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
        this.z = t1.z - t2.z;
        this.w = t1.w - t2.w;
    }

    public final void sub(Tuple4f t1) {
        this.x -= t1.x;
        this.y -= t1.y;
        this.z -= t1.z;
        this.w -= t1.w;
    }

    public final void negate(Tuple4f t1) {
        this.x = -t1.x;
        this.y = -t1.y;
        this.z = -t1.z;
        this.w = -t1.w;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
        this.w = -this.w;
    }

    public final void scale(float s2, Tuple4f t1) {
        this.x = s2 * t1.x;
        this.y = s2 * t1.y;
        this.z = s2 * t1.z;
        this.w = s2 * t1.w;
    }

    public final void scale(float s2) {
        this.x *= s2;
        this.y *= s2;
        this.z *= s2;
        this.w *= s2;
    }

    public final void scaleAdd(float s2, Tuple4f t1, Tuple4f t2) {
        this.x = s2 * t1.x + t2.x;
        this.y = s2 * t1.y + t2.y;
        this.z = s2 * t1.z + t2.z;
        this.w = s2 * t1.w + t2.w;
    }

    public final void scaleAdd(float s2, Tuple4f t1) {
        this.x = s2 * this.x + t1.x;
        this.y = s2 * this.y + t1.y;
        this.z = s2 * this.z + t1.z;
        this.w = s2 * this.w + t1.w;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }

    public boolean equals(Tuple4f t1) {
        try {
            return this.x == t1.x && this.y == t1.y && this.z == t1.z && this.w == t1.w;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean equals(Object t1) {
        try {
            Tuple4f t2 = (Tuple4f)t1;
            return this.x == t2.x && this.y == t2.y && this.z == t2.z && this.w == t2.w;
        }
        catch (NullPointerException e2) {
            return false;
        }
        catch (ClassCastException e1) {
            return false;
        }
    }

    public boolean epsilonEquals(Tuple4f t1, float epsilon) {
        float diff = this.x - t1.x;
        if (Float.isNaN(diff)) {
            return false;
        }
        float f2 = diff < 0.0f ? -diff : diff;
        if (f2 > epsilon) {
            return false;
        }
        diff = this.y - t1.y;
        if (Float.isNaN(diff)) {
            return false;
        }
        float f3 = diff < 0.0f ? -diff : diff;
        if (f3 > epsilon) {
            return false;
        }
        diff = this.z - t1.z;
        if (Float.isNaN(diff)) {
            return false;
        }
        float f4 = diff < 0.0f ? -diff : diff;
        if (f4 > epsilon) {
            return false;
        }
        diff = this.w - t1.w;
        if (Float.isNaN(diff)) {
            return false;
        }
        float f5 = diff < 0.0f ? -diff : diff;
        return !(f5 > epsilon);
    }

    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.x);
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.y);
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.z);
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.w);
        return (int)(bits ^ bits >> 32);
    }

    public final void clamp(float min, float max, Tuple4f t2) {
        this.x = t2.x > max ? max : (t2.x < min ? min : t2.x);
        this.y = t2.y > max ? max : (t2.y < min ? min : t2.y);
        this.z = t2.z > max ? max : (t2.z < min ? min : t2.z);
        this.w = t2.w > max ? max : (t2.w < min ? min : t2.w);
    }

    public final void clampMin(float min, Tuple4f t2) {
        this.x = t2.x < min ? min : t2.x;
        this.y = t2.y < min ? min : t2.y;
        this.z = t2.z < min ? min : t2.z;
        this.w = t2.w < min ? min : t2.w;
    }

    public final void clampMax(float max, Tuple4f t2) {
        this.x = t2.x > max ? max : t2.x;
        this.y = t2.y > max ? max : t2.y;
        this.z = t2.z > max ? max : t2.z;
        this.w = t2.w > max ? max : t2.z;
    }

    public final void absolute(Tuple4f t2) {
        this.x = Math.abs(t2.x);
        this.y = Math.abs(t2.y);
        this.z = Math.abs(t2.z);
        this.w = Math.abs(t2.w);
    }

    public final void clamp(float min, float max) {
        if (this.x > max) {
            this.x = max;
        } else if (this.x < min) {
            this.x = min;
        }
        if (this.y > max) {
            this.y = max;
        } else if (this.y < min) {
            this.y = min;
        }
        if (this.z > max) {
            this.z = max;
        } else if (this.z < min) {
            this.z = min;
        }
        if (this.w > max) {
            this.w = max;
        } else if (this.w < min) {
            this.w = min;
        }
    }

    public final void clampMin(float min) {
        if (this.x < min) {
            this.x = min;
        }
        if (this.y < min) {
            this.y = min;
        }
        if (this.z < min) {
            this.z = min;
        }
        if (this.w < min) {
            this.w = min;
        }
    }

    public final void clampMax(float max) {
        if (this.x > max) {
            this.x = max;
        }
        if (this.y > max) {
            this.y = max;
        }
        if (this.z > max) {
            this.z = max;
        }
        if (this.w > max) {
            this.w = max;
        }
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
        this.z = Math.abs(this.z);
        this.w = Math.abs(this.w);
    }

    public void interpolate(Tuple4f t1, Tuple4f t2, float alpha) {
        this.x = (1.0f - alpha) * t1.x + alpha * t2.x;
        this.y = (1.0f - alpha) * t1.y + alpha * t2.y;
        this.z = (1.0f - alpha) * t1.z + alpha * t2.z;
        this.w = (1.0f - alpha) * t1.w + alpha * t2.w;
    }

    public void interpolate(Tuple4f t1, float alpha) {
        this.x = (1.0f - alpha) * this.x + alpha * t1.x;
        this.y = (1.0f - alpha) * this.y + alpha * t1.y;
        this.z = (1.0f - alpha) * this.z + alpha * t1.z;
        this.w = (1.0f - alpha) * this.w + alpha * t1.w;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e2) {
            throw new InternalError();
        }
    }

    public final float getX() {
        return this.x;
    }

    public final void setX(float x2) {
        this.x = x2;
    }

    public final float getY() {
        return this.y;
    }

    public final void setY(float y2) {
        this.y = y2;
    }

    public final float getZ() {
        return this.z;
    }

    public final void setZ(float z2) {
        this.z = z2;
    }

    public final float getW() {
        return this.w;
    }

    public final void setW(float w2) {
        this.w = w2;
    }
}

