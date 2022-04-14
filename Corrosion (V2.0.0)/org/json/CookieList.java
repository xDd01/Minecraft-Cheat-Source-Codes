/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.util.Iterator;
import org.json.Cookie;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class CookieList {
    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        JSONTokener jSONTokener = new JSONTokener(string);
        while (jSONTokener.more()) {
            String string2 = Cookie.unescape(jSONTokener.nextTo('='));
            jSONTokener.next('=');
            jSONObject.put(string2, Cookie.unescape(jSONTokener.nextTo(';')));
            jSONTokener.next();
        }
        return jSONObject;
    }

    public static String toString(JSONObject jSONObject) throws JSONException {
        boolean bl2 = false;
        Iterator iterator = jSONObject.keys();
        StringBuffer stringBuffer = new StringBuffer();
        while (iterator.hasNext()) {
            String string = iterator.next().toString();
            if (jSONObject.isNull(string)) continue;
            if (bl2) {
                stringBuffer.append(';');
            }
            stringBuffer.append(Cookie.escape(string));
            stringBuffer.append("=");
            stringBuffer.append(Cookie.escape(jSONObject.getString(string)));
            bl2 = true;
        }
        return stringBuffer.toString();
    }
}

