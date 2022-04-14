package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.*;

public abstract class CollectionEndEvent extends Event
{
    public CollectionEndEvent(final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
    }
}
