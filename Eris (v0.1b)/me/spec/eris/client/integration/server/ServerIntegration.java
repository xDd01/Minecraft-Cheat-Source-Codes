package me.spec.eris.client.integration.server;

import me.spec.eris.Eris;
import me.spec.eris.client.integration.server.interfaces.Gamemode;
import me.spec.eris.client.integration.server.interfaces.Server;

public class ServerIntegration {

    private Gamemode gamemode = Gamemode.UNSPECIFIED;
    private Server server = Server.IRRELLEVANT;

    public void setServer(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return server;
    }

    public void setGameMode(Gamemode gamemode) {
        this.gamemode = gamemode;
    }

    public Gamemode getGameMode() {
        return gamemode;
    }

    public boolean onServer(String server) {
        return false;
    }
}
