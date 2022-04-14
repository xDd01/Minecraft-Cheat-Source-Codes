/*
 * Decompiled with CFR 0.152.
 */
package tv.twitch.broadcast;

import tv.twitch.broadcast.IngestServer;

public class IngestList {
    protected IngestServer[] servers = null;
    protected IngestServer defaultServer = null;

    public IngestServer[] getServers() {
        return this.servers;
    }

    public IngestServer getDefaultServer() {
        return this.defaultServer;
    }

    public IngestList(IngestServer[] ingestServerArray) {
        if (ingestServerArray == null) {
            this.servers = new IngestServer[0];
        } else {
            this.servers = new IngestServer[ingestServerArray.length];
            for (int i2 = 0; i2 < ingestServerArray.length; ++i2) {
                this.servers[i2] = ingestServerArray[i2];
                if (!this.servers[i2].defaultServer) continue;
                this.defaultServer = this.servers[i2];
            }
            if (this.defaultServer == null && this.servers.length > 0) {
                this.defaultServer = this.servers[0];
            }
        }
    }

    public IngestServer getBestServer() {
        if (this.servers == null || this.servers.length == 0) {
            return null;
        }
        IngestServer ingestServer = this.servers[0];
        for (int i2 = 1; i2 < this.servers.length; ++i2) {
            if (!(ingestServer.bitrateKbps < this.servers[i2].bitrateKbps)) continue;
            ingestServer = this.servers[i2];
        }
        return ingestServer;
    }
}

