/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Tuple3f;
import javax.vecmath.VecMathI18N;
import javax.vecmath.VecMathUtil;
import javax.vecmath.Vector3f;

public class Matrix3f
implements Serializable,
Cloneable {
    static final long serialVersionUID = 329697160112089834L;
    public float m00;
    public float m01;
    public float m02;
    public float m10;
    public float m11;
    public float m12;
    public float m20;
    public float m21;
    public float m22;
    private static final double EPS = 1.0E-8;

    public Matrix3f(float m00, float m01, float m02, float m10, float m11, float m12, float m20, float m21, float m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    public Matrix3f(float[] v2) {
        this.m00 = v2[0];
        this.m01 = v2[1];
        this.m02 = v2[2];
        this.m10 = v2[3];
        this.m11 = v2[4];
        this.m12 = v2[5];
        this.m20 = v2[6];
        this.m21 = v2[7];
        this.m22 = v2[8];
    }

    public Matrix3f(Matrix3d m1) {
        this.m00 = (float)m1.m00;
        this.m01 = (float)m1.m01;
        this.m02 = (float)m1.m02;
        this.m10 = (float)m1.m10;
        this.m11 = (float)m1.m11;
        this.m12 = (float)m1.m12;
        this.m20 = (float)m1.m20;
        this.m21 = (float)m1.m21;
        this.m22 = (float)m1.m22;
    }

    public Matrix3f(Matrix3f m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
    }

    public Matrix3f() {
        this.m00 = 0.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 0.0f;
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 0.0f;
    }

    public String toString() {
        return this.m00 + ", " + this.m01 + ", " + this.m02 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + "\n";
    }

    public final void setIdentity() {
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
    }

    public final void setScale(float scale) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        this.m00 = (float)(tmp_rot[0] * (double)scale);
        this.m01 = (float)(tmp_rot[1] * (double)scale);
        this.m02 = (float)(tmp_rot[2] * (double)scale);
        this.m10 = (float)(tmp_rot[3] * (double)scale);
        this.m11 = (float)(tmp_rot[4] * (double)scale);
        this.m12 = (float)(tmp_rot[5] * (double)scale);
        this.m20 = (float)(tmp_rot[6] * (double)scale);
        this.m21 = (float)(tmp_rot[7] * (double)scale);
        this.m22 = (float)(tmp_rot[8] * (double)scale);
    }

    public final void setElement(int row, int column, float value) {
        block0 : switch (row) {
            case 0: {
                switch (column) {
                    case 0: {
                        this.m00 = value;
                        break block0;
                    }
                    case 1: {
                        this.m01 = value;
                        break block0;
                    }
                    case 2: {
                        this.m02 = value;
                        break block0;
                    }
                }
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
            }
            case 1: {
                switch (column) {
                    case 0: {
                        this.m10 = value;
                        break block0;
                    }
                    case 1: {
                        this.m11 = value;
                        break block0;
                    }
                    case 2: {
                        this.m12 = value;
                        break block0;
                    }
                }
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
            }
            case 2: {
                switch (column) {
                    case 0: {
                        this.m20 = value;
                        break block0;
                    }
                    case 1: {
                        this.m21 = value;
                        break block0;
                    }
                    case 2: {
                        this.m22 = value;
                        break block0;
                    }
                }
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f0"));
            }
        }
    }

    public final void getRow(int row, Vector3f v2) {
        if (row == 0) {
            v2.x = this.m00;
            v2.y = this.m01;
            v2.z = this.m02;
        } else if (row == 1) {
            v2.x = this.m10;
            v2.y = this.m11;
            v2.z = this.m12;
        } else if (row == 2) {
            v2.x = this.m20;
            v2.y = this.m21;
            v2.z = this.m22;
        } else {
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f1"));
        }
    }

    public final void getRow(int row, float[] v2) {
        if (row == 0) {
            v2[0] = this.m00;
            v2[1] = this.m01;
            v2[2] = this.m02;
        } else if (row == 1) {
            v2[0] = this.m10;
            v2[1] = this.m11;
            v2[2] = this.m12;
        } else if (row == 2) {
            v2[0] = this.m20;
            v2[1] = this.m21;
            v2[2] = this.m22;
        } else {
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f1"));
        }
    }

    public final void getColumn(int column, Vector3f v2) {
        if (column == 0) {
            v2.x = this.m00;
            v2.y = this.m10;
            v2.z = this.m20;
        } else if (column == 1) {
            v2.x = this.m01;
            v2.y = this.m11;
            v2.z = this.m21;
        } else if (column == 2) {
            v2.x = this.m02;
            v2.y = this.m12;
            v2.z = this.m22;
        } else {
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f3"));
        }
    }

    public final void getColumn(int column, float[] v2) {
        if (column == 0) {
            v2[0] = this.m00;
            v2[1] = this.m10;
            v2[2] = this.m20;
        } else if (column == 1) {
            v2[0] = this.m01;
            v2[1] = this.m11;
            v2[2] = this.m21;
        } else if (column == 2) {
            v2[0] = this.m02;
            v2[1] = this.m12;
            v2[2] = this.m22;
        } else {
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f3"));
        }
    }

    public final float getElement(int row, int column) {
        switch (row) {
            case 0: {
                switch (column) {
                    case 0: {
                        return this.m00;
                    }
                    case 1: {
                        return this.m01;
                    }
                    case 2: {
                        return this.m02;
                    }
                }
                break;
            }
            case 1: {
                switch (column) {
                    case 0: {
                        return this.m10;
                    }
                    case 1: {
                        return this.m11;
                    }
                    case 2: {
                        return this.m12;
                    }
                }
                break;
            }
            case 2: {
                switch (column) {
                    case 0: {
                        return this.m20;
                    }
                    case 1: {
                        return this.m21;
                    }
                    case 2: {
                        return this.m22;
                    }
                }
                break;
            }
        }
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f5"));
    }

    public final void setRow(int row, float x2, float y2, float z2) {
        switch (row) {
            case 0: {
                this.m00 = x2;
                this.m01 = y2;
                this.m02 = z2;
                break;
            }
            case 1: {
                this.m10 = x2;
                this.m11 = y2;
                this.m12 = z2;
                break;
            }
            case 2: {
                this.m20 = x2;
                this.m21 = y2;
                this.m22 = z2;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
            }
        }
    }

    public final void setRow(int row, Vector3f v2) {
        switch (row) {
            case 0: {
                this.m00 = v2.x;
                this.m01 = v2.y;
                this.m02 = v2.z;
                break;
            }
            case 1: {
                this.m10 = v2.x;
                this.m11 = v2.y;
                this.m12 = v2.z;
                break;
            }
            case 2: {
                this.m20 = v2.x;
                this.m21 = v2.y;
                this.m22 = v2.z;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
            }
        }
    }

    public final void setRow(int row, float[] v2) {
        switch (row) {
            case 0: {
                this.m00 = v2[0];
                this.m01 = v2[1];
                this.m02 = v2[2];
                break;
            }
            case 1: {
                this.m10 = v2[0];
                this.m11 = v2[1];
                this.m12 = v2[2];
                break;
            }
            case 2: {
                this.m20 = v2[0];
                this.m21 = v2[1];
                this.m22 = v2[2];
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f6"));
            }
        }
    }

    public final void setColumn(int column, float x2, float y2, float z2) {
        switch (column) {
            case 0: {
                this.m00 = x2;
                this.m10 = y2;
                this.m20 = z2;
                break;
            }
            case 1: {
                this.m01 = x2;
                this.m11 = y2;
                this.m21 = z2;
                break;
            }
            case 2: {
                this.m02 = x2;
                this.m12 = y2;
                this.m22 = z2;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
            }
        }
    }

    public final void setColumn(int column, Vector3f v2) {
        switch (column) {
            case 0: {
                this.m00 = v2.x;
                this.m10 = v2.y;
                this.m20 = v2.z;
                break;
            }
            case 1: {
                this.m01 = v2.x;
                this.m11 = v2.y;
                this.m21 = v2.z;
                break;
            }
            case 2: {
                this.m02 = v2.x;
                this.m12 = v2.y;
                this.m22 = v2.z;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
            }
        }
    }

    public final void setColumn(int column, float[] v2) {
        switch (column) {
            case 0: {
                this.m00 = v2[0];
                this.m10 = v2[1];
                this.m20 = v2[2];
                break;
            }
            case 1: {
                this.m01 = v2[0];
                this.m11 = v2[1];
                this.m21 = v2[2];
                break;
            }
            case 2: {
                this.m02 = v2[0];
                this.m12 = v2[1];
                this.m22 = v2[2];
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3f9"));
            }
        }
    }

    public final float getScale() {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        return (float)Matrix3d.max3(tmp_scale);
    }

    public final void add(float scalar) {
        this.m00 += scalar;
        this.m01 += scalar;
        this.m02 += scalar;
        this.m10 += scalar;
        this.m11 += scalar;
        this.m12 += scalar;
        this.m20 += scalar;
        this.m21 += scalar;
        this.m22 += scalar;
    }

    public final void add(float scalar, Matrix3f m1) {
        this.m00 = m1.m00 + scalar;
        this.m01 = m1.m01 + scalar;
        this.m02 = m1.m02 + scalar;
        this.m10 = m1.m10 + scalar;
        this.m11 = m1.m11 + scalar;
        this.m12 = m1.m12 + scalar;
        this.m20 = m1.m20 + scalar;
        this.m21 = m1.m21 + scalar;
        this.m22 = m1.m22 + scalar;
    }

    public final void add(Matrix3f m1, Matrix3f m2) {
        this.m00 = m1.m00 + m2.m00;
        this.m01 = m1.m01 + m2.m01;
        this.m02 = m1.m02 + m2.m02;
        this.m10 = m1.m10 + m2.m10;
        this.m11 = m1.m11 + m2.m11;
        this.m12 = m1.m12 + m2.m12;
        this.m20 = m1.m20 + m2.m20;
        this.m21 = m1.m21 + m2.m21;
        this.m22 = m1.m22 + m2.m22;
    }

    public final void add(Matrix3f m1) {
        this.m00 += m1.m00;
        this.m01 += m1.m01;
        this.m02 += m1.m02;
        this.m10 += m1.m10;
        this.m11 += m1.m11;
        this.m12 += m1.m12;
        this.m20 += m1.m20;
        this.m21 += m1.m21;
        this.m22 += m1.m22;
    }

    public final void sub(Matrix3f m1, Matrix3f m2) {
        this.m00 = m1.m00 - m2.m00;
        this.m01 = m1.m01 - m2.m01;
        this.m02 = m1.m02 - m2.m02;
        this.m10 = m1.m10 - m2.m10;
        this.m11 = m1.m11 - m2.m11;
        this.m12 = m1.m12 - m2.m12;
        this.m20 = m1.m20 - m2.m20;
        this.m21 = m1.m21 - m2.m21;
        this.m22 = m1.m22 - m2.m22;
    }

    public final void sub(Matrix3f m1) {
        this.m00 -= m1.m00;
        this.m01 -= m1.m01;
        this.m02 -= m1.m02;
        this.m10 -= m1.m10;
        this.m11 -= m1.m11;
        this.m12 -= m1.m12;
        this.m20 -= m1.m20;
        this.m21 -= m1.m21;
        this.m22 -= m1.m22;
    }

    public final void transpose() {
        float temp = this.m10;
        this.m10 = this.m01;
        this.m01 = temp;
        temp = this.m20;
        this.m20 = this.m02;
        this.m02 = temp;
        temp = this.m21;
        this.m21 = this.m12;
        this.m12 = temp;
    }

    public final void transpose(Matrix3f m1) {
        if (this != m1) {
            this.m00 = m1.m00;
            this.m01 = m1.m10;
            this.m02 = m1.m20;
            this.m10 = m1.m01;
            this.m11 = m1.m11;
            this.m12 = m1.m21;
            this.m20 = m1.m02;
            this.m21 = m1.m12;
            this.m22 = m1.m22;
        } else {
            this.transpose();
        }
    }

    public final void set(Quat4f q1) {
        this.m00 = 1.0f - 2.0f * q1.y * q1.y - 2.0f * q1.z * q1.z;
        this.m10 = 2.0f * (q1.x * q1.y + q1.w * q1.z);
        this.m20 = 2.0f * (q1.x * q1.z - q1.w * q1.y);
        this.m01 = 2.0f * (q1.x * q1.y - q1.w * q1.z);
        this.m11 = 1.0f - 2.0f * q1.x * q1.x - 2.0f * q1.z * q1.z;
        this.m21 = 2.0f * (q1.y * q1.z + q1.w * q1.x);
        this.m02 = 2.0f * (q1.x * q1.z + q1.w * q1.y);
        this.m12 = 2.0f * (q1.y * q1.z - q1.w * q1.x);
        this.m22 = 1.0f - 2.0f * q1.x * q1.x - 2.0f * q1.y * q1.y;
    }

    public final void set(AxisAngle4f a1) {
        float mag = (float)Math.sqrt(a1.x * a1.x + a1.y * a1.y + a1.z * a1.z);
        if ((double)mag < 1.0E-8) {
            this.m00 = 1.0f;
            this.m01 = 0.0f;
            this.m02 = 0.0f;
            this.m10 = 0.0f;
            this.m11 = 1.0f;
            this.m12 = 0.0f;
            this.m20 = 0.0f;
            this.m21 = 0.0f;
            this.m22 = 1.0f;
        } else {
            mag = 1.0f / mag;
            float ax2 = a1.x * mag;
            float ay2 = a1.y * mag;
            float az2 = a1.z * mag;
            float sinTheta = (float)Math.sin(a1.angle);
            float cosTheta = (float)Math.cos(a1.angle);
            float t2 = 1.0f - cosTheta;
            float xz = ax2 * az2;
            float xy2 = ax2 * ay2;
            float yz2 = ay2 * az2;
            this.m00 = t2 * ax2 * ax2 + cosTheta;
            this.m01 = t2 * xy2 - sinTheta * az2;
            this.m02 = t2 * xz + sinTheta * ay2;
            this.m10 = t2 * xy2 + sinTheta * az2;
            this.m11 = t2 * ay2 * ay2 + cosTheta;
            this.m12 = t2 * yz2 - sinTheta * ax2;
            this.m20 = t2 * xz - sinTheta * ay2;
            this.m21 = t2 * yz2 + sinTheta * ax2;
            this.m22 = t2 * az2 * az2 + cosTheta;
        }
    }

    public final void set(AxisAngle4d a1) {
        double mag = Math.sqrt(a1.x * a1.x + a1.y * a1.y + a1.z * a1.z);
        if (mag < 1.0E-8) {
            this.m00 = 1.0f;
            this.m01 = 0.0f;
            this.m02 = 0.0f;
            this.m10 = 0.0f;
            this.m11 = 1.0f;
            this.m12 = 0.0f;
            this.m20 = 0.0f;
            this.m21 = 0.0f;
            this.m22 = 1.0f;
        } else {
            mag = 1.0 / mag;
            double ax2 = a1.x * mag;
            double ay2 = a1.y * mag;
            double az2 = a1.z * mag;
            double sinTheta = Math.sin(a1.angle);
            double cosTheta = Math.cos(a1.angle);
            double t2 = 1.0 - cosTheta;
            double xz = ax2 * az2;
            double xy2 = ax2 * ay2;
            double yz2 = ay2 * az2;
            this.m00 = (float)(t2 * ax2 * ax2 + cosTheta);
            this.m01 = (float)(t2 * xy2 - sinTheta * az2);
            this.m02 = (float)(t2 * xz + sinTheta * ay2);
            this.m10 = (float)(t2 * xy2 + sinTheta * az2);
            this.m11 = (float)(t2 * ay2 * ay2 + cosTheta);
            this.m12 = (float)(t2 * yz2 - sinTheta * ax2);
            this.m20 = (float)(t2 * xz - sinTheta * ay2);
            this.m21 = (float)(t2 * yz2 + sinTheta * ax2);
            this.m22 = (float)(t2 * az2 * az2 + cosTheta);
        }
    }

    public final void set(Quat4d q1) {
        this.m00 = (float)(1.0 - 2.0 * q1.y * q1.y - 2.0 * q1.z * q1.z);
        this.m10 = (float)(2.0 * (q1.x * q1.y + q1.w * q1.z));
        this.m20 = (float)(2.0 * (q1.x * q1.z - q1.w * q1.y));
        this.m01 = (float)(2.0 * (q1.x * q1.y - q1.w * q1.z));
        this.m11 = (float)(1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.z * q1.z);
        this.m21 = (float)(2.0 * (q1.y * q1.z + q1.w * q1.x));
        this.m02 = (float)(2.0 * (q1.x * q1.z + q1.w * q1.y));
        this.m12 = (float)(2.0 * (q1.y * q1.z - q1.w * q1.x));
        this.m22 = (float)(1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.y * q1.y);
    }

    public final void set(float[] m2) {
        this.m00 = m2[0];
        this.m01 = m2[1];
        this.m02 = m2[2];
        this.m10 = m2[3];
        this.m11 = m2[4];
        this.m12 = m2[5];
        this.m20 = m2[6];
        this.m21 = m2[7];
        this.m22 = m2[8];
    }

    public final void set(Matrix3f m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
    }

    public final void set(Matrix3d m1) {
        this.m00 = (float)m1.m00;
        this.m01 = (float)m1.m01;
        this.m02 = (float)m1.m02;
        this.m10 = (float)m1.m10;
        this.m11 = (float)m1.m11;
        this.m12 = (float)m1.m12;
        this.m20 = (float)m1.m20;
        this.m21 = (float)m1.m21;
        this.m22 = (float)m1.m22;
    }

    public final void invert(Matrix3f m1) {
        this.invertGeneral(m1);
    }

    public final void invert() {
        this.invertGeneral(this);
    }

    private final void invertGeneral(Matrix3f m1) {
        double[] temp = new double[9];
        double[] result = new double[9];
        int[] row_perm = new int[3];
        temp[0] = m1.m00;
        temp[1] = m1.m01;
        temp[2] = m1.m02;
        temp[3] = m1.m10;
        temp[4] = m1.m11;
        temp[5] = m1.m12;
        temp[6] = m1.m20;
        temp[7] = m1.m21;
        temp[8] = m1.m22;
        if (!Matrix3f.luDecomposition(temp, row_perm)) {
            throw new SingularMatrixException(VecMathI18N.getString("Matrix3f12"));
        }
        for (int i2 = 0; i2 < 9; ++i2) {
            result[i2] = 0.0;
        }
        result[0] = 1.0;
        result[4] = 1.0;
        result[8] = 1.0;
        Matrix3f.luBacksubstitution(temp, row_perm, result);
        this.m00 = (float)result[0];
        this.m01 = (float)result[1];
        this.m02 = (float)result[2];
        this.m10 = (float)result[3];
        this.m11 = (float)result[4];
        this.m12 = (float)result[5];
        this.m20 = (float)result[6];
        this.m21 = (float)result[7];
        this.m22 = (float)result[8];
    }

    static boolean luDecomposition(double[] matrix0, int[] row_perm) {
        double[] row_scale = new double[3];
        int ptr = 0;
        int rs2 = 0;
        int i2 = 3;
        while (i2-- != 0) {
            double big2 = 0.0;
            int j2 = 3;
            while (j2-- != 0) {
                double temp = matrix0[ptr++];
                if (!((temp = Math.abs(temp)) > big2)) continue;
                big2 = temp;
            }
            if (big2 == 0.0) {
                return false;
            }
            row_scale[rs2++] = 1.0 / big2;
        }
        int mtx = 0;
        for (int j3 = 0; j3 < 3; ++j3) {
            double temp;
            int p1;
            int k2;
            int p2;
            double sum;
            int target;
            int i3;
            for (i3 = 0; i3 < j3; ++i3) {
                target = mtx + 3 * i3 + j3;
                sum = matrix0[target];
                int k3 = i3;
                int p12 = mtx + 3 * i3;
                p2 = mtx + j3;
                while (k3-- != 0) {
                    sum -= matrix0[p12] * matrix0[p2];
                    ++p12;
                    p2 += 3;
                }
                matrix0[target] = sum;
            }
            double big3 = 0.0;
            int imax = -1;
            for (i3 = j3; i3 < 3; ++i3) {
                double d2;
                target = mtx + 3 * i3 + j3;
                sum = matrix0[target];
                k2 = j3;
                p1 = mtx + 3 * i3;
                p2 = mtx + j3;
                while (k2-- != 0) {
                    sum -= matrix0[p1] * matrix0[p2];
                    ++p1;
                    p2 += 3;
                }
                matrix0[target] = sum;
                temp = row_scale[i3] * Math.abs(sum);
                if (!(d2 >= big3)) continue;
                big3 = temp;
                imax = i3;
            }
            if (imax < 0) {
                throw new RuntimeException(VecMathI18N.getString("Matrix3f13"));
            }
            if (j3 != imax) {
                k2 = 3;
                p1 = mtx + 3 * imax;
                p2 = mtx + 3 * j3;
                while (k2-- != 0) {
                    temp = matrix0[p1];
                    matrix0[p1++] = matrix0[p2];
                    matrix0[p2++] = temp;
                }
                row_scale[imax] = row_scale[j3];
            }
            row_perm[j3] = imax;
            if (matrix0[mtx + 3 * j3 + j3] == 0.0) {
                return false;
            }
            if (j3 == 2) continue;
            temp = 1.0 / matrix0[mtx + 3 * j3 + j3];
            target = mtx + 3 * (j3 + 1) + j3;
            i3 = 2 - j3;
            while (i3-- != 0) {
                int n2 = target;
                matrix0[n2] = matrix0[n2] * temp;
                target += 3;
            }
        }
        return true;
    }

    static void luBacksubstitution(double[] matrix1, int[] row_perm, double[] matrix2) {
        int rp2 = 0;
        for (int k2 = 0; k2 < 3; ++k2) {
            int rv2;
            int cv2 = k2;
            int ii2 = -1;
            for (int i2 = 0; i2 < 3; ++i2) {
                int ip2 = row_perm[rp2 + i2];
                double sum = matrix2[cv2 + 3 * ip2];
                matrix2[cv2 + 3 * ip2] = matrix2[cv2 + 3 * i2];
                if (ii2 >= 0) {
                    rv2 = i2 * 3;
                    for (int j2 = ii2; j2 <= i2 - 1; ++j2) {
                        sum -= matrix1[rv2 + j2] * matrix2[cv2 + 3 * j2];
                    }
                } else if (sum != 0.0) {
                    ii2 = i2;
                }
                matrix2[cv2 + 3 * i2] = sum;
            }
            rv2 = 6;
            int n2 = cv2 + 6;
            matrix2[n2] = matrix2[n2] / matrix1[rv2 + 2];
            matrix2[cv2 + 3] = (matrix2[cv2 + 3] - matrix1[(rv2 -= 3) + 2] * matrix2[cv2 + 6]) / matrix1[rv2 + 1];
            matrix2[cv2 + 0] = (matrix2[cv2 + 0] - matrix1[(rv2 -= 3) + 1] * matrix2[cv2 + 3] - matrix1[rv2 + 2] * matrix2[cv2 + 6]) / matrix1[rv2 + 0];
        }
    }

    public final float determinant() {
        float total = this.m00 * (this.m11 * this.m22 - this.m12 * this.m21) + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22) + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
        return total;
    }

    public final void set(float scale) {
        this.m00 = scale;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = scale;
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = scale;
    }

    public final void rotX(float angle) {
        float sinAngle = (float)Math.sin(angle);
        float cosAngle = (float)Math.cos(angle);
        this.m00 = 1.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = cosAngle;
        this.m12 = -sinAngle;
        this.m20 = 0.0f;
        this.m21 = sinAngle;
        this.m22 = cosAngle;
    }

    public final void rotY(float angle) {
        float cosAngle;
        float sinAngle = (float)Math.sin(angle);
        this.m00 = cosAngle = (float)Math.cos(angle);
        this.m01 = 0.0f;
        this.m02 = sinAngle;
        this.m10 = 0.0f;
        this.m11 = 1.0f;
        this.m12 = 0.0f;
        this.m20 = -sinAngle;
        this.m21 = 0.0f;
        this.m22 = cosAngle;
    }

    public final void rotZ(float angle) {
        float cosAngle;
        float sinAngle = (float)Math.sin(angle);
        this.m00 = cosAngle = (float)Math.cos(angle);
        this.m01 = -sinAngle;
        this.m02 = 0.0f;
        this.m10 = sinAngle;
        this.m11 = cosAngle;
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 1.0f;
    }

    public final void mul(float scalar) {
        this.m00 *= scalar;
        this.m01 *= scalar;
        this.m02 *= scalar;
        this.m10 *= scalar;
        this.m11 *= scalar;
        this.m12 *= scalar;
        this.m20 *= scalar;
        this.m21 *= scalar;
        this.m22 *= scalar;
    }

    public final void mul(float scalar, Matrix3f m1) {
        this.m00 = scalar * m1.m00;
        this.m01 = scalar * m1.m01;
        this.m02 = scalar * m1.m02;
        this.m10 = scalar * m1.m10;
        this.m11 = scalar * m1.m11;
        this.m12 = scalar * m1.m12;
        this.m20 = scalar * m1.m20;
        this.m21 = scalar * m1.m21;
        this.m22 = scalar * m1.m22;
    }

    public final void mul(Matrix3f m1) {
        float m00 = this.m00 * m1.m00 + this.m01 * m1.m10 + this.m02 * m1.m20;
        float m01 = this.m00 * m1.m01 + this.m01 * m1.m11 + this.m02 * m1.m21;
        float m02 = this.m00 * m1.m02 + this.m01 * m1.m12 + this.m02 * m1.m22;
        float m10 = this.m10 * m1.m00 + this.m11 * m1.m10 + this.m12 * m1.m20;
        float m11 = this.m10 * m1.m01 + this.m11 * m1.m11 + this.m12 * m1.m21;
        float m12 = this.m10 * m1.m02 + this.m11 * m1.m12 + this.m12 * m1.m22;
        float m20 = this.m20 * m1.m00 + this.m21 * m1.m10 + this.m22 * m1.m20;
        float m21 = this.m20 * m1.m01 + this.m21 * m1.m11 + this.m22 * m1.m21;
        float m22 = this.m20 * m1.m02 + this.m21 * m1.m12 + this.m22 * m1.m22;
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    public final void mul(Matrix3f m1, Matrix3f m2) {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
            this.m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
            this.m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;
            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
            this.m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
            this.m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;
            this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
            this.m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
            this.m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
        } else {
            float m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
            float m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
            float m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;
            float m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
            float m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
            float m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;
            float m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
            float m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
            float m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
        }
    }

    public final void mulNormalize(Matrix3f m1) {
        double[] tmp = new double[9];
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        tmp[0] = this.m00 * m1.m00 + this.m01 * m1.m10 + this.m02 * m1.m20;
        tmp[1] = this.m00 * m1.m01 + this.m01 * m1.m11 + this.m02 * m1.m21;
        tmp[2] = this.m00 * m1.m02 + this.m01 * m1.m12 + this.m02 * m1.m22;
        tmp[3] = this.m10 * m1.m00 + this.m11 * m1.m10 + this.m12 * m1.m20;
        tmp[4] = this.m10 * m1.m01 + this.m11 * m1.m11 + this.m12 * m1.m21;
        tmp[5] = this.m10 * m1.m02 + this.m11 * m1.m12 + this.m12 * m1.m22;
        tmp[6] = this.m20 * m1.m00 + this.m21 * m1.m10 + this.m22 * m1.m20;
        tmp[7] = this.m20 * m1.m01 + this.m21 * m1.m11 + this.m22 * m1.m21;
        tmp[8] = this.m20 * m1.m02 + this.m21 * m1.m12 + this.m22 * m1.m22;
        Matrix3d.compute_svd(tmp, tmp_scale, tmp_rot);
        this.m00 = (float)tmp_rot[0];
        this.m01 = (float)tmp_rot[1];
        this.m02 = (float)tmp_rot[2];
        this.m10 = (float)tmp_rot[3];
        this.m11 = (float)tmp_rot[4];
        this.m12 = (float)tmp_rot[5];
        this.m20 = (float)tmp_rot[6];
        this.m21 = (float)tmp_rot[7];
        this.m22 = (float)tmp_rot[8];
    }

    public final void mulNormalize(Matrix3f m1, Matrix3f m2) {
        double[] tmp = new double[9];
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        tmp[0] = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
        tmp[1] = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
        tmp[2] = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;
        tmp[3] = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
        tmp[4] = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
        tmp[5] = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;
        tmp[6] = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
        tmp[7] = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
        tmp[8] = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
        Matrix3d.compute_svd(tmp, tmp_scale, tmp_rot);
        this.m00 = (float)tmp_rot[0];
        this.m01 = (float)tmp_rot[1];
        this.m02 = (float)tmp_rot[2];
        this.m10 = (float)tmp_rot[3];
        this.m11 = (float)tmp_rot[4];
        this.m12 = (float)tmp_rot[5];
        this.m20 = (float)tmp_rot[6];
        this.m21 = (float)tmp_rot[7];
        this.m22 = (float)tmp_rot[8];
    }

    public final void mulTransposeBoth(Matrix3f m1, Matrix3f m2) {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m10 * m2.m01 + m1.m20 * m2.m02;
            this.m01 = m1.m00 * m2.m10 + m1.m10 * m2.m11 + m1.m20 * m2.m12;
            this.m02 = m1.m00 * m2.m20 + m1.m10 * m2.m21 + m1.m20 * m2.m22;
            this.m10 = m1.m01 * m2.m00 + m1.m11 * m2.m01 + m1.m21 * m2.m02;
            this.m11 = m1.m01 * m2.m10 + m1.m11 * m2.m11 + m1.m21 * m2.m12;
            this.m12 = m1.m01 * m2.m20 + m1.m11 * m2.m21 + m1.m21 * m2.m22;
            this.m20 = m1.m02 * m2.m00 + m1.m12 * m2.m01 + m1.m22 * m2.m02;
            this.m21 = m1.m02 * m2.m10 + m1.m12 * m2.m11 + m1.m22 * m2.m12;
            this.m22 = m1.m02 * m2.m20 + m1.m12 * m2.m21 + m1.m22 * m2.m22;
        } else {
            float m00 = m1.m00 * m2.m00 + m1.m10 * m2.m01 + m1.m20 * m2.m02;
            float m01 = m1.m00 * m2.m10 + m1.m10 * m2.m11 + m1.m20 * m2.m12;
            float m02 = m1.m00 * m2.m20 + m1.m10 * m2.m21 + m1.m20 * m2.m22;
            float m10 = m1.m01 * m2.m00 + m1.m11 * m2.m01 + m1.m21 * m2.m02;
            float m11 = m1.m01 * m2.m10 + m1.m11 * m2.m11 + m1.m21 * m2.m12;
            float m12 = m1.m01 * m2.m20 + m1.m11 * m2.m21 + m1.m21 * m2.m22;
            float m20 = m1.m02 * m2.m00 + m1.m12 * m2.m01 + m1.m22 * m2.m02;
            float m21 = m1.m02 * m2.m10 + m1.m12 * m2.m11 + m1.m22 * m2.m12;
            float m22 = m1.m02 * m2.m20 + m1.m12 * m2.m21 + m1.m22 * m2.m22;
            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
        }
    }

    public final void mulTransposeRight(Matrix3f m1, Matrix3f m2) {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02;
            this.m01 = m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12;
            this.m02 = m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22;
            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02;
            this.m11 = m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12;
            this.m12 = m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22;
            this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02;
            this.m21 = m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12;
            this.m22 = m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22;
        } else {
            float m00 = m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02;
            float m01 = m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12;
            float m02 = m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22;
            float m10 = m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02;
            float m11 = m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12;
            float m12 = m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22;
            float m20 = m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02;
            float m21 = m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12;
            float m22 = m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22;
            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
        }
    }

    public final void mulTransposeLeft(Matrix3f m1, Matrix3f m2) {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20;
            this.m01 = m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21;
            this.m02 = m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22;
            this.m10 = m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20;
            this.m11 = m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21;
            this.m12 = m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22;
            this.m20 = m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20;
            this.m21 = m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21;
            this.m22 = m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22;
        } else {
            float m00 = m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20;
            float m01 = m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21;
            float m02 = m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22;
            float m10 = m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20;
            float m11 = m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21;
            float m12 = m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22;
            float m20 = m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20;
            float m21 = m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21;
            float m22 = m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22;
            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
        }
    }

    public final void normalize() {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        this.m00 = (float)tmp_rot[0];
        this.m01 = (float)tmp_rot[1];
        this.m02 = (float)tmp_rot[2];
        this.m10 = (float)tmp_rot[3];
        this.m11 = (float)tmp_rot[4];
        this.m12 = (float)tmp_rot[5];
        this.m20 = (float)tmp_rot[6];
        this.m21 = (float)tmp_rot[7];
        this.m22 = (float)tmp_rot[8];
    }

    public final void normalize(Matrix3f m1) {
        double[] tmp = new double[9];
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        tmp[0] = m1.m00;
        tmp[1] = m1.m01;
        tmp[2] = m1.m02;
        tmp[3] = m1.m10;
        tmp[4] = m1.m11;
        tmp[5] = m1.m12;
        tmp[6] = m1.m20;
        tmp[7] = m1.m21;
        tmp[8] = m1.m22;
        Matrix3d.compute_svd(tmp, tmp_scale, tmp_rot);
        this.m00 = (float)tmp_rot[0];
        this.m01 = (float)tmp_rot[1];
        this.m02 = (float)tmp_rot[2];
        this.m10 = (float)tmp_rot[3];
        this.m11 = (float)tmp_rot[4];
        this.m12 = (float)tmp_rot[5];
        this.m20 = (float)tmp_rot[6];
        this.m21 = (float)tmp_rot[7];
        this.m22 = (float)tmp_rot[8];
    }

    public final void normalizeCP() {
        float mag = 1.0f / (float)Math.sqrt(this.m00 * this.m00 + this.m10 * this.m10 + this.m20 * this.m20);
        this.m00 *= mag;
        this.m10 *= mag;
        this.m20 *= mag;
        mag = 1.0f / (float)Math.sqrt(this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21);
        this.m01 *= mag;
        this.m11 *= mag;
        this.m21 *= mag;
        this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
        this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
        this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
    }

    public final void normalizeCP(Matrix3f m1) {
        float mag = 1.0f / (float)Math.sqrt(m1.m00 * m1.m00 + m1.m10 * m1.m10 + m1.m20 * m1.m20);
        this.m00 = m1.m00 * mag;
        this.m10 = m1.m10 * mag;
        this.m20 = m1.m20 * mag;
        mag = 1.0f / (float)Math.sqrt(m1.m01 * m1.m01 + m1.m11 * m1.m11 + m1.m21 * m1.m21);
        this.m01 = m1.m01 * mag;
        this.m11 = m1.m11 * mag;
        this.m21 = m1.m21 * mag;
        this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
        this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
        this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
    }

    public boolean equals(Matrix3f m1) {
        try {
            return this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02 && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12 && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean equals(Object o1) {
        try {
            Matrix3f m2 = (Matrix3f)o1;
            return this.m00 == m2.m00 && this.m01 == m2.m01 && this.m02 == m2.m02 && this.m10 == m2.m10 && this.m11 == m2.m11 && this.m12 == m2.m12 && this.m20 == m2.m20 && this.m21 == m2.m21 && this.m22 == m2.m22;
        }
        catch (ClassCastException e1) {
            return false;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean epsilonEquals(Matrix3f m1, float epsilon) {
        boolean status = true;
        if (Math.abs(this.m00 - m1.m00) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m01 - m1.m01) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m02 - m1.m02) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m10 - m1.m10) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m11 - m1.m11) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m12 - m1.m12) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m20 - m1.m20) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m21 - m1.m21) > epsilon) {
            status = false;
        }
        if (Math.abs(this.m22 - m1.m22) > epsilon) {
            status = false;
        }
        return status;
    }

    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.m00);
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.m01);
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.m02);
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.m10);
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.m11);
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.m12);
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.m20);
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.m21);
        bits = 31L * bits + (long)VecMathUtil.floatToIntBits(this.m22);
        return (int)(bits ^ bits >> 32);
    }

    public final void setZero() {
        this.m00 = 0.0f;
        this.m01 = 0.0f;
        this.m02 = 0.0f;
        this.m10 = 0.0f;
        this.m11 = 0.0f;
        this.m12 = 0.0f;
        this.m20 = 0.0f;
        this.m21 = 0.0f;
        this.m22 = 0.0f;
    }

    public final void negate() {
        this.m00 = -this.m00;
        this.m01 = -this.m01;
        this.m02 = -this.m02;
        this.m10 = -this.m10;
        this.m11 = -this.m11;
        this.m12 = -this.m12;
        this.m20 = -this.m20;
        this.m21 = -this.m21;
        this.m22 = -this.m22;
    }

    public final void negate(Matrix3f m1) {
        this.m00 = -m1.m00;
        this.m01 = -m1.m01;
        this.m02 = -m1.m02;
        this.m10 = -m1.m10;
        this.m11 = -m1.m11;
        this.m12 = -m1.m12;
        this.m20 = -m1.m20;
        this.m21 = -m1.m21;
        this.m22 = -m1.m22;
    }

    public final void transform(Tuple3f t2) {
        float x2 = this.m00 * t2.x + this.m01 * t2.y + this.m02 * t2.z;
        float y2 = this.m10 * t2.x + this.m11 * t2.y + this.m12 * t2.z;
        float z2 = this.m20 * t2.x + this.m21 * t2.y + this.m22 * t2.z;
        t2.set(x2, y2, z2);
    }

    public final void transform(Tuple3f t2, Tuple3f result) {
        float x2 = this.m00 * t2.x + this.m01 * t2.y + this.m02 * t2.z;
        float y2 = this.m10 * t2.x + this.m11 * t2.y + this.m12 * t2.z;
        result.z = this.m20 * t2.x + this.m21 * t2.y + this.m22 * t2.z;
        result.x = x2;
        result.y = y2;
    }

    void getScaleRotate(double[] scales, double[] rot) {
        double[] tmp = new double[]{this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22};
        Matrix3d.compute_svd(tmp, scales, rot);
    }

    public Object clone() {
        Matrix3f m1 = null;
        try {
            m1 = (Matrix3f)super.clone();
        }
        catch (CloneNotSupportedException e2) {
            throw new InternalError();
        }
        return m1;
    }

    public final float getM00() {
        return this.m00;
    }

    public final void setM00(float m00) {
        this.m00 = m00;
    }

    public final float getM01() {
        return this.m01;
    }

    public final void setM01(float m01) {
        this.m01 = m01;
    }

    public final float getM02() {
        return this.m02;
    }

    public final void setM02(float m02) {
        this.m02 = m02;
    }

    public final float getM10() {
        return this.m10;
    }

    public final void setM10(float m10) {
        this.m10 = m10;
    }

    public final float getM11() {
        return this.m11;
    }

    public final void setM11(float m11) {
        this.m11 = m11;
    }

    public final float getM12() {
        return this.m12;
    }

    public final void setM12(float m12) {
        this.m12 = m12;
    }

    public final float getM20() {
        return this.m20;
    }

    public final void setM20(float m20) {
        this.m20 = m20;
    }

    public final float getM21() {
        return this.m21;
    }

    public final void setM21(float m21) {
        this.m21 = m21;
    }

    public final float getM22() {
        return this.m22;
    }

    public final void setM22(float m22) {
        this.m22 = m22;
    }
}

