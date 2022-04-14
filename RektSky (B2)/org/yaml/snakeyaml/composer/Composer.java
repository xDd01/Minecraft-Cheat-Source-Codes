package org.yaml.snakeyaml.composer;

import org.yaml.snakeyaml.parser.*;
import org.yaml.snakeyaml.resolver.*;
import org.yaml.snakeyaml.error.*;
import java.util.*;
import org.yaml.snakeyaml.events.*;
import org.yaml.snakeyaml.nodes.*;

public class Composer
{
    private final Parser parser;
    private final Resolver resolver;
    private final Map<String, Node> anchors;
    private final Set<Node> recursiveNodes;
    
    public Composer(final Parser parser, final Resolver resolver) {
        this.parser = parser;
        this.resolver = resolver;
        this.anchors = new HashMap<String, Node>();
        this.recursiveNodes = new HashSet<Node>();
    }
    
    public boolean checkNode() {
        if (this.parser.checkEvent(Event.ID.StreamStart)) {
            this.parser.getEvent();
        }
        return !this.parser.checkEvent(Event.ID.StreamEnd);
    }
    
    public Node getNode() {
        if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
            return this.composeDocument();
        }
        return null;
    }
    
    public Node getSingleNode() {
        this.parser.getEvent();
        Node document = null;
        if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
            document = this.composeDocument();
        }
        if (!this.parser.checkEvent(Event.ID.StreamEnd)) {
            final Event event = this.parser.getEvent();
            throw new ComposerException("expected a single document in the stream", document.getStartMark(), "but found another document", event.getStartMark());
        }
        this.parser.getEvent();
        return document;
    }
    
    private Node composeDocument() {
        this.parser.getEvent();
        final Node node = this.composeNode(null, null);
        this.parser.getEvent();
        this.anchors.clear();
        this.recursiveNodes.clear();
        return node;
    }
    
    private Node composeNode(final Node parent, final Object index) {
        this.recursiveNodes.add(parent);
        if (this.parser.checkEvent(Event.ID.Alias)) {
            final AliasEvent event = (AliasEvent)this.parser.getEvent();
            final String anchor = event.getAnchor();
            if (!this.anchors.containsKey(anchor)) {
                throw new ComposerException(null, null, "found undefined alias " + anchor, event.getStartMark());
            }
            final Node result = this.anchors.get(anchor);
            if (this.recursiveNodes.remove(result)) {
                result.setTwoStepsConstruction(true);
            }
            return result;
        }
        else {
            final NodeEvent event2 = (NodeEvent)this.parser.peekEvent();
            String anchor = null;
            anchor = event2.getAnchor();
            if (anchor != null && this.anchors.containsKey(anchor)) {
                throw new ComposerException("found duplicate anchor " + anchor + "; first occurence", this.anchors.get(anchor).getStartMark(), "second occurence", event2.getStartMark());
            }
            Node node = null;
            if (this.parser.checkEvent(Event.ID.Scalar)) {
                node = this.composeScalarNode(anchor);
            }
            else if (this.parser.checkEvent(Event.ID.SequenceStart)) {
                node = this.composeSequenceNode(anchor);
            }
            else {
                node = this.composeMappingNode(anchor);
            }
            this.recursiveNodes.remove(parent);
            return node;
        }
    }
    
    private Node composeScalarNode(final String anchor) {
        final ScalarEvent ev = (ScalarEvent)this.parser.getEvent();
        final String tag = ev.getTag();
        boolean resolved = false;
        Tag nodeTag;
        if (tag == null || tag.equals("!")) {
            nodeTag = this.resolver.resolve(NodeId.scalar, ev.getValue(), ev.getImplicit().isFirst());
            resolved = true;
        }
        else {
            nodeTag = new Tag(tag);
        }
        final Node node = new ScalarNode(nodeTag, resolved, ev.getValue(), ev.getStartMark(), ev.getEndMark(), ev.getStyle());
        if (anchor != null) {
            this.anchors.put(anchor, node);
        }
        return node;
    }
    
    private Node composeSequenceNode(final String anchor) {
        final SequenceStartEvent startEvent = (SequenceStartEvent)this.parser.getEvent();
        final String tag = startEvent.getTag();
        boolean resolved = false;
        Tag nodeTag;
        if (tag == null || tag.equals("!")) {
            nodeTag = this.resolver.resolve(NodeId.sequence, null, startEvent.getImplicit());
            resolved = true;
        }
        else {
            nodeTag = new Tag(tag);
        }
        final ArrayList<Node> children = new ArrayList<Node>();
        final SequenceNode node = new SequenceNode(nodeTag, resolved, children, startEvent.getStartMark(), null, startEvent.getFlowStyle());
        if (anchor != null) {
            this.anchors.put(anchor, node);
        }
        int index = 0;
        while (!this.parser.checkEvent(Event.ID.SequenceEnd)) {
            children.add(this.composeNode(node, index));
            ++index;
        }
        final Event endEvent = this.parser.getEvent();
        node.setEndMark(endEvent.getEndMark());
        return node;
    }
    
    private Node composeMappingNode(final String anchor) {
        final MappingStartEvent startEvent = (MappingStartEvent)this.parser.getEvent();
        final String tag = startEvent.getTag();
        boolean resolved = false;
        Tag nodeTag;
        if (tag == null || tag.equals("!")) {
            nodeTag = this.resolver.resolve(NodeId.mapping, null, startEvent.getImplicit());
            resolved = true;
        }
        else {
            nodeTag = new Tag(tag);
        }
        final List<NodeTuple> children = new ArrayList<NodeTuple>();
        final MappingNode node = new MappingNode(nodeTag, resolved, children, startEvent.getStartMark(), null, startEvent.getFlowStyle());
        if (anchor != null) {
            this.anchors.put(anchor, node);
        }
        while (!this.parser.checkEvent(Event.ID.MappingEnd)) {
            final Node itemKey = this.composeNode(node, null);
            if (itemKey.getTag().equals(Tag.MERGE)) {
                node.setMerged(true);
            }
            else if (itemKey.getTag().equals(Tag.VALUE)) {
                itemKey.setTag(Tag.STR);
            }
            final Node itemValue = this.composeNode(node, itemKey);
            children.add(new NodeTuple(itemKey, itemValue));
        }
        final Event endEvent = this.parser.getEvent();
        node.setEndMark(endEvent.getEndMark());
        return node;
    }
}
