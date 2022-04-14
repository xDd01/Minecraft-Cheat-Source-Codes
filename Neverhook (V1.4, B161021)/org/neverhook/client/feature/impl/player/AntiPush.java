package org.neverhook.client.feature.impl.player;

import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.settings.impl.BooleanSetting;

public class AntiPush extends Feature {

    public static BooleanSetting water;
    public static BooleanSetting players;
    public static BooleanSetting blocks;

    public AntiPush() {
        super("AntiPush", "Убирает отталкивание от игроков, воды и блоков", Type.Player);
        players = new BooleanSetting("Entity", true, () -> true);
        water = new BooleanSetting("Liquid", true, () -> true);
        blocks = new BooleanSetting("Blocks", true, () -> true);
        addSettings(players, water, blocks);
    }
}
