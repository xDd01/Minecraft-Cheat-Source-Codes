package org.apache.logging.log4j.core.appender.db.nosql.mongo;

import org.apache.logging.log4j.core.appender.db.nosql.*;
import com.mongodb.*;
import java.util.*;

public final class MongoDBObject implements NoSQLObject<BasicDBObject>
{
    private final BasicDBObject mongoObject;
    
    public MongoDBObject() {
        this.mongoObject = new BasicDBObject();
    }
    
    @Override
    public void set(final String field, final Object value) {
        this.mongoObject.append(field, value);
    }
    
    @Override
    public void set(final String field, final NoSQLObject<BasicDBObject> value) {
        this.mongoObject.append(field, (Object)value.unwrap());
    }
    
    @Override
    public void set(final String field, final Object[] values) {
        final BasicDBList list = new BasicDBList();
        Collections.addAll((Collection<? super Object>)list, values);
        this.mongoObject.append(field, (Object)list);
    }
    
    @Override
    public void set(final String field, final NoSQLObject<BasicDBObject>[] values) {
        final BasicDBList list = new BasicDBList();
        for (final NoSQLObject<BasicDBObject> value : values) {
            list.add((Object)value.unwrap());
        }
        this.mongoObject.append(field, (Object)list);
    }
    
    @Override
    public BasicDBObject unwrap() {
        return this.mongoObject;
    }
}
