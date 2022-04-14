package io.github.nevalackin.client.impl.module.misc.player;

import com.google.common.collect.Lists;
import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.api.notification.NotificationType;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.event.packet.ReceivePacketEvent;
import io.github.nevalackin.client.impl.event.player.UpdatePositionEvent;
import io.github.nevalackin.client.impl.event.world.LoadWorldEvent;
import io.github.nevalackin.client.impl.property.BooleanProperty;
import io.github.nevalackin.client.impl.property.EnumProperty;
import io.github.nevalackin.client.impl.property.MultiSelectionEnumProperty;
import io.github.nevalackin.client.util.misc.ServerUtil;
import io.github.nevalackin.client.util.misc.TimeUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.server.S45PacketTitle;

import java.io.File;
import java.io.IOException;

public final class AutoHypixel extends Module {

    private final BooleanProperty autoPlayProperty = new BooleanProperty("Auto Play", true);
    private final EnumProperty<KillSultsMode> killSults = new EnumProperty<>("Kill Sults", KillSultsMode.ENGLISH);

    private final MultiSelectionEnumProperty<MapBlacklistOption> mapBlacklistOptionsProperty = new MultiSelectionEnumProperty<>("Blacklisted Maps",
            Lists.newArrayList(),
            MapBlacklistOption.values());

    private final File customKillSultsFile;

    private TimeUtil delay = new TimeUtil();
    private boolean hasSkipped;

    private static final char[] BYPASS_CHARS = {'⛏', '⛘', '⛜', '⛠', '⛟', '⛐', '⛍', '⛡', '⛋', '⛌', '⛗', '⛩', '⛉'};

    private final String[] insults = {
            "%s shut the /fuck/ up /fatass/",
            "Childhood obesity is an epidemic, %s needs help.",
            "Solve for %s's chromosomes, x = 46 + 1.",
            "%s I can count your friends on one hand.",
            "%s got /raped/ by /trannyhack/.",
            "I /shagged/ %s's mum",
            "I'm fucking rapid mate",
            ""
    };

    public AutoHypixel() {
        super("Auto Hypixel", Category.MISC, Category.SubCategory.MISC_PLAYER);
        register(mapBlacklistOptionsProperty, autoPlayProperty);
        this.customKillSultsFile = new File("killsults.txt");
        if (!this.customKillSultsFile.exists()) {
            try {
                final boolean ignored = this.customKillSultsFile.createNewFile();
            } catch (IOException ignored) {
                // TODO :: Error log
            }
        }
    }

    @EventLink
    private final Listener<ReceivePacketEvent> onReceivePacket = event -> {
        if (event.getPacket() instanceof S45PacketTitle) {
            S45PacketTitle s45 = (S45PacketTitle) event.getPacket();
            if (s45.getMessage() != null && autoPlayProperty.getValue()) {
                String message = s45.getMessage().getFormattedText().toLowerCase();

                if (message.contains("you died") || message.contains("game over")) {
                    KetamineClient.getInstance().getNotificationManager().add(NotificationType.ERROR, "You Lost", "Sending Into Next Game, Play Better Perhaps?", 2000L);
                    mc.thePlayer.dispatchCommand("play solo_insane");
                }
                else if (message.contains("you win") || message.contains("victory")) {
                    KetamineClient.getInstance().getNotificationManager().add(NotificationType.SUCCESS, "You Won!", "Sending Into Next Game", 2000L);
                    mc.thePlayer.dispatchCommand("play solo_insane");
                }
            }
        }
    };

    @EventLink
    private final Listener<UpdatePositionEvent> onUpdatePosition = event -> {
        for (MapBlacklistOption blacklistedMap : mapBlacklistOptionsProperty.getValue()) {
            if (ServerUtil.getCurrentHypixelMap() == null) return;
            if (!ServerUtil.getCurrentHypixelMap().contains(blacklistedMap.toString())) continue;

            if (delay.passed(6000) && !hasSkipped) {
                KetamineClient.getInstance().getNotificationManager().add(NotificationType.WARNING, getName(), blacklistedMap + " is Blacklisted, Skipping.", 2000L);
                mc.thePlayer.dispatchCommand("play solo_insane");
                hasSkipped = true;
            }
        }
    };

    @EventLink
    private final Listener<LoadWorldEvent> onLoadWorld = event -> {
        delay.reset();
        hasSkipped = false;
    };

    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    private enum gameMode {
        SOLOS_INSANE("Solo_Insane"),
        TEAMS_INSANE("Teams_Insane");

        private final String name;

        gameMode(String name) {this.name = name;}

        @Override
        public String toString() {return this.name;}
    }

    private enum KillSultsMode {
        DISABLED("Disabled"),
        ENGLISH("English"),
        CUSTOM("Custom"),
        CZECH("Czech");

        private final String name;

        KillSultsMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private enum MapBlacklistOption {
        AEGIS("Aegis"),
        ATLAS("Atlas"),
        CLEARING("Clearing"),
        DAWN("Dawn"),
        DESSERTED_ISLAND("Desserted Island"),
        ELVEN("Elven"),
        EMBERCELL("Embercell"),
        ENTANGLED("Entangled"),
        FOSSIL("Fossil"),
        FRAGMENT("Fragment"),
        FIRELINK_SHRINE("Firelink Shrine"),
        FROSTBOUND("Frostbound"),
        GARAGE("Garage"),
        GARRISON("Garrision"),
        HANGING_GARDENS("Hanging Gardens"),
        HEAVEN_PALACE("Heaven Palace"),
        JAGGED("Jagged"),
        MYTHIC("Mythic"),
        NIU("Niu"),
        OCEANA("Oceana"),
        ONIONRING_2("Onionring 2"),
        PALETTE("Palette"),
        RAILROAD("Railroad"),
        SANCTUARY("Sanctuary"),
        SHIRE("Shire"),
        SENTINEL("Sentinel"),
        SHAOHAO("Shaohao"),
        SIEGE("Siege"),
        SUBMERGED("Submerged"),
        TIKI("Tiki"),
        TRIBAL("Tribal"),
        TOWER("Tower"),
        MARTIAN("Martian"),
        TRIBUTE("Trbiute"),
        WATERWAYS("Waterways"),
        WINTERHELM("Winterhelm");

        private final String name;

        MapBlacklistOption(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
