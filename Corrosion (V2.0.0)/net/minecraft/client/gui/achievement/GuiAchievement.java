/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui.achievement;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;

public class GuiAchievement
extends Gui {
    private static final ResourceLocation achievementBg = new ResourceLocation("textures/gui/achievement/achievement_background.png");
    private Minecraft mc;
    private int width;
    private int height;
    private String achievementTitle;
    private String achievementDescription;
    private Achievement theAchievement;
    private long notificationTime;
    private RenderItem renderItem;
    private boolean permanentNotification;

    public GuiAchievement(Minecraft mc2) {
        this.mc = mc2;
        this.renderItem = mc2.getRenderItem();
    }

    public void displayAchievement(Achievement ach2) {
    }

    public void displayUnformattedAchievement(Achievement achievementIn) {
    }

    private void updateAchievementWindowScale() {
    }

    public void updateAchievementWindow() {
    }

    public void clearAchievements() {
        this.theAchievement = null;
        this.notificationTime = 0L;
    }
}

