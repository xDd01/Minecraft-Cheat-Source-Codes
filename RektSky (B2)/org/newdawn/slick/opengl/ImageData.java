package org.newdawn.slick.opengl;

import java.nio.*;

public interface ImageData
{
    Format getFormat();
    
    int getWidth();
    
    int getHeight();
    
    int getTexWidth();
    
    int getTexHeight();
    
    ByteBuffer getImageBufferData();
    
    public enum Format
    {
        RGB(3, 24, false, 6407), 
        BGRA(4, 32, true, 32993), 
        RGBA(4, 32, true, 6408), 
        ALPHA(1, 8, true, 6406), 
        GRAY(1, 8, false, 6409), 
        GRAYALPHA(2, 16, true, 6410);
        
        private final boolean alpha;
        private final int bitdepth;
        private final int components;
        private final int OGLtype;
        
        private Format(final int comp, final int depth, final boolean hasAlpha, final int openGL) {
            this.components = comp;
            this.bitdepth = depth;
            this.alpha = hasAlpha;
            this.OGLtype = openGL;
        }
        
        public boolean hasAlpha() {
            return this.alpha;
        }
        
        public int getBitPerPixel() {
            return this.bitdepth;
        }
        
        public int getBitPerColor() {
            return this.bitdepth / this.components;
        }
        
        public int getColorComponents() {
            return this.components;
        }
        
        public int getOGLType() {
            return this.OGLtype;
        }
    }
}
