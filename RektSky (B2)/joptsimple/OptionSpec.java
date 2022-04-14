package joptsimple;

import java.util.*;

public interface OptionSpec<V>
{
    List<V> values(final OptionSet p0);
    
    V value(final OptionSet p0);
    
    List<String> options();
    
    boolean isForHelp();
}
