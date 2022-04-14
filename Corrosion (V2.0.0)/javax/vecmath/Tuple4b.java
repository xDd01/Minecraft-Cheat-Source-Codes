/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;

public abstract class Tuple4b
implements Serializable,
Cloneable {
    static final long serialVersionUID = -8226727741811898211L;
    public byte x;
    public byte y;
    public byte z;
    public byte w;

    public Tuple4b(byte b1, byte b2, byte b3, byte b4) {
        this.x = b1;
        this.y = b2;
        this.z = b3;
        this.w = b4;
    }

    public Tuple4b(byte[] t2) {
        this.x = t2[0];
        this.y = t2[1];
        this.z = t2[2];
        this.w = t2[3];
    }

    public Tuple4b(Tuple4b t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }

    public Tuple4b() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }

    public String toString() {
        return "(" + (this.x & 0xFF) + ", " + (this.y & 0xFF) + ", " + (this.z & 0xFF) + ", " + (this.w & 0xFF) + ")";
    }

    public final void get(byte[] b2) {
        b2[0] = this.x;
        b2[1] = this.y;
        b2[2] = this.z;
        b2[3] = this.w;
    }

    public final void get(Tuple4b t1) {
        t1.x = this.x;
        t1.y = this.y;
        t1.z = this.z;
        t1.w = this.w;
    }

    public final void set(Tuple4b t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = t1.w;
    }

    public final void set(byte[] b2) {
        this.x = b2[0];
        this.y = b2[1];
        this.z = b2[2];
        this.w = b2[3];
    }

    public boolean equals(Tuple4b t1) {
        try {
            return this.x == t1.x && this.y == t1.y && this.z == t1.z && this.w == t1.w;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean equals(Object t1) {
        try {
            Tuple4b t2 = (Tuple4b)t1;
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
        return (this.x & 0xFF) << 0 | (this.y & 0xFF) << 8 | (this.z & 0xFF) << 16 | (this.w & 0xFF) << 24;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e2) {
            throw new InternalError();
        }
    }

    public final byte getX() {
        return this.x;
    }

    public final void setX(byte x2) {
        this.x = x2;
    }

    public final byte getY() {
        return this.y;
    }

    public final void setY(byte y2) {
        this.y = y2;
    }

    public final byte getZ() {
        return this.z;
    }

    public final void setZ(byte z2) {
        this.z = z2;
    }

    public final byte getW() {
        return this.w;
    }

    public final void setW(byte w2) {
        this.w = w2;
    }
}

