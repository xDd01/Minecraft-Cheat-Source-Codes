package io.github.nevalackin.client.impl.ui.click.components.sub;

import io.github.nevalackin.client.api.ui.framework.Component;
import io.github.nevalackin.client.api.ui.framework.Predicated;
import io.github.nevalackin.client.impl.property.DoubleProperty;
import io.github.nevalackin.client.util.render.ColourUtil;
import io.github.nevalackin.client.util.render.DrawUtil;
import io.github.nevalackin.client.util.math.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public final class SliderComponent extends Component implements Predicated {

    private final DoubleProperty property;

    private boolean sliding;
    private double animatedPercentage;

    public SliderComponent(final Component parent,
                           final DoubleProperty property,
                           final double x, final double y,
                           final double width, final double height) {
        super(parent, x, y, width, height);
        this.property = property;
    }

    @Override
    public void onDraw(ScaledResolution scaledResolution, int mouseX, int mouseY) {
        final double x = this.getX();
        final double y = this.getY();
        final double w = this.getWidth();
        final double h = this.getHeight();

        final double value = this.property.getValue();
        final String valueText = this.property.getDisplayString();

        final double percentage = (value - this.property.getMin()) / (this.property.getMax() - this.property.getMin());

        final double sliderOffset = x + 4;
        final double sliderWidth = w - 8;

        final int colour = ColourUtil.overwriteAlphaComponent(getColour(this), 0xFF);

        if (this.animatedPercentage < percentage) {
            final double inc = 1.0 / Minecraft.getDebugFPS() * 2;
            if (percentage - this.animatedPercentage < inc) {
                this.animatedPercentage = percentage;
            } else {
                this.animatedPercentage += inc;
            }
        } else if (this.animatedPercentage > percentage) {
            final double inc = 1.0 / Minecraft.getDebugFPS() * 2;
            if (this.animatedPercentage - percentage < inc) {
                this.animatedPercentage = percentage;
            } else {
                this.animatedPercentage -= inc;
            }
        }

        if (this.sliding) {
            this.property.setValue(Math.max(this.property.getMin(), Math.min(
                    this.property.getMax(), MathUtil.round((mouseX - x) * (this.property.getMax() - this.property.getMin()) / sliderWidth + this.property.getMin(), this.property.getInc()))));
        }

        // Draw value text
        FONT_RENDERER.draw(valueText, x + w - FONT_RENDERER.getWidth(valueText) - 4, y + 3, 0xFFA5A5A5);

        // Draw slider
        {
            // Background
            DrawUtil.glDrawFilledQuad(sliderOffset, y + h - 3 - 1, sliderWidth, 1, 0xFF282828);
            // Filled
            DrawUtil.glDrawFilledQuad(sliderOffset, y + h - 3 - 1, sliderWidth * this.animatedPercentage, 1, colour);
            // Knob
            DrawUtil.glDrawFilledEllipse(sliderOffset + sliderWidth * this.animatedPercentage, y + h - 3 - 1 + 0.5, 12, colour);
            // Highlight knob on drag
            if (this.sliding)
                DrawUtil.glDrawFilledEllipse(sliderOffset + sliderWidth * this.animatedPercentage, y + h - 3 - 1 + 0.5, 17, ColourUtil.overwriteAlphaComponent(colour, 0x50));
        }

        // Draw property name
        FONT_RENDERER.draw(this.property.getName(), sliderOffset, y + 3, 0xFFEBEBEB);
    }


    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        final double x = this.getX();
        final double y = this.getY();
        final double w = this.getWidth();
        final double h = this.getHeight();

        this.sliding = button == 0 && mouseX > x && mouseY > y + h - 6 &&
                mouseX < x + w && mouseY < y + h + 3;
    }

    @Override
    public void onMouseRelease(int button) {
        this.sliding = false;
    }

    @Override
    public boolean isVisible() {
        return this.property.check();
    }
}