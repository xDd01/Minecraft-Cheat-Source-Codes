/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JSONArray {
    private ArrayList myArrayList;

    public JSONArray() {
        this.myArrayList = new ArrayList();
    }

    public JSONArray(JSONTokener jSONTokener) throws JSONException {
        this();
        char c2;
        char c3 = jSONTokener.nextClean();
        if (c3 == '[') {
            c2 = ']';
        } else if (c3 == '(') {
            c2 = ')';
        } else {
            throw jSONTokener.syntaxError("A JSONArray text must start with '['");
        }
        if (jSONTokener.nextClean() == ']') {
            return;
        }
        jSONTokener.back();
        block4: while (true) {
            if (jSONTokener.nextClean() == ',') {
                jSONTokener.back();
                this.myArrayList.add(null);
            } else {
                jSONTokener.back();
                this.myArrayList.add(jSONTokener.nextValue());
            }
            c3 = jSONTokener.nextClean();
            switch (c3) {
                case ',': 
                case ';': {
                    if (jSONTokener.nextClean() == ']') {
                        return;
                    }
                    jSONTokener.back();
                    continue block4;
                }
                case ')': 
                case ']': {
                    if (c2 != c3) {
                        throw jSONTokener.syntaxError("Expected a '" + new Character(c2) + "'");
                    }
                    return;
                }
            }
            break;
        }
        throw jSONTokener.syntaxError("Expected a ',' or ']'");
    }

    public JSONArray(String string) throws JSONException {
        this(new JSONTokener(string));
    }

    public JSONArray(Collection collection) {
        this.myArrayList = collection == null ? new ArrayList() : new ArrayList(collection);
    }

    public JSONArray(Collection collection, boolean bl2) {
        this.myArrayList = new ArrayList();
        if (collection != null) {
            Iterator iterator = collection.iterator();
            while (iterator.hasNext()) {
                this.myArrayList.add(new JSONObject(iterator.next(), bl2));
            }
        }
    }

    public JSONArray(Object object) throws JSONException {
        this();
        if (object.getClass().isArray()) {
            int n2 = Array.getLength(object);
            for (int i2 = 0; i2 < n2; ++i2) {
                this.put(Array.get(object, i2));
            }
        } else {
            throw new JSONException("JSONArray initial value should be a string or collection or array.");
        }
    }

    public JSONArray(Object object, boolean bl2) throws JSONException {
        this();
        if (object.getClass().isArray()) {
            int n2 = Array.getLength(object);
            for (int i2 = 0; i2 < n2; ++i2) {
                this.put(new JSONObject(Array.get(object, i2), bl2));
            }
        } else {
            throw new JSONException("JSONArray initial value should be a string or collection or array.");
        }
    }

    public Object get(int n2) throws JSONException {
        Object object = this.opt(n2);
        if (object == null) {
            throw new JSONException("JSONArray[" + n2 + "] not found.");
        }
        return object;
    }

    public boolean getBoolean(int n2) throws JSONException {
        Object object = this.get(n2);
        if (object.equals(Boolean.FALSE) || object instanceof String && ((String)object).equalsIgnoreCase("false")) {
            return false;
        }
        if (object.equals(Boolean.TRUE) || object instanceof String && ((String)object).equalsIgnoreCase("true")) {
            return true;
        }
        throw new JSONException("JSONArray[" + n2 + "] is not a Boolean.");
    }

    public double getDouble(int n2) throws JSONException {
        Object object = this.get(n2);
        try {
            return object instanceof Number ? ((Number)object).doubleValue() : Double.valueOf((String)object).doubleValue();
        }
        catch (Exception exception) {
            throw new JSONException("JSONArray[" + n2 + "] is not a number.");
        }
    }

    public int getInt(int n2) throws JSONException {
        Object object = this.get(n2);
        return object instanceof Number ? ((Number)object).intValue() : (int)this.getDouble(n2);
    }

    public JSONArray getJSONArray(int n2) throws JSONException {
        Object object = this.get(n2);
        if (object instanceof JSONArray) {
            return (JSONArray)object;
        }
        throw new JSONException("JSONArray[" + n2 + "] is not a JSONArray.");
    }

    public JSONObject getJSONObject(int n2) throws JSONException {
        Object object = this.get(n2);
        if (object instanceof JSONObject) {
            return (JSONObject)object;
        }
        throw new JSONException("JSONArray[" + n2 + "] is not a JSONObject.");
    }

    public long getLong(int n2) throws JSONException {
        Object object = this.get(n2);
        return object instanceof Number ? ((Number)object).longValue() : (long)this.getDouble(n2);
    }

    public String getString(int n2) throws JSONException {
        return this.get(n2).toString();
    }

    public boolean isNull(int n2) {
        return JSONObject.NULL.equals(this.opt(n2));
    }

    public String join(String string) throws JSONException {
        int n2 = this.length();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i2 = 0; i2 < n2; ++i2) {
            if (i2 > 0) {
                stringBuffer.append(string);
            }
            stringBuffer.append(JSONObject.valueToString(this.myArrayList.get(i2)));
        }
        return stringBuffer.toString();
    }

    public int length() {
        return this.myArrayList.size();
    }

    public Object opt(int n2) {
        return n2 < 0 || n2 >= this.length() ? null : this.myArrayList.get(n2);
    }

    public boolean optBoolean(int n2) {
        return this.optBoolean(n2, false);
    }

    public boolean optBoolean(int n2, boolean bl2) {
        try {
            return this.getBoolean(n2);
        }
        catch (Exception exception) {
            return bl2;
        }
    }

    public double optDouble(int n2) {
        return this.optDouble(n2, Double.NaN);
    }

    public double optDouble(int n2, double d2) {
        try {
            return this.getDouble(n2);
        }
        catch (Exception exception) {
            return d2;
        }
    }

    public int optInt(int n2) {
        return this.optInt(n2, 0);
    }

    public int optInt(int n2, int n3) {
        try {
            return this.getInt(n2);
        }
        catch (Exception exception) {
            return n3;
        }
    }

    public JSONArray optJSONArray(int n2) {
        Object object = this.opt(n2);
        return object instanceof JSONArray ? (JSONArray)object : null;
    }

    public JSONObject optJSONObject(int n2) {
        Object object = this.opt(n2);
        return object instanceof JSONObject ? (JSONObject)object : null;
    }

    public long optLong(int n2) {
        return this.optLong(n2, 0L);
    }

    public long optLong(int n2, long l2) {
        try {
            return this.getLong(n2);
        }
        catch (Exception exception) {
            return l2;
        }
    }

    public String optString(int n2) {
        return this.optString(n2, "");
    }

    public String optString(int n2, String string) {
        Object object = this.opt(n2);
        return object != null ? object.toString() : string;
    }

    public JSONArray put(boolean bl2) {
        this.put(bl2 ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONArray put(Collection collection) {
        this.put(new JSONArray(collection));
        return this;
    }

    public JSONArray put(double d2) throws JSONException {
        Double d3 = new Double(d2);
        JSONObject.testValidity(d3);
        this.put(d3);
        return this;
    }

    public JSONArray put(int n2) {
        this.put(new Integer(n2));
        return this;
    }

    public JSONArray put(long l2) {
        this.put(new Long(l2));
        return this;
    }

    public JSONArray put(Map map) {
        this.put(new JSONObject(map));
        return this;
    }

    public JSONArray put(Object object) {
        this.myArrayList.add(object);
        return this;
    }

    public JSONArray put(int n2, boolean bl2) throws JSONException {
        this.put(n2, bl2 ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONArray put(int n2, Collection collection) throws JSONException {
        this.put(n2, new JSONArray(collection));
        return this;
    }

    public JSONArray put(int n2, double d2) throws JSONException {
        this.put(n2, new Double(d2));
        return this;
    }

    public JSONArray put(int n2, int n3) throws JSONException {
        this.put(n2, new Integer(n3));
        return this;
    }

    public JSONArray put(int n2, long l2) throws JSONException {
        this.put(n2, new Long(l2));
        return this;
    }

    public JSONArray put(int n2, Map map) throws JSONException {
        this.put(n2, new JSONObject(map));
        return this;
    }

    public JSONArray put(int n2, Object object) throws JSONException {
        JSONObject.testValidity(object);
        if (n2 < 0) {
            throw new JSONException("JSONArray[" + n2 + "] not found.");
        }
        if (n2 < this.length()) {
            this.myArrayList.set(n2, object);
        } else {
            while (n2 != this.length()) {
                this.put(JSONObject.NULL);
            }
            this.put(object);
        }
        return this;
    }

    public JSONObject toJSONObject(JSONArray jSONArray) throws JSONException {
        if (jSONArray == null || jSONArray.length() == 0 || this.length() == 0) {
            return null;
        }
        JSONObject jSONObject = new JSONObject();
        for (int i2 = 0; i2 < jSONArray.length(); ++i2) {
            jSONObject.put(jSONArray.getString(i2), this.opt(i2));
        }
        return jSONObject;
    }

    public String toString() {
        try {
            return '[' + this.join(",") + ']';
        }
        catch (Exception exception) {
            return null;
        }
    }

    public String toString(int n2) throws JSONException {
        return this.toString(n2, 0);
    }

    String toString(int n2, int n3) throws JSONException {
        int n4 = this.length();
        if (n4 == 0) {
            return "[]";
        }
        StringBuffer stringBuffer = new StringBuffer("[");
        if (n4 == 1) {
            stringBuffer.append(JSONObject.valueToString(this.myArrayList.get(0), n2, n3));
        } else {
            int n5;
            int n6 = n3 + n2;
            stringBuffer.append('\n');
            for (n5 = 0; n5 < n4; ++n5) {
                if (n5 > 0) {
                    stringBuffer.append(",\n");
                }
                for (int i2 = 0; i2 < n6; ++i2) {
                    stringBuffer.append(' ');
                }
                stringBuffer.append(JSONObject.valueToString(this.myArrayList.get(n5), n2, n6));
            }
            stringBuffer.append('\n');
            for (n5 = 0; n5 < n3; ++n5) {
                stringBuffer.append(' ');
            }
        }
        stringBuffer.append(']');
        return stringBuffer.toString();
    }

    public Writer write(Writer writer) throws JSONException {
        try {
            boolean bl2 = false;
            int n2 = this.length();
            writer.write(91);
            for (int i2 = 0; i2 < n2; ++i2) {
                Object e2;
                if (bl2) {
                    writer.write(44);
                }
                if ((e2 = this.myArrayList.get(i2)) instanceof JSONObject) {
                    ((JSONObject)e2).write(writer);
                } else if (e2 instanceof JSONArray) {
                    ((JSONArray)e2).write(writer);
                } else {
                    writer.write(JSONObject.valueToString(e2));
                }
                bl2 = true;
            }
            writer.write(93);
            return writer;
        }
        catch (IOException iOException) {
            throw new JSONException(iOException);
        }
    }
}

