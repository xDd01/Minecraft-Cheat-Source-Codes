package javassist;

import java.io.*;
import java.net.*;

public interface ClassPath
{
    InputStream openClassfile(final String p0) throws NotFoundException;
    
    URL find(final String p0);
}
