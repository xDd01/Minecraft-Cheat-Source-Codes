package io.github.nevalackin.client.util.misc;

public final class TimeUtil {

    private long lastMS;
    private long time;

    public boolean reach(final long time) {
        return time() >= time;
    }

    public long time() {
        return System.nanoTime() / 1000000L - time;
    }

    public TimeUtil() {
        lastMS = getCurrentMS();
    }

    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public boolean passed(long millisecond) {
        return getCurrentMS() - lastMS >= millisecond;
    }

    public boolean passed(double millisecond) {
        return getCurrentMS() - lastMS >= millisecond;
    }

    public void reset() {
        lastMS = getCurrentMS();
    }

    public boolean hasTimeElapsed(final long time, final boolean reset) {
        if (this.lastMS > System.currentTimeMillis()) {
            this.lastMS = System.currentTimeMillis();
        }
        if (System.currentTimeMillis() - this.lastMS > time) {
            if (reset) {
                this.reset();
            }
            return true;
        }
        return false;
    }

    public boolean randDelay(double min, double max) {
        double random = (Math.random() * max) + min;
        return getCurrentMS() - this.lastMS >= random;
    }
}
