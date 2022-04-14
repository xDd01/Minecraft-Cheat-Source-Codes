/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.visual;

import cafe.corrosion.Corrosion;
import cafe.corrosion.component.draggable.IDraggable;
import cafe.corrosion.event.impl.EventPacketIn;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.drag.data.HudComponentProxy;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.ColorProperty;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.nameable.INameable;
import cafe.corrosion.util.player.PlayerUtil;
import cafe.corrosion.util.render.Blurrer;
import cafe.corrosion.util.render.ColorUtil;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S45PacketTitle;
import org.lwjgl.opengl.GL11;

@ModuleAttributes(name="HypixelHelper", description="Provides useful information about your activity on Hypixel", category=Module.Category.VISUAL)
public class HypixelHelper
extends Module
implements IDraggable {
    private static final int WHITE = Color.WHITE.getRGB();
    private static final int BACKGROUND_COLOR = new Color(20, 20, 20, 200).getRGB();
    private static final TTFFontRenderer SMALL = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 18.0f);
    private static final TTFFontRenderer LARGE = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.UBUNTU, 26.0f);
    private final EnumProperty<ColorUtil.ColorMode> colorMode = new EnumProperty((Module)this, "Color Mode", (INameable[])ColorUtil.ColorMode.values());
    private final ColorProperty colorProperty = new ColorProperty((Module)this, "Color", Color.RED);
    private final Map<ChatResult, Integer> chatResults = new HashMap<ChatResult, Integer>();
    private long lastDeath = System.currentTimeMillis();
    private long lastGameStartMessage = System.currentTimeMillis();

    public HypixelHelper() {
        Arrays.stream(ChatResult.values()).forEach(value -> this.chatResults.put((ChatResult)((Object)value), 0));
        this.registerEventHandler(EventPacketIn.class, event -> {
            String rawMessage;
            if (!(event.getPacket() instanceof S02PacketChat) && !(event.getPacket() instanceof S45PacketTitle) || HypixelHelper.mc.thePlayer == null) {
                return;
            }
            String playerName = HypixelHelper.mc.thePlayer.getName();
            if (event.getPacket() instanceof S02PacketChat) {
                S02PacketChat packet = (S02PacketChat)event.getPacket();
                rawMessage = PlayerUtil.stripColor(packet.getChatComponent().getUnformattedText());
            } else {
                S45PacketTitle packet = (S45PacketTitle)event.getPacket();
                if (packet.getMessage() == null || packet.getMessage().getUnformattedText() == null) {
                    return;
                }
                rawMessage = PlayerUtil.stripColor(packet.getMessage().getUnformattedText());
            }
            this.chatResults.forEach((result, amount) -> {
                if (result.getPredicate().test(this, rawMessage, playerName)) {
                    this.chatResults.put((ChatResult)((Object)((Object)result)), amount + 1);
                }
            });
        });
        Corrosion.INSTANCE.getGuiComponentManager().register(this, 5, 250, 165, 110);
    }

    @Override
    public void onEnable() {
        Arrays.stream(ChatResult.values()).forEach(result -> this.chatResults.put((ChatResult)((Object)result), 0));
    }

    @Override
    public void onDisable() {
        this.chatResults.clear();
    }

    @Override
    public void render(HudComponentProxy component, ScaledResolution scaledResolution, int posX, int posY, int expandX, int expandY) {
        GL11.glPushMatrix();
        Blurrer blurrer = Corrosion.INSTANCE.getBlurrer();
        blurrer.blur(posX, posY, expandX, expandY, true);
        blurrer.bloom(posX, posY, expandX, expandY, 15, 200);
        RenderUtil.drawRoundedRect(posX, posY, posX + expandX, posY + expandY, BACKGROUND_COLOR);
        RenderUtil.drawRoundedRect(posX, posY, posX + expandX, posY + 1, ColorUtil.getColor((ColorUtil.ColorMode)this.colorMode.getValue(), (Color)this.colorProperty.getValue(), 20));
        LARGE.drawString("Stats Tracker", posX + 2, posY + 4, Color.WHITE.getRGB());
        int offsetY = 25;
        for (Map.Entry<ChatResult, Integer> entry : this.chatResults.entrySet()) {
            String name = entry.getKey().getDisplayName();
            String amount = entry.getValue() + "";
            int end = posX + expandX - (int)SMALL.getWidth(amount) - 4;
            SMALL.drawString(name, posX + 2, posY + offsetY, WHITE);
            SMALL.drawString(amount, end, posY + offsetY, WHITE);
            offsetY = (int)((float)offsetY + (SMALL.getHeight("A") + 3.0f));
        }
        GL11.glPopMatrix();
    }

    public EnumProperty<ColorUtil.ColorMode> getColorMode() {
        return this.colorMode;
    }

    public ColorProperty getColorProperty() {
        return this.colorProperty;
    }

    public Map<ChatResult, Integer> getChatResults() {
        return this.chatResults;
    }

    public long getLastDeath() {
        return this.lastDeath;
    }

    public long getLastGameStartMessage() {
        return this.lastGameStartMessage;
    }

    public void setLastDeath(long lastDeath) {
        this.lastDeath = lastDeath;
    }

    public void setLastGameStartMessage(long lastGameStartMessage) {
        this.lastGameStartMessage = lastGameStartMessage;
    }

    private static interface HelperPredicate {
        public boolean test(HypixelHelper var1, String var2, String var3);
    }

    private static enum ChatResult {
        KILL("Kills", (helper, message, name) -> message.endsWith("by " + name) && !message.startsWith(name)),
        FINAL_KILL("Final Kills", (helper, message, name) -> !message.startsWith(name) && message.contains(name) && message.endsWith("FINAL KILL!")),
        FINAL_DEATH("Final Deaths", (helper, message, name) -> message.startsWith(name) && message.endsWith("FINAL KILL!")),
        DEATH("Deaths", (helper, message, name) -> {
            if (System.currentTimeMillis() - helper.getLastDeath() < 10000L) {
                return false;
            }
            boolean died = message.equals("YOU DIED!");
            if (died) {
                helper.setLastDeath(System.currentTimeMillis());
            }
            return died;
        }),
        WIN("Wins", (helper, message, name) -> message.equals("VICTORY!")),
        BEDS_BROKEN("Beds Broken", (helper, message, name) -> message.startsWith("BED DESTRUCTION >") && message.contains(name)),
        GAMES_PLAYED("Games Played", (helper, message, name) -> message.equals("The game starts in 1 second!"));

        private final String displayName;
        private final HelperPredicate predicate;

        public String getDisplayName() {
            return this.displayName;
        }

        public HelperPredicate getPredicate() {
            return this.predicate;
        }

        private ChatResult(String displayName, HelperPredicate predicate) {
            this.displayName = displayName;
            this.predicate = predicate;
        }
    }
}

