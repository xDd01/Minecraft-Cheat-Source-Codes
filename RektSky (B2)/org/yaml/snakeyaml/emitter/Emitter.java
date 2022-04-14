package org.yaml.snakeyaml.emitter;

import org.yaml.snakeyaml.util.*;
import org.yaml.snakeyaml.*;
import java.util.regex.*;
import java.util.concurrent.*;
import java.io.*;
import org.yaml.snakeyaml.scanner.*;
import org.yaml.snakeyaml.events.*;
import java.util.*;

public final class Emitter
{
    private static final Map<Character, String> ESCAPE_REPLACEMENTS;
    public static final int MIN_INDENT = 1;
    public static final int MAX_INDENT = 10;
    public static final char[] SPACE;
    private static final Map<String, String> DEFAULT_TAG_PREFIXES;
    private final Writer stream;
    private final ArrayStack<EmitterState> states;
    private EmitterState state;
    private final Queue<Event> events;
    private Event event;
    private final ArrayStack<Integer> indents;
    private Integer indent;
    private int flowLevel;
    private boolean rootContext;
    private boolean mappingContext;
    private boolean simpleKeyContext;
    private int column;
    private boolean whitespace;
    private boolean indention;
    private boolean openEnded;
    private Boolean canonical;
    private Boolean prettyFlow;
    private boolean allowUnicode;
    private int bestIndent;
    private int bestWidth;
    private char[] bestLineBreak;
    private Map<String, String> tagPrefixes;
    private String preparedAnchor;
    private String preparedTag;
    private ScalarAnalysis analysis;
    private Character style;
    private DumperOptions options;
    private static final Pattern HANDLE_FORMAT;
    private static final Pattern ANCHOR_FORMAT;
    
    public Emitter(final Writer stream, final DumperOptions opts) {
        this.stream = stream;
        this.states = new ArrayStack<EmitterState>(100);
        this.state = new ExpectStreamStart();
        this.events = new ArrayBlockingQueue<Event>(100);
        this.event = null;
        this.indents = new ArrayStack<Integer>(10);
        this.indent = null;
        this.flowLevel = 0;
        this.mappingContext = false;
        this.simpleKeyContext = false;
        this.column = 0;
        this.whitespace = true;
        this.indention = true;
        this.openEnded = false;
        this.canonical = opts.isCanonical();
        this.prettyFlow = opts.isPrettyFlow();
        this.allowUnicode = opts.isAllowUnicode();
        this.bestIndent = 2;
        if (opts.getIndent() > 1 && opts.getIndent() < 10) {
            this.bestIndent = opts.getIndent();
        }
        this.bestWidth = 80;
        if (opts.getWidth() > this.bestIndent * 2) {
            this.bestWidth = opts.getWidth();
        }
        this.bestLineBreak = opts.getLineBreak().getString().toCharArray();
        this.tagPrefixes = new LinkedHashMap<String, String>();
        this.preparedAnchor = null;
        this.preparedTag = null;
        this.analysis = null;
        this.style = null;
        this.options = opts;
    }
    
    public void emit(final Event event) throws IOException {
        this.events.add(event);
        while (!this.needMoreEvents()) {
            this.event = this.events.poll();
            this.state.expect();
            this.event = null;
        }
    }
    
    private boolean needMoreEvents() {
        if (this.events.isEmpty()) {
            return true;
        }
        final Event event = this.events.peek();
        if (event instanceof DocumentStartEvent) {
            return this.needEvents(1);
        }
        if (event instanceof SequenceStartEvent) {
            return this.needEvents(2);
        }
        return event instanceof MappingStartEvent && this.needEvents(3);
    }
    
    private boolean needEvents(final int count) {
        int level = 0;
        final Iterator<Event> iter = this.events.iterator();
        iter.next();
        while (iter.hasNext()) {
            final Event event = iter.next();
            if (event instanceof DocumentStartEvent || event instanceof CollectionStartEvent) {
                ++level;
            }
            else if (event instanceof DocumentEndEvent || event instanceof CollectionEndEvent) {
                --level;
            }
            else if (event instanceof StreamEndEvent) {
                level = -1;
            }
            if (level < 0) {
                return false;
            }
        }
        return this.events.size() < count + 1;
    }
    
    private void increaseIndent(final boolean flow, final boolean indentless) {
        this.indents.push(this.indent);
        if (this.indent == null) {
            if (flow) {
                this.indent = this.bestIndent;
            }
            else {
                this.indent = 0;
            }
        }
        else if (!indentless) {
            this.indent += this.bestIndent;
        }
    }
    
    private void expectNode(final boolean root, final boolean sequence, final boolean mapping, final boolean simpleKey) throws IOException {
        this.rootContext = root;
        this.mappingContext = mapping;
        this.simpleKeyContext = simpleKey;
        if (this.event instanceof AliasEvent) {
            this.expectAlias();
        }
        else {
            if (!(this.event instanceof ScalarEvent) && !(this.event instanceof CollectionStartEvent)) {
                throw new EmitterException("expected NodeEvent, but got " + this.event);
            }
            this.processAnchor("&");
            this.processTag();
            if (this.event instanceof ScalarEvent) {
                this.expectScalar();
            }
            else if (this.event instanceof SequenceStartEvent) {
                if (this.flowLevel != 0 || this.canonical || ((SequenceStartEvent)this.event).getFlowStyle() || this.checkEmptySequence()) {
                    this.expectFlowSequence();
                }
                else {
                    this.expectBlockSequence();
                }
            }
            else if (this.flowLevel != 0 || this.canonical || ((MappingStartEvent)this.event).getFlowStyle() || this.checkEmptyMapping()) {
                this.expectFlowMapping();
            }
            else {
                this.expectBlockMapping();
            }
        }
    }
    
