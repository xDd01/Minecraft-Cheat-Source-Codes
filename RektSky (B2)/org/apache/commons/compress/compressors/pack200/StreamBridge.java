package org.apache.commons.compress.compressors.pack200;

import java.io.*;

abstract class StreamBridge extends FilterOutputStream
{
    private InputStream input;
    private final Object inputLock;
    
    protected StreamBridge(final OutputStream out) {
        super(out);
        this.inputLock = new Object();
    }
    
    protected StreamBridge() {
        this(null);
    }
    
    InputStream getInput() throws IOException {
        synchronized (this.inputLock) {
            if (this.input == null) {
                this.input = this.getInputView();
            }
        }
        return this.input;
    }
    
    abstract InputStream getInputView() throws IOException;
    
    void stop() throws IOException {
        this.close();
        synchronized (this.inputLock) {
            if (this.input != null) {
                this.input.close();
                this.input = null;
            }
        }
    }
}
