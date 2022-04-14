/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Backup;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.screens.RealmsBackupInfoScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class RealmsBackupScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String PLUS_ICON_LOCATION = "realms:textures/gui/realms/plus_icon.png";
    private static final String RESTORE_ICON_LOCATION = "realms:textures/gui/realms/restore_icon.png";
    private static int lastScrollPosition = -1;
    private final RealmsConfigureWorldScreen lastScreen;
    private List<Backup> backups = Collections.emptyList();
    private String toolTip = null;
    private BackupSelectionList backupSelectionList;
    private int selectedBackup = -1;
    private static final int BACK_BUTTON_ID = 0;
    private static final int RESTORE_BUTTON_ID = 1;
    private static final int DOWNLOAD_BUTTON_ID = 2;
    private RealmsButton downloadButton;
    private Boolean noBackups = false;
    private RealmsServer serverData;
    private static final String UPLOADED_KEY = "Uploaded";

    public RealmsBackupScreen(RealmsConfigureWorldScreen lastscreen, RealmsServer serverData) {
        this.lastScreen = lastscreen;
        this.serverData = serverData;
    }

    @Override
    public void mouseEvent() {
        super.mouseEvent();
        this.backupSelectionList.mouseEvent();
    }

    @Override
    public void init() {
        Keyboard.enableRepeatEvents(true);
        this.buttonsClear();
        this.backupSelectionList = new BackupSelectionList();
        if (lastScrollPosition != -1) {
            this.backupSelectionList.scroll(lastScrollPosition);
        }
        new Thread("Realms-fetch-backups"){

            @Override
            public void run() {
                RealmsClient client = RealmsClient.createRealmsClient();
                try {
                    RealmsBackupScreen.this.backups = client.backupsFor((long)((RealmsBackupScreen)RealmsBackupScreen.this).serverData.id).backups;
                    RealmsBackupScreen.this.noBackups = RealmsBackupScreen.this.backups.size() == 0;
                    RealmsBackupScreen.this.generateChangeList();
                }
                catch (RealmsServiceException e2) {
                    LOGGER.error("Couldn't request backups", (Throwable)e2);
                }
            }
        }.start();
        this.postInit();
    }

    private void generateChangeList() {
        if (this.backups.size() <= 1) {
            return;
        }
        for (int i2 = 0; i2 < this.backups.size() - 1; ++i2) {
            Backup backup = this.backups.get(i2);
            Backup olderBackup = this.backups.get(i2 + 1);
            if (backup.metadata.isEmpty() || olderBackup.metadata.isEmpty()) continue;
            for (String key : backup.metadata.keySet()) {
                if (!key.contains(UPLOADED_KEY) && olderBackup.metadata.containsKey(key)) {
                    if (backup.metadata.get(key).equals(olderBackup.metadata.get(key))) continue;
                    this.addToChangeList(backup, key);
                    continue;
                }
                this.addToChangeList(backup, key);
            }
        }
    }

    private void addToChangeList(Backup backup, String key) {
        if (key.contains(UPLOADED_KEY)) {
            String uploadedTime = DateFormat.getDateTimeInstance(3, 3).format(backup.lastModifiedDate);
            backup.changeList.put(key, uploadedTime);
            backup.setUploadedVersion(true);
        } else {
            backup.changeList.put(key, backup.metadata.get(key));
        }
    }

    private void postInit() {
        this.downloadButton = RealmsBackupScreen.newButton(2, this.width() - 125, 32, 100, 20, RealmsBackupScreen.getLocalizedString("mco.backup.button.download"));
        this.buttonsAdd(this.downloadButton);
        this.buttonsAdd(RealmsBackupScreen.newButton(0, this.width() - 125, this.height() - 35, 85, 20, RealmsBackupScreen.getLocalizedString("gui.back")));
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public void buttonClicked(RealmsButton button) {
        if (!button.active()) {
            return;
        }
        if (button.id() == 0) {
            Realms.setScreen(this.lastScreen);
        } else if (button.id() == 2) {
            this.downloadClicked();
        }
    }

    @Override
    public void keyPressed(char eventCharacter, int eventKey) {
        if (eventKey == 1) {
            Realms.setScreen(this.lastScreen);
        }
    }

    private void restoreClicked(int selectedBackup) {
        if (selectedBackup >= 0 && selectedBackup < this.backups.size() && !this.serverData.expired) {
            this.selectedBackup = selectedBackup;
            Date backupDate = this.backups.get((int)selectedBackup).lastModifiedDate;
            String datePresentation = DateFormat.getDateTimeInstance(3, 3).format(backupDate);
            String age2 = RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - backupDate.getTime());
            String line2 = RealmsBackupScreen.getLocalizedString("mco.configure.world.restore.question.line1", datePresentation, age2);
            String line3 = RealmsBackupScreen.getLocalizedString("mco.configure.world.restore.question.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, line2, line3, true, 1));
        }
    }

    private void downloadClicked() {
        String line2 = RealmsBackupScreen.getLocalizedString("mco.configure.world.restore.download.question.line1");
        String line3 = RealmsBackupScreen.getLocalizedString("mco.configure.world.restore.download.question.line2");
        Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 2));
    }

    private void downloadWorldData() {
        RealmsClient client = RealmsClient.createRealmsClient();
        try {
            String downloadLink = client.download(this.serverData.id);
            Realms.setScreen(new RealmsDownloadLatestWorldScreen(this, downloadLink, this.serverData.name + " (" + this.serverData.slots.get(this.serverData.activeSlot).getSlotName(this.serverData.activeSlot) + ")"));
        }
        catch (RealmsServiceException e2) {
            LOGGER.error("Couldn't download world data");
            Realms.setScreen(new RealmsGenericErrorScreen(e2, (RealmsScreen)this));
        }
    }

    @Override
    public void confirmResult(boolean result, int id2) {
        if (result && id2 == 1) {
            this.restore();
        } else if (result && id2 == 2) {
            this.downloadWorldData();
        } else {
            Realms.setScreen(this);
        }
    }

    private void restore() {
        Backup backup = this.backups.get(this.selectedBackup);
        RestoreTask restoreTask = new RestoreTask(backup);
        RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen.getNewScreen(), restoreTask);
        longRunningMcoTaskScreen.start();
        Realms.setScreen(longRunningMcoTaskScreen);
    }

    @Override
    public void render(int xm2, int ym2, float a2) {
        this.toolTip = null;
        this.renderBackground();
        this.backupSelectionList.render(xm2, ym2, a2);
        this.drawCenteredString(RealmsBackupScreen.getLocalizedString("mco.configure.world.backup"), this.width() / 2, 12, 0xFFFFFF);
        this.drawString(RealmsBackupScreen.getLocalizedString("mco.configure.world.backup"), (this.width() - 150) / 2 - 90, 20, 0xA0A0A0);
        if (this.noBackups.booleanValue()) {
            this.drawString(RealmsBackupScreen.getLocalizedString("mco.backup.nobackups"), 20, this.height() / 2 - 10, 0xFFFFFF);
        }
        this.downloadButton.active(this.noBackups == false);
        super.render(xm2, ym2, a2);
        if (this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, xm2, ym2);
        }
    }

    protected void renderMousehoverTooltip(String msg, int x2, int y2) {
        if (msg == null) {
            return;
        }
        int rx = x2 + 12;
        int ry2 = y2 - 12;
        int width = this.fontWidth(msg);
        this.fillGradient(rx - 3, ry2 - 3, rx + width + 3, ry2 + 8 + 3, -1073741824, -1073741824);
        this.fontDrawShadow(msg, rx, ry2, 0xFFFFFF);
    }

    private class BackupSelectionList
    extends RealmsClickableScrolledSelectionList {
        public BackupSelectionList() {
            super(RealmsBackupScreen.this.width() - 150, RealmsBackupScreen.this.height(), 32, RealmsBackupScreen.this.height() - 15, 36);
        }

        @Override
        public int getItemCount() {
            return RealmsBackupScreen.this.backups.size() + 1;
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 36;
        }

        @Override
        public void renderBackground() {
            RealmsBackupScreen.this.renderBackground();
        }

        @Override
        public void customMouseEvent(int y0, int y1, int headerHeight, float yo2, int itemHeight) {
            if (Mouse.isButtonDown(0) && this.ym() >= y0 && this.ym() <= y1) {
                int x0 = this.width() / 2 - 92;
                int x1 = this.width();
                int clickSlotPos = this.ym() - y0 - headerHeight + (int)yo2 - 4;
                int slot = clickSlotPos / itemHeight;
                if (this.xm() >= x0 && this.xm() <= x1 && slot >= 0 && clickSlotPos >= 0 && slot < this.getItemCount()) {
                    this.itemClicked(clickSlotPos, slot, this.xm(), this.ym(), this.width());
                }
            }
        }

        @Override
        public void renderItem(int i2, int x2, int y2, int h2, int mouseX, int mouseY) {
            x2 += 16;
            if (i2 < RealmsBackupScreen.this.backups.size()) {
                this.renderBackupItem(i2, x2, y2, h2, RealmsBackupScreen.this.width);
            }
        }

        @Override
        public int getScrollbarPosition() {
            return this.width() - 5;
        }

        @Override
        public void itemClicked(int clickSlotPos, int slot, int xm2, int ym2, int width) {
            int infox = this.width() - 40;
            int infoy = clickSlotPos + 30 - this.getScroll();
            int mx2 = infox + 10;
            int my2 = infoy - 3;
            if (xm2 >= infox && xm2 <= infox + 9 && ym2 >= infoy && ym2 <= infoy + 9) {
                if (!((Backup)((RealmsBackupScreen)RealmsBackupScreen.this).backups.get((int)slot)).changeList.isEmpty()) {
                    lastScrollPosition = this.getScroll();
                    Realms.setScreen(new RealmsBackupInfoScreen(RealmsBackupScreen.this, (Backup)RealmsBackupScreen.this.backups.get(slot)));
                }
            } else if (xm2 >= mx2 && xm2 <= mx2 + 9 && ym2 >= my2 && ym2 <= my2 + 9) {
                lastScrollPosition = this.getScroll();
                RealmsBackupScreen.this.restoreClicked(slot);
            }
        }

        private void renderBackupItem(int i2, int x2, int y2, int h2, int width) {
            Backup backup = (Backup)RealmsBackupScreen.this.backups.get(i2);
            int color = backup.isUploadedVersion() ? -8388737 : 0xFFFFFF;
            RealmsBackupScreen.this.drawString("Backup (" + RealmsUtil.convertToAgePresentation(System.currentTimeMillis() - backup.lastModifiedDate.getTime()) + ")", x2 + 2, y2 + 1, color);
            RealmsBackupScreen.this.drawString(this.getMediumDatePresentation(backup.lastModifiedDate), x2 + 2, y2 + 12, 0x4C4C4C);
            int dx2 = this.width() - 30;
            int dy2 = -3;
            int infox = dx2 - 10;
            int infoy = dy2 + 3;
            if (!((RealmsBackupScreen)RealmsBackupScreen.this).serverData.expired) {
                this.drawRestore(dx2, y2 + dy2, this.xm(), this.ym());
            }
            if (!backup.changeList.isEmpty()) {
                this.drawInfo(infox, y2 + infoy, this.xm(), this.ym());
            }
        }

        private String getMediumDatePresentation(Date lastModifiedDate) {
            return DateFormat.getDateTimeInstance(3, 3).format(lastModifiedDate);
        }

        private void drawRestore(int x2, int y2, int xm2, int ym2) {
            boolean hovered = xm2 >= x2 && xm2 <= x2 + 12 && ym2 >= y2 && ym2 <= y2 + 14 && ym2 < RealmsBackupScreen.this.height() - 15 && ym2 > 32;
            RealmsScreen.bind(RealmsBackupScreen.RESTORE_ICON_LOCATION);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            RealmsScreen.blit(x2 * 2, y2 * 2, 0.0f, hovered ? 28.0f : 0.0f, 23, 28, 23.0f, 56.0f);
            GL11.glPopMatrix();
            if (hovered) {
                RealmsBackupScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.backup.button.restore");
            }
        }

        private void drawInfo(int x2, int y2, int xm2, int ym2) {
            boolean hovered = xm2 >= x2 && xm2 <= x2 + 8 && ym2 >= y2 && ym2 <= y2 + 8 && ym2 < RealmsBackupScreen.this.height() - 15 && ym2 > 32;
            RealmsScreen.bind(RealmsBackupScreen.PLUS_ICON_LOCATION);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glPushMatrix();
            GL11.glScalef(0.5f, 0.5f, 0.5f);
            RealmsScreen.blit(x2 * 2, y2 * 2, 0.0f, hovered ? 15.0f : 0.0f, 15, 15, 15.0f, 30.0f);
            GL11.glPopMatrix();
            if (hovered) {
                RealmsBackupScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.backup.changes.tooltip");
            }
        }
    }

    private class RestoreTask
    extends LongRunningTask {
        private final Backup backup;

        private RestoreTask(Backup backup) {
            this.backup = backup;
        }

        @Override
        public void run() {
            this.setTitle(RealmsScreen.getLocalizedString("mco.backup.restoring"));
            for (int i2 = 0; i2 < 6; ++i2) {
                try {
                    if (this.aborted()) {
                        return;
                    }
                    RealmsClient client = RealmsClient.createRealmsClient();
                    client.restoreWorld(((RealmsBackupScreen)RealmsBackupScreen.this).serverData.id, this.backup.backupId);
                    this.pause(1);
                    if (this.aborted()) {
                        return;
                    }
                    Realms.setScreen(RealmsBackupScreen.this.lastScreen.getNewScreen());
                    return;
                }
                catch (RetryCallException e2) {
                    if (this.aborted()) {
                        return;
                    }
                    this.pause(e2.delaySeconds);
                    continue;
                }
                catch (RealmsServiceException e3) {
                    if (this.aborted()) {
                        return;
                    }
                    LOGGER.error("Couldn't restore backup");
                    Realms.setScreen(new RealmsGenericErrorScreen(e3, (RealmsScreen)RealmsBackupScreen.this.lastScreen));
                    return;
                }
                catch (Exception e4) {
                    if (this.aborted()) {
                        return;
                    }
                    LOGGER.error("Couldn't restore backup");
                    this.error(e4.getLocalizedMessage());
                    return;
                }
            }
        }

        private void pause(int pauseSeconds) {
            try {
                Thread.sleep(pauseSeconds * 1000);
            }
            catch (InterruptedException e2) {
                LOGGER.error(e2);
            }
        }
    }
}

