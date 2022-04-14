/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.Tuple2f;
import javax.vecmath.VecMathUtil;

public abstract class Tuple2d
implements Serializable,
Cloneable {
    static final long serialVersionUID = 6205762482756093838L;
    public double x;
    public double y;

    public Tuple2d(double x2, double y2) {
        this.x = x2;
        this.y = y2;
    }

    public Tuple2d(double[] t2) {
        this.x = t2[0];
        this.y = t2[1];
    }

    public Tuple2d(Tuple2d t1) {
        this.x = t1.x;
        this.y = t1.y;
    }

    public Tuple2d(Tuple2f t1) {
        this.x = t1.x;
        this.y = t1.y;
    }

    public Tuple2d() {
        this.x = 0.0;
        this.y = 0.0;
    }

    public final void set(double x2, double y2) {
        this.x = x2;
        this.y = y2;
    }

    public final void set(double[] t2) {
        this.x = t2[0];
        this.y = t2[1];
    }

    public final void set(Tuple2d t1) {
        this.x = t1.x;
        this.y = t1.y;
    }

    public final void set(Tuple2f t1) {
        this.x = t1.x;
        this.y = t1.y;
    }

    public final void get(double[] t2) {
        t2[0] = this.x;
        t2[1] = this.y;
    }

    public final void add(Tuple2d t1, Tuple2d t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
    }

    public final void add(Tuple2d t1) {
        this.x += t1.x;
        this.y += t1.y;
    }

    public final void sub(Tuple2d t1, Tuple2d t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
    }

    public final void sub(Tuple2d t1) {
        this.x -= t1.x;
        this.y -= t1.y;
    }

    public final void negate(Tuple2d t1) {
        this.x = -t1.x;
        this.y = -t1.y;
    }

    public final void negate() {
        this.x = -this.x;
        this.y = -this.y;
    }

    public final void scale(double s2, Tuple2d t1) {
        this.x = s2 * t1.x;
        this.y = s2 * t1.y;
    }

    public final void scale(double s2) {
        this.x *= s2;
        this.y *= s2;
    }

    public final void scaleAdd(double s2, Tuple2d t1, Tuple2d t2) {
        this.x = s2 * t1.x + t2.x;
        this.y = s2 * t1.y + t2.y;
    }

    public final void scaleAdd(double s2, Tuple2d t1) {
        this.x = s2 * this.x + t1.x;
        this.y = s2 * this.y + t1.y;
    }

    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.x);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.y);
        return (int)(bits ^ bits >> 32);
    }

    public boolean equals(Tuple2d t1) {
        try {
            return this.x == t1.x && this.y == t1.y;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean equals(Object t1) {
        try {
            Tuple2d t2 = (Tuple2d)t1;
            return this.x == t2.x && this.y == t2.y;
        }
        catch (NullPointerException e2) {
            return false;
        }
        catch (ClassCastException e1) {
            return false;
        }
    }

    public boolean epsilonEquals(Tuple2d t1, double epsilon) {
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
        return !(d3 > epsilon);
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ")";
    }

    public final void clamp(double min, double max, Tuple2d t2) {
        this.x = t2.x > max ? max : (t2.x < min ? min : t2.x);
        this.y = t2.y > max ? max : (t2.y < min ? min : t2.y);
    }

    public final void clampMin(double min, Tuple2d t2) {
        this.x = t2.x < min ? min : t2.x;
        this.y = t2.y < min ? min : t2.y;
    }

    public final void clampMax(double max, Tuple2d t2) {
        this.x = t2.x > max ? max : t2.x;
        this.y = t2.y > max ? max : t2.y;
    }

    public final void absolute(Tuple2d t2) {
        this.x = Math.abs(t2.x);
        this.y = Math.abs(t2.y);
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
    }

    public final void clampMin(double min) {
        if (this.x < min) {
            this.x = min;
        }
        if (this.y < min) {
            this.y = min;
        }
    }

    public final void clampMax(double max) {
        if (this.x > max) {
            this.x = max;
        }
        if (this.y > max) {
            this.y = max;
        }
    }

    public final void absolute() {
        this.x = Math.abs(this.x);
        this.y = Math.abs(this.y);
    }

    public final void interpolate(Tuple2d t1, Tuple2d t2, double alpha) {
        this.x = (1.0 - alpha) * t1.x + alpha * t2.x;
        this.y = (1.0 - alpha) * t1.y + alpha * t2.y;
    }

    public final void interpolate(Tuple2d t1, double alpha) {
        this.x = (1.0 - alpha) * this.x + alpha * t1.x;
        this.y = (1.0 - alpha) * this.y + alpha * t1.y;
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
}

