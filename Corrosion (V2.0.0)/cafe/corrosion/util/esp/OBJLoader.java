/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.esp;

import cafe.corrosion.util.esp.HashMapWithDefault;
import cafe.corrosion.util.esp.IndexedModel;
import cafe.corrosion.util.esp.Material;
import cafe.corrosion.util.esp.MtlMaterialLib;
import cafe.corrosion.util.esp.ObjObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;
import org.apache.commons.io.output.ByteArrayOutputStream;

public class OBJLoader {
    private static final String COMMENT = "#";
    private static final String FACE = "f";
    private static final String POSITION = "v";
    private static final String TEX_COORDS = "vt";
    private static final String NORMAL = "vn";
    private static final String NEW_OBJECT = "o";
    private static final String NEW_GROUP = "g";
    private static final String USE_MATERIAL = "usemtl";
    private static final String NEW_MATERIAL = "mtllib";
    private boolean hasNormals = false;
    private boolean hasTexCoords = false;

    public HashMap<ObjObject, IndexedModel> loadModel(String startPath, String res) throws Exception {
        try {
            this.hasNormals = true;
            this.hasTexCoords = true;
            IndexedModel result = new IndexedModel();
            IndexedModel normalModel = new IndexedModel();
            String[] lines = res.split("\n|\r");
            int posOffset = 0;
            boolean indicesOffset = false;
            int texOffset = 0;
            int normOffset = 0;
            ArrayList<Vector3f> positions = new ArrayList<Vector3f>();
            ArrayList<Vector2f> texCoords = new ArrayList<Vector2f>();
            ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
            ArrayList<Object> indices = new ArrayList();
            ArrayList<Material> materials = new ArrayList<Material>();
            HashMapWithDefault<OBJIndex, Integer> resultIndexMap = new HashMapWithDefault<OBJIndex, Integer>();
            HashMapWithDefault<Integer, Integer> normalIndexMap = new HashMapWithDefault<Integer, Integer>();
            HashMapWithDefault<Integer, Integer> indexMap = new HashMapWithDefault<Integer, Integer>();
            resultIndexMap.setDefault(-1);
            normalIndexMap.setDefault(-1);
            indexMap.setDefault(-1);
            HashMap<ObjObject, IndexedModel> map = new HashMap<ObjObject, IndexedModel>();
            ObjObject currentObject = null;
            HashMap<ObjObject, IndexedModel[]> objects = new HashMap<ObjObject, IndexedModel[]>();
            currentObject = new ObjObject("main");
            objects.put(currentObject, new IndexedModel[]{result, normalModel});
            for (String line : lines) {
                String[] parts;
                if (line == null || line.trim().equals("") || (parts = OBJLoader.trim(line.split(" "))).length == 0 || parts[0].equals(COMMENT)) continue;
                if (parts[0].equals(POSITION)) {
                    positions.add(new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
                    continue;
                }
                if (parts[0].equals(FACE)) {
                    for (int i2 = 0; i2 < parts.length - 3; ++i2) {
                        indices.add(this.parseOBJIndex(parts[1], posOffset, texOffset, normOffset));
                        indices.add(this.parseOBJIndex(parts[2 + i2], posOffset, texOffset, normOffset));
                        indices.add(this.parseOBJIndex(parts[3 + i2], posOffset, texOffset, normOffset));
                    }
                    continue;
                }
                if (parts[0].equals(NORMAL)) {
                    normals.add(new Vector3f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
                    continue;
                }
                if (parts[0].equals(TEX_COORDS)) {
                    texCoords.add(new Vector2f(Float.parseFloat(parts[1]), Float.parseFloat(parts[2])));
                    continue;
                }
                if (parts[0].equals(NEW_MATERIAL)) {
                    String path = startPath + parts[1];
                    MtlMaterialLib material = new MtlMaterialLib(path);
                    material.parse(this.read(OBJLoader.class.getResourceAsStream(path)));
                    materials.addAll(material.getMaterials());
                    continue;
                }
                if (parts[0].equals(USE_MATERIAL)) {
                    currentObject.material = this.getMaterial(materials, parts[1]);
                    continue;
                }
                if (!parts[0].equals(NEW_OBJECT) && !parts[0].equals(NEW_GROUP)) continue;
                result.getObjIndices().addAll(indices);
                normalModel.getObjIndices().addAll(indices);
                result = new IndexedModel();
                normalModel = new IndexedModel();
                indices.clear();
                currentObject = new ObjObject(parts[1]);
                objects.put(currentObject, new IndexedModel[]{result, normalModel});
            }
            result.getObjIndices().addAll(indices);
            normalModel.getObjIndices().addAll(indices);
            for (ObjObject object : objects.keySet()) {
                int i3;
                result = ((IndexedModel[])objects.get(object))[0];
                normalModel = ((IndexedModel[])objects.get(object))[1];
                indices = result.getObjIndices();
                map.put(object, result);
                object.center = result.computeCenter();
                for (i3 = 0; i3 < indices.size(); ++i3) {
                    int normalModelIndex;
                    int modelVertexIndex;
                    Vector3f normal;
                    OBJIndex current = (OBJIndex)indices.get(i3);
                    Vector3f pos = (Vector3f)positions.get(current.positionIndex);
                    Vector2f texCoord = this.hasTexCoords ? (Vector2f)texCoords.get(current.texCoordsIndex) : new Vector2f();
                    if (this.hasNormals) {
                        try {
                            normal = (Vector3f)normals.get(current.normalIndex);
                        }
                        catch (Exception e2) {
                            normal = new Vector3f();
                        }
                    } else {
                        normal = new Vector3f();
                    }
                    if ((modelVertexIndex = ((Integer)resultIndexMap.get(current)).intValue()) == -1) {
                        resultIndexMap.put(current, result.getPositions().size());
                        modelVertexIndex = result.getPositions().size();
                        result.getPositions().add(pos);
                        result.getTexCoords().add(texCoord);
                        if (this.hasNormals) {
                            result.getNormals().add(normal);
                        }
                        result.getTangents().add(new Vector3f());
                    }
                    if ((normalModelIndex = ((Integer)normalIndexMap.get(current.positionIndex)).intValue()) == -1) {
                        normalModelIndex = normalModel.getPositions().size();
                        normalIndexMap.put(current.positionIndex, normalModelIndex);
                        normalModel.getPositions().add(pos);
                        normalModel.getTexCoords().add(texCoord);
                        normalModel.getNormals().add(normal);
                        normalModel.getTangents().add(new Vector3f());
                    }
                    result.getIndices().add(modelVertexIndex);
                    normalModel.getIndices().add(normalModelIndex);
                    indexMap.put(modelVertexIndex, normalModelIndex);
                }
                if (this.hasNormals) continue;
                normalModel.computeNormals();
                for (i3 = 0; i3 < result.getNormals().size(); ++i3) {
                    result.getNormals().add(normalModel.getNormals().get((Integer)indexMap.get(i3)));
                }
            }
            return map;
        }
        catch (Exception e3) {
            throw new RuntimeException("Error while loading model", e3);
        }
    }

    private Material getMaterial(ArrayList<Material> materials, String id2) {
        for (Material mat : materials) {
            if (!mat.getName().equals(id2)) continue;
            return mat;
        }
        return null;
    }

    protected String read(InputStream resource) throws IOException {
        int i2;
        byte[] buffer = new byte[65565];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((i2 = resource.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, i2);
        }
        out.flush();
        out.close();
        return new String(out.toByteArray(), "UTF-8");
    }

    public OBJIndex parseOBJIndex(String token, int posOffset, int texCoordsOffset, int normalOffset) {
        OBJIndex index = new OBJIndex();
        String[] values = token.split("/");
        index.positionIndex = Integer.parseInt(values[0]) - 1 - posOffset;
        if (values.length > 1) {
            if (values[1] != null && !values[1].equals("")) {
                index.texCoordsIndex = Integer.parseInt(values[1]) - 1 - texCoordsOffset;
            }
            this.hasTexCoords = true;
            if (values.length > 2) {
                index.normalIndex = Integer.parseInt(values[2]) - 1 - normalOffset;
                this.hasNormals = true;
            }
        }
        return index;
    }

    public static String[] trim(String[] split) {
        ArrayList<String> strings = new ArrayList<String>();
        for (String s2 : split) {
            if (s2 == null || s2.trim().equals("")) continue;
            strings.add(s2);
        }
        return strings.toArray(new String[0]);
    }

    public static final class OBJIndex {
        int positionIndex;
        int texCoordsIndex;
        int normalIndex;

        public boolean equals(Object o2) {
            if (o2 instanceof OBJIndex) {
                OBJIndex index = (OBJIndex)o2;
                return index.normalIndex == this.normalIndex && index.positionIndex == this.positionIndex && index.texCoordsIndex == this.texCoordsIndex;
            }
            return false;
        }

        public int hashCode() {
            int base = 17;
            int multiplier = 31;
            int result = 17;
            result = 31 * result + this.positionIndex;
            result = 31 * result + this.texCoordsIndex;
            result = 31 * result + this.normalIndex;
            return result;
        }
    }
}

