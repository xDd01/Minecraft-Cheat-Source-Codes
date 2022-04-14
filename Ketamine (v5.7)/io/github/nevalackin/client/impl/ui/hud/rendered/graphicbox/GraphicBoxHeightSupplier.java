package io.github.nevalackin.client.impl.ui.hud.rendered.graphicbox;

@FunctionalInterface
public interface GraphicBoxHeightSupplier {
    float get(
            float outlineWidth,
            float separatorWidth,
            float header
    );
}