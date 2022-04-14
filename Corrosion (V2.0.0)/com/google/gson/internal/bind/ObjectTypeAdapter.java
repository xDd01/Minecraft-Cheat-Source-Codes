/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson.internal.bind;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class ObjectTypeAdapter
extends TypeAdapter<Object> {
    public static final TypeAdapterFactory FACTORY = new TypeAdapterFactory(){

        @Override
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
            if (type.getRawType() == Object.class) {
                return new ObjectTypeAdapter(gson);
            }
            return null;
        }
    };
    private final Gson gson;

    private ObjectTypeAdapter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Object read(JsonReader in2) throws IOException {
        JsonToken token = in2.peek();
        switch (token) {
            case BEGIN_ARRAY: {
                ArrayList<Object> list = new ArrayList<Object>();
                in2.beginArray();
                while (in2.hasNext()) {
                    list.add(this.read(in2));
                }
                in2.endArray();
                return list;
            }
            case BEGIN_OBJECT: {
                LinkedTreeMap<String, Object> map = new LinkedTreeMap<String, Object>();
                in2.beginObject();
                while (in2.hasNext()) {
                    map.put(in2.nextName(), this.read(in2));
                }
                in2.endObject();
                return map;
            }
            case STRING: {
                return in2.nextString();
            }
            case NUMBER: {
                return in2.nextDouble();
            }
            case BOOLEAN: {
                return in2.nextBoolean();
            }
            case NULL: {
                in2.nextNull();
                return null;
            }
        }
        throw new IllegalStateException();
    }

    @Override
    public void write(JsonWriter out, Object value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        TypeAdapter<?> typeAdapter = this.gson.getAdapter(value.getClass());
        if (typeAdapter instanceof ObjectTypeAdapter) {
            out.beginObject();
            out.endObject();
            return;
        }
        typeAdapter.write(out, value);
    }
}

