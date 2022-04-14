package io.github.nevalackin.client.impl.ui.click.components.sub;

import io.github.nevalackin.client.api.ui.framework.Component;
import io.github.nevalackin.client.api.ui.framework.Predicated;
import io.github.nevalackin.client.impl.property.BooleanProperty;
import io.github.nevalackin.client.util.render.ColourUtil;
import io.github.nevalackin.client.util.render.DrawUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;


public final class CheckBoxComponent extends Component implements Predicated {


    private final BooleanProperty property;

    private double progress;

    public CheckBoxComponent(final Component parent,
                             final BooleanProperty property,
                             final double x, final double y,
                             final double width,
                             final double height) {
        super(parent, x, y, width, height);

        this.property = property;
    }

    @Override
    public void onDraw(ScaledResolution scaledResolution, int mouseX, int mouseY) {
        final double x = this.getX();
        final double y = this.getY();
        final double w = this.getWidth();
        final double h = this.getHeight();

        // Slider button thingy
        {
            final double width = 12;
            final double left = x + w - 4 - width;

            // Handle left-right animation
            if (this.property.getValue()) {
                if (this.progress < 1.0) this.progress += 1.0 / Minecraft.getDebugFPS() * 6;
                else this.progress = 1.0;
            } else {
                if (this.progress > 0.0) this.progress -= 1.0 / Minecraft.getDebugFPS() * 6;
                else this.progress = 0.0;
            }

            final double size = 5;

            final int colour = getColour(this);

            // Background
            final int backgroundColour = ColourUtil.overwriteAlphaComponent(ColourUtil.fadeBetween(0xFF454545, colour, this.progress), 0xFF);

            DrawUtil.glDrawFilledEllipse(left + size / 2, y + h / 2, (float) size * 2 + 2, backgroundColour);
            DrawUtil.glDrawFilledEllipse(left + width - size / 2, y + h / 2, (float) size * 2 + 2, backgroundColour);
            DrawUtil.glDrawFilledQuad(left + size / 2, y + h / 2 - (size + 1) / 2, width - size, size + 1, backgroundColour);

            // Knob
            DrawUtil.glDrawFilledEllipse(left + size / 2.0 + 0.5 + this.progress * (width - size - 1), y + h / 2, (float) size * 2, 0xFF1A191B);
        }


        // Draw property name
        FONT_RENDERER.draw(this.property.getName(),
                x + 4, y + h / 2 - 4,
                this.property.getValue() ? 0xFFEBEBEB : 0xFFA5A5A5);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {
        if (this.isHovered(mouseX, mouseY) && button == 0) {
            this.property.setValue(!this.property.getValue());
        }
    }

    @Override
    public boolean isVisible() {
        return property.check();
    }
}