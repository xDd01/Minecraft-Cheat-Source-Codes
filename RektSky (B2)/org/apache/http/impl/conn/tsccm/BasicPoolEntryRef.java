package org.apache.http.impl.conn.tsccm;

import org.apache.http.conn.routing.*;
import java.lang.ref.*;
import org.apache.http.util.*;

@Deprecated
public class BasicPoolEntryRef extends WeakReference<BasicPoolEntry>
{
    private final HttpRoute route;
    
    public BasicPoolEntryRef(final BasicPoolEntry entry, final ReferenceQueue<Object> queue) {
        super(entry, queue);
        Args.notNull(entry, "Pool entry");
        this.route = entry.getPlannedRoute();
    }
    
    public final HttpRoute getRoute() {
        return this.route;
    }
}
