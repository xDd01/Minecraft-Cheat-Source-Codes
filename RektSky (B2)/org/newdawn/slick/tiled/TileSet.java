package org.newdawn.slick.tiled;

import java.util.*;
import org.newdawn.slick.util.*;
import org.newdawn.slick.*;
import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class TileSet
{
    private final TiledMap map;
    public int index;
    public String name;
    public int firstGID;
    public int lastGID;
    public int tileWidth;
    public int tileHeight;
    public SpriteSheet tiles;
    public int tilesAcross;
    public int tilesDown;
    Properties tilesetProperties;
    private HashMap<Integer, Properties> tileProperties;
    public int tileSpacing;
    public int tileMargin;
    public String imageref;
    
    public TileSet(final TiledMap map, Element element, final boolean loadImage) throws SlickException {
        this.lastGID = Integer.MAX_VALUE;
        this.tilesetProperties = new Properties();
        this.tileProperties = new HashMap<Integer, Properties>();
        this.tileSpacing = 0;
        this.tileMargin = 0;
        this.map = map;
        this.firstGID = Integer.parseInt(element.getAttribute("firstgid"));
        final String source = element.getAttribute("source");
        if (source != null && !source.equals("")) {
            try {
                final InputStream in = ResourceLoader.getResourceAsStream(map.getTilesLocation() + "/" + source);
                final DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                final Document doc = builder.parse(in);
                final Element docElement = element = doc.getDocumentElement();
            }
            catch (Exception e) {
                Log.error(e);
                throw new SlickException("Unable to load or parse sourced tileset: " + this.map.tilesLocation + "/" + source);
            }
        }
        this.name = element.getAttribute("name");
        final String tileWidthString = element.getAttribute("tilewidth");
        final String tileHeightString = element.getAttribute("tileheight");
        if (tileWidthString.length() == 0 || tileHeightString.length() == 0) {
            throw new SlickException("TiledMap requires that the map be created with tilesets that use a single image.  Check the WiKi for more complete information.");
        }
        this.tileWidth = Integer.parseInt(tileWidthString);
        this.tileHeight = Integer.parseInt(tileHeightString);
        final String sv = element.getAttribute("spacing");
        if (sv != null && !sv.equals("")) {
            this.tileSpacing = Integer.parseInt(sv);
        }
        final String mv = element.getAttribute("margin");
        if (mv != null && !mv.equals("")) {
            this.tileMargin = Integer.parseInt(mv);
        }
        final NodeList list = element.getElementsByTagName("image");
        final Element imageNode = (Element)list.item(0);
        final String ref = imageNode.getAttribute("source");
        Color trans = null;
        final String t = imageNode.getAttribute("trans");
        if (t != null && t.length() > 0) {
            final int c = Integer.parseInt(t, 16);
            trans = new Color(c);
        }
        if (loadImage) {
            this.imageref = map.getTilesLocation() + "/" + ref;
            final Image image = new Image(map.getTilesLocation() + "/" + ref, false, 9728, trans);
            this.setTileSetImage(image);
        }
        final NodeList pElements = element.getElementsByTagName("tile");
        for (int i = 0; i < pElements.getLength(); ++i) {
            final Element tileElement = (Element)pElements.item(i);
            int id = Integer.parseInt(tileElement.getAttribute("id"));
            id += this.firstGID;
            final Properties tileProps = new Properties();
            final Element propsElement = (Element)tileElement.getElementsByTagName("properties").item(0);
            final NodeList tilePropertiesList = propsElement.getElementsByTagName("property");
            for (int p = 0; p < tilePropertiesList.getLength(); ++p) {
                final Element propElement = (Element)tilePropertiesList.item(p);
                final String name = propElement.getAttribute("name");
                final String value = propElement.getAttribute("value");
                tileProps.setProperty(name, value);
            }
            this.tileProperties.put(new Integer(id), tileProps);
        }
        final Properties tileProps2 = new Properties();
        final Element propsElement2 = (Element)element.getElementsByTagName("properties").item(0);
        if (propsElement2 != null) {
            final NodeList properties = propsElement2.getElementsByTagName("property");
            for (int p2 = 0; p2 < properties.getLength(); ++p2) {
                final Element propElement2 = (Element)properties.item(p2);
                final String name2 = propElement2.getAttribute("name");
                final String value2 = propElement2.getAttribute("value");
                this.tilesetProperties.setProperty(name2, value2);
            }
        }
    }
    
    public int getTileWidth() {
        return this.tileWidth;
    }
    
    public int getTileHeight() {
        return this.tileHeight;
    }
    
    public int getTileSpacing() {
        return this.tileSpacing;
    }
    
    public int getTileMargin() {
        return this.tileMargin;
    }
    
    public void setTileSetImage(final Image image) {
        this.tiles = new SpriteSheet(image, this.tileWidth, this.tileHeight, this.tileSpacing, this.tileMargin);
        this.tilesAcross = this.tiles.getHorizontalCount();
        this.tilesDown = this.tiles.getVerticalCount();
        if (this.tilesAcross <= 0) {
            this.tilesAcross = 1;
        }
        if (this.tilesDown <= 0) {
            this.tilesDown = 1;
        }
        this.lastGID = this.tilesAcross * this.tilesDown + this.firstGID - 1;
    }
    
    public Properties getProperties(final int globalID) {
        return this.tileProperties.get(new Integer(globalID));
    }
    
    public int getTileX(final int id) {
        return id % this.tilesAcross;
    }
    
    public int getTileY(final int id) {
        return id / this.tilesAcross;
    }
    
    public void setLimit(final int limit) {
        this.lastGID = limit;
    }
    
    public boolean contains(final int gid) {
        return gid >= this.firstGID && gid <= this.lastGID;
    }
}
