package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.nodes.*;

public interface Construct
{
    Object construct(final Node p0);
    
    void construct2ndStep(final Node p0, final Object p1);
}
