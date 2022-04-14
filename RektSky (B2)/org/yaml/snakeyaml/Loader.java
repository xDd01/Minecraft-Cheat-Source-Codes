package org.yaml.snakeyaml;

import org.yaml.snakeyaml.resolver.*;
import org.yaml.snakeyaml.constructor.*;

public final class Loader
{
    protected final BaseConstructor constructor;
    protected Resolver resolver;
    
    public Loader(final BaseConstructor constructor) {
        this.constructor = constructor;
    }
    
    public Loader() {
        this(new Constructor());
    }
}
