package org.apache.http.client.entity;

import org.apache.http.*;

public class DeflateDecompressingEntity extends DecompressingEntity
{
    public DeflateDecompressingEntity(final HttpEntity entity) {
        super(entity, DeflateInputStreamFactory.getInstance());
    }
}
