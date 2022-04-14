package org.yaml.snakeyaml.representer;

import java.util.regex.*;
import org.yaml.snakeyaml.nodes.*;
import biz.source_code.base64Coder.*;
import java.math.*;
import java.util.*;

class SafeRepresenter extends BaseRepresenter
{
    protected Map<Class<?>, Tag> classTags;
    public static Pattern BINARY_PATTERN;
    
    public SafeRepresenter() {
        this.nullRepresenter = new RepresentNull();
        this.representers.put(String.class, new RepresentString());
        this.representers.put(Boolean.class, new RepresentBoolean());
        this.representers.put(Character.class, new RepresentString());
        this.representers.put(byte[].class, new RepresentByteArray());
        this.multiRepresenters.put(Number.class, new RepresentNumber());
        this.multiRepresenters.put(List.class, new RepresentList());
        this.multiRepresenters.put(Map.class, new RepresentMap());
        this.multiRepresenters.put(Set.class, new RepresentSet());
        this.multiRepresenters.put(Iterator.class, new RepresentIterator());
        this.multiRepresenters.put(new Object[0].getClass(), new RepresentArray());
        this.multiRepresenters.put(Date.class, new RepresentDate());
        this.multiRepresenters.put(Enum.class, new RepresentEnum());
        this.multiRepresenters.put(Calendar.class, new RepresentDate());
        this.classTags = new HashMap<Class<?>, Tag>();
    }
    
    protected Tag getTag(final Class<?> clazz, final Tag defaultTag) {
        if (this.classTags.containsKey(clazz)) {
            return this.classTags.get(clazz);
        }
        return defaultTag;
    }
    
    @Deprecated
    public Tag addClassTag(final Class<?> clazz, final String tag) {
        return this.addClassTag(clazz, new Tag(tag));
    }
    
    public Tag addClassTag(final Class<?> clazz, final Tag tag) {
        if (tag == null) {
            throw new NullPointerException("Tag must be provided.");
        }
        return this.classTags.put(clazz, tag);
    }
    
    static {
        SafeRepresenter.BINARY_PATTERN = Pattern.compile("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F]");
    }
    
    protected class RepresentNull implements Represent
    {
        public Node representData(final Object data) {
            return SafeRepresenter.this.representScalar(Tag.NULL, "null");
        }
    }
    
    protected class RepresentString implements Represent
    {
        public Node representData(final Object data) {
            Tag tag = Tag.STR;
            Character style = null;
            String value = data.toString();
            if (SafeRepresenter.BINARY_PATTERN.matcher(value).find()) {
                tag = Tag.BINARY;
                final char[] binary = Base64Coder.encode(value.getBytes());
                value = String.valueOf(binary);
                style = '|';
            }
            return SafeRepresenter.this.representScalar(tag, value, style);
        }
    }
    
    protected class RepresentBoolean implements Represent
    {
        public Node representData(final Object data) {
            String value;
            if (Boolean.TRUE.equals(data)) {
                value = "true";
            }
            else {
                value = "false";
            }
            return SafeRepresenter.this.representScalar(Tag.BOOL, value);
        }
    }
    
