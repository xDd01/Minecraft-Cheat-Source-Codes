package org.apache.http.ssl;

import java.util.*;
import java.net.*;

public interface PrivateKeyStrategy
{
    String chooseAlias(final Map<String, PrivateKeyDetails> p0, final Socket p1);
}
