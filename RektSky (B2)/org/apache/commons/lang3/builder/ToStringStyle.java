package org.apache.commons.lang3.builder;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import org.apache.commons.lang3.*;

public abstract class ToStringStyle implements Serializable
{
    private static final long serialVersionUID = -2587890625525655916L;
    public static final ToStringStyle DEFAULT_STYLE;
    public static final ToStringStyle MULTI_LINE_STYLE;
    public static final ToStringStyle NO_FIELD_NAMES_STYLE;
    public static final ToStringStyle SHORT_PREFIX_STYLE;
    public static final ToStringStyle SIMPLE_STYLE;
    public static final ToStringStyle NO_CLASS_NAME_STYLE;
    public static final ToStringStyle JSON_STYLE;
    private static final ThreadLocal<WeakHashMap<Object, Object>> REGISTRY;
    private boolean useFieldNames;
    private boolean useClassName;
    private boolean useShortClassName;
    private boolean useIdentityHashCode;
    private String contentStart;
    private String contentEnd;
    private String fieldNameValueSeparator;
    private boolean fieldSeparatorAtStart;
    private boolean fieldSeparatorAtEnd;
    private String fieldSeparator;
    private String arrayStart;
    private String arraySeparator;
    private boolean arrayContentDetail;
    private String arrayEnd;
    private boolean defaultFullDetail;
    private String nullText;
    private String sizeStartText;
    private String sizeEndText;
    private String summaryObjectStartText;
    private String summaryObjectEndText;
    
    static Map<Object, Object> getRegistry() {
        return ToStringStyle.REGISTRY.get();
    }
    
    static boolean isRegistered(final Object value) {
        final Map<Object, Object> m = getRegistry();
        return m != null && m.containsKey(value);
    }
    
    static void register(final Object value) {
        if (value != null) {
            final Map<Object, Object> m = getRegistry();
            if (m == null) {
                ToStringStyle.REGISTRY.set(new WeakHashMap<Object, Object>());
            }
            getRegistry().put(value, null);
        }
    }
    
    static void unregister(final Object value) {
        if (value != null) {
            final Map<Object, Object> m = getRegistry();
            if (m != null) {
                m.remove(value);
                if (m.isEmpty()) {
                    ToStringStyle.REGISTRY.remove();
                }
            }
        }
    }
    
    protected ToStringStyle() {
        this.useFieldNames = true;
        this.useClassName = true;
        this.useShortClassName = false;
        this.useIdentityHashCode = true;
        this.contentStart = "[";
        this.contentEnd = "]";
        this.fieldNameValueSeparator = "=";
        this.fieldSeparatorAtStart = false;
        this.fieldSeparatorAtEnd = false;
        this.fieldSeparator = ",";
        this.arrayStart = "{";
        this.arraySeparator = ",";
        this.arrayContentDetail = true;
        this.arrayEnd = "}";
        this.defaultFullDetail = true;
        this.nullText = "<null>";
        this.sizeStartText = "<size=";
        this.sizeEndText = ">";
        this.summaryObjectStartText = "<";
        this.summaryObjectEndText = ">";
    }
    
    public void appendSuper(final StringBuffer buffer, final String superToString) {
        this.appendToString(buffer, superToString);
    }
    
    public void appendToString(final StringBuffer buffer, final String toString) {
        if (toString != null) {
            final int pos1 = toString.indexOf(this.contentStart) + this.contentStart.length();
            final int pos2 = toString.lastIndexOf(this.contentEnd);
            if (pos1 != pos2 && pos1 >= 0 && pos2 >= 0) {
                if (this.fieldSeparatorAtStart) {
                    this.removeLastFieldSeparator(buffer);
                }
                buffer.append(toString, pos1, pos2);
                this.appendFieldSeparator(buffer);
            }
        }
    }
    
    public void appendStart(final StringBuffer buffer, final Object object) {
        if (object != null) {
            this.appendClassName(buffer, object);
            this.appendIdentityHashCode(buffer, object);
            this.appendContentStart(buffer);
            if (this.fieldSeparatorAtStart) {
                this.appendFieldSeparator(buffer);
            }
        }
    }
    
    public void appendEnd(final StringBuffer buffer, final Object object) {
        if (!this.fieldSeparatorAtEnd) {
            this.removeLastFieldSeparator(buffer);
        }
        this.appendContentEnd(buffer);
        unregister(object);
    }
    
