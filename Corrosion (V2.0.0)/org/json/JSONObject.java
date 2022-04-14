/*
 * Decompiled with CFR 0.152.
 */
package org.json;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONString;
import org.json.JSONTokener;

public class JSONObject {
    private Map map;
    public static final Object NULL = new Null();

    public JSONObject() {
        this.map = new HashMap();
    }

    public JSONObject(JSONObject jSONObject, String[] stringArray) throws JSONException {
        this();
        for (int i2 = 0; i2 < stringArray.length; ++i2) {
            this.putOnce(stringArray[i2], jSONObject.opt(stringArray[i2]));
        }
    }

    public JSONObject(JSONTokener jSONTokener) throws JSONException {
        this();
        if (jSONTokener.nextClean() != '{') {
            throw jSONTokener.syntaxError("A JSONObject text must begin with '{'");
        }
        block8: while (true) {
            char c2 = jSONTokener.nextClean();
            switch (c2) {
                case '\u0000': {
                    throw jSONTokener.syntaxError("A JSONObject text must end with '}'");
                }
                case '}': {
                    return;
                }
            }
            jSONTokener.back();
            String string = jSONTokener.nextValue().toString();
            c2 = jSONTokener.nextClean();
            if (c2 == '=') {
                if (jSONTokener.next() != '>') {
                    jSONTokener.back();
                }
            } else if (c2 != ':') {
                throw jSONTokener.syntaxError("Expected a ':' after a key");
            }
            this.putOnce(string, jSONTokener.nextValue());
            switch (jSONTokener.nextClean()) {
                case ',': 
                case ';': {
                    if (jSONTokener.nextClean() == '}') {
                        return;
                    }
                    jSONTokener.back();
                    continue block8;
                }
                case '}': {
                    return;
                }
            }
            break;
        }
        throw jSONTokener.syntaxError("Expected a ',' or '}'");
    }

    public JSONObject(Map map) {
        this.map = map == null ? new HashMap() : map;
    }

    public JSONObject(Map map, boolean bl2) {
        this.map = new HashMap();
        if (map != null) {
            for (Map.Entry entry : map.entrySet()) {
                this.map.put(entry.getKey(), new JSONObject(entry.getValue(), bl2));
            }
        }
    }

    public JSONObject(Object object) {
        this();
        this.populateInternalMap(object, false);
    }

    public JSONObject(Object object, boolean bl2) {
        this();
        this.populateInternalMap(object, bl2);
    }

