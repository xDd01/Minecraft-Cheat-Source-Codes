package org.apache.commons.codec;

public interface BinaryEncoder extends Encoder
{
    byte[] encode(final byte[] p0) throws EncoderException;
}
