/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple4i
implements Serializable,
Cloneable {
    static final long serialVersionUID = 8064614250942616720L;
    public int x;
    public int y;
    public int z;
    public int w;

    public Tuple4i(int x2, int y2, int z2, int w2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
        this.w = w2;
    }

    public Tuple4i(int[] t2) {
        this.x = t2[0];
        this.y = t2[1];
        this.z = t2[2];
        this.w = t2[3];
    }

    public Tuple4i(Tuple4i t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }

    public Tuple4i() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public final void set(int x2, int y2, int z2, int w2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
        this.w = w2;
    }

    public final void set(int[] t2) {
        this.x = t2[0];
        this.y = t2[1];
        this.z = t2[2];
        this.w = t2[3];
    }

    public final void set(Tuple4i t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }

    public final void get(int[] t2) {
        t2[0] = this.x;
        t2[1] = this.y;
        t2[2] = this.z;
        t2[3] = this.w;
    }

    public final void get(Tuple4i t2) {
        t2.x = this.x;
        t2.y = this.y;
        t2.z = this.z;
        t2.w = this.w;
    }

    public final void add(Tuple4i t1, Tuple4i t2) {
        this.x = t1.x + t2.x;
        this.y = t1.y + t2.y;
        this.z = t1.z + t2.z;
        this.w = t1.w + t2.w;
    }

    public final void add(Tuple4i t1) {
        this.x += t1.x;
        this.y += t1.y;
        this.z += t1.z;
        this.w += t1.w;
    }

    public final void sub(Tuple4i t1, Tuple4i t2) {
        this.x = t1.x - t2.x;
        this.y = t1.y - t2.y;
        this.z = t1.z - t2.z;
        this.w = t1.w - t2.w;
    }

    public final void sub(Tuple4i t1) {
        this.x -= t1.x;
        this.y -= t1.y;
        this.z -= t1.z;
        this.w -= t1.w;
    }

    public final void negate(Tuple4i t1) {
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

    public final void scale(int s2, Tuple4i t1) {
        this.x = s2 * t1.x;
        this.y = s2 * t1.y;
        this.z = s2 * t1.z;
        this.w = s2 * t1.w;
    }

    public final void scale(int s2) {
        this.x *= s2;
        this.y *= s2;
        this.z *= s2;
        this.w *= s2;
    }

    public final void scaleAdd(int s2, Tuple4i t1, Tuple4i t2) {
        this.x = s2 * t1.x + t2.x;
        this.y = s2 * t1.y + t2.y;
        this.z = s2 * t1.z + t2.z;
        this.w = s2 * t1.w + t2.w;
    }

    public final void scaleAdd(int s2, Tuple4i t1) {
        this.x = s2 * this.x + t1.x;
        this.y = s2 * this.y + t1.y;
        this.z = s2 * this.z + t1.z;
        this.w = s2 * this.w + t1.w;
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + ")";
    }

    public boolean equals(Object t1) {
        try {
            Tuple4i t2 = (Tuple4i)t1;
            return this.x == t2.x && this.y == t2.y && this.z == t2.z && this.w == t2.w;
        }
        catch (NullPointerException e2) {
            return false;
        }
        catch (ClassCastException e1) {
            return false;
        }
    }

    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + (long)this.x;
        bits = 31L * bits + (long)this.y;
        bits = 31L * bits + (long)this.z;
        bits = 31L * bits + (long)this.w;
        return (int)(bits ^ bits >> 32);
    }

    public final void clamp(int min, int max, Tuple4i t2) {
        this.x = t2.x > max ? max : (t2.x < min ? min : t2.x);
        this.y = t2.y > max ? max : (t2.y < min ? min : t2.y);
        this.z = t2.z > max ? max : (t2.z < min ? min : t2.z);
        this.w = t2.w > max ? max : (t2.w < min ? min : t2.w);
    }

    public final void clampMin(int min, Tuple4i t2) {
        this.x = t2.x < min ? min : t2.x;
        this.y = t2.y < min ? min : t2.y;
        this.z = t2.z < min ? min : t2.z;
        this.w = t2.w < min ? min : t2.w;
    }

    public final void clampMax(int max, Tuple4i t2) {
        this.x = t2.x > max ? max : t2.x;
        this.y = t2.y > max ? max : t2.y;
        this.z = t2.z > max ? max : t2.z;
        this.w = t2.w > max ? max : t2.z;
    }

    public final void absolute(Tuple4i t2) {
        this.x = Math.abs(t2.x);
        this.y = Math.abs(t2.y);
        this.z = Math.abs(t2.z);
        this.w = Math.abs(t2.w);
    }

    public final void clamp(int min, int max) {
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

    public final void clampMin(int min) {
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

    public final void clampMax(int max) {
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

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e2) {
            throw new InternalError();
        }
    }

    public final int getX() {
        return this.x;
    }

    public final void setX(int x2) {
        this.x = x2;
    }

    public final int getY() {
        return this.y;
    }

    public final void setY(int y2) {
        this.y = y2;
    }

    public final int getZ() {
        return this.z;
    }

    public final void setZ(int z2) {
        this.z = z2;
    }

    public final int getW() {
        return this.w;
    }

    public final void setW(int w2) {
        this.w = w2;
    }
}

