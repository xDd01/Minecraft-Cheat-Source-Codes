package org.apache.http.cookie;

import org.apache.http.annotation.*;

public interface SetCookie2 extends SetCookie
{
    @Obsolete
    void setCommentURL(final String p0);
    
    @Obsolete
    void setPorts(final int[] p0);
    
    @Obsolete
    void setDiscard(final boolean p0);
}
