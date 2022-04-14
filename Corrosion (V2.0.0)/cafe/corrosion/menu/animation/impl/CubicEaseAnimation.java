/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.animation.impl;

import cafe.corrosion.menu.animation.Animation;

public class CubicEaseAnimation
extends Animation {
    public CubicEaseAnimation(long time) {
        super(time);
    }

    @Override
    public double calculate() {
        double x2 = this.getProgression();
        return x2 < 0.5 ? 4.0 * x2 * x2 * x2 : 1.0 - Math.pow(-2.0 * x2 + 2.0, 3.0) / 2.0;
    }
}

