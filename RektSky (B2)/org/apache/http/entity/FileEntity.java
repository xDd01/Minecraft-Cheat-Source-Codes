package org.apache.http.entity;

import org.apache.http.util.*;
import java.io.*;

public class FileEntity extends AbstractHttpEntity implements Cloneable
{
    protected final File file;
    
    @Deprecated
    public FileEntity(final File file, final String contentType) {
        this.file = Args.notNull(file, "File");
        this.setContentType(contentType);
    }
    
    public FileEntity(final File file, final ContentType contentType) {
        this.file = Args.notNull(file, "File");
        if (contentType != null) {
            this.setContentType(contentType.toString());
        }
    }
    
    public FileEntity(final File file) {
        this.file = Args.notNull(file, "File");
    }
    
    @Override
    public boolean isRepeatable() {
        return true;
    }
    
    @Override
    public long getContentLength() {
        return this.file.length();
    }
    
    @Override
    public InputStream getContent() throws IOException {
        return new FileInputStream(this.file);
    }
    
    @Override
    public void writeTo(final OutputStream outstream) throws IOException {
        Args.notNull(outstream, "Output stream");
        final InputStream instream = new FileInputStream(this.file);
        try {
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                outstream.write(tmp, 0, l);
            }
            outstream.flush();
        }
        finally {
            instream.close();
        }
    }
    
    @Override
    public boolean isStreaming() {
        return false;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
