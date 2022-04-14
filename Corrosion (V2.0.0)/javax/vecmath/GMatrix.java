/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.GVector;
import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.MismatchedSizeException;
import javax.vecmath.SingularMatrixException;
import javax.vecmath.VecMathI18N;
import javax.vecmath.VecMathUtil;

public class GMatrix
implements Serializable,
Cloneable {
    static final long serialVersionUID = 2777097312029690941L;
    private static final boolean debug = false;
    int nRow;
    int nCol;
    double[][] values;
    private static final double EPS = 1.0E-10;

    public GMatrix(int nRow, int nCol) {
        int i2;
        this.values = new double[nRow][nCol];
        this.nRow = nRow;
        this.nCol = nCol;
        for (i2 = 0; i2 < nRow; ++i2) {
            for (int j2 = 0; j2 < nCol; ++j2) {
                this.values[i2][j2] = 0.0;
            }
        }
        int l2 = nRow < nCol ? nRow : nCol;
        for (i2 = 0; i2 < l2; ++i2) {
            this.values[i2][i2] = 1.0;
        }
    }

    public GMatrix(int nRow, int nCol, double[] matrix) {
        this.values = new double[nRow][nCol];
        this.nRow = nRow;
        this.nCol = nCol;
        for (int i2 = 0; i2 < nRow; ++i2) {
            for (int j2 = 0; j2 < nCol; ++j2) {
                this.values[i2][j2] = matrix[i2 * nCol + j2];
            }
        }
    }

    public GMatrix(GMatrix matrix) {
        this.nRow = matrix.nRow;
        this.nCol = matrix.nCol;
        this.values = new double[this.nRow][this.nCol];
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = matrix.values[i2][j2];
            }
        }
    }

    public final void mul(GMatrix m1) {
        if (this.nCol != m1.nRow || this.nCol != m1.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix0"));
        }
        double[][] tmp = new double[this.nRow][this.nCol];
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                tmp[i2][j2] = 0.0;
                for (int k2 = 0; k2 < this.nCol; ++k2) {
                    double[] dArray = tmp[i2];
                    int n2 = j2;
                    dArray[n2] = dArray[n2] + this.values[i2][k2] * m1.values[k2][j2];
                }
            }
        }
        this.values = tmp;
    }

    public final void mul(GMatrix m1, GMatrix m2) {
        if (m1.nCol != m2.nRow || this.nRow != m1.nRow || this.nCol != m2.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix1"));
        }
        double[][] tmp = new double[this.nRow][this.nCol];
        for (int i2 = 0; i2 < m1.nRow; ++i2) {
            for (int j2 = 0; j2 < m2.nCol; ++j2) {
                tmp[i2][j2] = 0.0;
                for (int k2 = 0; k2 < m1.nCol; ++k2) {
                    double[] dArray = tmp[i2];
                    int n2 = j2;
                    dArray[n2] = dArray[n2] + m1.values[i2][k2] * m2.values[k2][j2];
                }
            }
        }
        this.values = tmp;
    }

    public final void mul(GVector v1, GVector v2) {
        if (this.nRow < v1.getSize()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix2"));
        }
        if (this.nCol < v2.getSize()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix3"));
        }
        for (int i2 = 0; i2 < v1.getSize(); ++i2) {
            for (int j2 = 0; j2 < v2.getSize(); ++j2) {
                this.values[i2][j2] = v1.values[i2] * v2.values[j2];
            }
        }
    }

    public final void add(GMatrix m1) {
        if (this.nRow != m1.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix4"));
        }
        if (this.nCol != m1.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix5"));
        }
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = this.values[i2][j2] + m1.values[i2][j2];
            }
        }
    }

    public final void add(GMatrix m1, GMatrix m2) {
        if (m2.nRow != m1.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix6"));
        }
        if (m2.nCol != m1.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix7"));
        }
        if (this.nCol != m1.nCol || this.nRow != m1.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix8"));
        }
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = m1.values[i2][j2] + m2.values[i2][j2];
            }
        }
    }

    public final void sub(GMatrix m1) {
        if (this.nRow != m1.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix9"));
        }
        if (this.nCol != m1.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix28"));
        }
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = this.values[i2][j2] - m1.values[i2][j2];
            }
        }
    }

    public final void sub(GMatrix m1, GMatrix m2) {
        if (m2.nRow != m1.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix10"));
        }
        if (m2.nCol != m1.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix11"));
        }
        if (this.nRow != m1.nRow || this.nCol != m1.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix12"));
        }
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = m1.values[i2][j2] - m2.values[i2][j2];
            }
        }
    }

    public final void negate() {
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = -this.values[i2][j2];
            }
        }
    }

    public final void negate(GMatrix m1) {
        if (this.nRow != m1.nRow || this.nCol != m1.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix13"));
        }
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = -m1.values[i2][j2];
            }
        }
    }

    public final void setIdentity() {
        int i2;
        for (i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = 0.0;
            }
        }
        int l2 = this.nRow < this.nCol ? this.nRow : this.nCol;
        for (i2 = 0; i2 < l2; ++i2) {
            this.values[i2][i2] = 1.0;
        }
    }

    public final void setZero() {
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = 0.0;
            }
        }
    }

    public final void identityMinus() {
        int i2;
        for (i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = -this.values[i2][j2];
            }
        }
        int l2 = this.nRow < this.nCol ? this.nRow : this.nCol;
        i2 = 0;
        while (i2 < l2) {
            double[] dArray = this.values[i2];
            int n2 = i2++;
            dArray[n2] = dArray[n2] + 1.0;
        }
    }

    public final void invert() {
        this.invertGeneral(this);
    }

    public final void invert(GMatrix m1) {
        this.invertGeneral(m1);
    }

    public final void copySubMatrix(int rowSource, int colSource, int numRow, int numCol, int rowDest, int colDest, GMatrix target) {
        if (this != target) {
            for (int i2 = 0; i2 < numRow; ++i2) {
                for (int j2 = 0; j2 < numCol; ++j2) {
                    target.values[rowDest + i2][colDest + j2] = this.values[rowSource + i2][colSource + j2];
                }
            }
        } else {
            int j3;
            int i3;
            double[][] tmp = new double[numRow][numCol];
            for (i3 = 0; i3 < numRow; ++i3) {
                for (j3 = 0; j3 < numCol; ++j3) {
                    tmp[i3][j3] = this.values[rowSource + i3][colSource + j3];
                }
            }
            for (i3 = 0; i3 < numRow; ++i3) {
                for (j3 = 0; j3 < numCol; ++j3) {
                    target.values[rowDest + i3][colDest + j3] = tmp[i3][j3];
                }
            }
        }
    }

    public final void setSize(int nRow, int nCol) {
        double[][] tmp = new double[nRow][nCol];
        int maxRow = this.nRow < nRow ? this.nRow : nRow;
        int maxCol = this.nCol < nCol ? this.nCol : nCol;
        for (int i2 = 0; i2 < maxRow; ++i2) {
            for (int j2 = 0; j2 < maxCol; ++j2) {
                tmp[i2][j2] = this.values[i2][j2];
            }
        }
        this.nRow = nRow;
        this.nCol = nCol;
        this.values = tmp;
    }

    public final void set(double[] matrix) {
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = matrix[this.nCol * i2 + j2];
            }
        }
    }

    public final void set(Matrix3f m1) {
        if (this.nCol < 3 || this.nRow < 3) {
            this.nCol = 3;
            this.nRow = 3;
            this.values = new double[this.nRow][this.nCol];
        }
        this.values[0][0] = m1.m00;
        this.values[0][1] = m1.m01;
        this.values[0][2] = m1.m02;
        this.values[1][0] = m1.m10;
        this.values[1][1] = m1.m11;
        this.values[1][2] = m1.m12;
        this.values[2][0] = m1.m20;
        this.values[2][1] = m1.m21;
        this.values[2][2] = m1.m22;
        for (int i2 = 3; i2 < this.nRow; ++i2) {
            for (int j2 = 3; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = 0.0;
            }
        }
    }

    public final void set(Matrix3d m1) {
        if (this.nRow < 3 || this.nCol < 3) {
            this.values = new double[3][3];
            this.nRow = 3;
            this.nCol = 3;
        }
        this.values[0][0] = m1.m00;
        this.values[0][1] = m1.m01;
        this.values[0][2] = m1.m02;
        this.values[1][0] = m1.m10;
        this.values[1][1] = m1.m11;
        this.values[1][2] = m1.m12;
        this.values[2][0] = m1.m20;
        this.values[2][1] = m1.m21;
        this.values[2][2] = m1.m22;
        for (int i2 = 3; i2 < this.nRow; ++i2) {
            for (int j2 = 3; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = 0.0;
            }
        }
    }

    public final void set(Matrix4f m1) {
        if (this.nRow < 4 || this.nCol < 4) {
            this.values = new double[4][4];
            this.nRow = 4;
            this.nCol = 4;
        }
        this.values[0][0] = m1.m00;
        this.values[0][1] = m1.m01;
        this.values[0][2] = m1.m02;
        this.values[0][3] = m1.m03;
        this.values[1][0] = m1.m10;
        this.values[1][1] = m1.m11;
        this.values[1][2] = m1.m12;
        this.values[1][3] = m1.m13;
        this.values[2][0] = m1.m20;
        this.values[2][1] = m1.m21;
        this.values[2][2] = m1.m22;
        this.values[2][3] = m1.m23;
        this.values[3][0] = m1.m30;
        this.values[3][1] = m1.m31;
        this.values[3][2] = m1.m32;
        this.values[3][3] = m1.m33;
        for (int i2 = 4; i2 < this.nRow; ++i2) {
            for (int j2 = 4; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = 0.0;
            }
        }
    }

    public final void set(Matrix4d m1) {
        if (this.nRow < 4 || this.nCol < 4) {
            this.values = new double[4][4];
            this.nRow = 4;
            this.nCol = 4;
        }
        this.values[0][0] = m1.m00;
        this.values[0][1] = m1.m01;
        this.values[0][2] = m1.m02;
        this.values[0][3] = m1.m03;
        this.values[1][0] = m1.m10;
        this.values[1][1] = m1.m11;
        this.values[1][2] = m1.m12;
        this.values[1][3] = m1.m13;
        this.values[2][0] = m1.m20;
        this.values[2][1] = m1.m21;
        this.values[2][2] = m1.m22;
        this.values[2][3] = m1.m23;
        this.values[3][0] = m1.m30;
        this.values[3][1] = m1.m31;
        this.values[3][2] = m1.m32;
        this.values[3][3] = m1.m33;
        for (int i2 = 4; i2 < this.nRow; ++i2) {
            for (int j2 = 4; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = 0.0;
            }
        }
    }

    public final void set(GMatrix m1) {
        int j2;
        int i2;
        if (this.nRow < m1.nRow || this.nCol < m1.nCol) {
            this.nRow = m1.nRow;
            this.nCol = m1.nCol;
            this.values = new double[this.nRow][this.nCol];
        }
        for (i2 = 0; i2 < Math.min(this.nRow, m1.nRow); ++i2) {
            for (j2 = 0; j2 < Math.min(this.nCol, m1.nCol); ++j2) {
                this.values[i2][j2] = m1.values[i2][j2];
            }
        }
        for (i2 = m1.nRow; i2 < this.nRow; ++i2) {
            for (j2 = m1.nCol; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = 0.0;
            }
        }
    }

    public final int getNumRow() {
        return this.nRow;
    }

    public final int getNumCol() {
        return this.nCol;
    }

    public final double getElement(int row, int column) {
        return this.values[row][column];
    }

    public final void setElement(int row, int column, double value) {
        this.values[row][column] = value;
    }

    public final void getRow(int row, double[] array) {
        for (int i2 = 0; i2 < this.nCol; ++i2) {
            array[i2] = this.values[row][i2];
        }
    }

    public final void getRow(int row, GVector vector) {
        if (vector.getSize() < this.nCol) {
            vector.setSize(this.nCol);
        }
        for (int i2 = 0; i2 < this.nCol; ++i2) {
            vector.values[i2] = this.values[row][i2];
        }
    }

    public final void getColumn(int col, double[] array) {
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            array[i2] = this.values[i2][col];
        }
    }

    public final void getColumn(int col, GVector vector) {
        if (vector.getSize() < this.nRow) {
            vector.setSize(this.nRow);
        }
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            vector.values[i2] = this.values[i2][col];
        }
    }

    public final void get(Matrix3d m1) {
        if (this.nRow < 3 || this.nCol < 3) {
            m1.setZero();
            if (this.nCol > 0) {
                if (this.nRow > 0) {
                    m1.m00 = this.values[0][0];
                    if (this.nRow > 1) {
                        m1.m10 = this.values[1][0];
                        if (this.nRow > 2) {
                            m1.m20 = this.values[2][0];
                        }
                    }
                }
                if (this.nCol > 1) {
                    if (this.nRow > 0) {
                        m1.m01 = this.values[0][1];
                        if (this.nRow > 1) {
                            m1.m11 = this.values[1][1];
                            if (this.nRow > 2) {
                                m1.m21 = this.values[2][1];
                            }
                        }
                    }
                    if (this.nCol > 2 && this.nRow > 0) {
                        m1.m02 = this.values[0][2];
                        if (this.nRow > 1) {
                            m1.m12 = this.values[1][2];
                            if (this.nRow > 2) {
                                m1.m22 = this.values[2][2];
                            }
                        }
                    }
                }
            }
        } else {
            m1.m00 = this.values[0][0];
            m1.m01 = this.values[0][1];
            m1.m02 = this.values[0][2];
            m1.m10 = this.values[1][0];
            m1.m11 = this.values[1][1];
            m1.m12 = this.values[1][2];
            m1.m20 = this.values[2][0];
            m1.m21 = this.values[2][1];
            m1.m22 = this.values[2][2];
        }
    }

    public final void get(Matrix3f m1) {
        if (this.nRow < 3 || this.nCol < 3) {
            m1.setZero();
            if (this.nCol > 0) {
                if (this.nRow > 0) {
                    m1.m00 = (float)this.values[0][0];
                    if (this.nRow > 1) {
                        m1.m10 = (float)this.values[1][0];
                        if (this.nRow > 2) {
                            m1.m20 = (float)this.values[2][0];
                        }
                    }
                }
                if (this.nCol > 1) {
                    if (this.nRow > 0) {
                        m1.m01 = (float)this.values[0][1];
                        if (this.nRow > 1) {
                            m1.m11 = (float)this.values[1][1];
                            if (this.nRow > 2) {
                                m1.m21 = (float)this.values[2][1];
                            }
                        }
                    }
                    if (this.nCol > 2 && this.nRow > 0) {
                        m1.m02 = (float)this.values[0][2];
                        if (this.nRow > 1) {
                            m1.m12 = (float)this.values[1][2];
                            if (this.nRow > 2) {
                                m1.m22 = (float)this.values[2][2];
                            }
                        }
                    }
                }
            }
        } else {
            m1.m00 = (float)this.values[0][0];
            m1.m01 = (float)this.values[0][1];
            m1.m02 = (float)this.values[0][2];
            m1.m10 = (float)this.values[1][0];
            m1.m11 = (float)this.values[1][1];
            m1.m12 = (float)this.values[1][2];
            m1.m20 = (float)this.values[2][0];
            m1.m21 = (float)this.values[2][1];
            m1.m22 = (float)this.values[2][2];
        }
    }

    public final void get(Matrix4d m1) {
        if (this.nRow < 4 || this.nCol < 4) {
            m1.setZero();
            if (this.nCol > 0) {
                if (this.nRow > 0) {
                    m1.m00 = this.values[0][0];
                    if (this.nRow > 1) {
                        m1.m10 = this.values[1][0];
                        if (this.nRow > 2) {
                            m1.m20 = this.values[2][0];
                            if (this.nRow > 3) {
                                m1.m30 = this.values[3][0];
                            }
                        }
                    }
                }
                if (this.nCol > 1) {
                    if (this.nRow > 0) {
                        m1.m01 = this.values[0][1];
                        if (this.nRow > 1) {
                            m1.m11 = this.values[1][1];
                            if (this.nRow > 2) {
                                m1.m21 = this.values[2][1];
                                if (this.nRow > 3) {
                                    m1.m31 = this.values[3][1];
                                }
                            }
                        }
                    }
                    if (this.nCol > 2) {
                        if (this.nRow > 0) {
                            m1.m02 = this.values[0][2];
                            if (this.nRow > 1) {
                                m1.m12 = this.values[1][2];
                                if (this.nRow > 2) {
                                    m1.m22 = this.values[2][2];
                                    if (this.nRow > 3) {
                                        m1.m32 = this.values[3][2];
                                    }
                                }
                            }
                        }
                        if (this.nCol > 3 && this.nRow > 0) {
                            m1.m03 = this.values[0][3];
                            if (this.nRow > 1) {
                                m1.m13 = this.values[1][3];
                                if (this.nRow > 2) {
                                    m1.m23 = this.values[2][3];
                                    if (this.nRow > 3) {
                                        m1.m33 = this.values[3][3];
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            m1.m00 = this.values[0][0];
            m1.m01 = this.values[0][1];
            m1.m02 = this.values[0][2];
            m1.m03 = this.values[0][3];
            m1.m10 = this.values[1][0];
            m1.m11 = this.values[1][1];
            m1.m12 = this.values[1][2];
            m1.m13 = this.values[1][3];
            m1.m20 = this.values[2][0];
            m1.m21 = this.values[2][1];
            m1.m22 = this.values[2][2];
            m1.m23 = this.values[2][3];
            m1.m30 = this.values[3][0];
            m1.m31 = this.values[3][1];
            m1.m32 = this.values[3][2];
            m1.m33 = this.values[3][3];
        }
    }

    public final void get(Matrix4f m1) {
        if (this.nRow < 4 || this.nCol < 4) {
            m1.setZero();
            if (this.nCol > 0) {
                if (this.nRow > 0) {
                    m1.m00 = (float)this.values[0][0];
                    if (this.nRow > 1) {
                        m1.m10 = (float)this.values[1][0];
                        if (this.nRow > 2) {
                            m1.m20 = (float)this.values[2][0];
                            if (this.nRow > 3) {
                                m1.m30 = (float)this.values[3][0];
                            }
                        }
                    }
                }
                if (this.nCol > 1) {
                    if (this.nRow > 0) {
                        m1.m01 = (float)this.values[0][1];
                        if (this.nRow > 1) {
                            m1.m11 = (float)this.values[1][1];
                            if (this.nRow > 2) {
                                m1.m21 = (float)this.values[2][1];
                                if (this.nRow > 3) {
                                    m1.m31 = (float)this.values[3][1];
                                }
                            }
                        }
                    }
                    if (this.nCol > 2) {
                        if (this.nRow > 0) {
                            m1.m02 = (float)this.values[0][2];
                            if (this.nRow > 1) {
                                m1.m12 = (float)this.values[1][2];
                                if (this.nRow > 2) {
                                    m1.m22 = (float)this.values[2][2];
                                    if (this.nRow > 3) {
                                        m1.m32 = (float)this.values[3][2];
                                    }
                                }
                            }
                        }
                        if (this.nCol > 3 && this.nRow > 0) {
                            m1.m03 = (float)this.values[0][3];
                            if (this.nRow > 1) {
                                m1.m13 = (float)this.values[1][3];
                                if (this.nRow > 2) {
                                    m1.m23 = (float)this.values[2][3];
                                    if (this.nRow > 3) {
                                        m1.m33 = (float)this.values[3][3];
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            m1.m00 = (float)this.values[0][0];
            m1.m01 = (float)this.values[0][1];
            m1.m02 = (float)this.values[0][2];
            m1.m03 = (float)this.values[0][3];
            m1.m10 = (float)this.values[1][0];
            m1.m11 = (float)this.values[1][1];
            m1.m12 = (float)this.values[1][2];
            m1.m13 = (float)this.values[1][3];
            m1.m20 = (float)this.values[2][0];
            m1.m21 = (float)this.values[2][1];
            m1.m22 = (float)this.values[2][2];
            m1.m23 = (float)this.values[2][3];
            m1.m30 = (float)this.values[3][0];
            m1.m31 = (float)this.values[3][1];
            m1.m32 = (float)this.values[3][2];
            m1.m33 = (float)this.values[3][3];
        }
    }

    public final void get(GMatrix m1) {
        int j2;
        int i2;
        int nc2 = this.nCol < m1.nCol ? this.nCol : m1.nCol;
        int nr2 = this.nRow < m1.nRow ? this.nRow : m1.nRow;
        for (i2 = 0; i2 < nr2; ++i2) {
            for (j2 = 0; j2 < nc2; ++j2) {
                m1.values[i2][j2] = this.values[i2][j2];
            }
        }
        for (i2 = nr2; i2 < m1.nRow; ++i2) {
            for (j2 = 0; j2 < m1.nCol; ++j2) {
                m1.values[i2][j2] = 0.0;
            }
        }
        for (j2 = nc2; j2 < m1.nCol; ++j2) {
            for (i2 = 0; i2 < nr2; ++i2) {
                m1.values[i2][j2] = 0.0;
            }
        }
    }

    public final void setRow(int row, double[] array) {
        for (int i2 = 0; i2 < this.nCol; ++i2) {
            this.values[row][i2] = array[i2];
        }
    }

    public final void setRow(int row, GVector vector) {
        for (int i2 = 0; i2 < this.nCol; ++i2) {
            this.values[row][i2] = vector.values[i2];
        }
    }

    public final void setColumn(int col, double[] array) {
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            this.values[i2][col] = array[i2];
        }
    }

    public final void setColumn(int col, GVector vector) {
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            this.values[i2][col] = vector.values[i2];
        }
    }

    public final void mulTransposeBoth(GMatrix m1, GMatrix m2) {
        if (m1.nRow != m2.nCol || this.nRow != m1.nCol || this.nCol != m2.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix14"));
        }
        if (m1 == this || m2 == this) {
            double[][] tmp = new double[this.nRow][this.nCol];
            for (int i2 = 0; i2 < this.nRow; ++i2) {
                for (int j2 = 0; j2 < this.nCol; ++j2) {
                    tmp[i2][j2] = 0.0;
                    for (int k2 = 0; k2 < m1.nRow; ++k2) {
                        double[] dArray = tmp[i2];
                        int n2 = j2;
                        dArray[n2] = dArray[n2] + m1.values[k2][i2] * m2.values[j2][k2];
                    }
                }
            }
            this.values = tmp;
        } else {
            for (int i3 = 0; i3 < this.nRow; ++i3) {
                for (int j3 = 0; j3 < this.nCol; ++j3) {
                    this.values[i3][j3] = 0.0;
                    for (int k3 = 0; k3 < m1.nRow; ++k3) {
                        double[] dArray = this.values[i3];
                        int n3 = j3;
                        dArray[n3] = dArray[n3] + m1.values[k3][i3] * m2.values[j3][k3];
                    }
                }
            }
        }
    }

    public final void mulTransposeRight(GMatrix m1, GMatrix m2) {
        if (m1.nCol != m2.nCol || this.nCol != m2.nRow || this.nRow != m1.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix15"));
        }
        if (m1 == this || m2 == this) {
            double[][] tmp = new double[this.nRow][this.nCol];
            for (int i2 = 0; i2 < this.nRow; ++i2) {
                for (int j2 = 0; j2 < this.nCol; ++j2) {
                    tmp[i2][j2] = 0.0;
                    for (int k2 = 0; k2 < m1.nCol; ++k2) {
                        double[] dArray = tmp[i2];
                        int n2 = j2;
                        dArray[n2] = dArray[n2] + m1.values[i2][k2] * m2.values[j2][k2];
                    }
                }
            }
            this.values = tmp;
        } else {
            for (int i3 = 0; i3 < this.nRow; ++i3) {
                for (int j3 = 0; j3 < this.nCol; ++j3) {
                    this.values[i3][j3] = 0.0;
                    for (int k3 = 0; k3 < m1.nCol; ++k3) {
                        double[] dArray = this.values[i3];
                        int n3 = j3;
                        dArray[n3] = dArray[n3] + m1.values[i3][k3] * m2.values[j3][k3];
                    }
                }
            }
        }
    }

    public final void mulTransposeLeft(GMatrix m1, GMatrix m2) {
        if (m1.nRow != m2.nRow || this.nCol != m2.nCol || this.nRow != m1.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix16"));
        }
        if (m1 == this || m2 == this) {
            double[][] tmp = new double[this.nRow][this.nCol];
            for (int i2 = 0; i2 < this.nRow; ++i2) {
                for (int j2 = 0; j2 < this.nCol; ++j2) {
                    tmp[i2][j2] = 0.0;
                    for (int k2 = 0; k2 < m1.nRow; ++k2) {
                        double[] dArray = tmp[i2];
                        int n2 = j2;
                        dArray[n2] = dArray[n2] + m1.values[k2][i2] * m2.values[k2][j2];
                    }
                }
            }
            this.values = tmp;
        } else {
            for (int i3 = 0; i3 < this.nRow; ++i3) {
                for (int j3 = 0; j3 < this.nCol; ++j3) {
                    this.values[i3][j3] = 0.0;
                    for (int k3 = 0; k3 < m1.nRow; ++k3) {
                        double[] dArray = this.values[i3];
                        int n3 = j3;
                        dArray[n3] = dArray[n3] + m1.values[k3][i3] * m2.values[k3][j3];
                    }
                }
            }
        }
    }

    public final void transpose() {
        if (this.nRow != this.nCol) {
            int i2 = this.nRow;
            this.nRow = this.nCol;
            this.nCol = i2;
            double[][] tmp = new double[this.nRow][this.nCol];
            for (i2 = 0; i2 < this.nRow; ++i2) {
                for (int j2 = 0; j2 < this.nCol; ++j2) {
                    tmp[i2][j2] = this.values[j2][i2];
                }
            }
            this.values = tmp;
        } else {
            for (int i3 = 0; i3 < this.nRow; ++i3) {
                for (int j3 = 0; j3 < i3; ++j3) {
                    double swap = this.values[i3][j3];
                    this.values[i3][j3] = this.values[j3][i3];
                    this.values[j3][i3] = swap;
                }
            }
        }
    }

    public final void transpose(GMatrix m1) {
        if (this.nRow != m1.nCol || this.nCol != m1.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix17"));
        }
        if (m1 != this) {
            for (int i2 = 0; i2 < this.nRow; ++i2) {
                for (int j2 = 0; j2 < this.nCol; ++j2) {
                    this.values[i2][j2] = m1.values[j2][i2];
                }
            }
        } else {
            this.transpose();
        }
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer(this.nRow * this.nCol * 8);
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                buffer.append(this.values[i2][j2]).append(" ");
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    private static void checkMatrix(GMatrix m2) {
        for (int i2 = 0; i2 < m2.nRow; ++i2) {
            for (int j2 = 0; j2 < m2.nCol; ++j2) {
                if (Math.abs(m2.values[i2][j2]) < 1.0E-10) {
                    System.out.print(" 0.0     ");
                    continue;
                }
                System.out.print(" " + m2.values[i2][j2]);
            }
            System.out.print("\n");
        }
    }

    public int hashCode() {
        long bits = 1L;
        bits = 31L * bits + (long)this.nRow;
        bits = 31L * bits + (long)this.nCol;
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                bits = 31L * bits + VecMathUtil.doubleToLongBits(this.values[i2][j2]);
            }
        }
        return (int)(bits ^ bits >> 32);
    }

    public boolean equals(GMatrix m1) {
        try {
            if (this.nRow != m1.nRow || this.nCol != m1.nCol) {
                return false;
            }
            for (int i2 = 0; i2 < this.nRow; ++i2) {
                for (int j2 = 0; j2 < this.nCol; ++j2) {
                    if (this.values[i2][j2] == m1.values[i2][j2]) continue;
                    return false;
                }
            }
            return true;
        }
        catch (NullPointerException e2) {
            return false;
        }
    }

    public boolean equals(Object o1) {
        try {
            GMatrix m2 = (GMatrix)o1;
            if (this.nRow != m2.nRow || this.nCol != m2.nCol) {
                return false;
            }
            for (int i2 = 0; i2 < this.nRow; ++i2) {
                for (int j2 = 0; j2 < this.nCol; ++j2) {
                    if (this.values[i2][j2] == m2.values[i2][j2]) continue;
                    return false;
                }
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

    public boolean epsilonEquals(GMatrix m1, float epsilon) {
        return this.epsilonEquals(m1, (double)epsilon);
    }

    public boolean epsilonEquals(GMatrix m1, double epsilon) {
        if (this.nRow != m1.nRow || this.nCol != m1.nCol) {
            return false;
        }
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                double diff = this.values[i2][j2] - m1.values[i2][j2];
                double d2 = diff < 0.0 ? -diff : diff;
                if (!(d2 > epsilon)) continue;
                return false;
            }
        }
        return true;
    }

    public final double trace() {
        int l2 = this.nRow < this.nCol ? this.nRow : this.nCol;
        double t2 = 0.0;
        for (int i2 = 0; i2 < l2; ++i2) {
            t2 += this.values[i2][i2];
        }
        return t2;
    }

    public final int SVD(GMatrix U, GMatrix W, GMatrix V) {
        if (this.nCol != V.nCol || this.nCol != V.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix18"));
        }
        if (this.nRow != U.nRow || this.nRow != U.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix25"));
        }
        if (this.nRow != W.nRow || this.nCol != W.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix26"));
        }
        if (this.nRow == 2 && this.nCol == 2 && this.values[1][0] == 0.0) {
            U.setIdentity();
            V.setIdentity();
            if (this.values[0][1] == 0.0) {
                return 2;
            }
            double[] sinl = new double[1];
            double[] sinr = new double[1];
            double[] cosl = new double[1];
            double[] cosr = new double[1];
            double[] single_values = new double[]{this.values[0][0], this.values[1][1]};
            GMatrix.compute_2X2(this.values[0][0], this.values[0][1], this.values[1][1], single_values, sinl, cosl, sinr, cosr, 0);
            GMatrix.update_u(0, U, cosl, sinl);
            GMatrix.update_v(0, V, cosr, sinr);
            return 2;
        }
        return GMatrix.computeSVD(this, U, W, V);
    }

    public final int LUD(GMatrix LU, GVector permutation) {
        int j2;
        int i2;
        int size = LU.nRow * LU.nCol;
        double[] temp = new double[size];
        int[] even_row_exchange = new int[1];
        int[] row_perm = new int[LU.nRow];
        if (this.nRow != this.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix19"));
        }
        if (this.nRow != LU.nRow) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix27"));
        }
        if (this.nCol != LU.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix27"));
        }
        if (LU.nRow != permutation.getSize()) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix20"));
        }
        for (i2 = 0; i2 < this.nRow; ++i2) {
            for (j2 = 0; j2 < this.nCol; ++j2) {
                temp[i2 * this.nCol + j2] = this.values[i2][j2];
            }
        }
        if (!GMatrix.luDecomposition(LU.nRow, temp, row_perm, even_row_exchange)) {
            throw new SingularMatrixException(VecMathI18N.getString("GMatrix21"));
        }
        for (i2 = 0; i2 < this.nRow; ++i2) {
            for (j2 = 0; j2 < this.nCol; ++j2) {
                LU.values[i2][j2] = temp[i2 * this.nCol + j2];
            }
        }
        for (i2 = 0; i2 < LU.nRow; ++i2) {
            permutation.values[i2] = row_perm[i2];
        }
        return even_row_exchange[0];
    }

    public final void setScale(double scale) {
        int i2;
        int l2 = this.nRow < this.nCol ? this.nRow : this.nCol;
        for (i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = 0.0;
            }
        }
        for (i2 = 0; i2 < l2; ++i2) {
            this.values[i2][i2] = scale;
        }
    }

    final void invertGeneral(GMatrix m1) {
        int j2;
        int i2;
        int size = m1.nRow * m1.nCol;
        double[] temp = new double[size];
        double[] result = new double[size];
        int[] row_perm = new int[m1.nRow];
        int[] even_row_exchange = new int[1];
        if (m1.nRow != m1.nCol) {
            throw new MismatchedSizeException(VecMathI18N.getString("GMatrix22"));
        }
        for (i2 = 0; i2 < this.nRow; ++i2) {
            for (j2 = 0; j2 < this.nCol; ++j2) {
                temp[i2 * this.nCol + j2] = m1.values[i2][j2];
            }
        }
        if (!GMatrix.luDecomposition(m1.nRow, temp, row_perm, even_row_exchange)) {
            throw new SingularMatrixException(VecMathI18N.getString("GMatrix21"));
        }
        for (i2 = 0; i2 < size; ++i2) {
            result[i2] = 0.0;
        }
        for (i2 = 0; i2 < this.nCol; ++i2) {
            result[i2 + i2 * this.nCol] = 1.0;
        }
        GMatrix.luBacksubstitution(m1.nRow, temp, row_perm, result);
        for (i2 = 0; i2 < this.nRow; ++i2) {
            for (j2 = 0; j2 < this.nCol; ++j2) {
                this.values[i2][j2] = result[i2 * this.nCol + j2];
            }
        }
    }

    static boolean luDecomposition(int dim, double[] matrix0, int[] row_perm, int[] even_row_xchg) {
        double temp;
        int j2;
        double big2;
        double[] row_scale = new double[dim];
        int ptr = 0;
        int rs2 = 0;
        even_row_xchg[0] = 1;
        int i2 = dim;
        while (i2-- != 0) {
            big2 = 0.0;
            j2 = dim;
            while (j2-- != 0) {
                temp = matrix0[ptr++];
                if (!((temp = Math.abs(temp)) > big2)) continue;
                big2 = temp;
            }
            if (big2 == 0.0) {
                return false;
            }
            row_scale[rs2++] = 1.0 / big2;
        }
        int mtx = 0;
        for (j2 = 0; j2 < dim; ++j2) {
            int p2;
            int p1;
            int k2;
            double sum;
            int target;
            for (i2 = 0; i2 < j2; ++i2) {
                target = mtx + dim * i2 + j2;
                sum = matrix0[target];
                k2 = i2;
                p1 = mtx + dim * i2;
                p2 = mtx + j2;
                while (k2-- != 0) {
                    sum -= matrix0[p1] * matrix0[p2];
                    ++p1;
                    p2 += dim;
                }
                matrix0[target] = sum;
            }
            big2 = 0.0;
            int imax = -1;
            for (i2 = j2; i2 < dim; ++i2) {
                double d2;
                target = mtx + dim * i2 + j2;
                sum = matrix0[target];
                k2 = j2;
                p1 = mtx + dim * i2;
                p2 = mtx + j2;
                while (k2-- != 0) {
                    sum -= matrix0[p1] * matrix0[p2];
                    ++p1;
                    p2 += dim;
                }
                matrix0[target] = sum;
                temp = row_scale[i2] * Math.abs(sum);
                if (!(d2 >= big2)) continue;
                big2 = temp;
                imax = i2;
            }
            if (imax < 0) {
                throw new RuntimeException(VecMathI18N.getString("GMatrix24"));
            }
            if (j2 != imax) {
                k2 = dim;
                p1 = mtx + dim * imax;
                p2 = mtx + dim * j2;
                while (k2-- != 0) {
                    temp = matrix0[p1];
                    matrix0[p1++] = matrix0[p2];
                    matrix0[p2++] = temp;
                }
                row_scale[imax] = row_scale[j2];
                even_row_xchg[0] = -even_row_xchg[0];
            }
            row_perm[j2] = imax;
            if (matrix0[mtx + dim * j2 + j2] == 0.0) {
                return false;
            }
            if (j2 == dim - 1) continue;
            temp = 1.0 / matrix0[mtx + dim * j2 + j2];
            target = mtx + dim * (j2 + 1) + j2;
            i2 = dim - 1 - j2;
            while (i2-- != 0) {
                int n2 = target;
                matrix0[n2] = matrix0[n2] * temp;
                target += dim;
            }
        }
        return true;
    }

    static void luBacksubstitution(int dim, double[] matrix1, int[] row_perm, double[] matrix2) {
        int rp2 = 0;
        for (int k2 = 0; k2 < dim; ++k2) {
            int j2;
            int rv2;
            int i2;
            int cv2 = k2;
            int ii2 = -1;
            for (i2 = 0; i2 < dim; ++i2) {
                int ip2 = row_perm[rp2 + i2];
                double sum = matrix2[cv2 + dim * ip2];
                matrix2[cv2 + dim * ip2] = matrix2[cv2 + dim * i2];
                if (ii2 >= 0) {
                    rv2 = i2 * dim;
                    for (j2 = ii2; j2 <= i2 - 1; ++j2) {
                        sum -= matrix1[rv2 + j2] * matrix2[cv2 + dim * j2];
                    }
                } else if (sum != 0.0) {
                    ii2 = i2;
                }
                matrix2[cv2 + dim * i2] = sum;
            }
            for (i2 = 0; i2 < dim; ++i2) {
                int ri2 = dim - 1 - i2;
                rv2 = dim * ri2;
                double tt2 = 0.0;
                for (j2 = 1; j2 <= i2; ++j2) {
                    tt2 += matrix1[rv2 + dim - j2] * matrix2[cv2 + dim * (dim - j2)];
                }
                matrix2[cv2 + dim * ri2] = (matrix2[cv2 + dim * ri2] - tt2) / matrix1[rv2 + ri2];
            }
        }
    }

    static int computeSVD(GMatrix mat, GMatrix U, GMatrix W, GMatrix V) {
        int i2;
        int eLength;
        int sLength;
        GMatrix tmp = new GMatrix(mat.nRow, mat.nCol);
        GMatrix u2 = new GMatrix(mat.nRow, mat.nCol);
        GMatrix v2 = new GMatrix(mat.nRow, mat.nCol);
        GMatrix m2 = new GMatrix(mat);
        if (m2.nRow >= m2.nCol) {
            sLength = m2.nCol;
            eLength = m2.nCol - 1;
        } else {
            sLength = m2.nRow;
            eLength = m2.nRow;
        }
        int vecLength = m2.nRow > m2.nCol ? m2.nRow : m2.nCol;
        double[] vec = new double[vecLength];
        double[] single_values = new double[sLength];
        double[] e2 = new double[eLength];
        int rank = 0;
        U.setIdentity();
        V.setIdentity();
        int nr2 = m2.nRow;
        int nc2 = m2.nCol;
        for (int si2 = 0; si2 < sLength; ++si2) {
            double t2;
            int k2;
            int j2;
            double scale;
            double mag;
            if (nr2 > 1) {
                mag = 0.0;
                for (i2 = 0; i2 < nr2; ++i2) {
                    mag += m2.values[i2 + si2][si2] * m2.values[i2 + si2][si2];
                }
                mag = Math.sqrt(mag);
                vec[0] = m2.values[si2][si2] == 0.0 ? mag : m2.values[si2][si2] + GMatrix.d_sign(mag, m2.values[si2][si2]);
                for (i2 = 1; i2 < nr2; ++i2) {
                    vec[i2] = m2.values[si2 + i2][si2];
                }
                scale = 0.0;
                for (i2 = 0; i2 < nr2; ++i2) {
                    scale += vec[i2] * vec[i2];
                }
                scale = 2.0 / scale;
                for (j2 = si2; j2 < m2.nRow; ++j2) {
                    for (k2 = si2; k2 < m2.nRow; ++k2) {
                        u2.values[j2][k2] = -scale * vec[j2 - si2] * vec[k2 - si2];
                    }
                }
                i2 = si2;
                while (i2 < m2.nRow) {
                    double[] dArray = u2.values[i2];
                    int n2 = i2++;
                    dArray[n2] = dArray[n2] + 1.0;
                }
                t2 = 0.0;
                for (i2 = si2; i2 < m2.nRow; ++i2) {
                    t2 += u2.values[si2][i2] * m2.values[i2][si2];
                }
                m2.values[si2][si2] = t2;
                for (j2 = si2; j2 < m2.nRow; ++j2) {
                    for (k2 = si2 + 1; k2 < m2.nCol; ++k2) {
                        tmp.values[j2][k2] = 0.0;
                        for (i2 = si2; i2 < m2.nCol; ++i2) {
                            double[] dArray = tmp.values[j2];
                            int n3 = k2;
                            dArray[n3] = dArray[n3] + u2.values[j2][i2] * m2.values[i2][k2];
                        }
                    }
                }
                for (j2 = si2; j2 < m2.nRow; ++j2) {
                    for (k2 = si2 + 1; k2 < m2.nCol; ++k2) {
                        m2.values[j2][k2] = tmp.values[j2][k2];
                    }
                }
                for (j2 = si2; j2 < m2.nRow; ++j2) {
                    for (k2 = 0; k2 < m2.nCol; ++k2) {
                        tmp.values[j2][k2] = 0.0;
                        for (i2 = si2; i2 < m2.nCol; ++i2) {
                            double[] dArray = tmp.values[j2];
                            int n4 = k2;
                            dArray[n4] = dArray[n4] + u2.values[j2][i2] * U.values[i2][k2];
                        }
                    }
                }
                for (j2 = si2; j2 < m2.nRow; ++j2) {
                    for (k2 = 0; k2 < m2.nCol; ++k2) {
                        U.values[j2][k2] = tmp.values[j2][k2];
                    }
                }
                --nr2;
            }
            if (nc2 <= 2) continue;
            mag = 0.0;
            for (i2 = 1; i2 < nc2; ++i2) {
                mag += m2.values[si2][si2 + i2] * m2.values[si2][si2 + i2];
            }
            mag = Math.sqrt(mag);
            vec[0] = m2.values[si2][si2 + 1] == 0.0 ? mag : m2.values[si2][si2 + 1] + GMatrix.d_sign(mag, m2.values[si2][si2 + 1]);
            for (i2 = 1; i2 < nc2 - 1; ++i2) {
                vec[i2] = m2.values[si2][si2 + i2 + 1];
            }
            scale = 0.0;
            for (i2 = 0; i2 < nc2 - 1; ++i2) {
                scale += vec[i2] * vec[i2];
            }
            scale = 2.0 / scale;
            for (j2 = si2 + 1; j2 < nc2; ++j2) {
                for (k2 = si2 + 1; k2 < m2.nCol; ++k2) {
                    v2.values[j2][k2] = -scale * vec[j2 - si2 - 1] * vec[k2 - si2 - 1];
                }
            }
            i2 = si2 + 1;
            while (i2 < m2.nCol) {
                double[] dArray = v2.values[i2];
                int n5 = i2++;
                dArray[n5] = dArray[n5] + 1.0;
            }
            t2 = 0.0;
            for (i2 = si2; i2 < m2.nCol; ++i2) {
                t2 += v2.values[i2][si2 + 1] * m2.values[si2][i2];
            }
            m2.values[si2][si2 + 1] = t2;
            for (j2 = si2 + 1; j2 < m2.nRow; ++j2) {
                for (k2 = si2 + 1; k2 < m2.nCol; ++k2) {
                    tmp.values[j2][k2] = 0.0;
                    for (i2 = si2 + 1; i2 < m2.nCol; ++i2) {
                        double[] dArray = tmp.values[j2];
                        int n6 = k2;
                        dArray[n6] = dArray[n6] + v2.values[i2][k2] * m2.values[j2][i2];
                    }
                }
            }
            for (j2 = si2 + 1; j2 < m2.nRow; ++j2) {
                for (k2 = si2 + 1; k2 < m2.nCol; ++k2) {
                    m2.values[j2][k2] = tmp.values[j2][k2];
                }
            }
            for (j2 = 0; j2 < m2.nRow; ++j2) {
                for (k2 = si2 + 1; k2 < m2.nCol; ++k2) {
                    tmp.values[j2][k2] = 0.0;
                    for (i2 = si2 + 1; i2 < m2.nCol; ++i2) {
                        double[] dArray = tmp.values[j2];
                        int n7 = k2;
                        dArray[n7] = dArray[n7] + v2.values[i2][k2] * V.values[j2][i2];
                    }
                }
            }
            for (j2 = 0; j2 < m2.nRow; ++j2) {
                for (k2 = si2 + 1; k2 < m2.nCol; ++k2) {
                    V.values[j2][k2] = tmp.values[j2][k2];
                }
            }
            --nc2;
        }
        for (i2 = 0; i2 < sLength; ++i2) {
            single_values[i2] = m2.values[i2][i2];
        }
        for (i2 = 0; i2 < eLength; ++i2) {
            e2[i2] = m2.values[i2][i2 + 1];
        }
        if (m2.nRow == 2 && m2.nCol == 2) {
            double[] cosl = new double[1];
            double[] cosr = new double[1];
            double[] sinl = new double[1];
            double[] sinr = new double[1];
            GMatrix.compute_2X2(single_values[0], e2[0], single_values[1], single_values, sinl, cosl, sinr, cosr, 0);
            GMatrix.update_u(0, U, cosl, sinl);
            GMatrix.update_v(0, V, cosr, sinr);
            return 2;
        }
        GMatrix.compute_qr(0, e2.length - 1, single_values, e2, U, V);
        rank = single_values.length;
        return rank;
    }

    static void compute_qr(int start, int end, double[] s2, double[] e2, GMatrix u2, GMatrix v2) {
        int i2;
        double[] cosl = new double[1];
        double[] cosr = new double[1];
        double[] sinl = new double[1];
        double[] sinr = new double[1];
        GMatrix m2 = new GMatrix(u2.nCol, v2.nRow);
        int MAX_INTERATIONS = 2;
        double CONVERGE_TOL = 4.89E-15;
        double c_b48 = 1.0;
        double c_b71 = -1.0;
        boolean converged = false;
        double f2 = 0.0;
        double g2 = 0.0;
        for (int k2 = 0; k2 < 2 && !converged; ++k2) {
            double r2;
            for (i2 = start; i2 <= end; ++i2) {
                if (i2 == start) {
                    int sl2 = e2.length == s2.length ? end : end + 1;
                    double shift = GMatrix.compute_shift(s2[sl2 - 1], e2[end], s2[sl2]);
                    f2 = (Math.abs(s2[i2]) - shift) * (GMatrix.d_sign(c_b48, s2[i2]) + shift / s2[i2]);
                    g2 = e2[i2];
                }
                r2 = GMatrix.compute_rot(f2, g2, sinr, cosr);
                if (i2 != start) {
                    e2[i2 - 1] = r2;
                }
                f2 = cosr[0] * s2[i2] + sinr[0] * e2[i2];
                e2[i2] = cosr[0] * e2[i2] - sinr[0] * s2[i2];
                g2 = sinr[0] * s2[i2 + 1];
                s2[i2 + 1] = cosr[0] * s2[i2 + 1];
                GMatrix.update_v(i2, v2, cosr, sinr);
                s2[i2] = r2 = GMatrix.compute_rot(f2, g2, sinl, cosl);
                f2 = cosl[0] * e2[i2] + sinl[0] * s2[i2 + 1];
                s2[i2 + 1] = cosl[0] * s2[i2 + 1] - sinl[0] * e2[i2];
                if (i2 < end) {
                    g2 = sinl[0] * e2[i2 + 1];
                    e2[i2 + 1] = cosl[0] * e2[i2 + 1];
                }
                GMatrix.update_u(i2, u2, cosl, sinl);
            }
            if (s2.length == e2.length) {
                r2 = GMatrix.compute_rot(f2, g2, sinr, cosr);
                f2 = cosr[0] * s2[i2] + sinr[0] * e2[i2];
                e2[i2] = cosr[0] * e2[i2] - sinr[0] * s2[i2];
                s2[i2 + 1] = cosr[0] * s2[i2 + 1];
                GMatrix.update_v(i2, v2, cosr, sinr);
            }
            while (end - start > 1 && Math.abs(e2[end]) < 4.89E-15) {
                --end;
            }
            for (int n2 = end - 2; n2 > start; --n2) {
                if (!(Math.abs(e2[n2]) < 4.89E-15)) continue;
                GMatrix.compute_qr(n2 + 1, end, s2, e2, u2, v2);
                end = n2 - 1;
                while (end - start > 1 && Math.abs(e2[end]) < 4.89E-15) {
                    --end;
                }
            }
            if (end - start > 1 || !(Math.abs(e2[start + 1]) < 4.89E-15)) continue;
            converged = true;
        }
        if (Math.abs(e2[1]) < 4.89E-15) {
            GMatrix.compute_2X2(s2[start], e2[start], s2[start + 1], s2, sinl, cosl, sinr, cosr, 0);
            e2[start] = 0.0;
            e2[start + 1] = 0.0;
        }
        i2 = start;
        GMatrix.update_u(i2, u2, cosl, sinl);
        GMatrix.update_v(i2, v2, cosr, sinr);
    }

    private static void print_se(double[] s2, double[] e2) {
        System.out.println("\ns =" + s2[0] + " " + s2[1] + " " + s2[2]);
        System.out.println("e =" + e2[0] + " " + e2[1]);
    }

    private static void update_v(int index, GMatrix v2, double[] cosr, double[] sinr) {
        for (int j2 = 0; j2 < v2.nRow; ++j2) {
            double vtemp = v2.values[j2][index];
            v2.values[j2][index] = cosr[0] * vtemp + sinr[0] * v2.values[j2][index + 1];
            v2.values[j2][index + 1] = -sinr[0] * vtemp + cosr[0] * v2.values[j2][index + 1];
        }
    }

    private static void chase_up(double[] s2, double[] e2, int k2, GMatrix v2) {
        int i2;
        double[] cosr = new double[1];
        double[] sinr = new double[1];
        GMatrix t2 = new GMatrix(v2.nRow, v2.nCol);
        GMatrix m2 = new GMatrix(v2.nRow, v2.nCol);
        double f2 = e2[k2];
        double g2 = s2[k2];
        for (i2 = k2; i2 > 0; --i2) {
            double r2 = GMatrix.compute_rot(f2, g2, sinr, cosr);
            f2 = -e2[i2 - 1] * sinr[0];
            g2 = s2[i2 - 1];
            s2[i2] = r2;
            e2[i2 - 1] = e2[i2 - 1] * cosr[0];
            GMatrix.update_v_split(i2, k2 + 1, v2, cosr, sinr, t2, m2);
        }
        s2[i2 + 1] = GMatrix.compute_rot(f2, g2, sinr, cosr);
        GMatrix.update_v_split(i2, k2 + 1, v2, cosr, sinr, t2, m2);
    }

    private static void chase_across(double[] s2, double[] e2, int k2, GMatrix u2) {
        int i2;
        double[] cosl = new double[1];
        double[] sinl = new double[1];
        GMatrix t2 = new GMatrix(u2.nRow, u2.nCol);
        GMatrix m2 = new GMatrix(u2.nRow, u2.nCol);
        double g2 = e2[k2];
        double f2 = s2[k2 + 1];
        for (i2 = k2; i2 < u2.nCol - 2; ++i2) {
            double r2 = GMatrix.compute_rot(f2, g2, sinl, cosl);
            g2 = -e2[i2 + 1] * sinl[0];
            f2 = s2[i2 + 2];
            s2[i2 + 1] = r2;
            e2[i2 + 1] = e2[i2 + 1] * cosl[0];
            GMatrix.update_u_split(k2, i2 + 1, u2, cosl, sinl, t2, m2);
        }
        s2[i2 + 1] = GMatrix.compute_rot(f2, g2, sinl, cosl);
        GMatrix.update_u_split(k2, i2 + 1, u2, cosl, sinl, t2, m2);
    }

    private static void update_v_split(int topr, int bottomr, GMatrix v2, double[] cosr, double[] sinr, GMatrix t2, GMatrix m2) {
        for (int j2 = 0; j2 < v2.nRow; ++j2) {
            double vtemp = v2.values[j2][topr];
            v2.values[j2][topr] = cosr[0] * vtemp - sinr[0] * v2.values[j2][bottomr];
            v2.values[j2][bottomr] = sinr[0] * vtemp + cosr[0] * v2.values[j2][bottomr];
        }
        System.out.println("topr    =" + topr);
        System.out.println("bottomr =" + bottomr);
        System.out.println("cosr =" + cosr[0]);
        System.out.println("sinr =" + sinr[0]);
        System.out.println("\nm =");
        GMatrix.checkMatrix(m2);
        System.out.println("\nv =");
        GMatrix.checkMatrix(t2);
        m2.mul(m2, t2);
        System.out.println("\nt*m =");
        GMatrix.checkMatrix(m2);
    }

    private static void update_u_split(int topr, int bottomr, GMatrix u2, double[] cosl, double[] sinl, GMatrix t2, GMatrix m2) {
        for (int j2 = 0; j2 < u2.nCol; ++j2) {
            double utemp = u2.values[topr][j2];
            u2.values[topr][j2] = cosl[0] * utemp - sinl[0] * u2.values[bottomr][j2];
            u2.values[bottomr][j2] = sinl[0] * utemp + cosl[0] * u2.values[bottomr][j2];
        }
        System.out.println("\nm=");
        GMatrix.checkMatrix(m2);
        System.out.println("\nu=");
        GMatrix.checkMatrix(t2);
        m2.mul(t2, m2);
        System.out.println("\nt*m=");
        GMatrix.checkMatrix(m2);
    }

    private static void update_u(int index, GMatrix u2, double[] cosl, double[] sinl) {
        for (int j2 = 0; j2 < u2.nCol; ++j2) {
            double utemp = u2.values[index][j2];
            u2.values[index][j2] = cosl[0] * utemp + sinl[0] * u2.values[index + 1][j2];
            u2.values[index + 1][j2] = -sinl[0] * utemp + cosl[0] * u2.values[index + 1][j2];
        }
    }

    private static void print_m(GMatrix m2, GMatrix u2, GMatrix v2) {
        GMatrix mtmp = new GMatrix(m2.nCol, m2.nRow);
        mtmp.mul(u2, mtmp);
        mtmp.mul(mtmp, v2);
        System.out.println("\n m = \n" + GMatrix.toString(mtmp));
    }

    private static String toString(GMatrix m2) {
        StringBuffer buffer = new StringBuffer(m2.nRow * m2.nCol * 8);
        for (int i2 = 0; i2 < m2.nRow; ++i2) {
            for (int j2 = 0; j2 < m2.nCol; ++j2) {
                if (Math.abs(m2.values[i2][j2]) < 1.0E-9) {
                    buffer.append("0.0000 ");
                    continue;
                }
                buffer.append(m2.values[i2][j2]).append(" ");
            }
            buffer.append("\n");
        }
        return buffer.toString();
    }

    private static void print_svd(double[] s2, double[] e2, GMatrix u2, GMatrix v2) {
        int i2;
        GMatrix mtmp = new GMatrix(u2.nCol, v2.nRow);
        System.out.println(" \ns = ");
        for (i2 = 0; i2 < s2.length; ++i2) {
            System.out.println(" " + s2[i2]);
        }
        System.out.println(" \ne = ");
        for (i2 = 0; i2 < e2.length; ++i2) {
            System.out.println(" " + e2[i2]);
        }
        System.out.println(" \nu  = \n" + u2.toString());
        System.out.println(" \nv  = \n" + v2.toString());
        mtmp.setIdentity();
        for (i2 = 0; i2 < s2.length; ++i2) {
            mtmp.values[i2][i2] = s2[i2];
        }
        for (i2 = 0; i2 < e2.length; ++i2) {
            mtmp.values[i2][i2 + 1] = e2[i2];
        }
        System.out.println(" \nm  = \n" + mtmp.toString());
        mtmp.mulTransposeLeft(u2, mtmp);
        mtmp.mulTransposeRight(mtmp, v2);
        System.out.println(" \n u.transpose*m*v.transpose  = \n" + mtmp.toString());
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

    static double compute_shift(double f2, double g2, double h2) {
        double ssmin;
        double fa2 = Math.abs(f2);
        double ga2 = Math.abs(g2);
        double ha2 = Math.abs(h2);
        double fhmn = GMatrix.min(fa2, ha2);
        double fhmx = GMatrix.max(fa2, ha2);
        if (fhmn == 0.0) {
            ssmin = 0.0;
            if (fhmx != 0.0) {
                double d2 = GMatrix.min(fhmx, ga2) / GMatrix.max(fhmx, ga2);
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
                if (fa2 / ga2 < 1.0E-10) {
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
                    if (fa2 / ga2 < 1.0E-10) {
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
                    t2 = mm == 0.0 ? (l2 == 0.0 ? GMatrix.d_sign(c_b3, ft2) * GMatrix.d_sign(c_b4, gt2) : gt2 / GMatrix.d_sign(d2, ft2) + m2 / t2) : (m2 / (s2 + t2) + m2 / (r2 + l2)) * (a2 + 1.0);
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
                tsign = GMatrix.d_sign(c_b4, csr[0]) * GMatrix.d_sign(c_b4, csl[0]) * GMatrix.d_sign(c_b4, f2);
            }
            if (pmax == 2) {
                tsign = GMatrix.d_sign(c_b4, snr[0]) * GMatrix.d_sign(c_b4, csl[0]) * GMatrix.d_sign(c_b4, g2);
            }
            if (pmax == 3) {
                tsign = GMatrix.d_sign(c_b4, snr[0]) * GMatrix.d_sign(c_b4, snl[0]) * GMatrix.d_sign(c_b4, h2);
            }
            single_values[index] = GMatrix.d_sign(ssmax, tsign);
            double d__1 = tsign * GMatrix.d_sign(c_b4, f2) * GMatrix.d_sign(c_b4, h2);
            single_values[index + 1] = GMatrix.d_sign(ssmin, d__1);
        }
        return 0;
    }

    static double compute_rot(double f2, double g2, double[] sin, double[] cos) {
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
            double scale = GMatrix.max(Math.abs(f1), Math.abs(g1));
            if (scale >= 4.9947976805055876E145) {
                int count = 0;
                while (scale >= 4.9947976805055876E145) {
                    ++count;
                    scale = GMatrix.max(Math.abs(f1 *= 2.002083095183101E-146), Math.abs(g1 *= 2.002083095183101E-146));
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
                    scale = GMatrix.max(Math.abs(f1 *= 4.9947976805055876E145), Math.abs(g1 *= 4.9947976805055876E145));
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
        sin[0] = sn2;
        cos[0] = cs2;
        return r2;
    }

    static double d_sign(double a2, double b2) {
        double x2 = a2 >= 0.0 ? a2 : -a2;
        return b2 >= 0.0 ? x2 : -x2;
    }

    public Object clone() {
        GMatrix m1 = null;
        try {
            m1 = (GMatrix)super.clone();
        }
        catch (CloneNotSupportedException e2) {
            throw new InternalError();
        }
        m1.values = new double[this.nRow][this.nCol];
        for (int i2 = 0; i2 < this.nRow; ++i2) {
            for (int j2 = 0; j2 < this.nCol; ++j2) {
                m1.values[i2][j2] = this.values[i2][j2];
            }
        }
        return m1;
    }
}

