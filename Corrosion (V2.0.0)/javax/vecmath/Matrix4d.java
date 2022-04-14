/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Tuple4d;
import javax.vecmath.Tuple4f;
import javax.vecmath.VecMathI18N;
import javax.vecmath.VecMathUtil;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4d;

public class Matrix4d
implements Serializable,
Cloneable {
    static final long serialVersionUID = 8223903484171633710L;
    public double m00;
    public double m01;
    public double m02;
    public double m03;
    public double m10;
    public double m11;
    public double m12;
    public double m13;
    public double m20;
    public double m21;
    public double m22;
    public double m23;
    public double m30;
    public double m31;
    public double m32;
    public double m33;
    private static final double EPS = 1.0E-10;

    public Matrix4d(double m00, double m01, double m02, double m03, double m10, double m11, double m12, double m13, double m20, double m21, double m22, double m23, double m30, double m31, double m32, double m33) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    public Matrix4d(double[] v2) {
        this.m00 = v2[0];
        this.m01 = v2[1];
        this.m02 = v2[2];
        this.m03 = v2[3];
        this.m10 = v2[4];
        this.m11 = v2[5];
        this.m12 = v2[6];
        this.m13 = v2[7];
        this.m20 = v2[8];
        this.m21 = v2[9];
        this.m22 = v2[10];
        this.m23 = v2[11];
        this.m30 = v2[12];
        this.m31 = v2[13];
        this.m32 = v2[14];
        this.m33 = v2[15];
    }

    public Matrix4d(Quat4d q1, Vector3d t1, double s2) {
        this.m00 = s2 * (1.0 - 2.0 * q1.y * q1.y - 2.0 * q1.z * q1.z);
        this.m10 = s2 * (2.0 * (q1.x * q1.y + q1.w * q1.z));
        this.m20 = s2 * (2.0 * (q1.x * q1.z - q1.w * q1.y));
        this.m01 = s2 * (2.0 * (q1.x * q1.y - q1.w * q1.z));
        this.m11 = s2 * (1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.z * q1.z);
        this.m21 = s2 * (2.0 * (q1.y * q1.z + q1.w * q1.x));
        this.m02 = s2 * (2.0 * (q1.x * q1.z + q1.w * q1.y));
        this.m12 = s2 * (2.0 * (q1.y * q1.z - q1.w * q1.x));
        this.m22 = s2 * (1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.y * q1.y);
        this.m03 = t1.x;
        this.m13 = t1.y;
        this.m23 = t1.z;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public Matrix4d(Quat4f q1, Vector3d t1, double s2) {
        this.m00 = s2 * (1.0 - 2.0 * (double)q1.y * (double)q1.y - 2.0 * (double)q1.z * (double)q1.z);
        this.m10 = s2 * (2.0 * (double)(q1.x * q1.y + q1.w * q1.z));
        this.m20 = s2 * (2.0 * (double)(q1.x * q1.z - q1.w * q1.y));
        this.m01 = s2 * (2.0 * (double)(q1.x * q1.y - q1.w * q1.z));
        this.m11 = s2 * (1.0 - 2.0 * (double)q1.x * (double)q1.x - 2.0 * (double)q1.z * (double)q1.z);
        this.m21 = s2 * (2.0 * (double)(q1.y * q1.z + q1.w * q1.x));
        this.m02 = s2 * (2.0 * (double)(q1.x * q1.z + q1.w * q1.y));
        this.m12 = s2 * (2.0 * (double)(q1.y * q1.z - q1.w * q1.x));
        this.m22 = s2 * (1.0 - 2.0 * (double)q1.x * (double)q1.x - 2.0 * (double)q1.y * (double)q1.y);
        this.m03 = t1.x;
        this.m13 = t1.y;
        this.m23 = t1.z;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public Matrix4d(Matrix4d m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m03 = m1.m03;
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m13 = m1.m13;
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
        this.m23 = m1.m23;
        this.m30 = m1.m30;
        this.m31 = m1.m31;
        this.m32 = m1.m32;
        this.m33 = m1.m33;
    }

    public Matrix4d(Matrix4f m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m03 = m1.m03;
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m13 = m1.m13;
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
        this.m23 = m1.m23;
        this.m30 = m1.m30;
        this.m31 = m1.m31;
        this.m32 = m1.m32;
        this.m33 = m1.m33;
    }

    public Matrix4d(Matrix3f m1, Vector3d t1, double s2) {
        this.m00 = (double)m1.m00 * s2;
        this.m01 = (double)m1.m01 * s2;
        this.m02 = (double)m1.m02 * s2;
        this.m03 = t1.x;
        this.m10 = (double)m1.m10 * s2;
        this.m11 = (double)m1.m11 * s2;
        this.m12 = (double)m1.m12 * s2;
        this.m13 = t1.y;
        this.m20 = (double)m1.m20 * s2;
        this.m21 = (double)m1.m21 * s2;
        this.m22 = (double)m1.m22 * s2;
        this.m23 = t1.z;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public Matrix4d(Matrix3d m1, Vector3d t1, double s2) {
        this.m00 = m1.m00 * s2;
        this.m01 = m1.m01 * s2;
        this.m02 = m1.m02 * s2;
        this.m03 = t1.x;
        this.m10 = m1.m10 * s2;
        this.m11 = m1.m11 * s2;
        this.m12 = m1.m12 * s2;
        this.m13 = t1.y;
        this.m20 = m1.m20 * s2;
        this.m21 = m1.m21 * s2;
        this.m22 = m1.m22 * s2;
        this.m23 = t1.z;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public Matrix4d() {
        this.m00 = 0.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m03 = 0.0;
        this.m10 = 0.0;
        this.m11 = 0.0;
        this.m12 = 0.0;
        this.m13 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 0.0;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 0.0;
    }

    public String toString() {
        return this.m00 + ", " + this.m01 + ", " + this.m02 + ", " + this.m03 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + ", " + this.m13 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + ", " + this.m23 + "\n" + this.m30 + ", " + this.m31 + ", " + this.m32 + ", " + this.m33 + "\n";
    }

    public final void setIdentity() {
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m03 = 0.0;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        this.m13 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void setElement(int row, int column, double value) {
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
                    case 3: {
                        this.m03 = value;
                        break block0;
                    }
                }
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
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
                    case 3: {
                        this.m13 = value;
                        break block0;
                    }
                }
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
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
                    case 3: {
                        this.m23 = value;
                        break block0;
                    }
                }
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
            }
            case 3: {
                switch (column) {
                    case 0: {
                        this.m30 = value;
                        break block0;
                    }
                    case 1: {
                        this.m31 = value;
                        break block0;
                    }
                    case 2: {
                        this.m32 = value;
                        break block0;
                    }
                    case 3: {
                        this.m33 = value;
                        break block0;
                    }
                }
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d0"));
            }
        }
    }

    public final double getElement(int row, int column) {
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
                    case 3: {
                        return this.m03;
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
                    case 3: {
                        return this.m13;
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
                    case 3: {
                        return this.m23;
                    }
                }
                break;
            }
            case 3: {
                switch (column) {
                    case 0: {
                        return this.m30;
                    }
                    case 1: {
                        return this.m31;
                    }
                    case 2: {
                        return this.m32;
                    }
                    case 3: {
                        return this.m33;
                    }
                }
                break;
            }
        }
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d1"));
    }

    public final void getRow(int row, Vector4d v2) {
        if (row == 0) {
            v2.x = this.m00;
            v2.y = this.m01;
            v2.z = this.m02;
            v2.w = this.m03;
        } else if (row == 1) {
            v2.x = this.m10;
            v2.y = this.m11;
            v2.z = this.m12;
            v2.w = this.m13;
        } else if (row == 2) {
            v2.x = this.m20;
            v2.y = this.m21;
            v2.z = this.m22;
            v2.w = this.m23;
        } else if (row == 3) {
            v2.x = this.m30;
            v2.y = this.m31;
            v2.z = this.m32;
            v2.w = this.m33;
        } else {
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d2"));
        }
    }

    public final void getRow(int row, double[] v2) {
        if (row == 0) {
            v2[0] = this.m00;
            v2[1] = this.m01;
            v2[2] = this.m02;
            v2[3] = this.m03;
        } else if (row == 1) {
            v2[0] = this.m10;
            v2[1] = this.m11;
            v2[2] = this.m12;
            v2[3] = this.m13;
        } else if (row == 2) {
            v2[0] = this.m20;
            v2[1] = this.m21;
            v2[2] = this.m22;
            v2[3] = this.m23;
        } else if (row == 3) {
            v2[0] = this.m30;
            v2[1] = this.m31;
            v2[2] = this.m32;
            v2[3] = this.m33;
        } else {
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d2"));
        }
    }

    public final void getColumn(int column, Vector4d v2) {
        if (column == 0) {
            v2.x = this.m00;
            v2.y = this.m10;
            v2.z = this.m20;
            v2.w = this.m30;
        } else if (column == 1) {
            v2.x = this.m01;
            v2.y = this.m11;
            v2.z = this.m21;
            v2.w = this.m31;
        } else if (column == 2) {
            v2.x = this.m02;
            v2.y = this.m12;
            v2.z = this.m22;
            v2.w = this.m32;
        } else if (column == 3) {
            v2.x = this.m03;
            v2.y = this.m13;
            v2.z = this.m23;
            v2.w = this.m33;
        } else {
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d3"));
        }
    }

    public final void getColumn(int column, double[] v2) {
        if (column == 0) {
            v2[0] = this.m00;
            v2[1] = this.m10;
            v2[2] = this.m20;
            v2[3] = this.m30;
        } else if (column == 1) {
            v2[0] = this.m01;
            v2[1] = this.m11;
            v2[2] = this.m21;
            v2[3] = this.m31;
        } else if (column == 2) {
            v2[0] = this.m02;
            v2[1] = this.m12;
            v2[2] = this.m22;
            v2[3] = this.m32;
        } else if (column == 3) {
            v2[0] = this.m03;
            v2[1] = this.m13;
            v2[2] = this.m23;
            v2[3] = this.m33;
        } else {
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d3"));
        }
    }

    public final void get(Matrix3d m1) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        m1.m00 = tmp_rot[0];
        m1.m01 = tmp_rot[1];
        m1.m02 = tmp_rot[2];
        m1.m10 = tmp_rot[3];
        m1.m11 = tmp_rot[4];
        m1.m12 = tmp_rot[5];
        m1.m20 = tmp_rot[6];
        m1.m21 = tmp_rot[7];
        m1.m22 = tmp_rot[8];
    }

    public final void get(Matrix3f m1) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        m1.m00 = (float)tmp_rot[0];
        m1.m01 = (float)tmp_rot[1];
        m1.m02 = (float)tmp_rot[2];
        m1.m10 = (float)tmp_rot[3];
        m1.m11 = (float)tmp_rot[4];
        m1.m12 = (float)tmp_rot[5];
        m1.m20 = (float)tmp_rot[6];
        m1.m21 = (float)tmp_rot[7];
        m1.m22 = (float)tmp_rot[8];
    }

    public final double get(Matrix3d m1, Vector3d t1) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        m1.m00 = tmp_rot[0];
        m1.m01 = tmp_rot[1];
        m1.m02 = tmp_rot[2];
        m1.m10 = tmp_rot[3];
        m1.m11 = tmp_rot[4];
        m1.m12 = tmp_rot[5];
        m1.m20 = tmp_rot[6];
        m1.m21 = tmp_rot[7];
        m1.m22 = tmp_rot[8];
        t1.x = this.m03;
        t1.y = this.m13;
        t1.z = this.m23;
        return Matrix3d.max3(tmp_scale);
    }

    public final double get(Matrix3f m1, Vector3d t1) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        m1.m00 = (float)tmp_rot[0];
        m1.m01 = (float)tmp_rot[1];
        m1.m02 = (float)tmp_rot[2];
        m1.m10 = (float)tmp_rot[3];
        m1.m11 = (float)tmp_rot[4];
        m1.m12 = (float)tmp_rot[5];
        m1.m20 = (float)tmp_rot[6];
        m1.m21 = (float)tmp_rot[7];
        m1.m22 = (float)tmp_rot[8];
        t1.x = this.m03;
        t1.y = this.m13;
        t1.z = this.m23;
        return Matrix3d.max3(tmp_scale);
    }

    public final void get(Quat4f q1) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        double ww2 = 0.25 * (1.0 + tmp_rot[0] + tmp_rot[4] + tmp_rot[8]);
        if (!((ww2 < 0.0 ? -ww2 : ww2) < 1.0E-30)) {
            q1.w = (float)Math.sqrt(ww2);
            ww2 = 0.25 / (double)q1.w;
            q1.x = (float)((tmp_rot[7] - tmp_rot[5]) * ww2);
            q1.y = (float)((tmp_rot[2] - tmp_rot[6]) * ww2);
            q1.z = (float)((tmp_rot[3] - tmp_rot[1]) * ww2);
            return;
        }
        q1.w = 0.0f;
        ww2 = -0.5 * (tmp_rot[4] + tmp_rot[8]);
        if (!((ww2 < 0.0 ? -ww2 : ww2) < 1.0E-30)) {
            q1.x = (float)Math.sqrt(ww2);
            ww2 = 0.5 / (double)q1.x;
            q1.y = (float)(tmp_rot[3] * ww2);
            q1.z = (float)(tmp_rot[6] * ww2);
            return;
        }
        q1.x = 0.0f;
        ww2 = 0.5 * (1.0 - tmp_rot[8]);
        if (!((ww2 < 0.0 ? -ww2 : ww2) < 1.0E-30)) {
            q1.y = (float)Math.sqrt(ww2);
            q1.z = (float)(tmp_rot[7] / (2.0 * (double)q1.y));
            return;
        }
        q1.y = 0.0f;
        q1.z = 1.0f;
    }

    public final void get(Quat4d q1) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        double ww2 = 0.25 * (1.0 + tmp_rot[0] + tmp_rot[4] + tmp_rot[8]);
        if (!((ww2 < 0.0 ? -ww2 : ww2) < 1.0E-30)) {
            q1.w = Math.sqrt(ww2);
            ww2 = 0.25 / q1.w;
            q1.x = (tmp_rot[7] - tmp_rot[5]) * ww2;
            q1.y = (tmp_rot[2] - tmp_rot[6]) * ww2;
            q1.z = (tmp_rot[3] - tmp_rot[1]) * ww2;
            return;
        }
        q1.w = 0.0;
        ww2 = -0.5 * (tmp_rot[4] + tmp_rot[8]);
        if (!((ww2 < 0.0 ? -ww2 : ww2) < 1.0E-30)) {
            q1.x = Math.sqrt(ww2);
            ww2 = 0.5 / q1.x;
            q1.y = tmp_rot[3] * ww2;
            q1.z = tmp_rot[6] * ww2;
            return;
        }
        q1.x = 0.0;
        ww2 = 0.5 * (1.0 - tmp_rot[8]);
        if (!((ww2 < 0.0 ? -ww2 : ww2) < 1.0E-30)) {
            q1.y = Math.sqrt(ww2);
            q1.z = tmp_rot[7] / (2.0 * q1.y);
            return;
        }
        q1.y = 0.0;
        q1.z = 1.0;
    }

    public final void get(Vector3d trans) {
        trans.x = this.m03;
        trans.y = this.m13;
        trans.z = this.m23;
    }

    public final void getRotationScale(Matrix3f m1) {
        m1.m00 = (float)this.m00;
        m1.m01 = (float)this.m01;
        m1.m02 = (float)this.m02;
        m1.m10 = (float)this.m10;
        m1.m11 = (float)this.m11;
        m1.m12 = (float)this.m12;
        m1.m20 = (float)this.m20;
        m1.m21 = (float)this.m21;
        m1.m22 = (float)this.m22;
    }

    public final void getRotationScale(Matrix3d m1) {
        m1.m00 = this.m00;
        m1.m01 = this.m01;
        m1.m02 = this.m02;
        m1.m10 = this.m10;
        m1.m11 = this.m11;
        m1.m12 = this.m12;
        m1.m20 = this.m20;
        m1.m21 = this.m21;
        m1.m22 = this.m22;
    }

    public final double getScale() {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        return Matrix3d.max3(tmp_scale);
    }

    public final void setRotationScale(Matrix3d m1) {
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

    public final void setRotationScale(Matrix3f m1) {
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

    public final void setScale(double scale) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        this.m00 = tmp_rot[0] * scale;
        this.m01 = tmp_rot[1] * scale;
        this.m02 = tmp_rot[2] * scale;
        this.m10 = tmp_rot[3] * scale;
        this.m11 = tmp_rot[4] * scale;
        this.m12 = tmp_rot[5] * scale;
        this.m20 = tmp_rot[6] * scale;
        this.m21 = tmp_rot[7] * scale;
        this.m22 = tmp_rot[8] * scale;
    }

    public final void setRow(int row, double x2, double y2, double z2, double w2) {
        switch (row) {
            case 0: {
                this.m00 = x2;
                this.m01 = y2;
                this.m02 = z2;
                this.m03 = w2;
                break;
            }
            case 1: {
                this.m10 = x2;
                this.m11 = y2;
                this.m12 = z2;
                this.m13 = w2;
                break;
            }
            case 2: {
                this.m20 = x2;
                this.m21 = y2;
                this.m22 = z2;
                this.m23 = w2;
                break;
            }
            case 3: {
                this.m30 = x2;
                this.m31 = y2;
                this.m32 = z2;
                this.m33 = w2;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d4"));
            }
        }
    }

    public final void setRow(int row, Vector4d v2) {
        switch (row) {
            case 0: {
                this.m00 = v2.x;
                this.m01 = v2.y;
                this.m02 = v2.z;
                this.m03 = v2.w;
                break;
            }
            case 1: {
                this.m10 = v2.x;
                this.m11 = v2.y;
                this.m12 = v2.z;
                this.m13 = v2.w;
                break;
            }
            case 2: {
                this.m20 = v2.x;
                this.m21 = v2.y;
                this.m22 = v2.z;
                this.m23 = v2.w;
                break;
            }
            case 3: {
                this.m30 = v2.x;
                this.m31 = v2.y;
                this.m32 = v2.z;
                this.m33 = v2.w;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d4"));
            }
        }
    }

    public final void setRow(int row, double[] v2) {
        switch (row) {
            case 0: {
                this.m00 = v2[0];
                this.m01 = v2[1];
                this.m02 = v2[2];
                this.m03 = v2[3];
                break;
            }
            case 1: {
                this.m10 = v2[0];
                this.m11 = v2[1];
                this.m12 = v2[2];
                this.m13 = v2[3];
                break;
            }
            case 2: {
                this.m20 = v2[0];
                this.m21 = v2[1];
                this.m22 = v2[2];
                this.m23 = v2[3];
                break;
            }
            case 3: {
                this.m30 = v2[0];
                this.m31 = v2[1];
                this.m32 = v2[2];
                this.m33 = v2[3];
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d4"));
            }
        }
    }

    public final void setColumn(int column, double x2, double y2, double z2, double w2) {
        switch (column) {
            case 0: {
                this.m00 = x2;
                this.m10 = y2;
                this.m20 = z2;
                this.m30 = w2;
                break;
            }
            case 1: {
                this.m01 = x2;
                this.m11 = y2;
                this.m21 = z2;
                this.m31 = w2;
                break;
            }
            case 2: {
                this.m02 = x2;
                this.m12 = y2;
                this.m22 = z2;
                this.m32 = w2;
                break;
            }
            case 3: {
                this.m03 = x2;
                this.m13 = y2;
                this.m23 = z2;
                this.m33 = w2;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d7"));
            }
        }
    }

    public final void setColumn(int column, Vector4d v2) {
        switch (column) {
            case 0: {
                this.m00 = v2.x;
                this.m10 = v2.y;
                this.m20 = v2.z;
                this.m30 = v2.w;
                break;
            }
            case 1: {
                this.m01 = v2.x;
                this.m11 = v2.y;
                this.m21 = v2.z;
                this.m31 = v2.w;
                break;
            }
            case 2: {
                this.m02 = v2.x;
                this.m12 = v2.y;
                this.m22 = v2.z;
                this.m32 = v2.w;
                break;
            }
            case 3: {
                this.m03 = v2.x;
                this.m13 = v2.y;
                this.m23 = v2.z;
                this.m33 = v2.w;
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d7"));
            }
        }
    }

    public final void setColumn(int column, double[] v2) {
        switch (column) {
            case 0: {
                this.m00 = v2[0];
                this.m10 = v2[1];
                this.m20 = v2[2];
                this.m30 = v2[3];
                break;
            }
            case 1: {
                this.m01 = v2[0];
                this.m11 = v2[1];
                this.m21 = v2[2];
                this.m31 = v2[3];
                break;
            }
            case 2: {
                this.m02 = v2[0];
                this.m12 = v2[1];
                this.m22 = v2[2];
                this.m32 = v2[3];
                break;
            }
            case 3: {
                this.m03 = v2[0];
                this.m13 = v2[1];
                this.m23 = v2[2];
                this.m33 = v2[3];
                break;
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix4d7"));
            }
        }
    }

    public final void add(double scalar) {
        this.m00 += scalar;
        this.m01 += scalar;
        this.m02 += scalar;
        this.m03 += scalar;
        this.m10 += scalar;
        this.m11 += scalar;
        this.m12 += scalar;
        this.m13 += scalar;
        this.m20 += scalar;
        this.m21 += scalar;
        this.m22 += scalar;
        this.m23 += scalar;
        this.m30 += scalar;
        this.m31 += scalar;
        this.m32 += scalar;
        this.m33 += scalar;
    }

    public final void add(double scalar, Matrix4d m1) {
        this.m00 = m1.m00 + scalar;
        this.m01 = m1.m01 + scalar;
        this.m02 = m1.m02 + scalar;
        this.m03 = m1.m03 + scalar;
        this.m10 = m1.m10 + scalar;
        this.m11 = m1.m11 + scalar;
        this.m12 = m1.m12 + scalar;
        this.m13 = m1.m13 + scalar;
        this.m20 = m1.m20 + scalar;
        this.m21 = m1.m21 + scalar;
        this.m22 = m1.m22 + scalar;
        this.m23 = m1.m23 + scalar;
        this.m30 = m1.m30 + scalar;
        this.m31 = m1.m31 + scalar;
        this.m32 = m1.m32 + scalar;
        this.m33 = m1.m33 + scalar;
    }

    public final void add(Matrix4d m1, Matrix4d m2) {
        this.m00 = m1.m00 + m2.m00;
        this.m01 = m1.m01 + m2.m01;
        this.m02 = m1.m02 + m2.m02;
        this.m03 = m1.m03 + m2.m03;
        this.m10 = m1.m10 + m2.m10;
        this.m11 = m1.m11 + m2.m11;
        this.m12 = m1.m12 + m2.m12;
        this.m13 = m1.m13 + m2.m13;
        this.m20 = m1.m20 + m2.m20;
        this.m21 = m1.m21 + m2.m21;
        this.m22 = m1.m22 + m2.m22;
        this.m23 = m1.m23 + m2.m23;
        this.m30 = m1.m30 + m2.m30;
        this.m31 = m1.m31 + m2.m31;
        this.m32 = m1.m32 + m2.m32;
        this.m33 = m1.m33 + m2.m33;
    }

    public final void add(Matrix4d m1) {
        this.m00 += m1.m00;
        this.m01 += m1.m01;
        this.m02 += m1.m02;
        this.m03 += m1.m03;
        this.m10 += m1.m10;
        this.m11 += m1.m11;
        this.m12 += m1.m12;
        this.m13 += m1.m13;
        this.m20 += m1.m20;
        this.m21 += m1.m21;
        this.m22 += m1.m22;
        this.m23 += m1.m23;
        this.m30 += m1.m30;
        this.m31 += m1.m31;
        this.m32 += m1.m32;
        this.m33 += m1.m33;
    }

    public final void sub(Matrix4d m1, Matrix4d m2) {
        this.m00 = m1.m00 - m2.m00;
        this.m01 = m1.m01 - m2.m01;
        this.m02 = m1.m02 - m2.m02;
        this.m03 = m1.m03 - m2.m03;
        this.m10 = m1.m10 - m2.m10;
        this.m11 = m1.m11 - m2.m11;
        this.m12 = m1.m12 - m2.m12;
        this.m13 = m1.m13 - m2.m13;
        this.m20 = m1.m20 - m2.m20;
        this.m21 = m1.m21 - m2.m21;
        this.m22 = m1.m22 - m2.m22;
        this.m23 = m1.m23 - m2.m23;
        this.m30 = m1.m30 - m2.m30;
        this.m31 = m1.m31 - m2.m31;
        this.m32 = m1.m32 - m2.m32;
        this.m33 = m1.m33 - m2.m33;
    }

    public final void sub(Matrix4d m1) {
        this.m00 -= m1.m00;
        this.m01 -= m1.m01;
        this.m02 -= m1.m02;
        this.m03 -= m1.m03;
        this.m10 -= m1.m10;
        this.m11 -= m1.m11;
        this.m12 -= m1.m12;
        this.m13 -= m1.m13;
        this.m20 -= m1.m20;
        this.m21 -= m1.m21;
        this.m22 -= m1.m22;
        this.m23 -= m1.m23;
        this.m30 -= m1.m30;
        this.m31 -= m1.m31;
        this.m32 -= m1.m32;
        this.m33 -= m1.m33;
    }

    public final void transpose() {
        double temp = this.m10;
        this.m10 = this.m01;
        this.m01 = temp;
        temp = this.m20;
        this.m20 = this.m02;
        this.m02 = temp;
        temp = this.m30;
        this.m30 = this.m03;
        this.m03 = temp;
        temp = this.m21;
        this.m21 = this.m12;
        this.m12 = temp;
        temp = this.m31;
        this.m31 = this.m13;
        this.m13 = temp;
        temp = this.m32;
        this.m32 = this.m23;
        this.m23 = temp;
    }

    public final void transpose(Matrix4d m1) {
        if (this != m1) {
            this.m00 = m1.m00;
            this.m01 = m1.m10;
            this.m02 = m1.m20;
            this.m03 = m1.m30;
            this.m10 = m1.m01;
            this.m11 = m1.m11;
            this.m12 = m1.m21;
            this.m13 = m1.m31;
            this.m20 = m1.m02;
            this.m21 = m1.m12;
            this.m22 = m1.m22;
            this.m23 = m1.m32;
            this.m30 = m1.m03;
            this.m31 = m1.m13;
            this.m32 = m1.m23;
            this.m33 = m1.m33;
        } else {
            this.transpose();
        }
    }

    public final void set(double[] m2) {
        this.m00 = m2[0];
        this.m01 = m2[1];
        this.m02 = m2[2];
        this.m03 = m2[3];
        this.m10 = m2[4];
        this.m11 = m2[5];
        this.m12 = m2[6];
        this.m13 = m2[7];
        this.m20 = m2[8];
        this.m21 = m2[9];
        this.m22 = m2[10];
        this.m23 = m2[11];
        this.m30 = m2[12];
        this.m31 = m2[13];
        this.m32 = m2[14];
        this.m33 = m2[15];
    }

    public final void set(Matrix3f m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m03 = 0.0;
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m13 = 0.0;
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(Matrix3d m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m03 = 0.0;
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m13 = 0.0;
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(Quat4d q1) {
        this.m00 = 1.0 - 2.0 * q1.y * q1.y - 2.0 * q1.z * q1.z;
        this.m10 = 2.0 * (q1.x * q1.y + q1.w * q1.z);
        this.m20 = 2.0 * (q1.x * q1.z - q1.w * q1.y);
        this.m01 = 2.0 * (q1.x * q1.y - q1.w * q1.z);
        this.m11 = 1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.z * q1.z;
        this.m21 = 2.0 * (q1.y * q1.z + q1.w * q1.x);
        this.m02 = 2.0 * (q1.x * q1.z + q1.w * q1.y);
        this.m12 = 2.0 * (q1.y * q1.z - q1.w * q1.x);
        this.m22 = 1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.y * q1.y;
        this.m03 = 0.0;
        this.m13 = 0.0;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(AxisAngle4d a1) {
        double mag = Math.sqrt(a1.x * a1.x + a1.y * a1.y + a1.z * a1.z);
        if (mag < 1.0E-10) {
            this.m00 = 1.0;
            this.m01 = 0.0;
            this.m02 = 0.0;
            this.m10 = 0.0;
            this.m11 = 1.0;
            this.m12 = 0.0;
            this.m20 = 0.0;
            this.m21 = 0.0;
            this.m22 = 1.0;
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
        this.m03 = 0.0;
        this.m13 = 0.0;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(Quat4f q1) {
        this.m00 = 1.0 - 2.0 * (double)q1.y * (double)q1.y - 2.0 * (double)q1.z * (double)q1.z;
        this.m10 = 2.0 * (double)(q1.x * q1.y + q1.w * q1.z);
        this.m20 = 2.0 * (double)(q1.x * q1.z - q1.w * q1.y);
        this.m01 = 2.0 * (double)(q1.x * q1.y - q1.w * q1.z);
        this.m11 = 1.0 - 2.0 * (double)q1.x * (double)q1.x - 2.0 * (double)q1.z * (double)q1.z;
        this.m21 = 2.0 * (double)(q1.y * q1.z + q1.w * q1.x);
        this.m02 = 2.0 * (double)(q1.x * q1.z + q1.w * q1.y);
        this.m12 = 2.0 * (double)(q1.y * q1.z - q1.w * q1.x);
        this.m22 = 1.0 - 2.0 * (double)q1.x * (double)q1.x - 2.0 * (double)q1.y * (double)q1.y;
        this.m03 = 0.0;
        this.m13 = 0.0;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(AxisAngle4f a1) {
        double mag = Math.sqrt(a1.x * a1.x + a1.y * a1.y + a1.z * a1.z);
        if (mag < 1.0E-10) {
            this.m00 = 1.0;
            this.m01 = 0.0;
            this.m02 = 0.0;
            this.m10 = 0.0;
            this.m11 = 1.0;
            this.m12 = 0.0;
            this.m20 = 0.0;
            this.m21 = 0.0;
            this.m22 = 1.0;
        } else {
            mag = 1.0 / mag;
            double ax2 = (double)a1.x * mag;
            double ay2 = (double)a1.y * mag;
            double az2 = (double)a1.z * mag;
            double sinTheta = Math.sin(a1.angle);
            double cosTheta = Math.cos(a1.angle);
            double t2 = 1.0 - cosTheta;
            double xz = ax2 * az2;
            double xy2 = ax2 * ay2;
            double yz2 = ay2 * az2;
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
        this.m03 = 0.0;
        this.m13 = 0.0;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(Quat4d q1, Vector3d t1, double s2) {
        this.m00 = s2 * (1.0 - 2.0 * q1.y * q1.y - 2.0 * q1.z * q1.z);
        this.m10 = s2 * (2.0 * (q1.x * q1.y + q1.w * q1.z));
        this.m20 = s2 * (2.0 * (q1.x * q1.z - q1.w * q1.y));
        this.m01 = s2 * (2.0 * (q1.x * q1.y - q1.w * q1.z));
        this.m11 = s2 * (1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.z * q1.z);
        this.m21 = s2 * (2.0 * (q1.y * q1.z + q1.w * q1.x));
        this.m02 = s2 * (2.0 * (q1.x * q1.z + q1.w * q1.y));
        this.m12 = s2 * (2.0 * (q1.y * q1.z - q1.w * q1.x));
        this.m22 = s2 * (1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.y * q1.y);
        this.m03 = t1.x;
        this.m13 = t1.y;
        this.m23 = t1.z;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(Quat4f q1, Vector3d t1, double s2) {
        this.m00 = s2 * (1.0 - 2.0 * (double)q1.y * (double)q1.y - 2.0 * (double)q1.z * (double)q1.z);
        this.m10 = s2 * (2.0 * (double)(q1.x * q1.y + q1.w * q1.z));
        this.m20 = s2 * (2.0 * (double)(q1.x * q1.z - q1.w * q1.y));
        this.m01 = s2 * (2.0 * (double)(q1.x * q1.y - q1.w * q1.z));
        this.m11 = s2 * (1.0 - 2.0 * (double)q1.x * (double)q1.x - 2.0 * (double)q1.z * (double)q1.z);
        this.m21 = s2 * (2.0 * (double)(q1.y * q1.z + q1.w * q1.x));
        this.m02 = s2 * (2.0 * (double)(q1.x * q1.z + q1.w * q1.y));
        this.m12 = s2 * (2.0 * (double)(q1.y * q1.z - q1.w * q1.x));
        this.m22 = s2 * (1.0 - 2.0 * (double)q1.x * (double)q1.x - 2.0 * (double)q1.y * (double)q1.y);
        this.m03 = t1.x;
        this.m13 = t1.y;
        this.m23 = t1.z;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(Quat4f q1, Vector3f t1, float s2) {
        this.m00 = (double)s2 * (1.0 - 2.0 * (double)q1.y * (double)q1.y - 2.0 * (double)q1.z * (double)q1.z);
        this.m10 = (double)s2 * (2.0 * (double)(q1.x * q1.y + q1.w * q1.z));
        this.m20 = (double)s2 * (2.0 * (double)(q1.x * q1.z - q1.w * q1.y));
        this.m01 = (double)s2 * (2.0 * (double)(q1.x * q1.y - q1.w * q1.z));
        this.m11 = (double)s2 * (1.0 - 2.0 * (double)q1.x * (double)q1.x - 2.0 * (double)q1.z * (double)q1.z);
        this.m21 = (double)s2 * (2.0 * (double)(q1.y * q1.z + q1.w * q1.x));
        this.m02 = (double)s2 * (2.0 * (double)(q1.x * q1.z + q1.w * q1.y));
        this.m12 = (double)s2 * (2.0 * (double)(q1.y * q1.z - q1.w * q1.x));
        this.m22 = (double)s2 * (1.0 - 2.0 * (double)q1.x * (double)q1.x - 2.0 * (double)q1.y * (double)q1.y);
        this.m03 = t1.x;
        this.m13 = t1.y;
        this.m23 = t1.z;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(Matrix4f m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m03 = m1.m03;
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m13 = m1.m13;
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
        this.m23 = m1.m23;
        this.m30 = m1.m30;
        this.m31 = m1.m31;
        this.m32 = m1.m32;
        this.m33 = m1.m33;
    }

    public final void set(Matrix4d m1) {
        this.m00 = m1.m00;
        this.m01 = m1.m01;
        this.m02 = m1.m02;
        this.m03 = m1.m03;
        this.m10 = m1.m10;
        this.m11 = m1.m11;
        this.m12 = m1.m12;
        this.m13 = m1.m13;
        this.m20 = m1.m20;
        this.m21 = m1.m21;
        this.m22 = m1.m22;
        this.m23 = m1.m23;
        this.m30 = m1.m30;
        this.m31 = m1.m31;
        this.m32 = m1.m32;
        this.m33 = m1.m33;
    }

    public final void invert(Matrix4d m1) {
        this.invertGeneral(m1);
    }

    public final void invert() {
        this.invertGeneral(this);
    }

    final void invertGeneral(Matrix4d m1) {
        double[] result = new double[16];
        int[] row_perm = new int[4];
        double[] tmp = new double[]{m1.m00, m1.m01, m1.m02, m1.m03, m1.m10, m1.m11, m1.m12, m1.m13, m1.m20, m1.m21, m1.m22, m1.m23, m1.m30, m1.m31, m1.m32, m1.m33};
        if (!Matrix4d.luDecomposition(tmp, row_perm)) {
            throw new SingularMatrixException(VecMathI18N.getString("Matrix4d10"));
        }
        for (int i2 = 0; i2 < 16; ++i2) {
            result[i2] = 0.0;
        }
        result[0] = 1.0;
        result[5] = 1.0;
        result[10] = 1.0;
        result[15] = 1.0;
        Matrix4d.luBacksubstitution(tmp, row_perm, result);
        this.m00 = result[0];
        this.m01 = result[1];
        this.m02 = result[2];
        this.m03 = result[3];
        this.m10 = result[4];
        this.m11 = result[5];
        this.m12 = result[6];
        this.m13 = result[7];
        this.m20 = result[8];
        this.m21 = result[9];
        this.m22 = result[10];
        this.m23 = result[11];
        this.m30 = result[12];
        this.m31 = result[13];
        this.m32 = result[14];
        this.m33 = result[15];
    }

    static boolean luDecomposition(double[] matrix0, int[] row_perm) {
        double[] row_scale = new double[4];
        int ptr = 0;
        int rs2 = 0;
        int i2 = 4;
        while (i2-- != 0) {
            double big2 = 0.0;
            int j2 = 4;
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
        for (int j3 = 0; j3 < 4; ++j3) {
            double temp;
            int p1;
            int k2;
            int p2;
            double sum;
            int target;
            int i3;
            for (i3 = 0; i3 < j3; ++i3) {
                target = mtx + 4 * i3 + j3;
                sum = matrix0[target];
                int k3 = i3;
                int p12 = mtx + 4 * i3;
                p2 = mtx + j3;
                while (k3-- != 0) {
                    sum -= matrix0[p12] * matrix0[p2];
                    ++p12;
                    p2 += 4;
                }
                matrix0[target] = sum;
            }
            double big3 = 0.0;
            int imax = -1;
            for (i3 = j3; i3 < 4; ++i3) {
                double d2;
                target = mtx + 4 * i3 + j3;
                sum = matrix0[target];
                k2 = j3;
                p1 = mtx + 4 * i3;
                p2 = mtx + j3;
                while (k2-- != 0) {
                    sum -= matrix0[p1] * matrix0[p2];
                    ++p1;
                    p2 += 4;
                }
                matrix0[target] = sum;
                temp = row_scale[i3] * Math.abs(sum);
                if (!(d2 >= big3)) continue;
                big3 = temp;
                imax = i3;
            }
            if (imax < 0) {
                throw new RuntimeException(VecMathI18N.getString("Matrix4d11"));
            }
            if (j3 != imax) {
                k2 = 4;
                p1 = mtx + 4 * imax;
                p2 = mtx + 4 * j3;
                while (k2-- != 0) {
                    temp = matrix0[p1];
                    matrix0[p1++] = matrix0[p2];
                    matrix0[p2++] = temp;
                }
                row_scale[imax] = row_scale[j3];
            }
            row_perm[j3] = imax;
            if (matrix0[mtx + 4 * j3 + j3] == 0.0) {
                return false;
            }
            if (j3 == 3) continue;
            temp = 1.0 / matrix0[mtx + 4 * j3 + j3];
            target = mtx + 4 * (j3 + 1) + j3;
            i3 = 3 - j3;
            while (i3-- != 0) {
                int n2 = target;
                matrix0[n2] = matrix0[n2] * temp;
                target += 4;
            }
        }
        return true;
    }

    static void luBacksubstitution(double[] matrix1, int[] row_perm, double[] matrix2) {
        int rp2 = 0;
        for (int k2 = 0; k2 < 4; ++k2) {
            int rv2;
            int cv2 = k2;
            int ii2 = -1;
            for (int i2 = 0; i2 < 4; ++i2) {
                int ip2 = row_perm[rp2 + i2];
                double sum = matrix2[cv2 + 4 * ip2];
                matrix2[cv2 + 4 * ip2] = matrix2[cv2 + 4 * i2];
                if (ii2 >= 0) {
                    rv2 = i2 * 4;
                    for (int j2 = ii2; j2 <= i2 - 1; ++j2) {
                        sum -= matrix1[rv2 + j2] * matrix2[cv2 + 4 * j2];
                    }
                } else if (sum != 0.0) {
                    ii2 = i2;
                }
                matrix2[cv2 + 4 * i2] = sum;
            }
            rv2 = 12;
            int n2 = cv2 + 12;
            matrix2[n2] = matrix2[n2] / matrix1[rv2 + 3];
            matrix2[cv2 + 8] = (matrix2[cv2 + 8] - matrix1[(rv2 -= 4) + 3] * matrix2[cv2 + 12]) / matrix1[rv2 + 2];
            matrix2[cv2 + 4] = (matrix2[cv2 + 4] - matrix1[(rv2 -= 4) + 2] * matrix2[cv2 + 8] - matrix1[rv2 + 3] * matrix2[cv2 + 12]) / matrix1[rv2 + 1];
            matrix2[cv2 + 0] = (matrix2[cv2 + 0] - matrix1[(rv2 -= 4) + 1] * matrix2[cv2 + 4] - matrix1[rv2 + 2] * matrix2[cv2 + 8] - matrix1[rv2 + 3] * matrix2[cv2 + 12]) / matrix1[rv2 + 0];
        }
    }

    public final double determinant() {
        double det = this.m00 * (this.m11 * this.m22 * this.m33 + this.m12 * this.m23 * this.m31 + this.m13 * this.m21 * this.m32 - this.m13 * this.m22 * this.m31 - this.m11 * this.m23 * this.m32 - this.m12 * this.m21 * this.m33);
        det -= this.m01 * (this.m10 * this.m22 * this.m33 + this.m12 * this.m23 * this.m30 + this.m13 * this.m20 * this.m32 - this.m13 * this.m22 * this.m30 - this.m10 * this.m23 * this.m32 - this.m12 * this.m20 * this.m33);
        det += this.m02 * (this.m10 * this.m21 * this.m33 + this.m11 * this.m23 * this.m30 + this.m13 * this.m20 * this.m31 - this.m13 * this.m21 * this.m30 - this.m10 * this.m23 * this.m31 - this.m11 * this.m20 * this.m33);
        return det -= this.m03 * (this.m10 * this.m21 * this.m32 + this.m11 * this.m22 * this.m30 + this.m12 * this.m20 * this.m31 - this.m12 * this.m21 * this.m30 - this.m10 * this.m22 * this.m31 - this.m11 * this.m20 * this.m32);
    }

    public final void set(double scale) {
        this.m00 = scale;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m03 = 0.0;
        this.m10 = 0.0;
        this.m11 = scale;
        this.m12 = 0.0;
        this.m13 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = scale;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(Vector3d v1) {
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m03 = v1.x;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        this.m13 = v1.y;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
        this.m23 = v1.z;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(double scale, Vector3d v1) {
        this.m00 = scale;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m03 = v1.x;
        this.m10 = 0.0;
        this.m11 = scale;
        this.m12 = 0.0;
        this.m13 = v1.y;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = scale;
        this.m23 = v1.z;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(Vector3d v1, double scale) {
        this.m00 = scale;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m03 = scale * v1.x;
        this.m10 = 0.0;
        this.m11 = scale;
        this.m12 = 0.0;
        this.m13 = scale * v1.y;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = scale;
        this.m23 = scale * v1.z;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(Matrix3f m1, Vector3f t1, float scale) {
        this.m00 = m1.m00 * scale;
        this.m01 = m1.m01 * scale;
        this.m02 = m1.m02 * scale;
        this.m03 = t1.x;
        this.m10 = m1.m10 * scale;
        this.m11 = m1.m11 * scale;
        this.m12 = m1.m12 * scale;
        this.m13 = t1.y;
        this.m20 = m1.m20 * scale;
        this.m21 = m1.m21 * scale;
        this.m22 = m1.m22 * scale;
        this.m23 = t1.z;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void set(Matrix3d m1, Vector3d t1, double scale) {
        this.m00 = m1.m00 * scale;
        this.m01 = m1.m01 * scale;
        this.m02 = m1.m02 * scale;
        this.m03 = t1.x;
        this.m10 = m1.m10 * scale;
        this.m11 = m1.m11 * scale;
        this.m12 = m1.m12 * scale;
        this.m13 = t1.y;
        this.m20 = m1.m20 * scale;
        this.m21 = m1.m21 * scale;
        this.m22 = m1.m22 * scale;
        this.m23 = t1.z;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void setTranslation(Vector3d trans) {
        this.m03 = trans.x;
        this.m13 = trans.y;
        this.m23 = trans.z;
    }

    public final void rotX(double angle) {
        double sinAngle = Math.sin(angle);
        double cosAngle = Math.cos(angle);
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m03 = 0.0;
        this.m10 = 0.0;
        this.m11 = cosAngle;
        this.m12 = -sinAngle;
        this.m13 = 0.0;
        this.m20 = 0.0;
        this.m21 = sinAngle;
        this.m22 = cosAngle;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void rotY(double angle) {
        double cosAngle;
        double sinAngle = Math.sin(angle);
        this.m00 = cosAngle = Math.cos(angle);
        this.m01 = 0.0;
        this.m02 = sinAngle;
        this.m03 = 0.0;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        this.m13 = 0.0;
        this.m20 = -sinAngle;
        this.m21 = 0.0;
        this.m22 = cosAngle;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void rotZ(double angle) {
        double cosAngle;
        double sinAngle = Math.sin(angle);
        this.m00 = cosAngle = Math.cos(angle);
        this.m01 = -sinAngle;
        this.m02 = 0.0;
        this.m03 = 0.0;
        this.m10 = sinAngle;
        this.m11 = cosAngle;
        this.m12 = 0.0;
        this.m13 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 1.0;
    }

    public final void mul(double scalar) {
        this.m00 *= scalar;
        this.m01 *= scalar;
        this.m02 *= scalar;
        this.m03 *= scalar;
        this.m10 *= scalar;
        this.m11 *= scalar;
        this.m12 *= scalar;
        this.m13 *= scalar;
        this.m20 *= scalar;
        this.m21 *= scalar;
        this.m22 *= scalar;
        this.m23 *= scalar;
        this.m30 *= scalar;
        this.m31 *= scalar;
        this.m32 *= scalar;
        this.m33 *= scalar;
    }

    public final void mul(double scalar, Matrix4d m1) {
        this.m00 = m1.m00 * scalar;
        this.m01 = m1.m01 * scalar;
        this.m02 = m1.m02 * scalar;
        this.m03 = m1.m03 * scalar;
        this.m10 = m1.m10 * scalar;
        this.m11 = m1.m11 * scalar;
        this.m12 = m1.m12 * scalar;
        this.m13 = m1.m13 * scalar;
        this.m20 = m1.m20 * scalar;
        this.m21 = m1.m21 * scalar;
        this.m22 = m1.m22 * scalar;
        this.m23 = m1.m23 * scalar;
        this.m30 = m1.m30 * scalar;
        this.m31 = m1.m31 * scalar;
        this.m32 = m1.m32 * scalar;
        this.m33 = m1.m33 * scalar;
    }

    public final void mul(Matrix4d m1) {
        double m00 = this.m00 * m1.m00 + this.m01 * m1.m10 + this.m02 * m1.m20 + this.m03 * m1.m30;
        double m01 = this.m00 * m1.m01 + this.m01 * m1.m11 + this.m02 * m1.m21 + this.m03 * m1.m31;
        double m02 = this.m00 * m1.m02 + this.m01 * m1.m12 + this.m02 * m1.m22 + this.m03 * m1.m32;
        double m03 = this.m00 * m1.m03 + this.m01 * m1.m13 + this.m02 * m1.m23 + this.m03 * m1.m33;
        double m10 = this.m10 * m1.m00 + this.m11 * m1.m10 + this.m12 * m1.m20 + this.m13 * m1.m30;
        double m11 = this.m10 * m1.m01 + this.m11 * m1.m11 + this.m12 * m1.m21 + this.m13 * m1.m31;
        double m12 = this.m10 * m1.m02 + this.m11 * m1.m12 + this.m12 * m1.m22 + this.m13 * m1.m32;
        double m13 = this.m10 * m1.m03 + this.m11 * m1.m13 + this.m12 * m1.m23 + this.m13 * m1.m33;
        double m20 = this.m20 * m1.m00 + this.m21 * m1.m10 + this.m22 * m1.m20 + this.m23 * m1.m30;
        double m21 = this.m20 * m1.m01 + this.m21 * m1.m11 + this.m22 * m1.m21 + this.m23 * m1.m31;
        double m22 = this.m20 * m1.m02 + this.m21 * m1.m12 + this.m22 * m1.m22 + this.m23 * m1.m32;
        double m23 = this.m20 * m1.m03 + this.m21 * m1.m13 + this.m22 * m1.m23 + this.m23 * m1.m33;
        double m30 = this.m30 * m1.m00 + this.m31 * m1.m10 + this.m32 * m1.m20 + this.m33 * m1.m30;
        double m31 = this.m30 * m1.m01 + this.m31 * m1.m11 + this.m32 * m1.m21 + this.m33 * m1.m31;
        double m32 = this.m30 * m1.m02 + this.m31 * m1.m12 + this.m32 * m1.m22 + this.m33 * m1.m32;
        double m33 = this.m30 * m1.m03 + this.m31 * m1.m13 + this.m32 * m1.m23 + this.m33 * m1.m33;
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
    }

    public final void mul(Matrix4d m1, Matrix4d m2) {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20 + m1.m03 * m2.m30;
            this.m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21 + m1.m03 * m2.m31;
            this.m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22 + m1.m03 * m2.m32;
            this.m03 = m1.m00 * m2.m03 + m1.m01 * m2.m13 + m1.m02 * m2.m23 + m1.m03 * m2.m33;
            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20 + m1.m13 * m2.m30;
            this.m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21 + m1.m13 * m2.m31;
            this.m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22 + m1.m13 * m2.m32;
            this.m13 = m1.m10 * m2.m03 + m1.m11 * m2.m13 + m1.m12 * m2.m23 + m1.m13 * m2.m33;
            this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20 + m1.m23 * m2.m30;
            this.m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21 + m1.m23 * m2.m31;
            this.m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22 + m1.m23 * m2.m32;
            this.m23 = m1.m20 * m2.m03 + m1.m21 * m2.m13 + m1.m22 * m2.m23 + m1.m23 * m2.m33;
            this.m30 = m1.m30 * m2.m00 + m1.m31 * m2.m10 + m1.m32 * m2.m20 + m1.m33 * m2.m30;
            this.m31 = m1.m30 * m2.m01 + m1.m31 * m2.m11 + m1.m32 * m2.m21 + m1.m33 * m2.m31;
            this.m32 = m1.m30 * m2.m02 + m1.m31 * m2.m12 + m1.m32 * m2.m22 + m1.m33 * m2.m32;
            this.m33 = m1.m30 * m2.m03 + m1.m31 * m2.m13 + m1.m32 * m2.m23 + m1.m33 * m2.m33;
        } else {
            double m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20 + m1.m03 * m2.m30;
            double m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21 + m1.m03 * m2.m31;
            double m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22 + m1.m03 * m2.m32;
            double m03 = m1.m00 * m2.m03 + m1.m01 * m2.m13 + m1.m02 * m2.m23 + m1.m03 * m2.m33;
            double m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20 + m1.m13 * m2.m30;
            double m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21 + m1.m13 * m2.m31;
            double m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22 + m1.m13 * m2.m32;
            double m13 = m1.m10 * m2.m03 + m1.m11 * m2.m13 + m1.m12 * m2.m23 + m1.m13 * m2.m33;
            double m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20 + m1.m23 * m2.m30;
            double m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21 + m1.m23 * m2.m31;
            double m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22 + m1.m23 * m2.m32;
            double m23 = m1.m20 * m2.m03 + m1.m21 * m2.m13 + m1.m22 * m2.m23 + m1.m23 * m2.m33;
            double m30 = m1.m30 * m2.m00 + m1.m31 * m2.m10 + m1.m32 * m2.m20 + m1.m33 * m2.m30;
            double m31 = m1.m30 * m2.m01 + m1.m31 * m2.m11 + m1.m32 * m2.m21 + m1.m33 * m2.m31;
            double m32 = m1.m30 * m2.m02 + m1.m31 * m2.m12 + m1.m32 * m2.m22 + m1.m33 * m2.m32;
            double m33 = m1.m30 * m2.m03 + m1.m31 * m2.m13 + m1.m32 * m2.m23 + m1.m33 * m2.m33;
            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m03 = m03;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m30 = m30;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;
        }
    }

    public final void mulTransposeBoth(Matrix4d m1, Matrix4d m2) {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m10 * m2.m01 + m1.m20 * m2.m02 + m1.m30 * m2.m03;
            this.m01 = m1.m00 * m2.m10 + m1.m10 * m2.m11 + m1.m20 * m2.m12 + m1.m30 * m2.m13;
            this.m02 = m1.m00 * m2.m20 + m1.m10 * m2.m21 + m1.m20 * m2.m22 + m1.m30 * m2.m23;
            this.m03 = m1.m00 * m2.m30 + m1.m10 * m2.m31 + m1.m20 * m2.m32 + m1.m30 * m2.m33;
            this.m10 = m1.m01 * m2.m00 + m1.m11 * m2.m01 + m1.m21 * m2.m02 + m1.m31 * m2.m03;
            this.m11 = m1.m01 * m2.m10 + m1.m11 * m2.m11 + m1.m21 * m2.m12 + m1.m31 * m2.m13;
            this.m12 = m1.m01 * m2.m20 + m1.m11 * m2.m21 + m1.m21 * m2.m22 + m1.m31 * m2.m23;
            this.m13 = m1.m01 * m2.m30 + m1.m11 * m2.m31 + m1.m21 * m2.m32 + m1.m31 * m2.m33;
            this.m20 = m1.m02 * m2.m00 + m1.m12 * m2.m01 + m1.m22 * m2.m02 + m1.m32 * m2.m03;
            this.m21 = m1.m02 * m2.m10 + m1.m12 * m2.m11 + m1.m22 * m2.m12 + m1.m32 * m2.m13;
            this.m22 = m1.m02 * m2.m20 + m1.m12 * m2.m21 + m1.m22 * m2.m22 + m1.m32 * m2.m23;
            this.m23 = m1.m02 * m2.m30 + m1.m12 * m2.m31 + m1.m22 * m2.m32 + m1.m32 * m2.m33;
            this.m30 = m1.m03 * m2.m00 + m1.m13 * m2.m01 + m1.m23 * m2.m02 + m1.m33 * m2.m03;
            this.m31 = m1.m03 * m2.m10 + m1.m13 * m2.m11 + m1.m23 * m2.m12 + m1.m33 * m2.m13;
            this.m32 = m1.m03 * m2.m20 + m1.m13 * m2.m21 + m1.m23 * m2.m22 + m1.m33 * m2.m23;
            this.m33 = m1.m03 * m2.m30 + m1.m13 * m2.m31 + m1.m23 * m2.m32 + m1.m33 * m2.m33;
        } else {
            double m00 = m1.m00 * m2.m00 + m1.m10 * m2.m01 + m1.m20 * m2.m02 + m1.m30 * m2.m03;
            double m01 = m1.m00 * m2.m10 + m1.m10 * m2.m11 + m1.m20 * m2.m12 + m1.m30 * m2.m13;
            double m02 = m1.m00 * m2.m20 + m1.m10 * m2.m21 + m1.m20 * m2.m22 + m1.m30 * m2.m23;
            double m03 = m1.m00 * m2.m30 + m1.m10 * m2.m31 + m1.m20 * m2.m32 + m1.m30 * m2.m33;
            double m10 = m1.m01 * m2.m00 + m1.m11 * m2.m01 + m1.m21 * m2.m02 + m1.m31 * m2.m03;
            double m11 = m1.m01 * m2.m10 + m1.m11 * m2.m11 + m1.m21 * m2.m12 + m1.m31 * m2.m13;
            double m12 = m1.m01 * m2.m20 + m1.m11 * m2.m21 + m1.m21 * m2.m22 + m1.m31 * m2.m23;
            double m13 = m1.m01 * m2.m30 + m1.m11 * m2.m31 + m1.m21 * m2.m32 + m1.m31 * m2.m33;
            double m20 = m1.m02 * m2.m00 + m1.m12 * m2.m01 + m1.m22 * m2.m02 + m1.m32 * m2.m03;
            double m21 = m1.m02 * m2.m10 + m1.m12 * m2.m11 + m1.m22 * m2.m12 + m1.m32 * m2.m13;
            double m22 = m1.m02 * m2.m20 + m1.m12 * m2.m21 + m1.m22 * m2.m22 + m1.m32 * m2.m23;
            double m23 = m1.m02 * m2.m30 + m1.m12 * m2.m31 + m1.m22 * m2.m32 + m1.m32 * m2.m33;
            double m30 = m1.m03 * m2.m00 + m1.m13 * m2.m01 + m1.m23 * m2.m02 + m1.m33 * m2.m03;
            double m31 = m1.m03 * m2.m10 + m1.m13 * m2.m11 + m1.m23 * m2.m12 + m1.m33 * m2.m13;
            double m32 = m1.m03 * m2.m20 + m1.m13 * m2.m21 + m1.m23 * m2.m22 + m1.m33 * m2.m23;
            double m33 = m1.m03 * m2.m30 + m1.m13 * m2.m31 + m1.m23 * m2.m32 + m1.m33 * m2.m33;
            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m03 = m03;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m30 = m30;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;
        }
    }

    public final void mulTransposeRight(Matrix4d m1, Matrix4d m2) {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02 + m1.m03 * m2.m03;
            this.m01 = m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12 + m1.m03 * m2.m13;
            this.m02 = m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22 + m1.m03 * m2.m23;
            this.m03 = m1.m00 * m2.m30 + m1.m01 * m2.m31 + m1.m02 * m2.m32 + m1.m03 * m2.m33;
            this.m10 = m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02 + m1.m13 * m2.m03;
            this.m11 = m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12 + m1.m13 * m2.m13;
            this.m12 = m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22 + m1.m13 * m2.m23;
            this.m13 = m1.m10 * m2.m30 + m1.m11 * m2.m31 + m1.m12 * m2.m32 + m1.m13 * m2.m33;
            this.m20 = m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02 + m1.m23 * m2.m03;
            this.m21 = m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12 + m1.m23 * m2.m13;
            this.m22 = m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22 + m1.m23 * m2.m23;
            this.m23 = m1.m20 * m2.m30 + m1.m21 * m2.m31 + m1.m22 * m2.m32 + m1.m23 * m2.m33;
            this.m30 = m1.m30 * m2.m00 + m1.m31 * m2.m01 + m1.m32 * m2.m02 + m1.m33 * m2.m03;
            this.m31 = m1.m30 * m2.m10 + m1.m31 * m2.m11 + m1.m32 * m2.m12 + m1.m33 * m2.m13;
            this.m32 = m1.m30 * m2.m20 + m1.m31 * m2.m21 + m1.m32 * m2.m22 + m1.m33 * m2.m23;
            this.m33 = m1.m30 * m2.m30 + m1.m31 * m2.m31 + m1.m32 * m2.m32 + m1.m33 * m2.m33;
        } else {
            double m00 = m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02 + m1.m03 * m2.m03;
            double m01 = m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12 + m1.m03 * m2.m13;
            double m02 = m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22 + m1.m03 * m2.m23;
            double m03 = m1.m00 * m2.m30 + m1.m01 * m2.m31 + m1.m02 * m2.m32 + m1.m03 * m2.m33;
            double m10 = m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02 + m1.m13 * m2.m03;
            double m11 = m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12 + m1.m13 * m2.m13;
            double m12 = m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22 + m1.m13 * m2.m23;
            double m13 = m1.m10 * m2.m30 + m1.m11 * m2.m31 + m1.m12 * m2.m32 + m1.m13 * m2.m33;
            double m20 = m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02 + m1.m23 * m2.m03;
            double m21 = m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12 + m1.m23 * m2.m13;
            double m22 = m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22 + m1.m23 * m2.m23;
            double m23 = m1.m20 * m2.m30 + m1.m21 * m2.m31 + m1.m22 * m2.m32 + m1.m23 * m2.m33;
            double m30 = m1.m30 * m2.m00 + m1.m31 * m2.m01 + m1.m32 * m2.m02 + m1.m33 * m2.m03;
            double m31 = m1.m30 * m2.m10 + m1.m31 * m2.m11 + m1.m32 * m2.m12 + m1.m33 * m2.m13;
            double m32 = m1.m30 * m2.m20 + m1.m31 * m2.m21 + m1.m32 * m2.m22 + m1.m33 * m2.m23;
            double m33 = m1.m30 * m2.m30 + m1.m31 * m2.m31 + m1.m32 * m2.m32 + m1.m33 * m2.m33;
            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m03 = m03;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m30 = m30;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;
        }
    }

    public final void mulTransposeLeft(Matrix4d m1, Matrix4d m2) {
        if (this != m1 && this != m2) {
            this.m00 = m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20 + m1.m30 * m2.m30;
            this.m01 = m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21 + m1.m30 * m2.m31;
            this.m02 = m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22 + m1.m30 * m2.m32;
            this.m03 = m1.m00 * m2.m03 + m1.m10 * m2.m13 + m1.m20 * m2.m23 + m1.m30 * m2.m33;
            this.m10 = m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20 + m1.m31 * m2.m30;
            this.m11 = m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21 + m1.m31 * m2.m31;
            this.m12 = m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22 + m1.m31 * m2.m32;
            this.m13 = m1.m01 * m2.m03 + m1.m11 * m2.m13 + m1.m21 * m2.m23 + m1.m31 * m2.m33;
            this.m20 = m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20 + m1.m32 * m2.m30;
            this.m21 = m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21 + m1.m32 * m2.m31;
            this.m22 = m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22 + m1.m32 * m2.m32;
            this.m23 = m1.m02 * m2.m03 + m1.m12 * m2.m13 + m1.m22 * m2.m23 + m1.m32 * m2.m33;
            this.m30 = m1.m03 * m2.m00 + m1.m13 * m2.m10 + m1.m23 * m2.m20 + m1.m33 * m2.m30;
            this.m31 = m1.m03 * m2.m01 + m1.m13 * m2.m11 + m1.m23 * m2.m21 + m1.m33 * m2.m31;
            this.m32 = m1.m03 * m2.m02 + m1.m13 * m2.m12 + m1.m23 * m2.m22 + m1.m33 * m2.m32;
            this.m33 = m1.m03 * m2.m03 + m1.m13 * m2.m13 + m1.m23 * m2.m23 + m1.m33 * m2.m33;
        } else {
            double m00 = m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20 + m1.m30 * m2.m30;
            double m01 = m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21 + m1.m30 * m2.m31;
            double m02 = m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22 + m1.m30 * m2.m32;
            double m03 = m1.m00 * m2.m03 + m1.m10 * m2.m13 + m1.m20 * m2.m23 + m1.m30 * m2.m33;
            double m10 = m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20 + m1.m31 * m2.m30;
            double m11 = m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21 + m1.m31 * m2.m31;
            double m12 = m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22 + m1.m31 * m2.m32;
            double m13 = m1.m01 * m2.m03 + m1.m11 * m2.m13 + m1.m21 * m2.m23 + m1.m31 * m2.m33;
            double m20 = m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20 + m1.m32 * m2.m30;
            double m21 = m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21 + m1.m32 * m2.m31;
            double m22 = m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22 + m1.m32 * m2.m32;
            double m23 = m1.m02 * m2.m03 + m1.m12 * m2.m13 + m1.m22 * m2.m23 + m1.m32 * m2.m33;
            double m30 = m1.m03 * m2.m00 + m1.m13 * m2.m10 + m1.m23 * m2.m20 + m1.m33 * m2.m30;
            double m31 = m1.m03 * m2.m01 + m1.m13 * m2.m11 + m1.m23 * m2.m21 + m1.m33 * m2.m31;
            double m32 = m1.m03 * m2.m02 + m1.m13 * m2.m12 + m1.m23 * m2.m22 + m1.m33 * m2.m32;
            double m33 = m1.m03 * m2.m03 + m1.m13 * m2.m13 + m1.m23 * m2.m23 + m1.m33 * m2.m33;
            this.m00 = m00;
            this.m01 = m01;
            this.m02 = m02;
            this.m03 = m03;
            this.m10 = m10;
            this.m11 = m11;
            this.m12 = m12;
            this.m13 = m13;
            this.m20 = m20;
            this.m21 = m21;
            this.m22 = m22;
            this.m23 = m23;
            this.m30 = m30;
            this.m31 = m31;
            this.m32 = m32;
            this.m33 = m33;
        }
    }

    public boolean equals(Matrix4d m1) {
        try {
            return this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02 && this.m03 == m1.m03 && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12 && this.m13 == m1.m13 && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22 && this.m23 == m1.m23 && this.m30 == m1.m30 && this.m31 == m1.m31 && this.m32 == m1.m32 && this.m33 == m1.m33;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean equals(Object t1) {
        try {
            Matrix4d m2 = (Matrix4d)t1;
            return this.m00 == m2.m00 && this.m01 == m2.m01 && this.m02 == m2.m02 && this.m03 == m2.m03 && this.m10 == m2.m10 && this.m11 == m2.m11 && this.m12 == m2.m12 && this.m13 == m2.m13 && this.m20 == m2.m20 && this.m21 == m2.m21 && this.m22 == m2.m22 && this.m23 == m2.m23 && this.m30 == m2.m30 && this.m31 == m2.m31 && this.m32 == m2.m32 && this.m33 == m2.m33;
        }
        catch (ClassCastException e1) {
            return false;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean epsilonEquals(Matrix4d m1, float epsilon) {
        return this.epsilonEquals(m1, (double)epsilon);
    }

    public boolean epsilonEquals(Matrix4d m1, double epsilon) {
        double diff = this.m00 - m1.m00;
        double d2 = diff < 0.0 ? -diff : diff;
        if (d2 > epsilon) {
            return false;
        }
        diff = this.m01 - m1.m01;
        double d3 = diff < 0.0 ? -diff : diff;
        if (d3 > epsilon) {
            return false;
        }
        diff = this.m02 - m1.m02;
        double d4 = diff < 0.0 ? -diff : diff;
        if (d4 > epsilon) {
            return false;
        }
        diff = this.m03 - m1.m03;
        double d5 = diff < 0.0 ? -diff : diff;
        if (d5 > epsilon) {
            return false;
        }
        diff = this.m10 - m1.m10;
        double d6 = diff < 0.0 ? -diff : diff;
        if (d6 > epsilon) {
            return false;
        }
        diff = this.m11 - m1.m11;
        double d7 = diff < 0.0 ? -diff : diff;
        if (d7 > epsilon) {
            return false;
        }
        diff = this.m12 - m1.m12;
        double d8 = diff < 0.0 ? -diff : diff;
        if (d8 > epsilon) {
            return false;
        }
        diff = this.m13 - m1.m13;
        double d9 = diff < 0.0 ? -diff : diff;
        if (d9 > epsilon) {
            return false;
        }
        diff = this.m20 - m1.m20;
        double d10 = diff < 0.0 ? -diff : diff;
        if (d10 > epsilon) {
            return false;
        }
        diff = this.m21 - m1.m21;
        double d11 = diff < 0.0 ? -diff : diff;
        if (d11 > epsilon) {
            return false;
        }
        diff = this.m22 - m1.m22;
        double d12 = diff < 0.0 ? -diff : diff;
        if (d12 > epsilon) {
            return false;
        }
        diff = this.m23 - m1.m23;
        double d13 = diff < 0.0 ? -diff : diff;
        if (d13 > epsilon) {
            return false;
        }
        diff = this.m30 - m1.m30;
        double d14 = diff < 0.0 ? -diff : diff;
        if (d14 > epsilon) {
            return false;
        }
        diff = this.m31 - m1.m31;
        double d15 = diff < 0.0 ? -diff : diff;
        if (d15 > epsilon) {
            return false;
        }
        diff = this.m32 - m1.m32;
        double d16 = diff < 0.0 ? -diff : diff;
        if (d16 > epsilon) {
            return false;
        }
        diff = this.m33 - m1.m33;
        double d17 = diff < 0.0 ? -diff : diff;
        return !(d17 > epsilon);
    }

    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m00);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m01);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m02);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m03);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m10);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m11);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m12);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m13);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m20);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m21);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m22);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m23);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m30);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m31);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m32);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m33);
        return (int)(bits ^ bits >> 32);
    }

    public final void transform(Tuple4d vec, Tuple4d vecOut) {
        double x2 = this.m00 * vec.x + this.m01 * vec.y + this.m02 * vec.z + this.m03 * vec.w;
        double y2 = this.m10 * vec.x + this.m11 * vec.y + this.m12 * vec.z + this.m13 * vec.w;
        double z2 = this.m20 * vec.x + this.m21 * vec.y + this.m22 * vec.z + this.m23 * vec.w;
        vecOut.w = this.m30 * vec.x + this.m31 * vec.y + this.m32 * vec.z + this.m33 * vec.w;
        vecOut.x = x2;
        vecOut.y = y2;
        vecOut.z = z2;
    }

    public final void transform(Tuple4d vec) {
        double x2 = this.m00 * vec.x + this.m01 * vec.y + this.m02 * vec.z + this.m03 * vec.w;
        double y2 = this.m10 * vec.x + this.m11 * vec.y + this.m12 * vec.z + this.m13 * vec.w;
        double z2 = this.m20 * vec.x + this.m21 * vec.y + this.m22 * vec.z + this.m23 * vec.w;
        vec.w = this.m30 * vec.x + this.m31 * vec.y + this.m32 * vec.z + this.m33 * vec.w;
        vec.x = x2;
        vec.y = y2;
        vec.z = z2;
    }

    public final void transform(Tuple4f vec, Tuple4f vecOut) {
        float x2 = (float)(this.m00 * (double)vec.x + this.m01 * (double)vec.y + this.m02 * (double)vec.z + this.m03 * (double)vec.w);
        float y2 = (float)(this.m10 * (double)vec.x + this.m11 * (double)vec.y + this.m12 * (double)vec.z + this.m13 * (double)vec.w);
        float z2 = (float)(this.m20 * (double)vec.x + this.m21 * (double)vec.y + this.m22 * (double)vec.z + this.m23 * (double)vec.w);
        vecOut.w = (float)(this.m30 * (double)vec.x + this.m31 * (double)vec.y + this.m32 * (double)vec.z + this.m33 * (double)vec.w);
        vecOut.x = x2;
        vecOut.y = y2;
        vecOut.z = z2;
    }

    public final void transform(Tuple4f vec) {
        float x2 = (float)(this.m00 * (double)vec.x + this.m01 * (double)vec.y + this.m02 * (double)vec.z + this.m03 * (double)vec.w);
        float y2 = (float)(this.m10 * (double)vec.x + this.m11 * (double)vec.y + this.m12 * (double)vec.z + this.m13 * (double)vec.w);
        float z2 = (float)(this.m20 * (double)vec.x + this.m21 * (double)vec.y + this.m22 * (double)vec.z + this.m23 * (double)vec.w);
        vec.w = (float)(this.m30 * (double)vec.x + this.m31 * (double)vec.y + this.m32 * (double)vec.z + this.m33 * (double)vec.w);
        vec.x = x2;
        vec.y = y2;
        vec.z = z2;
    }

    public final void transform(Point3d point, Point3d pointOut) {
        double x2 = this.m00 * point.x + this.m01 * point.y + this.m02 * point.z + this.m03;
        double y2 = this.m10 * point.x + this.m11 * point.y + this.m12 * point.z + this.m13;
        pointOut.z = this.m20 * point.x + this.m21 * point.y + this.m22 * point.z + this.m23;
        pointOut.x = x2;
        pointOut.y = y2;
    }

    public final void transform(Point3d point) {
        double x2 = this.m00 * point.x + this.m01 * point.y + this.m02 * point.z + this.m03;
        double y2 = this.m10 * point.x + this.m11 * point.y + this.m12 * point.z + this.m13;
        point.z = this.m20 * point.x + this.m21 * point.y + this.m22 * point.z + this.m23;
        point.x = x2;
        point.y = y2;
    }

    public final void transform(Point3f point, Point3f pointOut) {
        float x2 = (float)(this.m00 * (double)point.x + this.m01 * (double)point.y + this.m02 * (double)point.z + this.m03);
        float y2 = (float)(this.m10 * (double)point.x + this.m11 * (double)point.y + this.m12 * (double)point.z + this.m13);
        pointOut.z = (float)(this.m20 * (double)point.x + this.m21 * (double)point.y + this.m22 * (double)point.z + this.m23);
        pointOut.x = x2;
        pointOut.y = y2;
    }

    public final void transform(Point3f point) {
        float x2 = (float)(this.m00 * (double)point.x + this.m01 * (double)point.y + this.m02 * (double)point.z + this.m03);
        float y2 = (float)(this.m10 * (double)point.x + this.m11 * (double)point.y + this.m12 * (double)point.z + this.m13);
        point.z = (float)(this.m20 * (double)point.x + this.m21 * (double)point.y + this.m22 * (double)point.z + this.m23);
        point.x = x2;
        point.y = y2;
    }

    public final void transform(Vector3d normal, Vector3d normalOut) {
        double x2 = this.m00 * normal.x + this.m01 * normal.y + this.m02 * normal.z;
        double y2 = this.m10 * normal.x + this.m11 * normal.y + this.m12 * normal.z;
        normalOut.z = this.m20 * normal.x + this.m21 * normal.y + this.m22 * normal.z;
        normalOut.x = x2;
        normalOut.y = y2;
    }

    public final void transform(Vector3d normal) {
        double x2 = this.m00 * normal.x + this.m01 * normal.y + this.m02 * normal.z;
        double y2 = this.m10 * normal.x + this.m11 * normal.y + this.m12 * normal.z;
        normal.z = this.m20 * normal.x + this.m21 * normal.y + this.m22 * normal.z;
        normal.x = x2;
        normal.y = y2;
    }

    public final void transform(Vector3f normal, Vector3f normalOut) {
        float x2 = (float)(this.m00 * (double)normal.x + this.m01 * (double)normal.y + this.m02 * (double)normal.z);
        float y2 = (float)(this.m10 * (double)normal.x + this.m11 * (double)normal.y + this.m12 * (double)normal.z);
        normalOut.z = (float)(this.m20 * (double)normal.x + this.m21 * (double)normal.y + this.m22 * (double)normal.z);
        normalOut.x = x2;
        normalOut.y = y2;
    }

    public final void transform(Vector3f normal) {
        float x2 = (float)(this.m00 * (double)normal.x + this.m01 * (double)normal.y + this.m02 * (double)normal.z);
        float y2 = (float)(this.m10 * (double)normal.x + this.m11 * (double)normal.y + this.m12 * (double)normal.z);
        normal.z = (float)(this.m20 * (double)normal.x + this.m21 * (double)normal.y + this.m22 * (double)normal.z);
        normal.x = x2;
        normal.y = y2;
    }

    public final void setRotation(Matrix3d m1) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        this.m00 = m1.m00 * tmp_scale[0];
        this.m01 = m1.m01 * tmp_scale[1];
        this.m02 = m1.m02 * tmp_scale[2];
        this.m10 = m1.m10 * tmp_scale[0];
        this.m11 = m1.m11 * tmp_scale[1];
        this.m12 = m1.m12 * tmp_scale[2];
        this.m20 = m1.m20 * tmp_scale[0];
        this.m21 = m1.m21 * tmp_scale[1];
        this.m22 = m1.m22 * tmp_scale[2];
    }

    public final void setRotation(Matrix3f m1) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        this.m00 = (double)m1.m00 * tmp_scale[0];
        this.m01 = (double)m1.m01 * tmp_scale[1];
        this.m02 = (double)m1.m02 * tmp_scale[2];
        this.m10 = (double)m1.m10 * tmp_scale[0];
        this.m11 = (double)m1.m11 * tmp_scale[1];
        this.m12 = (double)m1.m12 * tmp_scale[2];
        this.m20 = (double)m1.m20 * tmp_scale[0];
        this.m21 = (double)m1.m21 * tmp_scale[1];
        this.m22 = (double)m1.m22 * tmp_scale[2];
    }

    public final void setRotation(Quat4f q1) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        this.m00 = (1.0 - (double)(2.0f * q1.y * q1.y) - (double)(2.0f * q1.z * q1.z)) * tmp_scale[0];
        this.m10 = 2.0 * (double)(q1.x * q1.y + q1.w * q1.z) * tmp_scale[0];
        this.m20 = 2.0 * (double)(q1.x * q1.z - q1.w * q1.y) * tmp_scale[0];
        this.m01 = 2.0 * (double)(q1.x * q1.y - q1.w * q1.z) * tmp_scale[1];
        this.m11 = (1.0 - (double)(2.0f * q1.x * q1.x) - (double)(2.0f * q1.z * q1.z)) * tmp_scale[1];
        this.m21 = 2.0 * (double)(q1.y * q1.z + q1.w * q1.x) * tmp_scale[1];
        this.m02 = 2.0 * (double)(q1.x * q1.z + q1.w * q1.y) * tmp_scale[2];
        this.m12 = 2.0 * (double)(q1.y * q1.z - q1.w * q1.x) * tmp_scale[2];
        this.m22 = (1.0 - (double)(2.0f * q1.x * q1.x) - (double)(2.0f * q1.y * q1.y)) * tmp_scale[2];
    }

    public final void setRotation(Quat4d q1) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        this.m00 = (1.0 - 2.0 * q1.y * q1.y - 2.0 * q1.z * q1.z) * tmp_scale[0];
        this.m10 = 2.0 * (q1.x * q1.y + q1.w * q1.z) * tmp_scale[0];
        this.m20 = 2.0 * (q1.x * q1.z - q1.w * q1.y) * tmp_scale[0];
        this.m01 = 2.0 * (q1.x * q1.y - q1.w * q1.z) * tmp_scale[1];
        this.m11 = (1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.z * q1.z) * tmp_scale[1];
        this.m21 = 2.0 * (q1.y * q1.z + q1.w * q1.x) * tmp_scale[1];
        this.m02 = 2.0 * (q1.x * q1.z + q1.w * q1.y) * tmp_scale[2];
        this.m12 = 2.0 * (q1.y * q1.z - q1.w * q1.x) * tmp_scale[2];
        this.m22 = (1.0 - 2.0 * q1.x * q1.x - 2.0 * q1.y * q1.y) * tmp_scale[2];
    }

    public final void setRotation(AxisAngle4d a1) {
        double[] tmp_rot = new double[9];
        double[] tmp_scale = new double[3];
        this.getScaleRotate(tmp_scale, tmp_rot);
        double mag = 1.0 / Math.sqrt(a1.x * a1.x + a1.y * a1.y + a1.z * a1.z);
        double ax2 = a1.x * mag;
        double ay2 = a1.y * mag;
        double az2 = a1.z * mag;
        double sinTheta = Math.sin(a1.angle);
        double cosTheta = Math.cos(a1.angle);
        double t2 = 1.0 - cosTheta;
        double xz = a1.x * a1.z;
        double xy2 = a1.x * a1.y;
        double yz2 = a1.y * a1.z;
        this.m00 = (t2 * ax2 * ax2 + cosTheta) * tmp_scale[0];
        this.m01 = (t2 * xy2 - sinTheta * az2) * tmp_scale[1];
        this.m02 = (t2 * xz + sinTheta * ay2) * tmp_scale[2];
        this.m10 = (t2 * xy2 + sinTheta * az2) * tmp_scale[0];
        this.m11 = (t2 * ay2 * ay2 + cosTheta) * tmp_scale[1];
        this.m12 = (t2 * yz2 - sinTheta * ax2) * tmp_scale[2];
        this.m20 = (t2 * xz - sinTheta * ay2) * tmp_scale[0];
        this.m21 = (t2 * yz2 + sinTheta * ax2) * tmp_scale[1];
        this.m22 = (t2 * az2 * az2 + cosTheta) * tmp_scale[2];
    }

    public final void setZero() {
        this.m00 = 0.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m03 = 0.0;
        this.m10 = 0.0;
        this.m11 = 0.0;
        this.m12 = 0.0;
        this.m13 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 0.0;
        this.m23 = 0.0;
        this.m30 = 0.0;
        this.m31 = 0.0;
        this.m32 = 0.0;
        this.m33 = 0.0;
    }

    public final void negate() {
        this.m00 = -this.m00;
        this.m01 = -this.m01;
        this.m02 = -this.m02;
        this.m03 = -this.m03;
        this.m10 = -this.m10;
        this.m11 = -this.m11;
        this.m12 = -this.m12;
        this.m13 = -this.m13;
        this.m20 = -this.m20;
        this.m21 = -this.m21;
        this.m22 = -this.m22;
        this.m23 = -this.m23;
        this.m30 = -this.m30;
        this.m31 = -this.m31;
        this.m32 = -this.m32;
        this.m33 = -this.m33;
    }

    public final void negate(Matrix4d m1) {
        this.m00 = -m1.m00;
        this.m01 = -m1.m01;
        this.m02 = -m1.m02;
        this.m03 = -m1.m03;
        this.m10 = -m1.m10;
        this.m11 = -m1.m11;
        this.m12 = -m1.m12;
        this.m13 = -m1.m13;
        this.m20 = -m1.m20;
        this.m21 = -m1.m21;
        this.m22 = -m1.m22;
        this.m23 = -m1.m23;
        this.m30 = -m1.m30;
        this.m31 = -m1.m31;
        this.m32 = -m1.m32;
        this.m33 = -m1.m33;
    }

    private final void getScaleRotate(double[] scales, double[] rots) {
        double[] tmp = new double[]{this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22};
        Matrix3d.compute_svd(tmp, scales, rots);
    }

    public Object clone() {
        Matrix4d m1 = null;
        try {
            m1 = (Matrix4d)super.clone();
        }
        catch (CloneNotSupportedException e2) {
            throw new InternalError();
        }
        return m1;
    }

    public final double getM00() {
        return this.m00;
    }

    public final void setM00(double m00) {
        this.m00 = m00;
    }

    public final double getM01() {
        return this.m01;
    }

    public final void setM01(double m01) {
        this.m01 = m01;
    }

    public final double getM02() {
        return this.m02;
    }

    public final void setM02(double m02) {
        this.m02 = m02;
    }

    public final double getM10() {
        return this.m10;
    }

    public final void setM10(double m10) {
        this.m10 = m10;
    }

    public final double getM11() {
        return this.m11;
    }

    public final void setM11(double m11) {
        this.m11 = m11;
    }

    public final double getM12() {
        return this.m12;
    }

    public final void setM12(double m12) {
        this.m12 = m12;
    }

    public final double getM20() {
        return this.m20;
    }

    public final void setM20(double m20) {
        this.m20 = m20;
    }

    public final double getM21() {
        return this.m21;
    }

    public final void setM21(double m21) {
        this.m21 = m21;
    }

    public final double getM22() {
        return this.m22;
    }

    public final void setM22(double m22) {
        this.m22 = m22;
    }

    public final double getM03() {
        return this.m03;
    }

    public final void setM03(double m03) {
        this.m03 = m03;
    }

    public final double getM13() {
        return this.m13;
    }

    public final void setM13(double m13) {
        this.m13 = m13;
    }

    public final double getM23() {
        return this.m23;
    }

    public final void setM23(double m23) {
        this.m23 = m23;
    }

    public final double getM30() {
        return this.m30;
    }

    public final void setM30(double m30) {
        this.m30 = m30;
    }

    public final double getM31() {
        return this.m31;
    }

    public final void setM31(double m31) {
        this.m31 = m31;
    }

    public final double getM32() {
        return this.m32;
    }

    public final void setM32(double m32) {
        this.m32 = m32;
    }

    public final double getM33() {
        return this.m33;
    }

    public final void setM33(double m33) {
        this.m33 = m33;
    }
}

