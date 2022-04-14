/*
 * Decompiled with CFR 0.152.
 */
package optifine;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FilenameUtils;

public class CapeUtils {
    public static void downloadCape(final AbstractClientPlayer p_downloadCape_0_) {
        String s2 = p_downloadCape_0_.getNameClear();
        if (s2 != null && !s2.isEmpty()) {
            String s1 = "http://s.optifine.net/capes/" + s2 + ".png";
            String s22 = FilenameUtils.getBaseName(s1);
            final ResourceLocation resourcelocation = new ResourceLocation("capeof/" + s22);
            TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
            ITextureObject itextureobject = texturemanager.getTexture(resourcelocation);
            if (itextureobject != null && itextureobject instanceof ThreadDownloadImageData) {
                ThreadDownloadImageData threaddownloadimagedata = (ThreadDownloadImageData)itextureobject;
                if (threaddownloadimagedata.imageFound != null) {
                    if (threaddownloadimagedata.imageFound.booleanValue()) {
                        p_downloadCape_0_.setLocationOfCape(resourcelocation);
                    }
                    return;
                }
            }
            IImageBuffer iimagebuffer = new IImageBuffer(){
                ImageBufferDownload ibd = new ImageBufferDownload();

                @Override
                public BufferedImage parseUserSkin(BufferedImage image) {
                    return CapeUtils.parseCape(image);
                }

                @Override
                public void skinAvailable() {
                    p_downloadCape_0_.setLocationOfCape(resourcelocation);
                }
            };
            ThreadDownloadImageData threaddownloadimagedata1 = new ThreadDownloadImageData(null, s1, null, iimagebuffer);
            threaddownloadimagedata1.pipeline = true;
            texturemanager.loadTexture(resourcelocation, threaddownloadimagedata1);
        }
    }

    public static BufferedImage parseCape(BufferedImage p_parseCape_0_) {
        int j2;
        int i2 = 64;
        int k2 = p_parseCape_0_.getWidth();
        int l2 = p_parseCape_0_.getHeight();
        for (j2 = 32; i2 < k2 || j2 < l2; i2 *= 2, j2 *= 2) {
        }
        BufferedImage bufferedimage = new BufferedImage(i2, j2, 2);
        Graphics graphics = bufferedimage.getGraphics();
        graphics.drawImage(p_parseCape_0_, 0, 0, null);
        graphics.dispose();
        return bufferedimage;
    }
}

