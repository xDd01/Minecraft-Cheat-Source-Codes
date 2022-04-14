/*
 * Decompiled with CFR 0.152.
 */
package io.socket.hasbinary;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HasBinary {
    private static final Logger logger = Logger.getLogger(HasBinary.class.getName());

    private HasBinary() {
    }

    public static boolean hasBinary(Object data) {
        return HasBinary._hasBinary(data);
    }

    private static boolean _hasBinary(Object obj) {
        block9: {
            block8: {
                if (obj == null) {
                    return false;
                }
                if (obj instanceof byte[]) {
                    return true;
                }
                if (!(obj instanceof JSONArray)) break block8;
                JSONArray _obj = (JSONArray)obj;
                int length = _obj.length();
                for (int i2 = 0; i2 < length; ++i2) {
                    Object v2;
                    try {
                        v2 = _obj.isNull(i2) ? null : _obj.get(i2);
                    }
                    catch (JSONException e2) {
                        logger.log(Level.WARNING, "An error occured while retrieving data from JSONArray", e2);
                        return false;
                    }
                    if (!HasBinary._hasBinary(v2)) continue;
                    return true;
                }
                break block9;
            }
            if (!(obj instanceof JSONObject)) break block9;
            JSONObject _obj = (JSONObject)obj;
            Iterator keys = _obj.keys();
            while (keys.hasNext()) {
                Object v3;
                String key = (String)keys.next();
                try {
                    v3 = _obj.get(key);
                }
                catch (JSONException e3) {
                    logger.log(Level.WARNING, "An error occured while retrieving data from JSONObject", e3);
                    return false;
                }
                if (!HasBinary._hasBinary(v3)) continue;
                return true;
            }
        }
        return false;
    }
}

