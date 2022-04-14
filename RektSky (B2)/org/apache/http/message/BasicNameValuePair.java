package org.apache.http.message;

import org.apache.http.*;
import java.io.*;
import org.apache.http.annotation.*;
import org.apache.http.util.*;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class BasicNameValuePair implements NameValuePair, Cloneable, Serializable
{
    private static final long serialVersionUID = -6437800749411518984L;
    private final String name;
    private final String value;
    
    public BasicNameValuePair(final String name, final String value) {
        this.name = Args.notNull(name, "Name");
        this.value = value;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        if (this.value == null) {
            return this.name;
        }
        final int len = this.name.length() + 1 + this.value.length();
        final StringBuilder buffer = new StringBuilder(len);
        buffer.append(this.name);
        buffer.append("=");
        buffer.append(this.value);
        return buffer.toString();
    }
    
    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object instanceof NameValuePair) {
            final BasicNameValuePair that = (BasicNameValuePair)object;
            return this.name.equals(that.name) && LangUtils.equals(this.value, that.value);
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.name);
        hash = LangUtils.hashCode(hash, this.value);
        return hash;
    }
    
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
