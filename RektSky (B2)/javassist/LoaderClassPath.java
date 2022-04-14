package javassist;

import java.lang.ref.*;
import java.io.*;
import java.net.*;

public class LoaderClassPath implements ClassPath
{
    private Reference<ClassLoader> clref;
    
    public LoaderClassPath(final ClassLoader cl) {
        this.clref = new WeakReference<ClassLoader>(cl);
    }
    
    @Override
    public String toString() {
        return (this.clref.get() == null) ? "<null>" : this.clref.get().toString();
    }
    
    @Override
    public InputStream openClassfile(final String classname) throws NotFoundException {
        final String cname = classname.replace('.', '/') + ".class";
        final ClassLoader cl = this.clref.get();
        if (cl == null) {
            return null;
        }
        final InputStream is = cl.getResourceAsStream(cname);
        return is;
    }
    
    @Override
    public URL find(final String classname) {
        final String cname = classname.replace('.', '/') + ".class";
        final ClassLoader cl = this.clref.get();
        if (cl == null) {
            return null;
        }
        final URL url = cl.getResource(cname);
        return url;
    }
}
