package org.apache.commons.lang3.text.translate;

import java.io.*;

@Deprecated
public class UnicodeUnpairedSurrogateRemover extends CodePointTranslator
{
    @Override
    public boolean translate(final int codepoint, final Writer out) throws IOException {
        return codepoint >= 55296 && codepoint <= 57343;
    }
}
