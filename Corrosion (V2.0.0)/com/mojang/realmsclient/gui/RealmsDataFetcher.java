/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.realms.Realms;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.Display;

public class RealmsDataFetcher {
    private static final Logger LOGGER = LogManager.getLogger();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
    private static final int SERVER_UPDATE_INTERVAL = 60;
    private static final int PENDING_INVITES_INTERVAL = 10;
    private static final int TRIAL_UPDATE_INTERVAL = 60;
    private volatile boolean stopped = true;
    private ServerListUpdateTask serverListUpdateTask = new ServerListUpdateTask();
    private PendingInviteUpdateTask pendingInviteUpdateTask = new PendingInviteUpdateTask();
    private TrialAvailabilityTask trialAvailabilityTask = new TrialAvailabilityTask();
    private Set<RealmsServer> removedServers = Sets.newHashSet();
    private List<RealmsServer> servers = Lists.newArrayList();
    private int pendingInvitesCount;
    private boolean trialAvailable = false;
    private ScheduledFuture<?> serverListScheduledFuture;
    private ScheduledFuture<?> pendingInviteScheduledFuture;
    private ScheduledFuture<?> trialAvailableScheduledFuture;
    private Map<String, Boolean> fetchStatus = new ConcurrentHashMap<String, Boolean>(Task.values().length);

    public RealmsDataFetcher() {
        this.scheduleTasks();
    }

    public synchronized void init() {
        if (this.stopped) {
            this.stopped = false;
            this.cancelTasks();
            this.scheduleTasks();
        }
    }

    public synchronized boolean isFetchedSinceLastTry(Task task) {
        Boolean result = this.fetchStatus.get(task.toString());
        return result == null ? false : result;
    }

    public synchronized void markClean() {
        for (String task : this.fetchStatus.keySet()) {
            this.fetchStatus.put(task, false);
        }
    }

    public synchronized void forceUpdate() {
        this.stop();
        this.init();
    }

    public synchronized List<RealmsServer> getServers() {
        return Lists.newArrayList(this.servers);
    }

    public int getPendingInvitesCount() {
        return this.pendingInvitesCount;
    }

    public boolean isTrialAvailable() {
        return this.trialAvailable;
    }

    public synchronized void stop() {
        this.stopped = true;
        this.cancelTasks();
    }

    private void scheduleTasks() {
        for (Task task : Task.values()) {
            this.fetchStatus.put(task.toString(), false);
        }
        this.serverListScheduledFuture = this.scheduler.scheduleAtFixedRate(this.serverListUpdateTask, 0L, 60L, TimeUnit.SECONDS);
        this.pendingInviteScheduledFuture = this.scheduler.scheduleAtFixedRate(this.pendingInviteUpdateTask, 0L, 10L, TimeUnit.SECONDS);
        this.trialAvailableScheduledFuture = this.scheduler.scheduleAtFixedRate(this.trialAvailabilityTask, 0L, 60L, TimeUnit.SECONDS);
    }

    private void cancelTasks() {
        try {
            this.serverListScheduledFuture.cancel(false);
            this.pendingInviteScheduledFuture.cancel(false);
            this.trialAvailableScheduledFuture.cancel(false);
        }
        catch (Exception e2) {
            LOGGER.error("Failed to cancel Realms tasks");
        }
    }

    private synchronized void setServers(List<RealmsServer> newServers) {
        int removedCnt = 0;
        for (RealmsServer server : this.removedServers) {
            if (!newServers.remove(server)) continue;
            ++removedCnt;
        }
        if (removedCnt == 0) {
            this.removedServers.clear();
        }
        this.servers = newServers;
    }

    private synchronized void setTrialAvailabile(boolean trialAvailabile) {
        this.trialAvailable = trialAvailabile;
    }

    public synchronized void removeItem(RealmsServer server) {
        this.servers.remove(server);
        this.removedServers.add(server);
    }

    private void sort(List<RealmsServer> servers) {
        Collections.sort(servers, new RealmsServer.McoServerComparator(Realms.getName()));
    }

    private boolean isActive() {
        return !this.stopped && Display.isActive();
    }

    public static enum Task {
        SERVER_LIST,
        PENDING_INVITE,
        TRIAL_AVAILABLE;

    }

    private class TrialAvailabilityTask
    implements Runnable {
        private TrialAvailabilityTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.getTrialAvailable();
            }
        }

        private void getTrialAvailable() {
            try {
                RealmsClient client = RealmsClient.createRealmsClient();
                if (client != null) {
                    RealmsDataFetcher.this.trialAvailable = client.trialAvailable();
                    RealmsDataFetcher.this.fetchStatus.put(Task.TRIAL_AVAILABLE.toString(), true);
                }
            }
            catch (RealmsServiceException e2) {
                LOGGER.error("Couldn't get trial availability", (Throwable)e2);
            }
            catch (IOException e3) {
                LOGGER.error("Couldn't parse response from checking trial availability");
            }
        }
    }

    private class PendingInviteUpdateTask
    implements Runnable {
        private PendingInviteUpdateTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.updatePendingInvites();
            }
        }

        private void updatePendingInvites() {
            try {
                RealmsClient client = RealmsClient.createRealmsClient();
                if (client != null) {
                    RealmsDataFetcher.this.pendingInvitesCount = client.pendingInvitesCount();
                    RealmsDataFetcher.this.fetchStatus.put(Task.PENDING_INVITE.toString(), true);
                }
            }
            catch (RealmsServiceException e2) {
                LOGGER.error("Couldn't get pending invite count", (Throwable)e2);
            }
        }
    }

    private class ServerListUpdateTask
    implements Runnable {
        private ServerListUpdateTask() {
        }

        @Override
        public void run() {
            if (RealmsDataFetcher.this.isActive()) {
                this.updateServersList();
            }
        }

        private void updateServersList() {
            try {
                RealmsClient client = RealmsClient.createRealmsClient();
                if (client != null) {
                    List<RealmsServer> servers = client.listWorlds().servers;
                    if (servers != null) {
                        RealmsDataFetcher.this.sort(servers);
                        RealmsDataFetcher.this.setServers(servers);
                        RealmsDataFetcher.this.fetchStatus.put(Task.SERVER_LIST.toString(), true);
                    } else {
                        LOGGER.warn("Realms server list was null or empty");
                    }
                }
            }
            catch (RealmsServiceException e2) {
                LOGGER.error("Couldn't get server list", (Throwable)e2);
            }
            catch (IOException e3) {
                LOGGER.error("Couldn't parse response from server getting list");
            }
        }
    }
}

