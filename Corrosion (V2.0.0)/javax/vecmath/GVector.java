/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.GMatrix;
import javax.vecmath.MismatchedSizeException;
import javax.vecmath.Tuple2f;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4d;
import javax.vecmath.Tuple4f;
import javax.vecmath.VecMathI18N;
import javax.vecmath.VecMathUtil;

public class GVector
implements Serializable,
Cloneable {
    private int length;
    double[] values;
    static final long serialVersionUID = 1398850036893875112L;

    public GVector(int length) {
        this.length = length;
        this.values = new double[length];
        for (int i2 = 0; i2 < length; ++i2) {
            this.values[i2] = 0.0;
        }
    }

    public GVector(double[] vector) {
        this.length = vector.length;
        this.values = new double[vector.length];
        for (int i2 = 0; i2 < this.length; ++i2) {
            this.values[i2] = vector[i2];
        }
    }

    public GVector(GVector vector) {
        this.values = new double[vector.length];
        this.length = vector.length;
        for (int i2 = 0; i2 < this.length; ++i2) {
            this.values[i2] = vector.values[i2];
        }
    }

    public GVector(Tuple2f tuple) {
        this.values = new double[2];
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.length = 2;
    }

    public GVector(Tuple3f tuple) {
        this.values = new double[3];
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        this.length = 3;
    }

    public GVector(Tuple3d tuple) {
        this.values = new double[3];
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        this.length = 3;
    }

    public GVector(Tuple4f tuple) {
        this.values = new double[4];
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        this.values[3] = tuple.w;
        this.length = 4;
    }

    public GVector(Tuple4d tuple) {
        this.values = new double[4];
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        this.values[3] = tuple.w;
        this.length = 4;
    }

    public GVector(double[] vector, int length) {
        this.length = length;
        this.values = new double[length];
        for (int i2 = 0; i2 < length; ++i2) {
            this.values[i2] = vector[i2];
        }
    }

    public final double norm() {
        double sq2 = 0.0;
        for (int i2 = 0; i2 < this.length; ++i2) {
            sq2 += this.values[i2] * this.values[i2];
        }
        return Math.sqrt(sq2);
    }

    public final double normSquared() {
        double sq2 = 0.0;
        for (int i2 = 0; i2 < this.length; ++i2) {
            sq2 += this.values[i2] * this.values[i2];
        }
        return sq2;
    }

    public final void normalize(GVector v1) {
        int i2;
        double sq2 = 0.0;
        if (this.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector0"));
        }
        for (i2 = 0; i2 < this.length; ++i2) {
            sq2 += v1.values[i2] * v1.values[i2];
        }
        double invMag = 1.0 / Math.sqrt(sq2);
        for (i2 = 0; i2 < this.length; ++i2) {
            this.values[i2] = v1.values[i2] * invMag;
        }
    }

    public final void normalize() {
        int i2;
        double sq2 = 0.0;
        for (i2 = 0; i2 < this.length; ++i2) {
            sq2 += this.values[i2] * this.values[i2];
        }
        double invMag = 1.0 / Math.sqrt(sq2);
        for (i2 = 0; i2 < this.length; ++i2) {
            this.values[i2] = this.values[i2] * invMag;
        }
    }

    public final void scale(double s2, GVector v1) {
        if (this.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector1"));
        }
        for (int i2 = 0; i2 < this.length; ++i2) {
            this.values[i2] = v1.values[i2] * s2;
        }
    }

    public final void scale(double s2) {
        for (int i2 = 0; i2 < this.length; ++i2) {
            this.values[i2] = this.values[i2] * s2;
        }
    }

    public final void scaleAdd(double s2, GVector v1, GVector v2) {
        if (v2.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector2"));
        }
        if (this.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector3"));
        }
        for (int i2 = 0; i2 < this.length; ++i2) {
            this.values[i2] = v1.values[i2] * s2 + v2.values[i2];
        }
    }

    public final void add(GVector vector) {
        if (this.length != vector.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector4"));
        }
        for (int i2 = 0; i2 < this.length; ++i2) {
            int n2 = i2;
            this.values[n2] = this.values[n2] + vector.values[i2];
        }
    }

    public final void add(GVector vector1, GVector vector2) {
        if (vector1.length != vector2.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector5"));
        }
        if (this.length != vector1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector6"));
        }
        for (int i2 = 0; i2 < this.length; ++i2) {
            this.values[i2] = vector1.values[i2] + vector2.values[i2];
        }
    }

    public final void sub(GVector vector) {
        if (this.length != vector.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector7"));
        }
        for (int i2 = 0; i2 < this.length; ++i2) {
            int n2 = i2;
            this.values[n2] = this.values[n2] - vector.values[i2];
        }
    }

    public final void sub(GVector vector1, GVector vector2) {
        if (vector1.length != vector2.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector8"));
        }
        if (this.length != vector1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector9"));
        }
        for (int i2 = 0; i2 < this.length; ++i2) {
            this.values[i2] = vector1.values[i2] - vector2.values[i2];
        }
    }

    public final void mul(GMatrix m1, GVector v1) {
        if (m1.getNumCol() != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector10"));
        }
        if (this.length != m1.getNumRow()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector11"));
        }
        double[] v2 = v1 != this ? v1.values : (double[])this.values.clone();
        for (int j2 = this.length - 1; j2 >= 0; --j2) {
            this.values[j2] = 0.0;
            for (int i2 = v1.length - 1; i2 >= 0; --i2) {
                int n2 = j2;
                this.values[n2] = this.values[n2] + m1.values[j2][i2] * v2[i2];
            }
        }
    }

    public final void mul(GVector v1, GMatrix m1) {
        if (m1.getNumRow() != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector12"));
        }
        if (this.length != m1.getNumCol()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector13"));
        }
        double[] v2 = v1 != this ? v1.values : (double[])this.values.clone();
        for (int j2 = this.length - 1; j2 >= 0; --j2) {
            this.values[j2] = 0.0;
            for (int i2 = v1.length - 1; i2 >= 0; --i2) {
                int n2 = j2;
                this.values[n2] = this.values[n2] + m1.values[i2][j2] * v2[i2];
            }
        }
    }

    public final void negate() {
        int i2 = this.length - 1;
        while (i2 >= 0) {
            int n2 = i2--;
            this.values[n2] = this.values[n2] * -1.0;
        }
    }

    public final void zero() {
        for (int i2 = 0; i2 < this.length; ++i2) {
            this.values[i2] = 0.0;
        }
    }

    public final void setSize(int length) {
        double[] tmp = new double[length];
        int max = this.length < length ? this.length : length;
        for (int i2 = 0; i2 < max; ++i2) {
            tmp[i2] = this.values[i2];
        }
        this.length = length;
        this.values = tmp;
    }

    public final void set(double[] vector) {
        for (int i2 = this.length - 1; i2 >= 0; --i2) {
            this.values[i2] = vector[i2];
        }
    }

    public final void set(GVector vector) {
        if (this.length < vector.length) {
            this.length = vector.length;
            this.values = new double[this.length];
            for (int i2 = 0; i2 < this.length; ++i2) {
                this.values[i2] = vector.values[i2];
            }
        } else {
            int i3;
            for (i3 = 0; i3 < vector.length; ++i3) {
                this.values[i3] = vector.values[i3];
            }
            for (i3 = vector.length; i3 < this.length; ++i3) {
                this.values[i3] = 0.0;
            }
        }
    }

    public final void set(Tuple2f tuple) {
        if (this.length < 2) {
            this.length = 2;
            this.values = new double[2];
        }
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        for (int i2 = 2; i2 < this.length; ++i2) {
            this.values[i2] = 0.0;
        }
    }

    public final void set(Tuple3f tuple) {
        if (this.length < 3) {
            this.length = 3;
            this.values = new double[3];
        }
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        for (int i2 = 3; i2 < this.length; ++i2) {
            this.values[i2] = 0.0;
        }
    }

    public final void set(Tuple3d tuple) {
        if (this.length < 3) {
            this.length = 3;
            this.values = new double[3];
        }
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        for (int i2 = 3; i2 < this.length; ++i2) {
            this.values[i2] = 0.0;
        }
    }

    public final void set(Tuple4f tuple) {
        if (this.length < 4) {
            this.length = 4;
            this.values = new double[4];
        }
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        this.values[3] = tuple.w;
        for (int i2 = 4; i2 < this.length; ++i2) {
            this.values[i2] = 0.0;
        }
    }

    public final void set(Tuple4d tuple) {
        if (this.length < 4) {
            this.length = 4;
            this.values = new double[4];
        }
        this.values[0] = tuple.x;
        this.values[1] = tuple.y;
        this.values[2] = tuple.z;
        this.values[3] = tuple.w;
        for (int i2 = 4; i2 < this.length; ++i2) {
            this.values[i2] = 0.0;
        }
    }

    public final int getSize() {
        return this.values.length;
    }

    public final double getElement(int index) {
        return this.values[index];
    }

    public final void setElement(int index, double value) {
        this.values[index] = value;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(this.length * 8);
        for (int i2 = 0; i2 < this.length; ++i2) {
            buffer.append(this.values[i2]).append(" ");
        }
        return buffer.toString();
    }

    public int hashCode() {
        long bits = 1L;
        for (int i2 = 0; i2 < this.length; ++i2) {
            bits = 31L * bits + VecMathUtil.doubleToLongBits(this.values[i2]);
        }
        return (int)(bits ^ bits >> 32);
    }

    public boolean equals(GVector vector1) {
        try {
            if (this.length != vector1.length) {
                return false;
            }
            for (int i2 = 0; i2 < this.length; ++i2) {
                if (this.values[i2] == vector1.values[i2]) continue;
                return false;
            }
            return true;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean equals(Object o1) {
        try {
            GVector v2 = (GVector)o1;
            if (this.length != v2.length) {
                return false;
            }
            for (int i2 = 0; i2 < this.length; ++i2) {
                if (this.values[i2] == v2.values[i2]) continue;
                return false;
            }
            return true;
        }
        catch (ClassCastException e1) {
            return false;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean epsilonEquals(GVector v1, double epsilon) {
        if (this.length != v1.length) {
            return false;
        }
        for (int i2 = 0; i2 < this.length; ++i2) {
            double diff = this.values[i2] - v1.values[i2];
            double d2 = diff < 0.0 ? -diff : diff;
            if (!(d2 > epsilon)) continue;
            return false;
        }
        return true;
    }

    public final double dot(GVector v1) {
        if (this.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector14"));
        }
        double result = 0.0;
        for (int i2 = 0; i2 < this.length; ++i2) {
            result += this.values[i2] * v1.values[i2];
        }
        return result;
    }

    public final void SVDBackSolve(GMatrix U, GMatrix W, GMatrix V, GVector b2) {
        if (U.nRow != b2.getSize() || U.nRow != U.nCol || U.nRow != W.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector15"));
        }
        if (W.nCol != this.values.length || W.nCol != V.nCol || W.nCol != V.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector23"));
        }
        GMatrix tmp = new GMatrix(U.nRow, W.nCol);
        tmp.mul(U, V);
        tmp.mulTransposeRight(U, W);
        tmp.invert();
        this.mul(tmp, b2);
    }

    public final void LUDBackSolve(GMatrix LU, GVector b2, GVector permutation) {
        int i2;
        int size = LU.nRow * LU.nCol;
        double[] temp = new double[size];
        double[] result = new double[size];
        int[] row_perm = new int[b2.getSize()];
        if (LU.nRow != b2.getSize()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector16"));
        }
        if (LU.nRow != permutation.getSize()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector24"));
        }
        if (LU.nRow != LU.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector25"));
        }
        for (i2 = 0; i2 < LU.nRow; ++i2) {
            for (int j2 = 0; j2 < LU.nCol; ++j2) {
                temp[i2 * LU.nCol + j2] = LU.values[i2][j2];
            }
        }
        for (i2 = 0; i2 < size; ++i2) {
            result[i2] = 0.0;
        }
        for (i2 = 0; i2 < LU.nRow; ++i2) {
            result[i2 * LU.nCol] = b2.values[i2];
        }
        for (i2 = 0; i2 < LU.nCol; ++i2) {
            row_perm[i2] = (int)permutation.values[i2];
        }
        GMatrix.luBacksubstitution(LU.nRow, temp, row_perm, result);
        for (i2 = 0; i2 < LU.nRow; ++i2) {
            this.values[i2] = result[i2 * LU.nCol];
        }
    }

    public final double angle(GVector v1) {
        return Math.acos(this.dot(v1) / (this.norm() * v1.norm()));
    }

    public final void interpolate(GVector v1, GVector v2, float alpha) {
        this.interpolate(v1, v2, (double)alpha);
    }

    public final void interpolate(GVector v1, float alpha) {
        this.interpolate(v1, (double)alpha);
    }

    public final void interpolate(GVector v1, GVector v2, double alpha) {
        if (v2.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector20"));
        }
        if (this.length != v1.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector21"));
        }
        for (int i2 = 0; i2 < this.length; ++i2) {
            this.values[i2] = (1.0 - alpha) * v1.values[i2] + alpha * v2.values[i2];
        }
    }

    public final void interpolate(GVector v1, double alpha) {
        if (v1.length != this.length) {
            throw new MismatchedSizeException(VecMathI18N.getString("GVector22"));
        }
        for (int i2 = 0; i2 < this.length; ++i2) {
            this.values[i2] = (1.0 - alpha) * this.values[i2] + alpha * v1.values[i2];
        }
    }

    public Object clone() {
        GVector v1 = null;
        try {
            v1 = (GVector)super.clone();
        }
        catch (CloneNotSupportedException e2) {
            throw new InternalError();
        }
        v1.values = new double[this.length];
        for (int i2 = 0; i2 < this.length; ++i2) {
            v1.values[i2] = this.values[i2];
        }
        return v1;
    }
}

