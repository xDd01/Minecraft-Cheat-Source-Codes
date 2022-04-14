package org.apache.http.impl.io;

import org.apache.http.io.*;
import org.apache.http.message.*;
import org.apache.http.params.*;
import java.io.*;
import org.apache.http.*;

@Deprecated
public class HttpResponseWriter extends AbstractMessageWriter<HttpResponse>
{
    public HttpResponseWriter(final SessionOutputBuffer buffer, final LineFormatter formatter, final HttpParams params) {
        super(buffer, formatter, params);
    }
    
    @Override
    protected void writeHeadLine(final HttpResponse message) throws IOException {
        this.lineFormatter.formatStatusLine(this.lineBuf, message.getStatusLine());
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
