/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.vector;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import org.lwjgl.util.vector.Vector3f;

public class Vector<T extends Number> {
    private T x;
    private T y;
    private T z;

    public Vector(T x2, T y2, T z2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
    }

    public Vector<T> setX(T x2) {
        this.x = x2;
        return this;
    }

    public Vector<T> setY(T y2) {
        this.y = y2;
        return this;
    }

    public Vector<T> setZ(T z2) {
        this.z = z2;
        return this;
    }

    public T getX() {
        return this.x;
    }

    public T getY() {
        return this.y;
    }

    public T getZ() {
        return this.z;
    }

    public static class Vec3f
    extends Vector3f {
        public Vec3f(float x2, float y2, float z2) {
            super(x2, y2, z2);
        }

        public Vec3f(BlockPos pos) {
            super(pos.getX(), pos.getY(), pos.getZ());
        }

        public Vec3f(Vec3i vec) {
            super(vec.getX(), vec.getY(), vec.getZ());
        }

        public double squareDistanceTo(Vec3f vec) {
            double var2 = vec.x - this.x;
            double var4 = vec.y - this.y;
            double var5 = vec.z - this.z;
            return var2 * var2 + var4 * var4 + var5 * var5;
        }

        public Vec3f add(float x2, float y2, float z2) {
            this.x += x2;
            this.y += y2;
            this.z += z2;
            return this;
        }

        public Vec3f add(Vec3f vec) {
            this.x += vec.x;
            this.y += vec.y;
            this.z += vec.z;
            return this;
        }

        @Override
        public Vec3f scale(float amount) {
            this.x *= amount;
            this.y *= amount;
            this.z *= amount;
            return this;
        }

        public Vec3f clone() {
            return new Vec3f(this.x, this.y, this.z);
        }
    }

    public static class Vector3<T extends Number>
    extends Vector<Number> {
        public Vector3(T x2, T y2, T z2) {
            super(x2, y2, z2);
        }

        public Vector2<T> toVector2() {
            return new Vector2(this.getX(), this.getY());
        }
    }

    public static class Vector2<T extends Number>
    extends Vector<Number> {
        public Vector2(T x2, T y2) {
            super(x2, y2, 0);
        }

        public Vector3<T> toVector3() {
            return new Vector3(this.getX(), this.getY(), this.getZ());
        }
    }
}

