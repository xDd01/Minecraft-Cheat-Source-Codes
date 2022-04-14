/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.render;

import org.lwjgl.opengl.GL11;

public class Stencil {
    public static void stencilStage(StencilStage stencilStage) {
        if (stencilStage == StencilStage.ENABLE_MASK) {
            GL11.glEnable(2960);
            GL11.glClear(1024);
            GL11.glStencilMask(255);
            GL11.glStencilFunc(519, 1, 255);
            GL11.glStencilOp(7680, 7680, 7681);
        } else if (stencilStage == StencilStage.ENABLE_DRAW) {
            GL11.glStencilMask(0);
            GL11.glStencilFunc(517, 0, 255);
            GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        } else if (stencilStage == StencilStage.DISABLE) {
            GL11.glStencilMask(255);
            GL11.glDisable(2960);
        }
    }

    public static enum StencilStage {
        ENABLE_MASK,
        ENABLE_DRAW,
        DISABLE;

    }
}

