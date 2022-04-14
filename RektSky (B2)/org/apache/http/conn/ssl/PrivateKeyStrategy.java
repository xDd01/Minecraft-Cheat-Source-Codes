package org.apache.http.conn.ssl;

import java.util.*;
import java.net.*;

@Deprecated
public interface PrivateKeyStrategy
{
    String chooseAlias(final Map<String, PrivateKeyDetails> p0, final Socket p1);
}
