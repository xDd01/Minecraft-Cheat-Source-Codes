/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.esp;

import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

public class Vertex {
    private Vector3f pos;
    private Vector2f texCoords;
    private Vector3f normal;
    private Vector3f tangent;

    public Vertex(Vector3f pos, Vector2f texCoords, Vector3f normal, Vector3f tangent) {
        this.pos = pos;
        this.texCoords = texCoords;
        this.normal = normal;
        this.tangent = tangent;
    }

    public Vector3f getPos() {
        return this.pos;
    }

    public Vector2f getTexCoords() {
        return this.texCoords;
    }

    public Vector3f getNormal() {
        return this.normal;
    }

    public Vector3f getTangent() {
        return this.tangent;
    }
}

