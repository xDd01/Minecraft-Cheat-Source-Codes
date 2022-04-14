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
import javax.vecmath.Quat4d;
import javax.vecmath.Tuple4d;
import javax.vecmath.Tuple4f;

public class Quat4f
extends Tuple4f
implements Serializable {
    static final long serialVersionUID = 2675933778405442383L;
    static final double EPS = 1.0E-6;
    static final double EPS2 = 1.0E-30;
    static final double PIO2 = 1.57079632679;

    public Quat4f(float x2, float y2, float z2, float w2) {
        float mag = (float)(1.0 / Math.sqrt(x2 * x2 + y2 * y2 + z2 * z2 + w2 * w2));
        this.x = x2 * mag;
        this.y = y2 * mag;
        this.z = z2 * mag;
        this.w = w2 * mag;
    }

    public Quat4f(float[] q2) {
        float mag = (float)(1.0 / Math.sqrt(q2[0] * q2[0] + q2[1] * q2[1] + q2[2] * q2[2] + q2[3] * q2[3]));
        this.x = q2[0] * mag;
        this.y = q2[1] * mag;
        this.z = q2[2] * mag;
        this.w = q2[3] * mag;
    }

    public Quat4f(Quat4f q1) {
        super(q1);
    }

    public Quat4f(Quat4d q1) {
        super(q1);
    }

    public Quat4f(Tuple4f t1) {
        float mag = (float)(1.0 / Math.sqrt(t1.x * t1.x + t1.y * t1.y + t1.z * t1.z + t1.w * t1.w));
        this.x = t1.x * mag;
        this.y = t1.y * mag;
        this.z = t1.z * mag;
        this.w = t1.w * mag;
    }

    public Quat4f(Tuple4d t1) {
        double mag = 1.0 / Math.sqrt(t1.x * t1.x + t1.y * t1.y + t1.z * t1.z + t1.w * t1.w);
        this.x = (float)(t1.x * mag);
        this.y = (float)(t1.y * mag);
        this.z = (float)(t1.z * mag);
        this.w = (float)(t1.w * mag);
    }

    public Quat4f() {
    }

    public final void conjugate(Quat4f q1) {
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

    public final void mul(Quat4f q1, Quat4f q2) {
        if (this != q1 && this != q2) {
            this.w = q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z;
            this.x = q1.w * q2.x + q2.w * q1.x + q1.y * q2.z - q1.z * q2.y;
            this.y = q1.w * q2.y + q2.w * q1.y - q1.x * q2.z + q1.z * q2.x;
            this.z = q1.w * q2.z + q2.w * q1.z + q1.x * q2.y - q1.y * q2.x;
        } else {
            float w2 = q1.w * q2.w - q1.x * q2.x - q1.y * q2.y - q1.z * q2.z;
            float x2 = q1.w * q2.x + q2.w * q1.x + q1.y * q2.z - q1.z * q2.y;
            float y2 = q1.w * q2.y + q2.w * q1.y - q1.x * q2.z + q1.z * q2.x;
            this.z = q1.w * q2.z + q2.w * q1.z + q1.x * q2.y - q1.y * q2.x;
            this.w = w2;
            this.x = x2;
            this.y = y2;
        }
    }

    public final void mul(Quat4f q1) {
        float w2 = this.w * q1.w - this.x * q1.x - this.y * q1.y - this.z * q1.z;
        float x2 = this.w * q1.x + q1.w * this.x + this.y * q1.z - this.z * q1.y;
        float y2 = this.w * q1.y + q1.w * this.y - this.x * q1.z + this.z * q1.x;
        this.z = this.w * q1.z + q1.w * this.z + this.x * q1.y - this.y * q1.x;
        this.w = w2;
        this.x = x2;
        this.y = y2;
    }

    public final void mulInverse(Quat4f q1, Quat4f q2) {
        Quat4f tempQuat = new Quat4f(q2);
        tempQuat.inverse();
        this.mul(q1, tempQuat);
    }

    public final void mulInverse(Quat4f q1) {
        Quat4f tempQuat = new Quat4f(q1);
        tempQuat.inverse();
        this.mul(tempQuat);
    }

    public final void inverse(Quat4f q1) {
        float norm = 1.0f / (q1.w * q1.w + q1.x * q1.x + q1.y * q1.y + q1.z * q1.z);
        this.w = norm * q1.w;
        this.x = -norm * q1.x;
        this.y = -norm * q1.y;
        this.z = -norm * q1.z;
    }

    public final void inverse() {
        float norm = 1.0f / (this.w * this.w + this.x * this.x + this.y * this.y + this.z * this.z);
        this.w *= norm;
        this.x *= -norm;
        this.y *= -norm;
        this.z *= -norm;
    }

    public final void normalize(Quat4f q1) {
        float norm = q1.x * q1.x + q1.y * q1.y + q1.z * q1.z + q1.w * q1.w;
        if (norm > 0.0f) {
            norm = 1.0f / (float)Math.sqrt(norm);
            this.x = norm * q1.x;
            this.y = norm * q1.y;
            this.z = norm * q1.z;
            this.w = norm * q1.w;
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
            this.w = 0.0f;
        }
    }

    public final void normalize() {
        float norm = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
        if (norm > 0.0f) {
            norm = 1.0f / (float)Math.sqrt(norm);
            this.x *= norm;
            this.y *= norm;
            this.z *= norm;
            this.w *= norm;
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
            this.w = 0.0f;
        }
    }

    public final void set(Matrix4f m1) {
        float ww2 = 0.25f * (m1.m00 + m1.m11 + m1.m22 + m1.m33);
        if (ww2 >= 0.0f) {
            if ((double)ww2 >= 1.0E-30) {
                this.w = (float)Math.sqrt(ww2);
                ww2 = 0.25f / this.w;
                this.x = (m1.m21 - m1.m12) * ww2;
                this.y = (m1.m02 - m1.m20) * ww2;
                this.z = (m1.m10 - m1.m01) * ww2;
                return;
            }
        } else {
            this.w = 0.0f;
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 1.0f;
            return;
        }
        this.w = 0.0f;
        ww2 = -0.5f * (m1.m11 + m1.m22);
        if (ww2 >= 0.0f) {
            if ((double)ww2 >= 1.0E-30) {
                this.x = (float)Math.sqrt(ww2);
                ww2 = 1.0f / (2.0f * this.x);
                this.y = m1.m10 * ww2;
                this.z = m1.m20 * ww2;
                return;
            }
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 1.0f;
            return;
        }
        this.x = 0.0f;
        ww2 = 0.5f * (1.0f - m1.m22);
        if ((double)ww2 >= 1.0E-30) {
            this.y = (float)Math.sqrt(ww2);
            this.z = m1.m21 / (2.0f * this.y);
            return;
        }
        this.y = 0.0f;
        this.z = 1.0f;
    }

    public final void set(Matrix4d m1) {
        double ww2 = 0.25 * (m1.m00 + m1.m11 + m1.m22 + m1.m33);
        if (ww2 >= 0.0) {
            if (ww2 >= 1.0E-30) {
                this.w = (float)Math.sqrt(ww2);
                ww2 = 0.25 / (double)this.w;
                this.x = (float)((m1.m21 - m1.m12) * ww2);
                this.y = (float)((m1.m02 - m1.m20) * ww2);
                this.z = (float)((m1.m10 - m1.m01) * ww2);
                return;
            }
        } else {
            this.w = 0.0f;
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 1.0f;
            return;
        }
        this.w = 0.0f;
        ww2 = -0.5 * (m1.m11 + m1.m22);
        if (ww2 >= 0.0) {
            if (ww2 >= 1.0E-30) {
                this.x = (float)Math.sqrt(ww2);
                ww2 = 0.5 / (double)this.x;
                this.y = (float)(m1.m10 * ww2);
                this.z = (float)(m1.m20 * ww2);
                return;
            }
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 1.0f;
            return;
        }
        this.x = 0.0f;
        ww2 = 0.5 * (1.0 - m1.m22);
        if (ww2 >= 1.0E-30) {
            this.y = (float)Math.sqrt(ww2);
            this.z = (float)(m1.m21 / (2.0 * (double)this.y));
            return;
        }
        this.y = 0.0f;
        this.z = 1.0f;
    }

    public final void set(Matrix3f m1) {
        float ww2 = 0.25f * (m1.m00 + m1.m11 + m1.m22 + 1.0f);
        if (ww2 >= 0.0f) {
            if ((double)ww2 >= 1.0E-30) {
                this.w = (float)Math.sqrt(ww2);
                ww2 = 0.25f / this.w;
                this.x = (m1.m21 - m1.m12) * ww2;
                this.y = (m1.m02 - m1.m20) * ww2;
                this.z = (m1.m10 - m1.m01) * ww2;
                return;
            }
        } else {
            this.w = 0.0f;
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 1.0f;
            return;
        }
        this.w = 0.0f;
        ww2 = -0.5f * (m1.m11 + m1.m22);
        if (ww2 >= 0.0f) {
            if ((double)ww2 >= 1.0E-30) {
                this.x = (float)Math.sqrt(ww2);
                ww2 = 0.5f / this.x;
                this.y = m1.m10 * ww2;
                this.z = m1.m20 * ww2;
                return;
            }
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 1.0f;
            return;
        }
        this.x = 0.0f;
        ww2 = 0.5f * (1.0f - m1.m22);
        if ((double)ww2 >= 1.0E-30) {
            this.y = (float)Math.sqrt(ww2);
            this.z = m1.m21 / (2.0f * this.y);
            return;
        }
        this.y = 0.0f;
        this.z = 1.0f;
    }

    public final void set(Matrix3d m1) {
        double ww2 = 0.25 * (m1.m00 + m1.m11 + m1.m22 + 1.0);
        if (ww2 >= 0.0) {
            if (ww2 >= 1.0E-30) {
                this.w = (float)Math.sqrt(ww2);
                ww2 = 0.25 / (double)this.w;
                this.x = (float)((m1.m21 - m1.m12) * ww2);
                this.y = (float)((m1.m02 - m1.m20) * ww2);
                this.z = (float)((m1.m10 - m1.m01) * ww2);
                return;
            }
        } else {
            this.w = 0.0f;
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 1.0f;
            return;
        }
        this.w = 0.0f;
        ww2 = -0.5 * (m1.m11 + m1.m22);
        if (ww2 >= 0.0) {
            if (ww2 >= 1.0E-30) {
                this.x = (float)Math.sqrt(ww2);
                ww2 = 0.5 / (double)this.x;
                this.y = (float)(m1.m10 * ww2);
                this.z = (float)(m1.m20 * ww2);
                return;
            }
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 1.0f;
            return;
        }
        this.x = 0.0f;
        ww2 = 0.5 * (1.0 - m1.m22);
        if (ww2 >= 1.0E-30) {
            this.y = (float)Math.sqrt(ww2);
            this.z = (float)(m1.m21 / (2.0 * (double)this.y));
            return;
        }
        this.y = 0.0f;
        this.z = 1.0f;
    }

    public final void set(AxisAngle4f a2) {
        float amag = (float)Math.sqrt(a2.x * a2.x + a2.y * a2.y + a2.z * a2.z);
        if ((double)amag < 1.0E-6) {
            this.w = 0.0f;
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
        } else {
            amag = 1.0f / amag;
            float mag = (float)Math.sin((double)a2.angle / 2.0);
            this.w = (float)Math.cos((double)a2.angle / 2.0);
            this.x = a2.x * amag * mag;
            this.y = a2.y * amag * mag;
            this.z = a2.z * amag * mag;
        }
    }

    public final void set(AxisAngle4d a2) {
        float amag = (float)(1.0 / Math.sqrt(a2.x * a2.x + a2.y * a2.y + a2.z * a2.z));
        if ((double)amag < 1.0E-6) {
            this.w = 0.0f;
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
        } else {
            amag = 1.0f / amag;
            float mag = (float)Math.sin(a2.angle / 2.0);
            this.w = (float)Math.cos(a2.angle / 2.0);
            this.x = (float)a2.x * amag * mag;
            this.y = (float)a2.y * amag * mag;
            this.z = (float)a2.z * amag * mag;
        }
    }

    public final void interpolate(Quat4f q1, float alpha) {
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
        if (1.0 - dot > 1.0E-6) {
            double om = Math.acos(dot);
            double sinom = Math.sin(om);
            s1 = Math.sin((1.0 - (double)alpha) * om) / sinom;
            s2 = Math.sin((double)alpha * om) / sinom;
        } else {
            s1 = 1.0 - (double)alpha;
            s2 = alpha;
        }
        this.w = (float)(s1 * (double)this.w + s2 * (double)q1.w);
        this.x = (float)(s1 * (double)this.x + s2 * (double)q1.x);
        this.y = (float)(s1 * (double)this.y + s2 * (double)q1.y);
        this.z = (float)(s1 * (double)this.z + s2 * (double)q1.z);
    }

    public final void interpolate(Quat4f q1, Quat4f q2, float alpha) {
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
        if (1.0 - dot > 1.0E-6) {
            double om = Math.acos(dot);
            double sinom = Math.sin(om);
            s1 = Math.sin((1.0 - (double)alpha) * om) / sinom;
            s2 = Math.sin((double)alpha * om) / sinom;
        } else {
            s1 = 1.0 - (double)alpha;
            s2 = alpha;
        }
        this.w = (float)(s1 * (double)q1.w + s2 * (double)q2.w);
        this.x = (float)(s1 * (double)q1.x + s2 * (double)q2.x);
        this.y = (float)(s1 * (double)q1.y + s2 * (double)q2.y);
        this.z = (float)(s1 * (double)q1.z + s2 * (double)q2.z);
    }
}

