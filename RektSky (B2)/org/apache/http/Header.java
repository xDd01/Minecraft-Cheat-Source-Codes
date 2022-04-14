package org.apache.http;

public interface Header extends NameValuePair
{
    HeaderElement[] getElements() throws ParseException;
}
