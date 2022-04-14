package me.spec.eris.client.ui.click.panels.component;

import me.spec.eris.api.value.Value;
import me.spec.eris.client.ui.click.panels.component.button.impl.module.ModuleButton;

public class SetComp {

    protected ModuleButton parent;
    protected Value set;

    public SetComp(Value<?> s, ModuleButton parent) {
        this.set = s;
        this.parent = parent;
    }

    public int drawScreen(int mouseX, int mouseY, int x, int y) {
        return 0;
    }

    public void mouseClicked(int x, int y, int button) {
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    public Value<?> getSetting() {
        return this.set;
    }

    public void keyTyped(char typedChar, int keyCode) {

    }
}