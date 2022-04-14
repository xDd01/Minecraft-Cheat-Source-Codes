package io.github.nevalackin.client.impl.event.render.model;

import io.github.nevalackin.client.api.event.Event;
import io.github.nevalackin.client.impl.event.render.RenderCallback;
import net.minecraft.entity.EntityLivingBase;

import java.util.Set;

public final class ApplyHurtEffectEvent implements Event {

    private RenderCallbackFunc preRenderModelCallback;
    private RenderCallbackFunc preRenderLayersCallback;
    private int hurtColour;
    private final EntityLivingBase entity;

    public ApplyHurtEffectEvent(RenderCallbackFunc preRenderModelCallback,
                                RenderCallbackFunc preRenderLayersCallback,
                                int hurtColour, EntityLivingBase entity) {
        this.preRenderLayersCallback = preRenderLayersCallback;
        this.preRenderModelCallback = preRenderModelCallback;
        this.hurtColour = hurtColour;
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

    public int getHurtColour() {
        return hurtColour;
    }

    public void setHurtColour(int hurtColour) {
        this.hurtColour = hurtColour;
    }

    public RenderCallbackFunc getPreRenderModelCallback() {
        return preRenderModelCallback;
    }

    public void setPreRenderModelCallback(RenderCallbackFunc preRenderModelCallback) {
        this.preRenderModelCallback = preRenderModelCallback;
    }

    public RenderCallbackFunc getPreRenderLayersCallback() {
        return preRenderLayersCallback;
    }

    public void setPreRenderLayersCallback(RenderCallbackFunc preRenderLayersCallback) {
        this.preRenderLayersCallback = preRenderLayersCallback;
    }

    public enum RenderCallbackFunc {
        SET, UNSET, NONE
    }
}
