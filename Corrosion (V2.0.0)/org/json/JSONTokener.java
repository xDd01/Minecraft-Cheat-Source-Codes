/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONTokener {
    private int index;
    private Reader reader;
    private char lastChar;
    private boolean useLastChar;

    public JSONTokener(Reader reader) {
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.useLastChar = false;
        this.index = 0;
    }

    public JSONTokener(String string) {
        this(new StringReader(string));
    }

    public void back() throws JSONException {
        if (this.useLastChar || this.index <= 0) {
            throw new JSONException("Stepping back two steps is not supported");
        }
        --this.index;
        this.useLastChar = true;
    }

    public static int dehexchar(char c2) {
        if (c2 >= '0' && c2 <= '9') {
            return c2 - 48;
        }
        if (c2 >= 'A' && c2 <= 'F') {
            return c2 - 55;
        }
        if (c2 >= 'a' && c2 <= 'f') {
            return c2 - 87;
        }
        return -1;
    }

    public boolean more() throws JSONException {
        char c2 = this.next();
        if (c2 == '\u0000') {
            return false;
        }
        this.back();
        return true;
    }

    public char next() throws JSONException {
        int n2;
        if (this.useLastChar) {
            this.useLastChar = false;
            if (this.lastChar != '\u0000') {
                ++this.index;
            }
            return this.lastChar;
        }
        try {
            n2 = this.reader.read();
        }
        catch (IOException iOException) {
            throw new JSONException(iOException);
        }
        if (n2 <= 0) {
            this.lastChar = '\u0000';
            return '\u0000';
        }
        ++this.index;
        this.lastChar = (char)n2;
        return this.lastChar;
    }

    public char next(char c2) throws JSONException {
        char c3 = this.next();
        if (c3 != c2) {
            throw this.syntaxError("Expected '" + c2 + "' and instead saw '" + c3 + "'");
        }
        return c3;
    }

    public String next(int n2) throws JSONException {
        if (n2 == 0) {
            return "";
        }
        char[] cArray = new char[n2];
        int n3 = 0;
        if (this.useLastChar) {
            this.useLastChar = false;
            cArray[0] = this.lastChar;
            n3 = 1;
        }
        try {
            int n4;
            while (n3 < n2 && (n4 = this.reader.read(cArray, n3, n2 - n3)) != -1) {
                n3 += n4;
            }
        }
        catch (IOException iOException) {
            throw new JSONException(iOException);
        }
        this.index += n3;
        if (n3 < n2) {
            throw this.syntaxError("Substring bounds error");
        }
        this.lastChar = cArray[n2 - 1];
        return new String(cArray);
    }

    public char nextClean() throws JSONException {
        char c2;
        while ((c2 = this.next()) != '\u0000' && c2 <= ' ') {
        }
        return c2;
    }

    public String nextString(char c2) throws JSONException {
        StringBuffer stringBuffer = new StringBuffer();
        block13: while (true) {
            char c3 = this.next();
            switch (c3) {
                case '\u0000': 
                case '\n': 
                case '\r': {
                    throw this.syntaxError("Unterminated string");
                }
                case '\\': {
                    c3 = this.next();
                    switch (c3) {
                        case 'b': {
                            stringBuffer.append('\b');
                            continue block13;
                        }
                        case 't': {
                            stringBuffer.append('\t');
                            continue block13;
                        }
                        case 'n': {
                            stringBuffer.append('\n');
                            continue block13;
                        }
                        case 'f': {
                            stringBuffer.append('\f');
                            continue block13;
                        }
                        case 'r': {
                            stringBuffer.append('\r');
                            continue block13;
                        }
                        case 'u': {
                            stringBuffer.append((char)Integer.parseInt(this.next(4), 16));
                            continue block13;
                        }
                        case 'x': {
                            stringBuffer.append((char)Integer.parseInt(this.next(2), 16));
                            continue block13;
                        }
                    }
                    stringBuffer.append(c3);
                    continue block13;
                }
            }
            if (c3 == c2) {
                return stringBuffer.toString();
            }
            stringBuffer.append(c3);
        }
    }

    public String nextTo(char c2) throws JSONException {
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            char c3;
            if ((c3 = this.next()) == c2 || c3 == '\u0000' || c3 == '\n' || c3 == '\r') {
                if (c3 != '\u0000') {
                    this.back();
                }
                return stringBuffer.toString().trim();
            }
            stringBuffer.append(c3);
        }
    }

    public String nextTo(String string) throws JSONException {
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            char c2;
            if (string.indexOf(c2 = this.next()) >= 0 || c2 == '\u0000' || c2 == '\n' || c2 == '\r') {
                if (c2 != '\u0000') {
                    this.back();
                }
                return stringBuffer.toString().trim();
            }
            stringBuffer.append(c2);
        }
    }

    public Object nextValue() throws JSONException {
        char c2 = this.nextClean();
        switch (c2) {
            case '\"': 
            case '\'': {
                return this.nextString(c2);
            }
            case '{': {
                this.back();
                return new JSONObject(this);
            }
            case '(': 
            case '[': {
                this.back();
                return new JSONArray(this);
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        while (c2 >= ' ' && ",:]}/\\\"[{;=#".indexOf(c2) < 0) {
            stringBuffer.append(c2);
            c2 = this.next();
        }
        this.back();
        String string = stringBuffer.toString().trim();
        if (string.equals("")) {
            throw this.syntaxError("Missing value");
        }
        return JSONObject.stringToValue(string);
    }

    public char skipTo(char c2) throws JSONException {
        char c3;
        try {
            int n2 = this.index;
            this.reader.mark(Integer.MAX_VALUE);
            do {
                if ((c3 = this.next()) != '\u0000') continue;
                this.reader.reset();
                this.index = n2;
                return c3;
            } while (c3 != c2);
        }
        catch (IOException iOException) {
            throw new JSONException(iOException);
        }
        this.back();
        return c3;
    }

    public JSONException syntaxError(String string) {
        return new JSONException(string + this.toString());
    }

    public String toString() {
        return " at character " + this.index;
    }
}

