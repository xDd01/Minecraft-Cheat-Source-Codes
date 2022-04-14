package org.reflections.scanners;

import org.reflections.*;
import java.lang.reflect.*;
import org.reflections.util.*;
import org.reflections.adapters.*;
import javassist.bytecode.*;
import java.util.*;

public class MethodParameterNamesScanner extends AbstractScanner
{
    @Override
    public void scan(final Object cls, final Store store) {
        final MetadataAdapter md = this.getMetadataAdapter();
        for (final Object method : md.getMethods(cls)) {
            final String key = md.getMethodFullKey(cls, method);
            if (this.acceptResult(key)) {
                final CodeAttribute codeAttribute = ((MethodInfo)method).getCodeAttribute();
                final LocalVariableAttribute table = (codeAttribute != null) ? ((LocalVariableAttribute)codeAttribute.getAttribute("LocalVariableTable")) : null;
                final int length = (table != null) ? table.tableLength() : 0;
                int i = Modifier.isStatic(((MethodInfo)method).getAccessFlags()) ? 0 : 1;
                if (i >= length) {
                    continue;
                }
                final List<String> names = new ArrayList<String>(length - i);
                while (i < length) {
                    names.add(((MethodInfo)method).getConstPool().getUtf8Info(table.nameIndex(i++)));
                }
                this.put(store, key, Utils.join(names, ", "));
            }
        }
    }
}
