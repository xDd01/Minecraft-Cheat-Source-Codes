package io.github.nevalackin.client.api.ui.framework;

import io.github.nevalackin.client.util.render.DrawUtil;

public interface Expandable {

    void setState(ExpandState state);

    ExpandState getState();

    default boolean isExpanded() {
        return this.getState() != ExpandState.CLOSED && this.getExpandProgress() > 0.0;
    }

    double getX();
    double getY();

    double getExpandedX();
    double getExpandedY();
    double getExpandedWidth();
    double calculateExpandedHeight();

    double getExpandProgress();

    default double getExpandedHeight() {
        return this.calculateExpandedHeight() * DrawUtil.bezierBlendAnimation(this.getExpandProgress());
    }

    default boolean isHoveredExpanded(final int mouseX, final int mouseY) {
        final double ex, ey;
        return this.isExpanded() &&
            mouseX > (ex = this.getExpandedX()) &&
            mouseX < ex + this.getExpandedWidth() &&
            mouseY > (ey = this.getExpandedY()) &&
            mouseY < ey + this.getExpandedHeight();
    }

    default boolean isHoveredExpand(final int mouseX, final int mouseY) {
        final double ex = this.getExpandedX();
        final double ey = this.getExpandedY();
        final double ew = this.getExpandedWidth();
        final double eh = this.getExpandedHeight();

        return mouseX > ex && mouseY > ey && mouseX < ex + ew && mouseY < ey + eh;
    }
}
