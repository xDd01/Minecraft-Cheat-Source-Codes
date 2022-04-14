package org.newdawn.slick.tiled;

import org.newdawn.slick.util.*;
import org.newdawn.slick.*;
import java.io.*;
import java.util.zip.*;
import org.w3c.dom.*;
import java.util.*;

public class Layer
{
    private static byte[] baseCodes;
    private final TiledMap map;
    public int index;
    public String name;
    public int[][][] data;
    public int width;
    public int height;
    public float opacity;
    public boolean visible;
    public Properties props;
    private TiledMapPlus tmap;
    
    public Layer(final TiledMap map, final Element element) throws SlickException {
        this.opacity = 1.0f;
        this.visible = true;
        this.map = map;
        if (map instanceof TiledMapPlus) {
            this.tmap = (TiledMapPlus)map;
        }
        this.name = element.getAttribute("name");
        this.width = Integer.parseInt(element.getAttribute("width"));
        this.height = Integer.parseInt(element.getAttribute("height"));
        this.data = new int[this.width][this.height][3];
        final String opacityS = element.getAttribute("opacity");
        if (!opacityS.equals("")) {
            this.opacity = Float.parseFloat(opacityS);
        }
        if (element.getAttribute("visible").equals("0")) {
            this.visible = false;
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
        final Element dataNode = (Element)element.getElementsByTagName("data").item(0);
        final String encoding = dataNode.getAttribute("encoding");
        final String compression = dataNode.getAttribute("compression");
        if (encoding.equals("base64") && compression.equals("gzip")) {
            try {
                final Node cdata = dataNode.getFirstChild();
                final char[] enc = cdata.getNodeValue().trim().toCharArray();
                final byte[] dec = this.decodeBase64(enc);
                final GZIPInputStream is = new GZIPInputStream(new ByteArrayInputStream(dec));
                this.readData(is);
                return;
            }
            catch (IOException e) {
                Log.error(e);
                throw new SlickException("Unable to decode base 64 block");
            }
        }
        if (!encoding.equals("base64") || !compression.equals("zlib")) {
            throw new SlickException("Unsupport tiled map type: " + encoding + "," + compression + " (only gzip/zlib base64 supported)");
        }
        final Node cdata = dataNode.getFirstChild();
        final char[] enc = cdata.getNodeValue().trim().toCharArray();
        final byte[] dec = this.decodeBase64(enc);
        final InflaterInputStream is2 = new InflaterInputStream(new ByteArrayInputStream(dec));
        this.readData(is2);
    }
    
    protected void readData(final InputStream is) {
        for (int y = 0; y < this.height; ++y) {
            for (int x = 0; x < this.width; ++x) {
                int tileId = 0;
                try {
                    tileId |= is.read();
                    tileId |= is.read() << 8;
                    tileId |= is.read() << 16;
                    tileId |= is.read() << 24;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                if (tileId == 0) {
                    this.data[x][y][0] = -1;
                    this.data[x][y][1] = 0;
                    this.data[x][y][2] = 0;
                }
                else {
                    final int realTileId = tileId & 0x1FFFFFFF;
                    final TileSet set = this.map.findTileSet(realTileId);
                    if (set != null) {
                        this.data[x][y][0] = set.index;
                        this.data[x][y][1] = realTileId - set.firstGID;
                    }
                    this.data[x][y][2] = tileId;
                }
            }
        }
    }
    
    public int getTileID(final int x, final int y) {
        return this.data[x][y][2];
    }
    
    public void setTileID(final int x, final int y, final int tile) {
        if (tile == 0) {
            this.data[x][y][0] = -1;
            this.data[x][y][1] = 0;
            this.data[x][y][2] = 0;
        }
        else {
            final TileSet set = this.map.findTileSet(tile);
            this.data[x][y][0] = set.index;
            this.data[x][y][1] = tile - set.firstGID;
            this.data[x][y][2] = tile;
        }
    }
    
    public void render(final int x, final int y, final int sx, final int sy, final int width, final int ty, final boolean lineByLine, final int mapTileWidth, final int mapTileHeight) {
        for (int tileset = 0; tileset < this.map.getTileSetCount(); ++tileset) {
            TileSet set = null;
            for (int tx = 0; tx < width; ++tx) {
                if (sx + tx >= 0) {
                    if (sy + ty >= 0) {
                        if (sx + tx < this.width) {
                            if (sy + ty < this.height) {
                                if (this.data[sx + tx][sy + ty][0] == tileset) {
                                    if (set == null) {
                                        set = this.map.getTileSet(tileset);
                                        set.tiles.startUse();
                                    }
                                    final int sheetX = set.getTileX(this.data[sx + tx][sy + ty][1]);
                                    final int sheetY = set.getTileY(this.data[sx + tx][sy + ty][1]);
                                    final int tileOffsetY = set.tileHeight - mapTileHeight;
                                    final byte b = (byte)(((long)this.data[sx + tx][sy + ty][2] & 0xE0000000L) >> 29);
                                    set.tiles.setAlpha(this.opacity);
                                    set.tiles.renderInUse(x + tx * mapTileWidth, y + ty * mapTileHeight - tileOffsetY, sheetX, sheetY, b);
                                }
                            }
                        }
                    }
                }
            }
            if (lineByLine) {
                if (set != null) {
                    set.tiles.endUse();
                    set = null;
                }
                this.map.renderedLine(ty, ty + sy, this.index);
            }
            if (set != null) {
                set.tiles.endUse();
            }
        }
    }
    
    private byte[] decodeBase64(final char[] data) {
        int temp = data.length;
        for (int ix = 0; ix < data.length; ++ix) {
            if (data[ix] > '\u00ff' || Layer.baseCodes[data[ix]] < 0) {
                --temp;
            }
        }
        int len = temp / 4 * 3;
        if (temp % 4 == 3) {
            len += 2;
        }
        if (temp % 4 == 2) {
            ++len;
        }
        final byte[] out = new byte[len];
        int shift = 0;
        int accum = 0;
        int index = 0;
        for (int ix2 = 0; ix2 < data.length; ++ix2) {
            final int value = (data[ix2] > '\u00ff') ? -1 : Layer.baseCodes[data[ix2]];
            if (value >= 0) {
                accum <<= 6;
                shift += 6;
                accum |= value;
                if (shift >= 8) {
                    shift -= 8;
                    out[index++] = (byte)(accum >> shift & 0xFF);
                }
            }
        }
        if (index != out.length) {
            throw new RuntimeException("Data length appears to be wrong (wrote " + index + " should be " + out.length + ")");
        }
        return out;
    }
    
    public ArrayList<Tile> getTiles() throws SlickException {
        if (this.tmap == null) {
            throw new SlickException("This method can only be used with Layers loaded using TiledMapPlus");
        }
        final ArrayList<Tile> tiles = new ArrayList<Tile>();
        for (int x = 0; x < this.width; ++x) {
            for (int y = 0; y < this.height; ++y) {
                final String tilesetName = this.tmap.tileSets.get(this.data[x][y][0]).name;
                final Tile t = new Tile(x, y, this.name, y, tilesetName);
                tiles.add(t);
            }
        }
        return tiles;
    }
    
    public ArrayList<Tile> getTilesOfTileset(final String tilesetName) throws SlickException {
        if (this.tmap == null) {
            throw new SlickException("This method can only be used with Layers loaded using TiledMapPlus");
        }
        final ArrayList<Tile> tiles = new ArrayList<Tile>();
        final int tilesetID = this.tmap.getTilesetID(tilesetName);
        for (int x = 0; x < this.tmap.getWidth(); ++x) {
            for (int y = 0; y < this.tmap.getHeight(); ++y) {
                if (this.data[x][y][0] == tilesetID) {
                    final Tile t = new Tile(x, y, this.name, this.data[x][y][1], tilesetName);
                    tiles.add(t);
                }
            }
        }
        return tiles;
    }
    
    public void removeTile(final int x, final int y) {
        this.data[x][y][0] = -1;
    }
    
    public void setTile(final int x, final int y, final int tileOffset, final String tilesetName) throws SlickException {
        if (this.tmap == null) {
            throw new SlickException("This method can only be used with Layers loaded using TiledMapPlus");
        }
        final int tilesetID = this.tmap.getTilesetID(tilesetName);
        final TileSet tileset = this.tmap.getTileSet(tilesetID);
        this.data[x][y][0] = tileset.index;
        this.data[x][y][1] = tileOffset;
        this.data[x][y][2] = tileset.firstGID + tileOffset;
    }
    
    public boolean isTileOfTileset(final int x, final int y, final String tilesetName) throws SlickException {
        if (this.tmap == null) {
            throw new SlickException("This method can only be used with Layers loaded using TiledMapPlus");
        }
        final int tilesetID = this.tmap.getTilesetID(tilesetName);
        return this.data[x][y][0] == tilesetID;
    }
    
    static {
        Layer.baseCodes = new byte[256];
        for (int i = 0; i < 256; ++i) {
            Layer.baseCodes[i] = -1;
        }
        for (int i = 65; i <= 90; ++i) {
            Layer.baseCodes[i] = (byte)(i - 65);
        }
        for (int i = 97; i <= 122; ++i) {
            Layer.baseCodes[i] = (byte)(26 + i - 97);
        }
        for (int i = 48; i <= 57; ++i) {
            Layer.baseCodes[i] = (byte)(52 + i - 48);
        }
        Layer.baseCodes[43] = 62;
        Layer.baseCodes[47] = 63;
    }
}
