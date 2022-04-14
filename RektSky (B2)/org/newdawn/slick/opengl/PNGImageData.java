package org.newdawn.slick.opengl;

import java.nio.*;
import java.io.*;
import org.lwjgl.*;
import org.newdawn.slick.util.*;

public class PNGImageData implements LoadableImageData
{
    private int width;
    private int height;
    private int texHeight;
    private int texWidth;
    private PNGDecoder decoder;
    private ImageData.Format format;
    private ByteBuffer scratch;
    
    @Override
    public ImageData.Format getFormat() {
        return this.format;
    }
    
    @Override
    public ByteBuffer getImageBufferData() {
        return this.scratch;
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
    public ByteBuffer loadImage(final InputStream fis) throws IOException {
        return this.loadImage(fis, false, null);
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
        final PNGDecoder decoder = new PNGDecoder(fis);
        this.width = decoder.getWidth();
        this.height = decoder.getHeight();
        this.texWidth = this.get2Fold(this.width);
        this.texHeight = this.get2Fold(this.height);
        PNGDecoder.Format decoderFormat;
        if (forceAlpha) {
            if (decoder.isRGB()) {
                decoderFormat = decoder.decideTextureFormat(PNGDecoder.Format.RGBA);
            }
            else {
                decoderFormat = decoder.decideTextureFormat(PNGDecoder.Format.LUMINANCE_ALPHA);
            }
        }
        else {
            decoderFormat = decoder.decideTextureFormat(PNGDecoder.Format.LUMINANCE);
        }
        switch (decoderFormat) {
            case RGB: {
                this.format = ImageData.Format.RGB;
                break;
            }
            case RGBA: {
                this.format = ImageData.Format.RGBA;
                break;
            }
            case BGRA: {
                this.format = ImageData.Format.BGRA;
                break;
            }
            case LUMINANCE: {
                this.format = ImageData.Format.GRAY;
                break;
            }
            case LUMINANCE_ALPHA: {
                this.format = ImageData.Format.GRAYALPHA;
                break;
            }
            default: {
                throw new IOException("Unsupported Image format.");
            }
        }
        final int perPixel = this.format.getColorComponents();
        this.scratch = BufferUtils.createByteBuffer(this.texWidth * this.texHeight * perPixel);
        if (flipped) {
            decoder.decodeFlipped(this.scratch, this.texWidth * perPixel, decoderFormat);
        }
        else {
            decoder.decode(this.scratch, this.texWidth * perPixel, decoderFormat);
        }
        if (this.height < this.texHeight - 1) {
            final int topOffset = (this.texHeight - 1) * (this.texWidth * perPixel);
            final int bottomOffset = (this.height - 1) * (this.texWidth * perPixel);
            for (int x = 0; x < this.texWidth; ++x) {
                for (int i = 0; i < perPixel; ++i) {
                    this.scratch.put(topOffset + x + i, this.scratch.get(x + i));
                    this.scratch.put(bottomOffset + this.texWidth * perPixel + x + i, this.scratch.get(bottomOffset + x + i));
                }
            }
        }
        if (this.width < this.texWidth - 1) {
            for (int y = 0; y < this.texHeight; ++y) {
                for (int j = 0; j < perPixel; ++j) {
                    this.scratch.put((y + 1) * (this.texWidth * perPixel) - perPixel + j, this.scratch.get(y * (this.texWidth * perPixel) + j));
                    this.scratch.put(y * (this.texWidth * perPixel) + this.width * perPixel + j, this.scratch.get(y * (this.texWidth * perPixel) + (this.width - 1) * perPixel + j));
                }
            }
        }
        this.scratch.position(0);
        if (transparent != null) {
            final int components = this.format.getColorComponents();
            if (transparent.length != components - 1) {
                Log.warn("The amount of color components of the transparent color does not fit the number of color components of the actual image.");
            }
            if (transparent.length < components - 1) {
                Log.error("Failed to apply transparent color, not enough color values in color definition.");
            }
            else {
                for (int size = this.texWidth * this.texHeight * components, i = 0; i < size; i += components) {
                    boolean match = true;
                    for (int c = 0; c < components - 1; ++c) {
                        if (this.toInt(this.scratch.get(i + c)) != transparent[c]) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        this.scratch.put(i + components - 1, (byte)0);
                    }
                }
            }
        }
        this.scratch.position(0);
        return this.scratch;
    }
    
    private int toInt(final byte b) {
        if (b < 0) {
            return 256 + b;
        }
        return b;
    }
    
    private int get2Fold(final int fold) {
        int ret;
        for (ret = 2; ret < fold; ret *= 2) {}
        return ret;
    }
    
    @Override
    public void configureEdging(final boolean edging) {
    }
    
    @Override
    public int getWidth() {
        return this.width;
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
}