    private void expectAlias() throws IOException {
        if (((NodeEvent)this.event).getAnchor() == null) {
            throw new EmitterException("anchor is not specified for alias");
        }
        this.processAnchor("*");
        this.state = this.states.pop();
    }
    
    private void expectScalar() throws IOException {
        this.increaseIndent(true, false);
        this.processScalar();
        this.indent = this.indents.pop();
        this.state = this.states.pop();
    }
    
    private void expectFlowSequence() throws IOException {
        this.writeIndicator("[", true, true, false);
        ++this.flowLevel;
        this.increaseIndent(true, false);
        if (this.prettyFlow) {
            this.writeIndent();
        }
        this.state = new ExpectFirstFlowSequenceItem();
    }
    
    private void expectFlowMapping() throws IOException {
        this.writeIndicator("{", true, true, false);
        ++this.flowLevel;
        this.increaseIndent(true, false);
        if (this.prettyFlow) {
            this.writeIndent();
        }
        this.state = new ExpectFirstFlowMappingKey();
    }
    
    private void expectBlockSequence() throws IOException {
        final boolean indentless = this.mappingContext && !this.indention;
        this.increaseIndent(false, indentless);
        this.state = new ExpectFirstBlockSequenceItem();
    }
    
    private void expectBlockMapping() throws IOException {
        this.increaseIndent(false, false);
        this.state = new ExpectFirstBlockMappingKey();
    }
    
    private boolean checkEmptySequence() {
        return this.event instanceof SequenceStartEvent && !this.events.isEmpty() && this.events.peek() instanceof SequenceEndEvent;
    }
    
    private boolean checkEmptyMapping() {
        return this.event instanceof MappingStartEvent && !this.events.isEmpty() && this.events.peek() instanceof MappingEndEvent;
    }
    
    private boolean checkEmptyDocument() {
        if (!(this.event instanceof DocumentStartEvent) || this.events.isEmpty()) {
            return false;
        }
        final Event event = this.events.peek();
        if (event instanceof ScalarEvent) {
            final ScalarEvent e = (ScalarEvent)event;
            return e.getAnchor() == null && e.getTag() == null && e.getImplicit() != null && e.getValue() == "";
        }
        return false;
    }
    
    private boolean checkSimpleKey() {
        int length = 0;
        if (this.event instanceof NodeEvent && ((NodeEvent)this.event).getAnchor() != null) {
            if (this.preparedAnchor == null) {
                this.preparedAnchor = prepareAnchor(((NodeEvent)this.event).getAnchor());
            }
            length += this.preparedAnchor.length();
        }
        String tag = null;
        if (this.event instanceof ScalarEvent) {
            tag = ((ScalarEvent)this.event).getTag();
        }
        else if (this.event instanceof CollectionStartEvent) {
            tag = ((CollectionStartEvent)this.event).getTag();
        }
        if (tag != null) {
            if (this.preparedTag == null) {
                this.preparedTag = this.prepareTag(tag);
            }
            length += this.preparedTag.length();
        }
        if (this.event instanceof ScalarEvent) {
            if (this.analysis == null) {
                this.analysis = this.analyzeScalar(((ScalarEvent)this.event).getValue());
            }
            length += this.analysis.scalar.length();
        }
        return length < 128 && (this.event instanceof AliasEvent || (this.event instanceof ScalarEvent && !this.analysis.empty && !this.analysis.multiline) || this.checkEmptySequence() || this.checkEmptyMapping());
    }
    
    private void processAnchor(final String indicator) throws IOException {
        final NodeEvent ev = (NodeEvent)this.event;
        if (ev.getAnchor() == null) {
            this.preparedAnchor = null;
            return;
        }
        if (this.preparedAnchor == null) {
            this.preparedAnchor = prepareAnchor(ev.getAnchor());
        }
        this.writeIndicator(indicator + this.preparedAnchor, true, false, false);
        this.preparedAnchor = null;
    }
    
    private void processTag() throws IOException {
        String tag = null;
        if (this.event instanceof ScalarEvent) {
            final ScalarEvent ev = (ScalarEvent)this.event;
            tag = ev.getTag();
            if (this.style == null) {
                this.style = this.chooseScalarStyle();
            }
            if ((!this.canonical || tag == null) && ((this.style == null && ev.getImplicit().isFirst()) || (this.style != null && ev.getImplicit().isSecond()))) {
                this.preparedTag = null;
                return;
            }
            if (ev.getImplicit().isFirst() && tag == null) {
                tag = "!";
                this.preparedTag = null;
            }
        }
        else {
            final CollectionStartEvent ev2 = (CollectionStartEvent)this.event;
            tag = ev2.getTag();
            if ((!this.canonical || tag == null) && ev2.getImplicit()) {
                this.preparedTag = null;
                return;
            }
        }
        if (tag == null) {
            throw new EmitterException("tag is not specified");
        }
        if (this.preparedTag == null) {
            this.preparedTag = this.prepareTag(tag);
        }
        this.writeIndicator(this.preparedTag, true, false, false);
        this.preparedTag = null;
    }
    
