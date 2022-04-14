package org.yaml.snakeyaml.scanner;

import org.yaml.snakeyaml.tokens.*;

public interface Scanner
{
    boolean checkToken(final Token.ID... p0);
    
    Token peekToken();
    
    Token getToken();
}
