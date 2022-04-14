/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.animation.impl;

import cafe.corrosion.menu.animation.Animation;

public class BounceAnimation
extends Animation {
    public BounceAnimation(long time) {
        super(time);
    }

    @Override
    public double calculate() {
        double x2 = this.getProgression();
        double n1 = 7.5625;
        double d1 = 2.75;
        if (x2 < 1.0 / d1) {
            return n1 * x2 * x2;
        }
        if (x2 < 2.0 / d1) {
            return n1 * (x2 -= 1.5 / d1) * x2 + 0.75;
        }
        if (x2 < 2.5 / d1) {
            return n1 * (x2 -= 2.25 / d1) * x2 + 0.9375;
        }
        return n1 * (x2 -= 2.625 / d1) * x2 + 0.984375;
    }
}

