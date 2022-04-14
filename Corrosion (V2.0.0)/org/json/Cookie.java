/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Cookie {
    public static String escape(String string) {
        String string2 = string.trim();
        StringBuffer stringBuffer = new StringBuffer();
        int n2 = string2.length();
        for (int i2 = 0; i2 < n2; ++i2) {
            char c2 = string2.charAt(i2);
            if (c2 < ' ' || c2 == '+' || c2 == '%' || c2 == '=' || c2 == ';') {
                stringBuffer.append('%');
                stringBuffer.append(Character.forDigit((char)(c2 >>> 4 & 0xF), 16));
                stringBuffer.append(Character.forDigit((char)(c2 & 0xF), 16));
                continue;
            }
            stringBuffer.append(c2);
        }
        return stringBuffer.toString();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        JSONTokener jSONTokener = new JSONTokener(string);
        jSONObject.put("name", jSONTokener.nextTo('='));
        jSONTokener.next('=');
        jSONObject.put("value", jSONTokener.nextTo(';'));
        jSONTokener.next();
        while (jSONTokener.more()) {
            Object object;
            String string2 = Cookie.unescape(jSONTokener.nextTo("=;"));
            if (jSONTokener.next() != '=') {
                if (!string2.equals("secure")) throw jSONTokener.syntaxError("Missing '=' in cookie parameter.");
                object = Boolean.TRUE;
            } else {
                object = Cookie.unescape(jSONTokener.nextTo(';'));
                jSONTokener.next();
            }
            jSONObject.put(string2, object);
        }
        return jSONObject;
    }

    public static String toString(JSONObject jSONObject) throws JSONException {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(Cookie.escape(jSONObject.getString("name")));
        stringBuffer.append("=");
        stringBuffer.append(Cookie.escape(jSONObject.getString("value")));
        if (jSONObject.has("expires")) {
            stringBuffer.append(";expires=");
            stringBuffer.append(jSONObject.getString("expires"));
        }
        if (jSONObject.has("domain")) {
            stringBuffer.append(";domain=");
            stringBuffer.append(Cookie.escape(jSONObject.getString("domain")));
        }
        if (jSONObject.has("path")) {
            stringBuffer.append(";path=");
            stringBuffer.append(Cookie.escape(jSONObject.getString("path")));
        }
        if (jSONObject.optBoolean("secure")) {
            stringBuffer.append(";secure");
        }
        return stringBuffer.toString();
    }

    public static String unescape(String string) {
        int n2 = string.length();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < n2; ++i2) {
            char c2 = string.charAt(i2);
            if (c2 == '+') {
                c2 = ' ';
            } else if (c2 == '%' && i2 + 2 < n2) {
                int n3 = JSONTokener.dehexchar(string.charAt(i2 + 1));
                int n4 = JSONTokener.dehexchar(string.charAt(i2 + 2));
                if (n3 >= 0 && n4 >= 0) {
                    c2 = (char)(n3 * 16 + n4);
                    i2 += 2;
                }
            }
            stringBuffer.append(c2);
        }
        return stringBuffer.toString();
    }
}

