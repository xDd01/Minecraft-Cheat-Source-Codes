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
import net.minecraft.client.gui.ScaledResolution;

@ModuleAttributes(name="HUD", description="Displays the client information and a list of active modules", category=Module.Category.VISUAL, tMobileName="QuikView", defaultModule=true)
public class HUD
extends Module
implements IDraggable {
    private static final TTFFontRenderer ROBOTO = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 24.0f);
    private static final String EDITION = "Yanchop Edition";
    private final EnumProperty<Brand> brandProperty = new EnumProperty((Module)this, "Brand", (INameable[])Brand.values());
    private final BooleanProperty smoothScoreboard = new BooleanProperty(this, "Smooth Scoreboard");
    private final BooleanProperty showEdition = new BooleanProperty((Module)this, "Show Edition", true);
    private final BooleanProperty useRainbow = new BooleanProperty(this, "Rainbow");
    private final ColorProperty color = new ColorProperty((Module)this, "Color", Color.RED);

    public HUD() {
        this.color.setHidden(this.useRainbow::getValue);
        Corrosion.INSTANCE.getGuiComponentManager().register(this, 5, 2, 0, 0);
    }

    @Override
    public void render(HudComponentProxy component, ScaledResolution scaledResolution, int posX, int posY, int expandX, int expandY) {
        int textColor = (Boolean)this.useRainbow.getValue() != false ? ColorUtil.astolfoColors(-6000, 5) : ((Color)this.color.getValue()).getRGB();
        String name = ((Brand)this.brandProperty.getValue()).getText() + ((Boolean)this.showEdition.getValue() != false ? " - Yanchop Edition" : "");
        ROBOTO.drawStringWithShadow(name, posX, posY, textColor);
        component.getXExpand().setValue(Float.valueOf(ROBOTO.getWidth(name)));
        component.getYExpand().setValue(Float.valueOf(ROBOTO.getHeight(name)));
    }

    public EnumProperty<Brand> getBrandProperty() {
        return this.brandProperty;
    }

    public BooleanProperty getSmoothScoreboard() {
        return this.smoothScoreboard;
    }

    public BooleanProperty getShowEdition() {
        return this.showEdition;
    }

    public BooleanProperty getUseRainbow() {
        return this.useRainbow;
    }

    public ColorProperty getColor() {
        return this.color;
    }

    public static enum Brand implements INameable
    {
        SIMPLE("Simple", "\u00a74C\u00a7forrosion"),
        T_MOBILE("T-Mobile", "\u00a7dT\u00a7f-Mobile"),
        ETB("ETB", "ETB");

        private final String name;
        private final String text;

        private Brand(String name, String text) {
            this.name = name;
            this.text = text;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public String getText() {
            return this.text;
        }
    }
}

