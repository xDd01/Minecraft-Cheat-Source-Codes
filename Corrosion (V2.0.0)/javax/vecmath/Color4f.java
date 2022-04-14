/*
 * Decompiled with CFR 0.152.
 */
package javax.vecmath;

import java.awt.Color;
import java.io.Serializable;
import javax.vecmath.Tuple4d;
import javax.vecmath.Tuple4f;

public class Color4f
extends Tuple4f
implements Serializable {
    static final long serialVersionUID = 8577680141580006740L;

    public Color4f(float x2, float y2, float z2, float w2) {
        super(x2, y2, z2, w2);
    }

    public Color4f(float[] c2) {
        super(c2);
    }

    public Color4f(Color4f c1) {
        super(c1);
    }

    public Color4f(Tuple4f t1) {
        super(t1);
    }

    public Color4f(Tuple4d t1) {
        super(t1);
    }

    public Color4f(Color color) {
        super((float)color.getRed() / 255.0f, (float)color.getGreen() / 255.0f, (float)color.getBlue() / 255.0f, (float)color.getAlpha() / 255.0f);
    }

    public Color4f() {
    }

    public final void set(Color color) {
        this.x = (float)color.getRed() / 255.0f;
        this.y = (float)color.getGreen() / 255.0f;
        this.z = (float)color.getBlue() / 255.0f;
        this.w = (float)color.getAlpha() / 255.0f;
    }

    public final Color get() {
        int r2 = Math.round(this.x * 255.0f);
        int g2 = Math.round(this.y * 255.0f);
        int b2 = Math.round(this.z * 255.0f);
        int a2 = Math.round(this.w * 255.0f);
        return new Color(r2, g2, b2, a2);
    }
}

