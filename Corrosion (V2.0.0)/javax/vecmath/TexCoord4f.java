/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.Tuple4d;
import javax.vecmath.Tuple4f;

public class TexCoord4f
extends Tuple4f
implements Serializable {
    static final long serialVersionUID = -3517736544731446513L;

    public TexCoord4f(float x2, float y2, float z2, float w2) {
        super(x2, y2, z2, w2);
    }

    public TexCoord4f(float[] v2) {
        super(v2);
    }

    public TexCoord4f(TexCoord4f v1) {
        super(v1);
    }

    public TexCoord4f(Tuple4f t1) {
        super(t1);
    }

    public TexCoord4f(Tuple4d t1) {
        super(t1);
    }

    public TexCoord4f() {
    }
}

