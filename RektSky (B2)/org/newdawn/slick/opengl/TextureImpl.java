package org.newdawn.slick.opengl;

import java.nio.*;
import org.lwjgl.*;
import org.newdawn.slick.opengl.renderer.*;
import org.newdawn.slick.util.*;

public class TextureImpl implements Texture
{
    protected static SGL GL;
    static Texture lastBind;
    private int target;
    private int textureID;
    private int height;
    private int width;
    private int texWidth;
    private int texHeight;
    private float widthRatio;
    private float heightRatio;
    private ImageData.Format format;
    private String ref;
    private String cacheName;
    private ReloadData reloadData;
    
    public static Texture getLastBind() {
        return TextureImpl.lastBind;
    }
    
    protected TextureImpl() {
        this.textureID = 0;
    }
    
    public TextureImpl(final String ref, final int target, final int textureID) {
        this.textureID = 0;
        this.target = target;
        this.ref = ref;
        this.textureID = textureID;
        TextureImpl.lastBind = this;
    }
    
    public void setCacheName(final String cacheName) {
        this.cacheName = cacheName;
    }
    
    @Override
    public boolean hasAlpha() {
        return this.format.hasAlpha();
    }
    
    @Override
    public String getTextureRef() {
        return this.ref;
    }
    
    public void setImageFormat(final ImageData.Format imageFormat) {
        this.format = imageFormat;
    }
    
    public static void bindNone() {
        TextureImpl.lastBind = null;
        TextureImpl.GL.glDisable(3553);
    }
    
    public static void unbind() {
        TextureImpl.lastBind = null;
    }
    
    @Override
    public void bind() {
        if (TextureImpl.lastBind != this) {
            TextureImpl.lastBind = this;
            TextureImpl.GL.glEnable(3553);
            TextureImpl.GL.glBindTexture(this.target, this.textureID);
        }
    }
    
    public void setHeight(final int height) {
        this.height = height;
        this.setHeight();
    }
    
    public void setWidth(final int width) {
        this.width = width;
        this.setWidth();
    }
    
    public ImageData.Format getImageFormat() {
        return this.format;
    }
    
    @Override
    public int getImageHeight() {
        return this.height;
    }
    
    @Override
    public int getImageWidth() {
        return this.width;
    }
    
    @Override
    public float getHeight() {
        return this.heightRatio;
    }
    
    @Override
    public float getWidth() {
        return this.widthRatio;
    }
    
    @Override
    public int getTextureHeight() {
        return this.texHeight;
    }
    
    @Override
    public int getTextureWidth() {
        return this.texWidth;
    }
    
    public void setTextureHeight(final int texHeight) {
        this.texHeight = texHeight;
        this.setHeight();
    }
    
    public void setTextureWidth(final int texWidth) {
        this.texWidth = texWidth;
        this.setWidth();
    }
    
    private void setHeight() {
        if (this.texHeight != 0) {
            this.heightRatio = this.height / (float)this.texHeight;
        }
    }
    
    private void setWidth() {
        if (this.texWidth != 0) {
            this.widthRatio = this.width / (float)this.texWidth;
        }
    }
    
    @Override
    public void release() {
        if (this.textureID == 0) {
            return;
        }
        InternalTextureLoader.deleteTextureID(this.textureID);
        if (TextureImpl.lastBind == this) {
            bindNone();
        }
        if (this.cacheName != null) {
            InternalTextureLoader.get().clear(this.cacheName);
        }
        else {
            InternalTextureLoader.get().clear(this.ref);
        }
        this.textureID = 0;
    }
    
    @Override
    public int getTextureID() {
        return this.textureID;
    }
    
    public void setTextureID(final int textureID) {
        this.textureID = textureID;
    }
    
    protected IntBuffer createIntBuffer(final int size) {
        final ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
        temp.order(ByteOrder.nativeOrder());
        return temp.asIntBuffer();
    }
    
    @Override
    public byte[] getTextureData() {
        final ByteBuffer buffer = BufferUtils.createByteBuffer(this.format.getColorComponents() * this.texWidth * this.texHeight);
        this.bind();
        TextureImpl.GL.glGetTexImage(3553, 0, this.format.getOGLType(), 5121, buffer);
        final byte[] data = new byte[buffer.limit()];
        buffer.get(data);
        buffer.clear();
        return data;
    }
    
    @Override
    public void setTextureFilter(final int textureFilter) {
        this.bind();
        TextureImpl.GL.glTexParameteri(this.target, 10241, textureFilter);
        TextureImpl.GL.glTexParameteri(this.target, 10240, textureFilter);
    }
    
    public void setTextureData(final int srcPixelFormat, final int componentCount, final int minFilter, final int magFilter, final ByteBuffer textureBuffer) {
        (this.reloadData = new ReloadData()).srcPixelFormat = srcPixelFormat;
        this.reloadData.componentCount = componentCount;
        this.reloadData.minFilter = minFilter;
        this.reloadData.magFilter = magFilter;
        this.reloadData.textureBuffer = textureBuffer;
    }
    
    public void reload() {
        if (this.reloadData != null) {
            this.textureID = this.reloadData.reload();
        }
    }
    
    static {
        TextureImpl.GL = Renderer.get();
    }
    
    private class ReloadData
    {
        private int srcPixelFormat;
        private int componentCount;
        private int minFilter;
        private int magFilter;
        private ByteBuffer textureBuffer;
        
        public int reload() {
            Log.error("Reloading texture: " + TextureImpl.this.ref);
            return InternalTextureLoader.get().reload(TextureImpl.this, this.srcPixelFormat, this.componentCount, this.minFilter, this.magFilter, this.textureBuffer);
        }
    }
}
