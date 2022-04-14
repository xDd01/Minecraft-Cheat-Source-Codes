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
import cafe.corrosion.util.font.type.FontType;
import java.awt.Color;
import java.text.DecimalFormat;
import net.minecraft.client.gui.ScaledResolution;

@ModuleAttributes(name="Coords", description="Displays your coordinates", category=Module.Category.VISUAL, defaultModule=true)
public class Coords
extends Module
implements IDraggable {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##");
    private static final TTFFontRenderer ROBOTO = Corrosion.INSTANCE.getFontManager().getFontRenderer(FontType.ROBOTO, 19.0f);
    private final BooleanProperty xyzProperty = new BooleanProperty((Module)this, "XYZ", true);
    private final BooleanProperty speedProperty = new BooleanProperty((Module)this, "BPS", true);

    public Coords() {
        Corrosion.INSTANCE.getGuiComponentManager().register(this, 5, 400, 20, 20);
    }

    public int getSize() {
        int size = 0;
        if (((Boolean)this.xyzProperty.getValue()).booleanValue()) {
            size += 3;
        }
        if (((Boolean)this.speedProperty.getValue()).booleanValue()) {
            ++size;
        }
        return size;
    }

    @Override
    public void render(HudComponentProxy component, ScaledResolution scaledResolution, int posX, int posY, int expandX, int expandY) {
        double mcX = Coords.mc.thePlayer.posX;
        double mcY = Coords.mc.thePlayer.posY;
        double mcZ = Coords.mc.thePlayer.posZ;
        String[] text = new String[this.getSize()];
        int index = 0;
        if (((Boolean)this.speedProperty.getValue()).booleanValue()) {
            double speed = Math.hypot(mcX - Coords.mc.thePlayer.lastTickPosX, mcZ - Coords.mc.thePlayer.lastTickPosZ) * 20.0 * (double)Coords.mc.timer.timerSpeed;
            text[0] = DECIMAL_FORMAT.format(speed) + " BPS";
            ++index;
        }
        text[index] = "X:" + DECIMAL_FORMAT.format(mcX);
        text[index + 1] = "Y:" + DECIMAL_FORMAT.format(mcY);
        text[index + 2] = "Z:" + DECIMAL_FORMAT.format(mcZ);
        int maxHeight = 0;
        int maxWidth = 0;
        for (int i2 = 0; i2 < 4; ++i2) {
            String contents = text[i2];
            maxHeight = (int)((float)maxHeight + (ROBOTO.getHeight(contents) + 1.0f));
            maxWidth = (int)Math.max((float)maxWidth, ROBOTO.getWidth(contents));
        }
        for (String contents : text) {
            float height = ROBOTO.getHeight(contents);
            ROBOTO.drawStringWithShadow(contents, posX, posY + maxHeight, Color.WHITE.getRGB());
            maxHeight = (int)((float)maxHeight + height);
        }
        component.getXExpand().setValue(maxWidth);
        component.getYExpand().setValue(maxHeight);
    }
}

