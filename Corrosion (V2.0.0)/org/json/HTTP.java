/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.util.Iterator;
import org.json.HTTPTokener;
import org.json.JSONException;
import org.json.JSONObject;

public class HTTP {
    public static final String CRLF = "\r\n";

    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        HTTPTokener hTTPTokener = new HTTPTokener(string);
        String string2 = hTTPTokener.nextToken();
        if (string2.toUpperCase().startsWith("HTTP")) {
            jSONObject.put("HTTP-Version", string2);
            jSONObject.put("Status-Code", hTTPTokener.nextToken());
            jSONObject.put("Reason-Phrase", hTTPTokener.nextTo('\u0000'));
            hTTPTokener.next();
        } else {
            jSONObject.put("Method", string2);
            jSONObject.put("Request-URI", hTTPTokener.nextToken());
            jSONObject.put("HTTP-Version", hTTPTokener.nextToken());
        }
        while (hTTPTokener.more()) {
            String string3 = hTTPTokener.nextTo(':');
            hTTPTokener.next(':');
            jSONObject.put(string3, hTTPTokener.nextTo('\u0000'));
            hTTPTokener.next();
        }
        return jSONObject;
    }

    public static String toString(JSONObject jSONObject) throws JSONException {
        Iterator iterator = jSONObject.keys();
        StringBuffer stringBuffer = new StringBuffer();
        if (jSONObject.has("Status-Code") && jSONObject.has("Reason-Phrase")) {
            stringBuffer.append(jSONObject.getString("HTTP-Version"));
            stringBuffer.append(' ');
            stringBuffer.append(jSONObject.getString("Status-Code"));
            stringBuffer.append(' ');
            stringBuffer.append(jSONObject.getString("Reason-Phrase"));
        } else if (jSONObject.has("Method") && jSONObject.has("Request-URI")) {
            stringBuffer.append(jSONObject.getString("Method"));
            stringBuffer.append(' ');
            stringBuffer.append('\"');
            stringBuffer.append(jSONObject.getString("Request-URI"));
            stringBuffer.append('\"');
            stringBuffer.append(' ');
            stringBuffer.append(jSONObject.getString("HTTP-Version"));
        } else {
            throw new JSONException("Not enough material for an HTTP header.");
        }
        stringBuffer.append(CRLF);
        while (iterator.hasNext()) {
            String string = iterator.next().toString();
            if (string.equals("HTTP-Version") || string.equals("Status-Code") || string.equals("Reason-Phrase") || string.equals("Method") || string.equals("Request-URI") || jSONObject.isNull(string)) continue;
            stringBuffer.append(string);
            stringBuffer.append(": ");
            stringBuffer.append(jSONObject.getString(string));
            stringBuffer.append(CRLF);
        }
        stringBuffer.append(CRLF);
        return stringBuffer.toString();
    }
}

