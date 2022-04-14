package me.spec.eris.client.ui.main.buttons;
import me.spec.eris.Eris;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

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
    
    public ExpandButton(int id, int x, int y, int x1, int y1, String text) {
        super(id, x, y, x1, y1, text);
        this.alphaIncriment = 100;
        this.alpha = 0;
        this.size = 0;
        this.x = x;
        this.y = y;
        this.x1 = x1;
        this.y1 = y1;
        this.text = text;
    }
    
    public ExpandButton(int id, int x, int y, String text) {
        this(id, x, y, 200, 20, text);
    }
    
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        boolean isOverButton = mouseX >= x && mouseX <= x + x1 && mouseY >= y && mouseY <= y + y1;
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
        drawRect(x - size, y - size, x + x1 + size, y + y1 + size, alpha);
        Eris.getInstance().getFontRenderer().drawCenteredString(text, x + x1 / 2, y + y1 / 2 - 4, -1);
    }
}

