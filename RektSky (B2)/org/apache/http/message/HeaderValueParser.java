package org.apache.http.message;

import org.apache.http.util.*;
import org.apache.http.*;

public interface HeaderValueParser
{
    HeaderElement[] parseElements(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
    
    HeaderElement parseHeaderElement(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
    
    NameValuePair[] parseParameters(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
    
    NameValuePair parseNameValuePair(final CharArrayBuffer p0, final ParserCursor p1) throws ParseException;
}
