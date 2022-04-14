package org.apache.http.util;

import java.nio.charset.*;
import java.io.*;

public class CharsetUtils
{
    public static Charset lookup(final String name) {
        if (name == null) {
            return null;
        }
        try {
            return Charset.forName(name);
        }
        catch (UnsupportedCharsetException ex) {
            return null;
        }
    }
    
    public static Charset get(final String name) throws UnsupportedEncodingException {
        if (name == null) {
            return null;
        }
        try {
            return Charset.forName(name);
        }
        catch (UnsupportedCharsetException ex) {
            throw new UnsupportedEncodingException(name);
        }
    }
}
