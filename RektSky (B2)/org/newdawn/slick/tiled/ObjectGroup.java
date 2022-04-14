package org.newdawn.slick.tiled;

import org.newdawn.slick.util.*;
import org.w3c.dom.*;
import org.newdawn.slick.*;
import java.util.*;

public class ObjectGroup
{
    public int index;
    public String name;
    public ArrayList<GroupObject> objects;
    public int width;
    public int height;
    private HashMap<String, Integer> nameToObjectMap;
    public Properties props;
    TiledMap map;
    public float opacity;
    public boolean visible;
    public Color color;
    
    public ObjectGroup(final Element element, final TiledMap map) throws SlickException {
        this.nameToObjectMap = new HashMap<String, Integer>();
        this.opacity = 1.0f;
        this.visible = true;
        this.color = new Color(Color.white);
        this.map = map;
        TiledMapPlus tmap = null;
        if (map instanceof TiledMapPlus) {
            tmap = (TiledMapPlus)map;
        }
        this.name = element.getAttribute("name");
        final String widthS = element.getAttribute("width");
        if (widthS != null && widthS.length() != 0) {
            this.width = Integer.parseInt(widthS);
        }
        final String heightS = element.getAttribute("height");
        if (heightS != null && heightS.length() != 0) {
            this.height = Integer.parseInt(heightS);
        }
        if (this.width == 0 || this.height == 0) {
            Log.warn("ObjectGroup " + this.name + " has zero size (width or height equal to 0)");
        }
        this.objects = new ArrayList<GroupObject>();
        final String opacityS = element.getAttribute("opacity");
        if (opacityS != null && opacityS.length() != 0) {
            this.opacity = Float.parseFloat(opacityS);
        }
        if ("0".equals(element.getAttribute("visible"))) {
            this.visible = false;
        }
        final String colorS = element.getAttribute("color");
        if (colorS != null && colorS.length() != 0) {
            try {
                this.color = Color.decode(colorS);
            }
            catch (NumberFormatException e) {
                Log.warn("color attribute in element " + this.name + " could not be parsed; reverting to white");
            }
        }
        final Element propsElement = (Element)element.getElementsByTagName("properties").item(0);
        if (propsElement != null) {
            final NodeList properties = propsElement.getElementsByTagName("property");
            if (properties != null) {
                this.props = new Properties();
                for (int p = 0; p < properties.getLength(); ++p) {
                    final Element propElement = (Element)properties.item(p);
                    final String name = propElement.getAttribute("name");
                    final String value = propElement.getAttribute("value");
                    this.props.setProperty(name, value);
                }
            }
        }
        final NodeList objectNodes = element.getElementsByTagName("object");
        for (int i = 0; i < objectNodes.getLength(); ++i) {
            final Element objElement = (Element)objectNodes.item(i);
            GroupObject object = null;
            if (tmap != null) {
                object = new GroupObject(objElement, tmap);
            }
            else {
                object = new GroupObject(objElement);
            }
            object.index = i;
            this.objects.add(object);
        }
    }
    
    public GroupObject getObject(final String objectName) {
        final GroupObject g = this.objects.get(this.nameToObjectMap.get(objectName));
        return g;
    }
    
    public ArrayList<GroupObject> getObjectsOfType(final String type) {
        final ArrayList<GroupObject> foundObjects = new ArrayList<GroupObject>();
        for (final GroupObject object : this.objects) {
            if (object.type.equals(type)) {
                foundObjects.add(object);
            }
        }
        return foundObjects;
    }
    
    public void removeObject(final String objectName) {
        final int objectOffset = this.nameToObjectMap.get(objectName);
        final GroupObject object = this.objects.remove(objectOffset);
    }
    
    public void setObjectNameMapping(final HashMap<String, Integer> map) {
        this.nameToObjectMap = map;
    }
    
    public void addObject(final GroupObject object) {
        this.objects.add(object);
        this.nameToObjectMap.put(object.name, this.objects.size());
    }
    
    public ArrayList<GroupObject> getObjects() {
        return this.objects;
    }
}
