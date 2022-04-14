package org.apache.http.conn.ssl;

import org.apache.http.util.*;

final class SubjectName
{
    static final int DNS = 2;
    static final int IP = 7;
    private final String value;
    private final int type;
    
    static SubjectName IP(final String value) {
        return new SubjectName(value, 7);
    }
    
    static SubjectName DNS(final String value) {
        return new SubjectName(value, 2);
    }
    
    SubjectName(final String value, final int type) {
        this.value = Args.notNull(value, "Value");
        this.type = Args.positive(type, "Type");
    }
    
    public int getType() {
        return this.type;
    }
    
    public String getValue() {
        return this.value;
    }
    
    @Override
    public String toString() {
        return this.value;
    }
}
