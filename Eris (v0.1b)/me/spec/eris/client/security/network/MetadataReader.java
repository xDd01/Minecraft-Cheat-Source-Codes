package me.spec.eris.client.security.network;

import java.io.DataInput;
import java.io.IOException;

@FunctionalInterface
public interface MetadataReader<T> {
    /**
     * Reads metadata about a collection/map from
     * the input and creates a corresponding object.
     *
     * @param input The input to read metadata from.
     * @return The object created according to the metadata.
     * @throws IOException
     */
    public T readMetadata(DataInput input) throws IOException;
}