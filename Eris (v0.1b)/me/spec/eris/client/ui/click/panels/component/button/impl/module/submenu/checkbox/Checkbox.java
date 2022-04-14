package me.spec.eris.client.ui.click.panels.component.button.impl.module.submenu.checkbox;

import java.awt.Color;

import me.spec.eris.Eris;
import me.spec.eris.api.value.types.BooleanValue;
import me.spec.eris.client.ui.click.ClickGui;
import me.spec.eris.client.ui.click.panels.component.Component;
import me.spec.eris.client.ui.click.panels.component.button.impl.module.ModuleButton;
import me.spec.eris.utils.visual.RenderUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;

public class Checkbox extends Component {

    private int x;
    private int y;
    private int height;
    private boolean hovered;

    private BooleanValue set;

    public Checkbox(BooleanValue s, ModuleButton b) {
        super(s, b);
        this.set = s;
    }

    @Override
    public int drawScreen(int mouseX, int mouseY, int x, int y) {
        this.hovered = this.isHovered(mouseX, mouseY);
        this.height = 15;
        this.x = x;
        this.y = y;

        Gui.drawRect(this.x, this.y, this.x + this.parent.getWidth(), this.y + this.height, ClickGui.getSecondaryColor(true).getRGB());
        String name = (set.getValueSplitter() ? EnumChatFormatting.BOLD + this.set.getValueName() : this.set.getValueName());
        ClickGui.getFont().drawString(name, (this.x + 16), (y + (ClickGui.getFont().getHeight(name) / 2) + 0.5F), new Color(175, 175, 175).getRGB());
        RenderUtilities.drawRoundedRect(this.x + 3, this.y + 5, this.x + 14, this.y + 14, new Color(0, 0, 0, 0).getRGB(), (boolean) this.set.getValue() ? new Color(29, 29, 29, 191).getRGB() : new Color(29, 29, 29, 190).getRGB());
        GlStateManager.color(1, 1, 1);
        float x1 = (boolean) this.set.getValue() ? 5 : 1.5F;
        float x2 = (boolean) this.set.getValue() ? 3 : 0F;

        float x1Diff = x1 - this.lastX1;
        float x2Diff = x2 - this.lastX2;
        this.lastX1 += x1Diff / 4;
        this.lastX2 += x2Diff / 4;
        //        RenderUtilities.drawRoundedRect(this.x + lastX1 + 2, this.y + 6, this.x + 10 + lastX2, this.y + 13, new Color((boolean) this.set.getValue() ? 0 : 255, (boolean) this.set.getValue() ? 255 : 0, 0, 180).getRGB(), new Color(0, 0, 0, 190).getRGB());
        RenderUtilities.drawRoundedRect(this.x + lastX1 + 2, this.y + 6, this.x + 10 + lastX2, this.y + 13, new Color((boolean) this.set.getValue() ? 0 : 255, (boolean) this.set.getValue() ? 255 : 0, 0, 180).getRGB(), (boolean)this.set.getValue() ? new Color(20, 190, 68).getRGB() : new Color(227, 0, 0).getRGB());

        if (hovered && set.getDescription().length() > 1) {
            new ScaledResolution(Minecraft.getMinecraft());
            ClickGui.toolTip = set.getDescription();
            float renderPosX = (this.x + ClickGui.getFont().getStringWidth(name) * 1.45f);
            float renderPosY = (y + (ClickGui.getFont().getHeight(set.getDescription()) / 2) + 0.5F) * .99f;
        //    RenderUtilities.drawRectangle(renderPosX, renderPosY, (double)ClickGui.getFont().getStringWidth(set.getDescription()) + .35, (double)ClickGui.getFont().getHeight(name), new Color(0,0,0).getRGB());
           RenderUtilities.drawRoundedRect(renderPosX - 1, renderPosY - 1, (renderPosX + 1) + ClickGui.getFont().getStringWidth(set.getDescription()), (renderPosY + 1) + ClickGui.getFont().getHeight(set.getDescription()), new Color(0, 0, 0, 181).getRGB(), new Color(0,0,0, 180).getRGB());
            ClickGui.getFont().drawString(set.getDescription(), (this.x + ClickGui.getFont().getStringWidth(name) * 1.45f), (y + (ClickGui.getFont().getHeight(name) / 2) + 0.5F) * .99f, new Color(175, 175, 175).getRGB());
        } else {
            ClickGui.toolTip = "no information to display";
        }

        return this.height;
    }

    private float lastX1 = 1.5F;
    private float lastX2 = -0.5F;

    @Override
    public void mouseClicked(int x, int y, int button) {
        if (button == 0 && this.hovered) {
            this.set.setValueObject(!((boolean) this.set.getValue()));
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + this.parent.getWidth() && mouseY > y && mouseY < y + height;
    }
}