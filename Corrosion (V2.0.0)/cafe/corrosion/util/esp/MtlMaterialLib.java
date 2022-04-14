/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.esp;

import cafe.corrosion.util.esp.Material;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class MtlMaterialLib {
    public static final String COMMENT = "#";
    public static final String NEW_MATERIAL = "newmtl";
    public static final String AMBIENT_COLOR = "Ka";
    public static final String DIFFUSE_COLOR = "Kd";
    public static final String SPECULAR_COLOR = "Ks";
    public static final String TRANSPARENCY_D = "d";
    public static final String TRANSPARENCY_TR = "Tr";
    public static final String ILLUMINATION = "illum";
    public static final String TEXTURE_AMBIENT = "map_Ka";
    public static final String TEXTURE_DIFFUSE = "map_Kd";
    public static final String TEXTURE_SPECULAR = "map_Ks";
    public static final String TEXTURE_TRANSPARENCY = "map_d";
    private ArrayList<Material> materials;
    private String path;
    private String startPath;

    public MtlMaterialLib(String path) {
        this.path = path;
        this.startPath = path.substring(0, path.lastIndexOf(47) + 1);
        this.materials = new ArrayList();
    }

    public void parse(String content) {
        String[] lines = content.split("\n");
        Material current = null;
        for (int i2 = 0; i2 < lines.length; ++i2) {
            String line = lines[i2].trim();
            String[] parts = line.split(" ");
            if (parts[0].equals(COMMENT)) continue;
            if (parts[0].equals(NEW_MATERIAL)) {
                Material material = new Material(parts[1]);
                this.materials.add(material);
                current = material;
                continue;
            }
            if (parts[0].equals(AMBIENT_COLOR)) {
                current.ambientColor = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
                continue;
            }
            if (parts[0].equals(DIFFUSE_COLOR)) {
                current.diffuseColor = new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3]));
                continue;
            }
            if (parts[0].equals(TEXTURE_DIFFUSE)) {
                current.diffuseTexture = this.loadTexture(this.startPath + parts[1]);
                continue;
            }
            if (parts[0].equals(TEXTURE_AMBIENT)) {
                current.ambientTexture = this.loadTexture(this.startPath + parts[1]);
                continue;
            }
            if (!parts[0].equals(TRANSPARENCY_D) && !parts[0].equals(TRANSPARENCY_TR)) continue;
            current.transparency = (float)Double.parseDouble(parts[1]);
        }
    }

    private int loadTexture(String string) {
        try {
            return MtlMaterialLib.loadTexture(ImageIO.read(MtlMaterialLib.class.getResource(string)));
        }
        catch (IOException e2) {
            e2.printStackTrace();
            return 0;
        }
    }

    public static ByteBuffer imageToByteBuffer(BufferedImage img) {
        int[] pixels = img.getRGB(0, 0, img.getWidth(), img.getHeight(), null, 0, img.getWidth());
        int bufLen = pixels.length * 4;
        ByteBuffer oglPixelBuf = BufferUtils.createByteBuffer(bufLen);
        for (int y2 = 0; y2 < img.getHeight(); ++y2) {
            for (int x2 = 0; x2 < img.getWidth(); ++x2) {
                int rgb = pixels[y2 * img.getWidth() + x2];
                float a2 = (float)(rgb >> 24 & 0xFF) / 255.0f;
                float r2 = (float)(rgb >> 16 & 0xFF) / 255.0f;
                float g2 = (float)(rgb >> 8 & 0xFF) / 255.0f;
                float b2 = (float)(rgb >> 0 & 0xFF) / 255.0f;
                oglPixelBuf.put((byte)(r2 * 255.0f));
                oglPixelBuf.put((byte)(g2 * 255.0f));
                oglPixelBuf.put((byte)(b2 * 255.0f));
                oglPixelBuf.put((byte)(a2 * 255.0f));
            }
        }
        oglPixelBuf.flip();
        return oglPixelBuf;
    }

    public static int loadTexture(BufferedImage img) {
        ByteBuffer oglPixelBuf = MtlMaterialLib.imageToByteBuffer(img);
        int id2 = GL11.glGenTextures();
        int target = 3553;
        GL11.glBindTexture(target, id2);
        GL11.glTexParameterf(target, 10241, 9728.0f);
        GL11.glTexParameterf(target, 10240, 9728.0f);
        GL11.glTexEnvf(8960, 8704, 8448.0f);
        GL11.glTexParameteri(target, 33084, 0);
        GL11.glTexParameteri(target, 33085, 0);
        GL11.glTexImage2D(target, 0, 32856, img.getWidth(), img.getHeight(), 0, 6408, 5121, oglPixelBuf);
        GL11.glBindTexture(target, 0);
        return id2;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }
}

