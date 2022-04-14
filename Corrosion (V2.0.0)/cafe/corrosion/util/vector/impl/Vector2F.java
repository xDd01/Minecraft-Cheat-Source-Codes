/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.vector.impl;

import org.lwjgl.util.vector.Vector2f;

public class Vector2F
extends Vector2f {
    public Vector2F(float x2, float y2) {
        super(x2, y2);
    }

    public Vector2F() {
        super(0.0f, 0.0f);
    }

    public Vector2F add(double x2, double y2) {
        this.x = (float)((double)this.x + x2);
        this.y = (float)((double)this.y + y2);
        return this;
    }

    public Vector2F add(float x2, float y2) {
        this.x += x2;
        this.y += y2;
        return this;
    }

    public Vector2F add(int x2, int y2) {
        this.x += (float)x2;
        this.y += (float)y2;
        return this;
    }

    public Vector2F add(Vector2F vec) {
        this.x += vec.x;
        this.y += vec.y;
        return this;
    }

    public Vector2F subtract(Vector2F pos) {
        this.x -= pos.x;
        this.y -= pos.y;
        return this;
    }

    public Vector2F subtract(double x2, double y2) {
        this.x = (float)((double)this.x - x2);
        this.y = (float)((double)this.y - y2);
        return this;
    }

    public Vector2F subtract(float x2, float y2) {
        this.x -= x2;
        this.y -= y2;
        return this;
    }

    public Vector2F subtract(int x2, int y2) {
        this.x -= (float)x2;
        this.y -= (float)y2;
        return this;
    }

    public Vector2F multiply(float number) {
        this.x *= this.x;
        this.y *= this.y;
        return this;
    }

    public Vector2F multiply(Vector2F vec) {
        this.x *= vec.x;
        this.y *= vec.y;
        return this;
    }

    public Vector2F multiply(float x2, float y2) {
        this.x *= x2;
        this.y *= y2;
        return this;
    }

    public Vector2F set(Vector2F vec) {
        this.x = vec.x;
        this.y = vec.y;
        return this;
    }

    public double squareDistanceTo(Vector2F vec) {
        double var2 = vec.x - this.x;
        double var4 = vec.y - this.y;
        return var2 * var2 + var4 * var4;
    }

    public Vector2F divide(float number) {
        this.x /= number;
        this.y /= number;
        return this;
    }

    public Vector2F clone() {
        return new Vector2F(this.x, this.y);
    }
}

