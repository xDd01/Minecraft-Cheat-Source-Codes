package shadersmod.client;

import java.util.*;

static final class ShaderPackParser$1 implements Comparator {
    public int compare(final ShaderOption o1, final ShaderOption o2) {
        return o1.getName().compareToIgnoreCase(o2.getName());
    }
    
    @Override
    public int compare(final Object x0, final Object x1) {
        return this.compare((ShaderOption)x0, (ShaderOption)x1);
    }
}