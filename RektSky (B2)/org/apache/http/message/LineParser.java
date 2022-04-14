package org.apache.http.message;

import org.apache.http.util.*;
import org.apache.http.*;

public interface LineParser
{
    ProtocolVersion parseProtocolVersion(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
    
    boolean hasProtocolVersion(final CharArrayBuffer p0, final ParserCursor p1);
    
    RequestLine parseRequestLine(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
    
    StatusLine parseStatusLine(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
    
    Header parseHeader(final CharArrayBuffer p0) throws ParseException;
}
