/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.Tuple4f;
import javax.vecmath.VecMathUtil;

public abstract class Tuple4d
implements Serializable,
Cloneable {
    static final long serialVersionUID = -4748953690425311052L;
    public double x;
    public double y;
    public double z;
    public double w;

    public Tuple4d(double x2, double y2, double z2, double w2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
        this.w = w2;
    }

    public Tuple4d(double[] t2) {
        this.x = t2[0];
        this.y = t2[1];
        this.z = t2[2];
        this.w = t2[3];
    }

    public Tuple4d(Tuple4d t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }

    public Tuple4d(Tuple4f t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }

    public Tuple4d() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        this.w = 0.0;
    }

    public final void set(double x2, double y2, double z2, double w2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
        this.w = w2;
    }

    public final void set(double[] t2) {
        this.x = t2[0];
        this.y = t2[1];
        this.z = t2[2];
        this.w = t2[3];
    }

    public final void set(Tuple4d t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }

    public final void set(Tuple4f t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }

    public final void get(double[] t2) {
        t2[0] = this.x;
        t2[1] = this.y;
        t2[2] = this.z;
        t2[3] = this.w;
    }

    public final void get(Tuple4d t2) {
        t2.x = this.x;
        t2.y = this.y;
        t2.z = this.z;
        t2.w = this.w;
    }

    public final void add(Tuple4d t1, Tuple4d t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
        this.z = t1.z + t2.z;
        this.w = t1.w + t2.w;
    }

    public final void add(Tuple4d t1) {
        this.x += t1.x;
        this.y += t1.y;
        this.z += t1.z;
        this.w += t1.w;
    }

    public final void sub(Tuple4d t1, Tuple4d t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
        this.z = t1.z - t2.z;
        this.w = t1.w - t2.w;
    }

    public final void sub(Tuple4d t1) {
        this.x -= t1.x;
        this.y -= t1.y;
        this.z -= t1.z;
        this.w -= t1.w;
    }

    public final void negate(Tuple4d t1) {
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

    public final void scale(double s2, Tuple4d t1) {
        this.x = s2 * t1.x;
        this.y = s2 * t1.y;
        this.z = s2 * t1.z;
        this.w = s2 * t1.w;
    }

    public final void scale(double s2) {
        this.x *= s2;
        this.y *= s2;
        this.z *= s2;
        this.w *= s2;
    }

    public final void scaleAdd(double s2, Tuple4d t1, Tuple4d t2) {
        this.x = s2 * t1.x + t2.x;
        this.y = s2 * t1.y + t2.y;
        this.z = s2 * t1.z + t2.z;
        this.w = s2 * t1.w + t2.w;
    }

    public final void scaleAdd(float s2, Tuple4d t1) {
        this.scaleAdd((double)s2, t1);
    }

    public final void scaleAdd(double s2, Tuple4d t1) {
        this.x = s2 * this.x + t1.x;
        this.y = s2 * this.y + t1.y;
        this.z = s2 * this.z + t1.z;
        this.w = s2 * this.w + t1.w;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }

    public boolean equals(Tuple4d t1) {
        try {
            return this.x == t1.x && this.y == t1.y && this.z == t1.z && this.w == t1.w;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean equals(Object t1) {
        try {
            Tuple4d t2 = (Tuple4d)t1;
            return this.x == t2.x && this.y == t2.y && this.z == t2.z && this.w == t2.w;
        }
        catch (NullPointerException e2) {
            return false;
        }
        catch (ClassCastException e1) {
            return false;
        }
    }

    public boolean epsilonEquals(Tuple4d t1, double epsilon) {
        double diff = this.x - t1.x;
        if (Double.isNaN(diff)) {
            return false;
        }
        double d2 = diff < 0.0 ? -diff : diff;
        if (d2 > epsilon) {
            return false;
        }
        diff = this.y - t1.y;
        if (Double.isNaN(diff)) {
            return false;
        }
        double d3 = diff < 0.0 ? -diff : diff;
        if (d3 > epsilon) {
            return false;
        }
        diff = this.z - t1.z;
        if (Double.isNaN(diff)) {
            return false;
        }
        double d4 = diff < 0.0 ? -diff : diff;
        if (d4 > epsilon) {
            return false;
        }
        diff = this.w - t1.w;
        if (Double.isNaN(diff)) {
            return false;
        }
        double d5 = diff < 0.0 ? -diff : diff;
        return !(d5 > epsilon);
    }

    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.x);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.y);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.z);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.w);
        return (int)(bits ^ bits >> 32);
    }

    public final void clamp(float min, float max, Tuple4d t2) {
        this.clamp((double)min, (double)max, t2);
    }

    public final void clamp(double min, double max, Tuple4d t2) {
        this.x = t2.x > max ? max : (t2.x < min ? min : t2.x);
        this.y = t2.y > max ? max : (t2.y < min ? min : t2.y);
        this.z = t2.z > max ? max : (t2.z < min ? min : t2.z);
        this.w = t2.w > max ? max : (t2.w < min ? min : t2.w);
    }

    public final void clampMin(float min, Tuple4d t2) {
        this.clampMin((double)min, t2);
    }

    public final void clampMin(double min, Tuple4d t2) {
        this.x = t2.x < min ? min : t2.x;
        this.y = t2.y < min ? min : t2.y;
        this.z = t2.z < min ? min : t2.z;
        this.w = t2.w < min ? min : t2.w;
    }

    public final void clampMax(float max, Tuple4d t2) {
        this.clampMax((double)max, t2);
    }

    public final void clampMax(double max, Tuple4d t2) {
        this.x = t2.x > max ? max : t2.x;
        this.y = t2.y > max ? max : t2.y;
        this.z = t2.z > max ? max : t2.z;
        this.w = t2.w > max ? max : t2.z;
    }

    public final void absolute(Tuple4d t2) {
        this.x = Math.abs(t2.x);
        this.y = Math.abs(t2.y);
        this.z = Math.abs(t2.z);
        this.w = Math.abs(t2.w);
    }

    public final void clamp(float min, float max) {
        this.clamp((double)min, (double)max);
    }

    public final void clamp(double min, double max) {
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
        this.clampMin((double)min);
    }

    public final void clampMin(double min) {
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
        this.clampMax((double)max);
    }

    public final void clampMax(double max) {
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

    public void interpolate(Tuple4d t1, Tuple4d t2, float alpha) {
        this.interpolate(t1, t2, (double)alpha);
    }

    public void interpolate(Tuple4d t1, Tuple4d t2, double alpha) {
        this.x = (1.0 - alpha) * t1.x + alpha * t2.x;
        this.y = (1.0 - alpha) * t1.y + alpha * t2.y;
        this.z = (1.0 - alpha) * t1.z + alpha * t2.z;
        this.w = (1.0 - alpha) * t1.w + alpha * t2.w;
    }

    public void interpolate(Tuple4d t1, float alpha) {
        this.interpolate(t1, (double)alpha);
    }

    public void interpolate(Tuple4d t1, double alpha) {
        this.x = (1.0 - alpha) * this.x + alpha * t1.x;
        this.y = (1.0 - alpha) * this.y + alpha * t1.y;
        this.z = (1.0 - alpha) * this.z + alpha * t1.z;
        this.w = (1.0 - alpha) * this.w + alpha * t1.w;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e2) {
            throw new InternalError();
        }
    }

    public final double getX() {
        return this.x;
    }

    public final void setX(double x2) {
        this.x = x2;
    }

    public final double getY() {
        return this.y;
    }

    public final void setY(double y2) {
        this.y = y2;
    }

    public final double getZ() {
        return this.z;
    }

    public final void setZ(double z2) {
        this.z = z2;
    }

    public final double getW() {
        return this.w;
    }

    public final void setW(double w2) {
        this.w = w2;
    }
}

