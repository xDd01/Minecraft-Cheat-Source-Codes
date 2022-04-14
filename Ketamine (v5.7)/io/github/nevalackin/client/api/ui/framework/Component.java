package io.github.nevalackin.client.api.ui.framework;

import io.github.nevalackin.client.api.ui.cfont.CustomFontRenderer;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.ui.click.components.PanelComponent;
import io.github.nevalackin.client.impl.ui.nl.components.Theme;
import io.github.nevalackin.client.util.render.DrawUtil;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

public abstract class Component {

    protected static final CustomFontRenderer FONT_RENDERER = KetamineClient.getInstance().getFontRenderer();

    private final List<Component> children = new ArrayList<>();
    private final Component parent;
    private double x;
    private double y;
    private double width;
    private double height;

    private double scissorBottom;
    protected double transformOffset;

    public Component(final Component parent,
                     final double x,
                     final double y,
                     final double width,
                     final double height) {
        this.parent = parent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Component getParent() {
        return this.parent;
    }

    public void addChild(Component child) {
        this.children.add(child);
    }

    public void onDraw(final ScaledResolution scaledResolution,
                       int mouseX,
                       int mouseY) {
        for (Component child : this.children) {
            child.onDraw(scaledResolution, mouseX, mouseY);
        }
    }

    public void onMouseClick(int mouseX,
                             int mouseY,
                             int button) {
        for (Component child : this.children) {
            child.onMouseClick(mouseX, mouseY, button);
        }
    }

    public void onMouseRelease(int button) {
        for (Component child : this.children) {
            child.onMouseRelease(button);
        }
    }

    public void onKeyPress(int keyCode) {
        for (Component child : this.children) {
            child.onKeyPress(keyCode);
        }
    }

    public double getTransformOffset() {
        Component familyMember = this.parent;
        double familyTreeTransform = this.transformOffset;

        while (familyMember != null) {
            familyTreeTransform += familyMember.transformOffset;
            familyMember = familyMember.parent;
        }

        return familyTreeTransform;
    }

    protected void glScissorBox(final double x, final double y,
                                final double width, final double height,
                                final ScaledResolution scaledResolution) {
        this.glScissorBox(x, y, width, height, true, scaledResolution);
    }

    protected void glScissorBox(final double x, final double y,
                                final double width, final double height,
                                final boolean useTransform,
                                final ScaledResolution scaledResolution) {
        final double yPos = useTransform ? y + this.getTransformOffset() : y;

        if (this.parent != null && yPos > this.parent.scissorBottom) return;

        final double currentScissorBottom = yPos + height;

        if (this.parent != null && currentScissorBottom > this.parent.scissorBottom) {
            DrawUtil.glScissorBox(x, yPos, width, height - (currentScissorBottom - this.parent.scissorBottom), scaledResolution);
        } else {
            this.scissorBottom = currentScissorBottom;
            DrawUtil.glScissorBox(x, yPos, width, height, scaledResolution);
        }
    }

    public double getX() {
        Component familyMember = this.parent;
        double familyTreeX = this.x;

        while (familyMember != null) {
            familyTreeX += familyMember.x;
            familyMember = familyMember.parent;
        }

        return familyTreeX;
    }

    public void setX(double x) {
        this.x = x;
    }

    public boolean isHovered(int mouseX,
                             int mouseY) {


        /*if (GUI.guiProperty.getValue().equals(GUI.type.NEVERLOSE)) {
            if (this instanceof Expandable) {
                final Expandable expandableComponent = (Expandable) this;
                if (expandableComponent.isHoveredExpanded(mouseX, mouseY)) return true;
            }
        }*/

        final double x = this.getX();
        final double y = this.getY();

        return mouseX > x && mouseY > y && mouseX < x + getWidth() && mouseY < y + getHeight();
    }

    public double getY() {
        Component familyMember = this.parent;
        double familyTreeY = this.y;

        while (familyMember != null) {
            familyTreeY += familyMember.y;
            familyMember = familyMember.parent;
        }

        return familyTreeY;
    }

    public Theme getTheme() {
        return this.parent.getTheme();
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public List<Component> getChildren() {
        return children;
    }

    public int getColour(final Component child) {
        Component familyMember = parent;

        while (familyMember != null) {
            if (familyMember instanceof PanelComponent)
                return familyMember.getColour(child);

            familyMember = familyMember.parent;
        }
        return 0xFFFFFFFF;
    };

    public int getTextOffset(final Component child) {
        Component familyMember = parent;

        while (familyMember != null) {
            if (familyMember instanceof PanelComponent)
                return familyMember.getTextOffset(child);

            familyMember = familyMember.parent;
        }
        return 4;
    };
}
