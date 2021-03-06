/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import net.minecraft.util.Vec3;
import optifine.Config;

public class CustomColorFader {
    private Vec3 color = null;
    private long timeUpdate = System.currentTimeMillis();

    public Vec3 getColor(double p_getColor_1_, double p_getColor_3_, double p_getColor_5_) {
        if (this.color == null) {
            this.color = new Vec3(p_getColor_1_, p_getColor_3_, p_getColor_5_);
            return this.color;
        }
        long i2 = System.currentTimeMillis();
        long j2 = i2 - this.timeUpdate;
        if (j2 == 0L) {
            return this.color;
        }
        this.timeUpdate = i2;
        if (Math.abs(p_getColor_1_ - this.color.xCoord) < 0.004 && Math.abs(p_getColor_3_ - this.color.yCoord) < 0.004 && Math.abs(p_getColor_5_ - this.color.zCoord) < 0.004) {
            return this.color;
        }
        double d0 = (double)j2 * 0.001;
        d0 = Config.limit(d0, 0.0, 1.0);
        double d1 = p_getColor_1_ - this.color.xCoord;
        double d2 = p_getColor_3_ - this.color.yCoord;
        double d3 = p_getColor_5_ - this.color.zCoord;
        double d4 = this.color.xCoord + d1 * d0;
        double d5 = this.color.yCoord + d2 * d0;
        double d6 = this.color.zCoord + d3 * d0;
        this.color = new Vec3(d4, d5, d6);
        return this.color;
    }
}

