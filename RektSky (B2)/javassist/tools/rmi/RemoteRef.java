package javassist.tools.rmi;

import java.io.*;

public class RemoteRef implements Serializable
{
    private static final long serialVersionUID = 1L;
    public int oid;
    public String classname;
    
    public RemoteRef(final int i) {
        this.oid = i;
        this.classname = null;
    }
    
    public RemoteRef(final int i, final String name) {
        this.oid = i;
        this.classname = name;
    }
}
