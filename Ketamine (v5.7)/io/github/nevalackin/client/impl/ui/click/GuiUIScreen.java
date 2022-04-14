package io.github.nevalackin.client.impl.ui.click;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.api.notification.NotificationType;
import io.github.nevalackin.client.impl.config.Config;
import io.github.nevalackin.client.impl.core.KetamineClient;
import io.github.nevalackin.client.impl.module.ModuleManagerImpl;
import io.github.nevalackin.client.impl.property.BooleanProperty;
import io.github.nevalackin.client.impl.property.ColourProperty;
import io.github.nevalackin.client.impl.property.EnumProperty;
import io.github.nevalackin.client.impl.ui.click.components.ModuleComponent;
import io.github.nevalackin.client.impl.ui.click.components.PanelComponent;
import io.github.nevalackin.client.impl.ui.click.components.sub.*;
import io.github.nevalackin.client.impl.ui.hud.components.HudComponent;
import io.github.nevalackin.client.util.render.BloomUtil;
import io.github.nevalackin.client.util.render.BlurUtil;
import io.github.nevalackin.client.util.render.ColourUtil;
import io.github.nevalackin.client.util.render.DrawUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;

public final class GuiUIScreen extends GuiScreen {

    private final PanelComponent configsPanel;

    public Collection<Config> configCache;
    private Config selectedConfig;

    private final Collection<HudComponent> hudComponents;

    private final List<PanelComponent> panels = new ArrayList<>();

    private PanelComponent selectedPanel;

    private double prevX, prevY;

