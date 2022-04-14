package org.newdawn.slick.opengl;

import org.newdawn.slick.util.*;
import java.lang.ref.*;
import java.io.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import java.nio.*;
import java.util.*;
import org.newdawn.slick.opengl.renderer.*;

public class InternalTextureLoader
{
    static int textureCount;
    private static boolean forcePOT;
    protected static SGL GL;
    private static final InternalTextureLoader loader;
    private HashMap texturesLinear;
    private HashMap texturesNearest;
    private int dstPixelFormat;
    private boolean deferred;
    private boolean holdTextureData;
    
    public static boolean isPowerOfTwo(final int n) {
        return (n & -n) == n;
    }
    
    public static boolean isForcePOT() {
        return InternalTextureLoader.forcePOT;
    }
    
    public static void setForcePOT(final boolean b) {
        InternalTextureLoader.forcePOT = b;
    }
    
    public static int getTextureCount() {
        return InternalTextureLoader.textureCount;
    }
    
    public static int createTextureID() {
        final IntBuffer tmp = createIntBuffer(1);
        InternalTextureLoader.GL.glGenTextures(tmp);
        ++InternalTextureLoader.textureCount;
        return tmp.get(0);
    }
    
    public static void deleteTextureID(final int id) {
        final IntBuffer texBuf = createIntBuffer(1);
        texBuf.put(id);
        texBuf.flip();
        InternalTextureLoader.GL.glDeleteTextures(texBuf);
        --InternalTextureLoader.textureCount;
    }
    
    public static boolean isGenerateMipmapSupported() {
        return GLContext.getCapabilities().OpenGL14 || GLContext.getCapabilities().GL_EXT_framebuffer_object;
    }
    
    public static boolean isNPOTSupported() {
        return GLContext.getCapabilities().GL_ARB_texture_non_power_of_two;
    }
    
    public static InternalTextureLoader get() {
        return InternalTextureLoader.loader;
    }
    
    private InternalTextureLoader() {
        this.texturesLinear = new HashMap();
        this.texturesNearest = new HashMap();
        this.dstPixelFormat = 6408;
    }
    
    public void setHoldTextureData(final boolean holdTextureData) {
        this.holdTextureData = holdTextureData;
    }
    
    public void setDeferredLoading(final boolean deferred) {
        this.deferred = deferred;
    }
    
    public boolean isDeferredLoading() {
        return this.deferred;
    }
    
    public void clear(final String name) {
        this.texturesLinear.remove(name);
        this.texturesNearest.remove(name);
    }
    
    public void clear() {
        this.texturesLinear.clear();
        this.texturesNearest.clear();
    }
    
    public void set16BitMode() {
        this.dstPixelFormat = 32859;
    }
    
    public Texture getTexture(final File source, final boolean flipped, final int filter) throws IOException {
        final String resourceName = source.getAbsolutePath();
        final InputStream in = new FileInputStream(source);
        return this.getTexture(in, resourceName, flipped, filter, null);
    }
    
    public Texture getTexture(final File source, final boolean flipped, final int filter, final int[] transparent) throws IOException {
        final String resourceName = source.getAbsolutePath();
        final InputStream in = new FileInputStream(source);
        return this.getTexture(in, resourceName, flipped, filter, transparent);
    }
    
    public Texture getTexture(final String resourceName, final boolean flipped, final int filter) throws IOException {
        final InputStream in = ResourceLoader.getResourceAsStream(resourceName);
        return this.getTexture(in, resourceName, flipped, filter, null);
    }
    
    public Texture getTexture(final String resourceName, final boolean flipped, final int filter, final int[] transparent) throws IOException {
        final InputStream in = ResourceLoader.getResourceAsStream(resourceName);
        return this.getTexture(in, resourceName, flipped, filter, transparent);
    }
    
    public Texture getTexture(final InputStream in, final String resourceName, final boolean flipped, final int filter) throws IOException {
        return this.getTexture(in, resourceName, flipped, filter, null);
    }
    
