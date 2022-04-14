package org.yaml.snakeyaml.reader;

import java.util.regex.*;
import org.yaml.snakeyaml.scanner.*;
import org.yaml.snakeyaml.error.*;
import java.io.*;
import java.nio.charset.*;

public class StreamReader
{
    static final Pattern NON_PRINTABLE;
    private String name;
    private final Reader stream;
    private int pointer;
    private boolean eof;
    private String buffer;
    private int index;
    private int line;
    private int column;
    private char[] data;
    
    public StreamReader(final String stream) {
        this.pointer = 0;
        this.eof = true;
        this.index = 0;
        this.line = 0;
        this.column = 0;
        this.name = "<string>";
        this.buffer = "";
        this.checkPrintable(stream);
        this.buffer = stream + "\u0000";
        this.stream = null;
        this.eof = true;
        this.data = null;
    }
    
    public StreamReader(final Reader reader) {
        this.pointer = 0;
        this.eof = true;
        this.index = 0;
        this.line = 0;
        this.column = 0;
        this.name = "<reader>";
        this.buffer = "";
        this.stream = reader;
        this.eof = false;
        this.data = new char[1024];
        this.update();
    }
    
    void checkPrintable(final CharSequence data) {
        final Matcher em = StreamReader.NON_PRINTABLE.matcher(data);
        if (em.find()) {
            final int position = this.index + this.buffer.length() - this.pointer + em.start();
            throw new ReaderException(this.name, position, em.group().charAt(0), "special characters are not allowed");
        }
    }
    
    void checkPrintable(final char[] chars, final int begin, final int end) {
        for (int i = begin; i < end; ++i) {
            final char c = chars[i];
            if ((c < ' ' || c > '~') && c != '\n' && c != '\r' && c != '\t' && c != '\u0085' && (c < ' ' || c > '\ud7ff') && (c < '\ue000' || c > '\ufffc')) {
                final int position = this.index + this.buffer.length() - this.pointer + i;
                throw new ReaderException(this.name, position, c, "special characters are not allowed");
            }
        }
    }
    
    public Mark getMark() {
        return new Mark(this.name, this.index, this.line, this.column, this.buffer, this.pointer);
    }
    
    public void forward() {
        this.forward(1);
    }
    
    public void forward(final int length) {
        if (this.pointer + length + 1 >= this.buffer.length()) {
            this.update();
        }
        char ch = '\0';
        for (int i = 0; i < length; ++i) {
            ch = this.buffer.charAt(this.pointer);
            ++this.pointer;
            ++this.index;
            if (Constant.LINEBR.has(ch) || (ch == '\r' && this.buffer.charAt(this.pointer) != '\n')) {
                ++this.line;
                this.column = 0;
            }
            else if (ch != '\ufeff') {
                ++this.column;
            }
        }
    }
    
    public char peek() {
        return this.buffer.charAt(this.pointer);
    }
    
    public char peek(final int index) {
        if (this.pointer + index + 1 > this.buffer.length()) {
            this.update();
        }
        return this.buffer.charAt(this.pointer + index);
    }
    
    public String prefix(final int length) {
        if (this.pointer + length >= this.buffer.length()) {
            this.update();
        }
        if (this.pointer + length > this.buffer.length()) {
            return this.buffer.substring(this.pointer);
        }
        return this.buffer.substring(this.pointer, this.pointer + length);
    }
    
    public String prefixForward(final int length) {
        final String prefix = this.prefix(length);
        this.pointer += length;
        this.index += length;
        this.column += length;
        return prefix;
    }
    
    private void update() {
        if (!this.eof) {
            this.buffer = this.buffer.substring(this.pointer);
            this.pointer = 0;
            try {
                final int converted = this.stream.read(this.data);
                if (converted > 0) {
                    this.checkPrintable(this.data, 0, converted);
                    this.buffer = new StringBuilder(this.buffer.length() + converted).append(this.buffer).append(this.data, 0, converted).toString();
                }
                else {
                    this.eof = true;
                    this.buffer += "\u0000";
                }
            }
            catch (IOException ioe) {
                throw new YAMLException(ioe);
            }
        }
    }
    
    public int getColumn() {
        return this.column;
    }
    
    public Charset getEncoding() {
        return Charset.forName(((UnicodeReader)this.stream).getEncoding());
    }
    
    public int getIndex() {
        return this.index;
    }
    
    public int getLine() {
        return this.line;
    }
    
    static {
        NON_PRINTABLE = Pattern.compile("[^\t\n\r -~\u0085 -\ud7ff\ue000-\ufffc]");
    }
}
