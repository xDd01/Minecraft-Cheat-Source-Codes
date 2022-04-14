/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.esp;

import cafe.corrosion.util.esp.Material;
import cafe.corrosion.util.esp.Mesh;
import org.lwjgl.util.vector.Vector3f;

public class ObjObject {
    private String name;
    public Mesh mesh;
    public Material material;
    public Vector3f center;

    public ObjObject(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

