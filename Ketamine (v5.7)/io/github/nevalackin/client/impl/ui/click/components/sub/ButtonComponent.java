package io.github.nevalackin.client.impl.ui.click.components.sub;

import io.github.nevalackin.client.api.ui.framework.Component;
import net.minecraft.client.gui.ScaledResolution;

public abstract class ButtonComponent extends Component {

    private final String buttonLabel;

    public ButtonComponent(Component parent, String buttonLabel, double x, double y, double width, double height) {
        super(parent, x, y, width, height);
        this.buttonLabel = buttonLabel;
    }

    @Override
    public abstract void onMouseClick(int mouseX, int mouseY, int button);

    @Override
    public void onDraw(ScaledResolution scaledResolution, int mouseX, int mouseY) {
        final double x = this.getX();
        final double y = this.getY();
        final double w = this.getWidth();
        final double h = this.getHeight();

        FONT_RENDERER.draw(this.buttonLabel,
                x + (w/2 - (FONT_RENDERER.getWidth(this.buttonLabel)/2)), y + h / 2 - 4, isHovered(mouseX, mouseY) ? 0xFFA5A5A5 : 0xFFF5F5F5);
        super.onDraw(scaledResolution, mouseX, mouseY);
    }
}
