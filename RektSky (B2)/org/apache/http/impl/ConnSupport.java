package org.apache.http.impl;

import org.apache.http.config.*;
import java.nio.charset.*;

public final class ConnSupport
{
    public static CharsetDecoder createDecoder(final ConnectionConfig cconfig) {
        if (cconfig == null) {
            return null;
        }
        final Charset charset = cconfig.getCharset();
        final CodingErrorAction malformed = cconfig.getMalformedInputAction();
        final CodingErrorAction unmappable = cconfig.getUnmappableInputAction();
        if (charset != null) {
            return charset.newDecoder().onMalformedInput((malformed != null) ? malformed : CodingErrorAction.REPORT).onUnmappableCharacter((unmappable != null) ? unmappable : CodingErrorAction.REPORT);
        }
        return null;
    }
    
    public static CharsetEncoder createEncoder(final ConnectionConfig cconfig) {
        if (cconfig == null) {
            return null;
        }
        final Charset charset = cconfig.getCharset();
        if (charset != null) {
            final CodingErrorAction malformed = cconfig.getMalformedInputAction();
            final CodingErrorAction unmappable = cconfig.getUnmappableInputAction();
            return charset.newEncoder().onMalformedInput((malformed != null) ? malformed : CodingErrorAction.REPORT).onUnmappableCharacter((unmappable != null) ? unmappable : CodingErrorAction.REPORT);
        }
        return null;
    }
}
