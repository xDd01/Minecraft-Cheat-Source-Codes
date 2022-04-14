/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.texture;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import optifine.Mipmaps;
import optifine.Reflector;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class TextureUtil {
    private static final Logger logger = LogManager.getLogger();
    private static final IntBuffer dataBuffer = GLAllocation.createDirectIntBuffer(0x400000);
    public static final DynamicTexture missingTexture = new DynamicTexture(16, 16);
    public static final int[] missingTextureData = missingTexture.getTextureData();
    private static final int[] mipmapBuffer;

    public static int glGenTextures() {
        return GlStateManager.generateTexture();
    }

    public static void deleteTexture(int textureId) {
        GlStateManager.deleteTexture(textureId);
    }

    public static int uploadTextureImage(int p_110987_0_, BufferedImage p_110987_1_) {
        return TextureUtil.uploadTextureImageAllocate(p_110987_0_, p_110987_1_, false, false);
    }

    public static void uploadTexture(int textureId, int[] p_110988_1_, int p_110988_2_, int p_110988_3_) {
        TextureUtil.bindTexture(textureId);
        TextureUtil.uploadTextureSub(0, p_110988_1_, p_110988_2_, p_110988_3_, 0, 0, false, false, false);
    }

    public static int[][] generateMipmapData(int p_147949_0_, int p_147949_1_, int[][] p_147949_2_) {
        int[][] aint = new int[p_147949_0_ + 1][];
        aint[0] = p_147949_2_[0];
        if (p_147949_0_ > 0) {
            boolean flag = false;
            for (int i2 = 0; i2 < p_147949_2_.length; ++i2) {
                if (p_147949_2_[0][i2] >> 24 != 0) continue;
                flag = true;
                break;
            }
            for (int l1 = 1; l1 <= p_147949_0_; ++l1) {
                if (p_147949_2_[l1] != null) {
                    aint[l1] = p_147949_2_[l1];
                    continue;
                }
                int[] aint1 = aint[l1 - 1];
                int[] aint2 = new int[aint1.length >> 2];
                int j2 = p_147949_1_ >> l1;
                int k2 = aint2.length / j2;
                int l2 = j2 << 1;
                for (int i1 = 0; i1 < j2; ++i1) {
                    for (int j1 = 0; j1 < k2; ++j1) {
                        int k1 = 2 * (i1 + j1 * l2);
                        aint2[i1 + j1 * j2] = TextureUtil.blendColors(aint1[k1 + 0], aint1[k1 + 1], aint1[k1 + 0 + l2], aint1[k1 + 1 + l2], flag);
                    }
                }
                aint[l1] = aint2;
            }
        }
        return aint;
    }

    private static int blendColors(int p_147943_0_, int p_147943_1_, int p_147943_2_, int p_147943_3_, boolean p_147943_4_) {
        return Mipmaps.alphaBlend(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_);
    }

    private static int blendColorComponent(int p_147944_0_, int p_147944_1_, int p_147944_2_, int p_147944_3_, int p_147944_4_) {
        float f2 = (float)Math.pow((float)(p_147944_0_ >> p_147944_4_ & 0xFF) / 255.0f, 2.2);
        float f1 = (float)Math.pow((float)(p_147944_1_ >> p_147944_4_ & 0xFF) / 255.0f, 2.2);
        float f22 = (float)Math.pow((float)(p_147944_2_ >> p_147944_4_ & 0xFF) / 255.0f, 2.2);
        float f3 = (float)Math.pow((float)(p_147944_3_ >> p_147944_4_ & 0xFF) / 255.0f, 2.2);
        float f4 = (float)Math.pow((double)(f2 + f1 + f22 + f3) * 0.25, 0.45454545454545453);
        return (int)((double)f4 * 255.0);
    }

    public static void uploadTextureMipmap(int[][] p_147955_0_, int p_147955_1_, int p_147955_2_, int p_147955_3_, int p_147955_4_, boolean p_147955_5_, boolean p_147955_6_) {
        for (int i2 = 0; i2 < p_147955_0_.length; ++i2) {
            int[] aint = p_147955_0_[i2];
            TextureUtil.uploadTextureSub(i2, aint, p_147955_1_ >> i2, p_147955_2_ >> i2, p_147955_3_ >> i2, p_147955_4_ >> i2, p_147955_5_, p_147955_6_, p_147955_0_.length > 1);
        }
    }

    private static void uploadTextureSub(int p_147947_0_, int[] p_147947_1_, int p_147947_2_, int p_147947_3_, int p_147947_4_, int p_147947_5_, boolean p_147947_6_, boolean p_147947_7_, boolean p_147947_8_) {
        int j2;
        int i2 = 0x400000 / p_147947_2_;
        TextureUtil.setTextureBlurMipmap(p_147947_6_, p_147947_8_);
        TextureUtil.setTextureClamped(p_147947_7_);
        for (int k2 = 0; k2 < p_147947_2_ * p_147947_3_; k2 += p_147947_2_ * j2) {
            int l2 = k2 / p_147947_2_;
            j2 = Math.min(i2, p_147947_3_ - l2);
            int i1 = p_147947_2_ * j2;
            TextureUtil.copyToBufferPos(p_147947_1_, k2, i1);
            GL11.glTexSubImage2D(3553, p_147947_0_, p_147947_4_, p_147947_5_ + l2, p_147947_2_, j2, 32993, 33639, dataBuffer);
        }
    }

    public static int uploadTextureImageAllocate(int p_110989_0_, BufferedImage p_110989_1_, boolean p_110989_2_, boolean p_110989_3_) {
        TextureUtil.allocateTexture(p_110989_0_, p_110989_1_.getWidth(), p_110989_1_.getHeight());
        return TextureUtil.uploadTextureImageSub(p_110989_0_, p_110989_1_, 0, 0, p_110989_2_, p_110989_3_);
    }

    public static void allocateTexture(int p_110991_0_, int p_110991_1_, int p_110991_2_) {
        TextureUtil.allocateTextureImpl(p_110991_0_, 0, p_110991_1_, p_110991_2_);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void allocateTextureImpl(int p_180600_0_, int p_180600_1_, int p_180600_2_, int p_180600_3_) {
        Class object = TextureUtil.class;
        if (Reflector.SplashScreen.exists()) {
            object = Reflector.SplashScreen.getTargetClass();
        }
        Class clazz = object;
        synchronized (clazz) {
            TextureUtil.deleteTexture(p_180600_0_);
            TextureUtil.bindTexture(p_180600_0_);
        }
        if (p_180600_1_ >= 0) {
            GL11.glTexParameteri(3553, 33085, p_180600_1_);
            GL11.glTexParameterf(3553, 33082, 0.0f);
            GL11.glTexParameterf(3553, 33083, p_180600_1_);
            GL11.glTexParameterf(3553, 34049, 0.0f);
        }
        for (int i2 = 0; i2 <= p_180600_1_; ++i2) {
            GL11.glTexImage2D(3553, i2, 6408, p_180600_2_ >> i2, p_180600_3_ >> i2, 0, 32993, 33639, (IntBuffer)null);
        }
    }

    public static int uploadTextureImageSub(int textureId, BufferedImage p_110995_1_, int p_110995_2_, int p_110995_3_, boolean p_110995_4_, boolean p_110995_5_) {
        TextureUtil.bindTexture(textureId);
        TextureUtil.uploadTextureImageSubImpl(p_110995_1_, p_110995_2_, p_110995_3_, p_110995_4_, p_110995_5_);
        return textureId;
    }

    private static void uploadTextureImageSubImpl(BufferedImage p_110993_0_, int p_110993_1_, int p_110993_2_, boolean p_110993_3_, boolean p_110993_4_) {
        int i2 = p_110993_0_.getWidth();
        int j2 = p_110993_0_.getHeight();
        int k2 = 0x400000 / i2;
        int[] aint = new int[k2 * i2];
        TextureUtil.setTextureBlurred(p_110993_3_);
        TextureUtil.setTextureClamped(p_110993_4_);
        for (int l2 = 0; l2 < i2 * j2; l2 += i2 * k2) {
            int i1 = l2 / i2;
            int j1 = Math.min(k2, j2 - i1);
            int k1 = i2 * j1;
            p_110993_0_.getRGB(0, i1, i2, j1, aint, 0, i2);
            TextureUtil.copyToBuffer(aint, k1);
            GL11.glTexSubImage2D(3553, 0, p_110993_1_, p_110993_2_ + i1, i2, j1, 32993, 33639, dataBuffer);
        }
    }

    public static void setTextureClamped(boolean p_110997_0_) {
        if (p_110997_0_) {
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
        } else {
            GL11.glTexParameteri(3553, 10242, 10497);
            GL11.glTexParameteri(3553, 10243, 10497);
        }
    }

    private static void setTextureBlurred(boolean p_147951_0_) {
        TextureUtil.setTextureBlurMipmap(p_147951_0_, false);
    }

    public static void setTextureBlurMipmap(boolean p_147954_0_, boolean p_147954_1_) {
        if (p_147954_0_) {
            GL11.glTexParameteri(3553, 10241, p_147954_1_ ? 9987 : 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
        } else {
            int i2 = Config.getMipmapType();
            GL11.glTexParameteri(3553, 10241, p_147954_1_ ? i2 : 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
        }
    }

    private static void copyToBuffer(int[] p_110990_0_, int p_110990_1_) {
        TextureUtil.copyToBufferPos(p_110990_0_, 0, p_110990_1_);
    }

    private static void copyToBufferPos(int[] p_110994_0_, int p_110994_1_, int p_110994_2_) {
        int[] aint = p_110994_0_;
        if (Minecraft.getMinecraft().gameSettings.anaglyph) {
            aint = TextureUtil.updateAnaglyph(p_110994_0_);
        }
        dataBuffer.clear();
        dataBuffer.put(aint, p_110994_1_, p_110994_2_);
        dataBuffer.position(0).limit(p_110994_2_);
    }

    static void bindTexture(int p_94277_0_) {
        GlStateManager.bindTexture(p_94277_0_);
    }

    public static int[] readImageData(IResourceManager resourceManager, ResourceLocation imageLocation) throws IOException {
        BufferedImage bufferedimage = TextureUtil.readBufferedImage(resourceManager.getResource(imageLocation).getInputStream());
        if (bufferedimage == null) {
            return null;
        }
        int i2 = bufferedimage.getWidth();
        int j2 = bufferedimage.getHeight();
        int[] aint = new int[i2 * j2];
        bufferedimage.getRGB(0, 0, i2, j2, aint, 0, i2);
        return aint;
    }

    public static BufferedImage readBufferedImage(InputStream imageStream) throws IOException {
        BufferedImage bufferedimage;
        if (imageStream == null) {
            return null;
        }
        try {
            bufferedimage = ImageIO.read(imageStream);
        }
        finally {
            IOUtils.closeQuietly(imageStream);
        }
        return bufferedimage;
    }

    public static int[] updateAnaglyph(int[] p_110985_0_) {
        int[] aint = new int[p_110985_0_.length];
        for (int i2 = 0; i2 < p_110985_0_.length; ++i2) {
            aint[i2] = TextureUtil.anaglyphColor(p_110985_0_[i2]);
        }
        return aint;
    }

    public static int anaglyphColor(int p_177054_0_) {
        int i2 = p_177054_0_ >> 24 & 0xFF;
        int j2 = p_177054_0_ >> 16 & 0xFF;
        int k2 = p_177054_0_ >> 8 & 0xFF;
        int l2 = p_177054_0_ & 0xFF;
        int i1 = (j2 * 30 + k2 * 59 + l2 * 11) / 100;
        int j1 = (j2 * 30 + k2 * 70) / 100;
        int k1 = (j2 * 30 + l2 * 70) / 100;
        return i2 << 24 | i1 << 16 | j1 << 8 | k1;
    }

    public static void saveGlTexture(String p_saveGlTexture_0_, int p_saveGlTexture_1_, int p_saveGlTexture_2_, int p_saveGlTexture_3_, int p_saveGlTexture_4_) {
        TextureUtil.bindTexture(p_saveGlTexture_1_);
        GL11.glPixelStorei(3333, 1);
        GL11.glPixelStorei(3317, 1);
        for (int i2 = 0; i2 <= p_saveGlTexture_2_; ++i2) {
            File file1 = new File(p_saveGlTexture_0_ + "_" + i2 + ".png");
            int j2 = p_saveGlTexture_3_ >> i2;
            int k2 = p_saveGlTexture_4_ >> i2;
            int l2 = j2 * k2;
            IntBuffer intbuffer = BufferUtils.createIntBuffer(l2);
            int[] aint = new int[l2];
            GL11.glGetTexImage(3553, i2, 32993, 33639, intbuffer);
            intbuffer.get(aint);
            BufferedImage bufferedimage = new BufferedImage(j2, k2, 2);
            bufferedimage.setRGB(0, 0, j2, k2, aint, 0, j2);
            try {
                ImageIO.write((RenderedImage)bufferedimage, "png", file1);
                logger.debug("Exported png to: {}", file1.getAbsolutePath());
                continue;
            }
            catch (Exception exception) {
                logger.debug("Unable to write: ", (Throwable)exception);
            }
        }
    }

    public static void processPixelValues(int[] p_147953_0_, int p_147953_1_, int p_147953_2_) {
        int[] aint = new int[p_147953_1_];
        int i2 = p_147953_2_ / 2;
        for (int j2 = 0; j2 < i2; ++j2) {
            System.arraycopy(p_147953_0_, j2 * p_147953_1_, aint, 0, p_147953_1_);
            System.arraycopy(p_147953_0_, (p_147953_2_ - 1 - j2) * p_147953_1_, p_147953_0_, j2 * p_147953_1_, p_147953_1_);
            System.arraycopy(aint, 0, p_147953_0_, (p_147953_2_ - 1 - j2) * p_147953_1_, p_147953_1_);
        }
    }

    static {
        int i2 = -16777216;
        int j2 = -524040;
        int[] aint = new int[]{-524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040};
        int[] aint1 = new int[]{-16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216};
        int k2 = aint.length;
        for (int l2 = 0; l2 < 16; ++l2) {
            System.arraycopy(l2 < k2 ? aint : aint1, 0, missingTextureData, 16 * l2, k2);
            System.arraycopy(l2 < k2 ? aint1 : aint, 0, missingTextureData, 16 * l2 + k2, k2);
        }
        missingTexture.updateDynamicTexture();
        mipmapBuffer = new int[4];
    }
}
