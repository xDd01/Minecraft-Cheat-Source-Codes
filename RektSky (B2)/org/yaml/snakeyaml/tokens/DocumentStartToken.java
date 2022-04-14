package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.*;

public final class DocumentStartToken extends Token
{
    public DocumentStartToken(final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
    }
    
    @Override
    public ID getTokenId() {
        return ID.DocumentStart;
    }
}
