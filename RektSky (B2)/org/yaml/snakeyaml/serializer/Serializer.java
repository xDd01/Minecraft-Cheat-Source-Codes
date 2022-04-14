package org.yaml.snakeyaml.serializer;

import org.yaml.snakeyaml.emitter.*;
import org.yaml.snakeyaml.resolver.*;
import org.yaml.snakeyaml.*;
import org.yaml.snakeyaml.error.*;
import java.io.*;
import java.util.*;
import java.text.*;
import org.yaml.snakeyaml.nodes.*;
import org.yaml.snakeyaml.events.*;

public final class Serializer
{
    private final Emitter emitter;
    private final Resolver resolver;
    private boolean explicitStart;
    private boolean explicitEnd;
    private Integer[] useVersion;
    private Map<String, String> useTags;
    private Set<Node> serializedNodes;
    private Map<Node, String> anchors;
    private int lastAnchorId;
    private Boolean closed;
    private Tag explicitRoot;
    
    public Serializer(final Emitter emitter, final Resolver resolver, final DumperOptions opts) {
        this.emitter = emitter;
        this.resolver = resolver;
        this.explicitStart = opts.isExplicitStart();
        this.explicitEnd = opts.isExplicitEnd();
        if (opts.getVersion() != null) {
            this.useVersion = opts.getVersion().getArray();
        }
        this.useTags = opts.getTags();
        this.serializedNodes = new HashSet<Node>();
        this.anchors = new HashMap<Node, String>();
        this.lastAnchorId = 0;
        this.closed = null;
        this.explicitRoot = opts.getExplicitRoot();
    }
    
    public void open() throws IOException {
        if (this.closed == null) {
            this.emitter.emit(new StreamStartEvent(null, null));
            this.closed = Boolean.FALSE;
            return;
        }
        if (Boolean.TRUE.equals(this.closed)) {
            throw new SerializerException("serializer is closed");
        }
        throw new SerializerException("serializer is already opened");
    }
    
    public void close() throws IOException {
        if (this.closed == null) {
            throw new SerializerException("serializer is not opened");
        }
        if (!Boolean.TRUE.equals(this.closed)) {
            this.emitter.emit(new StreamEndEvent(null, null));
            this.closed = Boolean.TRUE;
        }
    }
    
    public void serialize(final Node node) throws IOException {
        if (this.closed == null) {
            throw new SerializerException("serializer is not opened");
        }
        if (this.closed) {
            throw new SerializerException("serializer is closed");
        }
        this.emitter.emit(new DocumentStartEvent(null, null, this.explicitStart, this.useVersion, this.useTags));
        this.anchorNode(node);
        if (this.explicitRoot != null) {
            node.setTag(this.explicitRoot);
        }
        this.serializeNode(node, null, null);
        this.emitter.emit(new DocumentEndEvent(null, null, this.explicitEnd));
        this.serializedNodes.clear();
        this.anchors.clear();
        this.lastAnchorId = 0;
    }
    
    private void anchorNode(final Node node) {
        if (this.anchors.containsKey(node)) {
            String anchor = this.anchors.get(node);
            if (null == anchor) {
                anchor = this.generateAnchor();
                this.anchors.put(node, anchor);
            }
        }
        else {
            this.anchors.put(node, null);
            switch (node.getNodeId()) {
                case sequence: {
                    final SequenceNode seqNode = (SequenceNode)node;
                    final List<Node> list = seqNode.getValue();
                    for (final Node item : list) {
                        this.anchorNode(item);
                    }
                    break;
                }
                case mapping: {
                    final MappingNode mnode = (MappingNode)node;
                    final List<NodeTuple> map = mnode.getValue();
                    for (final NodeTuple object : map) {
                        final Node key = object.getKeyNode();
                        final Node value = object.getValueNode();
                        this.anchorNode(key);
                        this.anchorNode(value);
                    }
                    break;
                }
            }
        }
    }
    
    private String generateAnchor() {
        ++this.lastAnchorId;
        final NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumIntegerDigits(3);
        format.setGroupingUsed(false);
        final String anchorId = format.format(this.lastAnchorId);
        return "id" + anchorId;
    }
    
    private void serializeNode(final Node node, final Node parent, final Object index) throws IOException {
        final String tAlias = this.anchors.get(node);
        if (this.serializedNodes.contains(node)) {
            this.emitter.emit(new AliasEvent(tAlias, null, null));
        }
        else {
            this.serializedNodes.add(node);
            switch (node.getNodeId()) {
                case scalar: {
                    final ScalarNode scalarNode = (ScalarNode)node;
                    final Tag detectedTag = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(), true);
                    final Tag defaultTag = this.resolver.resolve(NodeId.scalar, scalarNode.getValue(), false);
                    final ImplicitTuple tuple = new ImplicitTuple(node.getTag().equals(detectedTag), node.getTag().equals(defaultTag));
                    final ScalarEvent event = new ScalarEvent(tAlias, node.getTag().getValue(), tuple, scalarNode.getValue(), null, null, scalarNode.getStyle());
                    this.emitter.emit(event);
                    break;
                }
                case sequence: {
                    final SequenceNode seqNode = (SequenceNode)node;
                    final boolean implicitS = node.getTag().equals(this.resolver.resolve(NodeId.sequence, null, true));
                    this.emitter.emit(new SequenceStartEvent(tAlias, node.getTag().getValue(), implicitS, null, null, seqNode.getFlowStyle()));
                    int indexCounter = 0;
                    final List<Node> list = seqNode.getValue();
                    for (final Node item : list) {
                        this.serializeNode(item, node, indexCounter);
                        ++indexCounter;
                    }
                    this.emitter.emit(new SequenceEndEvent(null, null));
                    break;
                }
                default: {
                    final Tag implicitTag = this.resolver.resolve(NodeId.mapping, null, true);
                    final boolean implicitM = node.getTag().equals(implicitTag);
                    this.emitter.emit(new MappingStartEvent(tAlias, node.getTag().getValue(), implicitM, null, null, ((CollectionNode)node).getFlowStyle()));
                    final MappingNode mnode = (MappingNode)node;
                    final List<NodeTuple> map = mnode.getValue();
                    for (final NodeTuple row : map) {
                        final Node key = row.getKeyNode();
                        final Node value = row.getValueNode();
                        this.serializeNode(key, mnode, null);
                        this.serializeNode(value, mnode, key);
                    }
                    this.emitter.emit(new MappingEndEvent(null, null));
                    break;
                }
            }
        }
    }
}
