package org.reflections.scanners;

import org.reflections.util.*;
import java.util.function.*;
import org.reflections.*;
import java.util.*;

public class SubTypesScanner extends AbstractScanner
{
    public SubTypesScanner() {
        this(true);
    }
    
    public SubTypesScanner(final boolean excludeObjectClass) {
        if (excludeObjectClass) {
            this.filterResultsBy(new FilterBuilder().exclude(Object.class.getName()));
        }
    }
    
    @Override
    public void scan(final Object cls, final Store store) {
        final String className = this.getMetadataAdapter().getClassName(cls);
        final String superclass = this.getMetadataAdapter().getSuperclassName(cls);
        if (this.acceptResult(superclass)) {
            this.put(store, superclass, className);
        }
        for (final String anInterface : this.getMetadataAdapter().getInterfacesNames(cls)) {
            if (this.acceptResult(anInterface)) {
                this.put(store, anInterface, className);
            }
        }
    }
}
