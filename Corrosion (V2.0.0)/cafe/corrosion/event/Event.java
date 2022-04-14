/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.event;

public abstract class Event {
    protected boolean cancelled;

    public boolean isCancelled() {
        return this.cancelled;
    }
}

