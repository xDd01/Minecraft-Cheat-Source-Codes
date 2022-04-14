/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.visual;

import cafe.corrosion.Corrosion;
import cafe.corrosion.component.draggable.IDraggable;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.menu.drag.data.HudComponentProxy;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.ColorProperty;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.nameable.INameable;
import cafe.corrosion.util.render.ColorUtil;
import java.awt.Color;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

@ModuleAttributes(name="ModuleList", description="Displays active modules", category=Module.Category.VISUAL, defaultModule=true)
public class ModuleList
extends Module
implements IDraggable {
    private static final int BACKGROUND = new Color(20, 20, 20, 200).getRGB();
    private final BooleanProperty animations = new BooleanProperty((Module)this, "Animations", true);
    private final BooleanProperty displayBackground = new BooleanProperty((Module)this, "Background", true);
    private final BooleanProperty reverseSorting = new BooleanProperty(this, "Reverse Sorting");
    private final BooleanProperty shaded = new BooleanProperty(this, "Shaded");
    private final EnumProperty<BlurMode> blurMode = new EnumProperty((Module)this, "Blur Mode", (INameable[])BlurMode.values());
    private final EnumProperty<ColorUtil.ColorMode> colorMode = new EnumProperty((Module)this, "Color Mode", (INameable[])ColorUtil.ColorMode.values());
    private final EnumProperty<ArrayList> listProperty = new EnumProperty((Module)this, "List Mode", (INameable[])ArrayList.values());
    private final ColorProperty color = new ColorProperty((Module)this, "Color", new Color(4, 97, 49));
    private final TTFFontRenderer font = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.PRODUCT_SANS, 19.0f);

    public ModuleList() {
        this.color.setHidden(() -> ((ColorUtil.ColorMode)this.colorMode.getValue()).isRainbow());
        this.shaded.setHidden(() -> this.blurMode.getValue() != BlurMode.BLUR);
        Corrosion.INSTANCE.getGuiComponentManager().register(this, 5, 5, 5, 5);
    }

    @Override
    public void render(HudComponentProxy component, ScaledResolution scaledResolution, int posX, int posY, int expandX, int expandY) {
        float startPos;
        float length;
        String name;
        List names = Corrosion.INSTANCE.getModuleManager().getObjects().stream().filter(module -> module.isEnabled() || module.getAnimation().isAnimating()).sorted((o1, o2) -> Float.compare(this.font.getWidth(o1.name()), this.font.getWidth(o2.name()))).collect(Collectors.toList());
        if (!((Boolean)this.reverseSorting.getValue()).booleanValue()) {
            Collections.reverse(names);
        }
        boolean displayBackground = (Boolean)this.displayBackground.getValue();
        boolean useShading = (Boolean)this.shaded.getValue() != false && !this.shaded.getHidden().getAsBoolean();
        int position = 1;
        if (displayBackground && ((BlurMode)this.blurMode.getValue()).equals(BlurMode.BLUR)) {
            for (Module module2 : names) {
                name = module2.name();
                length = this.font.getWidth(name);
                startPos = (float)scaledResolution.getScaledWidth() - length;
                if (module2.getAnimation().isAnimating()) {
                    double percent = module2.getAnimation().calculate();
                    startPos = module2.getAnimation().isInverted() ? (float)((double)startPos + (double)length * percent) : (float)((double)startPos - ((double)length * percent - (double)length));
                }
                ((BlurMode)this.blurMode.getValue()).getBoxConsumer().draw(startPos - 2.0f, position, length + 3.0f, 11.0f, !useShading);
                position += 11;
            }
        }
        position = 1;
        for (Module module2 : names) {
            name = module2.name();
            length = this.font.getWidth(name);
            startPos = (float)scaledResolution.getScaledWidth() - length;
            if (module2.getAnimation().isAnimating()) {
                double percent = module2.getAnimation().calculate();
                startPos = module2.getAnimation().isInverted() ? (float)((double)startPos + (double)length * percent) : (float)((double)startPos - ((double)length * percent - (double)length));
            }
            int color = ColorUtil.getColor((ColorUtil.ColorMode)this.colorMode.getValue(), (Color)this.color.getValue(), position);
            ((BlurMode)this.blurMode.getValue()).getBoxConsumer().draw(startPos - 2.0f, position, length + 1.0f, 11.0f, useShading);
            this.font.drawStringWithShadow(name, (int)startPos - 1, position + 1, color);
            component.getYExpand().setValue(position += 11);
        }
    }

    @Override
    public void renderBackground(ScaledResolution scaledResolution, int posX, int posY, int expandX, int expandY, int color) {
        List names = Corrosion.INSTANCE.getModuleManager().getObjects().stream().filter(module -> module.isEnabled() || module.getAnimation().isAnimating()).sorted((o1, o2) -> Float.compare(this.font.getWidth(o1.name()), this.font.getWidth(o2.name()))).collect(Collectors.toList());
        if (((Boolean)this.reverseSorting.getValue()).booleanValue()) {
            Collections.reverse(names);
        }
        int position = 1;
        for (Module module2 : names) {
            String name = module2.name();
            float length = this.font.getWidth(name);
            float startPos = (float)scaledResolution.getScaledWidth() - length;
            Gui.drawRect(startPos - 2.0f, position - 1, startPos + length + 1.0f, position + 10, color);
            position += 11;
        }
    }

    public BooleanProperty getAnimations() {
        return this.animations;
    }

    public BooleanProperty getDisplayBackground() {
        return this.displayBackground;
    }

    public BooleanProperty getReverseSorting() {
        return this.reverseSorting;
    }

    public BooleanProperty getShaded() {
        return this.shaded;
    }

    public EnumProperty<BlurMode> getBlurMode() {
        return this.blurMode;
    }

    public EnumProperty<ColorUtil.ColorMode> getColorMode() {
        return this.colorMode;
    }

    public EnumProperty<ArrayList> getListProperty() {
        return this.listProperty;
    }

    public ColorProperty getColor() {
        return this.color;
    }

    public TTFFontRenderer getFont() {
        return this.font;
    }

    private static interface BoxConsumer {
        public void draw(float var1, float var2, float var3, float var4, boolean var5);
    }

    public static enum BlurMode implements INameable
    {
        BLUR("Blur", (posX, posY, expandX, expandY, bloom) -> {
            if (bloom) {
                Corrosion.INSTANCE.getBlurrer().bloom((int)posX, (int)posY, (int)expandX, (int)expandY, 15, 200);
            } else {
                Corrosion.INSTANCE.getBlurrer().blur(posX, posY, expandX, expandY, true);
            }
        }),
        BLACK("Black", (posX, posY, expandX, expandY, bloom) -> Gui.drawRect(posX, posY, posX + expandX, posY + expandY, BACKGROUND)),
        NONE("None", (a2, b2, c2, d2, e2) -> {});

        private final String name;
        private final BoxConsumer boxConsumer;

        private BlurMode(String name, BoxConsumer boxConsumer) {
            this.name = name;
            this.boxConsumer = boxConsumer;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public BoxConsumer getBoxConsumer() {
            return this.boxConsumer;
        }
    }

    public static enum ArrayList implements INameable
    {
        CORROSION("Corrosion");

        private final String name;

        @Override
        public String getName() {
            return this.name;
        }

        private ArrayList(String name) {
            this.name = name;
        }
    }
}

