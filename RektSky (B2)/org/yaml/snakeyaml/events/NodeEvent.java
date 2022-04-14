package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.*;

public abstract class NodeEvent extends Event
{
    private final String anchor;
    
    public NodeEvent(final String anchor, final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
        this.anchor = anchor;
    }
    
    public String getAnchor() {
        return this.anchor;
    }
    
    @Override
    protected String getArguments() {
        return "anchor=" + this.anchor;
    }
}
