package io.github.nevalackin.client.impl.ui.hud.components;

public interface ResizeableHudComponent extends HudComponent {

    void setWidthUnchecked(double width);

    void setHeightUnchecked(double height);

    double getMinWidth();

    double getMinHeight();
}