package io.github.nevalackin.client.impl.ui.hud.rendered;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.api.ui.cfont.CustomFontRenderer;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.event.game.GetTimerSpeedEvent;
import io.github.nevalackin.client.impl.event.render.overlay.RenderGameOverlayEvent;
import io.github.nevalackin.client.impl.module.misc.player.StaffAnalyzer;
import io.github.nevalackin.client.impl.property.ColourProperty;
import io.github.nevalackin.client.impl.property.EnumProperty;
import io.github.nevalackin.client.impl.ui.hud.components.HudComponent;
import io.github.nevalackin.client.util.render.BloomUtil;
import io.github.nevalackin.client.util.render.ColourUtil;
import io.github.nevalackin.client.util.render.DrawUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class WatermarkModule extends Module implements HudComponent {

    private double xPos, yPos;
    private double width, height;

    private boolean dragon;

    private final EnumProperty<Colour> colourProperty = new EnumProperty<>("Colour", Colour.CLIENT);
    public final ColourProperty startColourProperty = new ColourProperty("Start Colour", 0xFFFA00BC, this::isBlend);
    public final ColourProperty endColourProperty = new ColourProperty("End Colour", 0xFF00E4FF, this::isBlend);

    private final DateFormat format = new SimpleDateFormat("kk:mm:ss");

    public WatermarkModule() {
        super("Watermark", Category.RENDER, Category.SubCategory.RENDER_OVERLAY);
        register(colourProperty, startColourProperty, endColourProperty);
        setX(4.0f);
        setY(4.0f);
        setEnabled(true);
    }

    @EventLink
    private final Listener<RenderGameOverlayEvent> onRenderOverlay = event -> {
        render(event.getScaledResolution().getScaledWidth(), event.getScaledResolution().getScaledHeight(), event.getPartialTicks());
    };

    private boolean isBlend() {
        return this.colourProperty.getValue() == Colour.BLEND;
    }

    @Override
    public boolean isDragging() {
        return dragon;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragon = dragging;
    }

    @Override
    public void render(int scaledWidth, int scaledHeight, double tickDelta) {
        fitInScreen(scaledWidth, scaledHeight);

        double x = getX();
        double y = getY();
        double margin = 2.0f;

        final CustomFontRenderer fontRenderer = KetamineClient.getInstance().getFontRenderer();

        String playTime = "";
        long totalSecs = (System.currentTimeMillis() - KetamineClient.getInstance().getStartTime()) / 1000;
        long secs = totalSecs % 60;
        long mins = (totalSecs / 60) % 60;
        long hours = (totalSecs / 60 / 60) % 24;
        long days = (totalSecs / 60 / 60) / 24;

        try {
            if (days > 0) playTime += days + "d ";
            if (hours > 0) playTime += hours + "h ";
            if (mins > 0) playTime += mins + "m ";
            if (secs > 0) playTime += secs + "s ";
            if (playTime.endsWith(" ")) playTime = playTime.substring(0, playTime.length() - 1);
        } catch (Exception ignored) {
        }

        String name = KetamineClient.getInstance().getName();
        String serverName = mc.getCurrentServerData().serverIP.toLowerCase();
        String bps = String.format("%.1f b/ps", Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * GetTimerSpeedEvent.lastTimerSpeed * 20.0);
        String banCount = StaffAnalyzer.totalBans + " Bans";

        String text = String.format("%s | %s | %s | %s | %s", name, serverName, bps, playTime, banCount);

        final int startColour = colourProperty.getValue().getModColour(startColourProperty.getValue(), endColourProperty.getValue(), 0);
        final int endColour = colourProperty.getValue().getModColour(endColourProperty.getValue(), startColourProperty.getValue(), 250);

        // Draw watermark
        {
            double bounds = fontRenderer.getWidth(text);
            double heightBounds = fontRenderer.getHeight(text);
            double thickness = 2.0f;

            double width = bounds + margin * 2.0f;
            double height = heightBounds + margin * 2.0f;

            // Initial Rect & Blur
            DrawUtil.glDrawFilledQuad(x, y + thickness, width, height, 0x80 << 24);

            // Top
            BloomUtil.drawAndBloom(() -> DrawUtil.glDrawSidewaysGradientRect(x, y + thickness - 1, width, 1, startColour, endColour));

            // Text
            fontRenderer.drawWithShadow(text, x + margin, y + margin + thickness, .5, 0xFFFFFFFF);

            this.width = width;
            this.height = height + thickness;
        }
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void setX(double x) {
        xPos = x;
    }

    @Override
    public void setY(double y) {
        yPos = y;
    }

    @Override
    public double getX() {
        return xPos;
    }

    @Override
    public double getY() {
        return yPos;
    }

    @Override
    public double setWidth(double width) {
        return this.width = width;
    }

    @Override
    public double setHeight(double height) {
        return this.height = height;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {

    }


    @FunctionalInterface
    private interface ModColourFunc {
        int getColour(final int startColour, final int endColour, final int index);
    }

    private enum Colour {
        BLEND("Blend", ((startColour, endColour, index) -> {
            return ColourUtil.fadeBetween(startColour, endColour, index * 150L);
        })),
        CLIENT("Client Colour", ((startColour, endColour, index) -> {
            return ColourUtil.fadeBetween(ColourUtil.getClientColour(), ColourUtil.getSecondaryColour(), index * 150L);
        }));

        private final String name;
        private final ModColourFunc modColourFunc;

        Colour(String name, ModColourFunc modColourFunc) {
            this.name = name;
            this.modColourFunc = modColourFunc;
        }

        public int getModColour(final int startColour, final int endColour, final int index) {
            return modColourFunc.getColour(startColour, endColour, index);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
