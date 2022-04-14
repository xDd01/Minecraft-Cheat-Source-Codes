package me.spec.eris.client.security.network;

import java.io.DataOutput;
import java.io.IOException;

@FunctionalInterface
public interface MetadataWriter<T> {
    /**
     * Writes metadata about the object to the output.
     *
     * @param output
     * @param obj
     * @throws IOException
     */
    public void writeMetadata(DataOutput output, T obj) throws IOException;
}