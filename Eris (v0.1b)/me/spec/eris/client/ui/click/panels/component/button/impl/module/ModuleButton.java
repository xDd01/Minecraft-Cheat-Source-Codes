package me.spec.eris.client.ui.click.panels.component.button.impl.module;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.util.MathHelper;
import me.spec.eris.client.ui.click.panels.component.Component;
import me.spec.eris.client.ui.click.panels.component.button.impl.module.submenu.mode.ModeButton;
import me.spec.eris.client.ui.click.panels.component.button.impl.module.submenu.slider.Slider;
import me.spec.eris.client.ui.click.panels.component.button.Button;
import me.spec.eris.client.ui.click.panels.component.button.impl.module.submenu.checkbox.Checkbox;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import me.spec.eris.api.module.Module;
import me.spec.eris.api.value.Value;
import me.spec.eris.api.value.types.BooleanValue;
import me.spec.eris.api.value.types.ModeValue;
import me.spec.eris.api.value.types.NumberValue;
import me.spec.eris.client.ui.click.ClickGui;
import me.spec.eris.utils.misc.Helper;
import me.spec.eris.utils.world.TimerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class ModuleButton extends Button {

    private Module host;

    public ModuleButton(Module mod) {
        this.host = mod;

        for (Value<?> s : mod.getSettings()) {
            if (s instanceof NumberValue) {
                this.settings.add(new Slider((NumberValue<?>) s, this));
            }
            if (s instanceof BooleanValue) {
                this.settings.add(new Checkbox((BooleanValue<?>) s, this));
            }
            if (s instanceof ModeValue) {
                this.settings.add(new ModeButton((ModeValue<?>) s, this));
            }
        }
        upTimer = new TimerUtils();
        downTimer = new TimerUtils();
    }

    public void keyTyped(char typedChar, int keyCode) {
        for (me.spec.eris.client.ui.click.panels.component.Component s : this.settings) {
            s.keyTyped(typedChar, keyCode);
        }
        if (isMiddleClick()) {
            if (!Keyboard.getKeyName(keyCode).equalsIgnoreCase("ESCAPE")) {
                Helper.sendMessage("Bound " + host.getDynamicName() + " to " + Keyboard.getKeyName(keyCode));
                host.setKey(keyCode, true);
                setMiddleClick(false);
            } else {
                Helper.sendMessage("Bound " + host.getDynamicName() + " to " + "NONE");
                host.setKey(Keyboard.KEY_NONE, true);
                setMiddleClick(false);
            }
        }
    }

    public Module getMod() {
        return this.host;
    }

    private float lastRed = (float) ClickGui.getSecondaryColor(false).getRed() / 255F;
    private float lastGreen = (float) ClickGui.getSecondaryColor(false).getGreen() / 255F;
    private float lastBlue = (float) ClickGui.getSecondaryColor(false).getBlue() / 255F;

    public int drawScreen(int mouseX, int mouseY, int x, int y, int width, boolean open) {

        ArrayList<me.spec.eris.client.ui.click.panels.component.Component> settings = getActiveComponents();
        this.clickable = open;
        this.x = x;
        this.y = y;
        this.height = 15;
        this.width = width;
        this.hovered = this.isHovered(mouseX, mouseY);

        Color correctColor = ClickGui.getSecondaryColor(false);
        if (this.hovered) {
            int dark = 8;
            correctColor = new Color(Math.max(correctColor.getRed() - dark, 0), Math.max(correctColor.getGreen() - dark, 0), Math.max(correctColor.getBlue() - dark, 0));
        }

        float speed = 256F / (float) Minecraft.getMinecraft().getDebugFPS();
        lastRed += (((float) correctColor.getRed() / 255F) - lastRed) / speed;
        lastGreen += (((float) correctColor.getGreen() / 255F) - lastGreen) / speed;
        lastBlue += (((float) correctColor.getBlue() / 255F) - lastBlue) / speed;

        lastRed = Math.max(0, Math.min(1, lastRed));
        lastGreen = Math.max(0, Math.min(1, lastGreen));
        lastBlue = Math.max(0, Math.min(1, lastBlue));

        Gui.drawRect(x, y, x + width, y + height, new Color(lastRed, lastGreen, lastBlue, (float) ClickGui.getSecondaryColor(false).getAlpha() / 255F).getRGB());
        ;
        ClickGui.getFont().drawString(this.host.getDynamicName() + getKey(), this.x + 1, this.y + (this.height / 2) - (ClickGui.getFont().getHeight(this.host.getName() + ": " + getKey()) / 2), this.host.isToggled() ? ClickGui.getPrimaryColor().getRGB() : new Color(175, 175, 175).getRGB());
        int addVal = 0;
        if (this.opened && !settings.isEmpty()) {
            addVal = this.height;
            if (this.animation > 0) {
                if (this.downTimer.hasReached(30)) {
                    this.animation--;
                    this.downTimer.reset();
                }
            }
            for (int i = 0; i < settings.size() - animation; i++) {
                addVal += settings.get(i).drawScreen(mouseX, mouseY, x, y + addVal);
            }
            addVal -= height;

        } else {
            if (this.animation < 0) {
                addVal = this.height;
                if (this.upTimer.hasReached(25)) {
                    this.animation++;
                    this.upTimer.reset();
                }
                for (int i = 0; i < Math.abs(animation); i++) {
                    addVal += settings.get(i).drawScreen(mouseX, mouseY, x, y + addVal);
                }
                addVal -= height;
            }
        }
        if (!settings.isEmpty()) {
            GL11.glPushMatrix();
            float f =  MathHelper.abs(MathHelper.sin((float) (Minecraft.getSystemTime() % 1000L / 1000.0f * 0)) * 0.1f);
            GL11.glScalef(f + 1.0f, f + 1.0f, f + 1.0f);
            GL11.glScalef(1, 1, 1);
            ClickGui.getFont().drawString(">", x + width - 10, this.y + (this.height / 2) - (ClickGui.getFont().getHeight("+") / 2), new Color(175, 175, 175).getRGB());
            GL11.glPopMatrix();
        }

        return this.height + addVal;
    }

    public void mouseClicked(int x, int y, int button) {
        if (!clickable) return;
        this.hovered = this.isHovered(x, y);
        ArrayList<me.spec.eris.client.ui.click.panels.component.Component> settings = getActiveComponents();
        if (this.hovered && button == 0) {
            this.host.toggle(true);
        } else if (this.hovered && button == 1) {
            opened = !opened;
            if (opened) {
                lastInteract = System.currentTimeMillis();
                animation = settings.size();
            } else {
                animation = -settings.size();
            }
        } else if (hovered && button == 2) {
            if (opened) {
                opened = !opened;
                animation = -settings.size();
            }
            setMiddleClick(!isMiddleClick());
            if (!this.isMiddleClick()) {
                Helper.sendMessage("Bound " + host.getName() + " to " + "NONE");
                host.setKey(Keyboard.KEY_NONE, false);
                setMiddleClick(false);
            }

        } else if (this.opened) {
            for (me.spec.eris.client.ui.click.panels.component.Component sc : settings) {
                sc.mouseClicked(x, y, button);
            }
        }
    }

    public String getKey() {
        if (isMiddleClick()) {
            return " [" + Keyboard.getKeyName(host.getKey()) + "]";
        } else {
            return "";
        }
    }

    private void setMiddleClick(boolean b) {
        isMiddleClick = b;
    }

    private boolean isMiddleClick() {
        return isMiddleClick;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        ArrayList<me.spec.eris.client.ui.click.panels.component.Component> settings = getActiveComponents();
        if (!clickable) return;
        if (this.opened) {
            for (me.spec.eris.client.ui.click.panels.component.Component sc : settings) {
                sc.mouseReleased(mouseX, mouseY, state);
            }
        }
    }

    private boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY > y && mouseY < y + height;
    }

    public int getWidth() {
        return this.width;
    }

    public ArrayList<me.spec.eris.client.ui.click.panels.component.Component> getActiveComponents() {
        ArrayList<me.spec.eris.client.ui.click.panels.component.Component> activeComponents = new ArrayList<>();
        for (int i = this.settings.size() - 1; i > -1; i--) {
            Component component = settings.get(i);
            if (component.getSetting().checkDependants()) {
                activeComponents.add(component);
            }
        }
        Collections.reverse(activeComponents);
        return activeComponents;
    }
}
