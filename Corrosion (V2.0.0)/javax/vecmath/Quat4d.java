/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Tuple4d;
import javax.vecmath.Tuple4f;

public class Quat4d
extends Tuple4d
implements Serializable {
    static final long serialVersionUID = 7577479888820201099L;
    static final double EPS = 1.0E-12;
    static final double EPS2 = 1.0E-30;
    static final double PIO2 = 1.57079632679;

    public Quat4d(double x2, double y2, double z2, double w2) {
        double mag = 1.0 / Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2 + w2 * w2);
        this.x = x2 * mag;
        this.y = y2 * mag;
        this.z = z2 * mag;
        this.w = w2 * mag;
    }

    public Quat4d(double[] q2) {
        double mag = 1.0 / Math.sqrt(q2[0] * q2[0] + q2[1] * q2[1] + q2[2] * q2[2] + q2[3] * q2[3]);
        this.x = q2[0] * mag;
        this.y = q2[1] * mag;
        this.z = q2[2] * mag;
        this.w = q2[3] * mag;
    }

    public Quat4d(Quat4d q1) {
        super(q1);
    }

    public Quat4d(Quat4f q1) {
        super(q1);
    }

    public Quat4d(Tuple4f t1) {
        double mag = 1.0 / Math.sqrt(t1.x * t1.x + t1.y * t1.y + t1.z * t1.z + t1.w * t1.w);
        this.x = (double)t1.x * mag;
        this.y = (double)t1.y * mag;
        this.z = (double)t1.z * mag;
        this.w = (double)t1.w * mag;
    }

    public Quat4d(Tuple4d t1) {
        double mag = 1.0 / Math.sqrt(t1.x * t1.x + t1.y * t1.y + t1.z * t1.z + t1.w * t1.w);
        this.x = t1.x * mag;
        this.y = t1.y * mag;
        this.z = t1.z * mag;
        this.w = t1.w * mag;
    }

    public Quat4d() {
    }

    public final void conjugate(Quat4d q1) {
        this.x = -q1.x;
        this.y = -q1.y;
        this.z = -q1.z;
        this.w = q1.w;
    }

    public final void conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public final void mul(Quat4d q1, Quat4d q2) {
        if (this != q1 && this != q2) {
            this.w = q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z;
            this.x = q1.w * q2.x + q2.w * q1.x + q1.y * q2.z - q1.z * q2.y;
            this.y = q1.w * q2.y + q2.w * q1.y - q1.x * q2.z + q1.z * q2.x;
            this.z = q1.w * q2.z + q2.w * q1.z + q1.x * q2.y - q1.y * q2.x;
        } else {
            double w2 = q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z;
            double x2 = q1.w * q2.x + q2.w * q1.x + q1.y * q2.z - q1.z * q2.y;
            double y2 = q1.w * q2.y + q2.w * q1.y - q1.x * q2.z + q1.z * q2.x;
            this.z = q1.w * q2.z + q2.w * q1.z + q1.x * q2.y - q1.y * q2.x;
            this.w = w2;
            this.x = x2;
            this.y = y2;
        }
    }

    public final void mul(Quat4d q1) {
        double w2 = this.w * q1.w - this.x * q1.x - this.y * q1.y - this.z * q1.z;
        double x2 = this.w * q1.x + q1.w * this.x + this.y * q1.z - this.z * q1.y;
        double y2 = this.w * q1.y + q1.w * this.y - this.x * q1.z + this.z * q1.x;
        this.z = this.w * q1.z + q1.w * this.z + this.x * q1.y - this.y * q1.x;
        this.w = w2;
        this.x = x2;
        this.y = y2;
    }

    public final void mulInverse(Quat4d q1, Quat4d q2) {
        Quat4d tempQuat = new Quat4d(q2);
        tempQuat.inverse();
        this.mul(q1, tempQuat);
    }

    public final void mulInverse(Quat4d q1) {
        Quat4d tempQuat = new Quat4d(q1);
        tempQuat.inverse();
        this.mul(tempQuat);
    }

    public final void inverse(Quat4d q1) {
        double norm = 1.0 / (q1.w * q1.w + q1.x * q1.x + q1.y * q1.y + q1.z * q1.z);
        this.w = norm * q1.w;
        this.x = -norm * q1.x;
        this.y = -norm * q1.y;
        this.z = -norm * q1.z;
    }

    public final void inverse() {
        double norm = 1.0 / (this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z);
        this.w *= norm;
        this.x *= -norm;
        this.y *= -norm;
        this.z *= -norm;
    }

    public final void normalize(Quat4d q1) {
        double norm = q1.x * q1.x + q1.y * q1.y + q1.z * q1.z + q1.w * q1.w;
        if (norm > 0.0) {
            norm = 1.0 / Math.sqrt(norm);
            this.x = norm * q1.x;
            this.y = norm * q1.y;
            this.z = norm * q1.z;
            this.w = norm * q1.w;
        } else {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
            this.w = 0.0;
        }
    }

    public final void normalize() {
        double norm = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
        if (norm > 0.0) {
            norm = 1.0 / Math.sqrt(norm);
            this.x *= norm;
            this.y *= norm;
            this.z *= norm;
            this.w *= norm;
        } else {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
            this.w = 0.0;
        }
    }

    public final void set(Matrix4f m1) {
        double ww2 = 0.25 * (double)(m1.m00 + m1.m11 + m1.m22 + m1.m33);
        if (ww2 >= 0.0) {
            if (ww2 >= 1.0E-30) {
                this.w = Math.sqrt(ww2);
                ww2 = 0.25 / this.w;
                this.x = (double)(m1.m21 - m1.m12) * ww2;
                this.y = (double)(m1.m02 - m1.m20) * ww2;
                this.z = (double)(m1.m10 - m1.m01) * ww2;
                return;
            }
        } else {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        this.w = 0.0;
        ww2 = -0.5 * (double)(m1.m11 + m1.m22);
        if (ww2 >= 0.0) {
            if (ww2 >= 1.0E-30) {
                this.x = Math.sqrt(ww2);
                ww2 = 1.0 / (2.0 * this.x);
                this.y = (double)m1.m10 * ww2;
                this.z = (double)m1.m20 * ww2;
                return;
            }
        } else {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        this.x = 0.0;
        ww2 = 0.5 * (1.0 - (double)m1.m22);
        if (ww2 >= 1.0E-30) {
            this.y = Math.sqrt(ww2);
            this.z = (double)m1.m21 / (2.0 * this.y);
            return;
        }
        this.y = 0.0;
        this.z = 1.0;
    }

    public final void set(Matrix4d m1) {
        double ww2 = 0.25 * (m1.m00 + m1.m11 + m1.m22 + m1.m33);
        if (ww2 >= 0.0) {
            if (ww2 >= 1.0E-30) {
                this.w = Math.sqrt(ww2);
                ww2 = 0.25 / this.w;
                this.x = (m1.m21 - m1.m12) * ww2;
                this.y = (m1.m02 - m1.m20) * ww2;
                this.z = (m1.m10 - m1.m01) * ww2;
                return;
            }
        } else {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        this.w = 0.0;
        ww2 = -0.5 * (m1.m11 + m1.m22);
        if (ww2 >= 0.0) {
            if (ww2 >= 1.0E-30) {
                this.x = Math.sqrt(ww2);
                ww2 = 0.5 / this.x;
                this.y = m1.m10 * ww2;
                this.z = m1.m20 * ww2;
                return;
            }
        } else {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        this.x = 0.0;
        ww2 = 0.5 * (1.0 - m1.m22);
        if (ww2 >= 1.0E-30) {
            this.y = Math.sqrt(ww2);
            this.z = m1.m21 / (2.0 * this.y);
            return;
        }
        this.y = 0.0;
        this.z = 1.0;
    }

    public final void set(Matrix3f m1) {
        double ww2 = 0.25 * ((double)(m1.m00 + m1.m11 + m1.m22) + 1.0);
        if (ww2 >= 0.0) {
            if (ww2 >= 1.0E-30) {
                this.w = Math.sqrt(ww2);
                ww2 = 0.25 / this.w;
                this.x = (double)(m1.m21 - m1.m12) * ww2;
                this.y = (double)(m1.m02 - m1.m20) * ww2;
                this.z = (double)(m1.m10 - m1.m01) * ww2;
                return;
            }
        } else {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        this.w = 0.0;
        ww2 = -0.5 * (double)(m1.m11 + m1.m22);
        if (ww2 >= 0.0) {
            if (ww2 >= 1.0E-30) {
                this.x = Math.sqrt(ww2);
                ww2 = 0.5 / this.x;
                this.y = (double)m1.m10 * ww2;
                this.z = (double)m1.m20 * ww2;
                return;
            }
        } else {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        this.x = 0.0;
        ww2 = 0.5 * (1.0 - (double)m1.m22);
        if (ww2 >= 1.0E-30) {
            this.y = Math.sqrt(ww2);
            this.z = (double)m1.m21 / (2.0 * this.y);
        }
        this.y = 0.0;
        this.z = 1.0;
    }

    public final void set(Matrix3d m1) {
        double ww2 = 0.25 * (m1.m00 + m1.m11 + m1.m22 + 1.0);
        if (ww2 >= 0.0) {
            if (ww2 >= 1.0E-30) {
                this.w = Math.sqrt(ww2);
                ww2 = 0.25 / this.w;
                this.x = (m1.m21 - m1.m12) * ww2;
                this.y = (m1.m02 - m1.m20) * ww2;
                this.z = (m1.m10 - m1.m01) * ww2;
                return;
            }
        } else {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        this.w = 0.0;
        ww2 = -0.5 * (m1.m11 + m1.m22);
        if (ww2 >= 0.0) {
            if (ww2 >= 1.0E-30) {
                this.x = Math.sqrt(ww2);
                ww2 = 0.5 / this.x;
                this.y = m1.m10 * ww2;
                this.z = m1.m20 * ww2;
                return;
            }
        } else {
            this.x = 0.0;
            this.y = 0.0;
            this.z = 1.0;
            return;
        }
        this.x = 0.0;
        ww2 = 0.5 * (1.0 - m1.m22);
        if (ww2 >= 1.0E-30) {
            this.y = Math.sqrt(ww2);
            this.z = m1.m21 / (2.0 * this.y);
            return;
        }
        this.y = 0.0;
        this.z = 1.0;
    }

    public final void set(AxisAngle4f a2) {
        double amag = Math.sqrt(a2.x * a2.x + a2.y * a2.y + a2.z * a2.z);
        if (amag < 1.0E-12) {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
        } else {
            double mag = Math.sin((double)a2.angle / 2.0);
            amag = 1.0 / amag;
            this.w = Math.cos((double)a2.angle / 2.0);
            this.x = (double)a2.x * amag * mag;
            this.y = (double)a2.y * amag * mag;
            this.z = (double)a2.z * amag * mag;
        }
    }

    public final void set(AxisAngle4d a2) {
        double amag = Math.sqrt(a2.x * a2.x + a2.y * a2.y + a2.z * a2.z);
        if (amag < 1.0E-12) {
            this.w = 0.0;
            this.x = 0.0;
            this.y = 0.0;
            this.z = 0.0;
        } else {
            amag = 1.0 / amag;
            double mag = Math.sin(a2.angle / 2.0);
            this.w = Math.cos(a2.angle / 2.0);
            this.x = a2.x * amag * mag;
            this.y = a2.y * amag * mag;
            this.z = a2.z * amag * mag;
        }
    }

    public final void interpolate(Quat4d q1, double alpha) {
        double s2;
        double s1;
        double dot = this.x * q1.x + this.y * q1.y + this.z * q1.z + this.w * q1.w;
        if (dot < 0.0) {
            q1.x = -q1.x;
            q1.y = -q1.y;
            q1.z = -q1.z;
            q1.w = -q1.w;
            dot = -dot;
        }
        if (1.0 - dot > 1.0E-12) {
            double om = Math.acos(dot);
            double sinom = Math.sin(om);
            s1 = Math.sin((1.0 - alpha) * om) / sinom;
            s2 = Math.sin(alpha * om) / sinom;
        } else {
            s1 = 1.0 - alpha;
            s2 = alpha;
        }
        this.w = s1 * this.w + s2 * q1.w;
        this.x = s1 * this.x + s2 * q1.x;
        this.y = s1 * this.y + s2 * q1.y;
        this.z = s1 * this.z + s2 * q1.z;
    }

    public final void interpolate(Quat4d q1, Quat4d q2, double alpha) {
        double s2;
        double s1;
        double dot = q2.x * q1.x + q2.y * q1.y + q2.z * q1.z + q2.w * q1.w;
        if (dot < 0.0) {
            q1.x = -q1.x;
            q1.y = -q1.y;
            q1.z = -q1.z;
            q1.w = -q1.w;
            dot = -dot;
        }
        if (1.0 - dot > 1.0E-12) {
            double om = Math.acos(dot);
            double sinom = Math.sin(om);
            s1 = Math.sin((1.0 - alpha) * om) / sinom;
            s2 = Math.sin(alpha * om) / sinom;
        } else {
            s1 = 1.0 - alpha;
            s2 = alpha;
        }
        this.w = s1 * q1.w + s2 * q2.w;
        this.x = s1 * q1.x + s2 * q2.x;
        this.y = s1 * q1.y + s2 * q2.y;
        this.z = s1 * q1.z + s2 * q2.z;
    }
}