    private Character chooseScalarStyle() {
        final ScalarEvent ev = (ScalarEvent)this.event;
        if (this.analysis == null) {
            this.analysis = this.analyzeScalar(ev.getValue());
        }
        if ((ev.getStyle() != null && ev.getStyle() == '\"') || this.canonical) {
            return '\"';
        }
        if (ev.getStyle() == null && ev.getImplicit().isFirst() && (!this.simpleKeyContext || (!this.analysis.empty && !this.analysis.multiline)) && ((this.flowLevel != 0 && this.analysis.allowFlowPlain) || (this.flowLevel == 0 && this.analysis.allowBlockPlain))) {
            return null;
        }
        if (ev.getStyle() != null && (ev.getStyle() == '|' || ev.getStyle() == '>') && this.flowLevel == 0 && !this.simpleKeyContext && this.analysis.allowBlock) {
            return ev.getStyle();
        }
        if ((ev.getStyle() == null || ev.getStyle() == '\'') && this.analysis.allowSingleQuoted && (!this.simpleKeyContext || !this.analysis.multiline)) {
            return '\'';
        }
        return '\"';
    }
    
    private void processScalar() throws IOException {
        final ScalarEvent ev = (ScalarEvent)this.event;
        if (this.analysis == null) {
            this.analysis = this.analyzeScalar(ev.getValue());
        }
        if (this.style == null) {
            this.style = this.chooseScalarStyle();
        }
        this.style = this.options.calculateScalarStyle(this.analysis, DumperOptions.ScalarStyle.createStyle(this.style)).getChar();
        final boolean split = !this.simpleKeyContext;
        if (this.style == null) {
            this.writePlain(this.analysis.scalar, split);
        }
        else {
            switch ((char)this.style) {
                case '\"': {
                    this.writeDoubleQuoted(this.analysis.scalar, split);
                    break;
                }
                case '\'': {
                    this.writeSingleQuoted(this.analysis.scalar, split);
                    break;
                }
                case '>': {
                    this.writeFolded(this.analysis.scalar);
                    break;
                }
                case '|': {
                    this.writeLiteral(this.analysis.scalar);
                    break;
                }
            }
        }
        this.analysis = null;
        this.style = null;
    }
    
    private String prepareVersion(final Integer[] version) {
        final Integer major = version[0];
        final Integer minor = version[1];
        if (major != 1) {
            throw new EmitterException("unsupported YAML version: " + version[0] + "." + version[1]);
        }
        return major.toString() + "." + minor.toString();
    }
    
    private String prepareTagHandle(final String handle) {
        if (handle.length() == 0) {
            throw new EmitterException("tag handle must not be empty");
        }
        if (handle.charAt(0) != '!' || handle.charAt(handle.length() - 1) != '!') {
            throw new EmitterException("tag handle must start and end with '!': " + handle);
        }
        if (!"!".equals(handle) && !Emitter.HANDLE_FORMAT.matcher(handle).matches()) {
            throw new EmitterException("invalid character in the tag handle: " + handle);
        }
        return handle;
    }
    
    private String prepareTagPrefix(final String prefix) {
        if (prefix.length() == 0) {
            throw new EmitterException("tag prefix must not be empty");
        }
        final StringBuilder chunks = new StringBuilder();
        final int start = 0;
        int end = 0;
        if (prefix.charAt(0) == '!') {
            end = 1;
        }
        while (end < prefix.length()) {
            ++end;
        }
        if (start < end) {
            chunks.append(prefix.substring(start, end));
        }
        return chunks.toString();
    }
    
    private String prepareTag(final String tag) {
        if (tag.length() == 0) {
            throw new EmitterException("tag must not be empty");
        }
        if ("!".equals(tag)) {
            return tag;
        }
        String handle = null;
        String suffix = tag;
        for (final String prefix : this.tagPrefixes.keySet()) {
            if (tag.startsWith(prefix) && ("!".equals(prefix) || prefix.length() < tag.length())) {
                handle = prefix;
            }
        }
        if (handle != null) {
            suffix = tag.substring(handle.length());
            handle = this.tagPrefixes.get(handle);
        }
        final int end = suffix.length();
        final String suffixText = (end > 0) ? suffix.substring(0, end) : "";
        if (handle != null) {
            return handle + suffixText;
        }
        return "!<" + suffixText + ">";
    }
    
    static String prepareAnchor(final String anchor) {
        if (anchor.length() == 0) {
            throw new EmitterException("anchor must not be empty");
        }
        if (!Emitter.ANCHOR_FORMAT.matcher(anchor).matches()) {
            throw new EmitterException("invalid character in the anchor: " + anchor);
        }
        return anchor;
    }
    
