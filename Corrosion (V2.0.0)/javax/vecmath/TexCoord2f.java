/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.Tuple2f;

public class TexCoord2f
extends Tuple2f
implements Serializable {
    static final long serialVersionUID = 7998248474800032487L;

    public TexCoord2f(float x2, float y2) {
        super(x2, y2);
    }

    public TexCoord2f(float[] v2) {
        super(v2);
    }

    public TexCoord2f(TexCoord2f v1) {
        super(v1);
    }

    public TexCoord2f(Tuple2f t1) {
        super(t1);
    }

    public TexCoord2f() {
    }
}

