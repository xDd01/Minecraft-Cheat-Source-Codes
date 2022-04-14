package io.github.nevalackin.client.impl.module.misc.player;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.api.notification.NotificationType;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.event.packet.ReceivePacketEvent;
import io.github.nevalackin.client.impl.event.player.UpdatePositionEvent;
import io.github.nevalackin.client.impl.property.DoubleProperty;
import io.github.nevalackin.client.util.misc.ServerUtil;
import io.github.nevalackin.client.util.misc.TimeUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.network.play.server.S02PacketChat;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class StaffAnalyzer extends Module {

    private String apiKey = "";
    private int lastStaffBans;
    private TimeUtil delay = new TimeUtil();
    private TimeUtil apiDelay = new TimeUtil();
    public static int totalBans = 0;
    private final DoubleProperty delayProperty = new DoubleProperty("Time Until Check", 60, 1, 60, 1);

    public StaffAnalyzer() {
        super("Staff Analyzer", Category.MISC, Category.SubCategory.MISC_PLAYER);
        this.register(delayProperty);
    }


    private String getTextWithoutFormatting(String input) {
        StringBuilder builder = new StringBuilder();
        boolean skip = false;
        for (char c : input.toCharArray()) {
            if (c == '§') {
                skip = true;
                continue;
            }
            if (skip) {
                skip = false;
                continue;
            }
            builder.append(c);
        }
        return builder.toString();
    }

    @EventLink
    private final Listener<ReceivePacketEvent> onReceivePacket = event -> {
        if (event.getPacket() instanceof S02PacketChat) {
            S02PacketChat s = (S02PacketChat) event.getPacket();
            String s0 = s.getChatComponent().getFormattedText();
            if (s0.contains("Your new API key is ")) {
                String s1 = getTextWithoutFormatting(s0);
                apiKey = s1.split("Your new API key is ")[1];
                event.setCancelled();
            }
        }
    };

    @EventLink
    private final Listener<UpdatePositionEvent> onUpdatePosition = event -> {
        if (ServerUtil.isHypixel()) {
            if (apiDelay.passed(2000)) {
                mc.thePlayer.sendChatMessage("/api new");
                apiDelay.reset();
            }
            if (delay.passed(delayProperty.getValue() * 1000) && !apiKey.isEmpty()) {
                new Thread(() -> {
                    try {
                        URL url = new URL("https://api.hypixel.net/punishmentstats?key=" + apiKey);
                        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                        for (String read; (read = in.readLine()) != null;) {
                            String line = StringUtils.substringAfterLast(read, "staff_total\"").replace("}", "").replace(":", "");
                            if (lastStaffBans == 0) lastStaffBans = Integer.parseInt(line);
                            else {
                                int bans = Integer.parseInt(line) - lastStaffBans;
                                if (bans > 0) {
                                    totalBans++;
                                    totalBans = totalBans + bans - 1;
                                    KetamineClient.getInstance().getNotificationManager().add(NotificationType.WARNING, getName(), "Staff banned " + bans + " player" + (bans == 1 ? "" : "s") + " in the last " + delayProperty.getValue() + " seconds " + totalBans + " total", 5000);
                                } else {
                                    KetamineClient.getInstance().getNotificationManager().add(NotificationType.INFO, getName(), "Staff banned 0 players in the last " + delayProperty.getValue() + " seconds " + totalBans + " total", 5000);
                                }
                            }
                            lastStaffBans = Integer.parseInt(line);
                        }
                    } catch (Exception ex) {
                        // ignored
                    }
                }).start();
                delay.reset();
            }
        } else {
            totalBans = 0;
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
