package io.github.nevalackin.client.impl.ui.click.components;

import io.github.nevalackin.client.api.binding.Bind;
import io.github.nevalackin.client.api.binding.BindType;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.api.notification.NotificationType;
import io.github.nevalackin.client.api.property.Property;
import io.github.nevalackin.client.api.ui.framework.Component;
import io.github.nevalackin.client.api.ui.framework.ExpandState;
import io.github.nevalackin.client.api.ui.framework.Expandable;
import io.github.nevalackin.client.api.ui.framework.Predicated;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.event.game.input.InputType;
import io.github.nevalackin.client.impl.property.*;
import io.github.nevalackin.client.impl.ui.click.components.sub.CheckBoxComponent;
import io.github.nevalackin.client.impl.ui.click.components.sub.ColourPickerComponent;
import io.github.nevalackin.client.impl.ui.click.components.sub.DropDownComponent;
import io.github.nevalackin.client.impl.ui.click.components.sub.SliderComponent;
import io.github.nevalackin.client.util.render.BloomUtil;
import io.github.nevalackin.client.util.render.ColourUtil;
import io.github.nevalackin.client.util.render.DrawUtil;
import javafx.scene.effect.Bloom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Keyboard;

import java.util.Iterator;

import static org.lwjgl.opengl.GL11.glTranslated;

public final class ModuleComponent extends Component implements Expandable {

    private final Module module;

    private boolean binding;

    public double enableProgress;
    private int mouseX, mouseY;

    private ExpandState state = ExpandState.CLOSED;
    private double progress;

    private final boolean last;

    private final ModuleComponent previousModuleComponent;

    public ModuleComponent(final Component parent,
                           final Module module,
                           final double x,
                           final double y,
                           final double width,
                           final double height,
                           final boolean last,
                           final ModuleComponent previousModuleComponent) {
        super(parent, x, y, width, height);

        this.module = module;
        this.last = last;

        // TODO: Alrat
        this.previousModuleComponent = previousModuleComponent;

        final Iterator<Property<?>> properties = module.getProperties().iterator();

        while (properties.hasNext()) {
            final Property<?> property = properties.next();

            if (property instanceof BooleanProperty) {
                addChild(new CheckBoxComponent(this, (BooleanProperty) property,
                        0.0, 0.0, getExpandedWidth(), 16.0));
            } else if (property instanceof EnumProperty) {
                addChild(new DropDownComponent(this, (EnumProperty<?>) property,
                        0.0, 0.0, getExpandedWidth(), 16.0,
                        last && !properties.hasNext()));
            }
            else if (property instanceof DoubleProperty) {
                addChild(new SliderComponent(this, (DoubleProperty) property,
                        0.0, 0.0, getExpandedWidth(), 20.0));
            } else if (property instanceof ColourProperty) {
                addChild(new ColourPickerComponent(this, (ColourProperty) property,
                        0.0, 0.0, getExpandedWidth(), 16.0,
                        last && !properties.hasNext()));
            }
            else if (property instanceof MultiSelectionEnumProperty) {
                addChild(new DropDownComponent(this, (MultiSelectionEnumProperty<?>) property,
                        0.0, 0.0, getExpandedWidth(), 16.0,
                        last && !properties.hasNext()));
            }
        }
    }

