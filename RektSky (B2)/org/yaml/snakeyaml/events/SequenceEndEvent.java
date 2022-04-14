package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.*;

public final class SequenceEndEvent extends CollectionEndEvent
{
    public SequenceEndEvent(final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
    }
    
    @Override
    public boolean is(final ID id) {
        return ID.SequenceEnd == id;
    }
}
