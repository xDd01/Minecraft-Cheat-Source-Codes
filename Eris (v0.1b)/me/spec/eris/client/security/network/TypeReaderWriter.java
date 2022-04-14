package me.spec.eris.client.security.network;

import java.io.IOException;
import java.io.DataInput;
import java.io.DataOutput;

/**
 * Reader and writer for a certain type.
 *
 * @author Jonathan
 */
public interface TypeReaderWriter<T> {

    /**
     * Writes the given object to the stream.
     *
     * @param obj    The object to write.
     * @param output The output to write the object to.
     * @throws IOException If the object could not be written.
     */
    public void write(T obj, DataOutput output) throws IOException;

    /**
     * Reads an object from the stream.
     *
     * @param input The input to read from.
     * @return The read object.
     * @throws IOException If the object could not be read.
     */
    public T read(DataInput input) throws IOException;

    /**
     * @return The type this reader/writer can handle.
     */
    public Class<T> getType();

    /**
     * @return The equivalent primitive type this reader/writer can handle, or null if it can't handle a primitive type.
     */
    public default Class<?> getPrimitiveType() {
        return null;
    }

    /**
     * @return True if this reader/writer can also handle subclasses of its type. Most don't.
     */
    public default boolean canHandleSubclasses() {
        return false;
    }

    /**
     * Optional operation: Reads an object from the data
     * input and stores its restored fields into the
     * target object.
     * <p>
     * Must be implemented if a subclass reader/writer needs
     * to restore its superclass fields.
     *
     * @param target The object to store the fields in.
     * @param input  The input to read from.
     * @throws IOException If the object could not be read.
     */
    public default void readFields(T target, DataInput input) throws IOException, IllegalArgumentException, IllegalAccessException {
        throw new UnsupportedOperationException("Not supported for this type!");
    }
}
