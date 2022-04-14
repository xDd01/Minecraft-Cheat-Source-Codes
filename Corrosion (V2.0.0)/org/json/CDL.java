/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class CDL {
    private static String getValue(JSONTokener jSONTokener) throws JSONException {
        char c2;
        while ((c2 = jSONTokener.next()) == ' ' || c2 == '\t') {
        }
        switch (c2) {
            case '\u0000': {
                return null;
            }
            case '\"': 
            case '\'': {
                return jSONTokener.nextString(c2);
            }
            case ',': {
                jSONTokener.back();
                return "";
            }
        }
        jSONTokener.back();
        return jSONTokener.nextTo(',');
    }

    public static JSONArray rowToJSONArray(JSONTokener jSONTokener) throws JSONException {
        char c2;
        JSONArray jSONArray = new JSONArray();
        block0: while (true) {
            String string;
            if ((string = CDL.getValue(jSONTokener)) == null || jSONArray.length() == 0 && string.length() == 0) {
                return null;
            }
            jSONArray.put(string);
            do {
                if ((c2 = jSONTokener.next()) == ',') continue block0;
            } while (c2 == ' ');
            break;
        }
        if (c2 == '\n' || c2 == '\r' || c2 == '\u0000') {
            return jSONArray;
        }
        throw jSONTokener.syntaxError("Bad character '" + c2 + "' (" + c2 + ").");
    }

    public static JSONObject rowToJSONObject(JSONArray jSONArray, JSONTokener jSONTokener) throws JSONException {
        JSONArray jSONArray2 = CDL.rowToJSONArray(jSONTokener);
        return jSONArray2 != null ? jSONArray2.toJSONObject(jSONArray) : null;
    }

    public static JSONArray toJSONArray(String string) throws JSONException {
        return CDL.toJSONArray(new JSONTokener(string));
    }

    public static JSONArray toJSONArray(JSONTokener jSONTokener) throws JSONException {
        return CDL.toJSONArray(CDL.rowToJSONArray(jSONTokener), jSONTokener);
    }

    public static JSONArray toJSONArray(JSONArray jSONArray, String string) throws JSONException {
        return CDL.toJSONArray(jSONArray, new JSONTokener(string));
    }

    public static JSONArray toJSONArray(JSONArray jSONArray, JSONTokener jSONTokener) throws JSONException {
        JSONObject jSONObject;
        if (jSONArray == null || jSONArray.length() == 0) {
            return null;
        }
        JSONArray jSONArray2 = new JSONArray();
        while ((jSONObject = CDL.rowToJSONObject(jSONArray, jSONTokener)) != null) {
            jSONArray2.put(jSONObject);
        }
        if (jSONArray2.length() == 0) {
            return null;
        }
        return jSONArray2;
    }

    public static String rowToString(JSONArray jSONArray) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < jSONArray.length(); ++i2) {
            Object object;
            if (i2 > 0) {
                stringBuffer.append(',');
            }
            if ((object = jSONArray.opt(i2)) == null) continue;
            String string = object.toString();
            if (string.indexOf(44) >= 0) {
                if (string.indexOf(34) >= 0) {
                    stringBuffer.append('\'');
                    stringBuffer.append(string);
                    stringBuffer.append('\'');
                    continue;
                }
                stringBuffer.append('\"');
                stringBuffer.append(string);
                stringBuffer.append('\"');
                continue;
            }
            stringBuffer.append(string);
        }
        stringBuffer.append('\n');
        return stringBuffer.toString();
    }

    public static String toString(JSONArray jSONArray) throws JSONException {
        JSONArray jSONArray2;
        JSONObject jSONObject = jSONArray.optJSONObject(0);
        if (jSONObject != null && (jSONArray2 = jSONObject.names()) != null) {
            return CDL.rowToString(jSONArray2) + CDL.toString(jSONArray2, jSONArray);
        }
        return null;
    }

    public static String toString(JSONArray jSONArray, JSONArray jSONArray2) throws JSONException {
        if (jSONArray == null || jSONArray.length() == 0) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < jSONArray2.length(); ++i2) {
            JSONObject jSONObject = jSONArray2.optJSONObject(i2);
            if (jSONObject == null) continue;
            stringBuffer.append(CDL.rowToString(jSONObject.toJSONArray(jSONArray)));
        }
        return stringBuffer.toString();
    }
}

