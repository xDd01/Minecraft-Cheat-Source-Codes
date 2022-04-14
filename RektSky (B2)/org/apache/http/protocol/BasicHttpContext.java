package org.apache.http.protocol;

import org.apache.http.annotation.*;
import java.util.*;
import java.util.concurrent.*;
import org.apache.http.util.*;

@Contract(threading = ThreadingBehavior.SAFE_CONDITIONAL)
public class BasicHttpContext implements HttpContext
{
    private final HttpContext parentContext;
    private final Map<String, Object> map;
    
    public BasicHttpContext() {
        this(null);
    }
    
    public BasicHttpContext(final HttpContext parentContext) {
        this.map = new ConcurrentHashMap<String, Object>();
        this.parentContext = parentContext;
    }
    
    @Override
    public Object getAttribute(final String id) {
        Args.notNull(id, "Id");
        Object obj = this.map.get(id);
        if (obj == null && this.parentContext != null) {
            obj = this.parentContext.getAttribute(id);
        }
        return obj;
    }
    
    @Override
    public void setAttribute(final String id, final Object obj) {
        Args.notNull(id, "Id");
        if (obj != null) {
            this.map.put(id, obj);
        }
        else {
            this.map.remove(id);
        }
    }
    
    @Override
    public Object removeAttribute(final String id) {
        Args.notNull(id, "Id");
        return this.map.remove(id);
    }
    
    public void clear() {
        this.map.clear();
    }
    
    @Override
    public String toString() {
        return this.map.toString();
    }
}