    protected void removeLastFieldSeparator(final StringBuffer buffer) {
        final int len = buffer.length();
        final int sepLen = this.fieldSeparator.length();
        if (len > 0 && sepLen > 0 && len >= sepLen) {
            boolean match = true;
            for (int i = 0; i < sepLen; ++i) {
                if (buffer.charAt(len - 1 - i) != this.fieldSeparator.charAt(sepLen - 1 - i)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                buffer.setLength(len - sepLen);
            }
        }
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final Object value, final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);
        if (value == null) {
            this.appendNullText(buffer, fieldName);
        }
        else {
            this.appendInternal(buffer, fieldName, value, this.isFullDetail(fullDetail));
        }
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendInternal(final StringBuffer buffer, final String fieldName, final Object value, final boolean detail) {
        if (isRegistered(value) && !(value instanceof Number) && !(value instanceof Boolean) && !(value instanceof Character)) {
            this.appendCyclicObject(buffer, fieldName, value);
            return;
        }
        register(value);
        try {
            if (value instanceof Collection) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (Collection<?>)value);
                }
                else {
                    this.appendSummarySize(buffer, fieldName, ((Collection)value).size());
                }
            }
            else if (value instanceof Map) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (Map<?, ?>)value);
                }
                else {
                    this.appendSummarySize(buffer, fieldName, ((Map)value).size());
                }
            }
            else if (value instanceof long[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (long[])value);
                }
                else {
                    this.appendSummary(buffer, fieldName, (long[])value);
                }
            }
            else if (value instanceof int[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (int[])value);
                }
                else {
                    this.appendSummary(buffer, fieldName, (int[])value);
                }
            }
            else if (value instanceof short[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (short[])value);
                }
                else {
                    this.appendSummary(buffer, fieldName, (short[])value);
                }
            }
            else if (value instanceof byte[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (byte[])value);
                }
                else {
                    this.appendSummary(buffer, fieldName, (byte[])value);
                }
            }
            else if (value instanceof char[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (char[])value);
                }
                else {
                    this.appendSummary(buffer, fieldName, (char[])value);
                }
            }
            else if (value instanceof double[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (double[])value);
                }
                else {
                    this.appendSummary(buffer, fieldName, (double[])value);
                }
            }
            else if (value instanceof float[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (float[])value);
                }
                else {
                    this.appendSummary(buffer, fieldName, (float[])value);
                }
            }
            else if (value instanceof boolean[]) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (boolean[])value);
                }
                else {
                    this.appendSummary(buffer, fieldName, (boolean[])value);
                }
            }
            else if (value.getClass().isArray()) {
                if (detail) {
                    this.appendDetail(buffer, fieldName, (Object[])value);
                }
                else {
                    this.appendSummary(buffer, fieldName, (Object[])value);
                }
            }
            else if (detail) {
                this.appendDetail(buffer, fieldName, value);
            }
            else {
                this.appendSummary(buffer, fieldName, value);
            }
        }
        finally {
            unregister(value);
        }
    }
    
    protected void appendCyclicObject(final StringBuffer buffer, final String fieldName, final Object value) {
        ObjectUtils.identityToString(buffer, value);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
        buffer.append(value);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Collection<?> coll) {
        buffer.append(coll);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Map<?, ?> map) {
        buffer.append(map);
    }
    
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final Object value) {
        buffer.append(this.summaryObjectStartText);
        buffer.append(this.getShortClassName(value.getClass()));
        buffer.append(this.summaryObjectEndText);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final long value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final long value) {
        buffer.append(value);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final int value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final int value) {
        buffer.append(value);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final short value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final short value) {
        buffer.append(value);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final byte value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final byte value) {
        buffer.append(value);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final char value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final char value) {
        buffer.append(value);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final double value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final double value) {
        buffer.append(value);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final float value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final float value) {
        buffer.append(value);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final boolean value) {
        this.appendFieldStart(buffer, fieldName);
        this.appendDetail(buffer, fieldName, value);
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final boolean value) {
        buffer.append(value);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final Object[] array, final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);
        if (array == null) {
            this.appendNullText(buffer, fieldName);
        }
        else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);
        }
        else {
            this.appendSummary(buffer, fieldName, array);
        }
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; ++i) {
            final Object item = array[i];
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            if (item == null) {
                this.appendNullText(buffer, fieldName);
            }
            else {
                this.appendInternal(buffer, fieldName, item, this.arrayContentDetail);
            }
        }
        buffer.append(this.arrayEnd);
    }
    
    protected void reflectionAppendArrayDetail(final StringBuffer buffer, final String fieldName, final Object array) {
        buffer.append(this.arrayStart);
        for (int length = Array.getLength(array), i = 0; i < length; ++i) {
            final Object item = Array.get(array, i);
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            if (item == null) {
                this.appendNullText(buffer, fieldName);
            }
            else {
                this.appendInternal(buffer, fieldName, item, this.arrayContentDetail);
            }
        }
        buffer.append(this.arrayEnd);
    }
    
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final Object[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final long[] array, final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);
        if (array == null) {
            this.appendNullText(buffer, fieldName);
        }
        else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);
        }
        else {
            this.appendSummary(buffer, fieldName, array);
        }
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final long[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }
    
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final long[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final int[] array, final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);
        if (array == null) {
            this.appendNullText(buffer, fieldName);
        }
        else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);
        }
        else {
            this.appendSummary(buffer, fieldName, array);
        }
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final int[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }
    
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final int[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final short[] array, final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);
        if (array == null) {
            this.appendNullText(buffer, fieldName);
        }
        else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);
        }
        else {
            this.appendSummary(buffer, fieldName, array);
        }
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final short[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }
    
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final short[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final byte[] array, final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);
        if (array == null) {
            this.appendNullText(buffer, fieldName);
        }
        else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);
        }
        else {
            this.appendSummary(buffer, fieldName, array);
        }
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final byte[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }
    
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final byte[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final char[] array, final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);
        if (array == null) {
            this.appendNullText(buffer, fieldName);
        }
        else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);
        }
        else {
            this.appendSummary(buffer, fieldName, array);
        }
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final char[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }
    
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final char[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final double[] array, final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);
        if (array == null) {
            this.appendNullText(buffer, fieldName);
        }
        else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);
        }
        else {
            this.appendSummary(buffer, fieldName, array);
        }
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final double[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }
    
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final double[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final float[] array, final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);
        if (array == null) {
            this.appendNullText(buffer, fieldName);
        }
        else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);
        }
        else {
            this.appendSummary(buffer, fieldName, array);
        }
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final float[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }
    
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final float[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }
    
    public void append(final StringBuffer buffer, final String fieldName, final boolean[] array, final Boolean fullDetail) {
        this.appendFieldStart(buffer, fieldName);
        if (array == null) {
            this.appendNullText(buffer, fieldName);
        }
        else if (this.isFullDetail(fullDetail)) {
            this.appendDetail(buffer, fieldName, array);
        }
        else {
            this.appendSummary(buffer, fieldName, array);
        }
        this.appendFieldEnd(buffer, fieldName);
    }
    
    protected void appendDetail(final StringBuffer buffer, final String fieldName, final boolean[] array) {
        buffer.append(this.arrayStart);
        for (int i = 0; i < array.length; ++i) {
            if (i > 0) {
                buffer.append(this.arraySeparator);
            }
            this.appendDetail(buffer, fieldName, array[i]);
        }
        buffer.append(this.arrayEnd);
    }
    
    protected void appendSummary(final StringBuffer buffer, final String fieldName, final boolean[] array) {
        this.appendSummarySize(buffer, fieldName, array.length);
    }
    
    protected void appendClassName(final StringBuffer buffer, final Object object) {
        if (this.useClassName && object != null) {
            register(object);
            if (this.useShortClassName) {
                buffer.append(this.getShortClassName(object.getClass()));
            }
            else {
                buffer.append(object.getClass().getName());
            }
        }
    }
    
    protected void appendIdentityHashCode(final StringBuffer buffer, final Object object) {
        if (this.isUseIdentityHashCode() && object != null) {
            register(object);
            buffer.append('@');
            buffer.append(Integer.toHexString(System.identityHashCode(object)));
        }
    }
    
    protected void appendContentStart(final StringBuffer buffer) {
        buffer.append(this.contentStart);
    }
    
    protected void appendContentEnd(final StringBuffer buffer) {
        buffer.append(this.contentEnd);
    }
    
    protected void appendNullText(final StringBuffer buffer, final String fieldName) {
        buffer.append(this.nullText);
    }
    
    protected void appendFieldSeparator(final StringBuffer buffer) {
        buffer.append(this.fieldSeparator);
    }
    
    protected void appendFieldStart(final StringBuffer buffer, final String fieldName) {
        if (this.useFieldNames && fieldName != null) {
            buffer.append(fieldName);
            buffer.append(this.fieldNameValueSeparator);
        }
    }
    
    protected void appendFieldEnd(final StringBuffer buffer, final String fieldName) {
        this.appendFieldSeparator(buffer);
    }
    
    protected void appendSummarySize(final StringBuffer buffer, final String fieldName, final int size) {
        buffer.append(this.sizeStartText);
        buffer.append(size);
        buffer.append(this.sizeEndText);
    }
    
    protected boolean isFullDetail(final Boolean fullDetailRequest) {
        if (fullDetailRequest == null) {
            return this.defaultFullDetail;
        }
        return fullDetailRequest;
    }
    
    protected String getShortClassName(final Class<?> cls) {
        return ClassUtils.getShortClassName(cls);
    }
    
    protected boolean isUseClassName() {
        return this.useClassName;
    }
    
    protected void setUseClassName(final boolean useClassName) {
        this.useClassName = useClassName;
    }
    
    protected boolean isUseShortClassName() {
        return this.useShortClassName;
    }
    
    protected void setUseShortClassName(final boolean useShortClassName) {
        this.useShortClassName = useShortClassName;
    }
    
    protected boolean isUseIdentityHashCode() {
        return this.useIdentityHashCode;
    }
    
    protected void setUseIdentityHashCode(final boolean useIdentityHashCode) {
        this.useIdentityHashCode = useIdentityHashCode;
    }
    
    protected boolean isUseFieldNames() {
        return this.useFieldNames;
    }
    
    protected void setUseFieldNames(final boolean useFieldNames) {
        this.useFieldNames = useFieldNames;
    }
    
    protected boolean isDefaultFullDetail() {
        return this.defaultFullDetail;
    }
    
    protected void setDefaultFullDetail(final boolean defaultFullDetail) {
        this.defaultFullDetail = defaultFullDetail;
    }
    
    protected boolean isArrayContentDetail() {
        return this.arrayContentDetail;
    }
    
    protected void setArrayContentDetail(final boolean arrayContentDetail) {
        this.arrayContentDetail = arrayContentDetail;
    }
    
    protected String getArrayStart() {
        return this.arrayStart;
    }
    
    protected void setArrayStart(String arrayStart) {
        if (arrayStart == null) {
            arrayStart = "";
        }
        this.arrayStart = arrayStart;
    }
    
    protected String getArrayEnd() {
        return this.arrayEnd;
    }
    
    protected void setArrayEnd(String arrayEnd) {
        if (arrayEnd == null) {
            arrayEnd = "";
        }
        this.arrayEnd = arrayEnd;
    }
    
    protected String getArraySeparator() {
        return this.arraySeparator;
    }
    
    protected void setArraySeparator(String arraySeparator) {
        if (arraySeparator == null) {
            arraySeparator = "";
        }
        this.arraySeparator = arraySeparator;
    }
    
    protected String getContentStart() {
        return this.contentStart;
    }
    
    protected void setContentStart(String contentStart) {
        if (contentStart == null) {
            contentStart = "";
        }
        this.contentStart = contentStart;
    }
    
    protected String getContentEnd() {
        return this.contentEnd;
    }
    
    protected void setContentEnd(String contentEnd) {
        if (contentEnd == null) {
            contentEnd = "";
        }
        this.contentEnd = contentEnd;
    }
    
    protected String getFieldNameValueSeparator() {
        return this.fieldNameValueSeparator;
    }
    
    protected void setFieldNameValueSeparator(String fieldNameValueSeparator) {
        if (fieldNameValueSeparator == null) {
            fieldNameValueSeparator = "";
        }
        this.fieldNameValueSeparator = fieldNameValueSeparator;
    }
    
    protected String getFieldSeparator() {
        return this.fieldSeparator;
    }
    
    protected void setFieldSeparator(String fieldSeparator) {
        if (fieldSeparator == null) {
            fieldSeparator = "";
        }
        this.fieldSeparator = fieldSeparator;
    }
    
    protected boolean isFieldSeparatorAtStart() {
        return this.fieldSeparatorAtStart;
    }
    
    protected void setFieldSeparatorAtStart(final boolean fieldSeparatorAtStart) {
        this.fieldSeparatorAtStart = fieldSeparatorAtStart;
    }
    
    protected boolean isFieldSeparatorAtEnd() {
        return this.fieldSeparatorAtEnd;
    }
    
    protected void setFieldSeparatorAtEnd(final boolean fieldSeparatorAtEnd) {
        this.fieldSeparatorAtEnd = fieldSeparatorAtEnd;
    }
    
    protected String getNullText() {
        return this.nullText;
    }
    
    protected void setNullText(String nullText) {
        if (nullText == null) {
            nullText = "";
        }
        this.nullText = nullText;
    }
    
    protected String getSizeStartText() {
        return this.sizeStartText;
    }
    
    protected void setSizeStartText(String sizeStartText) {
        if (sizeStartText == null) {
            sizeStartText = "";
        }
        this.sizeStartText = sizeStartText;
    }
    
    protected String getSizeEndText() {
        return this.sizeEndText;
    }
    
    protected void setSizeEndText(String sizeEndText) {
        if (sizeEndText == null) {
            sizeEndText = "";
        }
        this.sizeEndText = sizeEndText;
    }
    
    protected String getSummaryObjectStartText() {
        return this.summaryObjectStartText;
    }
    
    protected void setSummaryObjectStartText(String summaryObjectStartText) {
        if (summaryObjectStartText == null) {
            summaryObjectStartText = "";
        }
        this.summaryObjectStartText = summaryObjectStartText;
    }
    
    protected String getSummaryObjectEndText() {
        return this.summaryObjectEndText;
    }
    
    protected void setSummaryObjectEndText(String summaryObjectEndText) {
        if (summaryObjectEndText == null) {
            summaryObjectEndText = "";
        }
        this.summaryObjectEndText = summaryObjectEndText;
    }
    
    static {
        DEFAULT_STYLE = new DefaultToStringStyle();
        MULTI_LINE_STYLE = new MultiLineToStringStyle();
        NO_FIELD_NAMES_STYLE = new NoFieldNameToStringStyle();
        SHORT_PREFIX_STYLE = new ShortPrefixToStringStyle();
        SIMPLE_STYLE = new SimpleToStringStyle();
        NO_CLASS_NAME_STYLE = new NoClassNameToStringStyle();
        JSON_STYLE = new JsonToStringStyle();
        REGISTRY = new ThreadLocal<WeakHashMap<Object, Object>>();
    }
    
    private static final class DefaultToStringStyle extends ToStringStyle
    {
        private static final long serialVersionUID = 1L;
        
        DefaultToStringStyle() {
        }
        
        private Object readResolve() {
            return DefaultToStringStyle.DEFAULT_STYLE;
        }
    }
    
    private static final class NoFieldNameToStringStyle extends ToStringStyle
    {
        private static final long serialVersionUID = 1L;
        
        NoFieldNameToStringStyle() {
            this.setUseFieldNames(false);
        }
        
        private Object readResolve() {
            return NoFieldNameToStringStyle.NO_FIELD_NAMES_STYLE;
        }
    }
    
    private static final class ShortPrefixToStringStyle extends ToStringStyle
    {
        private static final long serialVersionUID = 1L;
        
        ShortPrefixToStringStyle() {
            this.setUseShortClassName(true);
            this.setUseIdentityHashCode(false);
        }
        
        private Object readResolve() {
            return ShortPrefixToStringStyle.SHORT_PREFIX_STYLE;
        }
    }
    
    private static final class SimpleToStringStyle extends ToStringStyle
    {
        private static final long serialVersionUID = 1L;
        
        SimpleToStringStyle() {
            this.setUseClassName(false);
            this.setUseIdentityHashCode(false);
            this.setUseFieldNames(false);
            this.setContentStart("");
            this.setContentEnd("");
        }
        
        private Object readResolve() {
            return SimpleToStringStyle.SIMPLE_STYLE;
        }
    }
    
    private static final class MultiLineToStringStyle extends ToStringStyle
    {
        private static final long serialVersionUID = 1L;
        
        MultiLineToStringStyle() {
            this.setContentStart("[");
            this.setFieldSeparator(System.lineSeparator() + "  ");
            this.setFieldSeparatorAtStart(true);
            this.setContentEnd(System.lineSeparator() + "]");
        }
        
        private Object readResolve() {
            return MultiLineToStringStyle.MULTI_LINE_STYLE;
        }
    }
    
    private static final class NoClassNameToStringStyle extends ToStringStyle
    {
        private static final long serialVersionUID = 1L;
        
        NoClassNameToStringStyle() {
            this.setUseClassName(false);
            this.setUseIdentityHashCode(false);
        }
        
        private Object readResolve() {
            return NoClassNameToStringStyle.NO_CLASS_NAME_STYLE;
        }
    }
    
    private static final class JsonToStringStyle extends ToStringStyle
    {
        private static final long serialVersionUID = 1L;
        private static final String FIELD_NAME_QUOTE = "\"";
        
        JsonToStringStyle() {
            this.setUseClassName(false);
            this.setUseIdentityHashCode(false);
            this.setContentStart("{");
            this.setContentEnd("}");
            this.setArrayStart("[");
            this.setArrayEnd("]");
            this.setFieldSeparator(",");
            this.setFieldNameValueSeparator(":");
            this.setNullText("null");
            this.setSummaryObjectStartText("\"<");
            this.setSummaryObjectEndText(">\"");
            this.setSizeStartText("\"<size=");
            this.setSizeEndText(">\"");
        }
        
        @Override
        public void append(final StringBuffer buffer, final String fieldName, final Object[] array, final Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
            super.append(buffer, fieldName, array, fullDetail);
        }
        
        @Override
        public void append(final StringBuffer buffer, final String fieldName, final long[] array, final Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
            super.append(buffer, fieldName, array, fullDetail);
        }
        
        @Override
        public void append(final StringBuffer buffer, final String fieldName, final int[] array, final Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
            super.append(buffer, fieldName, array, fullDetail);
        }
        
        @Override
        public void append(final StringBuffer buffer, final String fieldName, final short[] array, final Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
            super.append(buffer, fieldName, array, fullDetail);
        }
        
        @Override
        public void append(final StringBuffer buffer, final String fieldName, final byte[] array, final Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
            super.append(buffer, fieldName, array, fullDetail);
        }
        
        @Override
        public void append(final StringBuffer buffer, final String fieldName, final char[] array, final Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
            super.append(buffer, fieldName, array, fullDetail);
        }
        
        @Override
        public void append(final StringBuffer buffer, final String fieldName, final double[] array, final Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
            super.append(buffer, fieldName, array, fullDetail);
        }
        
        @Override
        public void append(final StringBuffer buffer, final String fieldName, final float[] array, final Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
            super.append(buffer, fieldName, array, fullDetail);
        }
        
        @Override
        public void append(final StringBuffer buffer, final String fieldName, final boolean[] array, final Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
            super.append(buffer, fieldName, array, fullDetail);
        }
        
        @Override
        public void append(final StringBuffer buffer, final String fieldName, final Object value, final Boolean fullDetail) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            if (!this.isFullDetail(fullDetail)) {
                throw new UnsupportedOperationException("FullDetail must be true when using JsonToStringStyle");
            }
            super.append(buffer, fieldName, value, fullDetail);
        }
        
        @Override
        protected void appendDetail(final StringBuffer buffer, final String fieldName, final char value) {
            this.appendValueAsString(buffer, String.valueOf(value));
        }
        
        @Override
        protected void appendDetail(final StringBuffer buffer, final String fieldName, final Object value) {
            if (value == null) {
                this.appendNullText(buffer, fieldName);
                return;
            }
            if (value instanceof String || value instanceof Character) {
                this.appendValueAsString(buffer, value.toString());
                return;
            }
            if (value instanceof Number || value instanceof Boolean) {
                buffer.append(value);
                return;
            }
            final String valueAsString = value.toString();
            if (this.isJsonObject(valueAsString) || this.isJsonArray(valueAsString)) {
                buffer.append(value);
                return;
            }
            this.appendDetail(buffer, fieldName, valueAsString);
        }
        
        private boolean isJsonArray(final String valueAsString) {
            return valueAsString.startsWith(this.getArrayStart()) && valueAsString.endsWith(this.getArrayEnd());
        }
        
        private boolean isJsonObject(final String valueAsString) {
            return valueAsString.startsWith(this.getContentStart()) && valueAsString.endsWith(this.getContentEnd());
        }
        
        private void appendValueAsString(final StringBuffer buffer, final String value) {
            buffer.append('\"').append(StringEscapeUtils.escapeJson(value)).append('\"');
        }
        
        @Override
        protected void appendFieldStart(final StringBuffer buffer, final String fieldName) {
            if (fieldName == null) {
                throw new UnsupportedOperationException("Field names are mandatory when using JsonToStringStyle");
            }
            super.appendFieldStart(buffer, "\"" + StringEscapeUtils.escapeJson(fieldName) + "\"");
        }
        
        private Object readResolve() {
            return JsonToStringStyle.JSON_STYLE;
        }
    }
}
