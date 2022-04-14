/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XMLTokener;

public class XML {
    public static final Character AMP = new Character('&');
    public static final Character APOS = new Character('\'');
    public static final Character BANG = new Character('!');
    public static final Character EQ = new Character('=');
    public static final Character GT = new Character('>');
    public static final Character LT = new Character('<');
    public static final Character QUEST = new Character('?');
    public static final Character QUOT = new Character('\"');
    public static final Character SLASH = new Character('/');

    public static String escape(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        int n2 = string.length();
        block6: for (int i2 = 0; i2 < n2; ++i2) {
            char c2 = string.charAt(i2);
            switch (c2) {
                case '&': {
                    stringBuffer.append("&amp;");
                    continue block6;
                }
                case '<': {
                    stringBuffer.append("&lt;");
                    continue block6;
                }
                case '>': {
                    stringBuffer.append("&gt;");
                    continue block6;
                }
                case '\"': {
                    stringBuffer.append("&quot;");
                    continue block6;
                }
                default: {
                    stringBuffer.append(c2);
                }
            }
        }
        return stringBuffer.toString();
    }

    public static void noSpace(String string) throws JSONException {
        int n2 = string.length();
        if (n2 == 0) {
            throw new JSONException("Empty string.");
        }
        for (int i2 = 0; i2 < n2; ++i2) {
            if (!Character.isWhitespace(string.charAt(i2))) continue;
            throw new JSONException("'" + string + "' contains a space character.");
        }
    }

    private static boolean parse(XMLTokener xMLTokener, JSONObject jSONObject, String string) throws JSONException {
        String string2;
        JSONObject jSONObject2 = null;
        Object object = xMLTokener.nextToken();
        if (object == BANG) {
            char c2 = xMLTokener.next();
            if (c2 == '-') {
                if (xMLTokener.next() == '-') {
                    xMLTokener.skipPast("-->");
                    return false;
                }
                xMLTokener.back();
            } else if (c2 == '[') {
                object = xMLTokener.nextToken();
                if (object.equals("CDATA") && xMLTokener.next() == '[') {
                    String string3 = xMLTokener.nextCDATA();
                    if (string3.length() > 0) {
                        jSONObject.accumulate("content", string3);
                    }
                    return false;
                }
                throw xMLTokener.syntaxError("Expected 'CDATA['");
            }
            int n2 = 1;
            do {
                if ((object = xMLTokener.nextMeta()) == null) {
                    throw xMLTokener.syntaxError("Missing '>' after '<!'.");
                }
                if (object == LT) {
                    ++n2;
                    continue;
                }
                if (object != GT) continue;
                --n2;
            } while (n2 > 0);
            return false;
        }
        if (object == QUEST) {
            xMLTokener.skipPast("?>");
            return false;
        }
        if (object == SLASH) {
            object = xMLTokener.nextToken();
            if (string == null) {
                throw xMLTokener.syntaxError("Mismatched close tag" + object);
            }
            if (!object.equals(string)) {
                throw xMLTokener.syntaxError("Mismatched " + string + " and " + object);
            }
            if (xMLTokener.nextToken() != GT) {
                throw xMLTokener.syntaxError("Misshaped close tag");
            }
            return true;
        }
        if (object instanceof Character) {
            throw xMLTokener.syntaxError("Misshaped tag");
        }
        String string4 = (String)object;
        object = null;
        jSONObject2 = new JSONObject();
        while (true) {
            if (object == null) {
                object = xMLTokener.nextToken();
            }
            if (!(object instanceof String)) break;
            string2 = (String)object;
            object = xMLTokener.nextToken();
            if (object == EQ) {
                object = xMLTokener.nextToken();
                if (!(object instanceof String)) {
                    throw xMLTokener.syntaxError("Missing value");
                }
                jSONObject2.accumulate(string2, JSONObject.stringToValue((String)object));
                object = null;
                continue;
            }
            jSONObject2.accumulate(string2, "");
        }
        if (object == SLASH) {
            if (xMLTokener.nextToken() != GT) {
                throw xMLTokener.syntaxError("Misshaped tag");
            }
            jSONObject.accumulate(string4, jSONObject2);
            return false;
        }
        if (object == GT) {
            while (true) {
                if ((object = xMLTokener.nextContent()) == null) {
                    if (string4 != null) {
                        throw xMLTokener.syntaxError("Unclosed tag " + string4);
                    }
                    return false;
                }
                if (object instanceof String) {
                    string2 = (String)object;
                    if (string2.length() <= 0) continue;
                    jSONObject2.accumulate("content", JSONObject.stringToValue(string2));
                    continue;
                }
                if (object == LT && XML.parse(xMLTokener, jSONObject2, string4)) break;
            }
            if (jSONObject2.length() == 0) {
                jSONObject.accumulate(string4, "");
            } else if (jSONObject2.length() == 1 && jSONObject2.opt("content") != null) {
                jSONObject.accumulate(string4, jSONObject2.opt("content"));
            } else {
                jSONObject.accumulate(string4, jSONObject2);
            }
            return false;
        }
        throw xMLTokener.syntaxError("Misshaped tag");
    }

