package org.lwjgl.util.glu.tessellation;

class CachedVertex
{
    public double[] coords;
    public Object data;
    
    CachedVertex() {
        this.coords = new double[3];
    }
}
