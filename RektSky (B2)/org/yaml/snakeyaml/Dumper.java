package org.yaml.snakeyaml;

import org.yaml.snakeyaml.representer.*;

public final class Dumper
{
    protected final Representer representer;
    protected final DumperOptions options;
    
    public Dumper(final Representer representer, final DumperOptions options) {
        this.representer = representer;
        this.options = options;
    }
    
    public Dumper(final DumperOptions options) {
        this(new Representer(), options);
    }
    
    public Dumper(final Representer representer) {
        this(representer, new DumperOptions());
    }
    
    public Dumper() {
        this(new Representer(), new DumperOptions());
    }
}
