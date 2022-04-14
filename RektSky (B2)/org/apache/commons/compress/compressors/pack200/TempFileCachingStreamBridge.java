package org.apache.commons.compress.compressors.pack200;

import java.nio.file.*;
import java.io.*;

class TempFileCachingStreamBridge extends StreamBridge
{
    private final File f;
    
    TempFileCachingStreamBridge() throws IOException {
        (this.f = File.createTempFile("commons-compress", "packtemp")).deleteOnExit();
        this.out = Files.newOutputStream(this.f.toPath(), new OpenOption[0]);
    }
    
    @Override
    InputStream getInputView() throws IOException {
        this.out.close();
        return new FilterInputStream(Files.newInputStream(this.f.toPath(), new OpenOption[0])) {
            @Override
            public void close() throws IOException {
                try {
                    super.close();
                }
                finally {
                    TempFileCachingStreamBridge.this.f.delete();
                }
            }
        };
    }
}
