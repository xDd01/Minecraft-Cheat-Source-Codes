package org.apache.logging.log4j.core.filter;

import org.apache.logging.log4j.core.*;

public interface Filterable
{
    void addFilter(final Filter p0);
    
    void removeFilter(final Filter p0);
    
    Filter getFilter();
    
    boolean hasFilter();
    
    boolean isFiltered(final LogEvent p0);
}
