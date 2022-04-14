package javassist;

import java.net.*;
import java.io.*;

final class DirClassPath implements ClassPath
{
    String directory;
    
    DirClassPath(final String dirName) {
        this.directory = dirName;
    }
    
    @Override
    public InputStream openClassfile(final String classname) {
        try {
            final char sep = File.separatorChar;
            final String filename = this.directory + sep + classname.replace('.', sep) + ".class";
            return new FileInputStream(filename.toString());
        }
        catch (FileNotFoundException ex) {}
        catch (SecurityException ex2) {}
        return null;
    }
    
    @Override
    public URL find(final String classname) {
        final char sep = File.separatorChar;
        final String filename = this.directory + sep + classname.replace('.', sep) + ".class";
        final File f = new File(filename);
        if (f.exists()) {
            try {
                return f.getCanonicalFile().toURI().toURL();
            }
            catch (MalformedURLException ex) {}
            catch (IOException ex2) {}
        }
        return null;
    }
    
    @Override
    public String toString() {
        return this.directory;
    }
}
