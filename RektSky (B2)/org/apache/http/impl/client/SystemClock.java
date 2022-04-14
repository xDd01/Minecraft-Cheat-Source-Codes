package org.apache.http.impl.client;

class SystemClock implements Clock
{
    @Override
    public long getCurrentTime() {
        return System.currentTimeMillis();
    }
}
