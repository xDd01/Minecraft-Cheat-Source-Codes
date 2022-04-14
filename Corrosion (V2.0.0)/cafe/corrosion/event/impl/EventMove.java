/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.event.impl;

import cafe.corrosion.event.Event;

public class EventMove
extends Event {
    private double x;
    private double y;
    private double z;

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public void setX(double x2) {
        this.x = x2;
    }

    public void setY(double y2) {
        this.y = y2;
    }

    public void setZ(double z2) {
        this.z = z2;
    }

    public EventMove(double x2, double y2, double z2) {
        this.x = x2;
        this.y = y2;
        this.z = z2;
    }
}

