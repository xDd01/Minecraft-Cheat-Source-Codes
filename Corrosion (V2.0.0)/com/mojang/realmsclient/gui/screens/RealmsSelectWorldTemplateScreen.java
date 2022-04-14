/*
 * Decompiled with CFR 0.152.
 */
package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.screens.RealmsScreenWithCallback;
import com.mojang.realmsclient.util.RealmsTextureManager;
import com.mojang.realmsclient.util.RealmsUtil;
import java.util.Collections;
import java.util.List;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSharedConstants;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class RealmsSelectWorldTemplateScreen
extends RealmsScreen {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String LINK_ICON = "realms:textures/gui/realms/link_icons.png";
    private static final String TRAILER_ICON = "realms:textures/gui/realms/trailer_icons.png";
    private static final String SLOT_FRAME_LOCATION = "realms:textures/gui/realms/slot_frame.png";
    private final RealmsScreenWithCallback<WorldTemplate> lastScreen;
    private WorldTemplate selectedWorldTemplate;
    private List<WorldTemplate> templates = Collections.emptyList();
    private WorldTemplateSelectionList worldTemplateSelectionList;
    private int selectedTemplate = -1;
    private String title;
    private static final int BUTTON_BACK_ID = 0;
    private static final int BUTTON_SELECT_ID = 1;
    private RealmsButton selectButton;
    private String toolTip = null;
    private String currentLink = null;
    private boolean isMiniGame;
    private boolean displayWarning = false;
    private int clicks = 0;

    public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback<WorldTemplate> lastScreen, WorldTemplate selectedWorldTemplate, boolean isMiniGame) {
        this.lastScreen = lastScreen;
        this.selectedWorldTemplate = selectedWorldTemplate;
        this.isMiniGame = isMiniGame;
        this.title = isMiniGame ? RealmsSelectWorldTemplateScreen.getLocalizedString("mco.template.title.minigame") : RealmsSelectWorldTemplateScreen.getLocalizedString("mco.template.title");
    }

    public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback<WorldTemplate> lastScreen, WorldTemplate selectedWorldTemplate, boolean isMiniGame, boolean displayWarning) {
        this(lastScreen, selectedWorldTemplate, isMiniGame);
        this.displayWarning = displayWarning;
    }

    public RealmsSelectWorldTemplateScreen(RealmsScreenWithCallback<WorldTemplate> lastScreen, WorldTemplate selectedWorldTemplate, boolean isMiniGame, boolean displayWarning, List<WorldTemplate> templates) {
        this(lastScreen, selectedWorldTemplate, isMiniGame, displayWarning);
        this.templates = templates;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public void mouseEvent() {
        super.mouseEvent();
        this.worldTemplateSelectionList.mouseEvent();
    }

    @Override
    public void init() {
        Keyboard.enableRepeatEvents(true);
        this.buttonsClear();
        this.worldTemplateSelectionList = new WorldTemplateSelectionList();
        if (this.templates.size() == 0) {
            final boolean isMiniGame = this.isMiniGame;
            new Thread("Realms-minigame-fetcher"){

                @Override
                public void run() {
                    RealmsClient client = RealmsClient.createRealmsClient();
                    try {
                        if (isMiniGame) {
                            RealmsSelectWorldTemplateScreen.this.templates = client.fetchMinigames().templates;
                        } else {
                            RealmsSelectWorldTemplateScreen.this.templates = client.fetchWorldTemplates().templates;
                        }
                    }
                    catch (RealmsServiceException e2) {
                        LOGGER.error("Couldn't fetch templates");
                    }
                }
            }.start();
        }
        this.buttonsAdd(RealmsSelectWorldTemplateScreen.newButton(0, this.width() / 2 + 6, this.height() - 32, 153, 20, this.isMiniGame ? RealmsSelectWorldTemplateScreen.getLocalizedString("gui.cancel") : RealmsSelectWorldTemplateScreen.getLocalizedString("gui.back")));
        this.selectButton = RealmsSelectWorldTemplateScreen.newButton(1, this.width() / 2 - 154, this.height() - 32, 153, 20, RealmsSelectWorldTemplateScreen.getLocalizedString("mco.template.button.select"));
        this.buttonsAdd(this.selectButton);
        this.selectButton.active(false);
    }

    @Override
    public void tick() {
        super.tick();
        --this.clicks;
        if (this.clicks < 0) {
            this.clicks = 0;
        }
    }

    @Override
    public void buttonClicked(RealmsButton button) {
        if (!button.active()) {
            return;
        }
        switch (button.id()) {
            case 0: {
                this.backButtonClicked();
                break;
            }
            case 1: {
                this.selectTemplate();
                break;
            }
            default: {
                return;
            }
        }
    }

    @Override
    public void keyPressed(char eventCharacter, int eventKey) {
        if (eventKey == 1) {
            this.backButtonClicked();
        }
    }

    private void backButtonClicked() {
        this.lastScreen.callback(null);
        Realms.setScreen(this.lastScreen);
    }

    private void selectTemplate() {
        if (this.selectedTemplate >= 0 && this.selectedTemplate < this.templates.size()) {
            WorldTemplate template = this.templates.get(this.selectedTemplate);
            template.setMinigame(this.isMiniGame);
            this.lastScreen.callback(template);
        }
    }

    @Override
    public void render(int xm2, int ym2, float a2) {
        this.toolTip = null;
        this.currentLink = null;
        this.renderBackground();
        this.worldTemplateSelectionList.render(xm2, ym2, a2);
        this.drawCenteredString(this.title, this.width() / 2, 13, 0xFFFFFF);
        if (this.displayWarning) {
            this.drawCenteredString(RealmsSelectWorldTemplateScreen.getLocalizedString("mco.minigame.world.info1"), this.width() / 2, RealmsConstants.row(-1), 0xA0A0A0);
            this.drawCenteredString(RealmsSelectWorldTemplateScreen.getLocalizedString("mco.minigame.world.info2"), this.width() / 2, RealmsConstants.row(0), 0xA0A0A0);
        }
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

    private class WorldTemplateSelectionList
    extends RealmsClickableScrolledSelectionList {
        public WorldTemplateSelectionList() {
            super(RealmsSelectWorldTemplateScreen.this.width(), RealmsSelectWorldTemplateScreen.this.height(), RealmsSelectWorldTemplateScreen.this.displayWarning ? RealmsConstants.row(1) : 32, RealmsSelectWorldTemplateScreen.this.height() - 40, 46);
        }

        @Override
        public int getItemCount() {
            return RealmsSelectWorldTemplateScreen.this.templates.size();
        }

        @Override
        public void customMouseEvent(int y0, int y1, int headerHeight, float yo2, int itemHeight) {
            if (Mouse.isButtonDown(0) && this.ym() >= y0 && this.ym() <= y1) {
                int x0 = this.width() / 2 - 150;
                int x1 = this.width();
                int clickSlotPos = this.ym() - y0 - headerHeight + (int)yo2 - 4;
                int slot = clickSlotPos / itemHeight;
                if (this.xm() >= x0 && this.xm() <= x1 && slot >= 0 && clickSlotPos >= 0 && slot < this.getItemCount()) {
                    this.itemClicked(clickSlotPos, slot, this.xm(), this.ym(), this.width());
                    if (slot >= RealmsSelectWorldTemplateScreen.this.templates.size()) {
                        return;
                    }
                    RealmsSelectWorldTemplateScreen.this.selectButton.active(true);
                    RealmsSelectWorldTemplateScreen.this.selectedTemplate = slot;
                    RealmsSelectWorldTemplateScreen.this.selectedWorldTemplate = null;
                    RealmsSelectWorldTemplateScreen.this.clicks += RealmsSharedConstants.TICKS_PER_SECOND / 3 + 1;
                    if (RealmsSelectWorldTemplateScreen.this.clicks >= RealmsSharedConstants.TICKS_PER_SECOND / 2) {
                        RealmsSelectWorldTemplateScreen.this.selectTemplate();
                    }
                }
            }
        }

        @Override
        public boolean isSelectedItem(int item) {
            if (RealmsSelectWorldTemplateScreen.this.templates.size() == 0) {
                return false;
            }
            if (item >= RealmsSelectWorldTemplateScreen.this.templates.size()) {
                return false;
            }
            if (RealmsSelectWorldTemplateScreen.this.selectedWorldTemplate != null) {
                boolean same = ((RealmsSelectWorldTemplateScreen)RealmsSelectWorldTemplateScreen.this).selectedWorldTemplate.name.equals(((WorldTemplate)((RealmsSelectWorldTemplateScreen)RealmsSelectWorldTemplateScreen.this).templates.get((int)item)).name);
                if (same) {
                    RealmsSelectWorldTemplateScreen.this.selectedTemplate = item;
                }
                return same;
            }
            return item == RealmsSelectWorldTemplateScreen.this.selectedTemplate;
        }

        @Override
        public void itemClicked(int clickSlotPos, int slot, int xm2, int ym2, int width) {
            if (slot >= RealmsSelectWorldTemplateScreen.this.templates.size()) {
                return;
            }
            if (RealmsSelectWorldTemplateScreen.this.currentLink != null) {
                RealmsUtil.browseTo(RealmsSelectWorldTemplateScreen.this.currentLink);
            }
        }

        @Override
        public int getMaxPosition() {
            return this.getItemCount() * 46;
        }

        @Override
        public void renderBackground() {
            RealmsSelectWorldTemplateScreen.this.renderBackground();
        }

        @Override
        public void renderItem(int i2, int x2, int y2, int h2, int mouseX, int mouseY) {
            if (i2 < RealmsSelectWorldTemplateScreen.this.templates.size()) {
                this.renderWorldTemplateItem(i2, x2, y2, h2);
            }
        }

        @Override
        public int getScrollbarPosition() {
            return super.getScrollbarPosition() + 30;
        }

        @Override
        public void renderSelected(int width, int y2, int h2, Tezzelator t2) {
            int x0 = this.getScrollbarPosition() - 290;
            int x1 = this.getScrollbarPosition() - 10;
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            GL11.glDisable(3553);
            t2.begin(7, RealmsDefaultVertexFormat.POSITION_TEX_COLOR);
            t2.vertex(x0, y2 + h2 + 2, 0.0).tex(0.0, 1.0).color(128, 128, 128, 255).endVertex();
            t2.vertex(x1, y2 + h2 + 2, 0.0).tex(1.0, 1.0).color(128, 128, 128, 255).endVertex();
            t2.vertex(x1, y2 - 2, 0.0).tex(1.0, 0.0).color(128, 128, 128, 255).endVertex();
            t2.vertex(x0, y2 - 2, 0.0).tex(0.0, 0.0).color(128, 128, 128, 255).endVertex();
            t2.vertex(x0 + 1, y2 + h2 + 1, 0.0).tex(0.0, 1.0).color(0, 0, 0, 255).endVertex();
            t2.vertex(x1 - 1, y2 + h2 + 1, 0.0).tex(1.0, 1.0).color(0, 0, 0, 255).endVertex();
            t2.vertex(x1 - 1, y2 - 1, 0.0).tex(1.0, 0.0).color(0, 0, 0, 255).endVertex();
            t2.vertex(x0 + 1, y2 - 1, 0.0).tex(0.0, 0.0).color(0, 0, 0, 255).endVertex();
            t2.end();
            GL11.glEnable(3553);
        }

        private void renderWorldTemplateItem(int i2, int x2, int y2, int h2) {
            WorldTemplate worldTemplate = (WorldTemplate)RealmsSelectWorldTemplateScreen.this.templates.get(i2);
            int textStart = x2 + 20;
            RealmsSelectWorldTemplateScreen.this.drawString(worldTemplate.name, textStart, y2 + 2, 0xFFFFFF);
            RealmsSelectWorldTemplateScreen.this.drawString(worldTemplate.author, textStart, y2 + 15, 0x6C6C6C);
            RealmsSelectWorldTemplateScreen.this.drawString(worldTemplate.version, textStart + 227 - RealmsSelectWorldTemplateScreen.this.fontWidth(worldTemplate.version), y2 + 1, 0x6C6C6C);
            if (!(worldTemplate.link.equals("") && worldTemplate.trailer.equals("") && worldTemplate.recommendedPlayers.equals(""))) {
                this.drawIcons(textStart - 1, y2 + 25, this.xm(), this.ym(), worldTemplate.link, worldTemplate.trailer, worldTemplate.recommendedPlayers);
            }
            this.drawImage(x2 - 25, y2 + 1, this.xm(), this.ym(), worldTemplate);
        }

        private void drawImage(int x2, int y2, int xm2, int ym2, WorldTemplate worldTemplate) {
            RealmsTextureManager.bindWorldTemplate(worldTemplate.id, worldTemplate.image);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            RealmsScreen.blit(x2 + 1, y2 + 1, 0.0f, 0.0f, 38, 38, 38.0f, 38.0f);
            RealmsScreen.bind(RealmsSelectWorldTemplateScreen.SLOT_FRAME_LOCATION);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
            RealmsScreen.blit(x2, y2, 0.0f, 0.0f, 40, 40, 40.0f, 40.0f);
        }

        private void drawIcons(int x2, int y2, int xm2, int ym2, String link, String trailerLink, String recommendedPlayers) {
            int offset;
            boolean linkHovered = false;
            boolean trailerHovered = false;
            if (!recommendedPlayers.equals("")) {
                RealmsSelectWorldTemplateScreen.this.drawString(recommendedPlayers, x2, y2 + 4, 0x4C4C4C);
            }
            int n2 = offset = recommendedPlayers.equals("") ? 0 : RealmsSelectWorldTemplateScreen.this.fontWidth(recommendedPlayers) + 2;
            if (xm2 >= x2 + offset && xm2 <= x2 + offset + 32 && ym2 >= y2 && ym2 <= y2 + 15 && ym2 < RealmsSelectWorldTemplateScreen.this.height() - 15 && ym2 > 32) {
                if (xm2 <= x2 + 15 + offset && xm2 > offset) {
                    if (!link.equals("")) {
                        linkHovered = true;
                    } else {
                        trailerHovered = true;
                    }
                } else if (!link.equals("")) {
                    trailerHovered = true;
                }
            }
            if (!link.equals("")) {
                RealmsScreen.bind(RealmsSelectWorldTemplateScreen.LINK_ICON);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glPushMatrix();
                GL11.glScalef(1.0f, 1.0f, 1.0f);
                RealmsScreen.blit(x2 + offset, y2, linkHovered ? 15.0f : 0.0f, 0.0f, 15, 15, 30.0f, 15.0f);
                GL11.glPopMatrix();
            }
            if (!trailerLink.equals("")) {
                RealmsScreen.bind(RealmsSelectWorldTemplateScreen.TRAILER_ICON);
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
                GL11.glPushMatrix();
                GL11.glScalef(1.0f, 1.0f, 1.0f);
                RealmsScreen.blit(x2 + offset + (link.equals("") ? 0 : 17), y2, trailerHovered ? 15.0f : 0.0f, 0.0f, 15, 15, 30.0f, 15.0f);
                GL11.glPopMatrix();
            }
            if (linkHovered && !link.equals("")) {
                RealmsSelectWorldTemplateScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.template.info.tooltip");
                RealmsSelectWorldTemplateScreen.this.currentLink = link;
            } else if (trailerHovered && !trailerLink.equals("")) {
                RealmsSelectWorldTemplateScreen.this.toolTip = RealmsScreen.getLocalizedString("mco.template.trailer.tooltip");
                RealmsSelectWorldTemplateScreen.this.currentLink = trailerLink;
            }
        }
    }
}

