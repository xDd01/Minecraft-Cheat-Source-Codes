package org.newdawn.slick.tiled;

import java.io.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.newdawn.slick.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import java.util.*;
import javax.xml.transform.*;
import org.newdawn.slick.*;

public class TiledMapPlus extends TiledMap
{
    private HashMap<String, Integer> objectGroupNameToOffset;
    private HashMap<String, Integer> layerNameToIDMap;
    private HashMap<String, Integer> tilesetNameToIDMap;
    
    public TiledMapPlus(final String ref) throws SlickException {
        this(ref, true);
    }
    
    public TiledMapPlus(final String ref, final boolean loadTileSets) throws SlickException {
        super(ref, loadTileSets);
        this.objectGroupNameToOffset = new HashMap<String, Integer>();
        this.layerNameToIDMap = new HashMap<String, Integer>();
        this.tilesetNameToIDMap = new HashMap<String, Integer>();
        this.processNameToObjectMap();
        this.processLayerMap();
        this.processTilesetMap();
    }
    
    public TiledMapPlus(final String ref, final String tileSetsLocation) throws SlickException {
        super(ref, tileSetsLocation);
        this.objectGroupNameToOffset = new HashMap<String, Integer>();
        this.layerNameToIDMap = new HashMap<String, Integer>();
        this.tilesetNameToIDMap = new HashMap<String, Integer>();
        this.processNameToObjectMap();
        this.processLayerMap();
        this.processTilesetMap();
    }
    
    public TiledMapPlus(final InputStream in) throws SlickException {
        super(in);
        this.objectGroupNameToOffset = new HashMap<String, Integer>();
        this.layerNameToIDMap = new HashMap<String, Integer>();
        this.tilesetNameToIDMap = new HashMap<String, Integer>();
        this.processNameToObjectMap();
        this.processLayerMap();
        this.processTilesetMap();
    }
    
    public TiledMapPlus(final InputStream in, final String tileSetsLocation) throws SlickException {
        super(in, tileSetsLocation);
        this.objectGroupNameToOffset = new HashMap<String, Integer>();
        this.layerNameToIDMap = new HashMap<String, Integer>();
        this.tilesetNameToIDMap = new HashMap<String, Integer>();
        this.processNameToObjectMap();
        this.processLayerMap();
        this.processTilesetMap();
    }
    
    private void processNameToObjectMap() {
        for (int i = 0; i < this.getObjectGroupCount(); ++i) {
            final ObjectGroup g = this.objectGroups.get(i);
            this.objectGroupNameToOffset.put(g.name, i);
            final HashMap<String, Integer> nameToObjectMap = new HashMap<String, Integer>();
            for (int ib = 0; ib < this.getObjectCount(i); ++ib) {
                nameToObjectMap.put(this.getObjectName(i, ib), ib);
            }
            g.setObjectNameMapping(nameToObjectMap);
        }
    }
    
    private void processLayerMap() {
        for (int l = 0; l < this.layers.size(); ++l) {
            final Layer layer = this.layers.get(l);
            this.layerNameToIDMap.put(layer.name, l);
        }
    }
    
    private void processTilesetMap() {
        for (int t = 0; t < this.getTileSetCount(); ++t) {
            final TileSet tileSet = this.getTileSet(t);
            this.tilesetNameToIDMap.put(tileSet.name, t);
        }
    }
    
    public Layer getLayer(final String layerName) {
        final int layerID = this.layerNameToIDMap.get(layerName);
        return this.layers.get(layerID);
    }
    
    public ObjectGroup getObjectGroup(final String groupName) {
        return this.objectGroups.get(this.objectGroupNameToOffset.get(groupName));
    }
    
    public ArrayList<ObjectGroup> getObjectGroups() {
        return this.objectGroups;
    }
    
    public ArrayList<Tile> getAllTilesFromAllLayers(final String tilesetName) {
        final ArrayList<Tile> tiles = new ArrayList<Tile>();
        final int tilesetID = this.tilesetNameToIDMap.get(tilesetName);
        for (int x = 0; x < this.getWidth(); ++x) {
            for (int y = 0; y < this.getHeight(); ++y) {
                for (int l = 0; l < this.getLayerCount(); ++l) {
                    final Layer layer = this.layers.get(l);
                    if (layer.data[x][y][0] == tilesetID) {
                        final Tile t = new Tile(x, y, layer.name, layer.data[x][y][2], tilesetName);
                        tiles.add(t);
                    }
                }
            }
        }
        return tiles;
    }
    
