package org.yaml.snakeyaml.parser;

import org.yaml.snakeyaml.util.*;
import org.yaml.snakeyaml.reader.*;
import org.yaml.snakeyaml.scanner.*;
import java.util.*;
import org.yaml.snakeyaml.error.*;
import org.yaml.snakeyaml.tokens.*;
import org.yaml.snakeyaml.events.*;

public final class ParserImpl implements Parser
{
    private static final Map<String, String> DEFAULT_TAGS;
    private final Scanner scanner;
    private Event currentEvent;
    private List<Integer> yamlVersion;
    private Map<String, String> tagHandles;
    private final ArrayStack<Production> states;
    private final ArrayStack<Mark> marks;
    private Production state;
    
    public ParserImpl(final StreamReader reader) {
        this.scanner = new ScannerImpl(reader);
        this.currentEvent = null;
        this.yamlVersion = null;
        this.tagHandles = new HashMap<String, String>();
        this.states = new ArrayStack<Production>(100);
        this.marks = new ArrayStack<Mark>(10);
        this.state = new ParseStreamStart();
    }
    
    public boolean checkEvent(final Event.ID choices) {
        this.peekEvent();
        return this.currentEvent != null && this.currentEvent.is(choices);
    }
    
    public Event peekEvent() {
        if (this.currentEvent == null && this.state != null) {
            this.currentEvent = this.state.produce();
        }
        return this.currentEvent;
    }
    
    public Event getEvent() {
        this.peekEvent();
        final Event value = this.currentEvent;
        this.currentEvent = null;
        return value;
    }
    
    private List<Object> processDirectives() {
        this.yamlVersion = null;
        this.tagHandles = new HashMap<String, String>();
        while (this.scanner.checkToken(Token.ID.Directive)) {
            final DirectiveToken token = (DirectiveToken)this.scanner.getToken();
            if (token.getName().equals("YAML")) {
                if (this.yamlVersion != null) {
                    throw new ParserException(null, null, "found duplicate YAML directive", token.getStartMark());
                }
                final List<Integer> value = token.getValue();
                final Integer major = value.get(0);
                if (major != 1) {
                    throw new ParserException(null, null, "found incompatible YAML document (version 1.* is required)", token.getStartMark());
                }
                this.yamlVersion = token.getValue();
            }
            else {
                if (!token.getName().equals("TAG")) {
                    continue;
                }
                final List<String> value2 = token.getValue();
                final String handle = value2.get(0);
                final String prefix = value2.get(1);
                if (this.tagHandles.containsKey(handle)) {
                    throw new ParserException(null, null, "duplicate tag handle " + handle, token.getStartMark());
                }
                this.tagHandles.put(handle, prefix);
            }
        }
        final List<Object> value3 = new ArrayList<Object>(2);
        value3.add(this.yamlVersion);
        if (!this.tagHandles.isEmpty()) {
            value3.add(new HashMap(this.tagHandles));
        }
        else {
            value3.add(new HashMap());
        }
        for (final String key : ParserImpl.DEFAULT_TAGS.keySet()) {
            if (!this.tagHandles.containsKey(key)) {
                this.tagHandles.put(key, ParserImpl.DEFAULT_TAGS.get(key));
            }
        }
        return value3;
    }
    
    private Event parseFlowNode() {
        return this.parseNode(false, false);
    }
    
    private Event parseBlockNodeOrIndentlessSequence() {
        return this.parseNode(true, true);
    }
    
