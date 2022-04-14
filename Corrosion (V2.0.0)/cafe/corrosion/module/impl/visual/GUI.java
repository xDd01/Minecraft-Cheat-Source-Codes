/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.visual;

import cafe.corrosion.event.impl.EventKeyPress;
import cafe.corrosion.menu.animation.Animation;
import cafe.corrosion.menu.animation.impl.BounceAnimation;
import cafe.corrosion.menu.animation.impl.CubicEaseAnimation;
import cafe.corrosion.menu.animation.impl.EaseOutBackAnimation;
import cafe.corrosion.menu.csgo.CounterStrikeGUI;
import cafe.corrosion.menu.dropdown.SimpleClickGUIScreen;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.util.nameable.INameable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.gui.GuiScreen;

@ModuleAttributes(name="GUI", description="Displays a menu of modules you can configure and enable", category=Module.Category.VISUAL, hidden=true)
public class GUI
extends Module {
    private final EnumProperty<GuiType> guiTypeProperty = new EnumProperty((Module)this, "Menu Type", (INameable[])GuiType.values());
    private final EnumProperty<AnimationStyle> animationStyleProperty = new EnumProperty((Module)this, "Animation Style", (INameable[])AnimationStyle.values());
    private final BooleanProperty useAnimation = new BooleanProperty((Module)this, "Use Animations", true);
    private final Map<GuiType, GuiScreen> clickGuis = new HashMap<GuiType, GuiScreen>();

    public GUI() {
        this.animationStyleProperty.setHidden(() -> (Boolean)this.useAnimation.getValue() == false);
        this.setKeyCode(54);
        this.registerEventHandler(EventKeyPress.class, event -> {
            if (GUI.mc.thePlayer == null || event.getKeyCode() != 35) {
                return;
            }
            mc.displayGuiScreen(new CounterStrikeGUI(this.animationStyleProperty, this.useAnimation));
        });
    }

    @Override
    public void onEnable() {
        this.toggle();
        if (GUI.mc.thePlayer == null) {
            return;
        }
        GuiType guiType = (GuiType)this.guiTypeProperty.getValue();
        if (!this.clickGuis.containsKey(guiType)) {
            this.clickGuis.put(guiType, guiType.getScreenSupplier().apply(this));
        }
        mc.displayGuiScreen(this.clickGuis.get(guiType));
    }

    public static enum AnimationStyle implements INameable
    {
        CUBIC_EASE("Cubic Ease", () -> new CubicEaseAnimation(400L)),
        BOUNCE("Bounce", () -> new BounceAnimation(650L)),
        EASE_BACK("Ease Back", () -> new EaseOutBackAnimation(400L));

        private final String name;
        private final Supplier<Animation> animationSupplier;

        @Override
        public String getName() {
            return this.name;
        }

        public Supplier<Animation> getAnimationSupplier() {
            return this.animationSupplier;
        }

        private AnimationStyle(String name, Supplier<Animation> animationSupplier) {
            this.name = name;
            this.animationSupplier = animationSupplier;
        }
    }

    public static enum GuiType implements INameable
    {
        DROP_DOWN("Dropdown", module -> new SimpleClickGUIScreen(((GUI)module).animationStyleProperty, ((GUI)module).useAnimation));

        private final String name;
        private final Function<GUI, GuiScreen> screenSupplier;

        @Override
        public String getName() {
            return this.name;
        }

        public Function<GUI, GuiScreen> getScreenSupplier() {
            return this.screenSupplier;
        }

        private GuiType(String name, Function<GUI, GuiScreen> screenSupplier) {
            this.name = name;
            this.screenSupplier = screenSupplier;
        }
    }
}

