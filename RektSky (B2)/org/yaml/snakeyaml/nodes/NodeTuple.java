package org.yaml.snakeyaml.nodes;

public final class NodeTuple
{
    private final Node keyNode;
    private final Node valueNode;
    
    public NodeTuple(final Node keyNode, final Node valueNode) {
        if (keyNode == null || valueNode == null) {
            throw new NullPointerException("Nodes must be provided.");
        }
        this.keyNode = keyNode;
        this.valueNode = valueNode;
    }
    
    public final Node getKeyNode() {
        return this.keyNode;
    }
    
    public final Node getValueNode() {
        return this.valueNode;
    }
    
    @Override
    public String toString() {
        return "<NodeTuple keyNode=" + this.keyNode.toString() + "; valueNode=" + this.valueNode.toString() + ">";
    }
}
