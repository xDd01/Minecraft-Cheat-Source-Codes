package org.apache.http.impl.auth;

import java.io.*;

@Deprecated
public interface SpnegoTokenGenerator
{
    byte[] generateSpnegoDERObject(final byte[] p0) throws IOException;
}
