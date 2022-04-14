/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.impl.player;

import cafe.corrosion.Corrosion;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.module.Module;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.property.type.BooleanProperty;
import cafe.corrosion.property.type.EnumProperty;
import cafe.corrosion.property.type.NumberProperty;
import cafe.corrosion.util.font.type.FontType;
import cafe.corrosion.util.nameable.INameable;

@ModuleAttributes(name="SmoothChat", description="Renders chat differently", category=Module.Category.VISUAL)
public class SmoothChat
extends Module {
    private final EnumProperty<FontType> font = new EnumProperty((Module)this, "Font", (INameable)FontType.ROBOTO, (INameable[])FontType.values());
    private final NumberProperty size = new NumberProperty(this, "Size", 9, 9, 24, 1);
    private final BooleanProperty transparentChat = new BooleanProperty((Module)this, "Transparent", false);

    public TTFFontRenderer buildFontRenderer() {
        return Corrosion.INSTANCE.getFontManager().getFontRenderer((FontType)this.font.getValue(), ((Number)this.size.getValue()).intValue());
    }

    public EnumProperty<FontType> getFont() {
        return this.font;
    }

    public NumberProperty getSize() {
        return this.size;
    }

    public BooleanProperty getTransparentChat() {
        return this.transparentChat;
    }
}

