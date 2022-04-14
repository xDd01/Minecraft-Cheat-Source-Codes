/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.util.HashMap;
import org.json.JSONException;
import org.json.JSONTokener;
import org.json.XML;

public class XMLTokener
extends JSONTokener {
    public static final HashMap entity = new HashMap(8);

    public XMLTokener(String string) {
        super(string);
    }

    public String nextCDATA() throws JSONException {
        int n2;
        StringBuffer stringBuffer = new StringBuffer();
        do {
            char c2;
            if ((c2 = this.next()) == '\u0000') {
                throw this.syntaxError("Unclosed CDATA");
            }
            stringBuffer.append(c2);
        } while ((n2 = stringBuffer.length() - 3) < 0 || stringBuffer.charAt(n2) != ']' || stringBuffer.charAt(n2 + 1) != ']' || stringBuffer.charAt(n2 + 2) != '>');
        stringBuffer.setLength(n2);
        return stringBuffer.toString();
    }

    public Object nextContent() throws JSONException {
        char c2;
        while (Character.isWhitespace(c2 = this.next())) {
        }
        if (c2 == '\u0000') {
            return null;
        }
        if (c2 == '<') {
            return XML.LT;
        }
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            if (c2 == '<' || c2 == '\u0000') {
                this.back();
                return stringBuffer.toString().trim();
            }
            if (c2 == '&') {
                stringBuffer.append(this.nextEntity(c2));
            } else {
                stringBuffer.append(c2);
            }
            c2 = this.next();
        }
    }

    public Object nextEntity(char c2) throws JSONException {
        char c3;
        StringBuffer stringBuffer = new StringBuffer();
        while (Character.isLetterOrDigit(c3 = this.next()) || c3 == '#') {
            stringBuffer.append(Character.toLowerCase(c3));
        }
        if (c3 != ';') {
            throw this.syntaxError("Missing ';' in XML entity: &" + stringBuffer);
        }
        String string = stringBuffer.toString();
        Object v2 = entity.get(string);
        return v2 != null ? v2 : c2 + string + ";";
    }

    public Object nextMeta() throws JSONException {
        char c2;
        while (Character.isWhitespace(c2 = this.next())) {
        }
        switch (c2) {
            case '\u0000': {
                throw this.syntaxError("Misshaped meta tag");
            }
            case '<': {
                return XML.LT;
            }
            case '>': {
                return XML.GT;
            }
            case '/': {
                return XML.SLASH;
            }
            case '=': {
                return XML.EQ;
            }
            case '!': {
                return XML.BANG;
            }
            case '?': {
                return XML.QUEST;
            }
            case '\"': 
            case '\'': {
                char c3 = c2;
                do {
                    if ((c2 = this.next()) != '\u0000') continue;
                    throw this.syntaxError("Unterminated string");
                } while (c2 != c3);
                return Boolean.TRUE;
            }
        }
        while (!Character.isWhitespace(c2 = this.next())) {
            switch (c2) {
                case '\u0000': 
                case '!': 
                case '\"': 
                case '\'': 
                case '/': 
                case '<': 
                case '=': 
                case '>': 
                case '?': {
                    this.back();
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.TRUE;
    }

    public Object nextToken() throws JSONException {
        char c2;
        while (Character.isWhitespace(c2 = this.next())) {
        }
        switch (c2) {
            case '\u0000': {
                throw this.syntaxError("Misshaped element");
            }
            case '<': {
                throw this.syntaxError("Misplaced '<'");
            }
            case '>': {
                return XML.GT;
            }
            case '/': {
                return XML.SLASH;
            }
            case '=': {
                return XML.EQ;
            }
            case '!': {
                return XML.BANG;
            }
            case '?': {
                return XML.QUEST;
            }
            case '\"': 
            case '\'': {
                char c3 = c2;
                StringBuffer stringBuffer = new StringBuffer();
                while (true) {
                    if ((c2 = this.next()) == '\u0000') {
                        throw this.syntaxError("Unterminated string");
                    }
                    if (c2 == c3) {
                        return stringBuffer.toString();
                    }
                    if (c2 == '&') {
                        stringBuffer.append(this.nextEntity(c2));
                        continue;
                    }
                    stringBuffer.append(c2);
                }
            }
        }
        StringBuffer stringBuffer = new StringBuffer();
        while (true) {
            stringBuffer.append(c2);
            c2 = this.next();
            if (Character.isWhitespace(c2)) {
                return stringBuffer.toString();
            }
            switch (c2) {
                case '\u0000': {
                    return stringBuffer.toString();
                }
                case '!': 
                case '/': 
                case '=': 
                case '>': 
                case '?': 
                case '[': 
                case ']': {
                    this.back();
                    return stringBuffer.toString();
                }
                case '\"': 
                case '\'': 
                case '<': {
                    throw this.syntaxError("Bad character in a name");
                }
            }
        }
    }

    public boolean skipPast(String string) throws JSONException {
        char c2;
        int n2;
        int n3 = 0;
        int n4 = string.length();
        char[] cArray = new char[n4];
        for (n2 = 0; n2 < n4; ++n2) {
            c2 = this.next();
            if (c2 == '\u0000') {
                return false;
            }
            cArray[n2] = c2;
        }
        while (true) {
            int n5 = n3;
            boolean bl2 = true;
            for (n2 = 0; n2 < n4; ++n2) {
                if (cArray[n5] != string.charAt(n2)) {
                    bl2 = false;
                    break;
                }
                if (++n5 < n4) continue;
                n5 -= n4;
            }
            if (bl2) {
                return true;
            }
            c2 = this.next();
            if (c2 == '\u0000') {
                return false;
            }
            cArray[n3] = c2;
            if (++n3 < n4) continue;
            n3 -= n4;
        }
    }

    static {
        entity.put("amp", XML.AMP);
        entity.put("apos", XML.APOS);
        entity.put("gt", XML.GT);
        entity.put("lt", XML.LT);
        entity.put("quot", XML.QUOT);
    }
}

