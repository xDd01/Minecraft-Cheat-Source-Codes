package org.apache.http.io;

import org.apache.http.*;
import org.apache.http.config.*;

public interface HttpMessageParserFactory<T extends HttpMessage>
{
    HttpMessageParser<T> create(final SessionInputBuffer p0, final MessageConstraints p1);
}
