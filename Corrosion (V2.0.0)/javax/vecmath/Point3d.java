/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.Point3f;
import javax.vecmath.Point4d;
import javax.vecmath.Tuple3d;
import javax.vecmath.Tuple3f;

public class Point3d
extends Tuple3d
implements Serializable {
    static final long serialVersionUID = 5718062286069042927L;

    public Point3d(double x2, double y2, double z2) {
        super(x2, y2, z2);
    }

    public Point3d(double[] p2) {
        super(p2);
    }

    public Point3d(Point3d p1) {
        super(p1);
    }

    public Point3d(Point3f p1) {
        super(p1);
    }

    public Point3d(Tuple3f t1) {
        super(t1);
    }

    public Point3d(Tuple3d t1) {
        super(t1);
    }

    public Point3d() {
    }

    public final double distanceSquared(Point3d p1) {
        double dx2 = this.x - p1.x;
        double dy2 = this.y - p1.y;
        double dz2 = this.z - p1.z;
        return dx2 * dx2 + dy2 * dy2 + dz2 * dz2;
    }

    public final double distance(Point3d p1) {
        double dx2 = this.x - p1.x;
        double dy2 = this.y - p1.y;
        double dz2 = this.z - p1.z;
        return Math.sqrt(dx2 * dx2 + dy2 * dy2 + dz2 * dz2);
    }

    public final double distanceL1(Point3d p1) {
        return Math.abs(this.x - p1.x) + Math.abs(this.y - p1.y) + Math.abs(this.z - p1.z);
    }

    public final double distanceLinf(Point3d p1) {
        double tmp = Math.max(Math.abs(this.x - p1.x), Math.abs(this.y - p1.y));
        return Math.max(tmp, Math.abs(this.z - p1.z));
    }

    public final void project(Point4d p1) {
        double oneOw = 1.0 / p1.w;
        this.x = p1.x * oneOw;
        this.y = p1.y * oneOw;
        this.z = p1.z * oneOw;
    }
}

