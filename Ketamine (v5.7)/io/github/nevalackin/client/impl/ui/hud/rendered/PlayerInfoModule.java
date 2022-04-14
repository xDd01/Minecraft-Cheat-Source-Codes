package io.github.nevalackin.client.impl.ui.hud.rendered;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.event.render.overlay.RenderGameOverlayEvent;
import io.github.nevalackin.client.impl.property.BooleanProperty;
import io.github.nevalackin.client.impl.ui.hud.components.HudComponent;
import io.github.nevalackin.client.impl.ui.hud.rendered.graphicbox.GraphicBoxField;
import io.github.nevalackin.client.impl.ui.hud.rendered.graphicbox.GraphicBoxUtils;
import io.github.nevalackin.client.util.render.ColourUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerInfoModule extends Module implements HudComponent {

    private double xPos, yPos;
    private double width, height;
    private boolean dragon;

    private final BooleanProperty hideNonTabListProperty = new BooleanProperty("Hide NPCs", false);

    public PlayerInfoModule() {
        super("Player Info", Category.RENDER, Category.SubCategory.RENDER_OVERLAY);
        register(hideNonTabListProperty);
        this.setX(4f);
        this.setY(22.0f);
    }

    @EventLink
    private final Listener<RenderGameOverlayEvent> onRenderOverlay = event -> {
        this.render(event.getScaledResolution().getScaledWidth(), event.getScaledResolution().getScaledHeight(), event.getPartialTicks());
    };

    @Override
    public void render(int scaledWidth, int scaledHeight, double tickDelta) {
        this.fitInScreen(scaledWidth, scaledHeight);
        if (mc.theWorld == null) return;
        List<EntityPlayer> playerEntities = mc.theWorld.playerEntities;
        float[] dimensions = GraphicBoxUtils.drawGraphicBox(FIELDS, hideNonTabListProperty.getValue() ? playerEntities.stream().filter(this::isOnTab).filter(entityPlayer -> !entityPlayer.getDisplayName().getFormattedText().startsWith("\2478[NPC]")).collect(Collectors.toList()) : playerEntities, (float) getX(), (float) getY());
        this.setWidth(dimensions[0]);
        this.setHeight(dimensions[1]);
    }

    private static final float BLOCK_TO_FT = 3.28084f;

    private static final List<GraphicBoxField<EntityPlayer>> FIELDS = Arrays.asList(
            new GraphicBoxField<>("Player", player -> {
                return player.getGameProfile().getName();
            }, player -> {
                return 0xFFFFFFFF;
            }),
            new GraphicBoxField<>("HP", player -> {
                return String.format("%.1f%%", player.getHealth() / player.getMaxHealth() * 100.0f);
            }, player -> {
                return ColourUtil.blendHealthColours(player.getHealth() / player.getMaxHealth());
            }),
            new GraphicBoxField<>("Dist", player -> {
                Minecraft client = Minecraft.getMinecraft();
                if (player instanceof EntityPlayerSP || client.thePlayer == null) return "0 ft";
                return String.format("%d ft", Math.round(client.thePlayer.getDistanceToEntity(player) * BLOCK_TO_FT));
            }, player -> {
                return 0xFFFFFFFF;
            }),
            new GraphicBoxField<>("Armor", player -> {
                return String.valueOf(player.getTotalArmorValue());
            }, player -> {
                Minecraft client = Minecraft.getMinecraft();
                if (player instanceof EntityPlayerSP || client.thePlayer == null || client.thePlayer.getTotalArmorValue() == player.getTotalArmorValue())
                    return 0xFFFFFF << 8;
                return client.thePlayer.getTotalArmorValue() > player.getTotalArmorValue() ? 0xFF00FF59 : 0xFFFF80 << 8;
            }),
            new GraphicBoxField<>("Priority", player -> {
                // TODO :: priority in player list
                return player instanceof EntityPlayerSP ? "LocalPlayer" : "Auto";
            }, player -> {
                return player instanceof EntityPlayerSP ? 0xFFFFFF << 8 : 0xFFFFFFFF;
            })
    );

    private boolean isOnTab(final Entity entity) {
        for (final NetworkPlayerInfo info : mc.getNetHandler().getPlayerInfoMap()) {
            if (info.getGameProfile().getName().equals(entity.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void setX(double x) {
        this.xPos = x;
    }

    @Override
    public void setY(double y) {
        this.yPos = y;
    }

    @Override
    public double getX() {
        return this.xPos;
    }

    @Override
    public double getY() {
        return this.yPos;
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
        return this.width;
    }

    @Override
    public double getHeight() {
        return this.height;
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
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {

    }
}
