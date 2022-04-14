package me.spec.eris.client.ui.click.panels;

import java.util.ArrayList;

import me.spec.eris.api.config.ClientConfig;
import me.spec.eris.api.module.ModuleCategory;
import me.spec.eris.client.ui.click.panels.component.button.impl.config.ConfigButton;
import me.spec.eris.client.ui.click.panels.component.button.impl.module.ModuleButton;
import me.spec.eris.client.ui.click.panels.component.button.Button;
import me.spec.eris.utils.visual.RenderUtilities;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import me.spec.eris.Eris;
import me.spec.eris.api.module.Module;
import me.spec.eris.client.ui.click.ClickGui;
import me.spec.eris.utils.world.TimerUtils;

public class Panel {
    private TimerUtils upTimer;
    private TimerUtils downTimer;
    private int x;
    private int y;
    private int width = 115;
    private int height = 15;
    private int animation = 0;
    public double lastClickedMs = 0.0;
    private ModuleCategory moduleCategory;
    private boolean open;
    public boolean onTop;
    private boolean dragging;
    private ArrayList<Button> buttons = new ArrayList<Button>();
    private int xOffset;
    private int yOffset;

    public Panel(int x, int y, ModuleCategory cat) {
        this.x = x;
        this.y = y;
        moduleCategory = cat;

        for (Module m : Eris.INSTANCE.moduleManager.getModulesInCategory(moduleCategory)) {
            buttons.add(new ModuleButton(m));
        }
        if(moduleCategory.equals(ModuleCategory.CONFIGS)) {
            for(ClientConfig clientConfig : Eris.getInstance().getConfigManager().getManagerArraylist()) {
                buttons.add(new ConfigButton(clientConfig));
            }
        }
        upTimer = new TimerUtils();
        downTimer = new TimerUtils();
    }

    public void reload() {
        buttons.clear();


        for (Module m : Eris.INSTANCE.moduleManager.getModulesInCategory(moduleCategory)) {
            buttons.add(new ModuleButton(m));
        }
        if(moduleCategory.equals(ModuleCategory.CONFIGS)) {
            for(ClientConfig clientConfig : Eris.getInstance().getConfigManager().getManagerArraylist()) {
                buttons.add(new ConfigButton(clientConfig));
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY) {
        if (dragging) {
            x = mouseX - (width / 2) + xOffset;
            y = mouseY - (height / 2) + yOffset;
        }
        GL11.glPushMatrix();
        RenderUtilities.drawRoundedRect(x - 1, y, x + width + 1, y + height, ClickGui.getPrimaryColor().getRGB(), ClickGui.getPrimaryColor().getRGB());
        ClickGui.getFont().drawString(moduleCategory.getName(), x + 1, y + (height / 2) - (ClickGui.getFont().getHeight(moduleCategory.getName()) / 2), -1);
        GL11.glPopMatrix();
        width = 115;
        height = 15;
        int offset = height;
        if (open) {
            if (animation > 0) {
                if (downTimer.hasReached(35)) {
                    animation--;
                    downTimer.reset();
                }
            }

            for (int i = 0; i < (buttons.size() - animation); i++) {
                offset += buttons.get(i).drawScreen(mouseX, mouseY, x, y + offset, width, open);
            }
        } else {
            if (animation < 0) {

                if (upTimer.hasReached(25)) {
                    animation++;
                    upTimer.reset();
                }
                for (int i = 0; i < Math.abs(animation); i++) {
                    if (i < buttons.size()) {
                        if (buttons.get(i).opened) {
                            animation = -buttons.get(i).settings.size();
                            buttons.get(i).opened = false;
                        }
                        offset += buttons.get(i).drawScreen(mouseX, mouseY, x, y + offset, width, open);
                    }
                }
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        for (Button b : buttons) {
            b.keyTyped(typedChar, keyCode);
        }
    }

    public void mouseClicked(int x, int y, int button) {
        if (isHovered(x, y)) {
            if (button == 1) {
                open = !open;
                if (open) {
                    animation = buttons.size();
                } else {
                    animation = -buttons.size();
                }
            } else if (button == 0 && !ClickGui.dragging) {
                dragging = true;
                ClickGui.dragging = true;
                int xPos = this.x + (width / 2);
                int yPos = this.y + (height / 2);
                this.xOffset = xPos - x;
                this.yOffset = yPos - y;
            }
        } else {
            for (int i = 0; i < buttons.size(); i++) {
                buttons.get(i).mouseClicked(x, y, button);
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (dragging && state == 0) {
            dragging = false;
            ClickGui.dragging = false;
        }

        for (Button b : buttons) {
            b.mouseReleased(mouseX, mouseY, state);
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height);
    }
}
