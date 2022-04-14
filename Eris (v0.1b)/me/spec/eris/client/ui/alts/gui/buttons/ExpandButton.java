package me.spec.eris.client.ui.alts.gui.buttons;
import me.spec.eris.Eris;
import me.spec.eris.utils.visual.RenderUtilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;
import java.util.function.Supplier;

public class ExpandButton extends GuiButton
{
    int alpha;
    int size;
    private int x;
    private int y;
    private int x1;
    private int y1;
    int alphaIncriment;
    private String text;
    private Supplier<?> supplier;
    
    public ExpandButton(int id, int x, int y, int x1, int y1, String text, Supplier<?> supplier) {
        super(id, x, y, x1, y1, text);
        this.alphaIncriment = 100;
        this.alpha = 0;
        this.size = 0;
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;
        this.text = text;
        this.supplier = supplier;
    }

    public boolean checkDependants() {
        return supplier == null ? true : (Boolean) supplier.get();
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (!checkDependants()) return;
        boolean isOverButton = mouseX >= x && mouseX <= x1 && mouseY >= y && mouseY <= y1;
        if (isOverButton && alphaIncriment <= 150) {
            alphaIncriment += 5;
            alpha = alphaIncriment << 24;
        }
        else if (!isOverButton && alphaIncriment >= 100) {
            alphaIncriment -= 5;
            alpha = alphaIncriment << 24;
        }
        if (alphaIncriment > 150) {
            alphaIncriment = 150;
        }
        else if (alphaIncriment < 100) {
            alphaIncriment = 100;
        }
        if (isOverButton && size <= 1) {
            ++size;
        }
        else if (!isOverButton && size >= 0) {
            --size;
        }
        RenderUtilities.drawRoundedRect(x - size, y - size, x1 + size, y1 + size, new Color(255,70,70, 120).getRGB(), new Color(0,0,0,120).getRGB());
        Eris.getInstance().getFontRenderer().drawCenteredString(text, x + Math.abs(x1 - x) / 2, ((y + Math.abs(y1 - y) / 2) - 5), -1);
    }
}

