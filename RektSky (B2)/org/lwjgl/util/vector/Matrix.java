package org.lwjgl.util.vector;

import java.io.*;
import java.nio.*;

public abstract class Matrix implements Serializable
{
    protected Matrix() {
    }
    
    public abstract Matrix setIdentity();
    
    public abstract Matrix invert();
    
    public abstract Matrix load(final FloatBuffer p0);
    
    public abstract Matrix loadTranspose(final FloatBuffer p0);
    
    public abstract Matrix negate();
    
    public abstract Matrix store(final FloatBuffer p0);
    
    public abstract Matrix storeTranspose(final FloatBuffer p0);
    
    public abstract Matrix transpose();
    
    public abstract Matrix setZero();
    
    public abstract float determinant();
}
