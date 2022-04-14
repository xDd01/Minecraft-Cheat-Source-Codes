package org.neverhook.client.feature.impl.visual;

import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.settings.impl.ColorSetting;

import java.awt.*;

public class WorldColor extends Feature {

    public static ColorSetting worldColor = new ColorSetting("World Color", Color.RED.getRGB(), () -> true);

    public WorldColor() {
        super("WorldColor", "Меняет цвет игры", Type.Visuals);
        addSettings(worldColor);
    }
}
