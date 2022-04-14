package me.spec.eris.client.integration.playtime;

public class PlaytimeTracker {

    private long startTime;
    public double hoursPlayed;

    public PlaytimeTracker() {
        this.startTime = System.currentTimeMillis();
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