    public void write(final OutputStream o) throws SlickException {
        try {
            final DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
            final DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
            final Document doc = docBuilder.newDocument();
            final Element map = doc.createElement("map");
            map.setAttribute("version", "1.0");
            if (this.orientation == 1) {
                map.setAttribute("orientation", "orthogonal");
            }
            else if (this.orientation == 2) {
                map.setAttribute("orientation", "isometric");
            }
            map.setAttribute("tilewidth", "" + this.tileWidth);
            map.setAttribute("tileheight", "" + this.tileHeight);
            map.setAttribute("width", "" + this.width);
            map.setAttribute("height", "" + this.height);
            doc.appendChild(map);
            for (int i = 0; i < this.tileSets.size(); ++i) {
                final TileSet tilesetData = this.tileSets.get(i);
                final Element tileset = doc.createElement("tileset");
                tileset.setAttribute("firstgid", "" + tilesetData.firstGID);
                tileset.setAttribute("name", tilesetData.name);
                tileset.setAttribute("tilewidth", "" + tilesetData.tileWidth);
                tileset.setAttribute("tileheight", "" + tilesetData.tileHeight);
                tileset.setAttribute("spacing", "" + tilesetData.tileSpacing);
                tileset.setAttribute("margin", "" + tilesetData.tileMargin);
                final Element image = doc.createElement("image");
                final String imagePath = tilesetData.imageref.replaceFirst(this.getTilesLocation() + "/", "");
                image.setAttribute("source", imagePath);
                image.setAttribute("width", "" + tilesetData.tiles.getWidth());
                image.setAttribute("height", "" + tilesetData.tiles.getHeight());
                tileset.appendChild(image);
                int tileCount = tilesetData.tiles.getHorizontalCount() * tilesetData.tiles.getVerticalCount();
                final Element tilesetProperties = doc.createElement("properties");
                final Properties tilesetPropertiesData = tilesetData.tilesetProperties;
                if (tilesetProperties != null) {
                    final Enumeration propertyEnum = tilesetPropertiesData.propertyNames();
                    while (propertyEnum.hasMoreElements()) {
                        final String key = propertyEnum.nextElement();
                        final Element tileProperty = doc.createElement("property");
                        tileProperty.setAttribute("name", key);
                        tileProperty.setAttribute("value", tilesetPropertiesData.getProperty(key));
                        tilesetProperties.appendChild(tileProperty);
                    }
                    tileset.appendChild(tilesetProperties);
                }
                if (tileCount == 1) {
                    ++tileCount;
                }
                for (int tileI = 0; tileI < tileCount; ++tileI) {
                    final Properties tileProperties = tilesetData.getProperties(tileI);
                    if (tileProperties != null) {
                        final Element tile = doc.createElement("tile");
                        final int tileID = tileI - tilesetData.firstGID;
                        tile.setAttribute("id", "" + tileID);
                        final Element tileProps = doc.createElement("properties");
                        final Enumeration propertyEnum2 = tilesetData.getProperties(tileI).propertyNames();
                        while (propertyEnum2.hasMoreElements()) {
                            final String key2 = propertyEnum2.nextElement();
                            final Element tileProperty2 = doc.createElement("property");
                            tileProperty2.setAttribute("name", key2);
                            tileProperty2.setAttribute("value", tileProperties.getProperty(key2));
                            tileProps.appendChild(tileProperty2);
                        }
                        tile.appendChild(tileProps);
                        tileset.appendChild(tile);
                    }
                }
                map.appendChild(tileset);
            }
            for (int i = 0; i < this.layers.size(); ++i) {
                final Element layer = doc.createElement("layer");
                final Layer layerData = this.layers.get(i);
                layer.setAttribute("name", layerData.name);
                layer.setAttribute("width", "" + layerData.width);
                layer.setAttribute("height", "" + layerData.height);
                layer.setAttribute("opacity", "" + layerData.opacity);
                if (layerData.visible) {
                    layer.setAttribute("visible", "1");
                }
                else {
                    layer.setAttribute("visible", "0");
                }
                final Element data = doc.createElement("data");
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                for (int tileY = 0; tileY < layerData.height; ++tileY) {
                    for (int tileX = 0; tileX < layerData.width; ++tileX) {
                        final int tileGID = layerData.data[tileX][tileY][2];
                        os.write(tileGID);
                        os.write(tileGID << 8);
                        os.write(tileGID << 16);
                        os.write(tileGID << 24);
                    }
                }
                os.flush();
                final String compressedData = Base64.encodeBytes(os.toByteArray(), 11);
                data.appendChild(doc.createTextNode(compressedData));
                data.setAttribute("encoding", "base64");
                data.setAttribute("compression", "gzip");
                layer.appendChild(data);
                map.appendChild(layer);
            }
            for (int objectGroupI = 0; objectGroupI < this.objectGroups.size(); ++objectGroupI) {
                final Element objectGroup = doc.createElement("objectgroup");
                final ObjectGroup objectGroupData = this.objectGroups.get(objectGroupI);
                objectGroup.setAttribute("color", "white");
                objectGroup.setAttribute("name", objectGroupData.name);
                objectGroup.setAttribute("width", "" + objectGroupData.width);
                objectGroup.setAttribute("height", "" + objectGroupData.height);
                objectGroup.setAttribute("opacity", "" + objectGroupData.opacity);
                if (objectGroupData.visible) {
                    objectGroup.setAttribute("visible", "1");
                }
                else {
                    objectGroup.setAttribute("visible", "0");
                }
                objectGroup.setAttribute("color", "#" + Float.toHexString(objectGroupData.color.r) + Float.toHexString(objectGroupData.color.g) + Float.toHexString(objectGroupData.color.b));
                for (int groupObjectI = 0; groupObjectI < objectGroupData.objects.size(); ++groupObjectI) {
                    final Element object = doc.createElement("object");
                    final GroupObject groupObject = objectGroupData.objects.get(groupObjectI);
                    object.setAttribute("x", "" + groupObject.x);
                    object.setAttribute("y", "" + groupObject.y);
                    switch (groupObject.objectType) {
                        case IMAGE: {
                            object.setAttribute("gid", "" + groupObject.gid);
                            break;
                        }
                        case RECTANGLE: {
                            object.setAttribute("name", groupObject.name);
                            object.setAttribute("type", groupObject.type);
                            object.setAttribute("width", "" + groupObject.width);
                            object.setAttribute("height", "" + groupObject.height);
                            break;
                        }
                        case POLYGON: {
                            final Element polygon = doc.createElement("polygon");
                            String polygonPoints = "";
                            for (int polygonPointIndex = 0; polygonPointIndex < groupObject.points.getPointCount() - 1; ++polygonPointIndex) {
                                polygonPoints = polygonPoints + groupObject.points.getPoint(polygonPointIndex)[0] + "," + groupObject.points.getPoint(polygonPointIndex)[1] + " ";
                            }
                            polygonPoints.trim();
                            polygon.setAttribute("points", polygonPoints);
                            break;
                        }
                        case POLYLINE: {
                            final Element polyline = doc.createElement("polyline");
                            String polylinePoints = "";
                            for (int polyLinePointIndex = 0; polyLinePointIndex < groupObject.points.getPointCount() - 1; ++polyLinePointIndex) {
                                polylinePoints = polylinePoints + groupObject.points.getPoint(polyLinePointIndex)[0] + "," + groupObject.points.getPoint(polyLinePointIndex)[1] + " ";
                            }
                            polylinePoints.trim();
                            polyline.setAttribute("points", polylinePoints);
                            break;
                        }
                    }
                    if (groupObject.props != null) {
                        final Element objectProps = doc.createElement("properties");
                        final Enumeration propertyEnum3 = groupObject.props.propertyNames();
                        while (propertyEnum3.hasMoreElements()) {
                            final String key3 = propertyEnum3.nextElement();
                            final Element objectProperty = doc.createElement("property");
                            objectProperty.setAttribute("name", key3);
                            objectProperty.setAttribute("value", groupObject.props.getProperty(key3));
                            objectProps.appendChild(objectProperty);
                        }
                        object.appendChild(objectProps);
                    }
                    objectGroup.appendChild(object);
                }
                map.appendChild(objectGroup);
            }
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            final Transformer transformer = transformerFactory.newTransformer();
            final DOMSource source = new DOMSource(doc);
            final StreamResult result = new StreamResult(o);
            transformer.transform(source, result);
        }
        catch (Exception e) {
            Log.error(e);
            throw new SlickException("Failed to write tiledmap", e);
        }
    }
    
    public ArrayList<Layer> getLayers() {
        return this.layers;
    }
    
    public ArrayList<TileSet> getTilesets() {
        return this.tileSets;
    }
    
    public Image getVisibleTile(final int x, final int y) throws SlickException {
        Image visibleTileImage = null;
        for (int l = this.getLayerCount() - 1; l > -1; --l) {
            if (visibleTileImage == null) {
                visibleTileImage = this.getTileImage(x, y, l);
            }
        }
        if (visibleTileImage == null) {
            throw new SlickException("Tile doesn't have a tileset!");
        }
        return visibleTileImage;
    }
    
    public int getTilesetID(final String tilesetName) {
        final int tilesetID = this.tilesetNameToIDMap.get(tilesetName);
        return tilesetID;
    }
    
    public int getLayerID(final String layerName) {
        final int layerID = this.layerNameToIDMap.get(layerName);
        return layerID;
    }
}
