/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.VecMathUtil;
import javax.vecmath.Vector3d;

public class AxisAngle4d
implements Serializable,
Cloneable {
    static final long serialVersionUID = 3644296204459140589L;
    public double x;
    public double y;
    public double z;
    public double angle;
    static final double EPS = 1.0E-12;

    public AxisAngle4d(double x2, double y2, double z2, double angle) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
        this.angle = angle;
    }

    public AxisAngle4d(double[] a2) {
        this.x = a2[0];
        this.y = a2[1];
        this.z = a2[2];
        this.angle = a2[3];
    }

    public AxisAngle4d(AxisAngle4d a1) {
        this.x = a1.x;
        this.y = a1.y;
        this.z = a1.z;
        this.angle = a1.angle;
    }

    public AxisAngle4d(AxisAngle4f a1) {
        this.x = a1.x;
        this.y = a1.y;
        this.z = a1.z;
        this.angle = a1.angle;
    }

    public AxisAngle4d(Vector3d axis, double angle) {
        this.x = axis.x;
        this.y = axis.y;
        this.z = axis.z;
        this.angle = angle;
    }

    public AxisAngle4d() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 1.0;
        this.angle = 0.0;
    }

    public final void set(double x2, double y2, double z2, double angle) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
        this.angle = angle;
    }

    public final void set(double[] a2) {
        this.x = a2[0];
        this.y = a2[1];
        this.z = a2[2];
        this.angle = a2[3];
    }

    public final void set(AxisAngle4d a1) {
        this.x = a1.x;
        this.y = a1.y;
        this.z = a1.z;
        this.angle = a1.angle;
    }

    public final void set(AxisAngle4f a1) {
        this.x = a1.x;
        this.y = a1.y;
        this.z = a1.z;
        this.angle = a1.angle;
    }

    public final void set(Vector3d axis, double angle) {
        this.x = axis.x;
        this.y = axis.y;
        this.z = axis.z;
        this.angle = angle;
    }

    public final void get(double[] a2) {
        a2[0] = this.x;
        a2[1] = this.y;
        a2[2] = this.z;
        a2[3] = this.angle;
    }

    public final void set(Matrix4f m1) {
        Matrix3d m3d = new Matrix3d();
        m1.get(m3d);
        this.x = (float)(m3d.m21 - m3d.m12);
        this.y = (float)(m3d.m02 - m3d.m20);
        this.z = (float)(m3d.m10 - m3d.m01);
        double mag = this.x * this.x + this.y * this.y + this.z * this.z;
        if (mag > 1.0E-12) {
            mag = Math.sqrt(mag);
            double sin = 0.5 * mag;
            double cos = 0.5 * (m3d.m00 + m3d.m11 + m3d.m22 - 1.0);
            this.angle = (float)Math.atan2(sin, cos);
            double invMag = 1.0 / mag;
            this.x *= invMag;
            this.y *= invMag;
            this.z *= invMag;
        } else {
            this.x = 0.0;
            this.y = 1.0;
            this.z = 0.0;
            this.angle = 0.0;
        }
    }

    public final void set(Matrix4d m1) {
        Matrix3d m3d = new Matrix3d();
        m1.get(m3d);
        this.x = (float)(m3d.m21 - m3d.m12);
        this.y = (float)(m3d.m02 - m3d.m20);
        this.z = (float)(m3d.m10 - m3d.m01);
        double mag = this.x * this.x + this.y * this.y + this.z * this.z;
        if (mag > 1.0E-12) {
            mag = Math.sqrt(mag);
            double sin = 0.5 * mag;
            double cos = 0.5 * (m3d.m00 + m3d.m11 + m3d.m22 - 1.0);
            this.angle = (float)Math.atan2(sin, cos);
            double invMag = 1.0 / mag;
            this.x *= invMag;
            this.y *= invMag;
            this.z *= invMag;
        } else {
            this.x = 0.0;
            this.y = 1.0;
            this.z = 0.0;
            this.angle = 0.0;
        }
    }

    public final void set(Matrix3f m1) {
        this.x = m1.m21 - m1.m12;
        this.y = m1.m02 - m1.m20;
        this.z = m1.m10 - m1.m01;
        double mag = this.x * this.x + this.y * this.y + this.z * this.z;
        if (mag > 1.0E-12) {
            mag = Math.sqrt(mag);
            double sin = 0.5 * mag;
            double cos = 0.5 * ((double)(m1.m00 + m1.m11 + m1.m22) - 1.0);
            this.angle = (float)Math.atan2(sin, cos);
            double invMag = 1.0 / mag;
            this.x *= invMag;
            this.y *= invMag;
            this.z *= invMag;
        } else {
            this.x = 0.0;
            this.y = 1.0;
            this.z = 0.0;
            this.angle = 0.0;
        }
    }

    public final void set(Matrix3d m1) {
        this.x = (float)(m1.m21 - m1.m12);
        this.y = (float)(m1.m02 - m1.m20);
        this.z = (float)(m1.m10 - m1.m01);
        double mag = this.x * this.x + this.y * this.y + this.z * this.z;
        if (mag > 1.0E-12) {
            mag = Math.sqrt(mag);
            double sin = 0.5 * mag;
            double cos = 0.5 * (m1.m00 + m1.m11 + m1.m22 - 1.0);
            this.angle = (float)Math.atan2(sin, cos);
            double invMag = 1.0 / mag;
            this.x *= invMag;
            this.y *= invMag;
            this.z *= invMag;
        } else {
            this.x = 0.0;
            this.y = 1.0;
            this.z = 0.0;
            this.angle = 0.0;
        }
    }

    public final void set(Quat4f q1) {
        double mag = q1.x * q1.x + q1.y * q1.y + q1.z * q1.z;
        if (mag > 1.0E-12) {
            mag = Math.sqrt(mag);
            double invMag = 1.0 / mag;
            this.x = (double)q1.x * invMag;
            this.y = (double)q1.y * invMag;
            this.z = (double)q1.z * invMag;
            this.angle = 2.0 * Math.atan2(mag, q1.w);
        } else {
            this.x = 0.0;
            this.y = 1.0;
            this.z = 0.0;
            this.angle = 0.0;
        }
    }

    public final void set(Quat4d q1) {
        double mag = q1.x * q1.x + q1.y * q1.y + q1.z * q1.z;
        if (mag > 1.0E-12) {
            mag = Math.sqrt(mag);
            double invMag = 1.0 / mag;
            this.x = q1.x * invMag;
            this.y = q1.y * invMag;
            this.z = q1.z * invMag;
            this.angle = 2.0 * Math.atan2(mag, q1.w);
        } else {
            this.x = 0.0;
            this.y = 1.0;
            this.z = 0.0;
            this.angle = 0.0;
        }
    }

    public String toString() {
        return "(" + this.x + ", " + this.y + ", " + this.z + ", " + this.angle + ")";
    }

    public boolean equals(AxisAngle4d a1) {
        try {
            return this.x == a1.x && this.y == a1.y && this.z == a1.z && this.angle == a1.angle;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean equals(Object o1) {
        try {
            AxisAngle4d a2 = (AxisAngle4d)o1;
            return this.x == a2.x && this.y == a2.y && this.z == a2.z && this.angle == a2.angle;
        }
        catch (NullPointerException e2) {
            return false;
        }
        catch (ClassCastException e1) {
            return false;
        }
    }

    public boolean epsilonEquals(AxisAngle4d a1, double epsilon) {
        double diff = this.x - a1.x;
        double d2 = diff < 0.0 ? -diff : diff;
        if (d2 > epsilon) {
            return false;
        }
        diff = this.y - a1.y;
        double d3 = diff < 0.0 ? -diff : diff;
        if (d3 > epsilon) {
            return false;
        }
        diff = this.z - a1.z;
        double d4 = diff < 0.0 ? -diff : diff;
        if (d4 > epsilon) {
            return false;
        }
        diff = this.angle - a1.angle;
        double d5 = diff < 0.0 ? -diff : diff;
        return !(d5 > epsilon);
    }

    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.x);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.y);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.z);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.angle);
        return (int)(bits ^ bits >> 32);
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e2) {
            throw new InternalError();
        }
    }

    public final double getAngle() {
        return this.angle;
    }

    public final void setAngle(double angle) {
        this.angle = angle;
    }

    public double getX() {
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

    public double getZ() {
        return this.z;
    }

    public final void setZ(double z2) {
        this.z = z2;
    }
}

