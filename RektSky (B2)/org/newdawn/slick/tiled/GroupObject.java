package org.newdawn.slick.tiled;

import org.newdawn.slick.geom.*;
import org.w3c.dom.*;
import org.newdawn.slick.*;
import java.util.*;

public class GroupObject
{
    public int index;
    public String name;
    public String type;
    public int x;
    public int y;
    public int width;
    public int height;
    String image;
    public Properties props;
    public int gid;
    TiledMapPlus map;
    ObjectType objectType;
    Polygon points;
    
    public GroupObject(final Element element) throws SlickException {
        this.name = "";
        this.type = "";
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.gid = -1;
        if (element.getAttribute("gid") != "") {
            this.gid = Integer.parseInt(element.getAttribute("gid"));
            this.objectType = ObjectType.IMAGE;
        }
        if (element.getElementsByTagName("polyline").item(0) != null) {
            this.objectType = ObjectType.POLYLINE;
        }
        else if (element.getElementsByTagName("polygon").item(0) != null) {
            this.objectType = ObjectType.POLYGON;
        }
        else {
            this.objectType = ObjectType.RECTANGLE;
        }
        if (this.objectType == ObjectType.IMAGE) {
            if (element.getAttribute("width") != "") {
                this.width = Integer.parseInt(element.getAttribute("width"));
            }
            if (element.getAttribute("height") != "") {
                this.height = Integer.parseInt(element.getAttribute("height"));
            }
            if (element.getAttribute("name") != "") {
                this.name = element.getAttribute("name");
            }
            if (element.getAttribute("type") != "") {
                this.type = element.getAttribute("type");
            }
        }
        else if (this.objectType == ObjectType.POLYGON || this.objectType == ObjectType.POLYLINE) {
            this.name = element.getAttribute("name");
            Element polyLine;
            if (this.objectType == ObjectType.POLYGON) {
                polyLine = (Element)element.getElementsByTagName("polygon").item(0);
            }
            else {
                polyLine = (Element)element.getElementsByTagName("polyline").item(0);
            }
            final String pointsUnformatted = polyLine.getAttribute("points");
            final String[] arr$;
            final String[] pointsFormatted = arr$ = pointsUnformatted.split(" ");
            for (final String pointS : arr$) {
                final String[] pointArray = pointS.split(",");
                final float pointX = Float.parseFloat(pointArray[0]);
                final float pointY = Float.parseFloat(pointArray[1]);
                this.points.addPoint(pointX, pointY);
            }
        }
        else if (this.objectType == ObjectType.RECTANGLE) {
            this.objectType = ObjectType.RECTANGLE;
            this.width = Integer.parseInt(element.getAttribute("width"));
            this.height = Integer.parseInt(element.getAttribute("height"));
            this.name = element.getAttribute("name");
            this.type = element.getAttribute("type");
        }
        this.x = Integer.parseInt(element.getAttribute("x"));
        this.y = Integer.parseInt(element.getAttribute("y"));
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
    }
    
    public GroupObject(final Element element, final TiledMapPlus map) throws SlickException {
        this.name = "";
        this.type = "";
        this.x = 0;
        this.y = 0;
        this.width = 0;
        this.height = 0;
        this.gid = -1;
        this.map = map;
        if (element.getAttribute("gid") != "") {
            this.gid = Integer.parseInt(element.getAttribute("gid"));
            this.objectType = ObjectType.IMAGE;
        }
        if (this.objectType == ObjectType.IMAGE) {
            if (element.getAttribute("width") != "") {
                this.width = Integer.parseInt(element.getAttribute("width"));
            }
            if (element.getAttribute("height") != "") {
                this.height = Integer.parseInt(element.getAttribute("height"));
            }
            if (element.getAttribute("name") != "") {
                this.name = element.getAttribute("name");
            }
            if (element.getAttribute("type") != "") {
                this.type = element.getAttribute("type");
            }
        }
        else if (this.objectType == ObjectType.RECTANGLE) {
            this.width = Integer.parseInt(element.getAttribute("width"));
            this.height = Integer.parseInt(element.getAttribute("height"));
            this.name = element.getAttribute("name");
            this.type = element.getAttribute("type");
        }
        else if (this.objectType == ObjectType.POLYGON) {
            this.name = element.getAttribute("name");
            final Element polyLine = (Element)element.getElementsByTagName("polyline").item(0);
            final String pointsUnformatted = polyLine.getAttribute("points");
            final String[] arr$;
            final String[] pointsFormatted = arr$ = pointsUnformatted.split(" ");
            for (final String pointS : arr$) {
                final String[] pointArray = pointS.split(",");
                final float pointX = Float.parseFloat(pointArray[0]);
                final float pointY = Float.parseFloat(pointArray[1]);
                this.points.addPoint(pointX, pointY);
            }
        }
        this.x = Integer.parseInt(element.getAttribute("x"));
        this.y = Integer.parseInt(element.getAttribute("y"));
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
    }
    
    public void putProperty(final String propertyKey, final String propertyValue) {
        ((Hashtable<String, String>)this.props).put(propertyKey, propertyValue);
    }
    
    public void removeProperty(final String propertyKey) {
        this.props.remove(propertyKey);
    }
    
    public Image getImage() throws SlickException {
        if (this.objectType != ObjectType.IMAGE) {
            throw new SlickException("Object isn't an image object");
        }
        if (this.map == null) {
            throw new SlickException("Object doesn't belong to a map of type TiledMapPlus");
        }
        final TileSet tileset = this.map.getTileSetByGID(this.gid);
        final int tilesetTileID = this.gid - tileset.firstGID;
        return tileset.tiles.getSubImage(tileset.getTileX(tilesetTileID), tileset.getTileY(tilesetTileID), tileset.tileWidth, tileset.tileHeight);
    }
    
    public enum ObjectType
    {
        IMAGE, 
        RECTANGLE, 
        POLYGON, 
        POLYLINE;
    }
}
