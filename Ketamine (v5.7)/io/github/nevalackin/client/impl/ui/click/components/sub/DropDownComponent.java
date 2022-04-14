package io.github.nevalackin.client.impl.ui.click.components.sub;

import io.github.nevalackin.client.api.ui.framework.Component;
import io.github.nevalackin.client.api.ui.framework.ExpandState;
import io.github.nevalackin.client.api.ui.framework.Expandable;
import io.github.nevalackin.client.api.ui.framework.Predicated;
import io.github.nevalackin.client.impl.property.EnumProperty;
import io.github.nevalackin.client.impl.property.MultiSelectionEnumProperty;
import io.github.nevalackin.client.util.render.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import static org.lwjgl.opengl.GL11.glTranslated;

public final class DropDownComponent extends Component implements Predicated, Expandable {

    private EnumProperty<?> property;
    private MultiSelectionEnumProperty<?> multiSelectionEnumProperty;
    private ExpandState state = ExpandState.CLOSED;
    private double progress;

    private final boolean last;

    private final boolean isMultiSelect;

    public DropDownComponent(final Component parent,
                             final EnumProperty<?> property,
                             final double x, final double y,
                             final double width, final double height,
                             final boolean last) {
        super(parent, x, y, width, height);

        this.property = property;

        this.last = last;

        this.isMultiSelect = false;
    }

    public DropDownComponent(final Component parent,
                             final MultiSelectionEnumProperty<?> property,
                             final double x, final double y,
                             final double width, final double height,
                             final boolean last) {
        super(parent, x, y, width, height);

        this.multiSelectionEnumProperty = property;

        this.last = last;

        this.isMultiSelect = true;
    }

    @Override
    public void onDraw(ScaledResolution scaledResolution, int mouseX, int mouseY) {
        final double x = this.getX();
        final double y = this.getY();
        final double w = this.getWidth();
        final double h = this.getHeight();

        final double textDrawPos = y + h / 2 - 4;

        // Draw property name
        FONT_RENDERER.draw(this.getPropertyName(), x + 4, textDrawPos, 0xFFEBEBEB);

        // Draw property value
        FONT_RENDERER.draw(this.getPropertyValueString(), x + w - FONT_RENDERER.getWidth(this.getPropertyValueString()) - 4, textDrawPos, 0xFFA5A5A5);

        // Calculate total expanded height
        final double expandedHeight = this.calculateExpandedHeight();

        // Handle drop down animation logic
        switch (this.getState()) {
            case CONTRACTING:
                if (this.progress <= 0.0) {
                    this.setState(ExpandState.CLOSED);
                } else {
                    this.progress -= 1.0 / Minecraft.getDebugFPS() * 5;
                }
                break;
            case EXPANDING:
                if (this.progress >= 1.0) {
                    this.setState(ExpandState.EXPANDED);
                } else {
                    this.progress += 1.0 / Minecraft.getDebugFPS() * 5;
                }
                break;
        }

        if (this.isExpanded()) {
            final double ex = this.getExpandedX();
            final double ey = this.getExpandedY();
            final double ew = this.getExpandedWidth();
            final double eh = expandedHeight * this.progress;

            // Draw triangle at top of settings (centred on module name)
            {
                final double triangleW = 7;
                final double triangleH = 3 * this.progress;
                final double xPosTriangle = x + 4 + 12;

                glTranslated(xPosTriangle, ey, 0);
                DrawUtil.glDrawTriangle(0, -triangleH,
                        -triangleW, 0,
                        triangleW, 0,
                        0x601A191B);
                glTranslated(-xPosTriangle, -ey, 0);
            }

            final boolean needScissor = this.progress != 1.0;

            if (this.last) {
                DrawUtil.glDrawRoundedQuad(ex, ey, (float) ew, (float) eh, 3.0f, 0x601A191B);
            } else {
                DrawUtil.glDrawFilledQuad(ex, ey, ew, eh + 2, 0x601A191B);
            }

            if (needScissor) {
                // Scissor box animated drop down
                glScissorBox(ex, ey, ew, eh, scaledResolution);
            }

            double yPos = ey + 2;

            if (isMultiSelect) {
                for (int i = 0; i < this.multiSelectionEnumProperty.getValues().length; i++) {
                    final String enumName = this.multiSelectionEnumProperty.getValues()[i].toString();

                    FONT_RENDERER.draw(enumName, ex + ew / 2.0 - FONT_RENDERER.getWidth(enumName) / 2.0, yPos,
                            // Highlight if is selected
                            this.multiSelectionEnumProperty.getValue().contains(this.multiSelectionEnumProperty.getValues()[i]) ? 0xFFA5A5A5 : 0xFF555555);

                    yPos += 14.0;
                }
            }
            else  {
                for (final Enum<?> value : this.property.getValues()) {
                    // Draw option name
                    FONT_RENDERER.draw(value.toString(), ex + ew / 2.0 - FONT_RENDERER.getWidth(value.toString()) / 2.0, yPos,
                            // Highlight if is selected
                            value == this.property.getValue() ? 0xFFA5A5A5 : 0xFF555555);

                    yPos += 14.0;
                }
            }

            if (needScissor) {
                DrawUtil.glRestoreScissor();
            }
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        if (this.isHovered(mouseX, mouseY) && button == 1) {
            if ((this.isMultiSelect ? this.multiSelectionEnumProperty.getValues().length : this.property.getValues().length) > 0) {
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

            return;
        }

        if (button == 0 && this.isExpanded() && this.isHoveredExpand(mouseX, mouseY)) {
            double yPos = this.getExpandedY() + 2;
            if (this.isMultiSelect) {
                for (int i = 0; i < this.multiSelectionEnumProperty.getValues().length; i++) {
                    if (mouseY >= this.getExpandedY() + 2 + i * 14 && mouseY <= this.getExpandedY() + 2 + i * 14 + 14) {
                        if (this.multiSelectionEnumProperty.isSelected(i))
                            this.multiSelectionEnumProperty.unselect(i);
                        else
                            this.multiSelectionEnumProperty.select(i);
                    }
                }
            }
            else {
                for (int i = 0; i < this.property.getValues().length; i++) {
                    if (mouseY > yPos && mouseY < yPos + 14) {
                        this.property.setValue(i);
                        this.setState(ExpandState.CONTRACTING);
                        return;
                    }

                    yPos += 14.0;
                }
            }
        }
    }

    @Override
    public boolean isVisible() {
        return this.isMultiSelect ? this.multiSelectionEnumProperty.check() : this.property.check();
    }

    @Override
    public void setState(ExpandState state) {
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

    @Override
    public ExpandState getState() {
        return this.state;
    }

    @Override
    public double calculateExpandedHeight() {
        return this.isMultiSelect ? this.multiSelectionEnumProperty.getValues().length * 14.0 : this.property.getValues().length * 14.0;
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
    public double getExpandProgress() {
        return this.progress;
    }

    private String getPropertyName() {
        return this.isMultiSelect ? this.multiSelectionEnumProperty.getName() : this.property.getName();
    }

    private String getPropertyValueString() {
        return this.isMultiSelect ? String.format("%d Selected", this.multiSelectionEnumProperty.getValue().size()) : this.property.getValue().toString();
    }
}