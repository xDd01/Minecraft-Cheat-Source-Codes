/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.io.Serializable;
import javax.vecmath.Point2d;
import javax.vecmath.Tuple2d;
import javax.vecmath.Tuple2f;

public class Point2f
extends Tuple2f
implements Serializable {
    static final long serialVersionUID = -4801347926528714435L;

    public Point2f(float x2, float y2) {
        super(x2, y2);
    }

    public Point2f(float[] p2) {
        super(p2);
    }

    public Point2f(Point2f p1) {
        super(p1);
    }

    public Point2f(Point2d p1) {
        super(p1);
    }

    public Point2f(Tuple2d t1) {
        super(t1);
    }

    public Point2f(Tuple2f t1) {
        super(t1);
    }

    public Point2f() {
    }

    public final float distanceSquared(Point2f p1) {
        float dx2 = this.x - p1.x;
        float dy2 = this.y - p1.y;
        return dx2 * dx2 + dy2 * dy2;
    }

    public final float distance(Point2f p1) {
        float dx2 = this.x - p1.x;
        float dy2 = this.y - p1.y;
        return (float)Math.sqrt(dx2 * dx2 + dy2 * dy2);
    }

    public final float distanceL1(Point2f p1) {
        return Math.abs(this.x - p1.x) + Math.abs(this.y - p1.y);
    }

    public final float distanceLinf(Point2f p1) {
        return Math.max(Math.abs(this.x - p1.x), Math.abs(this.y - p1.y));
    }
}

