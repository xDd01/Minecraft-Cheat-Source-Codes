/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.json.XMLTokener;

public class JSONML {
    private static Object parse(XMLTokener xMLTokener, boolean bl2, JSONArray jSONArray) throws JSONException {
        String string = null;
        JSONArray jSONArray2 = null;
        JSONObject jSONObject = null;
        String string2 = null;
        while (true) {
            Object object;
            if ((object = xMLTokener.nextContent()) == XML.LT) {
                object = xMLTokener.nextToken();
                if (object instanceof Character) {
                    if (object == XML.SLASH) {
                        object = xMLTokener.nextToken();
                        if (!(object instanceof String)) {
                            throw new JSONException("Expected a closing name instead of '" + object + "'.");
                        }
                        if (xMLTokener.nextToken() != XML.GT) {
                            throw xMLTokener.syntaxError("Misshaped close tag");
                        }
                        return object;
                    }
                    if (object == XML.BANG) {
                        char c2 = xMLTokener.next();
                        if (c2 == '-') {
                            if (xMLTokener.next() == '-') {
                                xMLTokener.skipPast("-->");
                            }
                            xMLTokener.back();
                            continue;
                        }
                        if (c2 == '[') {
                            object = xMLTokener.nextToken();
                            if (object.equals("CDATA") && xMLTokener.next() == '[') {
                                if (jSONArray == null) continue;
                                jSONArray.put(xMLTokener.nextCDATA());
                                continue;
                            }
                            throw xMLTokener.syntaxError("Expected 'CDATA['");
                        }
                        int n2 = 1;
                        do {
                            if ((object = xMLTokener.nextMeta()) == null) {
                                throw xMLTokener.syntaxError("Missing '>' after '<!'.");
                            }
                            if (object == XML.LT) {
                                ++n2;
                                continue;
                            }
                            if (object != XML.GT) continue;
                            --n2;
                        } while (n2 > 0);
                        continue;
                    }
                    if (object == XML.QUEST) {
                        xMLTokener.skipPast("?>");
                        continue;
                    }
                    throw xMLTokener.syntaxError("Misshaped tag");
                }
                if (!(object instanceof String)) {
                    throw xMLTokener.syntaxError("Bad tagName '" + object + "'.");
                }
                string2 = (String)object;
                jSONArray2 = new JSONArray();
                jSONObject = new JSONObject();
                if (bl2) {
                    jSONArray2.put(string2);
                    if (jSONArray != null) {
                        jSONArray.put(jSONArray2);
                    }
                } else {
                    jSONObject.put("tagName", string2);
                    if (jSONArray != null) {
                        jSONArray.put(jSONObject);
                    }
                }
                object = null;
                while (true) {
                    if (object == null) {
                        object = xMLTokener.nextToken();
                    }
                    if (object == null) {
                        throw xMLTokener.syntaxError("Misshaped tag");
                    }
                    if (!(object instanceof String)) break;
                    String string3 = (String)object;
                    if (!(bl2 || string3 != "tagName" && string3 != "childNode")) {
                        throw xMLTokener.syntaxError("Reserved attribute.");
                    }
                    object = xMLTokener.nextToken();
                    if (object == XML.EQ) {
                        object = xMLTokener.nextToken();
                        if (!(object instanceof String)) {
                            throw xMLTokener.syntaxError("Missing value");
                        }
                        jSONObject.accumulate(string3, JSONObject.stringToValue((String)object));
                        object = null;
                        continue;
                    }
                    jSONObject.accumulate(string3, "");
                }
                if (bl2 && jSONObject.length() > 0) {
                    jSONArray2.put(jSONObject);
                }
                if (object == XML.SLASH) {
                    if (xMLTokener.nextToken() != XML.GT) {
                        throw xMLTokener.syntaxError("Misshaped tag");
                    }
                    if (jSONArray != null) continue;
                    if (bl2) {
                        return jSONArray2;
                    }
                    return jSONObject;
                }
                if (object != XML.GT) {
                    throw xMLTokener.syntaxError("Misshaped tag");
                }
                string = (String)JSONML.parse(xMLTokener, bl2, jSONArray2);
                if (string == null) continue;
                if (!string.equals(string2)) {
                    throw xMLTokener.syntaxError("Mismatched '" + string2 + "' and '" + string + "'");
                }
                string2 = null;
                if (!bl2 && jSONArray2.length() > 0) {
                    jSONObject.put("childNodes", jSONArray2);
                }
                if (jSONArray != null) continue;
                if (bl2) {
                    return jSONArray2;
                }
                return jSONObject;
            }
            if (jSONArray == null) continue;
            jSONArray.put(object instanceof String ? JSONObject.stringToValue((String)object) : object);
        }
    }

