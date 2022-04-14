/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import org.json.JSONException;
import org.json.JSONTokener;

public class HTTPTokener
extends JSONTokener {
    public HTTPTokener(String string) {
        super(string);
    }

    public String nextToken() throws JSONException {
        char c2;
        StringBuffer stringBuffer = new StringBuffer();
        while (Character.isWhitespace(c2 = this.next())) {
        }
        if (c2 == '\"' || c2 == '\'') {
            char c3 = c2;
            while (true) {
                if ((c2 = this.next()) < ' ') {
                    throw this.syntaxError("Unterminated string.");
                }
                if (c2 == c3) {
                    return stringBuffer.toString();
                }
                stringBuffer.append(c2);
            }
        }
        while (c2 != '\u0000' && !Character.isWhitespace(c2)) {
            stringBuffer.append(c2);
            c2 = this.next();
        }
        return stringBuffer.toString();
    }
}

