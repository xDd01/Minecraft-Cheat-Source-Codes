/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.esp;

public abstract class Model {
    private String id;

    public abstract void render();

    public abstract void renderGroups(String var1);

    public void setID(String id2) {
        this.id = id2;
    }

    public String getID() {
        return this.id;
    }
}

