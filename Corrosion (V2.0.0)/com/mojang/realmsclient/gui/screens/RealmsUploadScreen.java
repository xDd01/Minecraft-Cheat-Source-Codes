/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.client.UploadStatus;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.util.UploadTokenCache;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsLevelSummary;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraft.realms.Tezzelator;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class RealmsUploadScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int CANCEL_BUTTON = 0;
    private static final int BACK_BUTTON = 1;
    private final RealmsResetWorldScreen lastScreen;
    private final RealmsLevelSummary selectedLevel;
    private final long worldId;
    private final int slotId;
    private final UploadStatus uploadStatus;
    private volatile String errorMessage = null;
    private volatile String status = null;
    private volatile String progress = null;
    private volatile boolean cancelled = false;
    private volatile boolean uploadFinished = false;
    private volatile boolean showDots = true;
    private volatile boolean uploadStarted = false;
    private RealmsButton backButton;
    private RealmsButton cancelButton;
    private int animTick = 0;
    private static final String[] DOTS = new String[]{"", ".", ". .", ". . ."};
    private int dotIndex = 0;
    private Long previousWrittenBytes = null;
    private Long previousTimeSnapshot = null;
    private long bytesPersSecond = 0L;
    private static final ReentrantLock uploadLock = new ReentrantLock();

    public RealmsUploadScreen(long worldId, int slotId, RealmsResetWorldScreen lastScreen, RealmsLevelSummary selectedLevel) {
        this.worldId = worldId;
        this.slotId = slotId;
        this.lastScreen = lastScreen;
        this.selectedLevel = selectedLevel;
        this.uploadStatus = new UploadStatus();
    }

    @Override
    public void init() {
        Keyboard.enableRepeatEvents(true);
        this.buttonsClear();
        this.backButton = RealmsUploadScreen.newButton(1, this.width() / 2 - 100, this.height() - 42, 200, 20, RealmsUploadScreen.getLocalizedString("gui.back"));
        this.cancelButton = RealmsUploadScreen.newButton(0, this.width() / 2 - 100, this.height() - 42, 200, 20, RealmsUploadScreen.getLocalizedString("gui.cancel"));
        this.buttonsAdd(this.cancelButton);
        if (!this.uploadStarted) {
            if (this.lastScreen.slot != -1) {
                this.lastScreen.switchSlot(this);
            } else {
                this.upload();
            }
        }
    }

    @Override
    public void confirmResult(boolean result, int buttonId) {
        if (result && !this.uploadStarted) {
            this.uploadStarted = true;
            Realms.setScreen(this);
            this.upload();
        }
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
        if (button.id() == 1) {
            this.lastScreen.confirmResult(true, 0);
        } else if (button.id() == 0) {
            this.cancelled = true;
            Realms.setScreen(this.lastScreen);
        }
    }

    @Override
    public void keyPressed(char ch, int eventKey) {
        if (eventKey == 1) {
            this.cancelled = true;
            Realms.setScreen(this.lastScreen);
        }
    }

    @Override
    public void render(int xm2, int ym2, float a2) {
        this.renderBackground();
        if (!this.uploadFinished && this.uploadStatus.bytesWritten != 0L && this.uploadStatus.bytesWritten.longValue() == this.uploadStatus.totalBytes.longValue()) {
            this.status = RealmsUploadScreen.getLocalizedString("mco.upload.verifying");
        }
        this.drawCenteredString(this.status, this.width() / 2, 50, 0xFFFFFF);
        if (this.showDots) {
            this.drawDots();
        }
        if (this.uploadStatus.bytesWritten != 0L && !this.cancelled) {
            this.drawProgressBar();
            this.drawUploadSpeed();
        }
        if (this.errorMessage != null) {
            this.drawCenteredString(this.errorMessage, this.width() / 2, 110, 0xFF0000);
        }
        super.render(xm2, ym2, a2);
    }

    private void drawDots() {
        int statusWidth = this.fontWidth(this.status);
        if (this.animTick % 10 == 0) {
            ++this.dotIndex;
        }
        this.drawString(DOTS[this.dotIndex % DOTS.length], this.width() / 2 + statusWidth / 2 + 5, 50, 0xFFFFFF);
    }

    private void drawProgressBar() {
        double percentage = this.uploadStatus.bytesWritten.doubleValue() / this.uploadStatus.totalBytes.doubleValue() * 100.0;
        if (percentage > 100.0) {
            percentage = 100.0;
        }
        this.progress = String.format("%.1f", percentage);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glDisable(3553);
        double base = this.width() / 2 - 100;
        double diff = 0.5;
        Tezzelator t2 = Tezzelator.instance;
        t2.begin(7, RealmsDefaultVertexFormat.POSITION_COLOR);
        t2.vertex(base - 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        t2.vertex(base + 200.0 * percentage / 100.0 + 0.5, 95.5, 0.0).color(217, 210, 210, 255).endVertex();
        t2.vertex(base + 200.0 * percentage / 100.0 + 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        t2.vertex(base - 0.5, 79.5, 0.0).color(217, 210, 210, 255).endVertex();
        t2.vertex(base, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        t2.vertex(base + 200.0 * percentage / 100.0, 95.0, 0.0).color(128, 128, 128, 255).endVertex();
        t2.vertex(base + 200.0 * percentage / 100.0, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        t2.vertex(base, 80.0, 0.0).color(128, 128, 128, 255).endVertex();
        t2.end();
        GL11.glEnable(3553);
        this.drawCenteredString(this.progress + " %", this.width() / 2, 84, 0xFFFFFF);
    }

    private void drawUploadSpeed() {
        if (this.animTick % RealmsSharedConstants.TICKS_PER_SECOND == 0) {
            if (this.previousWrittenBytes != null) {
                long timeElapsed = System.currentTimeMillis() - this.previousTimeSnapshot;
                if (timeElapsed == 0L) {
                    timeElapsed = 1L;
                }
                this.bytesPersSecond = 1000L * (this.uploadStatus.bytesWritten - this.previousWrittenBytes) / timeElapsed;
                this.drawUploadSpeed0(this.bytesPersSecond);
            }
            this.previousWrittenBytes = this.uploadStatus.bytesWritten;
            this.previousTimeSnapshot = System.currentTimeMillis();
        } else {
            this.drawUploadSpeed0(this.bytesPersSecond);
        }
    }

    private void drawUploadSpeed0(long bytesPersSecond) {
        if (bytesPersSecond > 0L) {
            int progressLength = this.fontWidth(this.progress);
            String stringPresentation = "(" + RealmsUploadScreen.humanReadableByteCount(bytesPersSecond) + ")";
            this.drawString(stringPresentation, this.width() / 2 + progressLength / 2 + 15, 84, 0xFFFFFF);
        }
    }

    public static String humanReadableByteCount(long bytes) {
        int unit = 1024;
        if (bytes < (long)unit) {
            return bytes + " B";
        }
        int exp = (int)(Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB/s", (double)bytes / Math.pow(unit, exp), pre);
    }

    @Override
    public void mouseEvent() {
        super.mouseEvent();
    }

    @Override
    public void tick() {
        super.tick();
        ++this.animTick;
    }

    private void upload() {
        this.uploadStarted = true;
        new Thread(){

            /*
             * Exception decompiling
             */
            @Override
            public void run() {
                /*
                 * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                 * 
                 * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [2[TRYBLOCK]], but top level block is 47[FORLOOP]
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
                 *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
                 *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
                 *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
                 *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
                 *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
                 *     at org.benf.cfr.reader.Main.main(Main.java:54)
                 */
                throw new IllegalStateException("Decompilation failed");
            }
        }.start();
    }

    private void uploadCancelled(long worldId) {
        this.status = RealmsUploadScreen.getLocalizedString("mco.upload.cancelled");
        String oldToken = UploadTokenCache.get(worldId);
        UploadTokenCache.invalidate(worldId);
        try {
            RealmsClient client = RealmsClient.createRealmsClient();
            client.uploadCancelled(worldId, oldToken);
        }
        catch (RealmsServiceException e2) {
            LOGGER.error("Failed to cancel upload", (Throwable)e2);
        }
    }

    private boolean verify(File archive) {
        return archive.length() < 1048576000L;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private File tarGzipArchive(File pathToDirectoryFile) throws IOException {
        TarArchiveOutputStream tar = null;
        try {
            File file = File.createTempFile("realms-upload-file", ".tar.gz");
            tar = new TarArchiveOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
            this.addFileToTarGz(tar, pathToDirectoryFile.getAbsolutePath(), "world", true);
            tar.finish();
            File file2 = file;
            return file2;
        }
        finally {
            if (tar != null) {
                tar.close();
            }
        }
    }

    private void addFileToTarGz(TarArchiveOutputStream tOut, String path, String base, boolean root) throws IOException {
        if (this.cancelled) {
            return;
        }
        File f2 = new File(path);
        String entryName = root ? base : base + f2.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(f2, entryName);
        tOut.putArchiveEntry(tarEntry);
        if (f2.isFile()) {
            IOUtils.copy(new FileInputStream(f2), tOut);
            tOut.closeArchiveEntry();
        } else {
            tOut.closeArchiveEntry();
            File[] children = f2.listFiles();
            if (children != null) {
                for (File child : children) {
                    this.addFileToTarGz(tOut, child.getAbsolutePath(), entryName + "/", false);
                }
            }
        }
    }

    static /* synthetic */ long access$000(RealmsUploadScreen x0) {
        return x0.worldId;
    }

    static /* synthetic */ ReentrantLock access$100() {
        return uploadLock;
    }

    static /* synthetic */ String access$202(RealmsUploadScreen x0, String x1) {
        x0.status = x1;
        return x0.status;
    }

    static /* synthetic */ boolean access$300(RealmsUploadScreen x0) {
        return x0.cancelled;
    }

    static /* synthetic */ void access$400(RealmsUploadScreen x0, long x1) {
        x0.uploadCancelled(x1);
    }

    static /* synthetic */ RealmsLevelSummary access$500(RealmsUploadScreen x0) {
        return x0.selectedLevel;
    }

    static /* synthetic */ File access$600(RealmsUploadScreen x0, File x1) throws IOException {
        return x0.tarGzipArchive(x1);
    }

    static /* synthetic */ boolean access$700(RealmsUploadScreen x0, File x1) {
        return x0.verify(x1);
    }

    static /* synthetic */ String access$802(RealmsUploadScreen x0, String x1) {
        x0.errorMessage = x1;
        return x0.errorMessage;
    }

    static /* synthetic */ int access$900(RealmsUploadScreen x0) {
        return x0.slotId;
    }

    static /* synthetic */ UploadStatus access$1000(RealmsUploadScreen x0) {
        return x0.uploadStatus;
    }

    static /* synthetic */ Logger access$1100() {
        return LOGGER;
    }

    static /* synthetic */ boolean access$1202(RealmsUploadScreen x0, boolean x1) {
        x0.uploadFinished = x1;
        return x0.uploadFinished;
    }

    static /* synthetic */ RealmsButton access$1300(RealmsUploadScreen x0) {
        return x0.backButton;
    }

    static /* synthetic */ boolean access$1402(RealmsUploadScreen x0, boolean x1) {
        x0.showDots = x1;
        return x0.showDots;
    }

    static /* synthetic */ RealmsButton access$1500(RealmsUploadScreen x0) {
        return x0.cancelButton;
    }
}

