/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.animation.impl;

import cafe.corrosion.menu.animation.Animation;

public class ExpandAnimation
extends Animation {
    private float x;
    private float y;
    private long lastMS;

    public ExpandAnimation(float x2, float y2) {
        super(400L);
        this.x = x2;
        this.y = y2;
    }

    public void expand(float targetX, float targetY, float xSpeed, float ySpeed) {
        long currentMS = System.currentTimeMillis();
        long delta = currentMS - this.lastMS;
        if (delta > 60L) {
            delta = 16L;
        }
        this.lastMS = currentMS;
        int deltaX = (int)(Math.abs(targetX - this.x) * xSpeed);
        int deltaY = (int)(Math.abs(targetY - this.y) * ySpeed);
        this.x = this.calculateCompensation(targetX, this.x, delta, deltaX);
        this.y = this.calculateCompensation(targetY, this.y, delta, deltaY);
    }

    public float calculateCompensation(float target, float current, long delta, int speed) {
        float diff = current - target;
        if (delta < 1L) {
            delta = 1L;
        }
        if (delta > 1000L) {
            delta = 16L;
        }
        double max = Math.max((double)((long)speed * delta) / 16.666666666666668, 0.5);
        if (diff > (float)speed) {
            if ((current = (float)((double)current - max)) < target) {
                current = target;
            }
        } else if (diff < (float)(-speed)) {
            if ((current = (float)((double)current + max)) > target) {
                current = target;
            }
        } else {
            current = target;
        }
        return current;
    }

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    public void setX(float x2) {
        this.x = x2;
    }

    public void setY(float y2) {
        this.y = y2;
    }

    @Override
    public double calculate() {
        return 0.0;
    }
}

