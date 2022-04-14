package io.netty.channel;

import io.netty.util.*;
import java.nio.channels.*;
import java.io.*;

public interface FileRegion extends ReferenceCounted
{
    long position();
    
    long transfered();
    
    long count();
    
    long transferTo(final WritableByteChannel p0, final long p1) throws IOException;
}
