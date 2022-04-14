package org.newdawn.slick.opengl;

import org.lwjgl.input.*;
import java.io.*;
import org.newdawn.slick.util.*;
import org.lwjgl.*;
import java.nio.*;

public class CursorLoader
{
    private static CursorLoader single;
    private float transparencyThreshold;
    
    public static CursorLoader get() {
        return CursorLoader.single;
    }
    
    private CursorLoader() {
        this.transparencyThreshold = 0.8f;
    }
    
    public float getTransparencyThreshold() {
        return this.transparencyThreshold;
    }
    
    public void setTransparencyThreshold(final float value) {
        if (value < 0.0f || value > 1.0f) {
            throw new IllegalArgumentException("Value is outside of valid range.");
        }
        this.transparencyThreshold = value;
    }
    
    private byte applyThreshold(final byte alpha) {
        int value = alpha;
        if (value < 0) {
            value += 256;
        }
        if (value > 256.0f * this.transparencyThreshold) {
            return -1;
        }
        return 0;
    }
    
    public Cursor getCursor(final String ref, final int x, final int y) throws IOException, LWJGLException {
        LoadableImageData imageData = null;
        imageData = ImageDataFactory.getImageDataFor(ref);
        imageData.configureEdging(false);
        final ByteBuffer buff = imageData.loadImage(ResourceLoader.getResourceAsStream(ref), true, true, null);
        return this.getCursor(buff, x, y, imageData.getWidth(), imageData.getHeight());
    }
    
    public Cursor getCursor(final ByteBuffer buf, final int x, final int y, final int width, final int height) throws IOException, LWJGLException {
        return this.getCursor(buf, x, y, width, height, width, height);
    }
    
    public Cursor getCursor(ByteBuffer buf, final int x, final int y, final int width, final int height, final int imageWidth, final int imageHeight) throws IOException, LWJGLException {
        if (height < imageHeight) {
            throw new IllegalArgumentException("The image height can't be larger then the actual texture size.");
        }
        if (width < imageWidth) {
            throw new IllegalArgumentException("The image width can't be larger then the actual texture size.");
        }
        if (width <= 0 || height <= 0 || imageWidth <= 0 || imageHeight <= 0) {
            throw new IllegalArgumentException("Zero is a illegal value for height and width values");
        }
        final int capabilities = Cursor.getCapabilities();
        final boolean transparencySupport = (capabilities & 0x1) != 0x0;
        final boolean fullTransparencySupport = (capabilities & 0x2) != 0x0;
        if (!transparencySupport) {
            Log.info("Your system does not support cursors with transparency. The mouse cursor may look messy.");
        }
        for (int i = 0; i < buf.limit(); i += 4) {
            final byte red = buf.get(i);
            final byte green = buf.get(i + 1);
            final byte blue = buf.get(i + 2);
            final byte alpha = buf.get(i + 3);
            buf.put(i + 2, red);
            buf.put(i + 1, green);
            buf.put(i, blue);
            if (fullTransparencySupport) {
                buf.put(i + 3, alpha);
            }
            else if (transparencySupport) {
                buf.put(i + 3, this.applyThreshold(alpha));
            }
            else {
                buf.put(i + 3, (byte)(-1));
            }
        }
        final int maxSize = Cursor.getMaxCursorSize();
        final int minSize = Cursor.getMinCursorSize();
        int cursorTextureHeight = height;
        int cursorTextureWidth = width;
        int ySpot = imageHeight - y - 1;
        int xSpot = x;
        if (ySpot < 0) {
            ySpot = 0;
        }
        if (cursorTextureHeight > maxSize || cursorTextureWidth > maxSize) {
            final int targetHeight = Math.min(maxSize, cursorTextureHeight);
            final int targetWidth = Math.min(maxSize, cursorTextureWidth);
            ySpot -= imageHeight - targetHeight;
            xSpot -= imageWidth - targetWidth;
            final byte[] pixelBuffer = new byte[4];
            final ByteBuffer tempBuffer = BufferUtils.createByteBuffer(targetHeight * targetWidth * 4);
            BufferUtils.zeroBuffer(tempBuffer);
            for (int tempX = 0; tempX < targetHeight; ++tempX) {
                for (int tempY = 0; tempY < targetWidth; ++tempY) {
                    buf.position((tempX + tempY * cursorTextureWidth) * 4);
                    buf.get(pixelBuffer);
                    tempBuffer.position((tempX + tempY * targetWidth) * 4);
                    tempBuffer.put(pixelBuffer);
                }
            }
            cursorTextureHeight = targetHeight;
            cursorTextureWidth = targetWidth;
            buf = tempBuffer;
        }
        if (cursorTextureHeight < minSize || cursorTextureWidth < minSize) {
            final int targetHeight = Math.max(minSize, cursorTextureHeight);
            final int targetWidth = Math.max(minSize, cursorTextureWidth);
            final byte[] pixelBuffer = new byte[4];
            final ByteBuffer tempBuffer = BufferUtils.createByteBuffer(targetHeight * targetWidth * 4);
            BufferUtils.zeroBuffer(tempBuffer);
            for (int tempX = 0; tempX < imageWidth; ++tempX) {
                for (int tempY = 0; tempY < imageHeight; ++tempY) {
                    buf.position((tempX + tempY * cursorTextureWidth) * 4);
                    buf.get(pixelBuffer);
                    tempBuffer.position((tempX + tempY * targetWidth) * 4);
                    tempBuffer.put(pixelBuffer);
                }
            }
            cursorTextureHeight = targetHeight;
            cursorTextureWidth = targetWidth;
            buf = tempBuffer;
        }
        try {
            buf.position(0);
            return new Cursor(cursorTextureWidth, cursorTextureHeight, xSpot, ySpot, 1, buf.asIntBuffer(), null);
        }
        catch (Throwable e) {
            Log.info("Chances are you cursor is too small for this platform");
            throw new LWJGLException(e);
        }
    }
    
    public Cursor getCursor(final ImageData imageData, final int x, final int y) throws IOException, LWJGLException {
        return this.getCursor(imageData.getImageBufferData(), x, y, imageData.getTexWidth(), imageData.getTexHeight(), imageData.getWidth(), imageData.getHeight());
    }
    
    public Cursor getAnimatedCursor(final String ref, final int x, final int y, final int width, final int height, final int[] cursorDelays) throws IOException, LWJGLException {
        final IntBuffer cursorDelaysBuffer = ByteBuffer.allocateDirect(cursorDelays.length * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
        for (int i = 0; i < cursorDelays.length; ++i) {
            cursorDelaysBuffer.put(cursorDelays[i]);
        }
        cursorDelaysBuffer.flip();
        final LoadableImageData imageData = new TGAImageData();
        final ByteBuffer buf = imageData.loadImage(ResourceLoader.getResourceAsStream(ref), false, null);
        return new Cursor(width, height, x, y, cursorDelays.length, buf.asIntBuffer(), cursorDelaysBuffer);
    }
    
    static {
        CursorLoader.single = new CursorLoader();
    }
}