    public TextureImpl getTexture(final InputStream in, final String resourceName, final boolean flipped, final int filter, final int[] transparent) throws IOException {
        if (this.deferred) {
            return new DeferredTexture(in, resourceName, flipped, filter, transparent);
        }
        HashMap hash = this.texturesLinear;
        if (filter == 9728) {
            hash = this.texturesNearest;
        }
        String resName = resourceName;
        if (transparent != null) {
            resName = resName + ":" + transparent[0] + ":" + transparent[1] + ":" + transparent[2];
        }
        resName = resName + ":" + flipped;
        if (this.holdTextureData) {
            final TextureImpl tex = hash.get(resName);
            if (tex != null) {
                return tex;
            }
        }
        else {
            final SoftReference ref = hash.get(resName);
            if (ref != null) {
                final TextureImpl tex2 = ref.get();
                if (tex2 != null) {
                    return tex2;
                }
                hash.remove(resName);
            }
        }
        try {
            InternalTextureLoader.GL.glGetError();
        }
        catch (NullPointerException e) {
            throw new RuntimeException("Image based resources must be loaded as part of init() or the game loop. They cannot be loaded before initialisation.");
        }
        final TextureImpl tex = this.getTexture(in, resourceName, 3553, filter, filter, flipped, transparent);
        tex.setCacheName(resName);
        if (this.holdTextureData) {
            hash.put(resName, tex);
        }
        else {
            hash.put(resName, new SoftReference<TextureImpl>(tex));
        }
        return tex;
    }
    
    private TextureImpl getTexture(final InputStream in, final String resourceName, final int target, final int minFilter, final int magFilter, final boolean flipped, final int[] transparent) throws IOException {
        final LoadableImageData imageData = ImageDataFactory.getImageDataFor(resourceName);
        final ByteBuffer textureBuffer = imageData.loadImage(new BufferedInputStream(in), flipped, transparent);
        final int textureID = createTextureID();
        final TextureImpl texture = new TextureImpl(resourceName, target, textureID);
        InternalTextureLoader.GL.glEnable(target);
        InternalTextureLoader.GL.glBindTexture(target, textureID);
        final int width = imageData.getWidth();
        final int height = imageData.getHeight();
        final ImageData.Format format = imageData.getFormat();
        texture.setTextureWidth(imageData.getTexWidth());
        texture.setTextureHeight(imageData.getTexHeight());
        final int texWidth = texture.getTextureWidth();
        final int texHeight = texture.getTextureHeight();
        final IntBuffer temp = BufferUtils.createIntBuffer(16);
        InternalTextureLoader.GL.glGetInteger(3379, temp);
        final int max = temp.get(0);
        if (texWidth > max || texHeight > max) {
            throw new IOException("Attempt to allocate a texture to big for the current hardware");
        }
        final int srcPixelFormat = format.getOGLType();
        final int componentCount = format.getColorComponents();
        texture.setWidth(width);
        texture.setHeight(height);
        texture.setImageFormat(format);
        if (this.holdTextureData) {
            texture.setTextureData(srcPixelFormat, componentCount, minFilter, magFilter, textureBuffer);
        }
        InternalTextureLoader.GL.glTexParameteri(target, 10241, minFilter);
        InternalTextureLoader.GL.glTexParameteri(target, 10240, magFilter);
        InternalTextureLoader.GL.glTexImage2D(target, 0, this.dstPixelFormat, get2Fold(width), get2Fold(height), 0, srcPixelFormat, 5121, textureBuffer);
        return texture;
    }
    
    public TextureImpl createTexture(final ImageData data, final ByteBuffer buffer, final String ref, final int target, int minFilter, final int magFilter, boolean genMipmaps, final ImageData.Format internalFormat) throws IOException {
        final int textureID = createTextureID();
        final TextureImpl texture = new TextureImpl(ref, target, textureID);
        InternalTextureLoader.GL.glEnable(target);
        InternalTextureLoader.GL.glBindTexture(target, textureID);
        final int width = data.getWidth();
        final int height = data.getHeight();
        int texWidth = data.getTexWidth();
        int texHeight = data.getTexHeight();
        final boolean usePOT = !isNPOTSupported() || isForcePOT();
        if (usePOT) {
            texWidth = get2Fold(width);
            texHeight = get2Fold(height);
        }
        final int max = GL11.glGetInteger(3379);
        if (texWidth > max || texHeight > max) {
            throw new IOException("Attempt to allocate a texture to big for the current hardware");
        }
        final ImageData.Format dataFormat = data.getFormat();
        final int dstFmt = (internalFormat != null) ? internalFormat.getOGLType() : this.dstPixelFormat;
        final int srcFmt = dataFormat.getOGLType();
        texture.setTextureWidth(texWidth);
        texture.setTextureHeight(texHeight);
        texture.setWidth(width);
        texture.setHeight(height);
        texture.setImageFormat((internalFormat != null) ? internalFormat : ImageData.Format.RGBA);
        if (this.holdTextureData) {
            final int componentCount = dataFormat.getColorComponents();
            texture.setTextureData(srcFmt, componentCount, minFilter, magFilter, buffer);
        }
        final ContextCapabilities cx = GLContext.getCapabilities();
        if (genMipmaps && !isGenerateMipmapSupported()) {
            minFilter = magFilter;
            genMipmaps = false;
        }
        InternalTextureLoader.GL.glTexParameteri(target, 10241, minFilter);
        InternalTextureLoader.GL.glTexParameteri(target, 10240, magFilter);
        if (genMipmaps && !cx.OpenGL30 && !cx.GL_EXT_framebuffer_object) {
            InternalTextureLoader.GL.glTexParameteri(target, 33169, 1);
            genMipmaps = false;
        }
        InternalTextureLoader.GL.glTexImage2D(target, 0, dstFmt, texWidth, texHeight, 0, srcFmt, 5121, buffer);
        if (genMipmaps) {
            GL11.glEnable(target);
            if (cx.OpenGL30) {
                GL30.glGenerateMipmap(target);
            }
            else {
                EXTFramebufferObject.glGenerateMipmapEXT(target);
            }
        }
        return texture;
    }
    
