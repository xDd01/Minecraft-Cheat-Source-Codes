/*
 * Decompiled with CFR 0.152.
 */
package shadersmod.client;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.client.renderer.texture.Stitcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import optifine.Config;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import shadersmod.client.MultiTexID;
import shadersmod.client.Shaders;
import shadersmod.common.SMCLog;

public class ShadersTex {
    public static final int initialBufferSize = 0x100000;
    public static ByteBuffer byteBuffer = BufferUtils.createByteBuffer(0x400000);
    public static IntBuffer intBuffer = byteBuffer.asIntBuffer();
    public static int[] intArray = new int[0x100000];
    public static final int defBaseTexColor = 0;
    public static final int defNormTexColor = -8421377;
    public static final int defSpecTexColor = 0;
    public static Map<Integer, MultiTexID> multiTexMap = new HashMap<Integer, MultiTexID>();
    public static TextureMap updatingTextureMap = null;
    public static TextureAtlasSprite updatingSprite = null;
    public static MultiTexID updatingTex = null;
    public static MultiTexID boundTex = null;
    public static int updatingPage = 0;
    public static String iconName = null;
    public static IResourceManager resManager = null;
    static ResourceLocation resLocation = null;
    static int imageSize = 0;

    public static IntBuffer getIntBuffer(int size) {
        if (intBuffer.capacity() < size) {
            int i2 = ShadersTex.roundUpPOT(size);
            byteBuffer = BufferUtils.createByteBuffer(i2 * 4);
            intBuffer = byteBuffer.asIntBuffer();
        }
        return intBuffer;
    }

    public static int[] getIntArray(int size) {
        if (intArray == null) {
            intArray = new int[0x100000];
        }
        if (intArray.length < size) {
            intArray = new int[ShadersTex.roundUpPOT(size)];
        }
        return intArray;
    }

    public static int roundUpPOT(int x2) {
        int i2 = x2 - 1;
        i2 |= i2 >> 1;
        i2 |= i2 >> 2;
        i2 |= i2 >> 4;
        i2 |= i2 >> 8;
        i2 |= i2 >> 16;
        return i2 + 1;
    }

    public static int log2(int x2) {
        int i2 = 0;
        if ((x2 & 0xFFFF0000) != 0) {
            i2 += 16;
            x2 >>= 16;
        }
        if ((x2 & 0xFF00) != 0) {
            i2 += 8;
            x2 >>= 8;
        }
        if ((x2 & 0xF0) != 0) {
            i2 += 4;
            x2 >>= 4;
        }
        if ((x2 & 6) != 0) {
            i2 += 2;
            x2 >>= 2;
        }
        if ((x2 & 2) != 0) {
            ++i2;
        }
        return i2;
    }

    public static IntBuffer fillIntBuffer(int size, int value) {
        int[] aint = ShadersTex.getIntArray(size);
        IntBuffer intbuffer = ShadersTex.getIntBuffer(size);
        Arrays.fill(intArray, 0, size, value);
        intBuffer.put(intArray, 0, size);
        return intBuffer;
    }

    public static int[] createAIntImage(int size) {
        int[] aint = new int[size * 3];
        Arrays.fill(aint, 0, size, 0);
        Arrays.fill(aint, size, size * 2, -8421377);
        Arrays.fill(aint, size * 2, size * 3, 0);
        return aint;
    }

    public static int[] createAIntImage(int size, int color) {
        int[] aint = new int[size * 3];
        Arrays.fill(aint, 0, size, color);
        Arrays.fill(aint, size, size * 2, -8421377);
        Arrays.fill(aint, size * 2, size * 3, 0);
        return aint;
    }

    public static MultiTexID getMultiTexID(AbstractTexture tex) {
        MultiTexID multitexid = tex.multiTex;
        if (multitexid == null) {
            int i2 = tex.getGlTextureId();
            multitexid = multiTexMap.get(i2);
            if (multitexid == null) {
                multitexid = new MultiTexID(i2, GL11.glGenTextures(), GL11.glGenTextures());
                multiTexMap.put(i2, multitexid);
            }
            tex.multiTex = multitexid;
        }
        return multitexid;
    }

