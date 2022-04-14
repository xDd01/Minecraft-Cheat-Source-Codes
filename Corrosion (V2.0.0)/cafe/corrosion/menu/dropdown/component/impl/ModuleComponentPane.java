/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.menu.dropdown.component.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.dropdown.component.GuiComponentPane;
import cafe.corrosion.menu.dropdown.component.impl.sub.PropertyComponentPane;
import cafe.corrosion.menu.dropdown.component.impl.sub.impl.BooleanPropertyComponentPane;
import cafe.corrosion.menu.dropdown.component.impl.sub.impl.ColorPropertyComponentPane;
import cafe.corrosion.menu.dropdown.component.impl.sub.impl.EnumPropertyComponentPane;
import cafe.corrosion.menu.dropdown.component.impl.sub.impl.NumberPropertyComponentPane;
import cafe.corrosion.menu.keybind.KeyBindMenu;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.Property;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.ColorProperty;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.font.type.FontType;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class ModuleComponentPane
extends GuiComponentPane {
    private static final int GUI_COLOR = new Color(20, 20, 20).getRGB();
    private static final int SELECTED_COLOR = new Color(135, 26, 26).getRGB();
    private static final int EXPAND_X = 110;
    private static final int EXPAND_Y = 20;
    private static final TTFFontRenderer ROBOTO = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 19.0f);
    private final List<PropertyComponentPane<?>> properties = new ArrayList();
    private final Module module;
    private final ModuleAttributes moduleAttributes;
    private boolean expanded;

    public ModuleComponentPane(Module module) {
        super(0, 0, 110, 20);
        this.module = module;
        this.moduleAttributes = module.getAttributes();
        this.properties.addAll(Corrosion.INSTANCE.getPropertyRegistry().getProperties(module).stream().map(property -> {
            if (property instanceof BooleanProperty) {
                return new BooleanPropertyComponentPane((BooleanProperty)property);
            }
            if (property instanceof EnumProperty) {
                return new EnumPropertyComponentPane((EnumProperty)property);
            }
            if (property instanceof NumberProperty) {
                return new NumberPropertyComponentPane((NumberProperty)property);
            }
            if (property instanceof ColorProperty) {
                return new ColorPropertyComponentPane((ColorProperty)property);
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    @Override
    public void draw(int mouseX, int mouseY) {
        Gui.drawRect(this.posX, this.posY, this.posX + this.expandX, this.posY + this.expandY, GUI_COLOR);
        this.drawCenteredString(ROBOTO, this.moduleAttributes.name(), this.module.isEnabled() ? SELECTED_COLOR : Color.WHITE.getRGB(), 0);
        if (this.properties.stream().anyMatch(property -> !((Property)property.getProperty()).getHidden().getAsBoolean())) {
            String text = this.expanded ? "<" : ">";
            float height = ROBOTO.getHeight(text) / 2.0f;
            ROBOTO.drawString(text, this.posX + 3, (float)this.posY + (float)this.expandY / 2.0f - height, Color.WHITE.getRGB());
        }
        if (this.expanded) {
            int posY = this.posY + this.expandY;
            for (PropertyComponentPane<?> component : this.properties) {
                if (((Property)component.getProperty()).getHidden().getAsBoolean()) continue;
                component.reposition(this.posX, posY);
                component.draw(mouseX, mouseY);
                posY += component.getAdditionalHeight();
            }
        }
    }

    @Override
    public void onClickReleased(int mouseX, int mouseY, int mouseButton) {
        if (this.expanded) {
            this.properties.forEach(property -> property.onClickReleased(mouseX, mouseY, mouseButton));
        }
        if (!this.mouseHovered(mouseX, mouseY)) {
            return;
        }
        switch (mouseButton) {
            case 0: {
                this.module.toggle();
                break;
            }
            case 1: {
                this.expanded = !this.expanded;
                break;
            }
            case 2: {
                Minecraft.getMinecraft().displayGuiScreen(new KeyBindMenu(this.module, Minecraft.getMinecraft().currentScreen));
            }
        }
    }

    public void reposition(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    @Override
    public void onClickBegin(int mouseX, int mouseY, int mouseButton) {
        if (this.expanded) {
            this.properties.forEach(property -> property.onClickBegin(mouseX, mouseY, mouseButton));
        }
    }

    public int getAdditionalHeight() {
        return this.expanded ? this.properties.stream().filter(property -> !((Property)property.getProperty()).getHidden().getAsBoolean()).mapToInt(PropertyComponentPane::getAdditionalHeight).sum() : 0;
    }

    public List<PropertyComponentPane<?>> getProperties() {
        return this.properties;
    }

    public Module getModule() {
        return this.module;
    }

    public ModuleAttributes getModuleAttributes() {
        return this.moduleAttributes;
    }

    public boolean isExpanded() {
        return this.expanded;
    }
}

