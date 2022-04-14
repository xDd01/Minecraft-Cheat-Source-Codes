/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.timer;

public class Stopwatch {
    private long startTime = System.currentTimeMillis();

    public long getTime() {
        return this.startTime;
    }

    public void reset() {
        this.startTime = System.currentTimeMillis();
    }

    public boolean hasElapsed(long time) {
        return System.currentTimeMillis() - this.startTime >= time;
    }
}

