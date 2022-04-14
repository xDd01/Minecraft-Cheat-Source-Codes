package org.newdawn.slick.imageout;

import java.nio.*;
import java.awt.color.*;
import java.awt.*;
import java.util.*;
import javax.imageio.*;
import org.newdawn.slick.*;
import java.awt.image.*;
import java.io.*;

public class ImageIOWriter implements ImageWriter
{
    @Override
    public void saveImage(final Image image, final String format, final OutputStream output, final boolean hasAlpha) throws IOException {
        int len = 4 * image.getWidth() * image.getHeight();
        if (!hasAlpha) {
            len = 3 * image.getWidth() * image.getHeight();
        }
        final ByteBuffer out = ByteBuffer.allocate(len);
        for (int y = 0; y < image.getHeight(); ++y) {
            for (int x = 0; x < image.getWidth(); ++x) {
                final Color c = image.getColor(x, y);
                out.put((byte)(c.r * 255.0f));
                out.put((byte)(c.g * 255.0f));
                out.put((byte)(c.b * 255.0f));
                if (hasAlpha) {
                    out.put((byte)(c.a * 255.0f));
                }
            }
        }
        final DataBufferByte dataBuffer = new DataBufferByte(out.array(), len);
        PixelInterleavedSampleModel sampleModel;
        ColorModel cm;
        if (hasAlpha) {
            final int[] offsets = { 0, 1, 2, 3 };
            sampleModel = new PixelInterleavedSampleModel(0, image.getWidth(), image.getHeight(), 4, 4 * image.getWidth(), offsets);
            cm = new ComponentColorModel(ColorSpace.getInstance(1000), new int[] { 8, 8, 8, 8 }, true, false, 3, 0);
        }
        else {
            final int[] offsets = { 0, 1, 2 };
            sampleModel = new PixelInterleavedSampleModel(0, image.getWidth(), image.getHeight(), 3, 3 * image.getWidth(), offsets);
            cm = new ComponentColorModel(ColorSpace.getInstance(1000), new int[] { 8, 8, 8, 0 }, false, false, 1, 0);
        }
        final WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, new Point(0, 0));
        final BufferedImage img = new BufferedImage(cm, raster, false, null);
        ImageIO.write(img, format, output);
    }
}
