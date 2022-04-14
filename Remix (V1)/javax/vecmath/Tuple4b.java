package javax.vecmath;

import java.io.*;

public abstract class Tuple4b implements Serializable, Cloneable
{
    static final long serialVersionUID = -8226727741811898211L;
    public byte x;
    public byte y;
    public byte z;
    public byte w;
    
    public Tuple4b(final byte x, final byte y, final byte z, final byte w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Tuple4b(final byte[] array) {
        this.x = array[0];
        this.y = array[1];
        this.z = array[2];
        this.w = array[3];
    }
    
    public Tuple4b(final Tuple4b tuple4b) {
        this.x = tuple4b.x;
        this.y = tuple4b.y;
        this.z = tuple4b.z;
        this.w = tuple4b.w;
    }
    
    public Tuple4b() {
        this.x = 0;
        this.y = 0;
        this.z = 0;
        this.w = 0;
    }
    
    @Override
    public String toString() {
        return "(" + (this.x & 0xFF) + ", " + (this.y & 0xFF) + ", " + (this.z & 0xFF) + ", " + (this.w & 0xFF) + ")";
    }
    
    public final void get(final byte[] array) {
        array[0] = this.x;
        array[1] = this.y;
        array[2] = this.z;
        array[3] = this.w;
    }
    
    public final void get(final Tuple4b tuple4b) {
        tuple4b.x = this.x;
        tuple4b.y = this.y;
        tuple4b.z = this.z;
        tuple4b.w = this.w;
    }
    
    public final void set(final Tuple4b tuple4b) {
        this.x = tuple4b.x;
        this.y = tuple4b.y;
        this.z = tuple4b.z;
        this.w = tuple4b.w;
    }
    
    public final void set(final byte[] array) {
        this.x = array[0];
        this.y = array[1];
        this.z = array[2];
        this.w = array[3];
    }
    
    public boolean equals(final Tuple4b tuple4b) {
        try {
            return this.x == tuple4b.x && this.y == tuple4b.y && this.z == tuple4b.z && this.w == tuple4b.w;
        }
        catch (NullPointerException ex) {
            return false;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        try {
            final Tuple4b tuple4b = (Tuple4b)o;
            return this.x == tuple4b.x && this.y == tuple4b.y && this.z == tuple4b.z && this.w == tuple4b.w;
        }
        catch (NullPointerException ex) {
            return false;
        }
        catch (ClassCastException ex2) {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        return (this.x & 0xFF) << 0 | (this.y & 0xFF) << 8 | (this.z & 0xFF) << 16 | (this.w & 0xFF) << 24;
    }
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
    
    public final byte getX() {
        return this.x;
    }
    
    public final void setX(final byte x) {
        this.x = x;
    }
    
    public final byte getY() {
        return this.y;
    }
    
    public final void setY(final byte y) {
        this.y = y;
    }
    
    public final byte getZ() {
        return this.z;
    }
    
    public final void setZ(final byte z) {
        this.z = z;
    }
    
    public final byte getW() {
        return this.w;
    }
    
    public final void setW(final byte w) {
        this.w = w;
    }
}