    public static JSONArray toJSONArray(String string) throws JSONException {
        return JSONML.toJSONArray(new XMLTokener(string));
    }

    public static JSONArray toJSONArray(XMLTokener xMLTokener) throws JSONException {
        return (JSONArray)JSONML.parse(xMLTokener, true, null);
    }

    public static JSONObject toJSONObject(XMLTokener xMLTokener) throws JSONException {
        return (JSONObject)JSONML.parse(xMLTokener, false, null);
    }

    public static JSONObject toJSONObject(String string) throws JSONException {
        return JSONML.toJSONObject(new XMLTokener(string));
    }

    public static String toString(JSONArray jSONArray) throws JSONException {
        int n2;
        int n3;
        StringBuffer stringBuffer = new StringBuffer();
        String string = jSONArray.getString(0);
        XML.noSpace(string);
        string = XML.escape(string);
        stringBuffer.append('<');
        stringBuffer.append(string);
        Object object = jSONArray.opt(1);
        if (object instanceof JSONObject) {
            n3 = 2;
            JSONObject jSONObject = (JSONObject)object;
            Iterator iterator = jSONObject.keys();
            while (iterator.hasNext()) {
                String string2 = iterator.next().toString();
                XML.noSpace(string2);
                String string3 = jSONObject.optString(string2);
                if (string3 == null) continue;
                stringBuffer.append(' ');
                stringBuffer.append(XML.escape(string2));
                stringBuffer.append('=');
                stringBuffer.append('\"');
                stringBuffer.append(XML.escape(string3));
                stringBuffer.append('\"');
            }
        } else {
            n3 = 1;
        }
        if (n3 >= (n2 = jSONArray.length())) {
            stringBuffer.append('/');
            stringBuffer.append('>');
        } else {
            stringBuffer.append('>');
            do {
                object = jSONArray.get(n3);
                ++n3;
                if (object == null) continue;
                if (object instanceof String) {
                    stringBuffer.append(XML.escape(object.toString()));
                    continue;
                }
                if (object instanceof JSONObject) {
                    stringBuffer.append(JSONML.toString((JSONObject)object));
                    continue;
                }
                if (!(object instanceof JSONArray)) continue;
                stringBuffer.append(JSONML.toString((JSONArray)object));
            } while (n3 < n2);
            stringBuffer.append('<');
            stringBuffer.append('/');
            stringBuffer.append(string);
            stringBuffer.append('>');
        }
        return stringBuffer.toString();
    }

    public static String toString(JSONObject jSONObject) throws JSONException {
        StringBuffer stringBuffer = new StringBuffer();
        String string = jSONObject.optString("tagName");
        if (string == null) {
            return XML.escape(jSONObject.toString());
        }
        XML.noSpace(string);
        string = XML.escape(string);
        stringBuffer.append('<');
        stringBuffer.append(string);
        Iterator iterator = jSONObject.keys();
        while (iterator.hasNext()) {
            String string2 = iterator.next().toString();
            if (string2.equals("tagName") || string2.equals("childNodes")) continue;
            XML.noSpace(string2);
            String string3 = jSONObject.optString(string2);
            if (string3 == null) continue;
            stringBuffer.append(' ');
            stringBuffer.append(XML.escape(string2));
            stringBuffer.append('=');
            stringBuffer.append('\"');
            stringBuffer.append(XML.escape(string3));
            stringBuffer.append('\"');
        }
        JSONArray jSONArray = jSONObject.optJSONArray("childNodes");
        if (jSONArray == null) {
            stringBuffer.append('/');
            stringBuffer.append('>');
        } else {
            stringBuffer.append('>');
            int n2 = jSONArray.length();
            for (int i2 = 0; i2 < n2; ++i2) {
                Object object = jSONArray.get(i2);
                if (object == null) continue;
                if (object instanceof String) {
                    stringBuffer.append(XML.escape(object.toString()));
                    continue;
                }
                if (object instanceof JSONObject) {
                    stringBuffer.append(JSONML.toString((JSONObject)object));
                    continue;
                }
                if (!(object instanceof JSONArray)) continue;
                stringBuffer.append(JSONML.toString((JSONArray)object));
            }
            stringBuffer.append('<');
            stringBuffer.append('/');
            stringBuffer.append(string);
            stringBuffer.append('>');
        }
        return stringBuffer.toString();
    }
}

