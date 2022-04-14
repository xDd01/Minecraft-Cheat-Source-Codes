/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module;

import cafe.corrosion.Corrosion;
import cafe.corrosion.event.handler.IHandler;
import cafe.corrosion.keybind.KeyStorage;
import cafe.corrosion.menu.animation.Animation;
import cafe.corrosion.menu.animation.impl.EaseOutBackAnimation;
import cafe.corrosion.module.attribute.ModuleAttributes;
import cafe.corrosion.module.impl.visual.ModuleList;
import cafe.corrosion.social.feature.Feature;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Collections;
import net.minecraft.util.ResourceLocation;

public abstract class Module
extends Feature
implements IHandler {
    private final Animation animation = new EaseOutBackAnimation(300L);
    private final ModuleAttributes attributes;
    private int keyCode;
    private boolean enabled;

    public Module() {
        Class<?> clazz = this.getClass();
        if (!clazz.isAnnotationPresent(ModuleAttributes.class)) {
            throw new RuntimeException("No ModuleAttributes found for class " + clazz.getName() + "!");
        }
        this.attributes = clazz.getDeclaredAnnotation(ModuleAttributes.class);
        this.keyCode = KeyStorage.getKey(this.attributes.name());
    }

    public void setKeyCode(int keyCode) {
        this.keyCode = keyCode;
        KeyStorage.setKey(this.attributes.name(), keyCode);
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void toggle() {
        if (!this.enabled) {
            this.enabled = true;
            ENABLED_MODULES.add(this);
            Collections.reverse(ENABLED_MODULES);
            this.onEnable();
        } else {
            this.enabled = false;
            ENABLED_MODULES.remove(this);
            if (!this.attributes.hidden()) {
                this.animation.start(true);
            }
            this.onDisable();
        }
        ModuleList moduleList = (ModuleList)Corrosion.INSTANCE.getModuleManager().getModule(ModuleList.class);
        if (moduleList.isEnabled() && ((Boolean)moduleList.getAnimations().getValue()).booleanValue() && !this.attributes.hidden()) {
            this.animation.start(!this.enabled);
        }
    }

    public String getMode() {
        return null;
    }

    public String name() {
        return this.attributes.name() + (this.getMode() == null ? "" : " " + (Object)((Object)ChatFormatting.GRAY) + this.getMode());
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public ModuleAttributes getAttributes() {
        return this.attributes;
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public static enum Category {
        COMBAT("Combat", new ResourceLocation("corrosion/icons/combat.png")),
        MOVEMENT("Movement", new ResourceLocation("corrosion/icons/movement.png")),
        VISUAL("Render", new ResourceLocation("corrosion/icons/render.png")),
        EXPLOIT("Exploit", new ResourceLocation("corrosion/icons/exploit.png")),
        PLAYER("Player", new ResourceLocation("corrosion/icons/player.png")),
        MISC("Misc", new ResourceLocation("corrosion/icons/misc.png"));

        private final String name;
        private final ResourceLocation textureLocation;

        public String getName() {
            return this.name;
        }

        public ResourceLocation getTextureLocation() {
            return this.textureLocation;
        }

        private Category(String name, ResourceLocation textureLocation) {
            this.name = name;
            this.textureLocation = textureLocation;
        }
    }
}

