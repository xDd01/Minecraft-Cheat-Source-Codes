package org.apache.commons.io.input;

import java.io.*;

public class InfiniteCircularInputStream extends InputStream
{
    private final byte[] repeatedContent;
    private int position;
    
    public InfiniteCircularInputStream(final byte[] repeatedContent) {
        this.position = -1;
        this.repeatedContent = repeatedContent;
    }
    
    @Override
    public int read() {
        this.position = (this.position + 1) % this.repeatedContent.length;
        return this.repeatedContent[this.position] & 0xFF;
    }
}
