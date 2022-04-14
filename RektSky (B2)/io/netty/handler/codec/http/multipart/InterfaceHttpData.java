package io.netty.handler.codec.http.multipart;

import io.netty.util.*;

public interface InterfaceHttpData extends Comparable<InterfaceHttpData>, ReferenceCounted
{
    String getName();
    
    HttpDataType getHttpDataType();
    
    public enum HttpDataType
    {
        Attribute, 
        FileUpload, 
        InternalAttribute;
    }
}