    public static JSONObject toJSONObject(String string) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        XMLTokener xMLTokener = new XMLTokener(string);
        while (xMLTokener.more() && xMLTokener.skipPast("<")) {
            XML.parse(xMLTokener, jSONObject, null);
        }
        return jSONObject;
    }

    public static String toString(Object object) throws JSONException {
        return XML.toString(object, null);
    }

    public static String toString(Object object, String string) throws JSONException {
        String string2;
        StringBuffer stringBuffer = new StringBuffer();
        if (object instanceof JSONObject) {
            if (string != null) {
                stringBuffer.append('<');
                stringBuffer.append(string);
                stringBuffer.append('>');
            }
            JSONObject jSONObject = (JSONObject)object;
            Iterator iterator = jSONObject.keys();
            while (iterator.hasNext()) {
                int n2;
                int n3;
                JSONArray jSONArray;
                String string3 = iterator.next().toString();
                Object object2 = jSONObject.opt(string3);
                if (object2 == null) {
                    object2 = "";
                }
                String string4 = object2 instanceof String ? (String)object2 : null;
                if (string3.equals("content")) {
                    if (object2 instanceof JSONArray) {
                        jSONArray = (JSONArray)object2;
                        n3 = jSONArray.length();
                        for (n2 = 0; n2 < n3; ++n2) {
                            if (n2 > 0) {
                                stringBuffer.append('\n');
                            }
                            stringBuffer.append(XML.escape(jSONArray.get(n2).toString()));
                        }
                        continue;
                    }
                    stringBuffer.append(XML.escape(object2.toString()));
                    continue;
                }
                if (object2 instanceof JSONArray) {
                    jSONArray = (JSONArray)object2;
                    n3 = jSONArray.length();
                    for (n2 = 0; n2 < n3; ++n2) {
                        object2 = jSONArray.get(n2);
                        if (object2 instanceof JSONArray) {
                            stringBuffer.append('<');
                            stringBuffer.append(string3);
                            stringBuffer.append('>');
                            stringBuffer.append(XML.toString(object2));
                            stringBuffer.append("</");
                            stringBuffer.append(string3);
                            stringBuffer.append('>');
                            continue;
                        }
                        stringBuffer.append(XML.toString(object2, string3));
                    }
                    continue;
                }
                if (object2.equals("")) {
                    stringBuffer.append('<');
                    stringBuffer.append(string3);
                    stringBuffer.append("/>");
                    continue;
                }
                stringBuffer.append(XML.toString(object2, string3));
            }
            if (string != null) {
                stringBuffer.append("</");
                stringBuffer.append(string);
                stringBuffer.append('>');
            }
            return stringBuffer.toString();
        }
        if (object instanceof JSONArray) {
            JSONArray jSONArray = (JSONArray)object;
            int n4 = jSONArray.length();
            for (int i2 = 0; i2 < n4; ++i2) {
                Object object3 = jSONArray.opt(i2);
                stringBuffer.append(XML.toString(object3, string == null ? "array" : string));
            }
            return stringBuffer.toString();
        }
        String string5 = string2 = object == null ? "null" : XML.escape(object.toString());
        return string == null ? "\"" + string2 + "\"" : (string2.length() == 0 ? "<" + string + "/>" : "<" + string + ">" + string2 + "</" + string + ">");
    }
}

