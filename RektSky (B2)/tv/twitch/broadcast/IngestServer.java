package tv.twitch.broadcast;

public class IngestServer
{
    public String serverName;
    public String serverUrl;
    public boolean defaultServer;
    public float bitrateKbps;
    
    public IngestServer() {
        this.bitrateKbps = 0.0f;
    }
}
