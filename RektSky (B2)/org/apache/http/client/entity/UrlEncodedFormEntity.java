package org.apache.http.client.entity;

import java.util.*;
import org.apache.http.*;
import org.apache.http.protocol.*;
import org.apache.http.client.utils.*;
import org.apache.http.entity.*;
import java.io.*;
import java.nio.charset.*;

public class UrlEncodedFormEntity extends StringEntity
{
    public UrlEncodedFormEntity(final List<? extends NameValuePair> parameters, final String charset) throws UnsupportedEncodingException {
        super(URLEncodedUtils.format(parameters, (charset != null) ? charset : HTTP.DEF_CONTENT_CHARSET.name()), ContentType.create("application/x-www-form-urlencoded", charset));
    }
    
    public UrlEncodedFormEntity(final Iterable<? extends NameValuePair> parameters, final Charset charset) {
        super(URLEncodedUtils.format(parameters, (charset != null) ? charset : HTTP.DEF_CONTENT_CHARSET), ContentType.create("application/x-www-form-urlencoded", charset));
    }
    
    public UrlEncodedFormEntity(final List<? extends NameValuePair> parameters) throws UnsupportedEncodingException {
        this(parameters, (Charset)null);
    }
    
    public UrlEncodedFormEntity(final Iterable<? extends NameValuePair> parameters) {
        this(parameters, null);
    }
}
