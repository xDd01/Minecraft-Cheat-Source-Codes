package org.newdawn.slick.tiled;

public class Tile
{
    public int x;
    public int y;
    public String layerName;
    public int gid;
    public String tilesetName;
    
    public Tile(final int x, final int y, final String layerName, final int gid, final String tileset) {
        this.x = x;
        this.y = y;
        this.layerName = layerName;
        this.gid = gid;
        this.tilesetName = tileset;
    }
}