    private Event parseNode(final boolean block, final boolean indentlessSequence) {
        Mark startMark = null;
        Mark endMark = null;
        Mark tagMark = null;
        Event event;
        if (this.scanner.checkToken(Token.ID.Alias)) {
            final AliasToken token = (AliasToken)this.scanner.getToken();
            event = new AliasEvent(token.getValue(), token.getStartMark(), token.getEndMark());
            this.state = this.states.pop();
        }
        else {
            String anchor = null;
            TagTuple tagTokenTag = null;
            if (this.scanner.checkToken(Token.ID.Anchor)) {
                final AnchorToken token2 = (AnchorToken)this.scanner.getToken();
                startMark = token2.getStartMark();
                endMark = token2.getEndMark();
                anchor = token2.getValue();
                if (this.scanner.checkToken(Token.ID.Tag)) {
                    final TagToken tagToken = (TagToken)this.scanner.getToken();
                    tagMark = tagToken.getStartMark();
                    endMark = tagToken.getEndMark();
                    tagTokenTag = tagToken.getValue();
                }
            }
            else if (this.scanner.checkToken(Token.ID.Tag)) {
                final TagToken tagToken2 = (TagToken)this.scanner.getToken();
                startMark = (tagMark = tagToken2.getStartMark());
                endMark = tagToken2.getEndMark();
                tagTokenTag = tagToken2.getValue();
                if (this.scanner.checkToken(Token.ID.Anchor)) {
                    final AnchorToken token3 = (AnchorToken)this.scanner.getToken();
                    endMark = token3.getEndMark();
                    anchor = token3.getValue();
                }
            }
            String tag = null;
            if (tagTokenTag != null) {
                final String handle = tagTokenTag.getHandle();
                final String suffix = tagTokenTag.getSuffix();
                if (handle != null) {
                    if (!this.tagHandles.containsKey(handle)) {
                        throw new ParserException("while parsing a node", startMark, "found undefined tag handle " + handle, tagMark);
                    }
                    tag = this.tagHandles.get(handle) + suffix;
                }
                else {
                    tag = suffix;
                }
            }
            if (startMark == null) {
                startMark = (endMark = this.scanner.peekToken().getStartMark());
            }
            event = null;
            final boolean implicit = tag == null || tag.equals("!");
            if (indentlessSequence && this.scanner.checkToken(Token.ID.BlockEntry)) {
                endMark = this.scanner.peekToken().getEndMark();
                event = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, Boolean.FALSE);
                this.state = new ParseIndentlessSequenceEntry();
            }
            else if (this.scanner.checkToken(Token.ID.Scalar)) {
                final ScalarToken token4 = (ScalarToken)this.scanner.getToken();
                endMark = token4.getEndMark();
                ImplicitTuple implicitValues;
                if ((token4.getPlain() && tag == null) || "!".equals(tag)) {
                    implicitValues = new ImplicitTuple(true, false);
                }
                else if (tag == null) {
                    implicitValues = new ImplicitTuple(false, true);
                }
                else {
                    implicitValues = new ImplicitTuple(false, false);
                }
                event = new ScalarEvent(anchor, tag, implicitValues, token4.getValue(), startMark, endMark, token4.getStyle());
                this.state = this.states.pop();
            }
            else if (this.scanner.checkToken(Token.ID.FlowSequenceStart)) {
                endMark = this.scanner.peekToken().getEndMark();
                event = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, Boolean.TRUE);
                this.state = new ParseFlowSequenceFirstEntry();
            }
            else if (this.scanner.checkToken(Token.ID.FlowMappingStart)) {
                endMark = this.scanner.peekToken().getEndMark();
                event = new MappingStartEvent(anchor, tag, implicit, startMark, endMark, Boolean.TRUE);
                this.state = new ParseFlowMappingFirstKey();
            }
            else if (block && this.scanner.checkToken(Token.ID.BlockSequenceStart)) {
                endMark = this.scanner.peekToken().getStartMark();
                event = new SequenceStartEvent(anchor, tag, implicit, startMark, endMark, Boolean.FALSE);
                this.state = new ParseBlockSequenceFirstEntry();
            }
            else if (block && this.scanner.checkToken(Token.ID.BlockMappingStart)) {
                endMark = this.scanner.peekToken().getStartMark();
                event = new MappingStartEvent(anchor, tag, implicit, startMark, endMark, Boolean.FALSE);
                this.state = new ParseBlockMappingFirstKey();
            }
            else {
                if (anchor == null && tag == null) {
                    String node;
                    if (block) {
                        node = "block";
                    }
                    else {
                        node = "flow";
                    }
                    final Token token5 = this.scanner.peekToken();
                    throw new ParserException("while parsing a " + node + " node", startMark, "expected the node content, but found " + token5.getTokenId(), token5.getStartMark());
                }
                event = new ScalarEvent(anchor, tag, new ImplicitTuple(implicit, false), "", startMark, endMark, '\0');
                this.state = this.states.pop();
            }
        }
        return event;
    }
    
    private Event processEmptyScalar(final Mark mark) {
        return new ScalarEvent(null, null, new ImplicitTuple(true, false), "", mark, mark, '\0');
    }
    
    static {
        (DEFAULT_TAGS = new HashMap<String, String>()).put("!", "!");
        ParserImpl.DEFAULT_TAGS.put("!!", "tag:yaml.org,2002:");
    }
    
    private class ParseStreamStart implements Production
    {
        public Event produce() {
            final StreamStartToken token = (StreamStartToken)ParserImpl.this.scanner.getToken();
            final Event event = new StreamStartEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = new ParseImplicitDocumentStart();
            return event;
        }
    }
    
    private class ParseImplicitDocumentStart implements Production
    {
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.StreamEnd)) {
                ParserImpl.this.tagHandles = ParserImpl.DEFAULT_TAGS;
                final Token token = ParserImpl.this.scanner.peekToken();
                final Mark endMark;
                final Mark startMark = endMark = token.getStartMark();
                final Event event = new DocumentStartEvent(startMark, endMark, false, null, null);
                ParserImpl.this.states.push(new ParseDocumentEnd());
                ParserImpl.this.state = new ParseBlockNode();
                return event;
            }
            final Production p = new ParseDocumentStart();
            return p.produce();
        }
    }
    
    private class ParseDocumentStart implements Production
    {
        public Event produce() {
            while (ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
                ParserImpl.this.scanner.getToken();
            }
            Event event;
            if (!ParserImpl.this.scanner.checkToken(Token.ID.StreamEnd)) {
                Token token = ParserImpl.this.scanner.peekToken();
                final Mark startMark = token.getStartMark();
                final List<Object> version_tags = ParserImpl.this.processDirectives();
                final List<Object> version = version_tags.get(0);
                final Map<String, String> tags = version_tags.get(1);
                if (!ParserImpl.this.scanner.checkToken(Token.ID.DocumentStart)) {
                    throw new ParserException(null, null, "expected '<document start>', but found " + ParserImpl.this.scanner.peekToken().getTokenId(), ParserImpl.this.scanner.peekToken().getStartMark());
                }
                token = ParserImpl.this.scanner.getToken();
                final Mark endMark = token.getEndMark();
                Integer[] versionInteger;
                if (version != null) {
                    versionInteger = new Integer[2];
                    versionInteger = version.toArray(versionInteger);
                }
                else {
                    versionInteger = null;
                }
                event = new DocumentStartEvent(startMark, endMark, true, versionInteger, tags);
                ParserImpl.this.states.push(new ParseDocumentEnd());
                ParserImpl.this.state = new ParseDocumentContent();
            }
            else {
                final StreamEndToken token2 = (StreamEndToken)ParserImpl.this.scanner.getToken();
                event = new StreamEndEvent(token2.getStartMark(), token2.getEndMark());
                if (!ParserImpl.this.states.isEmpty()) {
                    throw new YAMLException("Unexpected end of stream. States left: " + ParserImpl.this.states);
                }
                if (!ParserImpl.this.marks.isEmpty()) {
                    throw new YAMLException("Unexpected end of stream. Marks left: " + ParserImpl.this.marks);
                }
                ParserImpl.this.state = null;
            }
            return event;
        }
    }
    
    private class ParseDocumentEnd implements Production
    {
        public Event produce() {
            Token token = ParserImpl.this.scanner.peekToken();
            Mark endMark;
            final Mark startMark = endMark = token.getStartMark();
            boolean explicit = false;
            if (ParserImpl.this.scanner.checkToken(Token.ID.DocumentEnd)) {
                token = ParserImpl.this.scanner.getToken();
                endMark = token.getEndMark();
                explicit = true;
            }
            final Event event = new DocumentEndEvent(startMark, endMark, explicit);
            ParserImpl.this.state = new ParseDocumentStart();
            return event;
        }
    }
    
    private class ParseDocumentContent implements Production
    {
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.Directive, Token.ID.DocumentStart, Token.ID.DocumentEnd, Token.ID.StreamEnd)) {
                final Event event = ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
                ParserImpl.this.state = ParserImpl.this.states.pop();
                return event;
            }
            final Production p = new ParseBlockNode();
            return p.produce();
        }
    }
    
    private class ParseBlockNode implements Production
    {
        public Event produce() {
            return ParserImpl.this.parseNode(true, false);
        }
    }
    
    private class ParseBlockSequenceFirstEntry implements Production
    {
        public Event produce() {
            final Token token = ParserImpl.this.scanner.getToken();
            ParserImpl.this.marks.push(token.getStartMark());
            return new ParseBlockSequenceEntry().produce();
        }
    }
    
    private class ParseBlockSequenceEntry implements Production
    {
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
                final BlockEntryToken token = (BlockEntryToken)ParserImpl.this.scanner.getToken();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.BlockEnd)) {
                    ParserImpl.this.states.push(new ParseBlockSequenceEntry());
                    return new ParseBlockNode().produce();
                }
                ParserImpl.this.state = new ParseBlockSequenceEntry();
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
            else {
                if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
                    final Token token2 = ParserImpl.this.scanner.peekToken();
                    throw new ParserException("while parsing a block collection", ParserImpl.this.marks.pop(), "expected <block end>, but found " + token2.getTokenId(), token2.getStartMark());
                }
                final Token token2 = ParserImpl.this.scanner.getToken();
                final Event event = new SequenceEndEvent(token2.getStartMark(), token2.getEndMark());
                ParserImpl.this.state = ParserImpl.this.states.pop();
                ParserImpl.this.marks.pop();
                return event;
            }
        }
    }
    
    private class ParseIndentlessSequenceEntry implements Production
    {
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry)) {
                final Token token = ParserImpl.this.scanner.peekToken();
                final Event event = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
                ParserImpl.this.state = ParserImpl.this.states.pop();
                return event;
            }
            final Token token = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEntry, Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
                ParserImpl.this.states.push(new ParseIndentlessSequenceEntry());
                return new ParseBlockNode().produce();
            }
            ParserImpl.this.state = new ParseIndentlessSequenceEntry();
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
        }
    }
    
    private class ParseBlockMappingFirstKey implements Production
    {
        public Event produce() {
            final Token token = ParserImpl.this.scanner.getToken();
            ParserImpl.this.marks.push(token.getStartMark());
            return new ParseBlockMappingKey().produce();
        }
    }
    
    private class ParseBlockMappingKey implements Production
    {
        public Event produce() {
            if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
                final Token token = ParserImpl.this.scanner.getToken();
                if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
                    ParserImpl.this.states.push(new ParseBlockMappingValue());
                    return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
                }
                ParserImpl.this.state = new ParseBlockMappingValue();
                return ParserImpl.this.processEmptyScalar(token.getEndMark());
            }
            else {
                if (!ParserImpl.this.scanner.checkToken(Token.ID.BlockEnd)) {
                    final Token token = ParserImpl.this.scanner.peekToken();
                    throw new ParserException("while parsing a block mapping", ParserImpl.this.marks.pop(), "expected <block end>, but found " + token.getTokenId(), token.getStartMark());
                }
                final Token token = ParserImpl.this.scanner.getToken();
                final Event event = new MappingEndEvent(token.getStartMark(), token.getEndMark());
                ParserImpl.this.state = ParserImpl.this.states.pop();
                ParserImpl.this.marks.pop();
                return event;
            }
        }
    }
    
    private class ParseBlockMappingValue implements Production
    {
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
                ParserImpl.this.state = new ParseBlockMappingKey();
                final Token token = ParserImpl.this.scanner.peekToken();
                return ParserImpl.this.processEmptyScalar(token.getStartMark());
            }
            final Token token = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Key, Token.ID.Value, Token.ID.BlockEnd)) {
                ParserImpl.this.states.push(new ParseBlockMappingKey());
                return ParserImpl.this.parseBlockNodeOrIndentlessSequence();
            }
            ParserImpl.this.state = new ParseBlockMappingKey();
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
        }
    }
    
    private class ParseFlowSequenceFirstEntry implements Production
    {
        public Event produce() {
            final Token token = ParserImpl.this.scanner.getToken();
            ParserImpl.this.marks.push(token.getStartMark());
            return new ParseFlowSequenceEntry(true).produce();
        }
    }
    
    private class ParseFlowSequenceEntry implements Production
    {
        private boolean first;
        
        public ParseFlowSequenceEntry(final boolean first) {
            this.first = false;
            this.first = first;
        }
        
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
                if (!this.first) {
                    if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                        final Token token = ParserImpl.this.scanner.peekToken();
                        throw new ParserException("while parsing a flow sequence", ParserImpl.this.marks.pop(), "expected ',' or ']', but got " + token.getTokenId(), token.getStartMark());
                    }
                    ParserImpl.this.scanner.getToken();
                }
                if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
                    final Token token = ParserImpl.this.scanner.peekToken();
                    final Event event = new MappingStartEvent(null, null, true, token.getStartMark(), token.getEndMark(), Boolean.TRUE);
                    ParserImpl.this.state = new ParseFlowSequenceEntryMappingKey();
                    return event;
                }
                if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowSequenceEnd)) {
                    ParserImpl.this.states.push(new ParseFlowSequenceEntry(false));
                    return ParserImpl.this.parseFlowNode();
                }
            }
            final Token token = ParserImpl.this.scanner.getToken();
            final Event event = new SequenceEndEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = ParserImpl.this.states.pop();
            ParserImpl.this.marks.pop();
            return event;
        }
    }
    
    private class ParseFlowSequenceEntryMappingKey implements Production
    {
        public Event produce() {
            final Token token = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
                ParserImpl.this.states.push(new ParseFlowSequenceEntryMappingValue());
                return ParserImpl.this.parseFlowNode();
            }
            ParserImpl.this.state = new ParseFlowSequenceEntryMappingValue();
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
        }
    }
    
    private class ParseFlowSequenceEntryMappingValue implements Production
    {
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
                ParserImpl.this.state = new ParseFlowSequenceEntryMappingEnd();
                final Token token = ParserImpl.this.scanner.peekToken();
                return ParserImpl.this.processEmptyScalar(token.getStartMark());
            }
            final Token token = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowSequenceEnd)) {
                ParserImpl.this.states.push(new ParseFlowSequenceEntryMappingEnd());
                return ParserImpl.this.parseFlowNode();
            }
            ParserImpl.this.state = new ParseFlowSequenceEntryMappingEnd();
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
        }
    }
    
    private class ParseFlowSequenceEntryMappingEnd implements Production
    {
        public Event produce() {
            ParserImpl.this.state = new ParseFlowSequenceEntry(false);
            final Token token = ParserImpl.this.scanner.peekToken();
            return new MappingEndEvent(token.getStartMark(), token.getEndMark());
        }
    }
    
    private class ParseFlowMappingFirstKey implements Production
    {
        public Event produce() {
            final Token token = ParserImpl.this.scanner.getToken();
            ParserImpl.this.marks.push(token.getStartMark());
            return new ParseFlowMappingKey(true).produce();
        }
    }
    
    private class ParseFlowMappingKey implements Production
    {
        private boolean first;
        
        public ParseFlowMappingKey(final boolean first) {
            this.first = false;
            this.first = first;
        }
        
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
                if (!this.first) {
                    if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry)) {
                        final Token token = ParserImpl.this.scanner.peekToken();
                        throw new ParserException("while parsing a flow mapping", ParserImpl.this.marks.pop(), "expected ',' or '}', but got " + token.getTokenId(), token.getStartMark());
                    }
                    ParserImpl.this.scanner.getToken();
                }
                if (ParserImpl.this.scanner.checkToken(Token.ID.Key)) {
                    final Token token = ParserImpl.this.scanner.getToken();
                    if (!ParserImpl.this.scanner.checkToken(Token.ID.Value, Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
                        ParserImpl.this.states.push(new ParseFlowMappingValue());
                        return ParserImpl.this.parseFlowNode();
                    }
                    ParserImpl.this.state = new ParseFlowMappingValue();
                    return ParserImpl.this.processEmptyScalar(token.getEndMark());
                }
                else if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowMappingEnd)) {
                    ParserImpl.this.states.push(new ParseFlowMappingEmptyValue());
                    return ParserImpl.this.parseFlowNode();
                }
            }
            final Token token = ParserImpl.this.scanner.getToken();
            final Event event = new MappingEndEvent(token.getStartMark(), token.getEndMark());
            ParserImpl.this.state = ParserImpl.this.states.pop();
            ParserImpl.this.marks.pop();
            return event;
        }
    }
    
    private class ParseFlowMappingValue implements Production
    {
        public Event produce() {
            if (!ParserImpl.this.scanner.checkToken(Token.ID.Value)) {
                ParserImpl.this.state = new ParseFlowMappingKey(false);
                final Token token = ParserImpl.this.scanner.peekToken();
                return ParserImpl.this.processEmptyScalar(token.getStartMark());
            }
            final Token token = ParserImpl.this.scanner.getToken();
            if (!ParserImpl.this.scanner.checkToken(Token.ID.FlowEntry, Token.ID.FlowMappingEnd)) {
                ParserImpl.this.states.push(new ParseFlowMappingKey(false));
                return ParserImpl.this.parseFlowNode();
            }
            ParserImpl.this.state = new ParseFlowMappingKey(false);
            return ParserImpl.this.processEmptyScalar(token.getEndMark());
        }
    }
    
    private class ParseFlowMappingEmptyValue implements Production
    {
        public Event produce() {
            ParserImpl.this.state = new ParseFlowMappingKey(false);
            return ParserImpl.this.processEmptyScalar(ParserImpl.this.scanner.peekToken().getStartMark());
        }
    }
}