    public static void deleteTextures(AbstractTexture atex, int texid) {
        MultiTexID multitexid = atex.multiTex;
        if (multitexid != null) {
            atex.multiTex = null;
            multiTexMap.remove(multitexid.base);
            GlStateManager.deleteTexture(multitexid.norm);
            GlStateManager.deleteTexture(multitexid.spec);
            if (multitexid.base != texid) {
                SMCLog.warning("Error : MultiTexID.base mismatch: " + multitexid.base + ", texid: " + texid);
                GlStateManager.deleteTexture(multitexid.base);
            }
        }
    }

    public static void bindNSTextures(int normTex, int specTex) {
        if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
            GlStateManager.setActiveTexture(33986);
            GlStateManager.bindTexture(normTex);
            GlStateManager.setActiveTexture(33987);
            GlStateManager.bindTexture(specTex);
            GlStateManager.setActiveTexture(33984);
        }
    }

    public static void bindNSTextures(MultiTexID multiTex) {
        ShadersTex.bindNSTextures(multiTex.norm, multiTex.spec);
    }

    public static void bindTextures(int baseTex, int normTex, int specTex) {
        if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
            GlStateManager.setActiveTexture(33986);
            GlStateManager.bindTexture(normTex);
            GlStateManager.setActiveTexture(33987);
            GlStateManager.bindTexture(specTex);
            GlStateManager.setActiveTexture(33984);
        }
        GlStateManager.bindTexture(baseTex);
    }

    public static void bindTextures(MultiTexID multiTex) {
        boundTex = multiTex;
        if (Shaders.isRenderingWorld && GlStateManager.getActiveTextureUnit() == 33984) {
            if (Shaders.configNormalMap) {
                GlStateManager.setActiveTexture(33986);
                GlStateManager.bindTexture(multiTex.norm);
            }
            if (Shaders.configSpecularMap) {
                GlStateManager.setActiveTexture(33987);
                GlStateManager.bindTexture(multiTex.spec);
            }
            GlStateManager.setActiveTexture(33984);
        }
        GlStateManager.bindTexture(multiTex.base);
    }

    public static void bindTexture(ITextureObject tex) {
        int i2 = tex.getGlTextureId();
        if (tex instanceof TextureMap) {
            Shaders.atlasSizeX = ((TextureMap)tex).atlasWidth;
            Shaders.atlasSizeY = ((TextureMap)tex).atlasHeight;
            ShadersTex.bindTextures(tex.getMultiTexID());
        } else {
            Shaders.atlasSizeX = 0;
            Shaders.atlasSizeY = 0;
            ShadersTex.bindTextures(tex.getMultiTexID());
        }
    }

    public static void bindTextureMapForUpdateAndRender(TextureManager tm2, ResourceLocation resLoc) {
        TextureMap texturemap = (TextureMap)tm2.getTexture(resLoc);
        Shaders.atlasSizeX = texturemap.atlasWidth;
        Shaders.atlasSizeY = texturemap.atlasHeight;
        updatingTex = texturemap.getMultiTexID();
        ShadersTex.bindTextures(updatingTex);
    }

    public static void bindTextures(int baseTex) {
        MultiTexID multitexid = multiTexMap.get(baseTex);
        ShadersTex.bindTextures(multitexid);
    }

    public static void initDynamicTexture(int texID, int width, int height, DynamicTexture tex) {
        MultiTexID multitexid = tex.getMultiTexID();
        int[] aint = tex.getTextureData();
        int i2 = width * height;
        Arrays.fill(aint, i2, i2 * 2, -8421377);
        Arrays.fill(aint, i2 * 2, i2 * 3, 0);
        TextureUtil.allocateTexture(multitexid.base, width, height);
        TextureUtil.setTextureBlurMipmap(false, false);
        TextureUtil.setTextureClamped(false);
        TextureUtil.allocateTexture(multitexid.norm, width, height);
        TextureUtil.setTextureBlurMipmap(false, false);
        TextureUtil.setTextureClamped(false);
        TextureUtil.allocateTexture(multitexid.spec, width, height);
        TextureUtil.setTextureBlurMipmap(false, false);
        TextureUtil.setTextureClamped(false);
        GlStateManager.bindTexture(multitexid.base);
    }

    public static void updateDynamicTexture(int texID, int[] src, int width, int height, DynamicTexture tex) {
        MultiTexID multitexid = tex.getMultiTexID();
        GlStateManager.bindTexture(multitexid.base);
        ShadersTex.updateDynTexSubImage1(src, width, height, 0, 0, 0);
        GlStateManager.bindTexture(multitexid.norm);
        ShadersTex.updateDynTexSubImage1(src, width, height, 0, 0, 1);
        GlStateManager.bindTexture(multitexid.spec);
        ShadersTex.updateDynTexSubImage1(src, width, height, 0, 0, 2);
        GlStateManager.bindTexture(multitexid.base);
    }

    public static void updateDynTexSubImage1(int[] src, int width, int height, int posX, int posY, int page) {
        int i2 = width * height;
        IntBuffer intbuffer = ShadersTex.getIntBuffer(i2);
        intbuffer.clear();
        int j2 = page * i2;
        if (src.length >= j2 + i2) {
            intbuffer.put(src, j2, i2).position(0).limit(i2);
            GL11.glTexSubImage2D(3553, 0, posX, posY, width, height, 32993, 33639, intbuffer);
            intbuffer.clear();
        }
    }

    public static ITextureObject createDefaultTexture() {
        DynamicTexture dynamictexture = new DynamicTexture(1, 1);
        dynamictexture.getTextureData()[0] = -1;
        dynamictexture.updateDynamicTexture();
        return dynamictexture;
    }

    public static void allocateTextureMap(int texID, int mipmapLevels, int width, int height, Stitcher stitcher, TextureMap tex) {
        MultiTexID multitexid;
        SMCLog.info("allocateTextureMap " + mipmapLevels + " " + width + " " + height + " ");
        updatingTextureMap = tex;
        tex.atlasWidth = width;
        tex.atlasHeight = height;
        updatingTex = multitexid = ShadersTex.getMultiTexID(tex);
        TextureUtil.allocateTextureImpl(multitexid.base, mipmapLevels, width, height);
        if (Shaders.configNormalMap) {
            TextureUtil.allocateTextureImpl(multitexid.norm, mipmapLevels, width, height);
        }
        if (Shaders.configSpecularMap) {
            TextureUtil.allocateTextureImpl(multitexid.spec, mipmapLevels, width, height);
        }
        GlStateManager.bindTexture(texID);
    }

    public static TextureAtlasSprite setSprite(TextureAtlasSprite tas) {
        updatingSprite = tas;
        return tas;
    }

    public static String setIconName(String name) {
        iconName = name;
        return name;
    }

    public static void uploadTexSubForLoadAtlas(int[][] data, int width, int height, int xoffset, int yoffset, boolean linear, boolean clamp) {
        TextureUtil.uploadTextureMipmap(data, width, height, xoffset, yoffset, linear, clamp);
        boolean flag = false;
        if (Shaders.configNormalMap) {
            int[][] aint = ShadersTex.readImageAndMipmaps(iconName + "_n", width, height, data.length, flag, -8421377);
            GlStateManager.bindTexture(ShadersTex.updatingTex.norm);
            TextureUtil.uploadTextureMipmap(aint, width, height, xoffset, yoffset, linear, clamp);
        }
        if (Shaders.configSpecularMap) {
            int[][] aint1 = ShadersTex.readImageAndMipmaps(iconName + "_s", width, height, data.length, flag, 0);
            GlStateManager.bindTexture(ShadersTex.updatingTex.spec);
            TextureUtil.uploadTextureMipmap(aint1, width, height, xoffset, yoffset, linear, clamp);
        }
        GlStateManager.bindTexture(ShadersTex.updatingTex.base);
    }

    public static int[][] readImageAndMipmaps(String name, int width, int height, int numLevels, boolean border, int defColor) {
        Object aint = new int[numLevels][];
        int[] aint1 = new int[width * height];
        aint[0] = aint1;
        boolean flag = false;
        BufferedImage bufferedimage = ShadersTex.readImage(updatingTextureMap.completeResourceLocation(new ResourceLocation(name), 0));
        if (bufferedimage != null) {
            int i2 = bufferedimage.getWidth();
            int j2 = bufferedimage.getHeight();
            if (i2 + (border ? 16 : 0) == width) {
                flag = true;
                bufferedimage.getRGB(0, 0, i2, i2, aint1, 0, i2);
            }
        }
        if (!flag) {
            Arrays.fill(aint1, defColor);
        }
        GlStateManager.bindTexture(ShadersTex.updatingTex.spec);
        aint = ShadersTex.genMipmapsSimple(((int[][])aint).length - 1, width, aint);
        return aint;
    }

    public static BufferedImage readImage(ResourceLocation resLoc) {
        try {
            if (!Config.hasResource(resLoc)) {
                return null;
            }
            InputStream inputstream = Config.getResourceStream(resLoc);
            if (inputstream == null) {
                return null;
            }
            BufferedImage bufferedimage = ImageIO.read(inputstream);
            inputstream.close();
            return bufferedimage;
        }
        catch (IOException var3) {
            return null;
        }
    }

    public static int[][] genMipmapsSimple(int maxLevel, int width, int[][] data) {
        for (int i2 = 1; i2 <= maxLevel; ++i2) {
            if (data[i2] != null) continue;
            int j2 = width >> i2;
            int k2 = j2 * 2;
            int[] aint = data[i2 - 1];
            data[i2] = new int[j2 * j2];
            int[] aint1 = data[i2];
            for (int i1 = 0; i1 < j2; ++i1) {
                for (int l2 = 0; l2 < j2; ++l2) {
                    int j1 = i1 * 2 * k2 + l2 * 2;
                    aint1[i1 * j2 + l2] = ShadersTex.blend4Simple(aint[j1], aint[j1 + 1], aint[j1 + k2], aint[j1 + k2 + 1]);
                }
            }
        }
        return data;
    }

    public static void uploadTexSub(int[][] data, int width, int height, int xoffset, int yoffset, boolean linear, boolean clamp) {
        TextureUtil.uploadTextureMipmap(data, width, height, xoffset, yoffset, linear, clamp);
        if (Shaders.configNormalMap || Shaders.configSpecularMap) {
            if (Shaders.configNormalMap) {
                GlStateManager.bindTexture(ShadersTex.updatingTex.norm);
                ShadersTex.uploadTexSub1(data, width, height, xoffset, yoffset, 1);
            }
            if (Shaders.configSpecularMap) {
                GlStateManager.bindTexture(ShadersTex.updatingTex.spec);
                ShadersTex.uploadTexSub1(data, width, height, xoffset, yoffset, 2);
            }
            GlStateManager.bindTexture(ShadersTex.updatingTex.base);
        }
    }

    public static void uploadTexSub1(int[][] src, int width, int height, int posX, int posY, int page) {
        int i2 = width * height;
        IntBuffer intbuffer = ShadersTex.getIntBuffer(i2);
        int j2 = src.length;
        int k2 = 0;
        int l2 = width;
        int i1 = height;
        int j1 = posX;
        int k1 = posY;
        while (l2 > 0 && i1 > 0 && k2 < j2) {
            int l1 = l2 * i1;
            int[] aint = src[k2];
            intbuffer.clear();
            if (aint.length >= l1 * (page + 1)) {
                intbuffer.put(aint, l1 * page, l1).position(0).limit(l1);
                GL11.glTexSubImage2D(3553, k2, j1, k1, l2, i1, 32993, 33639, intbuffer);
            }
            l2 >>= 1;
            i1 >>= 1;
            j1 >>= 1;
            k1 >>= 1;
            ++k2;
        }
        intbuffer.clear();
    }

    public static int blend4Alpha(int c0, int c1, int c2, int c3) {
        int k1;
        int i2 = c0 >>> 24 & 0xFF;
        int j2 = c1 >>> 24 & 0xFF;
        int k2 = c2 >>> 24 & 0xFF;
        int l2 = c3 >>> 24 & 0xFF;
        int i1 = i2 + j2 + k2 + l2;
        int j1 = (i1 + 2) / 4;
        if (i1 != 0) {
            k1 = i1;
        } else {
            k1 = 4;
            i2 = 1;
            j2 = 1;
            k2 = 1;
            l2 = 1;
        }
        int l1 = (k1 + 1) / 2;
        int i22 = j1 << 24 | ((c0 >>> 16 & 0xFF) * i2 + (c1 >>> 16 & 0xFF) * j2 + (c2 >>> 16 & 0xFF) * k2 + (c3 >>> 16 & 0xFF) * l2 + l1) / k1 << 16 | ((c0 >>> 8 & 0xFF) * i2 + (c1 >>> 8 & 0xFF) * j2 + (c2 >>> 8 & 0xFF) * k2 + (c3 >>> 8 & 0xFF) * l2 + l1) / k1 << 8 | ((c0 >>> 0 & 0xFF) * i2 + (c1 >>> 0 & 0xFF) * j2 + (c2 >>> 0 & 0xFF) * k2 + (c3 >>> 0 & 0xFF) * l2 + l1) / k1 << 0;
        return i22;
    }

    public static int blend4Simple(int c0, int c1, int c2, int c3) {
        int i2 = ((c0 >>> 24 & 0xFF) + (c1 >>> 24 & 0xFF) + (c2 >>> 24 & 0xFF) + (c3 >>> 24 & 0xFF) + 2) / 4 << 24 | ((c0 >>> 16 & 0xFF) + (c1 >>> 16 & 0xFF) + (c2 >>> 16 & 0xFF) + (c3 >>> 16 & 0xFF) + 2) / 4 << 16 | ((c0 >>> 8 & 0xFF) + (c1 >>> 8 & 0xFF) + (c2 >>> 8 & 0xFF) + (c3 >>> 8 & 0xFF) + 2) / 4 << 8 | ((c0 >>> 0 & 0xFF) + (c1 >>> 0 & 0xFF) + (c2 >>> 0 & 0xFF) + (c3 >>> 0 & 0xFF) + 2) / 4 << 0;
        return i2;
    }

    public static void genMipmapAlpha(int[] aint, int offset, int width, int height) {
        Math.min(width, height);
        int o2 = offset;
        int w2 = width;
        int h2 = height;
        int o1 = 0;
        int w1 = 0;
        int h1 = 0;
        int i2 = 0;
        while (w2 > 1 && h2 > 1) {
            o1 = o2 + w2 * h2;
            w1 = w2 / 2;
            h1 = h2 / 2;
            for (int l1 = 0; l1 < h1; ++l1) {
                int i22 = o1 + l1 * w1;
                int j2 = o2 + l1 * 2 * w2;
                for (int k2 = 0; k2 < w1; ++k2) {
                    aint[i22 + k2] = ShadersTex.blend4Alpha(aint[j2 + k2 * 2], aint[j2 + k2 * 2 + 1], aint[j2 + w2 + k2 * 2], aint[j2 + w2 + k2 * 2 + 1]);
                }
            }
            ++i2;
            w2 = w1;
            h2 = h1;
            o2 = o1;
        }
        while (i2 > 0) {
            w2 = width >> --i2;
            h2 = height >> i2;
            int l2 = o2 = o1 - w2 * h2;
            for (int i3 = 0; i3 < h2; ++i3) {
                for (int j3 = 0; j3 < w2; ++j3) {
                    if (aint[l2] == 0) {
                        aint[l2] = aint[o1 + i3 / 2 * w1 + j3 / 2] & 0xFFFFFF;
                    }
                    ++l2;
                }
            }
            o1 = o2;
            w1 = w2;
        }
    }

    public static void genMipmapSimple(int[] aint, int offset, int width, int height) {
        Math.min(width, height);
        int o2 = offset;
        int w2 = width;
        int h2 = height;
        int o1 = 0;
        int w1 = 0;
        int h1 = 0;
        int i2 = 0;
        while (w2 > 1 && h2 > 1) {
            o1 = o2 + w2 * h2;
            w1 = w2 / 2;
            h1 = h2 / 2;
            for (int l1 = 0; l1 < h1; ++l1) {
                int i22 = o1 + l1 * w1;
                int j2 = o2 + l1 * 2 * w2;
                for (int k2 = 0; k2 < w1; ++k2) {
                    aint[i22 + k2] = ShadersTex.blend4Simple(aint[j2 + k2 * 2], aint[j2 + k2 * 2 + 1], aint[j2 + w2 + k2 * 2], aint[j2 + w2 + k2 * 2 + 1]);
                }
            }
            ++i2;
            w2 = w1;
            h2 = h1;
            o2 = o1;
        }
        while (i2 > 0) {
            w2 = width >> --i2;
            h2 = height >> i2;
            int l2 = o2 = o1 - w2 * h2;
            for (int i3 = 0; i3 < h2; ++i3) {
                for (int j3 = 0; j3 < w2; ++j3) {
                    if (aint[l2] == 0) {
                        aint[l2] = aint[o1 + i3 / 2 * w1 + j3 / 2] & 0xFFFFFF;
                    }
                    ++l2;
                }
            }
            o1 = o2;
            w1 = w2;
        }
    }

    public static boolean isSemiTransparent(int[] aint, int width, int height) {
        int i2 = width * height;
        if (aint[0] >>> 24 == 255 && aint[i2 - 1] == 0) {
            return true;
        }
        for (int j2 = 0; j2 < i2; ++j2) {
            int k2 = aint[j2] >>> 24;
            if (k2 == 0 || k2 == 255) continue;
            return true;
        }
        return false;
    }

    public static void updateSubTex1(int[] src, int width, int height, int posX, int posY) {
        int i2 = 0;
        int j2 = width;
        int k2 = height;
        int l2 = posX;
        int i1 = posY;
        while (j2 > 0 && k2 > 0) {
            GL11.glCopyTexSubImage2D(3553, i2, l2, i1, 0, 0, j2, k2);
            ++i2;
            j2 /= 2;
            k2 /= 2;
            l2 /= 2;
            i1 /= 2;
        }
    }

    public static void setupTexture(MultiTexID multiTex, int[] src, int width, int height, boolean linear, boolean clamp) {
        int i2 = linear ? 9729 : 9728;
        int j2 = clamp ? 10496 : 10497;
        int k2 = width * height;
        IntBuffer intbuffer = ShadersTex.getIntBuffer(k2);
        intbuffer.clear();
        intbuffer.put(src, 0, k2).position(0).limit(k2);
        GlStateManager.bindTexture(multiTex.base);
        GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, intbuffer);
        GL11.glTexParameteri(3553, 10241, i2);
        GL11.glTexParameteri(3553, 10240, i2);
        GL11.glTexParameteri(3553, 10242, j2);
        GL11.glTexParameteri(3553, 10243, j2);
        intbuffer.put(src, k2, k2).position(0).limit(k2);
        GlStateManager.bindTexture(multiTex.norm);
        GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, intbuffer);
        GL11.glTexParameteri(3553, 10241, i2);
        GL11.glTexParameteri(3553, 10240, i2);
        GL11.glTexParameteri(3553, 10242, j2);
        GL11.glTexParameteri(3553, 10243, j2);
        intbuffer.put(src, k2 * 2, k2).position(0).limit(k2);
        GlStateManager.bindTexture(multiTex.spec);
        GL11.glTexImage2D(3553, 0, 6408, width, height, 0, 32993, 33639, intbuffer);
        GL11.glTexParameteri(3553, 10241, i2);
        GL11.glTexParameteri(3553, 10240, i2);
        GL11.glTexParameteri(3553, 10242, j2);
        GL11.glTexParameteri(3553, 10243, j2);
        GlStateManager.bindTexture(multiTex.base);
    }

    public static void updateSubImage(MultiTexID multiTex, int[] src, int width, int height, int posX, int posY, boolean linear, boolean clamp) {
        int i2 = width * height;
        IntBuffer intbuffer = ShadersTex.getIntBuffer(i2);
        intbuffer.clear();
        intbuffer.put(src, 0, i2);
        intbuffer.position(0).limit(i2);
        GlStateManager.bindTexture(multiTex.base);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexParameteri(3553, 10242, 10497);
        GL11.glTexParameteri(3553, 10243, 10497);
        GL11.glTexSubImage2D(3553, 0, posX, posY, width, height, 32993, 33639, intbuffer);
        if (src.length == i2 * 3) {
            intbuffer.clear();
            intbuffer.put(src, i2, i2).position(0);
            intbuffer.position(0).limit(i2);
        }
        GlStateManager.bindTexture(multiTex.norm);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexParameteri(3553, 10242, 10497);
        GL11.glTexParameteri(3553, 10243, 10497);
        GL11.glTexSubImage2D(3553, 0, posX, posY, width, height, 32993, 33639, intbuffer);
        if (src.length == i2 * 3) {
            intbuffer.clear();
            intbuffer.put(src, i2 * 2, i2);
            intbuffer.position(0).limit(i2);
        }
        GlStateManager.bindTexture(multiTex.spec);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glTexParameteri(3553, 10242, 10497);
        GL11.glTexParameteri(3553, 10243, 10497);
        GL11.glTexSubImage2D(3553, 0, posX, posY, width, height, 32993, 33639, intbuffer);
        GlStateManager.setActiveTexture(33984);
    }

    public static ResourceLocation getNSMapLocation(ResourceLocation location, String mapName) {
        String s2 = location.getResourcePath();
        String[] astring = s2.split(".png");
        String s1 = astring[0];
        return new ResourceLocation(location.getResourceDomain(), s1 + "_" + mapName + ".png");
    }

    public static void loadNSMap(IResourceManager manager, ResourceLocation location, int width, int height, int[] aint) {
        if (Shaders.configNormalMap) {
            ShadersTex.loadNSMap1(manager, ShadersTex.getNSMapLocation(location, "n"), width, height, aint, width * height, -8421377);
        }
        if (Shaders.configSpecularMap) {
            ShadersTex.loadNSMap1(manager, ShadersTex.getNSMapLocation(location, "s"), width, height, aint, width * height * 2, 0);
        }
    }

    public static void loadNSMap1(IResourceManager manager, ResourceLocation location, int width, int height, int[] aint, int offset, int defaultColor) {
        boolean flag = false;
        try {
            IResource iresource = manager.getResource(location);
            BufferedImage bufferedimage = ImageIO.read(iresource.getInputStream());
            if (bufferedimage != null && bufferedimage.getWidth() == width && bufferedimage.getHeight() == height) {
                bufferedimage.getRGB(0, 0, width, height, aint, offset, width);
                flag = true;
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        if (!flag) {
            Arrays.fill(aint, offset, offset + width * height, defaultColor);
        }
    }

    public static int loadSimpleTexture(int textureID, BufferedImage bufferedimage, boolean linear, boolean clamp, IResourceManager resourceManager, ResourceLocation location, MultiTexID multiTex) {
        int i2 = bufferedimage.getWidth();
        int j2 = bufferedimage.getHeight();
        int k2 = i2 * j2;
        int[] aint = ShadersTex.getIntArray(k2 * 3);
        bufferedimage.getRGB(0, 0, i2, j2, aint, 0, i2);
        ShadersTex.loadNSMap(resourceManager, location, i2, j2, aint);
        ShadersTex.setupTexture(multiTex, aint, i2, j2, linear, clamp);
        return textureID;
    }

    public static void mergeImage(int[] aint, int dstoff, int srcoff, int size) {
    }

    public static int blendColor(int color1, int color2, int factor1) {
        int i2 = 255 - factor1;
        return ((color1 >>> 24 & 0xFF) * factor1 + (color2 >>> 24 & 0xFF) * i2) / 255 << 24 | ((color1 >>> 16 & 0xFF) * factor1 + (color2 >>> 16 & 0xFF) * i2) / 255 << 16 | ((color1 >>> 8 & 0xFF) * factor1 + (color2 >>> 8 & 0xFF) * i2) / 255 << 8 | ((color1 >>> 0 & 0xFF) * factor1 + (color2 >>> 0 & 0xFF) * i2) / 255 << 0;
    }

    public static void loadLayeredTexture(LayeredTexture tex, IResourceManager manager, List list) {
        int i2 = 0;
        int j2 = 0;
        int k2 = 0;
        int[] aint = null;
        for (Object s2 : list) {
            if (s2 == null) continue;
            try {
                ResourceLocation resourcelocation = new ResourceLocation((String)s2);
                InputStream inputstream = manager.getResource(resourcelocation).getInputStream();
                BufferedImage bufferedimage = ImageIO.read(inputstream);
                if (k2 == 0) {
                    i2 = bufferedimage.getWidth();
                    j2 = bufferedimage.getHeight();
                    k2 = i2 * j2;
                    aint = ShadersTex.createAIntImage(k2, 0);
                }
                int[] aint1 = ShadersTex.getIntArray(k2 * 3);
                bufferedimage.getRGB(0, 0, i2, j2, aint1, 0, i2);
                ShadersTex.loadNSMap(manager, resourcelocation, i2, j2, aint1);
                for (int l2 = 0; l2 < k2; ++l2) {
                    int i1 = aint1[l2] >>> 24 & 0xFF;
                    aint[k2 * 0 + l2] = ShadersTex.blendColor(aint1[k2 * 0 + l2], aint[k2 * 0 + l2], i1);
                    aint[k2 * 1 + l2] = ShadersTex.blendColor(aint1[k2 * 1 + l2], aint[k2 * 1 + l2], i1);
                    aint[k2 * 2 + l2] = ShadersTex.blendColor(aint1[k2 * 2 + l2], aint[k2 * 2 + l2], i1);
                }
            }
            catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }
        ShadersTex.setupTexture(tex.getMultiTexID(), aint, i2, j2, false, false);
    }

    static void updateTextureMinMagFilter() {
        TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject itextureobject = texturemanager.getTexture(TextureMap.locationBlocksTexture);
        if (itextureobject != null) {
            MultiTexID multitexid = itextureobject.getMultiTexID();
            GlStateManager.bindTexture(multitexid.base);
            GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilB]);
            GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilB]);
            GlStateManager.bindTexture(multitexid.norm);
            GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilN]);
            GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilN]);
            GlStateManager.bindTexture(multitexid.spec);
            GL11.glTexParameteri(3553, 10241, Shaders.texMinFilValue[Shaders.configTexMinFilS]);
            GL11.glTexParameteri(3553, 10240, Shaders.texMagFilValue[Shaders.configTexMagFilS]);
            GlStateManager.bindTexture(0);
        }
    }

    public static IResource loadResource(IResourceManager manager, ResourceLocation location) throws IOException {
        resManager = manager;
        resLocation = location;
        return manager.getResource(location);
    }

    public static int[] loadAtlasSprite(BufferedImage bufferedimage, int startX, int startY, int w2, int h2, int[] aint, int offset, int scansize) {
        imageSize = w2 * h2;
        bufferedimage.getRGB(startX, startY, w2, h2, aint, offset, scansize);
        ShadersTex.loadNSMap(resManager, resLocation, w2, h2, aint);
        return aint;
    }

    public static int[][] getFrameTexData(int[][] src, int width, int height, int frameIndex) {
        int i2 = src.length;
        int[][] aint = new int[i2][];
        for (int j2 = 0; j2 < i2; ++j2) {
            int[] aint1 = src[j2];
            if (aint1 == null) continue;
            int k2 = (width >> j2) * (height >> j2);
            int[] aint2 = new int[k2 * 3];
            aint[j2] = aint2;
            int l2 = aint1.length / 3;
            int i1 = k2 * frameIndex;
            int j1 = 0;
            System.arraycopy(aint1, i1, aint2, j1, k2);
            System.arraycopy(aint1, i1 += l2, aint2, j1 += k2, k2);
            System.arraycopy(aint1, i1 += l2, aint2, j1 += k2, k2);
        }
        return aint;
    }

    public static int[][] prepareAF(TextureAtlasSprite tas, int[][] src, int width, int height) {
        boolean flag = true;
        return src;
    }

    public static void fixTransparentColor(TextureAtlasSprite tas, int[] aint) {
    }
}

