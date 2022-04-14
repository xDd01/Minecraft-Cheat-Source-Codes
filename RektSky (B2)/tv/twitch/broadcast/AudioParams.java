package tv.twitch.broadcast;

public class AudioParams
{
    public boolean audioEnabled;
    public boolean enableMicCapture;
    public boolean enablePlaybackCapture;
    public boolean enablePassthroughAudio;
    
    public AudioParams() {
        this.audioEnabled = true;
        this.enableMicCapture = true;
        this.enablePlaybackCapture = true;
        this.enablePassthroughAudio = false;
    }
}