    public Texture createTexture(final int width, final int height) throws IOException {
        return this.createTexture(width, height, 9728);
    }
    
    public Texture createTexture(final int width, final int height, final int filter) throws IOException {
        final ImageData ds = new EmptyImageData(width, height);
        return this.getTexture(ds, filter);
    }
    
    public Texture getTexture(final ImageData dataSource, final int filter) throws IOException {
        final int target = 3553;
        final ByteBuffer textureBuffer = dataSource.getImageBufferData();
        final int textureID = createTextureID();
        final TextureImpl texture = new TextureImpl("generated:" + dataSource, target, textureID);
        final int minFilter = filter;
        final int magFilter = filter;
        final boolean flipped = false;
        InternalTextureLoader.GL.glEnable(target);
        InternalTextureLoader.GL.glBindTexture(target, textureID);
        final int width = dataSource.getWidth();
        final int height = dataSource.getHeight();
        final ImageData.Format format = dataSource.getFormat();
        texture.setTextureWidth(dataSource.getTexWidth());
        texture.setTextureHeight(dataSource.getTexHeight());
        final int texWidth = texture.getTextureWidth();
        final int texHeight = texture.getTextureHeight();
        final int srcPixelFormat = format.getOGLType();
        final int componentCount = format.getColorComponents();
        texture.setWidth(width);
        texture.setHeight(height);
        texture.setImageFormat(format);
        final IntBuffer temp = BufferUtils.createIntBuffer(16);
        InternalTextureLoader.GL.glGetInteger(3379, temp);
        final int max = temp.get(0);
        if (texWidth > max || texHeight > max) {
            throw new IOException("Attempt to allocate a texture to big for the current hardware");
        }
        if (this.holdTextureData) {
            texture.setTextureData(srcPixelFormat, componentCount, minFilter, magFilter, textureBuffer);
        }
        InternalTextureLoader.GL.glTexParameteri(target, 10241, minFilter);
        InternalTextureLoader.GL.glTexParameteri(target, 10240, magFilter);
        InternalTextureLoader.GL.glTexImage2D(target, 0, this.dstPixelFormat, get2Fold(width), get2Fold(height), 0, srcPixelFormat, 5121, textureBuffer);
        return texture;
    }
    
    public static int get2Fold(final int fold) {
        int ret;
        for (ret = 2; ret < fold; ret *= 2) {}
        return ret;
    }
    
    public static IntBuffer createIntBuffer(final int size) {
        final ByteBuffer temp = ByteBuffer.allocateDirect(4 * size);
        temp.order(ByteOrder.nativeOrder());
        return temp.asIntBuffer();
    }
    
    public void reload() {
        Iterator texs = this.texturesLinear.values().iterator();
        while (texs.hasNext()) {
            texs.next().reload();
        }
        texs = this.texturesNearest.values().iterator();
        while (texs.hasNext()) {
            texs.next().reload();
        }
    }
    
    public int reload(final TextureImpl texture, final int srcPixelFormat, final int componentCount, final int minFilter, final int magFilter, final ByteBuffer textureBuffer) {
        final int target = 3553;
        final int textureID = createTextureID();
        InternalTextureLoader.GL.glEnable(target);
        InternalTextureLoader.GL.glBindTexture(target, textureID);
        InternalTextureLoader.GL.glTexParameteri(target, 10241, minFilter);
        InternalTextureLoader.GL.glTexParameteri(target, 10240, magFilter);
        InternalTextureLoader.GL.glTexImage2D(target, 0, this.dstPixelFormat, texture.getTextureWidth(), texture.getTextureHeight(), 0, srcPixelFormat, 5121, textureBuffer);
        return textureID;
    }
    
    static {
        InternalTextureLoader.textureCount = 0;
        InternalTextureLoader.forcePOT = true;
        InternalTextureLoader.GL = Renderer.get();
        loader = new InternalTextureLoader();
    }
}