    private void populateInternalMap(Object object, boolean bl2) {
        Class<?> clazz = object.getClass();
        if (clazz.getClassLoader() == null) {
            bl2 = false;
        }
        Method[] methodArray = bl2 ? clazz.getMethods() : clazz.getDeclaredMethods();
        for (int i2 = 0; i2 < methodArray.length; ++i2) {
            try {
                Method method = methodArray[i2];
                String string = method.getName();
                String string2 = "";
                if (string.startsWith("get")) {
                    string2 = string.substring(3);
                } else if (string.startsWith("is")) {
                    string2 = string.substring(2);
                }
                if (string2.length() <= 0 || !Character.isUpperCase(string2.charAt(0)) || method.getParameterTypes().length != 0) continue;
                if (string2.length() == 1) {
                    string2 = string2.toLowerCase();
                } else if (!Character.isUpperCase(string2.charAt(1))) {
                    string2 = string2.substring(0, 1).toLowerCase() + string2.substring(1);
                }
                Object object2 = method.invoke(object, null);
                if (object2 == null) {
                    this.map.put(string2, NULL);
                    continue;
                }
                if (object2.getClass().isArray()) {
                    this.map.put(string2, new JSONArray(object2, bl2));
                    continue;
                }
                if (object2 instanceof Collection) {
                    this.map.put(string2, new JSONArray((Collection)object2, bl2));
                    continue;
                }
                if (object2 instanceof Map) {
                    this.map.put(string2, new JSONObject((Map)object2, bl2));
                    continue;
                }
                if (this.isStandardProperty(object2.getClass())) {
                    this.map.put(string2, object2);
                    continue;
                }
                if (object2.getClass().getPackage().getName().startsWith("java") || object2.getClass().getClassLoader() == null) {
                    this.map.put(string2, object2.toString());
                    continue;
                }
                this.map.put(string2, new JSONObject(object2, bl2));
                continue;
            }
            catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    private boolean isStandardProperty(Class clazz) {
        return clazz.isPrimitive() || clazz.isAssignableFrom(Byte.class) || clazz.isAssignableFrom(Short.class) || clazz.isAssignableFrom(Integer.class) || clazz.isAssignableFrom(Long.class) || clazz.isAssignableFrom(Float.class) || clazz.isAssignableFrom(Double.class) || clazz.isAssignableFrom(Character.class) || clazz.isAssignableFrom(String.class) || clazz.isAssignableFrom(Boolean.class);
    }

    public JSONObject(Object object, String[] stringArray) {
        this();
        Class<?> clazz = object.getClass();
        for (int i2 = 0; i2 < stringArray.length; ++i2) {
            String string = stringArray[i2];
            try {
                this.putOpt(string, clazz.getField(string).get(object));
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public JSONObject(String string) throws JSONException {
        this(new JSONTokener(string));
    }

    public JSONObject accumulate(String string, Object object) throws JSONException {
        JSONObject.testValidity(object);
        Object object2 = this.opt(string);
        if (object2 == null) {
            this.put(string, object instanceof JSONArray ? new JSONArray().put(object) : object);
        } else if (object2 instanceof JSONArray) {
            ((JSONArray)object2).put(object);
        } else {
            this.put(string, new JSONArray().put(object2).put(object));
        }
        return this;
    }

    public JSONObject append(String string, Object object) throws JSONException {
        JSONObject.testValidity(object);
        Object object2 = this.opt(string);
        if (object2 == null) {
            this.put(string, new JSONArray().put(object));
        } else if (object2 instanceof JSONArray) {
            this.put(string, ((JSONArray)object2).put(object));
        } else {
            throw new JSONException("JSONObject[" + string + "] is not a JSONArray.");
        }
        return this;
    }

    public static String doubleToString(double d2) {
        if (Double.isInfinite(d2) || Double.isNaN(d2)) {
            return "null";
        }
        String string = Double.toString(d2);
        if (string.indexOf(46) > 0 && string.indexOf(101) < 0 && string.indexOf(69) < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    public Object get(String string) throws JSONException {
        Object object = this.opt(string);
        if (object == null) {
            throw new JSONException("JSONObject[" + JSONObject.quote(string) + "] not found.");
        }
        return object;
    }

    public boolean getBoolean(String string) throws JSONException {
        Object object = this.get(string);
        if (object.equals(Boolean.FALSE) || object instanceof String && ((String)object).equalsIgnoreCase("false")) {
            return false;
        }
        if (object.equals(Boolean.TRUE) || object instanceof String && ((String)object).equalsIgnoreCase("true")) {
            return true;
        }
        throw new JSONException("JSONObject[" + JSONObject.quote(string) + "] is not a Boolean.");
    }

    public double getDouble(String string) throws JSONException {
        Object object = this.get(string);
        try {
            return object instanceof Number ? ((Number)object).doubleValue() : Double.valueOf((String)object).doubleValue();
        }
        catch (Exception exception) {
            throw new JSONException("JSONObject[" + JSONObject.quote(string) + "] is not a number.");
        }
    }

    public int getInt(String string) throws JSONException {
        Object object = this.get(string);
        return object instanceof Number ? ((Number)object).intValue() : (int)this.getDouble(string);
    }

    public JSONArray getJSONArray(String string) throws JSONException {
        Object object = this.get(string);
        if (object instanceof JSONArray) {
            return (JSONArray)object;
        }
        throw new JSONException("JSONObject[" + JSONObject.quote(string) + "] is not a JSONArray.");
    }

    public JSONObject getJSONObject(String string) throws JSONException {
        Object object = this.get(string);
        if (object instanceof JSONObject) {
            return (JSONObject)object;
        }
        throw new JSONException("JSONObject[" + JSONObject.quote(string) + "] is not a JSONObject.");
    }

    public long getLong(String string) throws JSONException {
        Object object = this.get(string);
        return object instanceof Number ? ((Number)object).longValue() : (long)this.getDouble(string);
    }

    public static String[] getNames(JSONObject jSONObject) {
        int n2 = jSONObject.length();
        if (n2 == 0) {
            return null;
        }
        Iterator iterator = jSONObject.keys();
        String[] stringArray = new String[n2];
        int n3 = 0;
        while (iterator.hasNext()) {
            stringArray[n3] = (String)iterator.next();
            ++n3;
        }
        return stringArray;
    }

    public static String[] getNames(Object object) {
        if (object == null) {
            return null;
        }
        Class<?> clazz = object.getClass();
        Field[] fieldArray = clazz.getFields();
        int n2 = fieldArray.length;
        if (n2 == 0) {
            return null;
        }
        String[] stringArray = new String[n2];
        for (int i2 = 0; i2 < n2; ++i2) {
            stringArray[i2] = fieldArray[i2].getName();
        }
        return stringArray;
    }

    public String getString(String string) throws JSONException {
        return this.get(string).toString();
    }

    public boolean has(String string) {
        return this.map.containsKey(string);
    }

    public boolean isNull(String string) {
        return NULL.equals(this.opt(string));
    }

    public Iterator keys() {
        return this.map.keySet().iterator();
    }

    public int length() {
        return this.map.size();
    }

    public JSONArray names() {
        JSONArray jSONArray = new JSONArray();
        Iterator iterator = this.keys();
        while (iterator.hasNext()) {
            jSONArray.put(iterator.next());
        }
        return jSONArray.length() == 0 ? null : jSONArray;
    }

    public static String numberToString(Number number) throws JSONException {
        if (number == null) {
            throw new JSONException("Null pointer");
        }
        JSONObject.testValidity(number);
        String string = number.toString();
        if (string.indexOf(46) > 0 && string.indexOf(101) < 0 && string.indexOf(69) < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }

    public Object opt(String string) {
        return string == null ? null : this.map.get(string);
    }

    public boolean optBoolean(String string) {
        return this.optBoolean(string, false);
    }

    public boolean optBoolean(String string, boolean bl2) {
        try {
            return this.getBoolean(string);
        }
        catch (Exception exception) {
            return bl2;
        }
    }

    public JSONObject put(String string, Collection collection) throws JSONException {
        this.put(string, new JSONArray(collection));
        return this;
    }

    public double optDouble(String string) {
        return this.optDouble(string, Double.NaN);
    }

    public double optDouble(String string, double d2) {
        try {
            Object object = this.opt(string);
            return object instanceof Number ? ((Number)object).doubleValue() : new Double((String)object).doubleValue();
        }
        catch (Exception exception) {
            return d2;
        }
    }

    public int optInt(String string) {
        return this.optInt(string, 0);
    }

    public int optInt(String string, int n2) {
        try {
            return this.getInt(string);
        }
        catch (Exception exception) {
            return n2;
        }
    }

    public JSONArray optJSONArray(String string) {
        Object object = this.opt(string);
        return object instanceof JSONArray ? (JSONArray)object : null;
    }

    public JSONObject optJSONObject(String string) {
        Object object = this.opt(string);
        return object instanceof JSONObject ? (JSONObject)object : null;
    }

    public long optLong(String string) {
        return this.optLong(string, 0L);
    }

    public long optLong(String string, long l2) {
        try {
            return this.getLong(string);
        }
        catch (Exception exception) {
            return l2;
        }
    }

    public String optString(String string) {
        return this.optString(string, "");
    }

    public String optString(String string, String string2) {
        Object object = this.opt(string);
        return object != null ? object.toString() : string2;
    }

    public JSONObject put(String string, boolean bl2) throws JSONException {
        this.put(string, bl2 ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }

    public JSONObject put(String string, double d2) throws JSONException {
        this.put(string, new Double(d2));
        return this;
    }

    public JSONObject put(String string, int n2) throws JSONException {
        this.put(string, new Integer(n2));
        return this;
    }

    public JSONObject put(String string, long l2) throws JSONException {
        this.put(string, new Long(l2));
        return this;
    }

    public JSONObject put(String string, Map map) throws JSONException {
        this.put(string, new JSONObject(map));
        return this;
    }

    public JSONObject put(String string, Object object) throws JSONException {
        if (string == null) {
            throw new JSONException("Null key.");
        }
        if (object != null) {
            JSONObject.testValidity(object);
            this.map.put(string, object);
        } else {
            this.remove(string);
        }
        return this;
    }

    public JSONObject putOnce(String string, Object object) throws JSONException {
        if (string != null && object != null) {
            if (this.opt(string) != null) {
                throw new JSONException("Duplicate key \"" + string + "\"");
            }
            this.put(string, object);
        }
        return this;
    }

    public JSONObject putOpt(String string, Object object) throws JSONException {
        if (string != null && object != null) {
            this.put(string, object);
        }
        return this;
    }

    public static String quote(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }
        char c2 = '\u0000';
        int n2 = string.length();
        StringBuffer stringBuffer = new StringBuffer(n2 + 4);
        stringBuffer.append('\"');
        block9: for (int i2 = 0; i2 < n2; ++i2) {
            char c3 = c2;
            c2 = string.charAt(i2);
            switch (c2) {
                case '\"': 
                case '\\': {
                    stringBuffer.append('\\');
                    stringBuffer.append(c2);
                    continue block9;
                }
                case '/': {
                    if (c3 == '<') {
                        stringBuffer.append('\\');
                    }
                    stringBuffer.append(c2);
                    continue block9;
                }
                case '\b': {
                    stringBuffer.append("\\b");
                    continue block9;
                }
                case '\t': {
                    stringBuffer.append("\\t");
                    continue block9;
                }
                case '\n': {
                    stringBuffer.append("\\n");
                    continue block9;
                }
                case '\f': {
                    stringBuffer.append("\\f");
                    continue block9;
                }
                case '\r': {
                    stringBuffer.append("\\r");
                    continue block9;
                }
                default: {
                    if (c2 < ' ' || c2 >= '\u0080' && c2 < '\u00a0' || c2 >= '\u2000' && c2 < '\u2100') {
                        String string2 = "000" + Integer.toHexString(c2);
                        stringBuffer.append("\\u" + string2.substring(string2.length() - 4));
                        continue block9;
                    }
                    stringBuffer.append(c2);
                }
            }
        }
        stringBuffer.append('\"');
        return stringBuffer.toString();
    }

    public Object remove(String string) {
        return this.map.remove(string);
    }

    public Iterator sortedKeys() {
        return new TreeSet(this.map.keySet()).iterator();
    }

    public static Object stringToValue(String string) {
        if (string.equals("")) {
            return string;
        }
        if (string.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (string.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("null")) {
            return NULL;
        }
        char c2 = string.charAt(0);
        if (c2 >= '0' && c2 <= '9' || c2 == '.' || c2 == '-' || c2 == '+') {
            if (c2 == '0') {
                if (string.length() > 2 && (string.charAt(1) == 'x' || string.charAt(1) == 'X')) {
                    try {
                        return new Integer(Integer.parseInt(string.substring(2), 16));
                    }
                    catch (Exception exception) {
                    }
                } else {
                    try {
                        return new Integer(Integer.parseInt(string, 8));
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
            try {
                return new Integer(string);
            }
            catch (Exception exception) {
                try {
                    return new Long(string);
                }
                catch (Exception exception2) {
                    try {
                        return new Double(string);
                    }
                    catch (Exception exception3) {
                        // empty catch block
                    }
                }
            }
        }
        return string;
    }

    static void testValidity(Object object) throws JSONException {
        if (object != null && (object instanceof Double ? ((Double)object).isInfinite() || ((Double)object).isNaN() : object instanceof Float && (((Float)object).isInfinite() || ((Float)object).isNaN()))) {
            throw new JSONException("JSON does not allow non-finite numbers.");
        }
    }

    public JSONArray toJSONArray(JSONArray jSONArray) throws JSONException {
        if (jSONArray == null || jSONArray.length() == 0) {
            return null;
        }
        JSONArray jSONArray2 = new JSONArray();
        for (int i2 = 0; i2 < jSONArray.length(); ++i2) {
            jSONArray2.put(this.opt(jSONArray.getString(i2)));
        }
        return jSONArray2;
    }

    public String toString() {
        try {
            Iterator iterator = this.keys();
            StringBuffer stringBuffer = new StringBuffer("{");
            while (iterator.hasNext()) {
                if (stringBuffer.length() > 1) {
                    stringBuffer.append(',');
                }
                Object e2 = iterator.next();
                stringBuffer.append(JSONObject.quote(e2.toString()));
                stringBuffer.append(':');
                stringBuffer.append(JSONObject.valueToString(this.map.get(e2)));
            }
            stringBuffer.append('}');
            return stringBuffer.toString();
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
            return "{}";
        }
        Iterator iterator = this.sortedKeys();
        StringBuffer stringBuffer = new StringBuffer("{");
        int n5 = n3 + n2;
        if (n4 == 1) {
            Object e2 = iterator.next();
            stringBuffer.append(JSONObject.quote(e2.toString()));
            stringBuffer.append(": ");
            stringBuffer.append(JSONObject.valueToString(this.map.get(e2), n2, n3));
        } else {
            int n6;
            while (iterator.hasNext()) {
                Object e3 = iterator.next();
                if (stringBuffer.length() > 1) {
                    stringBuffer.append(",\n");
                } else {
                    stringBuffer.append('\n');
                }
                for (n6 = 0; n6 < n5; ++n6) {
                    stringBuffer.append(' ');
                }
                stringBuffer.append(JSONObject.quote(e3.toString()));
                stringBuffer.append(": ");
                stringBuffer.append(JSONObject.valueToString(this.map.get(e3), n2, n5));
            }
            if (stringBuffer.length() > 1) {
                stringBuffer.append('\n');
                for (n6 = 0; n6 < n3; ++n6) {
                    stringBuffer.append(' ');
                }
            }
        }
        stringBuffer.append('}');
        return stringBuffer.toString();
    }

    static String valueToString(Object object) throws JSONException {
        if (object == null || object.equals(null)) {
            return "null";
        }
        if (object instanceof JSONString) {
            String string;
            try {
                string = ((JSONString)object).toJSONString();
            }
            catch (Exception exception) {
                throw new JSONException(exception);
            }
            if (string instanceof String) {
                return string;
            }
            throw new JSONException("Bad value from toJSONString: " + string);
        }
        if (object instanceof Number) {
            return JSONObject.numberToString((Number)object);
        }
        if (object instanceof Boolean || object instanceof JSONObject || object instanceof JSONArray) {
            return object.toString();
        }
        if (object instanceof Map) {
            return new JSONObject((Map)object).toString();
        }
        if (object instanceof Collection) {
            return new JSONArray((Collection)object).toString();
        }
        if (object.getClass().isArray()) {
            return new JSONArray(object).toString();
        }
        return JSONObject.quote(object.toString());
    }

    static String valueToString(Object object, int n2, int n3) throws JSONException {
        if (object == null || object.equals(null)) {
            return "null";
        }
        try {
            String string;
            if (object instanceof JSONString && (string = ((JSONString)object).toJSONString()) instanceof String) {
                return string;
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        if (object instanceof Number) {
            return JSONObject.numberToString((Number)object);
        }
        if (object instanceof Boolean) {
            return object.toString();
        }
        if (object instanceof JSONObject) {
            return ((JSONObject)object).toString(n2, n3);
        }
        if (object instanceof JSONArray) {
            return ((JSONArray)object).toString(n2, n3);
        }
        if (object instanceof Map) {
            return new JSONObject((Map)object).toString(n2, n3);
        }
        if (object instanceof Collection) {
            return new JSONArray((Collection)object).toString(n2, n3);
        }
        if (object.getClass().isArray()) {
            return new JSONArray(object).toString(n2, n3);
        }
        return JSONObject.quote(object.toString());
    }

    public Writer write(Writer writer) throws JSONException {
        try {
            boolean bl2 = false;
            Iterator iterator = this.keys();
            writer.write(123);
            while (iterator.hasNext()) {
                if (bl2) {
                    writer.write(44);
                }
                Object e2 = iterator.next();
                writer.write(JSONObject.quote(e2.toString()));
                writer.write(58);
                Object v2 = this.map.get(e2);
                if (v2 instanceof JSONObject) {
                    ((JSONObject)v2).write(writer);
                } else if (v2 instanceof JSONArray) {
                    ((JSONArray)v2).write(writer);
                } else {
                    writer.write(JSONObject.valueToString(v2));
                }
                bl2 = true;
            }
            writer.write(125);
            return writer;
        }
        catch (IOException iOException) {
            throw new JSONException(iOException);
        }
    }

    private static final class Null {
        private Null() {
        }

        protected final Object clone() {
            return this;
        }

        public boolean equals(Object object) {
            return object == null || object == this;
        }

        public String toString() {
            return "null";
        }
    }
}

