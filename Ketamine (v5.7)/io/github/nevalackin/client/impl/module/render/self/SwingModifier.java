package io.github.nevalackin.client.impl.module.render.self;

import com.sun.org.apache.bcel.internal.generic.NEW;
import io.github.nevalackin.client.api.module.Category;
import io.github.nevalackin.client.api.module.Module;
import io.github.nevalackin.client.impl.event.render.item.ApplyBlockTransformationEvent;
import io.github.nevalackin.client.impl.event.render.item.GetArmSwingModEvent;
import io.github.nevalackin.client.impl.event.render.item.PreRenderItemEvent;
import io.github.nevalackin.client.impl.property.DoubleProperty;
import io.github.nevalackin.client.impl.property.EnumProperty;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public final class SwingModifier extends Module {

    private final DoubleProperty xTranslationProperty = new DoubleProperty("X Translate", 0, -1, 1, 0.1);
    private final DoubleProperty yTranslationProperty = new DoubleProperty("Y Translate", 0, -1, 1, 0.1);
    private final DoubleProperty zTranslationProperty = new DoubleProperty("Z Translate", 0, -1, 1, 0.1);

    private final EnumProperty<BlockTransformationType> blockTransformProperty = new EnumProperty<>("Block Transform", BlockTransformationType._1_7);

    private final DoubleProperty swingSpeedModProperty = new DoubleProperty("Swing Speed Mod", 1, 0, 5, 0.1);

    private float spinDeltaTime;

    public SwingModifier() {
        super("Swing Modifier", Category.RENDER, Category.SubCategory.RENDER_SELF);

        this.register(this.blockTransformProperty, this.xTranslationProperty, this.yTranslationProperty, this.zTranslationProperty, this.swingSpeedModProperty);
    }

    @EventLink
    private final Listener<GetArmSwingModEvent> onGetArmSwingMod = event -> {
        event.setModifier(this.swingSpeedModProperty.getValue().floatValue());
    };

    @EventLink
    private final Listener<ApplyBlockTransformationEvent> onBlockTransform = event -> {
        switch (this.blockTransformProperty.getValue()) {
            case _1_8:
                event.setReplacementTransform(((equipProgress, swingProgress) -> {
                    event.getOriginalTransform().transform(equipProgress, 0.f);
                }));
                break;
            case OLD:
                event.setReplacementTransform(((equipProgress, swingProgress) -> {
                    final double swingProg = Math.sin(Math.sqrt(swingProgress) * Math.PI);
                    glTranslatef(0.f, 0.2f, 0.f);
                    event.getOriginalTransform().transform(equipProgress / 3.f, 0.f);
                    glRotatef((float) swingProg * 30.f, -1.f, 0, 0);
                }));
                break;
            case ANTH:
                event.setReplacementTransform(((equipProgress, swingProgress) -> {
                    final double swingProg = Math.sin(Math.sqrt(swingProgress) * Math.PI);
                    glTranslatef(0.1f, 0.1f, 0.f);
                    glRotatef((float) swingProg * 20.f, 0.f, 1.f, 0.f);
                    event.getOriginalTransform().transform(equipProgress / 2.f, swingProgress);
                }));
                break;
            case THOMAZ:
                event.setReplacementTransform(((equipProgress, swingProgress) -> {
                    event.getOriginalTransform().transform(equipProgress, swingProgress);
                }));
                break;
            case NEVALACK:
                break;
            case POSK:
                event.setReplacementTransform(((equipProgress, swingProgress) -> {
                    final double swingProg = Math.sin(Math.sqrt(swingProgress) * Math.PI);
                    event.getOriginalTransform().transform(equipProgress/2, 0);
                    GL11.glTranslatef(0F, 0.25F, 0F);
                    GL11.glRotated(-swingProg * 25.0F, swingProg / 4.9, 0, 2.0F);
                    GL11.glRotated(-swingProg * 30.0F, 4.0F, swingProg * 1.7, 0F);
                }));
                break;
            case DRAKE:
                event.setReplacementTransform(((equipProgress, swingProgress) -> {
                    glTranslatef(-0.15f, 0.f, 0.f);
                    event.getOriginalTransform().transform(equipProgress, 0.f);
                    glRotatef( this.spinDeltaTime * 360.f, 0.f, 1.f, 0.f);
                    glRotatef(45.f, 1.f, -1.f, 0.f);

                    this.spinDeltaTime += 1.f / 100.f;
                    if (this.spinDeltaTime > 1.f) this.spinDeltaTime = 0.f;
                }));
                break;
        }
    };

    @EventLink
    private final Listener<PreRenderItemEvent> onPreItemRender = event -> {
        // Apply translation
        glTranslated(this.xTranslationProperty.getValue(),
                     this.yTranslationProperty.getValue(),
                     this.zTranslationProperty.getValue());
    };

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    private enum BlockTransformationType {
        _1_8("1.8"),
        _1_7("1.7"),
        ANTH("Anth"),
        THOMAZ("Thomaz"),
        NEVALACK("NevaLack"),
        POSK("Posk"),
        DRAKE("Drake"),
        OLD("Old");

        private final String name;

        BlockTransformationType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
