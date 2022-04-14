/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.vector.impl;

import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3i;
import org.lwjgl.util.vector.Vector3f;

public class Vector3F
extends Vector3f {
    public Vector3F(float x2, float y2, float z2) {
        super(x2, y2, z2);
    }

    public Vector3F(BlockPos pos) {
        super(pos.getX(), pos.getY(), pos.getZ());
    }

    public Vector3F(Vec3i vec) {
        super(vec.getX(), vec.getY(), vec.getZ());
    }

    public Vector3F(double v2, double posY, double v1) {
        this((float)v2, (float)posY, (float)v1);
    }

    public double squareDistanceTo(Vector3F vec) {
        double var2 = vec.x - this.x;
        double var4 = vec.y - this.y;
        double var5 = vec.z - this.z;
        return var2 * var2 + var4 * var4 + var5 * var5;
    }

    public Vector3F add(float x2, float y2, float z2) {
        this.x += x2;
        this.y += y2;
        this.z += z2;
        return this;
    }

    public Vector3F add(Vector3F vec) {
        this.x += vec.x;
        this.y += vec.y;
        this.z += vec.z;
        return this;
    }

    @Override
    public Vector3F scale(float amount) {
        this.x *= amount;
        this.y *= amount;
        this.z *= amount;
        return this;
    }

    public Vector3F clone() {
        return new Vector3F(this.x, this.y, this.z);
    }
}

