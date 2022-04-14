/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.social.feature;

import cafe.corrosion.Corrosion;
import cafe.corrosion.event.Event;
import cafe.corrosion.event.handler.IHandler;
import cafe.corrosion.font.TTFFontRenderer;
import cafe.corrosion.module.Module;
import cafe.corrosion.util.font.type.FontType;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;

public abstract class Feature
implements IHandler {
    protected static final Minecraft mc = Minecraft.getMinecraft();
    protected static final List<Module> ENABLED_MODULES = new ArrayList<Module>();

    protected <T extends Event> void registerEventHandler(Class<T> clazz, Consumer<T> consumer) {
        Corrosion.INSTANCE.getEventBus().register(this, clazz, consumer);
    }

    protected static TTFFontRenderer getFont(FontType type, float size) {
        return Corrosion.INSTANCE.getFontManager().getFontRenderer(type, size);
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

