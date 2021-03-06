package net.minecraft.client.resources.data;

import java.util.*;

public class TextureMetadataSection implements IMetadataSection
{
    private final boolean textureBlur;
    private final boolean textureClamp;
    private final List listMipmaps;
    
    public TextureMetadataSection(final boolean p_i45102_1_, final boolean p_i45102_2_, final List p_i45102_3_) {
        this.textureBlur = p_i45102_1_;
        this.textureClamp = p_i45102_2_;
        this.listMipmaps = p_i45102_3_;
    }
    
    public boolean getTextureBlur() {
        return this.textureBlur;
    }
    
    public boolean getTextureClamp() {
        return this.textureClamp;
    }
    
    public List getListMipmaps() {
        return Collections.unmodifiableList((List<?>)this.listMipmaps);
    }
}
