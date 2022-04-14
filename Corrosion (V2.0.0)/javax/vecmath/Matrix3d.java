/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.AxisAngle4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Quat4d;
import javax.vecmath.Quat4f;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.Tuple3d;
import javax.vecmath.VecMathI18N;
import javax.vecmath.VecMathUtil;
import javax.vecmath.Vector3d;

public class Matrix3d
implements Serializable,
Cloneable {
    static final long serialVersionUID = 6837536777072402710L;
    public double m00;
    public double m01;
    public double m02;
    public double m10;
    public double m11;
    public double m12;
    public double m20;
    public double m21;
    public double m22;
    private static final double EPS = 1.110223024E-16;
    private static final double ERR_EPS = 1.0E-8;
    private static double xin;
    private static double yin;
    private static double zin;
    private static double xout;
    private static double yout;
    private static double zout;

    public Matrix3d(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21, double m22) {
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

    public Matrix3d(double[] v2) {
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

    public Matrix3d(Matrix3d m1) {
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

    public Matrix3d(Matrix3f m1) {
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

    public Matrix3d() {
        this.m00 = 0.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 0.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 0.0;
    }

    public String toString() {
        return this.m00 + ", " + this.m01 + ", " + this.m02 + "\n" + this.m10 + ", " + this.m11 + ", " + this.m12 + "\n" + this.m20 + ", " + this.m21 + ", " + this.m22 + "\n";
    }

    public final void setIdentity() {
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
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
                }
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
            }
            default: {
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d0"));
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
        throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d1"));
    }

    public final void getRow(int row, Vector3d v2) {
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
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d2"));
        }
    }

    public final void getRow(int row, double[] v2) {
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
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d2"));
        }
    }

    public final void getColumn(int column, Vector3d v2) {
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
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d4"));
        }
    }

    public final void getColumn(int column, double[] v2) {
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
            throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d4"));
        }
    }

    public final void setRow(int row, double x2, double y2, double z2) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
            }
        }
    }

    public final void setRow(int row, Vector3d v2) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
            }
        }
    }

    public final void setRow(int row, double[] v2) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d6"));
            }
        }
    }

    public final void setColumn(int column, double x2, double y2, double z2) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
            }
        }
    }

    public final void setColumn(int column, Vector3d v2) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
            }
        }
    }

    public final void setColumn(int column, double[] v2) {
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
                throw new ArrayIndexOutOfBoundsException(VecMathI18N.getString("Matrix3d9"));
            }
        }
    }

    public final double getScale() {
        double[] tmp_scale = new double[3];
        double[] tmp_rot = new double[9];
        this.getScaleRotate(tmp_scale, tmp_rot);
        return Matrix3d.max3(tmp_scale);
    }

    public final void add(double scalar) {
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

    public final void add(double scalar, Matrix3d m1) {
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

    public final void add(Matrix3d m1, Matrix3d m2) {
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

    public final void add(Matrix3d m1) {
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

    public final void sub(Matrix3d m1, Matrix3d m2) {
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

    public final void sub(Matrix3d m1) {
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
        double temp = this.m10;
        this.m10 = this.m01;
        this.m01 = temp;
        temp = this.m20;
        this.m20 = this.m02;
        this.m02 = temp;
        temp = this.m21;
        this.m21 = this.m12;
        this.m12 = temp;
    }

    public final void transpose(Matrix3d m1) {
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
    }

    public final void set(AxisAngle4d a1) {
        double mag = Math.sqrt(a1.x * a1.x + a1.y * a1.y + a1.z * a1.z);
        if (mag < 1.110223024E-16) {
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
    }

    public final void set(AxisAngle4f a1) {
        double mag = Math.sqrt(a1.x * a1.x + a1.y * a1.y + a1.z * a1.z);
        if (mag < 1.110223024E-16) {
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

    public final void set(double[] m2) {
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

    public final void invert(Matrix3d m1) {
        this.invertGeneral(m1);
    }

    public final void invert() {
        this.invertGeneral(this);
    }

    private final void invertGeneral(Matrix3d m1) {
        double[] result = new double[9];
        int[] row_perm = new int[3];
        double[] tmp = new double[]{m1.m00, m1.m01, m1.m02, m1.m10, m1.m11, m1.m12, m1.m20, m1.m21, m1.m22};
        if (!Matrix3d.luDecomposition(tmp, row_perm)) {
            throw new SingularMatrixException(VecMathI18N.getString("Matrix3d12"));
        }
        for (int i2 = 0; i2 < 9; ++i2) {
            result[i2] = 0.0;
        }
        result[0] = 1.0;
        result[4] = 1.0;
        result[8] = 1.0;
        Matrix3d.luBacksubstitution(tmp, row_perm, result);
        this.m00 = result[0];
        this.m01 = result[1];
        this.m02 = result[2];
        this.m10 = result[3];
        this.m11 = result[4];
        this.m12 = result[5];
        this.m20 = result[6];
        this.m21 = result[7];
        this.m22 = result[8];
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
                throw new RuntimeException(VecMathI18N.getString("Matrix3d13"));
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

    public final double determinant() {
        double total = this.m00 * (this.m11 * this.m22 - this.m12 * this.m21) + this.m01 * (this.m12 * this.m20 - this.m10 * this.m22) + this.m02 * (this.m10 * this.m21 - this.m11 * this.m20);
        return total;
    }

    public final void set(double scale) {
        this.m00 = scale;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = scale;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = scale;
    }

    public final void rotX(double angle) {
        double sinAngle = Math.sin(angle);
        double cosAngle = Math.cos(angle);
        this.m00 = 1.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = cosAngle;
        this.m12 = -sinAngle;
        this.m20 = 0.0;
        this.m21 = sinAngle;
        this.m22 = cosAngle;
    }

    public final void rotY(double angle) {
        double cosAngle;
        double sinAngle = Math.sin(angle);
        this.m00 = cosAngle = Math.cos(angle);
        this.m01 = 0.0;
        this.m02 = sinAngle;
        this.m10 = 0.0;
        this.m11 = 1.0;
        this.m12 = 0.0;
        this.m20 = -sinAngle;
        this.m21 = 0.0;
        this.m22 = cosAngle;
    }

    public final void rotZ(double angle) {
        double cosAngle;
        double sinAngle = Math.sin(angle);
        this.m00 = cosAngle = Math.cos(angle);
        this.m01 = -sinAngle;
        this.m02 = 0.0;
        this.m10 = sinAngle;
        this.m11 = cosAngle;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 1.0;
    }

    public final void mul(double scalar) {
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

    public final void mul(double scalar, Matrix3d m1) {
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

    public final void mul(Matrix3d m1) {
        double m00 = this.m00 * m1.m00 + this.m01 * m1.m10 + this.m02 * m1.m20;
        double m01 = this.m00 * m1.m01 + this.m01 * m1.m11 + this.m02 * m1.m21;
        double m02 = this.m00 * m1.m02 + this.m01 * m1.m12 + this.m02 * m1.m22;
        double m10 = this.m10 * m1.m00 + this.m11 * m1.m10 + this.m12 * m1.m20;
        double m11 = this.m10 * m1.m01 + this.m11 * m1.m11 + this.m12 * m1.m21;
        double m12 = this.m10 * m1.m02 + this.m11 * m1.m12 + this.m12 * m1.m22;
        double m20 = this.m20 * m1.m00 + this.m21 * m1.m10 + this.m22 * m1.m20;
        double m21 = this.m20 * m1.m01 + this.m21 * m1.m11 + this.m22 * m1.m21;
        double m22 = this.m20 * m1.m02 + this.m21 * m1.m12 + this.m22 * m1.m22;
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

    public final void mul(Matrix3d m1, Matrix3d m2) {
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
            double m00 = m1.m00 * m2.m00 + m1.m01 * m2.m10 + m1.m02 * m2.m20;
            double m01 = m1.m00 * m2.m01 + m1.m01 * m2.m11 + m1.m02 * m2.m21;
            double m02 = m1.m00 * m2.m02 + m1.m01 * m2.m12 + m1.m02 * m2.m22;
            double m10 = m1.m10 * m2.m00 + m1.m11 * m2.m10 + m1.m12 * m2.m20;
            double m11 = m1.m10 * m2.m01 + m1.m11 * m2.m11 + m1.m12 * m2.m21;
            double m12 = m1.m10 * m2.m02 + m1.m11 * m2.m12 + m1.m12 * m2.m22;
            double m20 = m1.m20 * m2.m00 + m1.m21 * m2.m10 + m1.m22 * m2.m20;
            double m21 = m1.m20 * m2.m01 + m1.m21 * m2.m11 + m1.m22 * m2.m21;
            double m22 = m1.m20 * m2.m02 + m1.m21 * m2.m12 + m1.m22 * m2.m22;
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

    public final void mulNormalize(Matrix3d m1) {
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
        this.m00 = tmp_rot[0];
        this.m01 = tmp_rot[1];
        this.m02 = tmp_rot[2];
        this.m10 = tmp_rot[3];
        this.m11 = tmp_rot[4];
        this.m12 = tmp_rot[5];
        this.m20 = tmp_rot[6];
        this.m21 = tmp_rot[7];
        this.m22 = tmp_rot[8];
    }

    public final void mulNormalize(Matrix3d m1, Matrix3d m2) {
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
        this.m00 = tmp_rot[0];
        this.m01 = tmp_rot[1];
        this.m02 = tmp_rot[2];
        this.m10 = tmp_rot[3];
        this.m11 = tmp_rot[4];
        this.m12 = tmp_rot[5];
        this.m20 = tmp_rot[6];
        this.m21 = tmp_rot[7];
        this.m22 = tmp_rot[8];
    }

    public final void mulTransposeBoth(Matrix3d m1, Matrix3d m2) {
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
            double m00 = m1.m00 * m2.m00 + m1.m10 * m2.m01 + m1.m20 * m2.m02;
            double m01 = m1.m00 * m2.m10 + m1.m10 * m2.m11 + m1.m20 * m2.m12;
            double m02 = m1.m00 * m2.m20 + m1.m10 * m2.m21 + m1.m20 * m2.m22;
            double m10 = m1.m01 * m2.m00 + m1.m11 * m2.m01 + m1.m21 * m2.m02;
            double m11 = m1.m01 * m2.m10 + m1.m11 * m2.m11 + m1.m21 * m2.m12;
            double m12 = m1.m01 * m2.m20 + m1.m11 * m2.m21 + m1.m21 * m2.m22;
            double m20 = m1.m02 * m2.m00 + m1.m12 * m2.m01 + m1.m22 * m2.m02;
            double m21 = m1.m02 * m2.m10 + m1.m12 * m2.m11 + m1.m22 * m2.m12;
            double m22 = m1.m02 * m2.m20 + m1.m12 * m2.m21 + m1.m22 * m2.m22;
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

    public final void mulTransposeRight(Matrix3d m1, Matrix3d m2) {
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
            double m00 = m1.m00 * m2.m00 + m1.m01 * m2.m01 + m1.m02 * m2.m02;
            double m01 = m1.m00 * m2.m10 + m1.m01 * m2.m11 + m1.m02 * m2.m12;
            double m02 = m1.m00 * m2.m20 + m1.m01 * m2.m21 + m1.m02 * m2.m22;
            double m10 = m1.m10 * m2.m00 + m1.m11 * m2.m01 + m1.m12 * m2.m02;
            double m11 = m1.m10 * m2.m10 + m1.m11 * m2.m11 + m1.m12 * m2.m12;
            double m12 = m1.m10 * m2.m20 + m1.m11 * m2.m21 + m1.m12 * m2.m22;
            double m20 = m1.m20 * m2.m00 + m1.m21 * m2.m01 + m1.m22 * m2.m02;
            double m21 = m1.m20 * m2.m10 + m1.m21 * m2.m11 + m1.m22 * m2.m12;
            double m22 = m1.m20 * m2.m20 + m1.m21 * m2.m21 + m1.m22 * m2.m22;
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

    public final void mulTransposeLeft(Matrix3d m1, Matrix3d m2) {
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
            double m00 = m1.m00 * m2.m00 + m1.m10 * m2.m10 + m1.m20 * m2.m20;
            double m01 = m1.m00 * m2.m01 + m1.m10 * m2.m11 + m1.m20 * m2.m21;
            double m02 = m1.m00 * m2.m02 + m1.m10 * m2.m12 + m1.m20 * m2.m22;
            double m10 = m1.m01 * m2.m00 + m1.m11 * m2.m10 + m1.m21 * m2.m20;
            double m11 = m1.m01 * m2.m01 + m1.m11 * m2.m11 + m1.m21 * m2.m21;
            double m12 = m1.m01 * m2.m02 + m1.m11 * m2.m12 + m1.m21 * m2.m22;
            double m20 = m1.m02 * m2.m00 + m1.m12 * m2.m10 + m1.m22 * m2.m20;
            double m21 = m1.m02 * m2.m01 + m1.m12 * m2.m11 + m1.m22 * m2.m21;
            double m22 = m1.m02 * m2.m02 + m1.m12 * m2.m12 + m1.m22 * m2.m22;
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
        this.m00 = tmp_rot[0];
        this.m01 = tmp_rot[1];
        this.m02 = tmp_rot[2];
        this.m10 = tmp_rot[3];
        this.m11 = tmp_rot[4];
        this.m12 = tmp_rot[5];
        this.m20 = tmp_rot[6];
        this.m21 = tmp_rot[7];
        this.m22 = tmp_rot[8];
    }

    public final void normalize(Matrix3d m1) {
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
        this.m00 = tmp_rot[0];
        this.m01 = tmp_rot[1];
        this.m02 = tmp_rot[2];
        this.m10 = tmp_rot[3];
        this.m11 = tmp_rot[4];
        this.m12 = tmp_rot[5];
        this.m20 = tmp_rot[6];
        this.m21 = tmp_rot[7];
        this.m22 = tmp_rot[8];
    }

    public final void normalizeCP() {
        double mag = 1.0 / Math.sqrt(this.m00 * this.m00 + this.m10 * this.m10 + this.m20 * this.m20);
        this.m00 *= mag;
        this.m10 *= mag;
        this.m20 *= mag;
        mag = 1.0 / Math.sqrt(this.m01 * this.m01 + this.m11 * this.m11 + this.m21 * this.m21);
        this.m01 *= mag;
        this.m11 *= mag;
        this.m21 *= mag;
        this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
        this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
        this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
    }

    public final void normalizeCP(Matrix3d m1) {
        double mag = 1.0 / Math.sqrt(m1.m00 * m1.m00 + m1.m10 * m1.m10 + m1.m20 * m1.m20);
        this.m00 = m1.m00 * mag;
        this.m10 = m1.m10 * mag;
        this.m20 = m1.m20 * mag;
        mag = 1.0 / Math.sqrt(m1.m01 * m1.m01 + m1.m11 * m1.m11 + m1.m21 * m1.m21);
        this.m01 = m1.m01 * mag;
        this.m11 = m1.m11 * mag;
        this.m21 = m1.m21 * mag;
        this.m02 = this.m10 * this.m21 - this.m11 * this.m20;
        this.m12 = this.m01 * this.m20 - this.m00 * this.m21;
        this.m22 = this.m00 * this.m11 - this.m01 * this.m10;
    }

    public boolean equals(Matrix3d m1) {
        try {
            return this.m00 == m1.m00 && this.m01 == m1.m01 && this.m02 == m1.m02 && this.m10 == m1.m10 && this.m11 == m1.m11 && this.m12 == m1.m12 && this.m20 == m1.m20 && this.m21 == m1.m21 && this.m22 == m1.m22;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean equals(Object t1) {
        try {
            Matrix3d m2 = (Matrix3d)t1;
            return this.m00 == m2.m00 && this.m01 == m2.m01 && this.m02 == m2.m02 && this.m10 == m2.m10 && this.m11 == m2.m11 && this.m12 == m2.m12 && this.m20 == m2.m20 && this.m21 == m2.m21 && this.m22 == m2.m22;
        }
        catch (ClassCastException e1) {
            return false;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean epsilonEquals(Matrix3d m1, double epsilon) {
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
        diff = this.m10 - m1.m10;
        double d5 = diff < 0.0 ? -diff : diff;
        if (d5 > epsilon) {
            return false;
        }
        diff = this.m11 - m1.m11;
        double d6 = diff < 0.0 ? -diff : diff;
        if (d6 > epsilon) {
            return false;
        }
        diff = this.m12 - m1.m12;
        double d7 = diff < 0.0 ? -diff : diff;
        if (d7 > epsilon) {
            return false;
        }
        diff = this.m20 - m1.m20;
        double d8 = diff < 0.0 ? -diff : diff;
        if (d8 > epsilon) {
            return false;
        }
        diff = this.m21 - m1.m21;
        double d9 = diff < 0.0 ? -diff : diff;
        if (d9 > epsilon) {
            return false;
        }
        diff = this.m22 - m1.m22;
        double d10 = diff < 0.0 ? -diff : diff;
        return !(d10 > epsilon);
    }

    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m00);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m01);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m02);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m10);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m11);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m12);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m20);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m21);
        bits = 31L * bits + VecMathUtil.doubleToLongBits(this.m22);
        return (int)(bits ^ bits >> 32);
    }

    public final void setZero() {
        this.m00 = 0.0;
        this.m01 = 0.0;
        this.m02 = 0.0;
        this.m10 = 0.0;
        this.m11 = 0.0;
        this.m12 = 0.0;
        this.m20 = 0.0;
        this.m21 = 0.0;
        this.m22 = 0.0;
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

    public final void negate(Matrix3d m1) {
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

    public final void transform(Tuple3d t2) {
        double x2 = this.m00 * t2.x + this.m01 * t2.y + this.m02 * t2.z;
        double y2 = this.m10 * t2.x + this.m11 * t2.y + this.m12 * t2.z;
        double z2 = this.m20 * t2.x + this.m21 * t2.y + this.m22 * t2.z;
        t2.set(x2, y2, z2);
    }

    public final void transform(Tuple3d t2, Tuple3d result) {
        double x2 = this.m00 * t2.x + this.m01 * t2.y + this.m02 * t2.z;
        double y2 = this.m10 * t2.x + this.m11 * t2.y + this.m12 * t2.z;
        result.z = this.m20 * t2.x + this.m21 * t2.y + this.m22 * t2.z;
        result.x = x2;
        result.y = y2;
    }

    final void getScaleRotate(double[] scales, double[] rots) {
        double[] tmp = new double[]{this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22};
        Matrix3d.compute_svd(tmp, scales, rots);
    }

    static void compute_svd(double[] m2, double[] outScale, double[] outRot) {
        double g2;
        int i2;
        double[] u1 = new double[9];
        double[] v1 = new double[9];
        double[] t1 = new double[9];
        double[] t2 = new double[9];
        double[] tmp = t1;
        double[] single_values = t2;
        double[] rot = new double[9];
        double[] e2 = new double[3];
        double[] scales = new double[3];
        int negCnt = 0;
        for (i2 = 0; i2 < 9; ++i2) {
            rot[i2] = m2[i2];
        }
        if (m2[3] * m2[3] < 1.110223024E-16) {
            u1[0] = 1.0;
            u1[1] = 0.0;
            u1[2] = 0.0;
            u1[3] = 0.0;
            u1[4] = 1.0;
            u1[5] = 0.0;
            u1[6] = 0.0;
            u1[7] = 0.0;
            u1[8] = 1.0;
        } else if (m2[0] * m2[0] < 1.110223024E-16) {
            tmp[0] = m2[0];
            tmp[1] = m2[1];
            tmp[2] = m2[2];
            m2[0] = m2[3];
            m2[1] = m2[4];
            m2[2] = m2[5];
            m2[3] = -tmp[0];
            m2[4] = -tmp[1];
            m2[5] = -tmp[2];
            u1[0] = 0.0;
            u1[1] = 1.0;
            u1[2] = 0.0;
            u1[3] = -1.0;
            u1[4] = 0.0;
            u1[5] = 0.0;
            u1[6] = 0.0;
            u1[7] = 0.0;
            u1[8] = 1.0;
        } else {
            g2 = 1.0 / Math.sqrt(m2[0] * m2[0] + m2[3] * m2[3]);
            double c1 = m2[0] * g2;
            double s1 = m2[3] * g2;
            tmp[0] = c1 * m2[0] + s1 * m2[3];
            tmp[1] = c1 * m2[1] + s1 * m2[4];
            tmp[2] = c1 * m2[2] + s1 * m2[5];
            m2[3] = -s1 * m2[0] + c1 * m2[3];
            m2[4] = -s1 * m2[1] + c1 * m2[4];
            m2[5] = -s1 * m2[2] + c1 * m2[5];
            m2[0] = tmp[0];
            m2[1] = tmp[1];
            m2[2] = tmp[2];
            u1[0] = c1;
            u1[1] = s1;
            u1[2] = 0.0;
            u1[3] = -s1;
            u1[4] = c1;
            u1[5] = 0.0;
            u1[6] = 0.0;
            u1[7] = 0.0;
            u1[8] = 1.0;
        }
        if (!(m2[6] * m2[6] < 1.110223024E-16)) {
            if (m2[0] * m2[0] < 1.110223024E-16) {
                tmp[0] = m2[0];
                tmp[1] = m2[1];
                tmp[2] = m2[2];
                m2[0] = m2[6];
                m2[1] = m2[7];
                m2[2] = m2[8];
                m2[6] = -tmp[0];
                m2[7] = -tmp[1];
                m2[8] = -tmp[2];
                tmp[0] = u1[0];
                tmp[1] = u1[1];
                tmp[2] = u1[2];
                u1[0] = u1[6];
                u1[1] = u1[7];
                u1[2] = u1[8];
                u1[6] = -tmp[0];
                u1[7] = -tmp[1];
                u1[8] = -tmp[2];
            } else {
                g2 = 1.0 / Math.sqrt(m2[0] * m2[0] + m2[6] * m2[6]);
                double c2 = m2[0] * g2;
                double s2 = m2[6] * g2;
                tmp[0] = c2 * m2[0] + s2 * m2[6];
                tmp[1] = c2 * m2[1] + s2 * m2[7];
                tmp[2] = c2 * m2[2] + s2 * m2[8];
                m2[6] = -s2 * m2[0] + c2 * m2[6];
                m2[7] = -s2 * m2[1] + c2 * m2[7];
                m2[8] = -s2 * m2[2] + c2 * m2[8];
                m2[0] = tmp[0];
                m2[1] = tmp[1];
                m2[2] = tmp[2];
                tmp[0] = c2 * u1[0];
                tmp[1] = c2 * u1[1];
                u1[2] = s2;
                tmp[6] = -u1[0] * s2;
                tmp[7] = -u1[1] * s2;
                u1[8] = c2;
                u1[0] = tmp[0];
                u1[1] = tmp[1];
                u1[6] = tmp[6];
                u1[7] = tmp[7];
            }
        }
        if (m2[2] * m2[2] < 1.110223024E-16) {
            v1[0] = 1.0;
            v1[1] = 0.0;
            v1[2] = 0.0;
            v1[3] = 0.0;
            v1[4] = 1.0;
            v1[5] = 0.0;
            v1[6] = 0.0;
            v1[7] = 0.0;
            v1[8] = 1.0;
        } else if (m2[1] * m2[1] < 1.110223024E-16) {
            tmp[2] = m2[2];
            tmp[5] = m2[5];
            tmp[8] = m2[8];
            m2[2] = -m2[1];
            m2[5] = -m2[4];
            m2[8] = -m2[7];
            m2[1] = tmp[2];
            m2[4] = tmp[5];
            m2[7] = tmp[8];
            v1[0] = 1.0;
            v1[1] = 0.0;
            v1[2] = 0.0;
            v1[3] = 0.0;
            v1[4] = 0.0;
            v1[5] = -1.0;
            v1[6] = 0.0;
            v1[7] = 1.0;
            v1[8] = 0.0;
        } else {
            g2 = 1.0 / Math.sqrt(m2[1] * m2[1] + m2[2] * m2[2]);
            double c3 = m2[1] * g2;
            double s3 = m2[2] * g2;
            tmp[1] = c3 * m2[1] + s3 * m2[2];
            m2[2] = -s3 * m2[1] + c3 * m2[2];
            m2[1] = tmp[1];
            tmp[4] = c3 * m2[4] + s3 * m2[5];
            m2[5] = -s3 * m2[4] + c3 * m2[5];
            m2[4] = tmp[4];
            tmp[7] = c3 * m2[7] + s3 * m2[8];
            m2[8] = -s3 * m2[7] + c3 * m2[8];
            m2[7] = tmp[7];
            v1[0] = 1.0;
            v1[1] = 0.0;
            v1[2] = 0.0;
            v1[3] = 0.0;
            v1[4] = c3;
            v1[5] = -s3;
            v1[6] = 0.0;
            v1[7] = s3;
            v1[8] = c3;
        }
        if (!(m2[7] * m2[7] < 1.110223024E-16)) {
            if (m2[4] * m2[4] < 1.110223024E-16) {
                tmp[3] = m2[3];
                tmp[4] = m2[4];
                tmp[5] = m2[5];
                m2[3] = m2[6];
                m2[4] = m2[7];
                m2[5] = m2[8];
                m2[6] = -tmp[3];
                m2[7] = -tmp[4];
                m2[8] = -tmp[5];
                tmp[3] = u1[3];
                tmp[4] = u1[4];
                tmp[5] = u1[5];
                u1[3] = u1[6];
                u1[4] = u1[7];
                u1[5] = u1[8];
                u1[6] = -tmp[3];
                u1[7] = -tmp[4];
                u1[8] = -tmp[5];
            } else {
                g2 = 1.0 / Math.sqrt(m2[4] * m2[4] + m2[7] * m2[7]);
                double c4 = m2[4] * g2;
                double s4 = m2[7] * g2;
                tmp[3] = c4 * m2[3] + s4 * m2[6];
                m2[6] = -s4 * m2[3] + c4 * m2[6];
                m2[3] = tmp[3];
                tmp[4] = c4 * m2[4] + s4 * m2[7];
                m2[7] = -s4 * m2[4] + c4 * m2[7];
                m2[4] = tmp[4];
                tmp[5] = c4 * m2[5] + s4 * m2[8];
                m2[8] = -s4 * m2[5] + c4 * m2[8];
                m2[5] = tmp[5];
                tmp[3] = c4 * u1[3] + s4 * u1[6];
                u1[6] = -s4 * u1[3] + c4 * u1[6];
                u1[3] = tmp[3];
                tmp[4] = c4 * u1[4] + s4 * u1[7];
                u1[7] = -s4 * u1[4] + c4 * u1[7];
                u1[4] = tmp[4];
                tmp[5] = c4 * u1[5] + s4 * u1[8];
                u1[8] = -s4 * u1[5] + c4 * u1[8];
                u1[5] = tmp[5];
            }
        }
        single_values[0] = m2[0];
        single_values[1] = m2[4];
        single_values[2] = m2[8];
        e2[0] = m2[1];
        e2[1] = m2[5];
        if (!(e2[0] * e2[0] < 1.110223024E-16) || !(e2[1] * e2[1] < 1.110223024E-16)) {
            Matrix3d.compute_qr(single_values, e2, u1, v1);
        }
        scales[0] = single_values[0];
        scales[1] = single_values[1];
        scales[2] = single_values[2];
        if (Matrix3d.almostEqual(Math.abs(scales[0]), 1.0) && Matrix3d.almostEqual(Math.abs(scales[1]), 1.0) && Matrix3d.almostEqual(Math.abs(scales[2]), 1.0)) {
            for (i2 = 0; i2 < 3; ++i2) {
                if (!(scales[i2] < 0.0)) continue;
                ++negCnt;
            }
            if (negCnt == 0 || negCnt == 2) {
                outScale[2] = 1.0;
                outScale[1] = 1.0;
                outScale[0] = 1.0;
                for (i2 = 0; i2 < 9; ++i2) {
                    outRot[i2] = rot[i2];
                }
                return;
            }
        }
        Matrix3d.transpose_mat(u1, t1);
        Matrix3d.transpose_mat(v1, t2);
        Matrix3d.svdReorder(m2, t1, t2, scales, outRot, outScale);
    }

    static void svdReorder(double[] m2, double[] t1, double[] t2, double[] scales, double[] outRot, double[] outScale) {
        int[] out = new int[3];
        int[] in2 = new int[3];
        double[] mag = new double[3];
        double[] rot = new double[9];
        if (scales[0] < 0.0) {
            scales[0] = -scales[0];
            t2[0] = -t2[0];
            t2[1] = -t2[1];
            t2[2] = -t2[2];
        }
        if (scales[1] < 0.0) {
            scales[1] = -scales[1];
            t2[3] = -t2[3];
            t2[4] = -t2[4];
            t2[5] = -t2[5];
        }
        if (scales[2] < 0.0) {
            scales[2] = -scales[2];
            t2[6] = -t2[6];
            t2[7] = -t2[7];
            t2[8] = -t2[8];
        }
        Matrix3d.mat_mul(t1, t2, rot);
        if (Matrix3d.almostEqual(Math.abs(scales[0]), Math.abs(scales[1])) && Matrix3d.almostEqual(Math.abs(scales[1]), Math.abs(scales[2]))) {
            int i2;
            for (i2 = 0; i2 < 9; ++i2) {
                outRot[i2] = rot[i2];
            }
            for (i2 = 0; i2 < 3; ++i2) {
                outScale[i2] = scales[i2];
            }
        } else {
            int in1;
            int in22;
            int in0;
            if (scales[0] > scales[1]) {
                if (scales[0] > scales[2]) {
                    if (scales[2] > scales[1]) {
                        out[0] = 0;
                        out[1] = 2;
                        out[2] = 1;
                    } else {
                        out[0] = 0;
                        out[1] = 1;
                        out[2] = 2;
                    }
                } else {
                    out[0] = 2;
                    out[1] = 0;
                    out[2] = 1;
                }
            } else if (scales[1] > scales[2]) {
                if (scales[2] > scales[0]) {
                    out[0] = 1;
                    out[1] = 2;
                    out[2] = 0;
                } else {
                    out[0] = 1;
                    out[1] = 0;
                    out[2] = 2;
                }
            } else {
                out[0] = 2;
                out[1] = 1;
                out[2] = 0;
            }
            mag[0] = m2[0] * m2[0] + m2[1] * m2[1] + m2[2] * m2[2];
            mag[1] = m2[3] * m2[3] + m2[4] * m2[4] + m2[5] * m2[5];
            mag[2] = m2[6] * m2[6] + m2[7] * m2[7] + m2[8] * m2[8];
            if (mag[0] > mag[1]) {
                if (mag[0] > mag[2]) {
                    if (mag[2] > mag[1]) {
                        in0 = 0;
                        in22 = 1;
                        in1 = 2;
                    } else {
                        in0 = 0;
                        in1 = 1;
                        in22 = 2;
                    }
                } else {
                    in22 = 0;
                    in0 = 1;
                    in1 = 2;
                }
            } else if (mag[1] > mag[2]) {
                if (mag[2] > mag[0]) {
                    in1 = 0;
                    in22 = 1;
                    in0 = 2;
                } else {
                    in1 = 0;
                    in0 = 1;
                    in22 = 2;
                }
            } else {
                in22 = 0;
                in1 = 1;
                in0 = 2;
            }
            int index = out[in0];
            outScale[0] = scales[index];
            index = out[in1];
            outScale[1] = scales[index];
            index = out[in22];
            outScale[2] = scales[index];
            index = out[in0];
            outRot[0] = rot[index];
            index = out[in0] + 3;
            outRot[3] = rot[index];
            index = out[in0] + 6;
            outRot[6] = rot[index];
            index = out[in1];
            outRot[1] = rot[index];
            index = out[in1] + 3;
            outRot[4] = rot[index];
            index = out[in1] + 6;
            outRot[7] = rot[index];
            index = out[in22];
            outRot[2] = rot[index];
            index = out[in22] + 3;
            outRot[5] = rot[index];
            index = out[in22] + 6;
            outRot[8] = rot[index];
        }
    }

    static int compute_qr(double[] s2, double[] e2, double[] u2, double[] v2) {
        double vtemp;
        double utemp;
        double[] cosl = new double[2];
        double[] cosr = new double[2];
        double[] sinl = new double[2];
        double[] sinr = new double[2];
        double[] m2 = new double[9];
        int MAX_INTERATIONS = 10;
        double CONVERGE_TOL = 4.89E-15;
        double c_b48 = 1.0;
        double c_b71 = -1.0;
        boolean converged = false;
        int first = 1;
        if (Math.abs(e2[1]) < 4.89E-15 || Math.abs(e2[0]) < 4.89E-15) {
            converged = true;
        }
        for (int k2 = 0; k2 < 10 && !converged; ++k2) {
            double shift = Matrix3d.compute_shift(s2[1], e2[1], s2[2]);
            double f2 = (Math.abs(s2[0]) - shift) * (Matrix3d.d_sign(c_b48, s2[0]) + shift / s2[0]);
            double g2 = e2[0];
            double r2 = Matrix3d.compute_rot(f2, g2, sinr, cosr, 0, first);
            f2 = cosr[0] * s2[0] + sinr[0] * e2[0];
            e2[0] = cosr[0] * e2[0] - sinr[0] * s2[0];
            g2 = sinr[0] * s2[1];
            s2[1] = cosr[0] * s2[1];
            r2 = Matrix3d.compute_rot(f2, g2, sinl, cosl, 0, first);
            first = 0;
            s2[0] = r2;
            f2 = cosl[0] * e2[0] + sinl[0] * s2[1];
            s2[1] = cosl[0] * s2[1] - sinl[0] * e2[0];
            g2 = sinl[0] * e2[1];
            e2[1] = cosl[0] * e2[1];
            e2[0] = r2 = Matrix3d.compute_rot(f2, g2, sinr, cosr, 1, first);
            f2 = cosr[1] * s2[1] + sinr[1] * e2[1];
            e2[1] = cosr[1] * e2[1] - sinr[1] * s2[1];
            g2 = sinr[1] * s2[2];
            s2[2] = cosr[1] * s2[2];
            s2[1] = r2 = Matrix3d.compute_rot(f2, g2, sinl, cosl, 1, first);
            f2 = cosl[1] * e2[1] + sinl[1] * s2[2];
            s2[2] = cosl[1] * s2[2] - sinl[1] * e2[1];
            e2[1] = f2;
            utemp = u2[0];
            u2[0] = cosl[0] * utemp + sinl[0] * u2[3];
            u2[3] = -sinl[0] * utemp + cosl[0] * u2[3];
            utemp = u2[1];
            u2[1] = cosl[0] * utemp + sinl[0] * u2[4];
            u2[4] = -sinl[0] * utemp + cosl[0] * u2[4];
            utemp = u2[2];
            u2[2] = cosl[0] * utemp + sinl[0] * u2[5];
            u2[5] = -sinl[0] * utemp + cosl[0] * u2[5];
            utemp = u2[3];
            u2[3] = cosl[1] * utemp + sinl[1] * u2[6];
            u2[6] = -sinl[1] * utemp + cosl[1] * u2[6];
            utemp = u2[4];
            u2[4] = cosl[1] * utemp + sinl[1] * u2[7];
            u2[7] = -sinl[1] * utemp + cosl[1] * u2[7];
            utemp = u2[5];
            u2[5] = cosl[1] * utemp + sinl[1] * u2[8];
            u2[8] = -sinl[1] * utemp + cosl[1] * u2[8];
            vtemp = v2[0];
            v2[0] = cosr[0] * vtemp + sinr[0] * v2[1];
            v2[1] = -sinr[0] * vtemp + cosr[0] * v2[1];
            vtemp = v2[3];
            v2[3] = cosr[0] * vtemp + sinr[0] * v2[4];
            v2[4] = -sinr[0] * vtemp + cosr[0] * v2[4];
            vtemp = v2[6];
            v2[6] = cosr[0] * vtemp + sinr[0] * v2[7];
            v2[7] = -sinr[0] * vtemp + cosr[0] * v2[7];
            vtemp = v2[1];
            v2[1] = cosr[1] * vtemp + sinr[1] * v2[2];
            v2[2] = -sinr[1] * vtemp + cosr[1] * v2[2];
            vtemp = v2[4];
            v2[4] = cosr[1] * vtemp + sinr[1] * v2[5];
            v2[5] = -sinr[1] * vtemp + cosr[1] * v2[5];
            vtemp = v2[7];
            v2[7] = cosr[1] * vtemp + sinr[1] * v2[8];
            v2[8] = -sinr[1] * vtemp + cosr[1] * v2[8];
            m2[0] = s2[0];
            m2[1] = e2[0];
            m2[2] = 0.0;
            m2[3] = 0.0;
            m2[4] = s2[1];
            m2[5] = e2[1];
            m2[6] = 0.0;
            m2[7] = 0.0;
            m2[8] = s2[2];
            if (!(Math.abs(e2[1]) < 4.89E-15) && !(Math.abs(e2[0]) < 4.89E-15)) continue;
            converged = true;
        }
        if (Math.abs(e2[1]) < 4.89E-15) {
            Matrix3d.compute_2X2(s2[0], e2[0], s2[1], s2, sinl, cosl, sinr, cosr, 0);
            utemp = u2[0];
            u2[0] = cosl[0] * utemp + sinl[0] * u2[3];
            u2[3] = -sinl[0] * utemp + cosl[0] * u2[3];
            utemp = u2[1];
            u2[1] = cosl[0] * utemp + sinl[0] * u2[4];
            u2[4] = -sinl[0] * utemp + cosl[0] * u2[4];
            utemp = u2[2];
            u2[2] = cosl[0] * utemp + sinl[0] * u2[5];
            u2[5] = -sinl[0] * utemp + cosl[0] * u2[5];
            vtemp = v2[0];
            v2[0] = cosr[0] * vtemp + sinr[0] * v2[1];
            v2[1] = -sinr[0] * vtemp + cosr[0] * v2[1];
            vtemp = v2[3];
            v2[3] = cosr[0] * vtemp + sinr[0] * v2[4];
            v2[4] = -sinr[0] * vtemp + cosr[0] * v2[4];
            vtemp = v2[6];
            v2[6] = cosr[0] * vtemp + sinr[0] * v2[7];
            v2[7] = -sinr[0] * vtemp + cosr[0] * v2[7];
        } else {
            Matrix3d.compute_2X2(s2[1], e2[1], s2[2], s2, sinl, cosl, sinr, cosr, 1);
            utemp = u2[3];
            u2[3] = cosl[0] * utemp + sinl[0] * u2[6];
            u2[6] = -sinl[0] * utemp + cosl[0] * u2[6];
            utemp = u2[4];
            u2[4] = cosl[0] * utemp + sinl[0] * u2[7];
            u2[7] = -sinl[0] * utemp + cosl[0] * u2[7];
            utemp = u2[5];
            u2[5] = cosl[0] * utemp + sinl[0] * u2[8];
            u2[8] = -sinl[0] * utemp + cosl[0] * u2[8];
            vtemp = v2[1];
            v2[1] = cosr[0] * vtemp + sinr[0] * v2[2];
            v2[2] = -sinr[0] * vtemp + cosr[0] * v2[2];
            vtemp = v2[4];
            v2[4] = cosr[0] * vtemp + sinr[0] * v2[5];
            v2[5] = -sinr[0] * vtemp + cosr[0] * v2[5];
            vtemp = v2[7];
            v2[7] = cosr[0] * vtemp + sinr[0] * v2[8];
            v2[8] = -sinr[0] * vtemp + cosr[0] * v2[8];
        }
        return 0;
    }

    static double max(double a2, double b2) {
        if (a2 > b2) {
            return a2;
        }
        return b2;
    }

    static double min(double a2, double b2) {
        if (a2 < b2) {
            return a2;
        }
        return b2;
    }

    static double d_sign(double a2, double b2) {
        double x2 = a2 >= 0.0 ? a2 : -a2;
        return b2 >= 0.0 ? x2 : -x2;
    }

    static double compute_shift(double f2, double g2, double h2) {
        double ssmin;
        double fa2 = Math.abs(f2);
        double ga2 = Math.abs(g2);
        double ha2 = Math.abs(h2);
        double fhmn = Matrix3d.min(fa2, ha2);
        double fhmx = Matrix3d.max(fa2, ha2);
        if (fhmn == 0.0) {
            ssmin = 0.0;
            if (fhmx != 0.0) {
                double d2 = Matrix3d.min(fhmx, ga2) / Matrix3d.max(fhmx, ga2);
            }
        } else if (ga2 < fhmx) {
            double as2 = fhmn / fhmx + 1.0;
            double at2 = (fhmx - fhmn) / fhmx;
            double d__1 = ga2 / fhmx;
            double au2 = d__1 * d__1;
            double c2 = 2.0 / (Math.sqrt(as2 * as2 + au2) + Math.sqrt(at2 * at2 + au2));
            ssmin = fhmn * c2;
        } else {
            double au3 = fhmx / ga2;
            if (au3 == 0.0) {
                ssmin = fhmn * fhmx / ga2;
            } else {
                double as3 = fhmn / fhmx + 1.0;
                double at3 = (fhmx - fhmn) / fhmx;
                double d__1 = as3 * au3;
                double d__2 = at3 * au3;
                double c3 = 1.0 / (Math.sqrt(d__1 * d__1 + 1.0) + Math.sqrt(d__2 * d__2 + 1.0));
                ssmin = fhmn * c3 * au3;
                ssmin += ssmin;
            }
        }
        return ssmin;
    }

    static int compute_2X2(double f2, double g2, double h2, double[] single_values, double[] snl, double[] csl, double[] snr, double[] csr, int index) {
        double gt2;
        double ga2;
        double c_b3 = 2.0;
        double c_b4 = 1.0;
        double ssmax = single_values[0];
        double ssmin = single_values[1];
        double clt = 0.0;
        double crt = 0.0;
        double slt = 0.0;
        double srt = 0.0;
        double tsign = 0.0;
        double ft2 = f2;
        double fa2 = Math.abs(ft2);
        double ht2 = h2;
        double ha2 = Math.abs(h2);
        int pmax = 1;
        boolean swap = ha2 > fa2;
        if (swap) {
            pmax = 3;
            double temp = ft2;
            ft2 = ht2;
            ht2 = temp;
            temp = fa2;
            fa2 = ha2;
            ha2 = temp;
        }
        if ((ga2 = Math.abs(gt2 = g2)) == 0.0) {
            single_values[1] = ha2;
            single_values[0] = fa2;
            clt = 1.0;
            crt = 1.0;
            slt = 0.0;
            srt = 0.0;
        } else {
            boolean gasmal = true;
            if (ga2 > fa2) {
                pmax = 2;
                if (fa2 / ga2 < 1.110223024E-16) {
                    gasmal = false;
                    ssmax = ga2;
                    ssmin = ha2 > 1.0 ? fa2 / (ga2 / ha2) : fa2 / ga2 * ha2;
                    clt = 1.0;
                    slt = ht2 / gt2;
                    srt = 1.0;
                    crt = ft2 / gt2;
                }
            }
            if (gasmal) {
                double d2 = fa2 - ha2;
                double l2 = d2 == fa2 ? 1.0 : d2 / fa2;
                double m2 = gt2 / ft2;
                double t2 = 2.0 - l2;
                double mm = m2 * m2;
                double tt2 = t2 * t2;
                double s2 = Math.sqrt(tt2 + mm);
                double r2 = l2 == 0.0 ? Math.abs(m2) : Math.sqrt(l2 * l2 + mm);
                double a2 = (s2 + r2) * 0.5;
                if (ga2 > fa2) {
                    pmax = 2;
                    if (fa2 / ga2 < 1.110223024E-16) {
                        gasmal = false;
                        ssmax = ga2;
                        ssmin = ha2 > 1.0 ? fa2 / (ga2 / ha2) : fa2 / ga2 * ha2;
                        clt = 1.0;
                        slt = ht2 / gt2;
                        srt = 1.0;
                        crt = ft2 / gt2;
                    }
                }
                if (gasmal) {
                    d2 = fa2 - ha2;
                    l2 = d2 == fa2 ? 1.0 : d2 / fa2;
                    m2 = gt2 / ft2;
                    t2 = 2.0 - l2;
                    mm = m2 * m2;
                    tt2 = t2 * t2;
                    s2 = Math.sqrt(tt2 + mm);
                    r2 = l2 == 0.0 ? Math.abs(m2) : Math.sqrt(l2 * l2 + mm);
                    a2 = (s2 + r2) * 0.5;
                    ssmin = ha2 / a2;
                    ssmax = fa2 * a2;
                    t2 = mm == 0.0 ? (l2 == 0.0 ? Matrix3d.d_sign(c_b3, ft2) * Matrix3d.d_sign(c_b4, gt2) : gt2 / Matrix3d.d_sign(d2, ft2) + m2 / t2) : (m2 / (s2 + t2) + m2 / (r2 + l2)) * (a2 + 1.0);
                    l2 = Math.sqrt(t2 * t2 + 4.0);
                    crt = 2.0 / l2;
                    srt = t2 / l2;
                    clt = (crt + srt * m2) / a2;
                    slt = ht2 / ft2 * srt / a2;
                }
            }
            if (swap) {
                csl[0] = srt;
                snl[0] = crt;
                csr[0] = slt;
                snr[0] = clt;
            } else {
                csl[0] = clt;
                snl[0] = slt;
                csr[0] = crt;
                snr[0] = srt;
            }
            if (pmax == 1) {
                tsign = Matrix3d.d_sign(c_b4, csr[0]) * Matrix3d.d_sign(c_b4, csl[0]) * Matrix3d.d_sign(c_b4, f2);
            }
            if (pmax == 2) {
                tsign = Matrix3d.d_sign(c_b4, snr[0]) * Matrix3d.d_sign(c_b4, csl[0]) * Matrix3d.d_sign(c_b4, g2);
            }
            if (pmax == 3) {
                tsign = Matrix3d.d_sign(c_b4, snr[0]) * Matrix3d.d_sign(c_b4, snl[0]) * Matrix3d.d_sign(c_b4, h2);
            }
            single_values[index] = Matrix3d.d_sign(ssmax, tsign);
            double d__1 = tsign * Matrix3d.d_sign(c_b4, f2) * Matrix3d.d_sign(c_b4, h2);
            single_values[index + 1] = Matrix3d.d_sign(ssmin, d__1);
        }
        return 0;
    }

    static double compute_rot(double f2, double g2, double[] sin, double[] cos, int index, int first) {
        double r2;
        double sn2;
        double cs2;
        double safmn2 = 2.002083095183101E-146;
        double safmx2 = 4.9947976805055876E145;
        if (g2 == 0.0) {
            cs2 = 1.0;
            sn2 = 0.0;
            r2 = f2;
        } else if (f2 == 0.0) {
            cs2 = 0.0;
            sn2 = 1.0;
            r2 = g2;
        } else {
            double f1 = f2;
            double g1 = g2;
            double scale = Matrix3d.max(Math.abs(f1), Math.abs(g1));
            if (scale >= 4.9947976805055876E145) {
                int count = 0;
                while (scale >= 4.9947976805055876E145) {
                    ++count;
                    scale = Matrix3d.max(Math.abs(f1 *= 2.002083095183101E-146), Math.abs(g1 *= 2.002083095183101E-146));
                }
                r2 = Math.sqrt(f1 * f1 + g1 * g1);
                cs2 = f1 / r2;
                sn2 = g1 / r2;
                int i__1 = count;
                for (int i2 = 1; i2 <= count; ++i2) {
                    r2 *= 4.9947976805055876E145;
                }
            } else if (scale <= 2.002083095183101E-146) {
                int count = 0;
                while (scale <= 2.002083095183101E-146) {
                    ++count;
                    scale = Matrix3d.max(Math.abs(f1 *= 4.9947976805055876E145), Math.abs(g1 *= 4.9947976805055876E145));
                }
                r2 = Math.sqrt(f1 * f1 + g1 * g1);
                cs2 = f1 / r2;
                sn2 = g1 / r2;
                int i__1 = count;
                for (int i3 = 1; i3 <= count; ++i3) {
                    r2 *= 2.002083095183101E-146;
                }
            } else {
                r2 = Math.sqrt(f1 * f1 + g1 * g1);
                cs2 = f1 / r2;
                sn2 = g1 / r2;
            }
            if (Math.abs(f2) > Math.abs(g2) && cs2 < 0.0) {
                cs2 = -cs2;
                sn2 = -sn2;
                r2 = -r2;
            }
        }
        sin[index] = sn2;
        cos[index] = cs2;
        return r2;
    }

    static void print_mat(double[] mat) {
        for (int i2 = 0; i2 < 3; ++i2) {
            System.out.println(mat[i2 * 3 + 0] + " " + mat[i2 * 3 + 1] + " " + mat[i2 * 3 + 2] + "\n");
        }
    }

    static void print_det(double[] mat) {
        double det = mat[0] * mat[4] * mat[8] + mat[1] * mat[5] * mat[6] + mat[2] * mat[3] * mat[7] - mat[2] * mat[4] * mat[6] - mat[0] * mat[5] * mat[7] - mat[1] * mat[3] * mat[8];
        System.out.println("det= " + det);
    }

    static void mat_mul(double[] m1, double[] m2, double[] m3) {
        double[] tmp = new double[]{m1[0] * m2[0] + m1[1] * m2[3] + m1[2] * m2[6], m1[0] * m2[1] + m1[1] * m2[4] + m1[2] * m2[7], m1[0] * m2[2] + m1[1] * m2[5] + m1[2] * m2[8], m1[3] * m2[0] + m1[4] * m2[3] + m1[5] * m2[6], m1[3] * m2[1] + m1[4] * m2[4] + m1[5] * m2[7], m1[3] * m2[2] + m1[4] * m2[5] + m1[5] * m2[8], m1[6] * m2[0] + m1[7] * m2[3] + m1[8] * m2[6], m1[6] * m2[1] + m1[7] * m2[4] + m1[8] * m2[7], m1[6] * m2[2] + m1[7] * m2[5] + m1[8] * m2[8]};
        for (int i2 = 0; i2 < 9; ++i2) {
            m3[i2] = tmp[i2];
        }
    }

    static void transpose_mat(double[] in2, double[] out) {
        out[0] = in2[0];
        out[1] = in2[3];
        out[2] = in2[6];
        out[3] = in2[1];
        out[4] = in2[4];
        out[5] = in2[7];
        out[6] = in2[2];
        out[7] = in2[5];
        out[8] = in2[8];
    }

    static double max3(double[] values) {
        if (values[0] > values[1]) {
            if (values[0] > values[2]) {
                return values[0];
            }
            return values[2];
        }
        if (values[1] > values[2]) {
            return values[1];
        }
        return values[2];
    }

    private static final boolean almostEqual(double a2, double b2) {
        double absB;
        double max;
        if (a2 == b2) {
            return true;
        }
        double EPSILON_ABSOLUTE = 1.0E-6;
        double EPSILON_RELATIVE = 1.0E-4;
        double diff = Math.abs(a2 - b2);
        double absA = Math.abs(a2);
        double d2 = max = absA >= (absB = Math.abs(b2)) ? absA : absB;
        if (diff < 1.0E-6) {
            return true;
        }
        return diff / max < 1.0E-4;
    }

    public Object clone() {
        Matrix3d m1 = null;
        try {
            m1 = (Matrix3d)super.clone();
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
}