    protected class RepresentNumber implements Represent
    {
        public Node representData(final Object data) {
            Tag tag;
            String value;
            if (data instanceof Byte || data instanceof Short || data instanceof Integer || data instanceof Long || data instanceof BigInteger) {
                tag = Tag.INT;
                value = data.toString();
            }
            else {
                final Number number = (Number)data;
                tag = Tag.FLOAT;
                if (number.equals(Double.NaN)) {
                    value = ".NaN";
                }
                else if (number.equals(Double.POSITIVE_INFINITY)) {
                    value = ".inf";
                }
                else if (number.equals(Double.NEGATIVE_INFINITY)) {
                    value = "-.inf";
                }
                else {
                    value = number.toString();
                }
            }
            return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(data.getClass(), tag), value);
        }
    }
    
    protected class RepresentList implements Represent
    {
        public Node representData(final Object data) {
            return SafeRepresenter.this.representSequence(SafeRepresenter.this.getTag(data.getClass(), Tag.SEQ), (Iterable<?>)data, null);
        }
    }
    
    protected class RepresentIterator implements Represent
    {
        public Node representData(final Object data) {
            final Iterator<Object> iter = (Iterator<Object>)data;
            return SafeRepresenter.this.representSequence(SafeRepresenter.this.getTag(data.getClass(), Tag.SEQ), new IteratorWrapper(iter), null);
        }
    }
    
    private class IteratorWrapper implements Iterable<Object>
    {
        private Iterator<Object> iter;
        
        public IteratorWrapper(final Iterator<Object> iter) {
            this.iter = iter;
        }
        
        public Iterator<Object> iterator() {
            return this.iter;
        }
    }
    
    protected class RepresentArray implements Represent
    {
        public Node representData(final Object data) {
            final Object[] array = (Object[])data;
            final List<Object> list = Arrays.asList(array);
            return SafeRepresenter.this.representSequence(Tag.SEQ, list, null);
        }
    }
    
    protected class RepresentMap implements Represent
    {
        public Node representData(final Object data) {
            return SafeRepresenter.this.representMapping(SafeRepresenter.this.getTag(data.getClass(), Tag.MAP), (Map<?, Object>)data, null);
        }
    }
    
    protected class RepresentSet implements Represent
    {
        public Node representData(final Object data) {
            final Map<Object, Object> value = new LinkedHashMap<Object, Object>();
            final Set<Object> set = (Set<Object>)data;
            for (final Object key : set) {
                value.put(key, null);
            }
            return SafeRepresenter.this.representMapping(SafeRepresenter.this.getTag(data.getClass(), Tag.SET), value, null);
        }
    }
    
    protected class RepresentDate implements Represent
    {
        public Node representData(final Object data) {
            Calendar calendar;
            if (data instanceof Calendar) {
                calendar = (Calendar)data;
            }
            else {
                calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTime((Date)data);
            }
            final int years = calendar.get(1);
            final int months = calendar.get(2) + 1;
            final int days = calendar.get(5);
            final int hour24 = calendar.get(11);
            final int minutes = calendar.get(12);
            final int seconds = calendar.get(13);
            final int millis = calendar.get(14);
            final StringBuilder buffer = new StringBuilder(String.valueOf(years));
            buffer.append("-");
            if (months < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(months));
            buffer.append("-");
            if (days < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(days));
            buffer.append("T");
            if (hour24 < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(hour24));
            buffer.append(":");
            if (minutes < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(minutes));
            buffer.append(":");
            if (seconds < 10) {
                buffer.append("0");
            }
            buffer.append(String.valueOf(seconds));
            if (millis > 0) {
                if (millis < 10) {
                    buffer.append(".00");
                }
                else if (millis < 100) {
                    buffer.append(".0");
                }
                else {
                    buffer.append(".");
                }
                buffer.append(String.valueOf(millis));
            }
            if (TimeZone.getTimeZone("UTC").equals(calendar.getTimeZone())) {
                buffer.append("Z");
            }
            else {
                final int gmtOffset = calendar.getTimeZone().getOffset(calendar.get(0), calendar.get(1), calendar.get(2), calendar.get(5), calendar.get(7), calendar.get(14));
                final int minutesOffset = gmtOffset / 60000;
                final int hoursOffset = minutesOffset / 60;
                final int partOfHour = minutesOffset % 60;
                buffer.append(((hoursOffset > 0) ? "+" : "") + hoursOffset + ":" + ((partOfHour < 10) ? ("0" + partOfHour) : Integer.valueOf(partOfHour)));
            }
            return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(data.getClass(), Tag.TIMESTAMP), buffer.toString(), null);
        }
    }
    
    protected class RepresentEnum implements Represent
    {
        public Node representData(final Object data) {
            final Tag tag = new Tag(data.getClass());
            return SafeRepresenter.this.representScalar(SafeRepresenter.this.getTag(data.getClass(), tag), data.toString());
        }
    }
    
    protected class RepresentByteArray implements Represent
    {
        public Node representData(final Object data) {
            final char[] binary = Base64Coder.encode((byte[])data);
            return SafeRepresenter.this.representScalar(Tag.BINARY, String.valueOf(binary), '|');
        }
    }
}
