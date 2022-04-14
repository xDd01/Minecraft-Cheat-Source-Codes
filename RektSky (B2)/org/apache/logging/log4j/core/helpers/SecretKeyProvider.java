package org.apache.logging.log4j.core.helpers;

import javax.crypto.*;

public interface SecretKeyProvider
{
    SecretKey getSecretKey();
}
