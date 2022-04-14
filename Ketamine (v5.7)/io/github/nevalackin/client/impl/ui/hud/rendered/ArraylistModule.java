package io.github.nevalackin.client.impl.ui.hud.rendered;

import com.google.common.collect.Lists;
import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.api.ui.cfont.CustomFontRenderer;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.event.render.overlay.RenderGameOverlayEvent;
import io.github.nevalackin.client.impl.property.*;
import io.github.nevalackin.client.impl.ui.hud.Quadrant;
import io.github.nevalackin.client.impl.ui.hud.components.HudComponent;
import io.github.nevalackin.client.util.render.BloomUtil;
import io.github.nevalackin.client.util.render.BlurUtil;
import io.github.nevalackin.client.util.render.ColourUtil;
import io.github.nevalackin.client.util.render.DrawUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public final class ArraylistModule extends Module implements HudComponent {

    private double xPos, yPos;
    private double width, height;

    private boolean dragon;

    // Background
    private final BooleanProperty backgroundProperty = new BooleanProperty("Background", true);
    private final BooleanProperty backgroundBlurProperty = new BooleanProperty("Blur Background", true);
    private final DoubleProperty backgroundOpacityProperty = new DoubleProperty("Background Opacity", 50, backgroundProperty::getValue, 0, 100, 1);

    // Lines
    private final MultiSelectionEnumProperty<LineSelection> lineSelectionProperty = new MultiSelectionEnumProperty<>("Lines",
            Lists.newArrayList(LineSelection.TOP, LineSelection.SIDE),
            LineSelection.values());

    // Sorting
    private final BooleanProperty hideRenderProperty = new BooleanProperty("Hide Render Modules", true);

    // Colours
    private final EnumProperty<Colour> colourProperty = new EnumProperty<>("Colour", Colour.CLIENT);
    public final ColourProperty startColourProperty = new ColourProperty("Start Colour", 0xFFFA00BC, this::isBlend);
    public final ColourProperty endColourProperty = new ColourProperty("End Colour", 0xFF00E4FF, this::isBlend);

    public ArraylistModule() {
        super("Arraylist", Category.RENDER, Category.SubCategory.RENDER_OVERLAY);
        register(backgroundProperty, backgroundBlurProperty, backgroundOpacityProperty, lineSelectionProperty, hideRenderProperty, colourProperty, startColourProperty, endColourProperty);
        setX(882.0f);
        setY(4.0f);
        setEnabled(true);
    }

    @EventLink
    private final Listener<RenderGameOverlayEvent> onRenderOverlay = event -> {
        render(event.getScaledResolution().getScaledWidth(), event.getScaledResolution().getScaledHeight(), event.getPartialTicks());
    };

    @Override
    public boolean isDragging() {
        return dragon;
    }

    @Override
    public void setDragging(boolean dragging) {
        this.dragon = dragging;
    }

    @Override
    public void render(int scaledWidth, int scaledHeight, double tickDelta) {
        fitInScreen(scaledWidth, scaledHeight);

        double x = getX();
        double y = getY();

        final CustomFontRenderer fontRenderer = KetamineClient.getInstance().getFontRenderer();

        Comparator<String> lengthComparator = (a, b) -> {
            double ab = fontRenderer.getWidth(a);
            double bb = fontRenderer.getWidth(b);
            return Double.compare(bb, ab);
        };

        final int startColour = colourProperty.getValue().getModColour(startColourProperty.getValue(), endColourProperty.getValue(), 0);

        Quadrant quad = getQuadrant(scaledWidth, scaledHeight);

        List<String> features = KetamineClient.getInstance().getModuleManager().getModules().stream()
                .filter(Module::isDisplayed).filter(m -> !m.getCategory().equals(Category.RENDER) || !hideRenderProperty.getValue())
                .map(Module::getName)
                .sorted(quad.isBottom() ? lengthComparator.reversed() : lengthComparator)
                .collect(Collectors.toList());

        double margin = 2.0f;
        double lineWidth = 2.0f;
        double elementHeight = 11.0f;

        for (int i = 0, size = features.size(); i < size; i++) {
            String feature = features.get(i);
            double bounds = fontRenderer.getWidth(feature);
            double elementY = y + i * elementHeight;
            double elementWidth = bounds + margin * 2.0f;
            width = Math.max(elementWidth + lineWidth, width);


            final int colour = colourProperty.getValue().getModColour(startColourProperty.getValue(), endColourProperty.getValue(), i);

            if (backgroundProperty.getValue()) {
                DrawUtil.glDrawFilledQuad(quad.isRight() ? x - lineWidth + width - bounds - 2 : x + lineWidth, elementY, elementWidth, elementHeight, (int) (0xFF * (backgroundOpacityProperty.getValue() / 100.0)) << 24);
            }
            if (backgroundBlurProperty.getValue()) {
                BlurUtil.blurArea(quad.isRight() ? x - lineWidth + width - bounds - 2 : x + lineWidth, elementY, elementWidth, elementHeight);
            }

            // Top
            if (lineSelectionProperty.isSelected(LineSelection.TOP) && i == 0) {
                BloomUtil.drawAndBloom(() -> DrawUtil.glDrawSidewaysGradientRect(quad.isRight() ? x - lineWidth + width - bounds - 2 : x + lineWidth, elementY - 1, elementWidth, 1, startColour, colour));
            }
            // Side
            if (lineSelectionProperty.isSelected(LineSelection.SIDE)) {
                BloomUtil.drawAndBloom(() -> DrawUtil.glDrawSidewaysGradientRect(quad.isRight() ? x - lineWidth + width + 2 : x + lineWidth - 1, elementY - 1, 1, elementHeight + 2, startColour, colour));
            }
            // Bottom
            if (lineSelectionProperty.isSelected(LineSelection.BOTTOM) && i == size - 1) {
                BloomUtil.drawAndBloom(() -> DrawUtil.glDrawSidewaysGradientRect(quad.isRight() ? x - lineWidth + width - bounds - 2 : x + lineWidth, elementY + elementHeight, elementWidth, 1, colour, startColour));
            }

            // Render Modules
            fontRenderer.drawWithShadow(feature, quad.isRight() ? x - margin - lineWidth + width - bounds + 1.5 : x + margin + lineWidth,
                    elementY + margin - 2, .5, colour);

            height = i * elementHeight;
            setHeight(height + i);
        }
    }

    private boolean isBlend() {
        return this.colourProperty.getValue() == Colour.BLEND;
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void setX(double x) {
        xPos = x;
    }

    @Override
    public void setY(double y) {
        yPos = y;
    }

    @Override
    public double getX() {
        return xPos;
    }

    @Override
    public double getY() {
        return yPos;
    }

    @Override
    public double setWidth(double width) {
        return this.width = width;
    }

    @Override
    public double setHeight(double height) {
        return this.height = height;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setVisible(boolean visible) {

    }

    private enum LineSelection {
        TOP("Top"),
        SIDE("Side"),
        BOTTOM("Bottom");


        private final String name;

        LineSelection(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    @FunctionalInterface
    private interface ModColourFunc {
        int getColour(final int startColour, final int endColour, final int index);
    }

    private enum Colour {
        BLEND("Blend", ((startColour, endColour, index) -> {
            return ColourUtil.fadeBetween(startColour, endColour, index * 150L);
        })),
        CLIENT("Client Colour", ((startColour, endColour, index) -> {
            return ColourUtil.fadeBetween(ColourUtil.getClientColour(), ColourUtil.getSecondaryColour(), index * 150L);
        }));

        private final String name;
        private final ModColourFunc modColourFunc;

        Colour(String name, ModColourFunc modColourFunc) {
            this.name = name;
            this.modColourFunc = modColourFunc;
        }

        public int getModColour(final int startColour, final int endColour, final int index) {
            return modColourFunc.getColour(startColour, endColour, index);
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
