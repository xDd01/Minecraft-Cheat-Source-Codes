package org.apache.http.client.entity;

import org.apache.http.*;

public class GzipDecompressingEntity extends DecompressingEntity
{
    public GzipDecompressingEntity(final HttpEntity entity) {
        super(entity, GZIPInputStreamFactory.getInstance());
    }
}
