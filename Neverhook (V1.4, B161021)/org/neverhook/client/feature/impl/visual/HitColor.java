package org.neverhook.client.feature.impl.visual;

import org.neverhook.client.feature.Feature;
import org.neverhook.client.feature.impl.Type;
import org.neverhook.client.settings.impl.ColorSetting;

import java.awt.*;

public class HitColor extends Feature {

    public static ColorSetting hitColor = new ColorSetting("Hit Color", Color.RED.getRGB(), () -> true);

    public HitColor() {
        super("HitColor", "Изменяет цвет удара", Type.Visuals);
        addSettings(hitColor);
    }
}
