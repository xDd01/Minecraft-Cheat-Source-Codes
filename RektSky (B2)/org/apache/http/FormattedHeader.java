package org.apache.http;

import org.apache.http.util.*;

public interface FormattedHeader extends Header
{
    CharArrayBuffer getBuffer();
    
    int getValuePos();
}
