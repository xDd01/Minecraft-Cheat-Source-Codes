package org.neverhook.client.helpers.misc;

import org.neverhook.client.helpers.Helper;

public class TimerHelper implements Helper {

    private long ms = getCurrentMS();

    private long getCurrentMS() {
        return System.currentTimeMillis();
    }

    public boolean hasReached(float milliseconds) {
        return getCurrentMS() - ms > milliseconds;
    }

    public void reset() {
        ms = getCurrentMS();
    }

    public long getTime() {
        return getCurrentMS() - ms;
    }
}
