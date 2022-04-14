/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.visual;

import cafe.corrosion.Corrosion;
import cafe.corrosion.component.draggable.IDraggable;
import cafe.corrosion.component.tabgui.TabModuleComponent;
import cafe.corrosion.event.impl.EventKeyPress;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.animation.Animation;
import cafe.corrosion.menu.animation.impl.CubicEaseAnimation;
import cafe.corrosion.menu.drag.data.HudComponentProxy;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.ColorProperty;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.nameable.INameable;
import cafe.corrosion.util.render.Blurrer;
import cafe.corrosion.util.render.ColorUtil;
import cafe.corrosion.util.render.RenderUtil;
import java.awt.Color;
import java.util.Arrays;
import java.util.function.Consumer;
import net.minecraft.client.gui.ScaledResolution;

@ModuleAttributes(name="TabGUI", description="Displays a menu similar to a ClickGUI in-game", category=Module.Category.VISUAL, defaultModule=true)
public class TabGUI
extends Module
implements IDraggable {
    private static final int BACKGROUND = new Color(20, 20, 20, 200).getRGB();
    private static final TTFFontRenderer FONT = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 19.0f);
    private final BooleanProperty useBlur = new BooleanProperty((Module)this, "Blur", true);
    private final EnumProperty<ColorUtil.ColorMode> colorMode = new EnumProperty((Module)this, "Color Mode", (INameable[])ColorUtil.ColorMode.values());
    private final ColorProperty colorProperty = new ColorProperty((Module)this, "Color", Color.RED);
    private final Animation animation = new CubicEaseAnimation(200L);
    private final TabModuleComponent[] tabModuleComponents = new TabModuleComponent[Module.Category.values().length];
    private final Consumer<EventKeyPress> onKeyPress = event -> {
        int length;
        boolean right;
        if (TabGUI.mc.currentScreen != null) {
            return;
        }
        int pressedKey = event.getKeyCode();
        boolean ascending = pressedKey == 208;
        boolean descending = pressedKey == 200;
        boolean left = pressedKey == 203;
        boolean bl2 = right = pressedKey == 205;
        if (left || right) {
            Arrays.stream(this.tabModuleComponents).filter(TabModuleComponent::isSelected).forEach(component -> component.setExpanded(right));
            return;
        }
        for (TabModuleComponent component2 : this.tabModuleComponents) {
            if (component2.isSelected()) {
                component2.onKeyPress(pressedKey);
            }
            if (!component2.isExpanded()) continue;
            return;
        }
        if (!ascending && !descending) {
            return;
        }
        int target = -1;
        for (int i2 = 0; i2 < this.tabModuleComponents.length; ++i2) {
            TabModuleComponent component3 = this.tabModuleComponents[i2];
            if (!component3.isSelected()) continue;
            component3.setSelected(false);
            this.animation.start(descending);
            target = ascending ? i2 + 1 : i2 - 1;
            break;
        }
        if (target == (length = this.tabModuleComponents.length)) {
            target = 0;
        } else if (target == -1) {
            target = length - 1;
        }
        this.tabModuleComponents[target].setSelected(true);
    };

    public TabGUI() {
        this.colorProperty.setHidden(() -> ((ColorUtil.ColorMode)this.colorMode.getValue()).isRainbow());
        for (int i2 = 0; i2 < this.tabModuleComponents.length; ++i2) {
            this.tabModuleComponents[i2] = new TabModuleComponent(Module.Category.values()[i2]);
        }
        this.tabModuleComponents[0].setSelected(true);
        this.registerEventHandler(EventKeyPress.class, this.onKeyPress);
        Corrosion.INSTANCE.getGuiComponentManager().register(this, 5, 20, 85, Module.Category.values().length * 20);
    }

    @Override
    public void onEnable() {
        Arrays.stream(this.tabModuleComponents).filter(component -> component.getModules() == null).forEach(TabModuleComponent::onPostLoad);
    }

    @Override
    public void render(HudComponentProxy baseComponent, ScaledResolution scaledResolution, int posX, int posY, int expandX, int expandY) {
        TabModuleComponent component;
        int i2;
        if (((Boolean)this.useBlur.getValue()).booleanValue()) {
            Blurrer blurrer = Corrosion.INSTANCE.getBlurrer();
            blurrer.blur(posX, posY, expandX, expandY, true);
            blurrer.bloom(posX, posY, expandX, expandY, 15, 200);
        }
        RenderUtil.drawRoundedRect(posX, posY, posX + expandX, posY + expandY, BACKGROUND, BACKGROUND);
        for (i2 = 0; i2 < this.tabModuleComponents.length; ++i2) {
            component = this.tabModuleComponents[i2];
            if (!component.isSelected() && !component.isExpanded()) continue;
            int selectedColor = ColorUtil.getColor((ColorUtil.ColorMode)this.colorMode.getValue(), (Color)this.colorProperty.getValue(), 5);
            if (this.animation.isAnimating()) {
                int deltaY;
                if (i2 == 0 && !this.animation.isInverted()) {
                    deltaY = (int)((double)(-(this.tabModuleComponents.length * 20 - 17)) * this.animation.getProgression());
                    int offset = this.tabModuleComponents.length * 20 - 17;
                    RenderUtil.drawRoundedRect(posX, posY + offset + deltaY, posX + expandX, posY + 17 + deltaY + offset, selectedColor);
                } else if (i2 == this.tabModuleComponents.length - 1 && this.animation.isInverted()) {
                    deltaY = (int)((double)(this.tabModuleComponents.length * 20 - 17) * this.animation.getProgression());
                    RenderUtil.drawRoundedRect(posX, posY + deltaY, posX + expandX, posY + 17 + deltaY, selectedColor);
                } else {
                    deltaY = (int)(this.animation.calculate() * (double)(this.animation.isInverted() ? -17 : 17));
                    int modifier = this.animation.isInverted() ? i2 + 1 : i2 - 1;
                    RenderUtil.drawRoundedRect(posX, posY + modifier * 20 + deltaY, posX + expandX, posY + 17 + modifier * 20 + deltaY, selectedColor);
                }
            } else {
                RenderUtil.drawRoundedRect(posX, posY + i2 * 20, posX + expandX, posY + 17 + i2 * 20, selectedColor);
            }
            if (!component.isExpanded()) continue;
            component.renderExpansion(posX + 85, posY, selectedColor);
        }
        for (i2 = 0; i2 < this.tabModuleComponents.length; ++i2) {
            component = this.tabModuleComponents[i2];
            FONT.drawStringWithShadow(component.getCategory().getName(), posX + 5, posY + 5 + i2 * 20, Color.WHITE.getRGB());
        }
    }

    @Override
    public void renderBackground(ScaledResolution scaledResolution, int posX, int posY, int expandX, int expandY, int color) {
        RenderUtil.drawRoundedRect(posX - 2, posY - 2, posX + expandX + 2, posY + expandY + 2, color);
    }
}

