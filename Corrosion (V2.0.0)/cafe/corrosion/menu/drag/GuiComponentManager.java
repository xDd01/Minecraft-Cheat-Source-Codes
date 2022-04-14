/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.drag;

import cafe.corrosion.Corrosion;
import cafe.corrosion.component.draggable.IDraggable;
import cafe.corrosion.event.handler.IHandler;
import cafe.corrosion.event.impl.Event2DRender;
import cafe.corrosion.menu.config.ConfigMenu;
import cafe.corrosion.menu.drag.data.HudComponentProxy;
import cafe.corrosion.menu.drag.screen.GuiDragScreen;
import cafe.corrosion.menu.dropdown.SimpleClickGUIScreen;
import cafe.corrosion.module.Module;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;

public class GuiComponentManager
implements IHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private final Map<Module, HudComponentProxy> proxiedComponents = new HashMap<Module, HudComponentProxy>();
    private final GuiDragScreen guiDragScreen = new GuiDragScreen(this);

    public void initialize() {
        Corrosion.INSTANCE.getEventBus().register(this, Event2DRender.class, event -> {
            ScaledResolution scaledResolution = new ScaledResolution(mc);
            this.proxiedComponents.forEach((module, component) -> {
                if (!module.isEnabled()) {
                    return;
                }
                ((IDraggable)((Object)module)).render((HudComponentProxy)component, scaledResolution, component.getX(), component.getY(), component.getExpandX(), component.getExpandY());
            });
        });
    }

    public void register(Module module, int posX, int posY, int expandX, int expandY) {
        if (!(module instanceof IDraggable)) {
            throw new IllegalArgumentException("Provided module is not draggable!");
        }
        this.proxiedComponents.put(module, new HudComponentProxy(module, posX, posY, expandX, expandY));
    }

    public void displayDragScreen() {
        mc.displayGuiScreen(this.guiDragScreen);
    }

    @Override
    public boolean isEnabled() {
        return (GuiComponentManager.mc.currentScreen == null || GuiComponentManager.mc.currentScreen instanceof SimpleClickGUIScreen || GuiComponentManager.mc.currentScreen instanceof GuiChat || GuiComponentManager.mc.currentScreen instanceof ConfigMenu) && !GuiComponentManager.mc.gameSettings.showDebugInfo;
    }

    public Map<Module, HudComponentProxy> getProxiedComponents() {
        return this.proxiedComponents;
    }

    public GuiDragScreen getGuiDragScreen() {
        return this.guiDragScreen;
    }
}

