package me.spec.eris.client.managers;

import me.spec.eris.api.manager.Manager;
import me.spec.eris.client.ui.hud.element.Element;

import java.io.IOException;

public class CustomHUDManager extends Manager<Element> {



    @Override
    public void loadManager() {
    }

    public void drawScreenForPanels(int mouseX, int mouseY) {
        getManagerArraylist().forEach(panel -> panel.drawScreen(mouseX, mouseY));
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        getManagerArraylist().forEach(panel -> {
            try {
                panel.mouseClicked(mouseX,mouseY, mouseButton);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        getManagerArraylist().forEach(panel -> panel.mouseReleased(mouseX, mouseY, state));
    }

    public void keyTyped(char typedChar, int keyCode) {
        getManagerArraylist().forEach(panel -> {
            try {
                panel.keyTyped(typedChar, keyCode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
