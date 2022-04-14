/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import optifine.Config;

public class NaturalProperties {
    public int rotation = 1;
    public boolean flip = false;
    private Map[] quadMaps = new Map[8];

    public NaturalProperties(String p_i68_1_) {
        if (p_i68_1_.equals("4")) {
            this.rotation = 4;
        } else if (p_i68_1_.equals("2")) {
            this.rotation = 2;
        } else if (p_i68_1_.equals("F")) {
            this.flip = true;
        } else if (p_i68_1_.equals("4F")) {
            this.rotation = 4;
            this.flip = true;
        } else if (p_i68_1_.equals("2F")) {
            this.rotation = 2;
            this.flip = true;
        } else {
            Config.warn("NaturalTextures: Unknown type: " + p_i68_1_);
        }
    }

    public boolean isValid() {
        return this.rotation != 2 && this.rotation != 4 ? this.flip : true;
    }

    public synchronized BakedQuad getQuad(BakedQuad p_getQuad_1_, int p_getQuad_2_, boolean p_getQuad_3_) {
        int i2 = p_getQuad_2_;
        if (p_getQuad_3_) {
            i2 = p_getQuad_2_ | 4;
        }
        if (i2 > 0 && i2 < this.quadMaps.length) {
            BakedQuad bakedquad;
            IdentityHashMap<BakedQuad, BakedQuad> map = this.quadMaps[i2];
            if (map == null) {
                this.quadMaps[i2] = map = new IdentityHashMap<BakedQuad, BakedQuad>(1);
            }
            if ((bakedquad = (BakedQuad)map.get(p_getQuad_1_)) == null) {
                bakedquad = this.makeQuad(p_getQuad_1_, p_getQuad_2_, p_getQuad_3_);
                map.put(p_getQuad_1_, bakedquad);
            }
            return bakedquad;
        }
        return p_getQuad_1_;
    }

    private BakedQuad makeQuad(BakedQuad p_makeQuad_1_, int p_makeQuad_2_, boolean p_makeQuad_3_) {
        int[] aint = p_makeQuad_1_.getVertexData();
        int i2 = p_makeQuad_1_.getTintIndex();
        EnumFacing enumfacing = p_makeQuad_1_.getFace();
        TextureAtlasSprite textureatlassprite = p_makeQuad_1_.getSprite();
        if (!this.isFullSprite(p_makeQuad_1_)) {
            return p_makeQuad_1_;
        }
        aint = this.transformVertexData(aint, p_makeQuad_2_, p_makeQuad_3_);
        BakedQuad bakedquad = new BakedQuad(aint, i2, enumfacing, textureatlassprite);
        return bakedquad;
    }

    private int[] transformVertexData(int[] p_transformVertexData_1_, int p_transformVertexData_2_, boolean p_transformVertexData_3_) {
        int[] aint = (int[])p_transformVertexData_1_.clone();
        int i2 = 4 - p_transformVertexData_2_;
        if (p_transformVertexData_3_) {
            i2 += 3;
        }
        i2 %= 4;
        int j2 = aint.length / 4;
        for (int k2 = 0; k2 < 4; ++k2) {
            int l2 = k2 * j2;
            int i1 = i2 * j2;
            aint[i1 + 4] = p_transformVertexData_1_[l2 + 4];
            aint[i1 + 4 + 1] = p_transformVertexData_1_[l2 + 4 + 1];
            if (p_transformVertexData_3_) {
                if (--i2 >= 0) continue;
                i2 = 3;
                continue;
            }
            if (++i2 <= 3) continue;
            i2 = 0;
        }
        return aint;
    }

    private boolean isFullSprite(BakedQuad p_isFullSprite_1_) {
        TextureAtlasSprite textureatlassprite = p_isFullSprite_1_.getSprite();
        float f2 = textureatlassprite.getMinU();
        float f1 = textureatlassprite.getMaxU();
        float f22 = f1 - f2;
        float f3 = f22 / 256.0f;
        float f4 = textureatlassprite.getMinV();
        float f5 = textureatlassprite.getMaxV();
        float f6 = f5 - f4;
        float f7 = f6 / 256.0f;
        int[] aint = p_isFullSprite_1_.getVertexData();
        int i2 = aint.length / 4;
        for (int j2 = 0; j2 < 4; ++j2) {
            int k2 = j2 * i2;
            float f8 = Float.intBitsToFloat(aint[k2 + 4]);
            float f9 = Float.intBitsToFloat(aint[k2 + 4 + 1]);
            if (!this.equalsDelta(f8, f2, f3) && !this.equalsDelta(f8, f1, f3)) {
                return false;
            }
            if (this.equalsDelta(f9, f4, f7) || this.equalsDelta(f9, f5, f7)) continue;
            return false;
        }
        return true;
    }

    private boolean equalsDelta(float p_equalsDelta_1_, float p_equalsDelta_2_, float p_equalsDelta_3_) {
        float f2 = MathHelper.abs(p_equalsDelta_1_ - p_equalsDelta_2_);
        return f2 < p_equalsDelta_3_;
    }
}

