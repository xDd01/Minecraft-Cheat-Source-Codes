/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.esp;

import cafe.corrosion.util.esp.Mesh;
import cafe.corrosion.util.esp.OBJLoader;
import cafe.corrosion.util.esp.Vertex;
import java.util.ArrayList;
import javax.vecmath.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class IndexedModel {
    private ArrayList<javax.vecmath.Vector3f> vertices = new ArrayList();
    private ArrayList<Vector2f> texCoords = new ArrayList();
    private ArrayList<javax.vecmath.Vector3f> normals = new ArrayList();
    private ArrayList<javax.vecmath.Vector3f> tangents = new ArrayList();
    private ArrayList<Integer> indices = new ArrayList();
    private ArrayList<OBJLoader.OBJIndex> objindices = new ArrayList();

    public ArrayList<javax.vecmath.Vector3f> getPositions() {
        return this.vertices;
    }

    public ArrayList<Vector2f> getTexCoords() {
        return this.texCoords;
    }

    public ArrayList<javax.vecmath.Vector3f> getNormals() {
        return this.normals;
    }

    public ArrayList<Integer> getIndices() {
        return this.indices;
    }

    public ArrayList<javax.vecmath.Vector3f> getTangents() {
        return this.tangents;
    }

    public void toMesh(Mesh mesh) {
        ArrayList<Vertex> verticesList = new ArrayList<Vertex>();
        int n2 = Math.min(this.vertices.size(), Math.min(this.texCoords.size(), this.normals.size()));
        for (int i2 = 0; i2 < n2; ++i2) {
            Vertex vertex = new Vertex(this.vertices.get(i2), this.texCoords.get(i2), this.normals.get(i2), new javax.vecmath.Vector3f());
            verticesList.add(vertex);
        }
        Integer[] indicesArray = this.indices.toArray(new Integer[0]);
        Vertex[] verticesArray = verticesList.toArray(new Vertex[0]);
        int[] indicesArrayInt = new int[indicesArray.length];
        for (int i3 = 0; i3 < indicesArray.length; ++i3) {
            indicesArrayInt[i3] = indicesArray[i3];
        }
        mesh.vertices = verticesArray;
        mesh.indices = indicesArrayInt;
    }

    public void computeNormals() {
        int i2;
        for (i2 = 0; i2 < this.indices.size(); i2 += 3) {
            int i0 = this.indices.get(i2);
            int i1 = this.indices.get(i2 + 1);
            int i22 = this.indices.get(i2 + 2);
            javax.vecmath.Vector3f v2 = (javax.vecmath.Vector3f)this.vertices.get(i1).clone();
            v2.sub(this.vertices.get(i0));
            javax.vecmath.Vector3f l0 = v2;
            v2 = (javax.vecmath.Vector3f)this.vertices.get(i22).clone();
            v2.sub(this.vertices.get(i0));
            javax.vecmath.Vector3f l1 = v2;
            v2 = (javax.vecmath.Vector3f)l0.clone();
            v2.cross(l0, l1);
            javax.vecmath.Vector3f normal = v2;
            v2 = (javax.vecmath.Vector3f)this.normals.get(i0).clone();
            v2.add(normal);
            this.normals.set(i0, v2);
            v2 = (javax.vecmath.Vector3f)this.normals.get(i1).clone();
            v2.add(normal);
            this.normals.set(i1, v2);
            v2 = (javax.vecmath.Vector3f)this.normals.get(i22).clone();
            v2.add(normal);
            this.normals.set(i22, v2);
        }
        for (i2 = 0; i2 < this.normals.size(); ++i2) {
            this.normals.get(i2).normalize();
        }
    }

    public void computeTangents() {
        int i2;
        this.tangents.clear();
        for (i2 = 0; i2 < this.vertices.size(); ++i2) {
            this.tangents.add(new javax.vecmath.Vector3f());
        }
        for (i2 = 0; i2 < this.indices.size(); i2 += 3) {
            int i0 = this.indices.get(i2);
            int i1 = this.indices.get(i2 + 1);
            int i22 = this.indices.get(i2 + 2);
            javax.vecmath.Vector3f v2 = (javax.vecmath.Vector3f)this.vertices.get(i1).clone();
            v2.sub(this.vertices.get(i0));
            javax.vecmath.Vector3f edge1 = v2;
            v2 = (javax.vecmath.Vector3f)this.vertices.get(i22).clone();
            v2.sub(this.vertices.get(i0));
            javax.vecmath.Vector3f edge2 = v2;
            double deltaU1 = this.texCoords.get((int)i1).x - this.texCoords.get((int)i0).x;
            double deltaU2 = this.texCoords.get((int)i22).x - this.texCoords.get((int)i0).x;
            double deltaV1 = this.texCoords.get((int)i1).y - this.texCoords.get((int)i0).y;
            double deltaV2 = this.texCoords.get((int)i22).y - this.texCoords.get((int)i0).y;
            double dividend = deltaU1 * deltaV2 - deltaU2 * deltaV1;
            double f2 = dividend == 0.0 ? 0.0 : 1.0 / dividend;
            javax.vecmath.Vector3f tangent = new javax.vecmath.Vector3f((float)(f2 * (deltaV2 * (double)edge1.x - deltaV1 * (double)edge2.x)), (float)(f2 * (deltaV2 * (double)edge1.y - deltaV1 * (double)edge2.y)), (float)(f2 * (deltaV2 * (double)edge1.z - deltaV1 * (double)edge2.z)));
            v2 = (javax.vecmath.Vector3f)this.tangents.get(i0).clone();
            v2.add(tangent);
            this.tangents.set(i0, v2);
            v2 = (javax.vecmath.Vector3f)this.tangents.get(i1).clone();
            v2.add(tangent);
            this.tangents.set(i1, v2);
            v2 = (javax.vecmath.Vector3f)this.tangents.get(i22).clone();
            v2.add(tangent);
            this.tangents.set(i22, v2);
        }
        for (i2 = 0; i2 < this.tangents.size(); ++i2) {
            this.tangents.get(i2).normalize();
        }
    }

    public ArrayList<OBJLoader.OBJIndex> getObjIndices() {
        return this.objindices;
    }

    public Vector3f computeCenter() {
        float x2 = 0.0f;
        float y2 = 0.0f;
        float z2 = 0.0f;
        for (javax.vecmath.Vector3f position : this.vertices) {
            x2 += position.x;
            y2 += position.y;
            z2 += position.z;
        }
        return new Vector3f(x2 /= (float)this.vertices.size(), y2 /= (float)this.vertices.size(), z2 /= (float)this.vertices.size());
    }
}

