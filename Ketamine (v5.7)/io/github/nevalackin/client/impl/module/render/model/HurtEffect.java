package io.github.nevalackin.client.impl.module.render.model;

import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.event.render.model.ApplyHurtEffectEvent;
import io.github.nevalackin.client.impl.property.BooleanProperty;
import io.github.nevalackin.client.impl.property.ColourProperty;
import io.github.nevalackin.client.impl.property.EnumProperty;
import io.github.nevalackin.client.util.render.ColourUtil;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;

public final class HurtEffect extends Module {

    private final EnumProperty<Mode> modeProperty = new EnumProperty<>("Mode", Mode.BOTH);
    private final BooleanProperty healthBasedColourProperty = new BooleanProperty("Health Based Colour", true);
    private final ColourProperty colourProperty = new ColourProperty("Colour", 0x4DFF0000, () -> this.modeProperty.getValue() != Mode.NONE && !healthBasedColourProperty.getValue());

    public HurtEffect() {
        super("Hurt Effect", Category.RENDER, Category.SubCategory.RENDER_MODEL);

        this.register(this.modeProperty, this.healthBasedColourProperty, this.colourProperty);
    }

    @EventLink
    private final Listener<ApplyHurtEffectEvent> onApplyHurtEvent = event -> {
        event.setHurtColour(healthBasedColourProperty.getValue() ? ColourUtil.overwriteAlphaComponent(ColourUtil.blendHealthColours(event.getEntity().getHealth() / event.getEntity().getMaxHealth()), 0x4D) : this.colourProperty.getValue());

        switch (this.modeProperty.getValue()) {
            case NONE:
                event.setPreRenderModelCallback(ApplyHurtEffectEvent.RenderCallbackFunc.NONE);
                break;
            case BOTH:
                event.setPreRenderLayersCallback(ApplyHurtEffectEvent.RenderCallbackFunc.NONE);
                break;
            case ARMOR_ONLY:
                event.setPreRenderModelCallback(ApplyHurtEffectEvent.RenderCallbackFunc.NONE);
                event.setPreRenderLayersCallback(ApplyHurtEffectEvent.RenderCallbackFunc.SET);
                break;
        }
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    private enum Mode {
        ARMOR_ONLY("Armour Only"),
        MODEL_ONLY("Model Only"),
        BOTH("Both"),
        NONE("None");

        private final String name;

        Mode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
