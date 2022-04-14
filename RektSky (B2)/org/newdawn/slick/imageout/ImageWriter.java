package org.newdawn.slick.imageout;

import org.newdawn.slick.*;
import java.io.*;

public interface ImageWriter
{
    void saveImage(final Image p0, final String p1, final OutputStream p2, final boolean p3) throws IOException;
}
