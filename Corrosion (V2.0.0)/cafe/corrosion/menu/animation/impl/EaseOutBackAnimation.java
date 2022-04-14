/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.animation.impl;

import cafe.corrosion.menu.animation.Animation;

public class EaseOutBackAnimation
extends Animation {
    public EaseOutBackAnimation(long time) {
        super(time);
    }

    @Override
    public double calculate() {
        double x2 = this.getProgression();
        double c1 = 1.70158;
        double c3 = c1 + 1.0;
        return 1.0 + c3 * Math.pow(x2 - 1.0, 3.0) + c1 * Math.pow(x2 - 1.0, 2.0);
    }
}

