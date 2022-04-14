package me.spec.eris.client.security.network;

/**
 * Associates a TypeReaderWriter with an integer ID (tag).
 *
 * @param <T>
 * @author Jonathan
 */
class TaggedReaderWriter<T> {
    private final short tag;
    private final TypeReaderWriter<T> readerWriter;

    /**
     * @param tag
     * @param readerWriter
     */
    public TaggedReaderWriter(short tag, TypeReaderWriter<T> readerWriter) {
        this.tag = tag;
        this.readerWriter = readerWriter;
    }

    public short getTag() {
        return this.tag;
    }

    public TypeReaderWriter<T> getReaderWriter() {
        return this.readerWriter;
    }
}