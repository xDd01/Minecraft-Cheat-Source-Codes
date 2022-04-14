/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.io.IOException;
import java.io.Writer;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONWriter {
    private static final int maxdepth = 20;
    private boolean comma = false;
    protected char mode = (char)105;
    private JSONObject[] stack = new JSONObject[20];
    private int top = 0;
    protected Writer writer;

    public JSONWriter(Writer writer) {
        this.writer = writer;
    }

    private JSONWriter append(String string) throws JSONException {
        if (string == null) {
            throw new JSONException("Null pointer");
        }
        if (this.mode == 'o' || this.mode == 'a') {
            try {
                if (this.comma && this.mode == 'a') {
                    this.writer.write(44);
                }
                this.writer.write(string);
            }
            catch (IOException iOException) {
                throw new JSONException(iOException);
            }
            if (this.mode == 'o') {
                this.mode = (char)107;
            }
            this.comma = true;
            return this;
        }
        throw new JSONException("Value out of sequence.");
    }

    public JSONWriter array() throws JSONException {
        if (this.mode == 'i' || this.mode == 'o' || this.mode == 'a') {
            this.push(null);
            this.append("[");
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced array.");
    }

    private JSONWriter end(char c2, char c3) throws JSONException {
        if (this.mode != c2) {
            throw new JSONException(c2 == 'o' ? "Misplaced endObject." : "Misplaced endArray.");
        }
        this.pop(c2);
        try {
            this.writer.write(c3);
        }
        catch (IOException iOException) {
            throw new JSONException(iOException);
        }
        this.comma = true;
        return this;
    }

    public JSONWriter endArray() throws JSONException {
        return this.end('a', ']');
    }

    public JSONWriter endObject() throws JSONException {
        return this.end('k', '}');
    }

    public JSONWriter key(String string) throws JSONException {
        if (string == null) {
            throw new JSONException("Null key.");
        }
        if (this.mode == 'k') {
            try {
                if (this.comma) {
                    this.writer.write(44);
                }
                this.stack[this.top - 1].putOnce(string, Boolean.TRUE);
                this.writer.write(JSONObject.quote(string));
                this.writer.write(58);
                this.comma = false;
                this.mode = (char)111;
                return this;
            }
            catch (IOException iOException) {
                throw new JSONException(iOException);
            }
        }
        throw new JSONException("Misplaced key.");
    }

    public JSONWriter object() throws JSONException {
        if (this.mode == 'i') {
            this.mode = (char)111;
        }
        if (this.mode == 'o' || this.mode == 'a') {
            this.append("{");
            this.push(new JSONObject());
            this.comma = false;
            return this;
        }
        throw new JSONException("Misplaced object.");
    }

    private void pop(char c2) throws JSONException {
        char c3;
        if (this.top <= 0) {
            throw new JSONException("Nesting error.");
        }
        char c4 = c3 = this.stack[this.top - 1] == null ? (char)'a' : 'k';
        if (c3 != c2) {
            throw new JSONException("Nesting error.");
        }
        --this.top;
        this.mode = (char)(this.top == 0 ? 100 : (this.stack[this.top - 1] == null ? 97 : 107));
    }

    private void push(JSONObject jSONObject) throws JSONException {
        if (this.top >= 20) {
            throw new JSONException("Nesting too deep.");
        }
        this.stack[this.top] = jSONObject;
        this.mode = (char)(jSONObject == null ? 97 : 107);
        ++this.top;
    }

    public JSONWriter value(boolean bl2) throws JSONException {
        return this.append(bl2 ? "true" : "false");
    }

    public JSONWriter value(double d2) throws JSONException {
        return this.value(new Double(d2));
    }

    public JSONWriter value(long l2) throws JSONException {
        return this.append(Long.toString(l2));
    }

    public JSONWriter value(Object object) throws JSONException {
        return this.append(JSONObject.valueToString(object));
    }
}