    @Override
    public void onDraw(ScaledResolution scaledResolution, int mouseX, int mouseY) {
        final double x = getX();
        final double y = getY();
        final double w = getWidth();
        final double h = getHeight();

        final boolean enabled = module.isEnabled();

        final double moduleBackgroundOffset = x + PanelComponent.MODULE_ENABLE_BAR_W + PanelComponent.MODULE_SEPARATOR_W;
        final String text = binding ? "Press a key..." : module.getName();

        final boolean needScissor = progress != 1.0;

        // Calculate total expanded height
        final double expandedHeight = calculateExpandedHeight();

        final double ex = getExpandedX();
        final double ey = getExpandedY();
        final double ew = getExpandedWidth();
        final double eh = expandedHeight * DrawUtil.bezierBlendAnimation(progress);
        final int colour = getColour(this);
        final int offset = getTextOffset(this);

        // Handle drop down animation logic
        final double speed = 6 - Math.min(4, Math.sqrt(expandedHeight) / 5);

        switch (getState()) {
            case CONTRACTING:
                if (progress <= 0.0) {
                    setState(ExpandState.CLOSED);
                } else {
                    progress -= 1.0 / Minecraft.getDebugFPS() * speed;
                }
                break;
            case EXPANDING:
                if (progress >= 1.0) {
                    setState(ExpandState.EXPANDED);
                } else {
                    progress += 1.0 / Minecraft.getDebugFPS() * speed;
                }
                break;
        }

        // Draw button on left side
        {
            // Handle button animation
            if (enabled) {
                if (enableProgress >= 1.0) {
                    enableProgress = 1.0;
                } else {
                    enableProgress += 1.0 / Minecraft.getDebugFPS() * 5;
                }
            }
            else {
                if (enableProgress <= 0.0) {
                    enableProgress = 0.0;
                } else {
                    enableProgress -= 1.0 / Minecraft.getDebugFPS() * 5;
                }
            }

            // TODO: Alrat
            int currentColour = ColourUtil.fadeBetween(0x001E1E1E, colour, enableProgress);
            int previousColour = currentColour;

            if (enableProgress > 0 && previousModuleComponent != null) {
                previousColour = ColourUtil.fadeBetween(0x001E1E1E, getColour(previousModuleComponent), enableProgress);
            }

            if (last) {
                DrawUtil.glDrawRoundedRectEllipse(x + PanelComponent.MODULE_ENABLE_BAR_W / 2 - 14, y + h / 2 - 8,
                        getWidth() + 2, getHeight() - 1, DrawUtil.RoundingMode.BOTTOM, 12,
                        4.0f, previousColour);
            } else {
                DrawUtil.glDrawFilledQuad(x + PanelComponent.MODULE_ENABLE_BAR_W / 2 - 14, y + h / 2 - 8, getWidth() + 1, getHeight(),
                        previousColour, currentColour);
            }
        }

        // Draw module name or "press a key" when binding
        FONT_RENDERER.draw(text,
                moduleBackgroundOffset + offset,
                y + h / 2 - 4,
                enabled ? 0xFFEBEBEB : 0xFFA5A5A5);

        // Draw + or - when expanded or closed
        if (hasChildren()) {
            DrawUtil.glDrawPlusSign(x + w - 12, y + h / 2, 6, progress * 180, 0xFFEBEBEB);
        }

        if (isExpanded()) {
            // Draw triangle at top of settings (centred on module name)
            {
                final double triangleW = 7;
                final double triangleH = 3 * progress;
                final double xPosTriangle = moduleBackgroundOffset + offset + 8;

                glTranslated(xPosTriangle, ey, 0);
                DrawUtil.glDrawTriangle(0, -triangleH,
                        -triangleW, 0,
                        triangleW, 0,
                        0x601A191B);
                glTranslated(-xPosTriangle, -ey, 0);
            }

            // Draw properties background rect
            if (last) {
                DrawUtil.glDrawRoundedQuad(ex, ey, (float) ew, (float) eh, 3.0f, 0x601A191B);
            } else {
                DrawUtil.glDrawFilledQuad(ex, ey, ew, eh + 2, 0x601A191B);
            }

            // Draw properties
            double yPos = getHeight();

            for (final Component component : getChildren()) {
                if (component instanceof Predicated) {
                    final Predicated Predicated = (Predicated) component;
                    if (!Predicated.isVisible()) continue;
                }

                component.setY(yPos);

                if (needScissor) {
                    // Scissor animated drop down
                    glScissorBox(ex, ey, ew, eh, scaledResolution);
                }

                component.onDraw(scaledResolution, mouseX, mouseY);

                if (component instanceof Expandable) {
                    final Expandable Expandable = (Expandable) component;
                    if (Expandable.isExpanded()) {
                        yPos += Expandable.getExpandedHeight();
                    }
                }

                if (needScissor) {
                    DrawUtil.glRestoreScissor();
                }

                yPos += component.getHeight();
            }
        }
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (isHovered(mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    module.toggle();
                    return;
                case 1:
                    if (hasChildren()) {
                        // Expand logic
                        switch (getState()) {
                            case CLOSED:
                            case CONTRACTING:
                                setState(ExpandState.EXPANDING);
                                break;
                            case EXPANDED:
                            case EXPANDING:
                                setState(ExpandState.CONTRACTING);
                                break;
                        }
                        return;
                    }
                    break;
                case 2:
                    binding = true;
                    return;
            }
        }

        if (isExpanded() && isHoveredExpand(mouseX, mouseY)) {
            // Properties mouse click
            double yPos = getHeight();

            for (final Component component : getChildren()) {
                if (component instanceof Predicated) {
                    final Predicated predicateComponent = (Predicated) component;
                    if (!predicateComponent.isVisible()) continue;
                }

                component.setY(yPos);

                // Handle mouse click
                component.onMouseClick(mouseX, mouseY, mouseButton);

                if (component instanceof Expandable) {
                    final Expandable expandableComponent = (Expandable) component;
                    if (expandableComponent.isExpanded()) {
                        yPos += expandableComponent.getExpandedHeight();
                    }
                }

                yPos += component.getHeight();
            }
        }
    }

    @Override
    public void onKeyPress(int keyCode) {
        if (binding) {
            if (keyCode != Keyboard.KEY_RSHIFT && keyCode != Keyboard.KEY_ESCAPE) {
                if (keyCode != Keyboard.KEY_DELETE && keyCode != Keyboard.KEY_BACK) {
                    module.setBind(new Bind(InputType.KEYBOARD, keyCode, BindType.TOGGLE));
                    KetamineClient.getInstance().getNotificationManager().add(NotificationType.INFO, String.format("Bound %s", module.getName()),
                            String.format("Set %s bind to %s", module.getName(), Keyboard.getKeyName(keyCode)),
                            2000L);
                }
                else {
                    module.setBind(null);
                    KetamineClient.getInstance().getNotificationManager().add(NotificationType.INFO, "Unbound Module",
                            String.format("Unbound %s", module.getName()),
                            2000L);
                }
                binding = false;
            }

            return;
        }

        super.onKeyPress(keyCode);
    }

    @Override
    public double calculateExpandedHeight() {
        double height = 0;

        for (final Component component : getChildren()) {
            if (component instanceof Predicated) {
                final Predicated Predicated = (Predicated) component;
                if (!Predicated.isVisible()) continue;
            }

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


    @Override
    public double getExpandedX() {
        return getX();
    }

    @Override
    public double getExpandedY() {
        return getY() + getHeight();
    }

    @Override
    public double getExpandedWidth() {
        return getWidth();
    }

    @Override
    public void setState(final ExpandState state) {
        this.state = state;

        switch (state) {
            case CLOSED:
                progress = 0.0;
                break;
            case EXPANDED:
                progress = 1.0;
                break;
        }
    }

    @Override
    public ExpandState getState() {
        return state;
    }

    @Override
    public double getExpandProgress() {
        return progress;
    }
}