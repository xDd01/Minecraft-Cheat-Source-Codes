package me.spec.eris.client.ui.fonts;

import java.awt.Font;
import java.io.InputStream;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import me.spec.eris.client.ui.fonts.data.TextureData;
import org.lwjgl.opengl.GL11;

import me.spec.eris.Eris;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class FontManager {
    private ResourceLocation darrow;
    private TTFFontRenderer defaultFont;
    private FontManager instance;
    private HashMap<String, TTFFontRenderer> fonts;

    public FontManager getInstance() {
        return this.instance;
    }

    public TTFFontRenderer getFont(final String key) {
        return this.fonts.getOrDefault(key, this.defaultFont);
    }

    private static TTFFontRenderer fontRender;

    public static TTFFontRenderer getFont() {
        if (fontRender == null) {
            fontRender = Eris.INSTANCE.fontManager.getFont("SFUI 18");
        }
        return fontRender;
    }

    public FontManager() {
        textureQueue = new ConcurrentLinkedQueue<TextureData>();
        executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
        this.run();
    }

    final ThreadPoolExecutor executorService;
    final ConcurrentLinkedQueue<TextureData> textureQueue;

    public void run() {
        this.darrow = new ResourceLocation("Roboto-Regular.ttf");
        this.fonts = new HashMap<String, TTFFontRenderer>();
        this.instance = this;
        this.defaultFont = new TTFFontRenderer(executorService, textureQueue, new Font("Arial", 0, 18));
        try {
            for (int i = 10; i < 22; i++) {
                final InputStream istream = this.getClass().getResourceAsStream("/assets/minecraft/eris/fonts/SF-UI-Display-Regular.ttf");
                Font myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, (float) i);
                fonts.put("SFUI " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
            for (int i = 10; i < 22; i++) {
                final InputStream istream = this.getClass().getResourceAsStream("/assets/minecraft/eris/fonts/Arial.ttf");
                Font myFont = Font.createFont(0, istream);
                myFont = myFont.deriveFont(0, (float) i);
                fonts.put("Arial " + i, new TTFFontRenderer(executorService, textureQueue, myFont));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(10L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (!textureQueue.isEmpty()) {
                final TextureData textureData = textureQueue.poll();
                GlStateManager.bindTexture(textureData.getTextureId());
                GL11.glTexParameteri(3553, 10241, 9728);
                GL11.glTexParameteri(3553, 10240, 9728);
                GL11.glTexImage2D(3553, 0, 6408, textureData.getWidth(), textureData.getHeight(), 0, 6408, 5121, textureData.getBuffer());
            }
        }
    }

    public TTFFontRenderer getInstalledFont(String name, int size) {
        try {
            return new TTFFontRenderer((ThreadPoolExecutor) Executors.newFixedThreadPool(8), new ConcurrentLinkedQueue<TextureData>(), new Font(name, 0, size));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}

