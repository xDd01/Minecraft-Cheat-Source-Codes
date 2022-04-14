package org.newdawn.slick.font.effects;

import org.newdawn.slick.*;
import org.newdawn.slick.font.*;
import java.awt.*;
import java.awt.image.*;

public class FilterEffect implements Effect
{
    private BufferedImageOp filter;
    
    public FilterEffect() {
    }
    
    public FilterEffect(final BufferedImageOp filter) {
        this.filter = filter;
    }
    
    @Override
    public void draw(final BufferedImage image, final Graphics2D g, final UnicodeFont unicodeFont, final Glyph glyph) {
        final BufferedImage scratchImage = EffectUtil.getScratchImage();
        this.filter.filter(image, scratchImage);
        image.getGraphics().drawImage(scratchImage, 0, 0, null);
    }
    
    public BufferedImageOp getFilter() {
        return this.filter;
    }
    
    public void setFilter(final BufferedImageOp filter) {
        this.filter = filter;
    }
}
