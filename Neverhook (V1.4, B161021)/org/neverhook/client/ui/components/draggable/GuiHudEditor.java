package org.neverhook.client.ui.components.draggable;

import net.minecraft.client.gui.GuiScreen;
import org.neverhook.client.NeverHook;

public class GuiHudEditor extends GuiScreen {

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawWorldBackground(0);
        for (DraggableModule mod : NeverHook.instance.draggableManager.getMods()) {
            mod.render(mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
