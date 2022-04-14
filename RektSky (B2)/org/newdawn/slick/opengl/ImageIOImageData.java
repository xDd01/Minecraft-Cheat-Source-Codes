package org.newdawn.slick.opengl;

import java.io.*;
import javax.imageio.*;
import java.util.*;
import java.awt.*;
import java.nio.*;
import java.awt.color.*;
import java.awt.image.*;

public class ImageIOImageData implements LoadableImageData
{
    private static final ColorModel glColorModelRGBA;
    private static final ColorModel glColorModelRGB;
    private static final ColorModel glColorModelGRAYALPHA;
    private static final ColorModel glColorModelGRAY;
    private ImageData.Format format;
    private int height;
    private int width;
    private int texWidth;
    private int texHeight;
    private boolean edging;
    
    public ImageIOImageData() {
        this.edging = true;
    }
    
    @Override
    public ImageData.Format getFormat() {
        return this.format;
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public int getTexHeight() {
        return this.texHeight;
    }
    
    @Override
    public int getTexWidth() {
        return this.texWidth;
    }
    
    @Override
    public int getWidth() {
        return this.width;
    }
    
    @Override
    public ByteBuffer loadImage(final InputStream fis) throws IOException {
        return this.loadImage(fis, true, null);
    }
    
    @Override
    public ByteBuffer loadImage(final InputStream fis, final boolean flipped, final int[] transparent) throws IOException {
        return this.loadImage(fis, flipped, false, transparent);
    }
    
    @Override
    public ByteBuffer loadImage(final InputStream fis, final boolean flipped, boolean forceAlpha, final int[] transparent) throws IOException {
        if (transparent != null) {
            forceAlpha = true;
        }
        final BufferedImage bufferedImage = ImageIO.read(fis);
        return this.imageToByteBuffer(bufferedImage, flipped, forceAlpha, transparent);
    }
    
    public ByteBuffer imageToByteBuffer(final BufferedImage image, final boolean flipped, final boolean forceAlpha, final int[] transparent) {
        ByteBuffer imageBuffer = null;
        int texWidth = 2;
        int texHeight = 2;
        while (texWidth < image.getWidth()) {
            texWidth *= 2;
        }
        while (texHeight < image.getHeight()) {
            texHeight *= 2;
        }
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.texHeight = texHeight;
        this.texWidth = texWidth;
        final boolean useAlpha = image.getColorModel().hasAlpha() || forceAlpha;
        final boolean isRGB = image.getColorModel().getNumColorComponents() == 3;
        ColorModel usedModel;
        if (isRGB) {
            if (useAlpha) {
                usedModel = ImageIOImageData.glColorModelRGBA;
                this.format = ImageData.Format.RGBA;
            }
            else {
                usedModel = ImageIOImageData.glColorModelRGB;
                this.format = ImageData.Format.RGB;
            }
        }
        else if (useAlpha) {
            usedModel = ImageIOImageData.glColorModelGRAYALPHA;
            this.format = ImageData.Format.GRAYALPHA;
        }
        else {
            usedModel = ImageIOImageData.glColorModelGRAY;
            this.format = ImageData.Format.GRAY;
        }
        final WritableRaster raster = Raster.createInterleavedRaster(0, texWidth, texHeight, this.format.getColorComponents(), null);
        final BufferedImage texImage = new BufferedImage(usedModel, raster, false, new Hashtable<Object, Object>());
        final Graphics2D g = (Graphics2D)texImage.getGraphics();
        if (useAlpha) {
            g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
            g.fillRect(0, 0, texWidth, texHeight);
        }
        if (flipped) {
            g.scale(1.0, -1.0);
            g.drawImage(image, 0, -this.height, null);
        }
        else {
            g.drawImage(image, 0, 0, null);
        }
        if (this.edging) {
            if (this.height < texHeight - 1) {
                this.copyArea(texImage, 0, 0, this.width, 1, 0, texHeight - 1);
                this.copyArea(texImage, 0, this.height - 1, this.width, 1, 0, 1);
            }
            if (this.width < texWidth - 1) {
                this.copyArea(texImage, 0, 0, 1, this.height, texWidth - 1, 0);
                this.copyArea(texImage, this.width - 1, 0, 1, this.height, 1, 0);
            }
        }
        final byte[] data = ((DataBufferByte)texImage.getRaster().getDataBuffer()).getData();
        if (!this.format.hasAlpha() && transparent != null) {
            for (int components = this.format.getColorComponents(), size = texWidth * texHeight * components, i = 0; i < size; i += components) {
                boolean match = true;
                for (int c = 0; c < components; ++c) {
                    if (this.toInt(data[i + c]) != transparent[c]) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    data[i + components] = 0;
                }
            }
        }
        imageBuffer = ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.flip();
        g.dispose();
        return imageBuffer;
    }
    
    private int toInt(final byte b) {
        if (b < 0) {
            return 256 + b;
        }
        return b;
    }
    
    @Override
    public ByteBuffer getImageBufferData() {
        throw new RuntimeException("ImageIOImageData doesn't store it's image.");
    }
    
    private void copyArea(final BufferedImage image, final int x, final int y, final int width, final int height, final int dx, final int dy) {
        final Graphics2D g = (Graphics2D)image.getGraphics();
        g.drawImage(image.getSubimage(x, y, width, height), x + dx, y + dy, null);
    }
    
    @Override
    public void configureEdging(final boolean edging) {
        this.edging = edging;
    }
    
    static {
        glColorModelRGBA = new ComponentColorModel(ColorSpace.getInstance(1000), new int[] { 8, 8, 8, 8 }, true, false, 3, 0);
        glColorModelRGB = new ComponentColorModel(ColorSpace.getInstance(1000), new int[] { 8, 8, 8, 0 }, false, false, 1, 0);
        glColorModelGRAYALPHA = new ComponentColorModel(ColorSpace.getInstance(1003), new int[] { 8, 8, 0, 0 }, true, false, 3, 0);
        glColorModelGRAY = new ComponentColorModel(ColorSpace.getInstance(1003), new int[] { 8, 0, 0, 0 }, false, false, 1, 0);
    }
}
