package org.apache.http.impl.io;

import org.apache.http.io.*;
import org.apache.http.message.*;
import java.io.*;
import org.apache.http.*;

public class DefaultHttpResponseWriter extends AbstractMessageWriter<HttpResponse>
{
    public DefaultHttpResponseWriter(final SessionOutputBuffer buffer, final LineFormatter formatter) {
        super(buffer, formatter);
    }
    
    public DefaultHttpResponseWriter(final SessionOutputBuffer buffer) {
        super(buffer, null);
    }
    
    @Override
    protected void writeHeadLine(final HttpResponse message) throws IOException {
        this.lineFormatter.formatStatusLine(this.lineBuf, message.getStatusLine());
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
