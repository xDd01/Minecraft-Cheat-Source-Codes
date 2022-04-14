package org.apache.http.impl.cookie;

import org.apache.http.cookie.*;
import java.io.*;
import org.apache.http.util.*;
import java.util.*;

public class BasicClientCookie implements SetCookie, ClientCookie, Cloneable, Serializable
{
    private static final long serialVersionUID = -3869795591041535538L;
    private final String name;
    private Map<String, String> attribs;
    private String value;
    private String cookieComment;
    private String cookieDomain;
    private Date cookieExpiryDate;
    private String cookiePath;
    private boolean isSecure;
    private int cookieVersion;
    private Date creationDate;
    
    public BasicClientCookie(final String name, final String value) {
        Args.notNull(name, "Name");
        this.name = name;
        this.attribs = new HashMap<String, String>();
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
    public void setValue(final String value) {
        this.value = value;
    }
    
    @Override
    public String getComment() {
        return this.cookieComment;
    }
    
    @Override
    public void setComment(final String comment) {
        this.cookieComment = comment;
    }
    
    @Override
    public String getCommentURL() {
        return null;
    }
    
    @Override
    public Date getExpiryDate() {
        return this.cookieExpiryDate;
    }
    
    @Override
    public void setExpiryDate(final Date expiryDate) {
        this.cookieExpiryDate = expiryDate;
    }
    
    @Override
    public boolean isPersistent() {
        return null != this.cookieExpiryDate;
    }
    
    @Override
    public String getDomain() {
        return this.cookieDomain;
    }
    
    @Override
    public void setDomain(final String domain) {
        if (domain != null) {
            this.cookieDomain = domain.toLowerCase(Locale.ROOT);
        }
        else {
            this.cookieDomain = null;
        }
    }
    
    @Override
    public String getPath() {
        return this.cookiePath;
    }
    
    @Override
    public void setPath(final String path) {
        this.cookiePath = path;
    }
    
    @Override
    public boolean isSecure() {
        return this.isSecure;
    }
    
    @Override
    public void setSecure(final boolean secure) {
        this.isSecure = secure;
    }
    
    @Override
    public int[] getPorts() {
        return null;
    }
    
    @Override
    public int getVersion() {
        return this.cookieVersion;
    }
    
    @Override
    public void setVersion(final int version) {
        this.cookieVersion = version;
    }
    
    @Override
    public boolean isExpired(final Date date) {
        Args.notNull(date, "Date");
        return this.cookieExpiryDate != null && this.cookieExpiryDate.getTime() <= date.getTime();
    }
    
    public Date getCreationDate() {
        return this.creationDate;
    }
    
    public void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }
    
    public void setAttribute(final String name, final String value) {
        this.attribs.put(name, value);
    }
    
    @Override
    public String getAttribute(final String name) {
        return this.attribs.get(name);
    }
    
    @Override
    public boolean containsAttribute(final String name) {
        return this.attribs.containsKey(name);
    }
    
    public boolean removeAttribute(final String name) {
        return this.attribs.remove(name) != null;
    }
    
    public Object clone() throws CloneNotSupportedException {
        final BasicClientCookie clone = (BasicClientCookie)super.clone();
        clone.attribs = new HashMap<String, String>(this.attribs);
        return clone;
    }
    
    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append("[version: ");
        buffer.append(Integer.toString(this.cookieVersion));
        buffer.append("]");
        buffer.append("[name: ");
        buffer.append(this.name);
        buffer.append("]");
        buffer.append("[value: ");
        buffer.append(this.value);
        buffer.append("]");
        buffer.append("[domain: ");
        buffer.append(this.cookieDomain);
        buffer.append("]");
        buffer.append("[path: ");
        buffer.append(this.cookiePath);
        buffer.append("]");
        buffer.append("[expiry: ");
        buffer.append(this.cookieExpiryDate);
        buffer.append("]");
        return buffer.toString();
    }
}
