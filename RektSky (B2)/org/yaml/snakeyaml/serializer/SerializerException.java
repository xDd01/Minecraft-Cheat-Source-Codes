package org.yaml.snakeyaml.serializer;

import org.yaml.snakeyaml.error.*;

public class SerializerException extends YAMLException
{
    private static final long serialVersionUID = 2632638197498912433L;
    
    public SerializerException(final String message) {
        super(message);
    }
}
