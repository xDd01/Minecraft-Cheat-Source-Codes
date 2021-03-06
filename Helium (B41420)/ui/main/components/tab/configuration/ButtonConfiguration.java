package rip.helium.ui.main.components.tab.configuration;

import java.awt.*;

import rip.helium.configuration.Configuration;
import rip.helium.ui.main.Interface;
import rip.helium.ui.main.components.base.BaseButton;
import rip.helium.utils.Dependency;
import rip.helium.utils.Draw;
import rip.helium.utils.font.Fonts;

/**
 * @author antja03
 */
public class ButtonConfiguration extends BaseButton {

    public Configuration config;
    private Dependency selectedDep;

    public ButtonConfiguration(Interface theInterface, Configuration config, double x, double y, double width, double height, Action action, Dependency selectedDep) {
        super(theInterface, x, y, width, height, action);
        this.config = config;
        this.selectedDep = selectedDep;
    }

    @Override
    public void drawComponent(double x, double y) {
        this.positionX = x - theInterface.getPositionX();
        this.positionY = y - theInterface.getPositionY();

        int red = 0, green = 215, blue = 64;

        Draw.drawRectangle(x, y, x + maxWidth, y + maxHeight,
                selectedDep.check() ? new Color(0, 190, 255, 120).getRGB() : new Color(25, 25, 25, 255).getRGB());

        if (config.isDefault())
            Fonts.bf24.drawString("*", x + 2, y + 4, new Color(255, 255, 255).getRGB());

        Fonts.f16.drawCenteredString(config.getIdentifier(), (float) (x + (maxWidth / 2)), (float) (y + 2),
                new Color(220, 220, 220, 255).getRGB());
    }
}
