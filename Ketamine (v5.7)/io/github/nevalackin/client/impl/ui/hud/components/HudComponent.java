package io.github.nevalackin.client.impl.ui.hud.components;

import io.github.nevalackin.client.impl.ui.hud.Quadrant;

public interface HudComponent {

    boolean isDragging(); void setDragging(boolean dragging);

    default void fitInScreen(int scaledWidth, int scaledHeight) {
        double x = this.getX();
        double y = this.getY();
        double width = this.getWidth();
        double height = this.getHeight();

        if (x < 0.0f) {
            this.setX(0.0f);
        } else if (x + width > scaledWidth) {
            this.setX(scaledWidth - width);
        }

        if (y < 0.0f) {
            this.setY(0.0f);
        } else if (y + height > scaledHeight) {
            this.setY(scaledHeight - height);
        }
    }

    default boolean isHovered(double mouseX, double mouseY) {
        double x = this.getX();
        double y = this.getY();

        return mouseX > x && mouseY > y &&
                mouseX < x + this.getWidth() &&
                mouseY < y + this.getHeight();
    }

    default Quadrant getQuadrant(int scaledWidth, int scaledHeight) {
        double x = this.getX();
        double y = this.getY();
        double hw = this.getWidth() * 0.5f;
        double hh = this.getHeight() * 0.5f;

        if (x > scaledWidth * 0.5f - hw) {
            if (y > scaledHeight * 0.5f - hh) {
                return Quadrant.BOTTOM_RIGHT;
            } else {
                return Quadrant.TOP_RIGHT;
            }
        } else {
            if (y > scaledHeight * 0.5f - hh) {
                return Quadrant.BOTTOM_LEFT;
            } else {
                return Quadrant.TOP_LEFT    ;
            }
        }
    }

    void render(int scaledWidth, int scaledHeight, double tickDelta);

    double getX();
    double getY();

    void setX(double x);
    void setY(double y);

    double setWidth(double width);
    double setHeight(double height);

    double getWidth();
    double getHeight();

    boolean isVisible();
    void setVisible(boolean visible);
}
