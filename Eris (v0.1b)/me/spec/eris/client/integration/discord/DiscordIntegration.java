package me.spec.eris.client.integration.discord;

import libraries.discordrpc.DiscordEventHandlers;
import libraries.discordrpc.DiscordRPC;
import libraries.discordrpc.DiscordRichPresence;
import me.spec.eris.utils.world.TimerUtils;

public class DiscordIntegration {

    private boolean isRunning;
    private TimerUtils timerUtils = new TimerUtils();

    public void setRunning(boolean running) {
        isRunning = running;
    }

    public DiscordIntegration() {
    	//loadPresence();
    }

    public void loadPresence() {
    	setRunning(true);
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) -> {}).build();
        DiscordRPC.discordInitialize("802394422058287124", handlers, true);

        new Thread("Protection lmfao") {
            @Override
            public void run() {
                DiscordRPC.discordRunCallbacks();
                timerUtils.reset();
            }
        }.start();
        new Thread("ez crack") { 
        }.start();
    }

    public void stopRPC() {
        setRunning(false);
        DiscordRPC.discordShutdown();
    }

    public void callbackRPC() {
        while(isRunning) {
            if(timerUtils.hasReached(50000)) {
                DiscordRPC.discordRunCallbacks();
                timerUtils.reset();
            }
        }
    }

    public void update(String state, String details) {
        if(isRunning) {
            DiscordRichPresence newPresence = new DiscordRichPresence.Builder(state).setDetails(details).setBigImage("rpc-image", "your gay").build();
            DiscordRPC.discordUpdatePresence(newPresence);
        }
    }
}
