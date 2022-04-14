/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.esp.mc;

import cafe.corrosion.util.esp.IndexedModel;
import cafe.corrosion.util.esp.Mesh;
import cafe.corrosion.util.esp.Model;
import cafe.corrosion.util.esp.OBJLoader;
import cafe.corrosion.util.esp.ObjEvent;
import cafe.corrosion.util.esp.ObjModel;
import cafe.corrosion.util.esp.ObjObject;
import cafe.corrosion.util.esp.Vertex;
import java.util.HashMap;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class TessellatorModel
extends ObjModel {
    public TessellatorModel(String string) {
        super(string);
        try {
            String content = new String(this.read(Model.class.getResourceAsStream(string)), "UTF-8");
            String startPath = string.substring(0, string.lastIndexOf(47) + 1);
            HashMap<ObjObject, IndexedModel> map = new OBJLoader().loadModel(startPath, content);
            this.objObjects.clear();
            Set<ObjObject> keys = map.keySet();
            for (ObjObject object : keys) {
                Mesh mesh;
                object.mesh = mesh = new Mesh();
                this.objObjects.add(object);
                map.get(object).toMesh(mesh);
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    @Override
    public void renderImpl() {
        this.objObjects.sort((a2, b2) -> {
            Vec3 v2 = Minecraft.getMinecraft().getRenderViewEntity().getPositionVector();
            double aDist = v2.distanceTo(new Vec3(a2.center.x, a2.center.y, a2.center.z));
            double bDist = v2.distanceTo(new Vec3(b2.center.x, b2.center.y, b2.center.z));
            return Double.compare(aDist, bDist);
        });
        for (ObjObject object : this.objObjects) {
            this.renderGroup(object);
        }
    }

    @Override
    public void renderGroupsImpl(String group) {
        for (ObjObject object : this.objObjects) {
            if (!object.getName().equals(group)) continue;
            this.renderGroup(object);
        }
    }

    @Override
    public void renderGroupImpl(ObjObject obj) {
        Tessellator tess = Tessellator.getInstance();
        WorldRenderer renderer = tess.getWorldRenderer();
        if (obj.mesh == null) {
            return;
        }
        Vector3f color = new Vector3f(1.0f, 1.0f, 1.0f);
        float alpha = 1.0f;
        if (obj.material != null) {
            GL11.glBindTexture(3553, obj.material.diffuseTexture);
        }
        int[] indices = obj.mesh.indices;
        Vertex[] vertices = obj.mesh.vertices;
        renderer.begin(4, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int i2 = 0; i2 < indices.length; i2 += 3) {
            int i0 = indices[i2];
            int i1 = indices[i2 + 1];
            int i22 = indices[i2 + 2];
            Vertex v0 = vertices[i0];
            Vertex v1 = vertices[i1];
            Vertex v2 = vertices[i22];
            renderer.pos(v0.getPos().x, v0.getPos().y, v0.getPos().z).tex(v0.getTexCoords().x, 1.0f - v0.getTexCoords().y).normal(v0.getNormal().x, v0.getNormal().y, v0.getNormal().z).endVertex();
            renderer.pos(v1.getPos().x, v1.getPos().y, v1.getPos().z).tex(v1.getTexCoords().x, 1.0f - v1.getTexCoords().y).normal(v1.getNormal().x, v1.getNormal().y, v1.getNormal().z).endVertex();
            renderer.pos(v2.getPos().x, v2.getPos().y, v2.getPos().z).tex(v2.getTexCoords().x, 1.0f - v2.getTexCoords().y).normal(v2.getNormal().x, v2.getNormal().y, v2.getNormal().z).endVertex();
        }
        tess.draw();
    }

    @Override
    public boolean fireEvent(ObjEvent event) {
        return true;
    }

    @Deprecated
    public void regenerateNormals() {
    }
}

