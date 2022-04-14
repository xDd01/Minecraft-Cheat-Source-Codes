package javassist;

import java.util.jar.*;
import java.util.*;
import java.io.*;
import java.net.*;

final class JarClassPath implements ClassPath
{
    List<String> jarfileEntries;
    String jarfileURL;
    
    JarClassPath(final String pathname) throws NotFoundException {
        JarFile jarfile = null;
        try {
            jarfile = new JarFile(pathname);
            this.jarfileEntries = new ArrayList<String>();
            for (final JarEntry je : Collections.list(jarfile.entries())) {
                if (je.getName().endsWith(".class")) {
                    this.jarfileEntries.add(je.getName());
                }
            }
            this.jarfileURL = new File(pathname).getCanonicalFile().toURI().toURL().toString();
            return;
        }
        catch (IOException ex) {}
        finally {
            if (null != jarfile) {
                try {
                    jarfile.close();
                }
                catch (IOException ex2) {}
            }
        }
        throw new NotFoundException(pathname);
    }
    
    @Override
    public InputStream openClassfile(final String classname) throws NotFoundException {
        final URL jarURL = this.find(classname);
        if (null != jarURL) {
            try {
                if (ClassPool.cacheOpenedJarFile) {
                    return jarURL.openConnection().getInputStream();
                }
                final URLConnection con = jarURL.openConnection();
                con.setUseCaches(false);
                return con.getInputStream();
            }
            catch (IOException e) {
                throw new NotFoundException("broken jar file?: " + classname);
            }
        }
        return null;
    }
    
    @Override
    public URL find(final String classname) {
        final String jarname = classname.replace('.', '/') + ".class";
        if (this.jarfileEntries.contains(jarname)) {
            try {
                return new URL(String.format("jar:%s!/%s", this.jarfileURL, jarname));
            }
            catch (MalformedURLException ex) {}
        }
        return null;
    }
    
    @Override
    public String toString() {
        return (this.jarfileURL == null) ? "<null>" : this.jarfileURL.toString();
    }
}
