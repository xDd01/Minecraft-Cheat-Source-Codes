package org.apache.commons.compress;

import java.io.*;

public class PasswordRequiredException extends IOException
{
    private static final long serialVersionUID = 1391070005491684483L;
    
    public PasswordRequiredException(final String name) {
        super("Cannot read encrypted content from " + name + " without a password.");
    }
}
