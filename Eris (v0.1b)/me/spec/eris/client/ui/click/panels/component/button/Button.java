package me.spec.eris.client.ui.click.panels.component.button;


import me.spec.eris.client.ui.click.panels.component.Component;
import me.spec.eris.utils.world.TimerUtils;

import java.util.ArrayList;

public abstract class Button {

    public Object host;

    public long lastInteract;
    public int x;
    public int y;
    public int width;
    public int height;
    public boolean hovered;
    public int animation;
    public boolean opened = false;
    public boolean clickable = false;
    public boolean isMiddleClick = false;
    public TimerUtils upTimer;
    public TimerUtils downTimer;

    public ArrayList<Component> settings = new ArrayList<Component>();

    public abstract int drawScreen(int mouseX, int mouseY, int x, int y, int width, boolean open);

    public abstract void keyTyped(char typedChar, int keyCode);

    public abstract void mouseClicked(int x, int y, int button);

    public abstract void mouseReleased(int mouseX, int mouseY, int state);


    public int getWidth() {
        return width;
    }
}
