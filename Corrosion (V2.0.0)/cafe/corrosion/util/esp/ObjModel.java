/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.esp;

import cafe.corrosion.util.esp.Model;
import cafe.corrosion.util.esp.ObjEvent;
import cafe.corrosion.util.esp.ObjObject;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.output.ByteArrayOutputStream;

public abstract class ObjModel
extends Model {
    public List<ObjObject> objObjects = new ArrayList<ObjObject>();
    protected String filename;

    ObjModel() {
    }

    public ObjModel(String classpathElem) {
        this();
        this.filename = classpathElem;
        if (this.filename.contains("/")) {
            this.setID(this.filename.substring(this.filename.lastIndexOf("/") + 1));
        } else {
            this.setID(this.filename);
        }
    }

    protected byte[] read(InputStream resource) throws IOException {
        int i2;
        byte[] buffer = new byte[65565];
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        while ((i2 = resource.read(buffer, 0, buffer.length)) != -1) {
            out.write(buffer, 0, i2);
        }
        out.flush();
        out.close();
        return out.toByteArray();
    }

    public void renderGroup(ObjObject group) {
        if (this.fireEvent(new ObjEvent(this, ObjEvent.EventType.PRE_RENDER_GROUP).setData(group, group))) {
            this.renderGroupImpl(group);
        }
        this.fireEvent(new ObjEvent(this, ObjEvent.EventType.POST_RENDER_GROUP).setData(group, group));
    }

    @Override
    public void renderGroups(String groupsName) {
        if (this.fireEvent(new ObjEvent(this, ObjEvent.EventType.PRE_RENDER_GROUPS).setData(groupsName))) {
            this.renderGroupsImpl(groupsName);
        }
        this.fireEvent(new ObjEvent(this, ObjEvent.EventType.POST_RENDER_GROUPS).setData(groupsName));
    }

    @Override
    public void render() {
        if (this.fireEvent(new ObjEvent(this, ObjEvent.EventType.PRE_RENDER_ALL))) {
            this.renderImpl();
        }
        this.fireEvent(new ObjEvent(this, ObjEvent.EventType.POST_RENDER_ALL));
    }

    protected abstract void renderGroupsImpl(String var1);

    protected abstract void renderGroupImpl(ObjObject var1);

    protected abstract void renderImpl();

    public abstract boolean fireEvent(ObjEvent var1);
}

