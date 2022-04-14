package org.apache.http.cookie;

import org.apache.http.annotation.*;
import java.util.*;

public interface Cookie
{
    String getName();
    
    String getValue();
    
    @Obsolete
    String getComment();
    
    @Obsolete
    String getCommentURL();
    
    Date getExpiryDate();
    
    boolean isPersistent();
    
    String getDomain();
    
    String getPath();
    
    @Obsolete
    int[] getPorts();
    
    boolean isSecure();
    
    @Obsolete
    int getVersion();
    
    boolean isExpired(final Date p0);
}
