package org.apache.commons.io.input;

import java.util.*;
import org.apache.commons.io.*;
import java.io.*;

public class TaggedInputStream extends ProxyInputStream
{
    private final Serializable tag;
    
    public TaggedInputStream(final InputStream proxy) {
        super(proxy);
        this.tag = UUID.randomUUID();
    }
    
    public boolean isCauseOf(final Throwable exception) {
        return TaggedIOException.isTaggedWith(exception, this.tag);
    }
    
    public void throwIfCauseOf(final Throwable throwable) throws IOException {
        TaggedIOException.throwCauseIfTaggedWith(throwable, this.tag);
    }
    
    @Override
    protected void handleIOException(final IOException e) throws IOException {
        throw new TaggedIOException(e, this.tag);
    }
}
