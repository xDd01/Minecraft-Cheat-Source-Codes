package org.apache.http.client.entity;

import java.io.*;

public interface InputStreamFactory
{
    InputStream create(final InputStream p0) throws IOException;
}
