package io.github.nevalackin.client.impl.ui.click.components.sub;

import io.github.nevalackin.client.api.ui.framework.Component;
import io.github.nevalackin.client.api.ui.framework.Predicated;
import io.github.nevalackin.client.impl.config.Config;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.util.render.DrawUtil;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;
import java.util.List;

public final class ConfigComponent extends Component implements Predicated {

    private final Config config;

    public ConfigComponent(final Component parent, final Config config,
                           final double x,
                           final double y,
                           final double width,
                           final double height) {
        super(parent, x, y, width, height);
        this.config = config;
    }


    @Override
    public void onDraw(ScaledResolution scaledResolution, int mouseX, int mouseY) {

        final double x = this.getX();
        final double y = this.getY();
        final double w = this.getWidth();
        final double h = this.getHeight();

        final boolean isSelected = KetamineClient.getInstance().getDropdownGUI().getSelectedConfig() == this.config;

        /*
            Thomas, you can change the color of the selected config text. It's currently 0xFFFF00FF (Magenta)
         */
        FONT_RENDERER.draw(this.config.getName().toLowerCase(),
                x + (w/2 - (FONT_RENDERER.getWidth(this.config.getName().toLowerCase())/2)), y + h / 2 - 4, isSelected ? 0xFFEBEBEB : 0xFFA5A5A5);

        /*
            Also, this ConfigComponent and ButtonComponent separator that uses my isLastConfigComponent() method
         */

        if (isLastConfigComponent())
            DrawUtil.glDrawFilledQuad(x, y + h, w, 1, 0xFF313523);

        super.onDraw(scaledResolution, mouseX, mouseY);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int button) {

        if (button == 0 && this.isHovered(mouseX, mouseY)) {
            KetamineClient.getInstance().getDropdownGUI().setSelectedConfig(this.config);
        }

        super.onMouseClick(mouseX, mouseY, button);
    }

    @Override
    public void onMouseRelease(int button) {
        super.onMouseRelease(button);
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    private boolean isLastConfigComponent() {
        final List<Component> cacheConfigComponents = new ArrayList<>(getParent().getChildren());
        cacheConfigComponents.removeIf(component -> !(component instanceof ConfigComponent));
        return cacheConfigComponents.indexOf(this) == cacheConfigComponents.size() - 1;
    }
}
