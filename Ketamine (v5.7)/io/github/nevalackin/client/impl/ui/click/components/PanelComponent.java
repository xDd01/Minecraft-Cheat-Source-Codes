package io.github.nevalackin.client.impl.ui.click.components;

import io.github.nevalackin.client.api.ui.cfont.StaticallySizedImage;
import io.github.nevalackin.client.api.ui.framework.Component;
import io.github.nevalackin.client.api.ui.framework.ExpandState;
import io.github.nevalackin.client.api.ui.framework.Expandable;
import io.github.nevalackin.client.util.misc.ResourceUtil;
import io.github.nevalackin.client.util.render.BlurUtil;
import io.github.nevalackin.client.util.render.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public abstract class PanelComponent extends Component implements Expandable {

    private boolean dragging;
    private double prevX, prevY;

    private ExpandState state = ExpandState.CLOSED;
    private double progress;

    private final String displayName;
    private double scrollbarActiveFadeInOut;

    private boolean hasScrollbar;
    private double maxPanelHeight;
    private int scrollOffset;

    private final StaticallySizedImage icon;
    private int scrollVelocity;

    private ComponentColourFunc childColourFunc;

    private ComponentOffsetFunc childTextOffsetFunc;


    private final int tabColour;

    private final boolean drawModuleBar;

    public PanelComponent(final String displayName,
                          final String iconPath,
                          final int tabColour,
                          final double x, final double y,
                          final double width, final double headerHeight,
                          final boolean drawModuleBar) {

        super(null, x, y, width, headerHeight);

        this.displayName = displayName;
        this.tabColour = tabColour;

        try {
            this.icon = new StaticallySizedImage(ImageIO.read(ResourceUtil.getResourceStream(String.format("icons/dropdown/%s.png", iconPath))), true, 2);
        } catch (IOException e) {
            throw new IllegalArgumentException("Resource does not exist.");
        }

        this.initComponents();

        this.drawModuleBar = drawModuleBar;
    }

    public void clearChildren() {
        this.getChildren().clear();
    }

    @Override
    public void onDraw(final ScaledResolution scaledResolution,
                       final int mouseX, final int mouseY) {
        final int colour = getColour(this);

        // Update position when dragging
        if (this.dragging) {
            this.setX(mouseX - this.prevX);
            this.setY(mouseY - this.prevY);
        }

        final double x = this.getX();
        final double y = this.getY();
        final double width = this.getWidth();
        final double height = this.getHeight();

        final boolean expanded = this.isExpanded();

        BlurUtil.blurArea(x, y + 1, width, height);
        // Draw header rect
        DrawUtil.glDrawRoundedRectEllipse(x, y + 1, width, height,
                expanded ? DrawUtil.RoundingMode.TOP : DrawUtil.RoundingMode.FULL,
                12, 3.0f, 0x751E1E1E);

        // Going to scale icon to 12x12 px
        final int iconSize = 12;

        FONT_RENDERER.draw(this.displayName,
                x + iconSize + 6.0,
                y + height / 2.0 - 4,
                0xFFD4D4D4);

        // Draw category icon (colour is saved from drawString above)
        this.icon.draw(x + 2, y + 1 + height / 2 - iconSize / 2.0F, iconSize, iconSize, colour);

        // Calculate total height when expanded
        final double expandedHeight = this.calculateExpandedHeight();

        final double eh = expandedHeight * DrawUtil.bezierBlendAnimation(this.progress);

        final int halfHeight = scaledResolution.getScaledHeight() / 2;
        // Only be able to scroll when the total height of the panel is greater than half ur screen
        this.hasScrollbar = expandedHeight > halfHeight;
        // The max amount of vertical space visible at one time
        this.maxPanelHeight = Math.min(eh, halfHeight);
        // If there is no scroll bar reset the scroll offset
        if (!this.hasScrollbar) {
            this.scrollOffset = 0;
        }

        // Handle drop down animation logic
        final double speed = 6 - Math.min(3, Math.sqrt(expandedHeight) / 5);

        switch (this.getState()) {
            case CONTRACTING:
                if (this.progress <= 0.0) {
                    this.setState(ExpandState.CLOSED);
                } else {
                    this.progress -= 1.0 / Minecraft.getDebugFPS() * speed;
                }
                break;
            case EXPANDING:
                if (this.progress >= 1.0) {
                    this.setState(ExpandState.EXPANDED);
                } else {
                    this.progress += 1.0 / Minecraft.getDebugFPS() * speed;
                }
                break;
        }

        if (expanded) {
            //DrawUtil.glDrawLine(x, y + height, x + width, y + height, 0.5F, true, 0xFFFFFFFF);

            final double ex = this.getExpandedX();
            final double ey = this.getExpandedY();
            final double ew = this.getExpandedWidth();

            if (this.hasScrollbar) {
                if (this.isHoveredExpand(mouseX, mouseY)) {
                    final int scrollAmount = -Mouse.getDWheel() / 20;

                    if (scrollAmount != 0) {
                        this.scrollVelocity += scrollAmount;
                        this.scrollbarActiveFadeInOut = 1.0;
                    }

                    this.scrollVelocity *= 0.98;

                    this.scrollOffset = Math.max(0, Math.min((int) (expandedHeight - this.maxPanelHeight), this.scrollOffset + this.scrollVelocity));
                }

                glScissorBox(ex, ey, ew, this.maxPanelHeight, scaledResolution);

                if (this.scrollOffset > 0) {
                    glTranslated(0, -this.scrollOffset, 0);
                    this.transformOffset -= scrollOffset;
                }
            }

            if (this.drawModuleBar) {
                BlurUtil.blurArea(ex, ey, MODULE_ENABLE_BAR_W, maxPanelHeight);
                DrawUtil.glDrawRoundedRectEllipse(ex, ey, MODULE_ENABLE_BAR_W, eh,
                        DrawUtil.RoundingMode.BOTTOM_LEFT,
                        12, 3.0f, 0x751E1E1E);

                BlurUtil.blurArea(ex + MODULE_ENABLE_BAR_W, ey,
                        MODULE_SEPARATOR_W, maxPanelHeight);
                // Draw separator
                DrawUtil.glDrawFilledQuad(ex + MODULE_ENABLE_BAR_W, ey,
                        MODULE_SEPARATOR_W, eh,
                        0x751E1E1E);
            }

            BlurUtil.blurArea(this.drawModuleBar ? ex + MODULE_ENABLE_BAR_W + MODULE_SEPARATOR_W : ex,
                    ey, this.drawModuleBar ? ew - MODULE_ENABLE_BAR_W - MODULE_SEPARATOR_W : ew,
                    maxPanelHeight);
            // Draw module background on right
            DrawUtil.glDrawRoundedRectEllipse(this.drawModuleBar ? ex + MODULE_ENABLE_BAR_W + MODULE_SEPARATOR_W : ex,
                    ey, this.drawModuleBar ? ew - MODULE_ENABLE_BAR_W - MODULE_SEPARATOR_W : ew,
                    eh, this.drawModuleBar ? DrawUtil.RoundingMode.BOTTOM_RIGHT : DrawUtil.RoundingMode.BOTTOM,
                    12, 3.0f, 0x751E1E1E);

            // Component drawing

            double yPos = this.getHeight();

            final List<Component> children = this.getChildren();
            for (int i = 0; i < children.size(); i++) {
                final Component component = children.get(i);

                component.setY(yPos);

                final boolean skipScissor = this.hasScrollbar && i == 0;

                if (!skipScissor) {
                    // Scissor box (with animated height)
                    glScissorBox(ex, ey, ew, this.hasScrollbar ? this.maxPanelHeight : eh + 1, false, scaledResolution);
                }

                component.onDraw(scaledResolution, mouseX, mouseY + this.scrollOffset);

                if (component instanceof Expandable) {
                    final Expandable Expandable = (Expandable) component;
                    if (Expandable.isExpanded()) {
                        yPos += Expandable.getExpandedHeight();
                    }
                }

                yPos += component.getHeight();
            }

            DrawUtil.glEndScissor();

            if (this.hasScrollbar) {
                if (this.scrollOffset > 0) {
                    // Restore matrix from scroll pos
                    glTranslated(0, this.scrollOffset, 0);
                    this.transformOffset += this.scrollOffset;
                    // Draw top shadow when scrolling
                    DrawUtil.glDrawFilledQuad(x, y + height, width, 6, 0x80000000, 0);
                }

                final double visiblePercentage = this.maxPanelHeight / eh;
                final double scrollbarHeight = visiblePercentage * this.maxPanelHeight;
                final double scrollBarWidth = 2;

                glPushMatrix();
                // Translate to top of scrollbar
                glTranslated(ex + width - (scrollBarWidth + 1), ey + (this.maxPanelHeight - scrollbarHeight) * (this.scrollOffset / (eh - this.maxPanelHeight)), 0);
                glPopMatrix();
            }
        } else {
            this.scrollOffset = 0;
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        if (this.isHovered(mouseX, mouseY)) {
            switch (button) {
                case 0:
                    // Drag logic
                    this.dragging = true;
                    this.prevX = mouseX - this.getX();
                    this.prevY = mouseY - this.getY();
                    break;
                case 1:
                    if (this.hasChildren()) {
                        // Expand logic
                        switch (this.getState()) {
                            case CLOSED:
                            case CONTRACTING:
                                this.setState(ExpandState.EXPANDING);
                                break;
                            case EXPANDED:
                            case EXPANDING:
                                this.setState(ExpandState.CONTRACTING);
                                break;
                        }
                    }

                    break;
            }
        }

        // Component mouse clicking
        if (this.isExpanded() && this.isHoveredExpand(mouseX, mouseY)) {
            double yPos = this.getHeight();
            try {
                for (final Component component : this.getChildren()) {
                    component.setY(yPos);

                    component.onMouseClick(mouseX, mouseY + this.scrollOffset, button);

                    if (component instanceof Expandable) {
                        final Expandable expandableComponent = (Expandable) component;
                        if (expandableComponent.isExpanded()) {
                            yPos += expandableComponent.getExpandedHeight();
                        }
                    }

                    yPos += component.getHeight();
                }
            } catch (Exception e
            ) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean isHoveredExpand(int mouseX, int mouseY) {
        final double ex = this.getExpandedX();
        final double ey = this.getExpandedY();
        final double ew = this.getExpandedWidth();
        final double eh = Math.min(this.getExpandedHeight(), this.maxPanelHeight);

        return mouseX > ex && mouseY > ey && mouseX < ex + ew && mouseY < ey + eh;
    }

    @Override
    public void onMouseRelease(int button) {
        if (button == 0)
            this.dragging = false;

        super.onMouseRelease(button);
    }

    @Override
    public double calculateExpandedHeight() {
        double height = 0;

        for (final Component component : this.getChildren()) {
            if (component instanceof Expandable) {
                final Expandable Expandable = (Expandable) component;
                if (Expandable.isExpanded()) {
                    height += Expandable.getExpandedHeight();
                }
            }

            height += component.getHeight();
        }

        return height;
    }

    public ComponentColourFunc getChildColourFunc() {
        return childColourFunc;
    }

    public void setChildColourFunc(ComponentColourFunc childColourFunc) {
        this.childColourFunc = childColourFunc;
    }

    @Override
    public double getExpandedX() {
        return this.getX();
    }

    @Override
    public double getExpandedY() {
        return this.getY() + this.getHeight();
    }

    @Override
    public double getExpandedWidth() {
        return this.getWidth();
    }

    @Override
    public ExpandState getState() {
        return this.state;
    }

    @Override
    public void setState(final ExpandState state) {
        this.state = state;

        switch (state) {
            case CLOSED:
                this.progress = 0.0;
                break;
            case EXPANDED:
                this.progress = 1.0;
                break;
        }
    }

    public int getTabColour() {
        return tabColour;
    }

    //If this is the parent of the module buttons it would be returning the color
    //But calling getColor from here has a null parent so it just returns white

    public void setTextOffsetFunc(ComponentOffsetFunc childTextOffsetFunc) {
        this.childTextOffsetFunc = childTextOffsetFunc;
    }

    @Override
    public int getColour(final Component child) {
        return this.childColourFunc.apply(child);
    }

    @Override
    public int getTextOffset(final Component child) {
        return this.childTextOffsetFunc.apply(child);
    }

    @Override
    public double getExpandProgress() {
        return this.progress;
    }

    public static final double MODULE_ENABLE_BAR_W = 26;
    public static final double MODULE_SEPARATOR_W = 2;

    public abstract void initComponents();
}

