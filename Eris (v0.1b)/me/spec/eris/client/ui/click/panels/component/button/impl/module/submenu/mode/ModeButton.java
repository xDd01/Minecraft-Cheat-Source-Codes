package me.spec.eris.client.ui.click.panels.component.button.impl.module.submenu.mode;

import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.client.ui.click.ClickGui;
import me.spec.eris.client.ui.click.panels.component.Component;
import me.spec.eris.client.ui.click.panels.component.button.impl.module.ModuleButton;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.EnumChatFormatting;

public class ModeButton extends Component {

    private int x;
    private int y;
    private int height;
    private boolean hovered;

    private ModeValue set;
    private int lastIndex = 0;

    public ModeButton(ModeValue<?> s, ModuleButton b) {
        super(s, b);
        this.set = s;

    }

    @Override
    public int drawScreen(int mouseX, int mouseY, int x, int y) {
        this.hovered = this.isHovered(mouseX, mouseY);
        this.height = 15;
        this.x = x;
        this.y = y;

        Gui.drawRect(this.x, this.y, this.x + this.parent.width, this.y + this.height, ClickGui.getSecondaryColor(true).getRGB());
        String name = this.set.getValueName() + EnumChatFormatting.GRAY + ": " + EnumChatFormatting.GRAY + String.valueOf(set.getValue()).substring(0, 1).substring(0, 1).toUpperCase() + String.valueOf(set.getValue()).substring(1).toLowerCase();
        ClickGui.getFont().drawString(name, (this.x + 2), (y + (ClickGui.getFont().getHeight(name) / 2) - 1), ClickGui.getPrimaryColor().getRGB());
        return this.height;
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        if ((button == 0 || button == 1) && this.hovered) {
            if (button == 0) {
                lastIndex++;
            } else if (button == 1) {
                lastIndex--;
            }
            if (lastIndex >= this.set.getModes().length) {
                lastIndex = 0;
            } else if (lastIndex < 0) {
                lastIndex = this.set.getModes().length - 1;
            }
            this.set.setValueObject(this.set.getModes()[lastIndex]);
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + this.parent.width && mouseY >= y && mouseY <= y + height;
    }
}