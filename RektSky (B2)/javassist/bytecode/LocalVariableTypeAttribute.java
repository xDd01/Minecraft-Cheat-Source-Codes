package javassist.bytecode;

import java.io.*;
import java.util.*;

public class LocalVariableTypeAttribute extends LocalVariableAttribute
{
    public static final String tag = "LocalVariableTypeTable";
    
    public LocalVariableTypeAttribute(final ConstPool cp) {
        super(cp, "LocalVariableTypeTable", new byte[2]);
        ByteArray.write16bit(0, this.info, 0);
    }
    
    LocalVariableTypeAttribute(final ConstPool cp, final int n, final DataInputStream in) throws IOException {
        super(cp, n, in);
    }
    
    private LocalVariableTypeAttribute(final ConstPool cp, final byte[] dest) {
        super(cp, "LocalVariableTypeTable", dest);
    }
    
    @Override
    String renameEntry(final String desc, final String oldname, final String newname) {
        return SignatureAttribute.renameClass(desc, oldname, newname);
    }
    
    @Override
    String renameEntry(final String desc, final Map<String, String> classnames) {
        return SignatureAttribute.renameClass(desc, classnames);
    }
    
    @Override
    LocalVariableAttribute makeThisAttr(final ConstPool cp, final byte[] dest) {
        return new LocalVariableTypeAttribute(cp, dest);
    }
}
