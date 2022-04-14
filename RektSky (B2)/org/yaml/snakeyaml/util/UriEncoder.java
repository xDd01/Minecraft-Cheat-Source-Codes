package org.yaml.snakeyaml.util;

import java.nio.*;
import java.net.*;
import org.yaml.snakeyaml.error.*;
import java.io.*;
import java.nio.charset.*;
import com.google.gdata.util.common.base.*;

public abstract class UriEncoder
{
    private static final CharsetDecoder UTF8Decoder;
    private static final String SAFE_CHARS = "-_.!~*'()@:$&,;=[]/";
    private static final Escaper escaper;
    
    public static String encode(final String uri) {
        return UriEncoder.escaper.escape(uri);
    }
    
    public static String decode(final ByteBuffer buff) throws CharacterCodingException {
        final CharBuffer chars = UriEncoder.UTF8Decoder.decode(buff);
        return chars.toString();
    }
    
    public static String decode(final String buff) {
        try {
            return URLDecoder.decode(buff, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new YAMLException(e);
        }
    }
    
    static {
        UTF8Decoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPORT);
        escaper = new PercentEscaper("-_.!~*'()@:$&,;=[]/", false);
    }
}
