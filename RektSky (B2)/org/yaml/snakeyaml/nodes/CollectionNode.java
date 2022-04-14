package org.yaml.snakeyaml.nodes;

import org.yaml.snakeyaml.error.*;

public abstract class CollectionNode extends Node
{
    private Boolean flowStyle;
    
    public CollectionNode(final Tag tag, final Mark startMark, final Mark endMark, final Boolean flowStyle) {
        super(tag, startMark, endMark);
        this.flowStyle = flowStyle;
    }
    
    public Boolean getFlowStyle() {
        return this.flowStyle;
    }
    
    public void setFlowStyle(final Boolean flowStyle) {
        this.flowStyle = flowStyle;
    }
    
    public void setEndMark(final Mark endMark) {
        this.endMark = endMark;
    }
}
