package io.netty.handler.codec.compression;

import io.netty.handler.codec.*;

public class DecompressionException extends DecoderException
{
    private static final long serialVersionUID = 3546272712208105199L;
    
    public DecompressionException() {
    }
    
    public DecompressionException(final String message, final Throwable cause) {
        super(message, cause);
    }
    
    public DecompressionException(final String message) {
        super(message);
    }
    
    public DecompressionException(final Throwable cause) {
        super(cause);
    }
}
