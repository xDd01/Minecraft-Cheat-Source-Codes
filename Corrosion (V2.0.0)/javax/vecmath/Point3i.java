/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.Tuple3i;

public class Point3i
extends Tuple3i
implements Serializable {
    static final long serialVersionUID = 6149289077348153921L;

    public Point3i(int x2, int y2, int z2) {
        super(x2, y2, z2);
    }

    public Point3i(int[] t2) {
        super(t2);
    }

    public Point3i(Tuple3i t1) {
        super(t1);
    }

    public Point3i() {
    }
}

