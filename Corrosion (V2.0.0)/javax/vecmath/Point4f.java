/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.Point4d;
import javax.vecmath.Tuple3f;
import javax.vecmath.Tuple4d;
import javax.vecmath.Tuple4f;

public class Point4f
extends Tuple4f
implements Serializable {
    static final long serialVersionUID = 4643134103185764459L;

    public Point4f(float x2, float y2, float z2, float w2) {
        super(x2, y2, z2, w2);
    }

    public Point4f(float[] p2) {
        super(p2);
    }

    public Point4f(Point4f p1) {
        super(p1);
    }

    public Point4f(Point4d p1) {
        super(p1);
    }

    public Point4f(Tuple4f t1) {
        super(t1);
    }

    public Point4f(Tuple4d t1) {
        super(t1);
    }

    public Point4f(Tuple3f t1) {
        super(t1.x, t1.y, t1.z, 1.0f);
    }

    public Point4f() {
    }

    public final void set(Tuple3f t1) {
        this.x = t1.x;
        this.y = t1.y;
        this.z = t1.z;
        this.w = 1.0f;
    }

    public final float distanceSquared(Point4f p1) {
        float dx2 = this.x - p1.x;
        float dy2 = this.y - p1.y;
        float dz2 = this.z - p1.z;
        float dw2 = this.w - p1.w;
        return dx2 * dx2 + dy2 * dy2 + dz2 * dz2 + dw2 * dw2;
    }

    public final float distance(Point4f p1) {
        float dx2 = this.x - p1.x;
        float dy2 = this.y - p1.y;
        float dz2 = this.z - p1.z;
        float dw2 = this.w - p1.w;
        return (float)Math.sqrt(dx2 * dx2 + dy2 * dy2 + dz2 * dz2 + dw2 * dw2);
    }

    public final float distanceL1(Point4f p1) {
        return Math.abs(this.x - p1.x) + Math.abs(this.y - p1.y) + Math.abs(this.z - p1.z) + Math.abs(this.w - p1.w);
    }

    public final float distanceLinf(Point4f p1) {
        float t1 = Math.max(Math.abs(this.x - p1.x), Math.abs(this.y - p1.y));
        float t2 = Math.max(Math.abs(this.z - p1.z), Math.abs(this.w - p1.w));
        return Math.max(t1, t2);
    }

    public final void project(Point4f p1) {
        float oneOw = 1.0f / p1.w;
        this.x = p1.x * oneOw;
        this.y = p1.y * oneOw;
        this.z = p1.z * oneOw;
        this.w = 1.0f;
    }
}

