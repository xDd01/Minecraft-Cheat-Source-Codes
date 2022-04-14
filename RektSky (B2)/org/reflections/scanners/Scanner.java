package org.reflections.scanners;

import java.util.function.*;
import org.reflections.vfs.*;
import org.reflections.*;

public interface Scanner
{
    void setConfiguration(final Configuration p0);
    
    Scanner filterResultsBy(final Predicate<String> p0);
    
    boolean acceptsInput(final String p0);
    
    Object scan(final Vfs.File p0, final Object p1, final Store p2);
    
    boolean acceptResult(final String p0);
}
