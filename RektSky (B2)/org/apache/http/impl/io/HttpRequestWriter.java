package org.apache.http.impl.io;

import org.apache.http.io.*;
import org.apache.http.message.*;
import org.apache.http.params.*;
import java.io.*;
import org.apache.http.*;

@Deprecated
public class HttpRequestWriter extends AbstractMessageWriter<HttpRequest>
{
    public HttpRequestWriter(final SessionOutputBuffer buffer, final LineFormatter formatter, final HttpParams params) {
        super(buffer, formatter, params);
    }
    
    @Override
    protected void writeHeadLine(final HttpRequest message) throws IOException {
        this.lineFormatter.formatRequestLine(this.lineBuf, message.getRequestLine());
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