    private ScalarAnalysis analyzeScalar(final String scalar) {
        if (scalar.length() == 0) {
            return new ScalarAnalysis(scalar, true, false, false, true, true, true, false);
        }
        boolean blockIndicators = false;
        boolean flowIndicators = false;
        boolean lineBreaks = false;
        boolean specialCharacters = false;
        boolean leadingSpace = false;
        boolean leadingBreak = false;
        boolean trailingSpace = false;
        boolean trailingBreak = false;
        boolean breakSpace = false;
        boolean spaceBreak = false;
        if (scalar.startsWith("---") || scalar.startsWith("...")) {
            blockIndicators = true;
            flowIndicators = true;
        }
        boolean preceededByWhitespace = true;
        boolean followedByWhitespace = scalar.length() == 1 || Constant.NULL_BL_T_LINEBR.has(scalar.charAt(1));
        boolean previousSpace = false;
        boolean previousBreak = false;
        char ch;
        boolean isLineBreak;
        for (int index = 0; index < scalar.length(); ++index, preceededByWhitespace = (Constant.NULL_BL_T.has(ch) || isLineBreak), followedByWhitespace = (index + 1 >= scalar.length() || Constant.NULL_BL_T.has(scalar.charAt(index + 1)) || isLineBreak)) {
            ch = scalar.charAt(index);
            if (index == 0) {
                if ("#,[]{}&*!|>'\"%@`".indexOf(ch) != -1) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
                if (ch == '?' || ch == ':') {
                    flowIndicators = true;
                    if (followedByWhitespace) {
                        blockIndicators = true;
                    }
                }
                if (ch == '-' && followedByWhitespace) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
            }
            else {
                if (",?[]{}".indexOf(ch) != -1) {
                    flowIndicators = true;
                }
                if (ch == ':') {
                    flowIndicators = true;
                    if (followedByWhitespace) {
                        blockIndicators = true;
                    }
                }
                if (ch == '#' && preceededByWhitespace) {
                    flowIndicators = true;
                    blockIndicators = true;
                }
            }
            isLineBreak = Constant.LINEBR.has(ch);
            if (isLineBreak) {
                lineBreaks = true;
            }
            if (ch != '\n' && (' ' > ch || ch > '~')) {
                if ((ch == '\u0085' || (' ' <= ch && ch <= '\ud7ff') || ('\ue000' <= ch && ch <= '\ufffd')) && ch != '\ufeff') {
                    if (!this.allowUnicode) {
                        specialCharacters = true;
                    }
                }
                else {
                    specialCharacters = true;
                }
            }
            if (ch == ' ') {
                if (index == 0) {
                    leadingSpace = true;
                }
                if (index == scalar.length() - 1) {
                    trailingSpace = true;
                }
                if (previousBreak) {
                    breakSpace = true;
                }
                previousSpace = true;
                previousBreak = false;
            }
            else if (isLineBreak) {
                if (index == 0) {
                    leadingBreak = true;
                }
                if (index == scalar.length() - 1) {
                    trailingBreak = true;
                }
                if (previousSpace) {
                    spaceBreak = true;
                }
                previousSpace = false;
                previousBreak = true;
            }
            else {
                previousSpace = false;
                previousBreak = false;
            }
        }
        boolean allowFlowPlain = true;
        boolean allowBlockPlain = true;
        boolean allowSingleQuoted = true;
        final boolean allowDoubleQuoted = true;
        boolean allowBlock = true;
        if (leadingSpace || leadingBreak || trailingSpace || trailingBreak) {
            allowBlockPlain = (allowFlowPlain = false);
        }
        if (trailingSpace) {
            allowBlock = false;
        }
        if (breakSpace) {
            allowBlockPlain = (allowFlowPlain = (allowSingleQuoted = false));
        }
        if (spaceBreak || specialCharacters) {
            allowBlockPlain = (allowFlowPlain = (allowSingleQuoted = (allowBlock = false)));
        }
        if (lineBreaks) {
            allowBlockPlain = (allowFlowPlain = false);
        }
        if (flowIndicators) {
            allowFlowPlain = false;
        }
        if (blockIndicators) {
            allowBlockPlain = false;
        }
        return new ScalarAnalysis(scalar, false, lineBreaks, allowFlowPlain, allowBlockPlain, allowSingleQuoted, allowDoubleQuoted, allowBlock);
    }
    
    void flushStream() throws IOException {
        this.stream.flush();
    }
    
    void writeStreamStart() {
    }
    
    void writeStreamEnd() throws IOException {
        this.flushStream();
    }
    
    void writeIndicator(final String indicator, final boolean needWhitespace, final boolean whitespace, final boolean indentation) throws IOException {
        if (!this.whitespace && needWhitespace) {
            ++this.column;
            this.stream.write(Emitter.SPACE);
        }
        this.whitespace = whitespace;
        this.indention = (this.indention && indentation);
        this.column += indicator.length();
        this.openEnded = false;
        this.stream.write(indicator);
    }
    
    void writeIndent() throws IOException {
        int indent;
        if (this.indent != null) {
            indent = this.indent;
        }
        else {
            indent = 0;
        }
        if (!this.indention || this.column > indent || (this.column == indent && !this.whitespace)) {
            this.writeLineBreak(null);
        }
        if (this.column < indent) {
            this.whitespace = true;
            final char[] data = new char[indent - this.column];
            for (int i = 0; i < data.length; ++i) {
                data[i] = ' ';
            }
            this.column = indent;
            this.stream.write(data);
        }
    }
    
