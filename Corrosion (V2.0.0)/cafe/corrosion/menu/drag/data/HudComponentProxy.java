/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.drag.data;

import cafe.corrosion.module.Module;
import cafe.corrosion.property.type.NumberProperty;

public class HudComponentProxy {
    private final NumberProperty xProperty;
    private final NumberProperty yProperty;
    private final NumberProperty xExpand;
    private final NumberProperty yExpand;

    public HudComponentProxy(Module module, int startX, int startY, int expandX, int expandY) {
        this.xProperty = (NumberProperty)new NumberProperty(module, "X", startX, 0, 5000, 1).setHidden(() -> true);
        this.yProperty = (NumberProperty)new NumberProperty(module, "Y", startY, 0, 5000, 1).setHidden(() -> true);
        this.xExpand = (NumberProperty)new NumberProperty(module, "X-Expand", expandX, 0, 5000, 1).setHidden(() -> true);
        this.yExpand = (NumberProperty)new NumberProperty(module, "Y-Expand", expandY, 0, 5000, 1).setHidden(() -> true);
    }

    public int getX() {
        return ((Number)this.xProperty.getValue()).intValue();
    }

    public int getY() {
        return ((Number)this.yProperty.getValue()).intValue();
    }

    public int getExpandX() {
        return ((Number)this.xExpand.getValue()).intValue();
    }

    public int getExpandY() {
        return ((Number)this.yExpand.getValue()).intValue();
    }

    public void setX(int newX) {
        this.xProperty.setValue(newX);
    }

    public void setY(int newY) {
        this.yProperty.setValue(newY);
    }

    public void drag(int mouseX, int mouseY) {
        this.setX(mouseX);
        this.setY(mouseY);
    }

    public NumberProperty getXProperty() {
        return this.xProperty;
    }

    public NumberProperty getYProperty() {
        return this.yProperty;
    }

    public NumberProperty getXExpand() {
        return this.xExpand;
    }

    public NumberProperty getYExpand() {
        return this.yExpand;
    }
}

