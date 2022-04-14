/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.realmsclient.RealmsVersion;
import com.mojang.realmsclient.client.Ping;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.PingResult;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RegionPingResult;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.mojang.realmsclient.gui.RealmsDataFetcher;
import com.mojang.realmsclient.gui.screens.RealmsBuyRealmsScreen;
import com.mojang.realmsclient.gui.screens.RealmsClientOutdatedScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsCreateRealmScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsParentalConsentScreen;
import com.mojang.realmsclient.gui.screens.RealmsPendingInvitesScreen;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsUtil;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsScrolledSelectionList;
import net.minecraft.realms.RealmsServerStatusPinger;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class RealmsMainScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean overrideConfigure = false;
    private static boolean stageEnabled = false;
    private boolean dontSetConnectedToRealms = false;
    protected static final int BUTTON_BACK_ID = 0;
    protected static final int BUTTON_PLAY_ID = 1;
    protected static final int BUTTON_CONFIGURE_ID = 2;
    protected static final int BUTTON_LEAVE_ID = 3;
    protected static final int BUTTON_BUY_ID = 4;
    protected static final int RESOURCEPACK_ID = 100;
    private RealmsServer resourcePackServer;
    private static final String ON_ICON_LOCATION = "realms:textures/gui/realms/on_icon.png";
    private static final String OFF_ICON_LOCATION = "realms:textures/gui/realms/off_icon.png";
    private static final String EXPIRED_ICON_LOCATION = "realms:textures/gui/realms/expired_icon.png";
    private static final String INVITATION_ICONS_LOCATION = "realms:textures/gui/realms/invitation_icons.png";
    private static final String INVITE_ICON_LOCATION = "realms:textures/gui/realms/invite_icon.png";
    private static final String WORLDICON_LOCATION = "realms:textures/gui/realms/world_icon.png";
    private static final String LOGO_LOCATION = "realms:textures/gui/title/realms.png";
    private static RealmsDataFetcher realmsDataFetcher = new RealmsDataFetcher();
    private static RealmsServerStatusPinger statusPinger = new RealmsServerStatusPinger();
    private static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build());
    private static int lastScrollYPosition = -1;
    private RealmsScreen lastScreen;
    private volatile ServerSelectionList serverSelectionList;
    private long selectedServerId = -1L;
    private RealmsButton configureButton;
    private RealmsButton leaveButton;
    private RealmsButton playButton;
    private RealmsButton buyButton;
    private String toolTip;
    private List<RealmsServer> realmsServers = Lists.newArrayList();
    private static final String mcoInfoUrl = "https://minecraft.net/realms";
    private volatile int numberOfPendingInvites = 0;
    private int animTick;
    private static volatile boolean mcoEnabled;
    private static volatile boolean mcoEnabledCheck;
    private static boolean checkedMcoAvailability;
    private static volatile boolean trialsAvailable;
    private static volatile boolean createdTrial;
    private static final ReentrantLock trialLock;
    private static RealmsScreen realmsGenericErrorScreen;
    private static boolean regionsPinged;
    private boolean onLink = false;
    private int mindex = 0;
    private char[] mchars = new char[]{'3', '2', '1', '4', '5', '6'};
    private int sindex = 0;
    private char[] schars = new char[]{'9', '8', '7', '1', '2', '3'};

    public RealmsMainScreen(RealmsScreen lastScreen) {
        this.lastScreen = lastScreen;
        this.checkIfMcoEnabled();
    }

    @Override
    public void mouseEvent() {
        super.mouseEvent();
        this.serverSelectionList.mouseEvent();
    }

    @Override
    public void init() {
        if (!this.dontSetConnectedToRealms) {
            Realms.setConnectedToRealms(false);
        }
        if (realmsGenericErrorScreen != null) {
            Realms.setScreen(realmsGenericErrorScreen);
            return;
        }
        Keyboard.enableRepeatEvents(true);
        this.buttonsClear();
        this.postInit();
        if (this.isMcoEnabled()) {
            realmsDataFetcher.init();
        }
    }

    public void postInit() {
        RealmsServer server;
        this.playButton = RealmsMainScreen.newButton(1, this.width() / 2 - 154, this.height() - 52, 154, 20, RealmsMainScreen.getLocalizedString("mco.selectServer.play"));
        this.buttonsAdd(this.playButton);
        this.configureButton = RealmsMainScreen.newButton(2, this.width() / 2 + 6, this.height() - 52, 154, 20, RealmsMainScreen.getLocalizedString("mco.selectServer.configure"));
        this.buttonsAdd(this.configureButton);
        this.leaveButton = RealmsMainScreen.newButton(3, this.width() / 2 - 154, this.height() - 28, 102, 20, RealmsMainScreen.getLocalizedString("mco.selectServer.leave"));
        this.buttonsAdd(this.leaveButton);
        this.buyButton = RealmsMainScreen.newButton(4, this.width() / 2 - 48, this.height() - 28, 102, 20, RealmsMainScreen.getLocalizedString("mco.selectServer.buy"));
        this.buttonsAdd(this.buyButton);
        this.buttonsAdd(RealmsMainScreen.newButton(0, this.width() / 2 + 58, this.height() - 28, 102, 20, RealmsMainScreen.getLocalizedString("gui.back")));
        this.serverSelectionList = new ServerSelectionList();
        if (lastScrollYPosition != -1) {
            this.serverSelectionList.scroll(lastScrollYPosition);
        }
        this.playButton.active((server = this.findServer(this.selectedServerId)) != null && server.state == RealmsServer.State.OPEN && !server.expired);
        this.configureButton.active(overrideConfigure || server != null && server.state != RealmsServer.State.ADMIN_LOCK && server.ownerUUID.equals(Realms.getUUID()));
        this.leaveButton.active(server != null && !server.ownerUUID.equals(Realms.getUUID()));
    }

    @Override
    public void tick() {
        ++this.animTick;
        if (this.noParentalConsent()) {
            Realms.setScreen(new RealmsParentalConsentScreen(this.lastScreen));
        }
        if (!this.isMcoEnabled()) {
            return;
        }
        realmsDataFetcher.init();
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.SERVER_LIST)) {
            List<RealmsServer> newServers = realmsDataFetcher.getServers();
            boolean ownsNonExpiredRealmServer = false;
            block0: for (RealmsServer retrievedServer : newServers) {
                if (this.isSelfOwnedNonExpiredServer(retrievedServer)) {
                    ownsNonExpiredRealmServer = true;
                }
                for (RealmsServer oldServer : this.realmsServers) {
                    if (retrievedServer.id != oldServer.id) continue;
                    retrievedServer.latestStatFrom(oldServer);
                    continue block0;
                }
            }
            this.realmsServers = newServers;
            if (!regionsPinged && ownsNonExpiredRealmServer) {
                regionsPinged = true;
                this.pingRegions();
            }
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.PENDING_INVITE)) {
            this.numberOfPendingInvites = realmsDataFetcher.getPendingInvitesCount();
        }
        if (realmsDataFetcher.isFetchedSinceLastTry(RealmsDataFetcher.Task.TRIAL_AVAILABLE) && !createdTrial) {
            trialsAvailable = realmsDataFetcher.isTrialAvailable();
        }
        realmsDataFetcher.markClean();
    }

    private void pingRegions() {
        new Thread(){

            @Override
            public void run() {
                List<RegionPingResult> regionPingResultList = Ping.pingAllRegions();
                RealmsClient client = RealmsClient.createRealmsClient();
                PingResult pingResult = new PingResult();
                pingResult.pingResults = regionPingResultList;
                pingResult.worldIds = RealmsMainScreen.this.getOwnedNonExpiredWorldIds();
                try {
                    client.sendPingResults(pingResult);
                }
                catch (Throwable t2) {
                    LOGGER.warn("Could not send ping result to Realms: ", t2);
                }
            }
        }.start();
    }

    private List<Long> getOwnedNonExpiredWorldIds() {
        ArrayList<Long> ids = new ArrayList<Long>();
        for (RealmsServer server : this.realmsServers) {
            if (!this.isSelfOwnedNonExpiredServer(server)) continue;
            ids.add(server.id);
        }
        return ids;
    }

    private boolean isMcoEnabled() {
        return mcoEnabled;
    }

    private boolean noParentalConsent() {
        return mcoEnabledCheck && !mcoEnabled;
    }

    @Override
    public void removed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void buttonClicked(RealmsButton button) {
        if (!button.active()) {
            return;
        }
        switch (button.id()) {
            case 1: {
                this.play(this.findServer(this.selectedServerId));
                break;
            }
            case 2: {
                this.configureClicked();
                break;
            }
            case 3: {
                this.leaveClicked();
                break;
            }
            case 4: {
                this.saveListScrollPosition();
                this.stopRealmsFetcherAndPinger();
                Realms.setScreen(new RealmsBuyRealmsScreen(this));
                break;
            }
            case 0: {
                this.stopRealmsFetcherAndPinger();
                Realms.setScreen(this.lastScreen);
                break;
            }
            default: {
                return;
            }
        }
    }

    private void createTrial() {
        if (createdTrial) {
            trialsAvailable = false;
            return;
        }
        final RealmsMainScreen mainScreen = this;
        new Thread("Realms-create-trial"){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void run() {
                try {
                    if (!trialLock.tryLock(10L, TimeUnit.MILLISECONDS)) {
                        return;
                    }
                    RealmsClient client = RealmsClient.createRealmsClient();
                    trialsAvailable = false;
                    if (client.createTrial().booleanValue()) {
                        createdTrial = true;
                        realmsDataFetcher.forceUpdate();
                    } else {
                        Realms.setScreen(new RealmsGenericErrorScreen(RealmsScreen.getLocalizedString("mco.trial.unavailable"), mainScreen));
                    }
                }
                catch (RealmsServiceException e2) {
                    LOGGER.error("Trials wasn't available: " + e2.toString());
                    Realms.setScreen(new RealmsGenericErrorScreen(e2, (RealmsScreen)RealmsMainScreen.this));
                }
                catch (IOException e3) {
                    LOGGER.error("Couldn't parse response when trying to create trial: " + e3.toString());
                    trialsAvailable = false;
                }
                catch (InterruptedException e4) {
                    LOGGER.error("Trial Interrupted exception: " + e4.toString());
                }
                finally {
                    if (trialLock.isHeldByCurrentThread()) {
                        trialLock.unlock();
                    }
                }
            }
        }.start();
    }

    private void checkIfMcoEnabled() {
        if (!checkedMcoAvailability) {
            checkedMcoAvailability = true;
            new Thread("MCO Availability Checker #1"){

                @Override
                public void run() {
                    RealmsClient client = RealmsClient.createRealmsClient();
                    try {
                        RealmsClient.CompatibleVersionResponse versionResponse = client.clientCompatible();
                        if (versionResponse.equals((Object)RealmsClient.CompatibleVersionResponse.OUTDATED)) {
                            Realms.setScreen(realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, true));
                            return;
                        }
                        if (versionResponse.equals((Object)RealmsClient.CompatibleVersionResponse.OTHER)) {
                            Realms.setScreen(realmsGenericErrorScreen = new RealmsClientOutdatedScreen(RealmsMainScreen.this.lastScreen, false));
                            return;
                        }
                    }
                    catch (RealmsServiceException e2) {
                        checkedMcoAvailability = false;
                        LOGGER.error("Couldn't connect to realms: ", e2.toString());
                        if (e2.httpResultCode == 401) {
                            realmsGenericErrorScreen = new RealmsGenericErrorScreen(e2, RealmsMainScreen.this.lastScreen);
                        }
                        Realms.setScreen(new RealmsGenericErrorScreen(e2, RealmsMainScreen.this.lastScreen));
                        return;
                    }
                    catch (IOException e3) {
                        checkedMcoAvailability = false;
                        LOGGER.error("Couldn't connect to realms: ", e3.getMessage());
                        Realms.setScreen(new RealmsGenericErrorScreen(e3.getMessage(), RealmsMainScreen.this.lastScreen));
                        return;
                    }
                    boolean retry = false;
                    for (int i2 = 0; i2 < 3; ++i2) {
                        try {
                            Boolean result = client.mcoEnabled();
                            if (result.booleanValue()) {
                                LOGGER.info("Realms is available for this user");
                                mcoEnabled = true;
                            } else {
                                LOGGER.info("Realms is not available for this user");
                                mcoEnabled = false;
                            }
                            mcoEnabledCheck = true;
                        }
                        catch (RetryCallException e4) {
                            retry = true;
                        }
                        catch (RealmsServiceException e5) {
                            LOGGER.error("Couldn't connect to Realms: " + e5.toString());
                        }
                        catch (IOException e6) {
                            LOGGER.error("Couldn't parse response connecting to Realms: " + e6.getMessage());
                        }
                        if (!retry) break;
                        try {
                            Thread.sleep(5000L);
                            continue;
                        }
                        catch (InterruptedException e7) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }.start();
        }
    }

    private void switchToStage() {
        if (!stageEnabled) {
            new Thread("MCO Stage Availability Checker #1"){

                @Override
                public void run() {
                    RealmsClient client = RealmsClient.createRealmsClient();
                    try {
                        Boolean result = client.stageAvailable();
                        if (result.booleanValue()) {
                            RealmsMainScreen.this.stopRealmsFetcherAndPinger();
                            RealmsClient.switchToStage();
                            LOGGER.info("Switched to stage");
                            realmsDataFetcher.init();
                            stageEnabled = true;
                        } else {
                            stageEnabled = false;
                        }
                    }
                    catch (RealmsServiceException e2) {
                        LOGGER.error("Couldn't connect to Realms: " + e2.toString());
                    }
                    catch (IOException e3) {
                        LOGGER.error("Couldn't parse response connecting to Realms: " + e3.getMessage());
                    }
                }
            }.start();
        }
    }

    private void switchToProd() {
        if (stageEnabled) {
            stageEnabled = false;
            this.stopRealmsFetcherAndPinger();
            RealmsClient.switchToProd();
            realmsDataFetcher.init();
        }
    }

    private void stopRealmsFetcherAndPinger() {
        if (this.isMcoEnabled()) {
            realmsDataFetcher.stop();
            statusPinger.removeAll();
        }
    }

    private void configureClicked() {
        RealmsServer selectedServer = this.findServer(this.selectedServerId);
        if (selectedServer != null && (Realms.getUUID().equals(selectedServer.ownerUUID) || overrideConfigure)) {
            this.stopRealmsFetcherAndPinger();
            this.saveListScrollPosition();
            Realms.setScreen(new RealmsConfigureWorldScreen(this, selectedServer.id));
        }
    }

    private void leaveClicked() {
        RealmsServer selectedServer = this.findServer(this.selectedServerId);
        if (selectedServer != null && !Realms.getUUID().equals(selectedServer.ownerUUID)) {
            this.saveListScrollPosition();
            String line2 = RealmsMainScreen.getLocalizedString("mco.configure.world.leave.question.line1");
            String line3 = RealmsMainScreen.getLocalizedString("mco.configure.world.leave.question.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 3));
        }
    }

    private void saveListScrollPosition() {
        lastScrollYPosition = this.serverSelectionList.getScroll();
    }

    private RealmsServer findServer(long id2) {
        for (RealmsServer server : this.realmsServers) {
            if (server.id != id2) continue;
            return server;
        }
        return null;
    }

    private int findIndex(long serverId) {
        for (int i2 = 0; i2 < this.realmsServers.size(); ++i2) {
            if (this.realmsServers.get((int)i2).id != serverId) continue;
            return i2;
        }
        return -1;
    }

    @Override
    public void confirmResult(boolean result, int id2) {
        if (id2 == 3) {
            if (result) {
                new Thread("Realms-leave-server"){

                    @Override
                    public void run() {
                        try {
                            RealmsServer server = RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId);
                            if (server != null) {
                                RealmsClient client = RealmsClient.createRealmsClient();
                                realmsDataFetcher.removeItem(server);
                                RealmsMainScreen.this.realmsServers.remove(server);
                                client.uninviteMyselfFrom(server.id);
                                realmsDataFetcher.removeItem(server);
                                RealmsMainScreen.this.realmsServers.remove(server);
                                RealmsMainScreen.this.updateSelectedItemPointer();
                            }
                        }
                        catch (RealmsServiceException e2) {
                            LOGGER.error("Couldn't configure world");
                            Realms.setScreen(new RealmsGenericErrorScreen(e2, (RealmsScreen)RealmsMainScreen.this));
                        }
                    }
                }.start();
            }
            Realms.setScreen(this);
        } else if (id2 == 100) {
            if (!result) {
                Realms.setScreen(this);
            } else {
                this.connectToServer(this.resourcePackServer);
            }
        }
    }

    private void updateSelectedItemPointer() {
        int originalIndex = this.findIndex(this.selectedServerId);
        if (this.realmsServers.size() - 1 == originalIndex) {
            --originalIndex;
        }
        if (this.realmsServers.size() == 0) {
            originalIndex = -1;
        }
        if (originalIndex >= 0 && originalIndex < this.realmsServers.size()) {
            this.selectedServerId = this.realmsServers.get((int)originalIndex).id;
        }
    }

    public void removeSelection() {
        this.selectedServerId = -1L;
    }

    @Override
    public void keyPressed(char ch, int eventKey) {
        switch (eventKey) {
            case 28: 
            case 156: {
                this.mindex = 0;
                this.sindex = 0;
                this.buttonClicked(this.playButton);
                break;
            }
            case 1: {
                this.mindex = 0;
                this.sindex = 0;
                this.stopRealmsFetcherAndPinger();
                Realms.setScreen(this.lastScreen);
                break;
            }
            default: {
                if (this.mchars[this.mindex] == ch) {
                    ++this.mindex;
                    if (this.mindex == this.mchars.length) {
                        this.mindex = 0;
                        overrideConfigure = true;
                    }
                } else {
                    this.mindex = 0;
                }
                if (this.schars[this.sindex] == ch) {
                    ++this.sindex;
                    if (this.sindex == this.schars.length) {
                        this.sindex = 0;
                        if (!stageEnabled) {
                            this.switchToStage();
                        } else {
                            this.switchToProd();
                        }
                    }
                    return;
                }
                this.sindex = 0;
            }
        }
    }

    @Override
    public void render(int xm2, int ym2, float a2) {
        this.toolTip = null;
        this.renderBackground();
        this.serverSelectionList.render(xm2, ym2, a2);
        this.drawRealmsLogo(this.width() / 2 - 50, 7);
        this.renderLink(xm2, ym2);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, xm2, ym2);
        }
        this.drawInvitationPendingIcon(xm2, ym2);
        if (stageEnabled) {
            this.renderStage();
        }
        super.render(xm2, ym2, a2);
    }

    private void drawRealmsLogo(int x2, int y2) {
        RealmsScreen.bind(LOGO_LOCATION);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RealmsScreen.blit(x2 * 2, y2 * 2 - 5, 0.0f, 0.0f, 200, 50, 200.0f, 50.0f);
        GL11.glPopMatrix();
    }

    @Override
    public void mouseClicked(int x2, int y2, int buttonNum) {
        if (this.inPendingInvitationArea(x2, y2)) {
            this.stopRealmsFetcherAndPinger();
            RealmsPendingInvitesScreen pendingInvitationScreen = new RealmsPendingInvitesScreen(this.lastScreen);
            Realms.setScreen(pendingInvitationScreen);
        }
        if (this.onLink) {
            RealmsUtil.browseTo(mcoInfoUrl);
        }
    }

    private void drawInvitationPendingIcon(int xm2, int ym2) {
        int pendingInvitesCount = this.numberOfPendingInvites;
        boolean hovering = this.inPendingInvitationArea(xm2, ym2);
        int baseX = this.width() / 2 + 50;
        int baseY = 8;
        if (pendingInvitesCount != 0) {
            float scale = 0.25f + (1.0f + RealmsMth.sin((float)this.animTick * 0.5f)) * 0.25f;
            int color = 0xFF000000 | (int)(scale * 64.0f) << 16 | (int)(scale * 64.0f) << 8 | (int)(scale * 64.0f) << 0;
            this.fillGradient(baseX - 2, 6, baseX + 18, 26, color, color);
            color = 0xFF000000 | (int)(scale * 255.0f) << 16 | (int)(scale * 255.0f) << 8 | (int)(scale * 255.0f) << 0;
            this.fillGradient(baseX - 2, 6, baseX + 18, 7, color, color);
            this.fillGradient(baseX - 2, 6, baseX - 1, 26, color, color);
            this.fillGradient(baseX + 17, 6, baseX + 18, 26, color, color);
            this.fillGradient(baseX - 2, 25, baseX + 18, 26, color, color);
        }
        RealmsScreen.bind(INVITE_ICON_LOCATION);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        RealmsScreen.blit(baseX, 2, hovering ? 16.0f : 0.0f, 0.0f, 15, 25, 31.0f, 25.0f);
        GL11.glPopMatrix();
        if (pendingInvitesCount != 0) {
            int spritePos = (Math.min(pendingInvitesCount, 6) - 1) * 8;
            int yOff = (int)(Math.max(0.0f, Math.max(RealmsMth.sin((float)(10 + this.animTick) * 0.57f), RealmsMth.cos((float)this.animTick * 0.35f))) * -6.0f);
            RealmsScreen.bind(INVITATION_ICONS_LOCATION);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPushMatrix();
            RealmsScreen.blit(baseX + 4, 12 + yOff, spritePos, hovering ? 8.0f : 0.0f, 8, 8, 48.0f, 16.0f);
            GL11.glPopMatrix();
        }
        if (hovering) {
            int rx = xm2 + 12;
            int ry2 = ym2 - 12;
            String message = pendingInvitesCount == 0 ? RealmsMainScreen.getLocalizedString("mco.invites.nopending") : RealmsMainScreen.getLocalizedString("mco.invites.pending");
            int width = this.fontWidth(message);
            this.fillGradient(rx - 3, ry2 - 3, rx + width + 3, ry2 + 8 + 3, -1073741824, -1073741824);
            this.fontDrawShadow(message, rx, ry2, -1);
        }
    }

    private boolean inPendingInvitationArea(int xm2, int ym2) {
        int x1 = this.width() / 2 + 50;
        int x2 = this.width() / 2 + 66;
        int y1 = 13;
        int y2 = 27;
        return x1 <= xm2 && xm2 <= x2 && y1 <= ym2 && ym2 <= y2;
    }

    public void play(RealmsServer server) {
        if (server != null) {
            this.stopRealmsFetcherAndPinger();
            this.dontSetConnectedToRealms = true;
            if (server.resourcePackUrl != null && server.resourcePackHash != null) {
                this.resourcePackServer = server;
                this.saveListScrollPosition();
                String line2 = RealmsMainScreen.getLocalizedString("mco.configure.world.resourcepack.question.line1");
                String line3 = RealmsMainScreen.getLocalizedString("mco.configure.world.resourcepack.question.line2");
                Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 100));
            } else {
                this.connectToServer(server);
            }
        }
    }

    private void connectToServer(RealmsServer server) {
        RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this, new RealmsTasks.RealmsConnectTask(this, server));
        longRunningMcoTaskScreen.start();
        Realms.setScreen(longRunningMcoTaskScreen);
    }

    private boolean isSelfOwnedServer(RealmsServer serverData) {
        return serverData.ownerUUID != null && serverData.ownerUUID.equals(Realms.getUUID());
    }

    private boolean isSelfOwnedNonExpiredServer(RealmsServer serverData) {
        return serverData.ownerUUID != null && serverData.ownerUUID.equals(Realms.getUUID()) && !serverData.expired;
    }

    private void drawExpired(int x2, int y2, int xm2, int ym2) {
        RealmsScreen.bind(EXPIRED_ICON_LOCATION);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RealmsScreen.blit(x2 * 2, y2 * 2, 0.0f, 0.0f, 15, 15, 15.0f, 15.0f);
        GL11.glPopMatrix();
        if (xm2 >= x2 && xm2 <= x2 + 9 && ym2 >= y2 && ym2 <= y2 + 9 && ym2 < this.height() - 64 && ym2 > 32) {
            this.toolTip = RealmsMainScreen.getLocalizedString("mco.selectServer.expired");
        }
    }

    private void drawExpiring(int x2, int y2, int xm2, int ym2, int daysLeft) {
        if (this.animTick % 20 < 10) {
            RealmsScreen.bind(ON_ICON_LOCATION);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            RealmsScreen.blit(x2 * 2, y2 * 2, 0.0f, 0.0f, 15, 15, 15.0f, 15.0f);
            GL11.glPopMatrix();
        }
        if (xm2 >= x2 && xm2 <= x2 + 9 && ym2 >= y2 && ym2 <= y2 + 9 && ym2 < this.height() - 64 && ym2 > 32) {
            this.toolTip = daysLeft == 0 ? RealmsMainScreen.getLocalizedString("mco.selectServer.expires.soon") : (daysLeft == 1 ? RealmsMainScreen.getLocalizedString("mco.selectServer.expires.day") : RealmsMainScreen.getLocalizedString("mco.selectServer.expires.days", daysLeft));
        }
    }

    private void drawOpen(int x2, int y2, int xm2, int ym2) {
        RealmsScreen.bind(ON_ICON_LOCATION);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RealmsScreen.blit(x2 * 2, y2 * 2, 0.0f, 0.0f, 15, 15, 15.0f, 15.0f);
        GL11.glPopMatrix();
        if (xm2 >= x2 && xm2 <= x2 + 9 && ym2 >= y2 && ym2 <= y2 + 9 && ym2 < this.height() - 64 && ym2 > 32) {
            this.toolTip = RealmsMainScreen.getLocalizedString("mco.selectServer.open");
        }
    }

    private void drawClose(int x2, int y2, int xm2, int ym2) {
        RealmsScreen.bind(OFF_ICON_LOCATION);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RealmsScreen.blit(x2 * 2, y2 * 2, 0.0f, 0.0f, 15, 15, 15.0f, 15.0f);
        GL11.glPopMatrix();
        if (xm2 >= x2 && xm2 <= x2 + 9 && ym2 >= y2 && ym2 <= y2 + 9 && ym2 < this.height() - 64 && ym2 > 32) {
            this.toolTip = RealmsMainScreen.getLocalizedString("mco.selectServer.closed");
        }
    }

    private void drawLocked(int x2, int y2, int xm2, int ym2) {
        RealmsScreen.bind(OFF_ICON_LOCATION);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        RealmsScreen.blit(x2 * 2, y2 * 2, 0.0f, 0.0f, 15, 15, 15.0f, 15.0f);
        GL11.glPopMatrix();
        if (xm2 >= x2 && xm2 <= x2 + 9 && ym2 >= y2 && ym2 <= y2 + 9 && ym2 < this.height() - 64 && ym2 > 32) {
            this.toolTip = RealmsMainScreen.getLocalizedString("mco.selectServer.locked");
        }
    }

    protected void renderMousehoverTooltip(String msg, int x2, int y2) {
        if (msg == null) {
            return;
        }
        int rx = x2 + 12;
        int ry2 = y2 - 12;
        int index = 0;
        int width = 0;
        for (String s2 : msg.split("\n")) {
            int the_width = this.fontWidth(s2);
            if (the_width <= width) continue;
            width = the_width;
        }
        for (String s2 : msg.split("\n")) {
            this.fillGradient(rx - 3, ry2 - (index == 0 ? 3 : 0) + index, rx + width + 3, ry2 + 8 + 3 + index, -1073741824, -1073741824);
            this.fontDrawShadow(s2, rx, ry2 + index, 0xFFFFFF);
            index += 10;
        }
    }

    private void renderLink(int xm2, int ym2) {
        String text = RealmsMainScreen.getLocalizedString("mco.selectServer.whatisrealms");
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        int textWidth = this.fontWidth(text);
        int leftPadding = 10;
        int topPadding = 12;
        int x1 = leftPadding;
        int x2 = x1 + textWidth + 1;
        int y1 = topPadding;
        int y2 = y1 + this.fontLineHeight();
        GL11.glTranslatef(x1, y1, 0.0f);
        if (x1 <= xm2 && xm2 <= x2 && y1 <= ym2 && ym2 <= y2) {
            this.onLink = true;
            this.drawString(text, 0, 0, 7107012);
        } else {
            this.onLink = false;
            this.drawString(text, 0, 0, 0x3366BB);
        }
        GL11.glPopMatrix();
    }

    private void renderStage() {
        String text = "STAGE!";
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glPushMatrix();
        GL11.glTranslatef(this.width() / 2 - 25, 20.0f, 0.0f);
        GL11.glRotatef(-20.0f, 0.0f, 0.0f, 1.0f);
        GL11.glScalef(1.5f, 1.5f, 1.5f);
        this.drawString(text, 0, 0, -256);
        GL11.glPopMatrix();
    }

    public RealmsScreen newScreen() {
        return new RealmsMainScreen(this.lastScreen);
    }

    static {
        createdTrial = false;
        trialLock = new ReentrantLock();
        realmsGenericErrorScreen = null;
        regionsPinged = false;
        String version = RealmsVersion.getVersion();
        if (version != null) {
            LOGGER.info("Realms library version == " + version);
        }
    }

    private class ServerSelectionList
    extends RealmsScrolledSelectionList {
        public ServerSelectionList() {
            super(RealmsMainScreen.this.width(), RealmsMainScreen.this.height(), 32, RealmsMainScreen.this.height() - 64, 36);
        }

        @Override
        public int getItemCount() {
            if (trialsAvailable) {
                return RealmsMainScreen.this.realmsServers.size() + 1;
            }
            return RealmsMainScreen.this.realmsServers.size();
        }

        @Override
        public void selectItem(int item, boolean doubleClick, int xMouse, int yMouse) {
            if (trialsAvailable) {
                if (item == 0) {
                    RealmsMainScreen.this.createTrial();
                    return;
                }
                --item;
            }
            if (item >= RealmsMainScreen.this.realmsServers.size()) {
                return;
            }
            RealmsServer server = (RealmsServer)RealmsMainScreen.this.realmsServers.get(item);
            if (server.state == RealmsServer.State.UNINITIALIZED) {
                RealmsMainScreen.this.selectedServerId = -1L;
                RealmsMainScreen.this.stopRealmsFetcherAndPinger();
                Realms.setScreen(new RealmsCreateRealmScreen(server, RealmsMainScreen.this));
            } else {
                RealmsMainScreen.this.selectedServerId = server.id;
            }
            RealmsMainScreen.this.configureButton.active(overrideConfigure || RealmsMainScreen.this.isSelfOwnedServer(server) && server.state != RealmsServer.State.ADMIN_LOCK && server.state != RealmsServer.State.UNINITIALIZED);
            RealmsMainScreen.this.leaveButton.active(!RealmsMainScreen.this.isSelfOwnedServer(server));
            RealmsMainScreen.this.playButton.active(server.state == RealmsServer.State.OPEN && !server.expired);
            if (doubleClick && RealmsMainScreen.this.playButton.active()) {
                RealmsMainScreen.this.play(RealmsMainScreen.this.findServer(RealmsMainScreen.this.selectedServerId));
            }
        }

        @Override
        public boolean isSelectedItem(int item) {
            if (trialsAvailable) {
                if (item == 0) {
                    return false;
                }
                --item;
            }
            return item == RealmsMainScreen.this.findIndex(RealmsMainScreen.this.selectedServerId);
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public void renderBackground() {
            RealmsMainScreen.this.renderBackground();
        }

        @Override
        protected void renderItem(int i2, int x2, int y2, int h2, Tezzelator t2, int mouseX, int mouseY) {
            if (trialsAvailable) {
                if (i2 == 0) {
                    this.renderTrialItem(i2, x2, y2);
                    return;
                }
                --i2;
            }
            if (i2 < RealmsMainScreen.this.realmsServers.size()) {
                this.renderMcoServerItem(i2, x2, y2);
            }
        }

        private void renderTrialItem(int i2, int x2, int y2) {
            int ry2 = y2 + 12;
            int index = 0;
            String msg = RealmsScreen.getLocalizedString("mco.trial.message");
            boolean hovered = false;
            if (x2 <= this.xm() && this.xm() <= this.getScrollbarPosition() && y2 <= this.ym() && this.ym() <= y2 + 32) {
                hovered = true;
            }
            float scale = 0.5f + (1.0f + RealmsMth.sin((float)RealmsMainScreen.this.animTick * 0.25f)) * 0.25f;
            int textColor = hovered ? 0xFF | (int)(127.0f * scale) << 16 | (int)(255.0f * scale) << 8 | (int)(127.0f * scale) : 0xFF000000 | (int)(127.0f * scale) << 16 | (int)(255.0f * scale) << 8 | (int)(127.0f * scale);
            for (String s2 : msg.split("\\\\n")) {
                RealmsMainScreen.this.drawCenteredString(s2, RealmsMainScreen.this.width() / 2, ry2 + index, textColor);
                index += 10;
            }
        }

        private void renderMcoServerItem(int i2, int x2, int y2) {
            int nameColor;
            final RealmsServer serverData = (RealmsServer)RealmsMainScreen.this.realmsServers.get(i2);
            int n2 = nameColor = RealmsMainScreen.this.isSelfOwnedServer(serverData) ? 0x7FFF7F : 0xFFFFFF;
            if (serverData.state == RealmsServer.State.UNINITIALIZED) {
                RealmsScreen.bind(RealmsMainScreen.WORLDICON_LOCATION);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glEnable(3008);
                GL11.glPushMatrix();
                RealmsScreen.blit(x2 + 10, y2 + 6, 0.0f, 0.0f, 40, 20, 40.0f, 20.0f);
                GL11.glPopMatrix();
                float scale = 0.5f + (1.0f + RealmsMth.sin((float)RealmsMainScreen.this.animTick * 0.25f)) * 0.25f;
                int textColor = 0xFF000000 | (int)(127.0f * scale) << 16 | (int)(255.0f * scale) << 8 | (int)(127.0f * scale);
                RealmsMainScreen.this.drawCenteredString(RealmsScreen.getLocalizedString("mco.selectServer.uninitialized"), x2 + 10 + 40 + 75, y2 + 12, textColor);
                return;
            }
            if (serverData.shouldPing(Realms.currentTimeMillis())) {
                serverData.serverPing.lastPingSnapshot = Realms.currentTimeMillis();
                THREAD_POOL.submit(new Runnable(){

                    @Override
                    public void run() {
                        try {
                            statusPinger.pingServer(serverData.ip, serverData.serverPing);
                        }
                        catch (UnknownHostException e2) {
                            LOGGER.error("Pinger: Could not resolve host");
                        }
                    }
                });
            }
            RealmsMainScreen.this.drawString(serverData.getName(), x2 + 2, y2 + 1, nameColor);
            int dx2 = 207;
            int dy2 = 1;
            if (serverData.expired) {
                RealmsMainScreen.this.drawExpired(x2 + dx2, y2 + dy2, this.xm(), this.ym());
            } else if (serverData.state == RealmsServer.State.CLOSED) {
                RealmsMainScreen.this.drawClose(x2 + dx2, y2 + dy2, this.xm(), this.ym());
            } else if (RealmsMainScreen.this.isSelfOwnedServer(serverData) && serverData.daysLeft < 7) {
                this.showStatus(x2 - 14, y2, serverData);
                RealmsMainScreen.this.drawExpiring(x2 + dx2, y2 + dy2, this.xm(), this.ym(), serverData.daysLeft);
            } else if (serverData.state == RealmsServer.State.OPEN) {
                RealmsMainScreen.this.drawOpen(x2 + dx2, y2 + dy2, this.xm(), this.ym());
                this.showStatus(x2 - 14, y2, serverData);
            } else if (serverData.state == RealmsServer.State.ADMIN_LOCK) {
                RealmsMainScreen.this.drawLocked(x2 + dx2, y2 + dy2, this.xm(), this.ym());
            }
            String noPlayers = "0";
            if (!serverData.serverPing.nrOfPlayers.equals(noPlayers)) {
                String coloredNumPlayers = (Object)((Object)ChatFormatting.GRAY) + "" + serverData.serverPing.nrOfPlayers;
                RealmsMainScreen.this.drawString(coloredNumPlayers, x2 + 200 - RealmsMainScreen.this.fontWidth(coloredNumPlayers), y2 + 1, 0x808080);
                if (this.xm() >= x2 + 200 - RealmsMainScreen.this.fontWidth(coloredNumPlayers) && this.xm() <= x2 + 200 && this.ym() >= y2 + 1 && this.ym() <= y2 + 9 && this.ym() < RealmsMainScreen.this.height() - 64 && this.ym() > 32) {
                    RealmsMainScreen.this.toolTip = serverData.serverPing.playerList;
                }
            }
            if (serverData.worldType.equals((Object)RealmsServer.WorldType.MINIGAME)) {
                int motdColor = 9206892;
                if (RealmsMainScreen.this.animTick % 10 < 5) {
                    motdColor = 0xCCAC5C;
                }
                String miniGameStr = RealmsScreen.getLocalizedString("mco.selectServer.minigame") + " ";
                int mgWidth = RealmsMainScreen.this.fontWidth(miniGameStr);
                RealmsMainScreen.this.drawString(miniGameStr, x2 + 2, y2 + 12, motdColor);
                RealmsMainScreen.this.drawString(serverData.getMinigameName(), x2 + 2 + mgWidth, y2 + 12, 0x6C6C6C);
            } else {
                RealmsMainScreen.this.drawString(serverData.getDescription(), x2 + 2, y2 + 12, 0x6C6C6C);
            }
            RealmsMainScreen.this.drawString(serverData.owner, x2 + 2, y2 + 12 + 11, 0x4C4C4C);
            RealmsScreen.bindFace(serverData.ownerUUID, serverData.owner);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            RealmsScreen.blit(x2 - 36, y2, 8.0f, 8.0f, 8, 8, 32, 32, 64.0f, 64.0f);
            RealmsScreen.blit(x2 - 36, y2, 40.0f, 8.0f, 8, 8, 32, 32, 64.0f, 64.0f);
        }

        private void showStatus(int x2, int y2, RealmsServer serverData) {
            if (serverData.ip == null) {
                return;
            }
            if (serverData.status != null) {
                RealmsMainScreen.this.drawString(serverData.status, x2 + 215 - RealmsMainScreen.this.fontWidth(serverData.status), y2 + 1, 0x808080);
            }
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            RealmsScreen.bind("textures/gui/icons.png");
        }
    }
}