    public GuiUIScreen() {
        final int margin = 1;
        final int height = 18;
        final int width = 120;

        this.allowUserInput = true;

        int xPos = margin + 200;

        this.configCache = KetamineClient.getInstance().getConfigManager().getConfigs();

        for (final Category category : Category.values()) {
            final PanelComponent panel = new PanelComponent(category.toString(), category.getCategoryName(), category.getColour(), xPos, margin, width, height, true) {
                @Override
                public void initComponents() {
                    final List<Module> modules = KetamineClient.getInstance().getModuleManager().getModules().stream()
                            .filter(module -> module.getCategory() == category)
                            .collect(Collectors.toList());

                    this.setChildColourFunc(child -> this.getTabColour());

                    this.setTextOffsetFunc(child -> -25);


                    final Iterator<Module> iterator = modules.iterator();

                    ModuleComponent previousComponent = null;
                    while (iterator.hasNext()) {
                        final Module module = iterator.next();

                        ModuleComponent moduleComponent = new ModuleComponent(this, module, 0.0, 0.0,
                                this.getExpandedWidth(), 18.0, !iterator.hasNext(), previousComponent);
                        this.addChild(moduleComponent);

                        previousComponent = moduleComponent;
                    }
                }
            };
            this.panels.add(panel);
            xPos += panel.getWidth() + margin;
        }

        final PanelComponent settingsPanel = new PanelComponent("Settings", "Settings", 0x50808080, xPos, margin, width, height, false) {
            @Override
            public void initComponents() {
                this.setChildColourFunc(child -> this.getTabColour());
                this.setTextOffsetFunc(child -> -25);

                final EnumProperty<UIColour> uiColourProperty = new EnumProperty<>("UI Colour", UIColour.TAB);
                uiColourProperty.addChangeListener(((now) -> {
                    switch (now) {
                        case TAB:
                            panels.forEach(panel -> panel.setChildColourFunc(child -> ColourUtil.overwriteAlphaComponent(panel.getTabColour(), 0x50)));
                            break;
                        case STATIC:
                            panels.forEach(panel -> panel.setChildColourFunc(child -> ColourUtil.getClientColour()));
                            break;
                        case ASTOLFO:
                            panels.forEach(panel -> panel.setChildColourFunc(child -> ColourUtil.blendOpacityRainbowColours((long) child.getY() * 5, 0x50)));
                            break;
                        case BLEND:
                            panels.forEach(panel -> panel.setChildColourFunc(child -> ColourUtil.overwriteAlphaComponent(ColourUtil.fadeBetween(ColourUtil.getClientColour(), ColourUtil.getSecondaryColour(), (long) child.getY() * 5), 0x50)));
                    }
                }));
                this.addChild(new DropDownComponent(this, uiColourProperty, 0.0, 0.0, this.getExpandedWidth(), 16.0, false));


                final BooleanProperty disableBlurProperty = new BooleanProperty("Disable All Blur", false);
                disableBlurProperty.addChangeListener(now -> BlurUtil.disableBlur = now);
                this.addChild(new CheckBoxComponent(this, disableBlurProperty, 0.0, 0.0, this.getExpandedWidth(), 16.0));

                final BooleanProperty disableBloomProperty = new BooleanProperty("Disable All Bloom", false);
                disableBloomProperty.addChangeListener(now -> BloomUtil.disableBloom = now);
                this.addChild(new CheckBoxComponent(this, disableBloomProperty, 0.0, 0.0, this.getExpandedWidth(), 16.0));

                final ColourProperty clientColourProperty = new ColourProperty("Client Colour", ColourUtil.getClientColour());
                clientColourProperty.addChangeListener(ColourUtil::setClientColour);
                this.addChild(new ColourPickerComponent(this, clientColourProperty, 0.0, 0.0, this.getExpandedWidth(), 16.0, true));

                final ColourProperty secondaryColourProperty = new ColourProperty("Secondary Colour", ColourUtil.getSecondaryColour());
                secondaryColourProperty.addChangeListener(ColourUtil::setSecondaryColour);
                this.addChild(new ColourPickerComponent(this, secondaryColourProperty, 0.0, 0.0, this.getExpandedWidth(), 16.0, true));

                /*this.addChild(new ButtonComponent(this, "Sync Colours", 0, 0, this.getExpandedWidth(), 16) {
                    @Override
                    public void onMouseClick(int mouseX, int mouseY, int button) {
                        if (button == 0 && isHovered(mouseX, mouseY)) {
                            WatermarkModule watermark = KetamineClient.getInstance().getModuleManager().getModule(WatermarkModule.class);
                            ArraylistModule arraylist = KetamineClient.getInstance().getModuleManager().getModule(ArraylistModule.class);
                            watermark.startColourProperty.setValue(clientColourProperty.getValue());
                            watermark.endColourProperty.setValue(secondaryColourProperty.getValue());
                            arraylist.startColourProperty.setValue(clientColourProperty.getValue());
                            arraylist.endColourProperty.setValue(secondaryColourProperty.getValue());
                        }
                    }
                });*/
            }
        };
        this.panels.add(settingsPanel);

        this.configsPanel = new PanelComponent("Configs", "Configs", 0x50FFFFFF, xPos + width + margin, margin, width, height, false) {
            @Override
            public void initComponents() {
                this.setChildColourFunc(child -> this.getTabColour());
                this.setTextOffsetFunc(child -> -25);

                configCache.forEach(config ->
                        this.addChild(new ConfigComponent(this, config, 0, 0, this.getExpandedWidth(), 16)));

                for (int i = 0; i < ConfigButton.values().length; i++) {
                    ConfigButton configButton = ConfigButton.values()[i];
                    this.addChild(new ButtonComponent(this, configButton.toString(), 0, 0, this.getExpandedWidth(), 16) {
                        @Override
                        public void onMouseClick(int mouseX, int mouseY, int button) {
                            if (button == 0 && isHovered(mouseX, mouseY)) {
                                if (configButton.equals(ConfigButton.LOAD) || configButton.equals(ConfigButton.SAVE)) {
                                    assert configButton.onClicked != null;
                                    configButton.onClicked.accept(selectedConfig);
                                } else {
                                    assert configButton.onClickedRunnable != null;
                                    configButton.onClickedRunnable.run();
                                }
                            }
                        }
                    });
                }


            }
        };
        this.panels.add(configsPanel);
        this.hudComponents = ModuleManagerImpl.getHudComponents();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // TODO :: ScaledResolution fix
        glAlphaFunc(GL_ALWAYS, 0.0F);

        for (HudComponent hudComponent : hudComponents) {
            /*DrawUtil.glDrawOutlinedQuad(hudComponent.getX(), hudComponent.getY(),
                    hudComponent.getWidth(), hudComponent.getHeight(), 2, -1);*/
            if (hudComponent.isDragging()) {
                hudComponent.setX(mouseX - this.prevX);
                hudComponent.setY(mouseY - this.prevY);
                return;
            }
        }

        this.panels.stream()
                .sorted(Comparator.comparing(panel -> panel == selectedPanel))
                .forEach(panel -> panel.onDraw(new ScaledResolution(this.mc), mouseX, mouseY));

        glAlphaFunc(GL_GREATER, 0.1F);

    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {

        if (mouseButton == 0) {
            for (HudComponent hudComponent : hudComponents) {
                if (hudComponent.isHovered(mouseX, mouseY)) {
                    hudComponent.setDragging(true);
                    prevX = mouseX - hudComponent.getX();
                    prevY = mouseY - hudComponent.getY();
                    return;
                }
            }
        }

        if (this.selectedPanel != null &&
                (this.selectedPanel.isHovered(mouseX, mouseY) ||
                        this.selectedPanel.isHoveredExpand(mouseX, mouseY))) {

            this.selectedPanel.onMouseClick(mouseX, mouseY, mouseButton);
            return;
        }

        for (final PanelComponent panel : this.panels) {
            if (panel.isHovered(mouseX, mouseY) || panel.isHoveredExpand(mouseX, mouseY)) {
                this.selectedPanel = panel;
                break;
            }
        }

        if (this.selectedPanel != null) {
            this.selectedPanel.onMouseClick(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        this.panels.forEach(panel -> panel.onKeyPress(keyCode));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == 0) {
            for (HudComponent hudComponent : hudComponents) {
                hudComponent.setDragging(false);
            }
        }
        this.panels.forEach(panel -> panel.onMouseRelease(state));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void updateConfigs(Collection<Config> configCollection) {
        this.configCache = configCollection;
        this.configsPanel.getChildren().clear();
        this.configsPanel.initComponents();
    }

    private enum UIColour {
        TAB("Tab"),
        STATIC("Static"),
        ASTOLFO("Astolfo"),
        BLEND("Blend");

        private final String name;

        UIColour(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private enum Position {
        LEFT("Left"),
        CENTER("Center");

        private final String name;

        Position(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    private enum ConfigButton {
        SAVE("Save", KetamineClient.getInstance().getConfigManager()::save),
        LOAD("Load", config -> {
            KetamineClient.getInstance().getConfigManager().load(config);
            KetamineClient.getInstance().getNotificationManager().add(NotificationType.SUCCESS, "Config", "Successfully Loaded Config: " + config.getName(), 1500);
        }),
        REFRESH("Refresh", KetamineClient.getInstance().getConfigManager()::refresh);

        private final String name;
        private final Consumer<Config> onClicked;
        private final Runnable onClickedRunnable;

        ConfigButton(String name, Consumer<Config> configConsumer) {
            this.name = name;
            this.onClicked = configConsumer;
            this.onClickedRunnable = null;
        }

        ConfigButton(String name, Runnable onClickedRunnable) {
            this.name = name;
            this.onClicked = null;
            this.onClickedRunnable = onClickedRunnable;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public void setSelectedConfig(Config selectedConfig) {
        this.selectedConfig = selectedConfig;
    }

    public Config getSelectedConfig() {
        return selectedConfig;
    }
}
