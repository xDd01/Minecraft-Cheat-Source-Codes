package org.apache.commons.compress.compressors.pack200;

import java.io.*;

class InMemoryCachingStreamBridge extends StreamBridge
{
    InMemoryCachingStreamBridge() {
        super(new ByteArrayOutputStream());
    }
    
    @Override
    InputStream getInputView() throws IOException {
        return new ByteArrayInputStream(((ByteArrayOutputStream)this.out).toByteArray());
    }
}