    private void writeLineBreak(final String data) throws IOException {
        this.whitespace = true;
        this.indention = true;
        this.column = 0;
        if (data == null) {
            this.stream.write(this.bestLineBreak);
        }
        else {
            this.stream.write(data);
        }
    }
    
    void writeVersionDirective(final String versionText) throws IOException {
        this.stream.write("%YAML ");
        this.stream.write(versionText);
        this.writeLineBreak(null);
    }
    
    void writeTagDirective(final String handleText, final String prefixText) throws IOException {
        this.stream.write("%TAG ");
        this.stream.write(handleText);
        this.stream.write(Emitter.SPACE);
        this.stream.write(prefixText);
        this.writeLineBreak(null);
    }
    
    private void writeSingleQuoted(final String text, final boolean split) throws IOException {
        this.writeIndicator("'", true, false, false);
        boolean spaces = false;
        boolean breaks = false;
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            char ch = '\0';
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (spaces) {
                if (ch == '\0' || ch != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split && start != 0 && end != text.length()) {
                        this.writeIndent();
                    }
                    else {
                        final int len = end - start;
                        this.column += len;
                        this.stream.write(text, start, len);
                    }
                    start = end;
                }
            }
            else if (breaks) {
                if (ch == '\0' || Constant.LINEBR.hasNo(ch)) {
                    if (text.charAt(start) == '\n') {
                        this.writeLineBreak(null);
                    }
                    final String data = text.substring(start, end);
                    for (final char br : data.toCharArray()) {
                        if (br == '\n') {
                            this.writeLineBreak(null);
                        }
                        else {
                            this.writeLineBreak(String.valueOf(br));
                        }
                    }
                    this.writeIndent();
                    start = end;
                }
            }
            else if (Constant.LINEBR.has(ch, "\u0000 '") && start < end) {
                final int len = end - start;
                this.column += len;
                this.stream.write(text, start, len);
                start = end;
            }
            if (ch == '\'') {
                this.column += 2;
                this.stream.write("''");
                start = end + 1;
            }
            if (ch != '\0') {
                spaces = (ch == ' ');
                breaks = Constant.LINEBR.has(ch);
            }
        }
        this.writeIndicator("'", false, false, false);
    }
    
    private void writeDoubleQuoted(final String text, final boolean split) throws IOException {
        this.writeIndicator("\"", true, false, false);
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            Character ch = null;
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (ch == null || "\"\\\u0085\u2028\u2029\ufeff".indexOf(ch) != -1 || ' ' > ch || ch > '~') {
                if (start < end) {
                    final int len = end - start;
                    this.column += len;
                    this.stream.write(text, start, len);
                    start = end;
                }
                if (ch != null) {
                    String data;
                    if (Emitter.ESCAPE_REPLACEMENTS.containsKey(new Character(ch))) {
                        data = "\\" + Emitter.ESCAPE_REPLACEMENTS.get(new Character(ch));
                    }
                    else if (!this.allowUnicode) {
                        if (ch <= '\u00ff') {
                            final String s = "0" + Integer.toString(ch, 16);
                            data = "\\x" + s.substring(s.length() - 2);
                        }
                        else {
                            final String s = "000" + Integer.toString(ch, 16);
                            data = "\\u" + s.substring(s.length() - 4);
                        }
                    }
                    else {
                        data = String.valueOf(ch);
                    }
                    this.column += data.length();
                    this.stream.write(data);
                    start = end + 1;
                }
            }
            if (0 < end && end < text.length() - 1 && (ch == ' ' || start >= end) && this.column + (end - start) > this.bestWidth && split) {
                String data;
                if (start >= end) {
                    data = "\\";
                }
                else {
                    data = text.substring(start, end) + "\\";
                }
                if (start < end) {
                    start = end;
                }
                this.column += data.length();
                this.stream.write(data);
                this.writeIndent();
                this.whitespace = false;
                this.indention = false;
                if (text.charAt(start) == ' ') {
                    data = "\\";
                    this.column += data.length();
                    this.stream.write(data);
                }
            }
        }
        this.writeIndicator("\"", false, false, false);
    }
    
    private String determineBlockHints(final String text) {
        final StringBuilder hints = new StringBuilder();
        if (Constant.LINEBR.has(text.charAt(0), " ")) {
            hints.append(this.bestIndent);
        }
        final char ch1 = text.charAt(text.length() - 1);
        if (Constant.LINEBR.hasNo(ch1)) {
            hints.append("-");
        }
        else if (text.length() == 1 || Constant.LINEBR.has(text.charAt(text.length() - 2))) {
            hints.append("+");
        }
        return hints.toString();
    }
    
    void writeFolded(final String text) throws IOException {
        final String hints = this.determineBlockHints(text);
        this.writeIndicator(">" + hints, true, false, false);
        if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
            this.openEnded = true;
        }
        this.writeLineBreak(null);
        boolean leadingSpace = true;
        boolean spaces = false;
        boolean breaks = true;
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            char ch = '\0';
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (breaks) {
                if (ch == '\0' || Constant.LINEBR.hasNo(ch)) {
                    if (!leadingSpace && ch != '\0' && ch != ' ' && text.charAt(start) == '\n') {
                        this.writeLineBreak(null);
                    }
                    leadingSpace = (ch == ' ');
                    final String data = text.substring(start, end);
                    for (final char br : data.toCharArray()) {
                        if (br == '\n') {
                            this.writeLineBreak(null);
                        }
                        else {
                            this.writeLineBreak(String.valueOf(br));
                        }
                    }
                    if (ch != '\0') {
                        this.writeIndent();
                    }
                    start = end;
                }
            }
            else if (spaces) {
                if (ch != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth) {
                        this.writeIndent();
                    }
                    else {
                        final int len = end - start;
                        this.column += len;
                        this.stream.write(text, start, len);
                    }
                    start = end;
                }
            }
            else if (Constant.LINEBR.has(ch, "\u0000 ")) {
                final int len = end - start;
                this.column += len;
                this.stream.write(text, start, len);
                if (ch == '\0') {
                    this.writeLineBreak(null);
                }
                start = end;
            }
            if (ch != '\0') {
                breaks = Constant.LINEBR.has(ch);
                spaces = (ch == ' ');
            }
        }
    }
    
    void writeLiteral(final String text) throws IOException {
        final String hints = this.determineBlockHints(text);
        this.writeIndicator("|" + hints, true, false, false);
        if (hints.length() > 0 && hints.charAt(hints.length() - 1) == '+') {
            this.openEnded = true;
        }
        this.writeLineBreak(null);
        boolean breaks = true;
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            char ch = '\0';
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (breaks) {
                if (ch == '\0' || Constant.LINEBR.hasNo(ch)) {
                    final String data = text.substring(start, end);
                    for (final char br : data.toCharArray()) {
                        if (br == '\n') {
                            this.writeLineBreak(null);
                        }
                        else {
                            this.writeLineBreak(String.valueOf(br));
                        }
                    }
                    if (ch != '\0') {
                        this.writeIndent();
                    }
                    start = end;
                }
            }
            else if (ch == '\0' || Constant.LINEBR.has(ch)) {
                this.stream.write(text, start, end - start);
                if (ch == '\0') {
                    this.writeLineBreak(null);
                }
                start = end;
            }
            if (ch != '\0') {
                breaks = Constant.LINEBR.has(ch);
            }
        }
    }
    
    void writePlain(final String text, final boolean split) throws IOException {
        if (this.rootContext) {
            this.openEnded = true;
        }
        if (text.length() == 0) {
            return;
        }
        if (!this.whitespace) {
            ++this.column;
            this.stream.write(Emitter.SPACE);
        }
        this.whitespace = false;
        this.indention = false;
        boolean spaces = false;
        boolean breaks = false;
        int start = 0;
        for (int end = 0; end <= text.length(); ++end) {
            char ch = '\0';
            if (end < text.length()) {
                ch = text.charAt(end);
            }
            if (spaces) {
                if (ch != ' ') {
                    if (start + 1 == end && this.column > this.bestWidth && split) {
                        this.writeIndent();
                        this.whitespace = false;
                        this.indention = false;
                    }
                    else {
                        final int len = end - start;
                        this.column += len;
                        this.stream.write(text, start, len);
                    }
                    start = end;
                }
            }
            else if (breaks) {
                if (Constant.LINEBR.hasNo(ch)) {
                    if (text.charAt(start) == '\n') {
                        this.writeLineBreak(null);
                    }
                    final String data = text.substring(start, end);
                    for (final char br : data.toCharArray()) {
                        if (br == '\n') {
                            this.writeLineBreak(null);
                        }
                        else {
                            this.writeLineBreak(String.valueOf(br));
                        }
                    }
                    this.writeIndent();
                    this.whitespace = false;
                    this.indention = false;
                    start = end;
                }
            }
            else if (ch == '\0' || Constant.LINEBR.has(ch)) {
                final int len = end - start;
                this.column += len;
                this.stream.write(text, start, len);
                start = end;
            }
            if (ch != '\0') {
                spaces = (ch == ' ');
                breaks = Constant.LINEBR.has(ch);
            }
        }
    }
    
    static {
        ESCAPE_REPLACEMENTS = new HashMap<Character, String>();
        SPACE = new char[] { ' ' };
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\0'), "0");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\u0007'), "a");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\b'), "b");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\t'), "t");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\n'), "n");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\u000b'), "v");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\f'), "f");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\r'), "r");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\u001b'), "e");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\"'), "\"");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\\'), "\\");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\u0085'), "N");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character(' '), "_");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\u2028'), "L");
        Emitter.ESCAPE_REPLACEMENTS.put(new Character('\u2029'), "P");
        (DEFAULT_TAG_PREFIXES = new LinkedHashMap<String, String>()).put("!", "!");
        Emitter.DEFAULT_TAG_PREFIXES.put("tag:yaml.org,2002:", "!!");
        HANDLE_FORMAT = Pattern.compile("^![-_\\w]*!$");
        ANCHOR_FORMAT = Pattern.compile("^[-_\\w]*$");
    }
    
    private class ExpectStreamStart implements EmitterState
    {
        public void expect() throws IOException {
            if (Emitter.this.event instanceof StreamStartEvent) {
                Emitter.this.writeStreamStart();
                Emitter.this.state = new ExpectFirstDocumentStart();
                return;
            }
            throw new EmitterException("expected StreamStartEvent, but got " + Emitter.this.event);
        }
    }
    
    private class ExpectNothing implements EmitterState
    {
        public void expect() throws IOException {
            throw new EmitterException("expecting nothing, but got " + Emitter.this.event);
        }
    }
    
    private class ExpectFirstDocumentStart implements EmitterState
    {
        public void expect() throws IOException {
            new ExpectDocumentStart(true).expect();
        }
    }
    
    private class ExpectDocumentStart implements EmitterState
    {
        private boolean first;
        
        public ExpectDocumentStart(final boolean first) {
            this.first = first;
        }
        
        public void expect() throws IOException {
            if (Emitter.this.event instanceof DocumentStartEvent) {
                final DocumentStartEvent ev = (DocumentStartEvent)Emitter.this.event;
                if ((ev.getVersion() != null || ev.getTags() != null) && Emitter.this.openEnded) {
                    Emitter.this.writeIndicator("...", true, false, false);
                    Emitter.this.writeIndent();
                }
                if (ev.getVersion() != null) {
                    final String versionText = Emitter.this.prepareVersion(ev.getVersion());
                    Emitter.this.writeVersionDirective(versionText);
                }
                Emitter.this.tagPrefixes = (Map<String, String>)new LinkedHashMap(Emitter.DEFAULT_TAG_PREFIXES);
                if (ev.getTags() != null) {
                    final Set<String> handles = new TreeSet<String>(ev.getTags().keySet());
                    for (final String handle : handles) {
                        final String prefix = ev.getTags().get(handle);
                        Emitter.this.tagPrefixes.put(prefix, handle);
                        final String handleText = Emitter.this.prepareTagHandle(handle);
                        final String prefixText = Emitter.this.prepareTagPrefix(prefix);
                        Emitter.this.writeTagDirective(handleText, prefixText);
                    }
                }
                final boolean implicit = this.first && !ev.getExplicit() && !Emitter.this.canonical && ev.getVersion() == null && ev.getTags() == null && !Emitter.this.checkEmptyDocument();
                if (!implicit) {
                    Emitter.this.writeIndent();
                    Emitter.this.writeIndicator("---", true, false, false);
                    if (Emitter.this.canonical) {
                        Emitter.this.writeIndent();
                    }
                }
                Emitter.this.state = new ExpectDocumentRoot();
            }
            else {
                if (!(Emitter.this.event instanceof StreamEndEvent)) {
                    throw new EmitterException("expected DocumentStartEvent, but got " + Emitter.this.event);
                }
                Emitter.this.writeStreamEnd();
                Emitter.this.state = new ExpectNothing();
            }
        }
    }
    
    private class ExpectDocumentEnd implements EmitterState
    {
        public void expect() throws IOException {
            if (Emitter.this.event instanceof DocumentEndEvent) {
                Emitter.this.writeIndent();
                if (((DocumentEndEvent)Emitter.this.event).getExplicit()) {
                    Emitter.this.writeIndicator("...", true, false, false);
                    Emitter.this.writeIndent();
                }
                Emitter.this.flushStream();
                Emitter.this.state = new ExpectDocumentStart(false);
                return;
            }
            throw new EmitterException("expected DocumentEndEvent, but got " + Emitter.this.event);
        }
    }
    
    private class ExpectDocumentRoot implements EmitterState
    {
        public void expect() throws IOException {
            Emitter.this.states.push(new ExpectDocumentEnd());
            Emitter.this.expectNode(true, false, false, false);
        }
    }
    
    private class ExpectFirstFlowSequenceItem implements EmitterState
    {
        public void expect() throws IOException {
            if (Emitter.this.event instanceof SequenceEndEvent) {
                Emitter.this.indent = Emitter.this.indents.pop();
                Emitter.this.flowLevel--;
                Emitter.this.writeIndicator("]", false, false, false);
                Emitter.this.state = Emitter.this.states.pop();
            }
            else {
                if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.prettyFlow) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.states.push(new ExpectFlowSequenceItem());
                Emitter.this.expectNode(false, true, false, false);
            }
        }
    }
    
    private class ExpectFlowSequenceItem implements EmitterState
    {
        public void expect() throws IOException {
            if (Emitter.this.event instanceof SequenceEndEvent) {
                Emitter.this.indent = Emitter.this.indents.pop();
                Emitter.this.flowLevel--;
                if (Emitter.this.canonical) {
                    Emitter.this.writeIndicator(",", false, false, false);
                    Emitter.this.writeIndent();
                }
                Emitter.this.writeIndicator("]", false, false, false);
                if (Emitter.this.prettyFlow) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.state = Emitter.this.states.pop();
            }
            else {
                Emitter.this.writeIndicator(",", false, false, false);
                if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.prettyFlow) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.states.push(new ExpectFlowSequenceItem());
                Emitter.this.expectNode(false, true, false, false);
            }
        }
    }
    
    private class ExpectFirstFlowMappingKey implements EmitterState
    {
        public void expect() throws IOException {
            if (Emitter.this.event instanceof MappingEndEvent) {
                Emitter.this.indent = Emitter.this.indents.pop();
                Emitter.this.flowLevel--;
                Emitter.this.writeIndicator("}", false, false, false);
                Emitter.this.state = Emitter.this.states.pop();
            }
            else {
                if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.prettyFlow) {
                    Emitter.this.writeIndent();
                }
                if (!Emitter.this.canonical && Emitter.this.checkSimpleKey()) {
                    Emitter.this.states.push(new ExpectFlowMappingSimpleValue());
                    Emitter.this.expectNode(false, false, true, true);
                }
                else {
                    Emitter.this.writeIndicator("?", true, false, false);
                    Emitter.this.states.push(new ExpectFlowMappingValue());
                    Emitter.this.expectNode(false, false, true, false);
                }
            }
        }
    }
    
    private class ExpectFlowMappingKey implements EmitterState
    {
        public void expect() throws IOException {
            if (Emitter.this.event instanceof MappingEndEvent) {
                Emitter.this.indent = Emitter.this.indents.pop();
                Emitter.this.flowLevel--;
                if (Emitter.this.canonical) {
                    Emitter.this.writeIndicator(",", false, false, false);
                    Emitter.this.writeIndent();
                }
                if (Emitter.this.prettyFlow) {
                    Emitter.this.writeIndent();
                }
                Emitter.this.writeIndicator("}", false, false, false);
                Emitter.this.state = Emitter.this.states.pop();
            }
            else {
                Emitter.this.writeIndicator(",", false, false, false);
                if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.prettyFlow) {
                    Emitter.this.writeIndent();
                }
                if (!Emitter.this.canonical && Emitter.this.checkSimpleKey()) {
                    Emitter.this.states.push(new ExpectFlowMappingSimpleValue());
                    Emitter.this.expectNode(false, false, true, true);
                }
                else {
                    Emitter.this.writeIndicator("?", true, false, false);
                    Emitter.this.states.push(new ExpectFlowMappingValue());
                    Emitter.this.expectNode(false, false, true, false);
                }
            }
        }
    }
    
    private class ExpectFlowMappingSimpleValue implements EmitterState
    {
        public void expect() throws IOException {
            Emitter.this.writeIndicator(":", false, false, false);
            Emitter.this.states.push(new ExpectFlowMappingKey());
            Emitter.this.expectNode(false, false, true, false);
        }
    }
    
    private class ExpectFlowMappingValue implements EmitterState
    {
        public void expect() throws IOException {
            if (Emitter.this.canonical || Emitter.this.column > Emitter.this.bestWidth || Emitter.this.prettyFlow) {
                Emitter.this.writeIndent();
            }
            Emitter.this.writeIndicator(":", true, false, false);
            Emitter.this.states.push(new ExpectFlowMappingKey());
            Emitter.this.expectNode(false, false, true, false);
        }
    }
    
    private class ExpectFirstBlockSequenceItem implements EmitterState
    {
        public void expect() throws IOException {
            new ExpectBlockSequenceItem(true).expect();
        }
    }
    
    private class ExpectBlockSequenceItem implements EmitterState
    {
        private boolean first;
        
        public ExpectBlockSequenceItem(final boolean first) {
            this.first = first;
        }
        
        public void expect() throws IOException {
            if (!this.first && Emitter.this.event instanceof SequenceEndEvent) {
                Emitter.this.indent = Emitter.this.indents.pop();
                Emitter.this.state = Emitter.this.states.pop();
            }
            else {
                Emitter.this.writeIndent();
                Emitter.this.writeIndicator("-", true, false, true);
                Emitter.this.states.push(new ExpectBlockSequenceItem(false));
                Emitter.this.expectNode(false, true, false, false);
            }
        }
    }
    
    private class ExpectFirstBlockMappingKey implements EmitterState
    {
        public void expect() throws IOException {
            new ExpectBlockMappingKey(true).expect();
        }
    }
    
    private class ExpectBlockMappingKey implements EmitterState
    {
        private boolean first;
        
        public ExpectBlockMappingKey(final boolean first) {
            this.first = first;
        }
        
        public void expect() throws IOException {
            if (!this.first && Emitter.this.event instanceof MappingEndEvent) {
                Emitter.this.indent = Emitter.this.indents.pop();
                Emitter.this.state = Emitter.this.states.pop();
            }
            else {
                Emitter.this.writeIndent();
                if (Emitter.this.checkSimpleKey()) {
                    Emitter.this.states.push(new ExpectBlockMappingSimpleValue());
                    Emitter.this.expectNode(false, false, true, true);
                }
                else {
                    Emitter.this.writeIndicator("?", true, false, true);
                    Emitter.this.states.push(new ExpectBlockMappingValue());
                    Emitter.this.expectNode(false, false, true, false);
                }
            }
        }
    }
    
    private class ExpectBlockMappingSimpleValue implements EmitterState
    {
        public void expect() throws IOException {
            Emitter.this.writeIndicator(":", false, false, false);
            Emitter.this.states.push(new ExpectBlockMappingKey(false));
            Emitter.this.expectNode(false, false, true, false);
        }
    }
    
    private class ExpectBlockMappingValue implements EmitterState
    {
        public void expect() throws IOException {
            Emitter.this.writeIndent();
            Emitter.this.writeIndicator(":", true, false, true);
            Emitter.this.states.push(new ExpectBlockMappingKey(false));
            Emitter.this.expectNode(false, false, true, false);
        }
    }
}
