package me.spec.eris.client.ui.click.panels.component.button.impl.module.submenu.slider;

import java.awt.Color;
import java.text.DecimalFormat;

import me.spec.eris.api.value.types.NumberValue;
import me.spec.eris.client.ui.click.ClickGui;
import me.spec.eris.client.ui.click.panels.component.Component;
import me.spec.eris.client.ui.click.panels.component.button.impl.module.ModuleButton;
import me.spec.eris.utils.visual.RenderUtilities;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

public class Slider extends Component {

    private boolean dragging = false;
    private int x;
    private int y;
    private int height;
    private boolean hovered;

    private NumberValue set;

    public Slider(NumberValue<?> s, ModuleButton b) {
        super(s, b);
        this.set = s;
    }

    @Override
    public int drawScreen(int mouseX, int mouseY, int x, int y) {
        this.hovered = this.isHovered(mouseX, mouseY);
        this.height = 15;
        this.x = x + 5;
        this.y = y;
        double maxX = this.parent.getWidth() - 15;

        float val = 0;
        double min = 0;
        double max = 0;
        if (this.set.getValue() instanceof Double) {
            min = (double) this.set.getMinimumValue();
            max = (double) this.set.getMaximumValue();
            double val1 = (double) this.set.getValue();
            val = (float) val1;
        } else if (this.set.getValue() instanceof Integer) {
            val = (int) this.set.getValue();
            min = (int) this.set.getMinimumValue();
            max = (int) this.set.getMaximumValue();
        } else if (this.set.getValue() instanceof Float) {
            val = (float) this.set.getValue();
            min = (float) this.set.getMinimumValue();
            max = (float) this.set.getMaximumValue();
        }
        if (this.dragging) {
            float toSet = (float) ((float) mouseX - (float) this.x) / (float) maxX;
            if (toSet > 1) {
                toSet = 1;
            }
            if (toSet < 0) {
                toSet = 0;
            }
            double toSet2 = ((max - min) * toSet) + min;
            if (this.set.getValue() instanceof Double) {
                this.set.setValueObject((double) toSet2);
            } else if (this.set.getValue() instanceof Integer) {
                this.set.setValueObject((int) toSet2);
            } else if (this.set.getValue() instanceof Float) {
                this.set.setValueObject((float) toSet2);
            }

        }
        float distance = (float) ((val - min) / (max - min));
        Gui.drawRect(this.x - 5, this.y, this.x - 5 + this.parent.getWidth(), this.y + this.height, ClickGui.getSecondaryColor(true).getRGB());
        String name = this.set.getValueName() + EnumChatFormatting.GRAY + ": " + new DecimalFormat("#.##").format(this.set.getValue());
        Gui.drawRect(this.x - 2, this.y + ClickGui.getFont().getHeight(name) + 6, (int) (this.x - 2 + (maxX * distance)), this.y + this.height - 4, ClickGui.getPrimaryColor().getRGB());

        boolean flag = ((int) (this.x + (maxX * distance)) + 3) > x + maxX;
        int location =  ((int) (this.x + (maxX * distance)) + 3);

        RenderUtilities.drawRoundedRect((int) (this.x - 2 + (maxX * distance)), this.y + ClickGui.getFont().getHeight(name) + 7, location,this.y + this.height - 4, new Color(255,255,255, 200).getRGB(), new Color(255,255,255, 200).getRGB() );
        GlStateManager.pushMatrix();
        float scale = 1;
        GlStateManager.scale(scale, scale, scale);
        ClickGui.getFont().drawString(name, (this.x - 3) / scale, (y + 1) / scale, ClickGui.getPrimaryColor().getRGB());
        GlStateManager.popMatrix();
        return this.height;
    }

    @Override
    public void mouseClicked(int x, int y, int button) {
        if (button == 0 && this.hovered) {
            this.dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            this.dragging = false;
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        double xPos = x + 5;
        double maxX = x + parent.getWidth() - 15;
        double yPosMin = y + height - 3;
        double yPosMax = y + height + 3;
        return mouseX >= xPos && mouseX <= maxX && mouseY >= yPosMin && mouseY <= yPosMax;
    }
}
