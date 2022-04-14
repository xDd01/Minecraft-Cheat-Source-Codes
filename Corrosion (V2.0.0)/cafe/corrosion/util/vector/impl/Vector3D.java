/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.vector.impl;

import net.minecraft.util.Vec3;

public class Vector3D {
    private final double x;
    private final double y;
    private final double z;

    public Vector3D(double x2, double y2, double z2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public Vector3D addVector(double x2, double y2, double z2) {
        return new Vector3D(this.x + x2, this.y + y2, this.z + z2);
    }

    public Vector3D floor() {
        return new Vector3D(Math.floor(this.x), Math.floor(this.y), Math.floor(this.z));
    }

    public double squareDistanceTo(Vector3D v2) {
        return Math.pow(v2.x - this.x, 2.0) + Math.pow(v2.y - this.y, 2.0) + Math.pow(v2.z - this.z, 2.0);
    }

    public Vector3D add(Vector3D v2) {
        return this.addVector(v2.getX(), v2.getY(), v2.getZ());
    }

    public Vec3 mc() {
        return new Vec3(this.x, this.y, this.z);
    }

    public String toString() {
        return "[" + this.x + ";" + this.y + ";" + this.z + "]";
    }
}

