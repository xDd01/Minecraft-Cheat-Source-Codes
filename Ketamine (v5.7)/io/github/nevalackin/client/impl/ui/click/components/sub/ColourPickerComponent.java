package io.github.nevalackin.client.impl.ui.click.components.sub;

import io.github.nevalackin.client.api.ui.framework.Component;
import io.github.nevalackin.client.api.ui.framework.ExpandState;
import io.github.nevalackin.client.api.ui.framework.Expandable;
import io.github.nevalackin.client.api.ui.framework.Predicated;
import io.github.nevalackin.client.impl.property.ColourProperty;
import io.github.nevalackin.client.util.render.ColourUtil;
import io.github.nevalackin.client.util.render.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public final class ColourPickerComponent extends Component implements Expandable, Predicated {

    private final ColourProperty property;

    private ExpandState state = ExpandState.CLOSED;
    private double progress;

    private boolean colorSelectorDragging;
    private boolean alphaBarDragging;
    private boolean hueBarDragging;

    private final boolean last;

    public ColourPickerComponent(final Component parent,
                                 final ColourProperty property,
                                 final double x, final double y,
                                 final double width, final double height,
                                 final boolean last) {
        super(parent, x, y, width, height);

        this.property = property;

        this.last = last;
    }

    @Override
    public void onDraw(ScaledResolution scaledResolution, int mouseX, int mouseY) {
        final double x = this.getX();
        final double y = this.getY();
        final double w = this.getWidth();
        final double h = this.getHeight();

        final int colour = this.property.getValue();

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

        // Draw property name
        FONT_RENDERER.draw(this.property.getName(), x + 4, y + h / 2 - 4, 0xFFEBEBEB);

        // Colour preview
        {
            // Outline
            DrawUtil.glDrawFilledQuad(x + w - 4 - 12 - 0.5, y + h / 2 - 2.5 - 0.5, 13, 7, 0xFF000000);

            // Background
            drawCheckeredBackground(x + w - 4 - 12, y + h / 2 - 2.5, 2, 6, 3);

            // Draw colour over
            DrawUtil.glDrawFilledQuad(x + w - 4 - 12, y + h / 2 - 2.5, 12, 6, colour);
        }

        if (this.isExpanded()) {
            final double ex = this.getExpandedX();
            final double ey = this.getExpandedY();
            final double ew = this.getExpandedWidth();
            final double eh = expandedHeight * this.progress;

            if (this.last) {
                DrawUtil.glDrawRoundedQuad(ex, ey, (float) ew, (float) eh,  3.0f, 0x991A191B);
            } else {
                DrawUtil.glDrawFilledQuad(ex, ey, ew, eh, 0x991A191B);
            }

            final boolean needScissor = this.progress != 1.0;

            if (needScissor) {
                // Start animated expand scissor box
                glScissorBox(ex, ey, ew, eh, scaledResolution);
            }

            final double cpX = ex + 4;
            final double cpY = ey + 4;

            final double alphaBarHeight = 12;
            final double hueSliderWidth = 10;

            final double cpWidth = this.getWidth() - 12 - hueSliderWidth;
            final double cpHeight = expandedHeight - 12 - alphaBarHeight;

            final double alphaBarY = cpY + cpHeight + 4;
            final double hueBarX = cpX + cpWidth + 4;

            final Color color = this.property.getColour();

            final float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);


            // Draw colour picker
            {
                if (this.colorSelectorDragging) {
                    final float saturation = (float) ((mouseX - cpX) / cpWidth);
                    final float brightness =  1.0F - (float) ((mouseY - cpY) / cpHeight);

                    this.property.setValue(ColourUtil.overwriteAlphaComponent(
                            Color.HSBtoRGB(hsb[0], Math.max(0, Math.min(1, saturation)), Math.max(0, Math.min(1, brightness))), colour >> 24 & 0xFF));

                    this.colorSelectorDragging = mouseX > cpX && mouseY > cpY && mouseX < cpX + cpWidth && mouseY < cpY + cpHeight;
                }

                // Outline
                DrawUtil.glDrawFilledQuad(cpX - 0.5, cpY - 0.5, cpWidth + 1, cpHeight + 1, 0xFF << 24);

                drawColourPicker(cpX, cpY, cpWidth, cpHeight, hsb[0]);

                glPushMatrix();
                glTranslated(cpX + hsb[1] * cpWidth - 1, cpY + (1.0f - hsb[2]) * cpHeight - 1, 0);

                // Draw indicator
                DrawUtil.glDrawFilledQuad(0, 0, 2, 2, 0x80FFFFFF);
                DrawUtil.glDrawOutlinedQuad(0, 0, 2, 2, 1, 0xFF << 24);

                glPopMatrix();
            }

            // Draw alpha bar
            {
                if (this.alphaBarDragging) {
                    final int alpha = (int) (((mouseX - cpX) / cpWidth) * 255);

                    this.property.setValue(new Color(color.getRed(), color.getGreen(), color.getBlue(), Math.min(255, Math.max(0, alpha))));

                    this.alphaBarDragging = mouseX > cpX && mouseY > alphaBarY && mouseX < cpX + cpWidth && mouseY < alphaBarY + alphaBarHeight;
                }

                // Outline
                DrawUtil.glDrawFilledQuad(cpX - 0.5, cpY + cpHeight + 4 - 0.5, cpWidth + 1, alphaBarHeight + 1, 0xFF000000);

                drawCheckeredBackground(cpX, cpY + cpHeight + 4, 4, (int) (cpWidth / 4), 3);

                // Gradient alpha bar
                DrawUtil.glDrawSidewaysGradientRect(cpX, cpY + cpHeight + 4, cpWidth, alphaBarHeight, 0, colour | 0xFF000000);

                // Indicator
                DrawUtil.glDrawOutlinedQuad(cpX + ((colour >> 24 & 0xFF) / 255.0f) * cpWidth - 1, cpY + cpHeight + 4 - 0.5, 1, alphaBarHeight + 0.5, 1, 0xFF << 24);
            }

            // Draw hue bar
            {
                if (this.hueBarDragging) {
                    final float hue = (float) ((mouseY - cpY) / cpHeight);

                    this.property.setValue(ColourUtil.overwriteAlphaComponent(Color.HSBtoRGB(hue, hsb[1], hsb[2]), colour >> 24 & 0xFF));

                    this.hueBarDragging = mouseX > hueBarX && mouseY > cpY && mouseX < hueBarX + hueSliderWidth && mouseY < cpY + cpHeight;
                }

                // Outline
                DrawUtil.glDrawFilledQuad(cpX + cpWidth + 4 - 0.5, cpY - 0.5, hueSliderWidth + 1, cpHeight + 1, 0xFF000000);
                // Bar
                drawHueSlider(cpX + cpWidth + 4, cpY, hueSliderWidth, cpHeight);
                // Indicator
                DrawUtil.glDrawOutlinedQuad(cpX + cpWidth + 4, cpY + hsb[0] * cpHeight - 1, hueSliderWidth + 0.5, 1, 0.5f, 0xFF << 24);
            }

            if (needScissor) {
                // End scissor
                DrawUtil.glRestoreScissor();
            }
        }
    }

    private static void drawCheckeredBackground(final double x,
                                                final double y,
                                                final double sqWidth,
                                                final int sqsWide,
                                                final int sqsHigh) {
        DrawUtil.glDrawFilledQuad(x, y, sqWidth * sqsWide,
                sqWidth * sqsHigh, 0xFFFFFFFF);

        // Enable blending
        final boolean restore = DrawUtil.glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Translate matrix to top-left of rect
        glTranslated(x, y, 0);
        // Set color
        DrawUtil.glColour(0xFFBFBFBF);

        // Begin rect
        glBegin(GL_QUADS);
        {
            double sqX = 0;
            double sqY = 0;

            for (int j = 0; j < sqsHigh; j++) {
                for (int i = 0; i < sqsWide / 2; i++) {
                    glVertex2d(sqX, sqY);
                    glVertex2d(sqX, sqY + sqWidth);
                    glVertex2d(sqX + sqWidth, sqY + sqWidth);
                    glVertex2d(sqX + sqWidth, sqY);

                    sqX += sqWidth * 2.0;
                }

                sqX = j % 2 == 0 ? sqWidth : 0;
                sqY += sqWidth;
            }
        }
        // Draw the rect
        glEnd();

        // Translate matrix back (instead of creating a new matrix with glPush/glPop)
        glTranslated(-x, -y, 0);
        // Disable blending
        DrawUtil.glRestoreBlend(restore);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    private static void drawColourPicker(final double x,
                                         final double y,
                                         final double width,
                                         final double height,
                                         final float hue) {
        DrawUtil.glDrawSidewaysGradientRect(x, y, width, height, 0xFFFFFFFF, Color.HSBtoRGB(hue, 1.0F, 1.0F));
        DrawUtil.glDrawFilledQuad(x, y, width, height, 0, 0xFF000000);
    }

    private static void drawHueSlider(final double x,
                                      final double y,
                                      final double width,
                                      final double height) {
        // Enable blending
        final boolean restore = DrawUtil.glEnableBlend();
        // Disable texture drawing
        glDisable(GL_TEXTURE_2D);
        // Translate matrix to top-left of rect
        glTranslated(x, y, 0);
        // Enable colour blending
        glShadeModel(GL_SMOOTH);

        final int[] colours = {
                0xFFFF0000, // red (255, 0, 0)
                0xFFFFFF00, // yellow (255, 255, 0)
                0xFF00FF00, // green (0, 255, 0)
                0xFF00FFFF, // aqua (0, 255, 255)
                0xFF0000FF, // blue (0, 0, 255)
                0xFFFF00FF, // purple (255, 0, 255)
                0xFFFF0000, // red (255, 0, 0)
        };

        final double segment = height / colours.length;

        // Begin rect
        glBegin(GL_QUADS);
        {
            for (int i = 0; i < colours.length; i++) {
                final int colour = colours[i];

                final int top = i != 0 ? ColourUtil.fadeBetween(colours[i - 1], colour, 0.5) : colour;
                final int bottom = i + 1 < colours.length ? ColourUtil.fadeBetween(colour, colours[i + 1], 0.5) : colour;

                final double start = segment * i;

                DrawUtil.glColour(top);
                glVertex2d(0, start);

                DrawUtil.glColour(bottom);
                glVertex2d(0, start + segment);

                glVertex2d(width, start + segment);

                DrawUtil.glColour(top);
                glVertex2d(width, start);
            }
        }
        // Draw the rect
        glEnd();

        // Disable colour blending
        glShadeModel(GL_FLAT);
        // Translate matrix back (instead of creating a new matrix with glPush/glPop)
        glTranslated(-x, -y, 0);
        // Disable blending
        DrawUtil.glRestoreBlend(restore);
        // Re-enable texture drawing
        glEnable(GL_TEXTURE_2D);
    }

    @Override
    public void onMouseRelease(int button) {
        this.colorSelectorDragging = false;
        this.alphaBarDragging = false;
        this.hueBarDragging = false;
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        switch (button) {
            case 0:
                final boolean expanded = this.isExpanded();
                final boolean expandedHovered = this.isHoveredExpand(mouseX, mouseY);

                final double ex = this.getExpandedX();
                final double ey = this.getExpandedY();
                final double expandedHeight = this.getExpandedHeight();

                final double cpX = ex + 4;
                final double cpY = ey + 4;

                final double alphaBarHeight = 12;
                final double hueSliderWidth = 10;

                final double cpWidth = this.getWidth() - 12 - hueSliderWidth;
                final double cpHeight = expandedHeight - 12 - alphaBarHeight;

                final double alphaBarY = cpY + cpHeight + 4;
                final double hueBarX = cpX + cpWidth + 4;

                this.colorSelectorDragging = expanded && expandedHovered && mouseX > cpX && mouseY > cpY && mouseX < cpX + cpWidth && mouseY < cpY + cpHeight;
                this.alphaBarDragging = expanded && expandedHovered && mouseX > cpX && mouseY > alphaBarY && mouseX < cpX + cpWidth && mouseY < alphaBarY + alphaBarHeight;
                this.hueBarDragging = expanded && expandedHovered && mouseX > hueBarX && mouseY > cpY && mouseX < hueBarX + hueSliderWidth && mouseY < cpY + cpHeight;
                break;
            case 1:
                if (this.isHovered(mouseX, mouseY)) {
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
        return 80;
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

    @Override
    public boolean isVisible() {
        return this.property.check();
    }
}