/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.Tuple4i;

public class Point4i
extends Tuple4i
implements Serializable {
    static final long serialVersionUID = 620124780244617983L;

    public Point4i(int x2, int y2, int z2, int w2) {
        super(x2, y2, z2, w2);
    }

    public Point4i(int[] t2) {
        super(t2);
    }

    public Point4i(Tuple4i t1) {
        super(t1);
    }

    public Point4i() {
    }
}

