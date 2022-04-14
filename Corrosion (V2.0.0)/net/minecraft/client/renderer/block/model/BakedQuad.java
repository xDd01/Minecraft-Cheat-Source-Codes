/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.block.model;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.IVertexProducer;
import optifine.Config;
import optifine.Reflector;

public class BakedQuad
implements IVertexProducer {
    protected int[] vertexData;
    protected final int tintIndex;
    protected final EnumFacing face;
    private TextureAtlasSprite sprite = null;
    private int[] vertexDataSingle = null;

    public BakedQuad(int[] p_i9_1_, int p_i9_2_, EnumFacing p_i9_3_, TextureAtlasSprite p_i9_4_) {
        this.vertexData = p_i9_1_;
        this.tintIndex = p_i9_2_;
        this.face = p_i9_3_;
        this.sprite = p_i9_4_;
        this.fixVertexData();
    }

    public TextureAtlasSprite getSprite() {
        if (this.sprite == null) {
            this.sprite = BakedQuad.getSpriteByUv(this.getVertexData());
        }
        return this.sprite;
    }

    public String toString() {
        return "vertex: " + this.vertexData.length / 7 + ", tint: " + this.tintIndex + ", facing: " + this.face + ", sprite: " + this.sprite;
    }

    public BakedQuad(int[] vertexDataIn, int tintIndexIn, EnumFacing faceIn) {
        this.vertexData = vertexDataIn;
        this.tintIndex = tintIndexIn;
        this.face = faceIn;
        this.fixVertexData();
    }

    public int[] getVertexData() {
        this.fixVertexData();
        return this.vertexData;
    }

    public boolean hasTintIndex() {
        return this.tintIndex != -1;
    }

    public int getTintIndex() {
        return this.tintIndex;
    }

    public EnumFacing getFace() {
        return this.face;
    }

    public int[] getVertexDataSingle() {
        if (this.vertexDataSingle == null) {
            this.vertexDataSingle = BakedQuad.makeVertexDataSingle(this.getVertexData(), this.getSprite());
        }
        return this.vertexDataSingle;
    }

    private static int[] makeVertexDataSingle(int[] p_makeVertexDataSingle_0_, TextureAtlasSprite p_makeVertexDataSingle_1_) {
        int[] aint = (int[])p_makeVertexDataSingle_0_.clone();
        int i2 = p_makeVertexDataSingle_1_.sheetWidth / p_makeVertexDataSingle_1_.getIconWidth();
        int j2 = p_makeVertexDataSingle_1_.sheetHeight / p_makeVertexDataSingle_1_.getIconHeight();
        int k2 = aint.length / 4;
        for (int l2 = 0; l2 < 4; ++l2) {
            int i1 = l2 * k2;
            float f2 = Float.intBitsToFloat(aint[i1 + 4]);
            float f1 = Float.intBitsToFloat(aint[i1 + 4 + 1]);
            float f22 = p_makeVertexDataSingle_1_.toSingleU(f2);
            float f3 = p_makeVertexDataSingle_1_.toSingleV(f1);
            aint[i1 + 4] = Float.floatToRawIntBits(f22);
            aint[i1 + 4 + 1] = Float.floatToRawIntBits(f3);
        }
        return aint;
    }

    @Override
    public void pipe(IVertexConsumer p_pipe_1_) {
        Reflector.callVoid(Reflector.LightUtil_putBakedQuad, p_pipe_1_, this);
    }

    private static TextureAtlasSprite getSpriteByUv(int[] p_getSpriteByUv_0_) {
        float f2 = 1.0f;
        float f1 = 1.0f;
        float f22 = 0.0f;
        float f3 = 0.0f;
        int i2 = p_getSpriteByUv_0_.length / 4;
        for (int j2 = 0; j2 < 4; ++j2) {
            int k2 = j2 * i2;
            float f4 = Float.intBitsToFloat(p_getSpriteByUv_0_[k2 + 4]);
            float f5 = Float.intBitsToFloat(p_getSpriteByUv_0_[k2 + 4 + 1]);
            f2 = Math.min(f2, f4);
            f1 = Math.min(f1, f5);
            f22 = Math.max(f22, f4);
            f3 = Math.max(f3, f5);
        }
        float f6 = (f2 + f22) / 2.0f;
        float f7 = (f1 + f3) / 2.0f;
        TextureAtlasSprite textureatlassprite = Minecraft.getMinecraft().getTextureMapBlocks().getIconByUV(f6, f7);
        return textureatlassprite;
    }

    private void fixVertexData() {
        if (Config.isShaders()) {
            if (this.vertexData.length == 28) {
                this.vertexData = BakedQuad.expandVertexData(this.vertexData);
            }
        } else if (this.vertexData.length == 56) {
            this.vertexData = BakedQuad.compactVertexData(this.vertexData);
        }
    }

    private static int[] expandVertexData(int[] p_expandVertexData_0_) {
        int i2 = p_expandVertexData_0_.length / 4;
        int j2 = i2 * 2;
        int[] aint = new int[j2 * 4];
        for (int k2 = 0; k2 < 4; ++k2) {
            System.arraycopy(p_expandVertexData_0_, k2 * i2, aint, k2 * j2, i2);
        }
        return aint;
    }

    private static int[] compactVertexData(int[] p_compactVertexData_0_) {
        int i2 = p_compactVertexData_0_.length / 4;
        int j2 = i2 / 2;
        int[] aint = new int[j2 * 4];
        for (int k2 = 0; k2 < 4; ++k2) {
            System.arraycopy(p_compactVertexData_0_, k2 * i2, aint, k2 * j2, j2);
        }
        return aint;
    }
}

