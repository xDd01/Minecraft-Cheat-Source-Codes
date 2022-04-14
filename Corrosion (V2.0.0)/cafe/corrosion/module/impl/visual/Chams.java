/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.visual;

import cafe.corrosion.event.impl.EventPlayerRender;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.util.nameable.INameable;
import net.minecraft.client.renderer.GlStateManager;

@ModuleAttributes(name="Chams", tMobileName="Tapestry", description="Shows players through walls", category=Module.Category.VISUAL)
public class Chams
extends Module {
    public final EnumProperty<Mode> mode = new EnumProperty((Module)this, "Mode", (INameable[])Mode.values());
    private final BooleanProperty disableLighting = new BooleanProperty((Module)this, "Disable Lighting", true);

    public Chams() {
        this.registerEventHandler(EventPlayerRender.class, event -> {
            if (event.getPlayer() == Chams.mc.thePlayer) {
                return;
            }
            if (event.isPre()) {
                GlStateManager.enablePolygonOffset();
                GlStateManager.doPolygonOffset(1.0f, -1300000.0f);
                if (((Boolean)this.disableLighting.getValue()).booleanValue()) {
                    GlStateManager.disableLighting();
                }
            } else {
                GlStateManager.disablePolygonOffset();
                if (((Boolean)this.disableLighting.getValue()).booleanValue()) {
                    GlStateManager.enableLighting();
                }
            }
        });
    }

    @Override
    public String getMode() {
        return ((Mode)this.mode.getValue()).getName();
    }

    public static enum Mode implements INameable
    {
        NORMAL("Normal");

        private final String name;

        private Mode(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }
    }
}

