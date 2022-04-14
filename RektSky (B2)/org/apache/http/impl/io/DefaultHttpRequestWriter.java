package org.apache.http.impl.io;

import org.apache.http.io.*;
import org.apache.http.message.*;
import java.io.*;
import org.apache.http.*;

public class DefaultHttpRequestWriter extends AbstractMessageWriter<HttpRequest>
{
    public DefaultHttpRequestWriter(final SessionOutputBuffer buffer, final LineFormatter formatter) {
        super(buffer, formatter);
    }
    
    public DefaultHttpRequestWriter(final SessionOutputBuffer buffer) {
        this(buffer, null);
    }
    
    @Override
    protected void writeHeadLine(final HttpRequest message) throws IOException {
        this.lineFormatter.formatRequestLine(this.lineBuf, message.getRequestLine());
        this.sessionBuffer.writeLine(this.lineBuf);
    }
}
