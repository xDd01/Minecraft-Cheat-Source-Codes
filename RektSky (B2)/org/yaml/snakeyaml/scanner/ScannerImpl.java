package org.yaml.snakeyaml.scanner;

import java.util.regex.*;
import org.yaml.snakeyaml.reader.*;
import org.yaml.snakeyaml.error.*;
import org.yaml.snakeyaml.tokens.*;
import java.nio.*;
import org.yaml.snakeyaml.util.*;
import java.nio.charset.*;
import java.util.*;

public final class ScannerImpl implements Scanner
{
    private static final Pattern NOT_HEXA;
    public static final Map<Character, String> ESCAPE_REPLACEMENTS;
    public static final Map<Character, Integer> ESCAPE_CODES;
    private final StreamReader reader;
    private boolean done;
    private int flowLevel;
    private List<Token> tokens;
    private int tokensTaken;
    private int indent;
    private ArrayStack<Integer> indents;
    private boolean allowSimpleKey;
    private Map<Integer, SimpleKey> possibleSimpleKeys;
    
    public ScannerImpl(final StreamReader reader) {
        this.done = false;
        this.flowLevel = 0;
        this.tokensTaken = 0;
        this.indent = -1;
        this.allowSimpleKey = true;
        this.reader = reader;
        this.tokens = new ArrayList<Token>(100);
        this.indents = new ArrayStack<Integer>(10);
        this.possibleSimpleKeys = new LinkedHashMap<Integer, SimpleKey>();
        this.fetchStreamStart();
    }
    
    public boolean checkToken(final Token.ID... choices) {
        while (this.needMoreTokens()) {
            this.fetchMoreTokens();
        }
        if (!this.tokens.isEmpty()) {
            if (choices.length == 0) {
                return true;
            }
            final Token.ID first = this.tokens.get(0).getTokenId();
            for (int i = 0; i < choices.length; ++i) {
                if (first == choices[i]) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public Token peekToken() {
        while (this.needMoreTokens()) {
            this.fetchMoreTokens();
        }
        return this.tokens.get(0);
    }
    
    public Token getToken() {
        if (!this.tokens.isEmpty()) {
            ++this.tokensTaken;
            return this.tokens.remove(0);
        }
        return null;
    }
    
    private boolean needMoreTokens() {
        if (this.done) {
            return false;
        }
        if (this.tokens.isEmpty()) {
            return true;
        }
        this.stalePossibleSimpleKeys();
        return this.nextPossibleSimpleKey() == this.tokensTaken;
    }
    
    private void fetchMoreTokens() {
        this.scanToNextToken();
        this.stalePossibleSimpleKeys();
        this.unwindIndent(this.reader.getColumn());
        final char ch = this.reader.peek();
        switch (ch) {
            case '\0': {
                this.fetchStreamEnd();
                return;
            }
            case '%': {
                if (this.checkDirective()) {
                    this.fetchDirective();
                    return;
                }
                break;
            }
            case '-': {
                if (this.checkDocumentStart()) {
                    this.fetchDocumentStart();
                    return;
                }
                if (this.checkBlockEntry()) {
                    this.fetchBlockEntry();
                    return;
                }
                break;
            }
            case '.': {
                if (this.checkDocumentEnd()) {
                    this.fetchDocumentEnd();
                    return;
                }
                break;
            }
            case '[': {
                this.fetchFlowSequenceStart();
                return;
            }
            case '{': {
                this.fetchFlowMappingStart();
                return;
            }
            case ']': {
                this.fetchFlowSequenceEnd();
                return;
            }
            case '}': {
                this.fetchFlowMappingEnd();
                return;
            }
            case ',': {
                this.fetchFlowEntry();
                return;
            }
            case '?': {
                if (this.checkKey()) {
                    this.fetchKey();
                    return;
                }
                break;
            }
            case ':': {
                if (this.checkValue()) {
                    this.fetchValue();
                    return;
                }
                break;
            }
            case '*': {
                this.fetchAlias();
                return;
            }
            case '&': {
                this.fetchAnchor();
                return;
            }
            case '!': {
                this.fetchTag();
                return;
            }
            case '|': {
                if (this.flowLevel == 0) {
                    this.fetchLiteral();
                    return;
                }
                break;
            }
            case '>': {
                if (this.flowLevel == 0) {
                    this.fetchFolded();
                    return;
                }
                break;
            }
            case '\'': {
                this.fetchSingle();
                return;
            }
            case '\"': {
                this.fetchDouble();
                return;
            }
        }
        if (this.checkPlain()) {
            this.fetchPlain();
            return;
        }
        String chRepresentation = String.valueOf(ch);
        for (final Character s : ScannerImpl.ESCAPE_REPLACEMENTS.keySet()) {
            final String v = ScannerImpl.ESCAPE_REPLACEMENTS.get(s);
            if (v.equals(chRepresentation)) {
                chRepresentation = "\\" + s;
                break;
            }
        }
        throw new ScannerException("while scanning for the next token", null, "found character " + ch + "'" + chRepresentation + "' that cannot start any token", this.reader.getMark());
    }
    
    private int nextPossibleSimpleKey() {
        if (!this.possibleSimpleKeys.isEmpty()) {
            return this.possibleSimpleKeys.values().iterator().next().getTokenNumber();
        }
        return -1;
    }
    
    private void stalePossibleSimpleKeys() {
        if (!this.possibleSimpleKeys.isEmpty()) {
            final Iterator<SimpleKey> iterator = this.possibleSimpleKeys.values().iterator();
            while (iterator.hasNext()) {
                final SimpleKey key = iterator.next();
                if (key.getLine() != this.reader.getLine() || this.reader.getIndex() - key.getIndex() > 1024) {
                    if (key.isRequired()) {
                        throw new ScannerException("while scanning a simple key", key.getMark(), "could not found expected ':'", this.reader.getMark());
                    }
                    iterator.remove();
                }
            }
        }
    }
    
    private void savePossibleSimpleKey() {
        final boolean required = this.flowLevel == 0 && this.indent == this.reader.getColumn();
        if (!this.allowSimpleKey && required) {
            throw new YAMLException("A simple key is required only if it is the first token in the current line");
        }
        if (this.allowSimpleKey) {
            this.removePossibleSimpleKey();
            final int tokenNumber = this.tokensTaken + this.tokens.size();
            final SimpleKey key = new SimpleKey(tokenNumber, required, this.reader.getIndex(), this.reader.getLine(), this.reader.getColumn(), this.reader.getMark());
            this.possibleSimpleKeys.put(this.flowLevel, key);
        }
    }
    
    private void removePossibleSimpleKey() {
        final SimpleKey key = this.possibleSimpleKeys.remove(this.flowLevel);
        if (key != null && key.isRequired()) {
            throw new ScannerException("while scanning a simple key", key.getMark(), "could not found expected ':'", this.reader.getMark());
        }
    }
    
    private void unwindIndent(final int col) {
        if (this.flowLevel != 0) {
            return;
        }
        while (this.indent > col) {
            final Mark mark = this.reader.getMark();
            this.indent = this.indents.pop();
            this.tokens.add(new BlockEndToken(mark, mark));
        }
    }
    
    private boolean addIndent(final int column) {
        if (this.indent < column) {
            this.indents.push(this.indent);
            this.indent = column;
            return true;
        }
        return false;
    }
    
    private void fetchStreamStart() {
        final Mark mark = this.reader.getMark();
        final Token token = new StreamStartToken(mark, mark);
        this.tokens.add(token);
    }
    
    private void fetchStreamEnd() {
        this.unwindIndent(-1);
        this.removePossibleSimpleKey();
        this.allowSimpleKey = false;
        this.possibleSimpleKeys.clear();
        final Mark mark = this.reader.getMark();
        final Token token = new StreamEndToken(mark, mark);
        this.tokens.add(token);
        this.done = true;
    }
    
    private void fetchDirective() {
        this.unwindIndent(-1);
        this.removePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = this.scanDirective();
        this.tokens.add(tok);
    }
    
    private void fetchDocumentStart() {
        this.fetchDocumentIndicator(true);
    }
    
    private void fetchDocumentEnd() {
        this.fetchDocumentIndicator(false);
    }
    
    private void fetchDocumentIndicator(final boolean isDocumentStart) {
        this.unwindIndent(-1);
        this.removePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Mark startMark = this.reader.getMark();
        this.reader.forward(3);
        final Mark endMark = this.reader.getMark();
        Token token;
        if (isDocumentStart) {
            token = new DocumentStartToken(startMark, endMark);
        }
        else {
            token = new DocumentEndToken(startMark, endMark);
        }
        this.tokens.add(token);
    }
    
    private void fetchFlowSequenceStart() {
        this.fetchFlowCollectionStart(false);
    }
    
    private void fetchFlowMappingStart() {
        this.fetchFlowCollectionStart(true);
    }
    
    private void fetchFlowCollectionStart(final boolean isMappingStart) {
        this.savePossibleSimpleKey();
        ++this.flowLevel;
        this.allowSimpleKey = true;
        final Mark startMark = this.reader.getMark();
        this.reader.forward(1);
        final Mark endMark = this.reader.getMark();
        Token token;
        if (isMappingStart) {
            token = new FlowMappingStartToken(startMark, endMark);
        }
        else {
            token = new FlowSequenceStartToken(startMark, endMark);
        }
        this.tokens.add(token);
    }
    
    private void fetchFlowSequenceEnd() {
        this.fetchFlowCollectionEnd(false);
    }
    
    private void fetchFlowMappingEnd() {
        this.fetchFlowCollectionEnd(true);
    }
    
    private void fetchFlowCollectionEnd(final boolean isMappingEnd) {
        this.removePossibleSimpleKey();
        --this.flowLevel;
        this.allowSimpleKey = false;
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final Mark endMark = this.reader.getMark();
        Token token;
        if (isMappingEnd) {
            token = new FlowMappingEndToken(startMark, endMark);
        }
        else {
            token = new FlowSequenceEndToken(startMark, endMark);
        }
        this.tokens.add(token);
    }
    
    private void fetchFlowEntry() {
        this.allowSimpleKey = true;
        this.removePossibleSimpleKey();
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final Mark endMark = this.reader.getMark();
        final Token token = new FlowEntryToken(startMark, endMark);
        this.tokens.add(token);
    }
    
    private void fetchBlockEntry() {
        if (this.flowLevel == 0) {
            if (!this.allowSimpleKey) {
                throw new ScannerException(null, null, "sequence entries are not allowed here", this.reader.getMark());
            }
            if (this.addIndent(this.reader.getColumn())) {
                final Mark mark = this.reader.getMark();
                this.tokens.add(new BlockSequenceStartToken(mark, mark));
            }
        }
        this.allowSimpleKey = true;
        this.removePossibleSimpleKey();
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final Mark endMark = this.reader.getMark();
        final Token token = new BlockEntryToken(startMark, endMark);
        this.tokens.add(token);
    }
    
    private void fetchKey() {
        if (this.flowLevel == 0) {
            if (!this.allowSimpleKey) {
                throw new ScannerException(null, null, "mapping keys are not allowed here", this.reader.getMark());
            }
            if (this.addIndent(this.reader.getColumn())) {
                final Mark mark = this.reader.getMark();
                this.tokens.add(new BlockMappingStartToken(mark, mark));
            }
        }
        this.allowSimpleKey = (this.flowLevel == 0);
        this.removePossibleSimpleKey();
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final Mark endMark = this.reader.getMark();
        final Token token = new KeyToken(startMark, endMark);
        this.tokens.add(token);
    }
    
    private void fetchValue() {
        final SimpleKey key = this.possibleSimpleKeys.remove(this.flowLevel);
        if (key != null) {
            this.tokens.add(key.getTokenNumber() - this.tokensTaken, new KeyToken(key.getMark(), key.getMark()));
            if (this.flowLevel == 0 && this.addIndent(key.getColumn())) {
                this.tokens.add(key.getTokenNumber() - this.tokensTaken, new BlockMappingStartToken(key.getMark(), key.getMark()));
            }
            this.allowSimpleKey = false;
        }
        else {
            if (this.flowLevel == 0 && !this.allowSimpleKey) {
                throw new ScannerException(null, null, "mapping values are not allowed here", this.reader.getMark());
            }
            if (this.flowLevel == 0 && this.addIndent(this.reader.getColumn())) {
                final Mark mark = this.reader.getMark();
                this.tokens.add(new BlockMappingStartToken(mark, mark));
            }
            this.allowSimpleKey = (this.flowLevel == 0);
            this.removePossibleSimpleKey();
        }
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final Mark endMark = this.reader.getMark();
        final Token token = new ValueToken(startMark, endMark);
        this.tokens.add(token);
    }
    
    private void fetchAlias() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = this.scanAnchor(false);
        this.tokens.add(tok);
    }
    
    private void fetchAnchor() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = this.scanAnchor(true);
        this.tokens.add(tok);
    }
    
    private void fetchTag() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = this.scanTag();
        this.tokens.add(tok);
    }
    
    private void fetchLiteral() {
        this.fetchBlockScalar('|');
    }
    
    private void fetchFolded() {
        this.fetchBlockScalar('>');
    }
    
    private void fetchBlockScalar(final char style) {
        this.allowSimpleKey = true;
        this.removePossibleSimpleKey();
        final Token tok = this.scanBlockScalar(style);
        this.tokens.add(tok);
    }
    
    private void fetchSingle() {
        this.fetchFlowScalar('\'');
    }
    
    private void fetchDouble() {
        this.fetchFlowScalar('\"');
    }
    
    private void fetchFlowScalar(final char style) {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = this.scanFlowScalar(style);
        this.tokens.add(tok);
    }
    
    private void fetchPlain() {
        this.savePossibleSimpleKey();
        this.allowSimpleKey = false;
        final Token tok = this.scanPlain();
        this.tokens.add(tok);
    }
    
    private boolean checkDirective() {
        return this.reader.getColumn() == 0;
    }
    
    private boolean checkDocumentStart() {
        return this.reader.getColumn() == 0 && "---".equals(this.reader.prefix(3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3));
    }
    
    private boolean checkDocumentEnd() {
        return this.reader.getColumn() == 0 && "...".equals(this.reader.prefix(3)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3));
    }
    
    private boolean checkBlockEntry() {
        return Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
    }
    
    private boolean checkKey() {
        return this.flowLevel != 0 || Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
    }
    
    private boolean checkValue() {
        return this.flowLevel != 0 || Constant.NULL_BL_T_LINEBR.has(this.reader.peek(1));
    }
    
    private boolean checkPlain() {
        final char ch = this.reader.peek();
        return Constant.NULL_BL_T_LINEBR.hasNo(ch, "-?:,[]{}#&*!|>'\"%@`") || (Constant.NULL_BL_T_LINEBR.hasNo(this.reader.peek(1)) && (ch == '-' || (this.flowLevel == 0 && "?:".indexOf(ch) != -1)));
    }
    
    private void scanToNextToken() {
        if (this.reader.getIndex() == 0 && this.reader.peek() == '\ufeff') {
            this.reader.forward();
        }
        boolean found = false;
        while (!found) {
            int ff;
            for (ff = 0; this.reader.peek(ff) == ' '; ++ff) {}
            if (ff > 0) {
                this.reader.forward(ff);
            }
            if (this.reader.peek() == '#') {
                for (ff = 0; Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(ff)); ++ff) {}
                if (ff > 0) {
                    this.reader.forward(ff);
                }
            }
            if (this.scanLineBreak().length() != 0) {
                if (this.flowLevel != 0) {
                    continue;
                }
                this.allowSimpleKey = true;
            }
            else {
                found = true;
            }
        }
    }
    
    private Token scanDirective() {
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final String name = this.scanDirectiveName(startMark);
        List<?> value = null;
        Mark endMark;
        if ("YAML".equals(name)) {
            value = this.scanYamlDirectiveValue(startMark);
            endMark = this.reader.getMark();
        }
        else if ("TAG".equals(name)) {
            value = this.scanTagDirectiveValue(startMark);
            endMark = this.reader.getMark();
        }
        else {
            endMark = this.reader.getMark();
            int ff;
            for (ff = 0; Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(ff)); ++ff) {}
            if (ff > 0) {
                this.reader.forward(ff);
            }
        }
        this.scanDirectiveIgnoredLine(startMark);
        return new DirectiveToken<Object>(name, value, startMark, endMark);
    }
    
    private String scanDirectiveName(final Mark startMark) {
        int length;
        char ch;
        for (length = 0, ch = this.reader.peek(length); Constant.ALPHA.has(ch); ch = this.reader.peek(length)) {
            ++length;
        }
        if (length == 0) {
            throw new ScannerException("while scanning a directive", startMark, "expected alphabetic or numeric character, but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        final String value = this.reader.prefixForward(length);
        ch = this.reader.peek();
        if (Constant.NULL_BL_LINEBR.hasNo(ch)) {
            throw new ScannerException("while scanning a directive", startMark, "expected alphabetic or numeric character, but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        return value;
    }
    
    private List<Integer> scanYamlDirectiveValue(final Mark startMark) {
        while (this.reader.peek() == ' ') {
            this.reader.forward();
        }
        final Integer major = this.scanYamlDirectiveNumber(startMark);
        if (this.reader.peek() != '.') {
            throw new ScannerException("while scanning a directive", startMark, "expected a digit or '.', but found " + this.reader.peek() + "(" + (int)this.reader.peek() + ")", this.reader.getMark());
        }
        this.reader.forward();
        final Integer minor = this.scanYamlDirectiveNumber(startMark);
        if (Constant.NULL_BL_LINEBR.hasNo(this.reader.peek())) {
            throw new ScannerException("while scanning a directive", startMark, "expected a digit or ' ', but found " + this.reader.peek() + "(" + (int)this.reader.peek() + ")", this.reader.getMark());
        }
        final List<Integer> result = new ArrayList<Integer>(2);
        result.add(major);
        result.add(minor);
        return result;
    }
    
    private Integer scanYamlDirectiveNumber(final Mark startMark) {
        final char ch = this.reader.peek();
        if (!Character.isDigit(ch)) {
            throw new ScannerException("while scanning a directive", startMark, "expected a digit, but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        int length;
        for (length = 0; Character.isDigit(this.reader.peek(length)); ++length) {}
        final Integer value = new Integer(this.reader.prefixForward(length));
        return value;
    }
    
    private List<String> scanTagDirectiveValue(final Mark startMark) {
        while (this.reader.peek() == ' ') {
            this.reader.forward();
        }
        final String handle = this.scanTagDirectiveHandle(startMark);
        while (this.reader.peek() == ' ') {
            this.reader.forward();
        }
        final String prefix = this.scanTagDirectivePrefix(startMark);
        final List<String> result = new ArrayList<String>(2);
        result.add(handle);
        result.add(prefix);
        return result;
    }
    
    private String scanTagDirectiveHandle(final Mark startMark) {
        final String value = this.scanTagHandle("directive", startMark);
        final char ch = this.reader.peek();
        if (ch != ' ') {
            throw new ScannerException("while scanning a directive", startMark, "expected ' ', but found " + this.reader.peek() + "(" + ch + ")", this.reader.getMark());
        }
        return value;
    }
    
    private String scanTagDirectivePrefix(final Mark startMark) {
        final String value = this.scanTagUri("directive", startMark);
        if (Constant.NULL_BL_LINEBR.hasNo(this.reader.peek())) {
            throw new ScannerException("while scanning a directive", startMark, "expected ' ', but found " + this.reader.peek() + "(" + (int)this.reader.peek() + ")", this.reader.getMark());
        }
        return value;
    }
    
    private String scanDirectiveIgnoredLine(final Mark startMark) {
        int ff;
        for (ff = 0; this.reader.peek(ff) == ' '; ++ff) {}
        if (ff > 0) {
            this.reader.forward(ff);
        }
        if (this.reader.peek() == '#') {
            for (ff = 0; Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(ff)); ++ff) {}
            this.reader.forward(ff);
        }
        final char ch = this.reader.peek();
        final String lineBreak = this.scanLineBreak();
        if (lineBreak.length() == 0 && ch != '\0') {
            throw new ScannerException("while scanning a directive", startMark, "expected a comment or a line break, but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        return lineBreak;
    }
    
    private Token scanAnchor(final boolean isAnchor) {
        final Mark startMark = this.reader.getMark();
        final char indicator = this.reader.peek();
        final String name = (indicator == '*') ? "alias" : "anchor";
        this.reader.forward();
        int length;
        char ch;
        for (length = 0, ch = this.reader.peek(length); Constant.ALPHA.has(ch); ch = this.reader.peek(length)) {
            ++length;
        }
        if (length == 0) {
            throw new ScannerException("while scanning an " + name, startMark, "expected alphabetic or numeric character, but found but found " + ch, this.reader.getMark());
        }
        final String value = this.reader.prefixForward(length);
        ch = this.reader.peek();
        if (Constant.NULL_BL_T_LINEBR.hasNo(ch, "?:,]}%@`")) {
            throw new ScannerException("while scanning an " + name, startMark, "expected alphabetic or numeric character, but found " + ch + "(" + (int)this.reader.peek() + ")", this.reader.getMark());
        }
        final Mark endMark = this.reader.getMark();
        Token tok;
        if (isAnchor) {
            tok = new AnchorToken(value, startMark, endMark);
        }
        else {
            tok = new AliasToken(value, startMark, endMark);
        }
        return tok;
    }
    
    private Token scanTag() {
        final Mark startMark = this.reader.getMark();
        char ch = this.reader.peek(1);
        String handle = null;
        String suffix = null;
        if (ch == '<') {
            this.reader.forward(2);
            suffix = this.scanTagUri("tag", startMark);
            if (this.reader.peek() != '>') {
                throw new ScannerException("while scanning a tag", startMark, "expected '>', but found '" + this.reader.peek() + "' (" + (int)this.reader.peek() + ")", this.reader.getMark());
            }
            this.reader.forward();
        }
        else if (Constant.NULL_BL_T_LINEBR.has(ch)) {
            suffix = "!";
            this.reader.forward();
        }
        else {
            int length = 1;
            boolean useHandle = false;
            while (Constant.NULL_BL_LINEBR.hasNo(ch)) {
                if (ch == '!') {
                    useHandle = true;
                    break;
                }
                ++length;
                ch = this.reader.peek(length);
            }
            handle = "!";
            if (useHandle) {
                handle = this.scanTagHandle("tag", startMark);
            }
            else {
                handle = "!";
                this.reader.forward();
            }
            suffix = this.scanTagUri("tag", startMark);
        }
        ch = this.reader.peek();
        if (Constant.NULL_BL_LINEBR.hasNo(ch)) {
            throw new ScannerException("while scanning a tag", startMark, "expected ' ', but found '" + ch + "' (" + (int)ch + ")", this.reader.getMark());
        }
        final TagTuple value = new TagTuple(handle, suffix);
        final Mark endMark = this.reader.getMark();
        return new TagToken(value, startMark, endMark);
    }
    
    private Token scanBlockScalar(final char style) {
        final boolean folded = style == '>';
        final StringBuilder chunks = new StringBuilder();
        final Mark startMark = this.reader.getMark();
        this.reader.forward();
        final Chomping chompi = this.scanBlockScalarIndicators(startMark);
        final int increment = chompi.getIncrement();
        this.scanBlockScalarIgnoredLine(startMark);
        int minIndent = this.indent + 1;
        if (minIndent < 1) {
            minIndent = 1;
        }
        String breaks = null;
        int maxIndent = 0;
        int indent = 0;
        Mark endMark;
        if (increment == -1) {
            final Object[] brme = this.scanBlockScalarIndentation();
            breaks = (String)brme[0];
            maxIndent = (int)brme[1];
            endMark = (Mark)brme[2];
            indent = Math.max(minIndent, maxIndent);
        }
        else {
            indent = minIndent + increment - 1;
            final Object[] brme = this.scanBlockScalarBreaks(indent);
            breaks = (String)brme[0];
            endMark = (Mark)brme[1];
        }
        String lineBreak = "";
        while (this.reader.getColumn() == indent && this.reader.peek() != '\0') {
            chunks.append(breaks);
            final boolean leadingNonSpace = " \t".indexOf(this.reader.peek()) == -1;
            int length;
            for (length = 0; Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(length)); ++length) {}
            chunks.append(this.reader.prefixForward(length));
            lineBreak = this.scanLineBreak();
            final Object[] brme2 = this.scanBlockScalarBreaks(indent);
            breaks = (String)brme2[0];
            endMark = (Mark)brme2[1];
            if (this.reader.getColumn() != indent || this.reader.peek() == '\0') {
                break;
            }
            if (folded && "\n".equals(lineBreak) && leadingNonSpace && " \t".indexOf(this.reader.peek()) == -1) {
                if (breaks.length() != 0) {
                    continue;
                }
                chunks.append(" ");
            }
            else {
                chunks.append(lineBreak);
            }
        }
        if (chompi.chompTailIsNotFalse()) {
            chunks.append(lineBreak);
        }
        if (chompi.chompTailIsTrue()) {
            chunks.append(breaks);
        }
        return new ScalarToken(chunks.toString(), false, startMark, endMark, style);
    }
    
    private Chomping scanBlockScalarIndicators(final Mark startMark) {
        Boolean chomping = null;
        int increment = -1;
        char ch = this.reader.peek();
        if (ch == '-' || ch == '+') {
            if (ch == '+') {
                chomping = Boolean.TRUE;
            }
            else {
                chomping = Boolean.FALSE;
            }
            this.reader.forward();
            ch = this.reader.peek();
            if (Character.isDigit(ch)) {
                increment = Integer.parseInt(String.valueOf(ch));
                if (increment == 0) {
                    throw new ScannerException("while scanning a block scalar", startMark, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark());
                }
                this.reader.forward();
            }
        }
        else if (Character.isDigit(ch)) {
            increment = Integer.parseInt(String.valueOf(ch));
            if (increment == 0) {
                throw new ScannerException("while scanning a block scalar", startMark, "expected indentation indicator in the range 1-9, but found 0", this.reader.getMark());
            }
            this.reader.forward();
            ch = this.reader.peek();
            if (ch == '-' || ch == '+') {
                if (ch == '+') {
                    chomping = Boolean.TRUE;
                }
                else {
                    chomping = Boolean.FALSE;
                }
                this.reader.forward();
            }
        }
        ch = this.reader.peek();
        if (Constant.NULL_BL_LINEBR.hasNo(ch)) {
            throw new ScannerException("while scanning a block scalar", startMark, "expected chomping or indentation indicators, but found " + ch, this.reader.getMark());
        }
        return new Chomping(chomping, increment);
    }
    
    private String scanBlockScalarIgnoredLine(final Mark startMark) {
        int ff;
        for (ff = 0; this.reader.peek(ff) == ' '; ++ff) {}
        if (ff > 0) {
            this.reader.forward(ff);
        }
        if (this.reader.peek() == '#') {
            for (ff = 0; Constant.NULL_OR_LINEBR.hasNo(this.reader.peek(ff)); ++ff) {}
            if (ff > 0) {
                this.reader.forward(ff);
            }
        }
        final char ch = this.reader.peek();
        final String lineBreak = this.scanLineBreak();
        if (lineBreak.length() == 0 && ch != '\0') {
            throw new ScannerException("while scanning a block scalar", startMark, "expected a comment or a line break, but found " + ch, this.reader.getMark());
        }
        return lineBreak;
    }
    
    private Object[] scanBlockScalarIndentation() {
        final StringBuilder chunks = new StringBuilder();
        int maxIndent = 0;
        Mark endMark = this.reader.getMark();
        while (Constant.LINEBR.has(this.reader.peek(), " \r")) {
            if (this.reader.peek() != ' ') {
                chunks.append(this.scanLineBreak());
                endMark = this.reader.getMark();
            }
            else {
                this.reader.forward();
                if (this.reader.getColumn() <= maxIndent) {
                    continue;
                }
                maxIndent = this.reader.getColumn();
            }
        }
        return new Object[] { chunks.toString(), maxIndent, endMark };
    }
    
    private Object[] scanBlockScalarBreaks(final int indent) {
        final StringBuilder chunks = new StringBuilder();
        Mark endMark = this.reader.getMark();
        int ff = 0;
        for (int col = this.reader.getColumn(); col < indent && this.reader.peek(ff) == ' '; ++ff, ++col) {}
        if (ff > 0) {
            this.reader.forward(ff);
        }
        String lineBreak = null;
        while ((lineBreak = this.scanLineBreak()).length() != 0) {
            chunks.append(lineBreak);
            endMark = this.reader.getMark();
            ff = 0;
            for (int col = this.reader.getColumn(); col < indent && this.reader.peek(ff) == ' '; ++ff, ++col) {}
            if (ff > 0) {
                this.reader.forward(ff);
            }
        }
        return new Object[] { chunks.toString(), endMark };
    }
    
    private Token scanFlowScalar(final char style) {
        final boolean _double = style == '\"';
        final StringBuilder chunks = new StringBuilder();
        final Mark startMark = this.reader.getMark();
        final char quote = this.reader.peek();
        this.reader.forward();
        chunks.append(this.scanFlowScalarNonSpaces(_double, startMark));
        while (this.reader.peek() != quote) {
            chunks.append(this.scanFlowScalarSpaces(startMark));
            chunks.append(this.scanFlowScalarNonSpaces(_double, startMark));
        }
        this.reader.forward();
        final Mark endMark = this.reader.getMark();
        return new ScalarToken(chunks.toString(), false, startMark, endMark, style);
    }
    
    private String scanFlowScalarNonSpaces(final boolean _double, final Mark startMark) {
        final StringBuilder chunks = new StringBuilder();
        while (true) {
            int length;
            for (length = 0; Constant.NULL_BL_T_LINEBR.hasNo(this.reader.peek(length), "'\"\\"); ++length) {}
            if (length != 0) {
                chunks.append(this.reader.prefixForward(length));
            }
            char ch = this.reader.peek();
            if (!_double && ch == '\'' && this.reader.peek(1) == '\'') {
                chunks.append("'");
                this.reader.forward(2);
            }
            else if ((_double && ch == '\'') || (!_double && "\"\\".indexOf(ch) != -1)) {
                chunks.append(ch);
                this.reader.forward();
            }
            else {
                if (!_double || ch != '\\') {
                    return chunks.toString();
                }
                this.reader.forward();
                ch = this.reader.peek();
                if (ScannerImpl.ESCAPE_REPLACEMENTS.containsKey(new Character(ch))) {
                    chunks.append(ScannerImpl.ESCAPE_REPLACEMENTS.get(new Character(ch)));
                    this.reader.forward();
                }
                else if (ScannerImpl.ESCAPE_CODES.containsKey(new Character(ch))) {
                    length = ScannerImpl.ESCAPE_CODES.get(new Character(ch));
                    this.reader.forward();
                    final String hex = this.reader.prefix(length);
                    if (ScannerImpl.NOT_HEXA.matcher(hex).find()) {
                        throw new ScannerException("while scanning a double-quoted scalar", startMark, "expected escape sequence of " + length + " hexadecimal numbers, but found: " + hex, this.reader.getMark());
                    }
                    final char unicode = (char)Integer.parseInt(hex, 16);
                    chunks.append(unicode);
                    this.reader.forward(length);
                }
                else {
                    if (this.scanLineBreak().length() == 0) {
                        throw new ScannerException("while scanning a double-quoted scalar", startMark, "found unknown escape character " + ch + "(" + (int)ch + ")", this.reader.getMark());
                    }
                    chunks.append(this.scanFlowScalarBreaks(startMark));
                }
            }
        }
    }
    
    private String scanFlowScalarSpaces(final Mark startMark) {
        final StringBuilder chunks = new StringBuilder();
        int length;
        for (length = 0; " \t".indexOf(this.reader.peek(length)) != -1; ++length) {}
        final String whitespaces = this.reader.prefixForward(length);
        final char ch = this.reader.peek();
        if (ch == '\0') {
            throw new ScannerException("while scanning a quoted scalar", startMark, "found unexpected end of stream", this.reader.getMark());
        }
        final String lineBreak = this.scanLineBreak();
        if (lineBreak.length() != 0) {
            final String breaks = this.scanFlowScalarBreaks(startMark);
            if (!"\n".equals(lineBreak)) {
                chunks.append(lineBreak);
            }
            else if (breaks.length() == 0) {
                chunks.append(" ");
            }
            chunks.append(breaks);
        }
        else {
            chunks.append(whitespaces);
        }
        return chunks.toString();
    }
    
    private String scanFlowScalarBreaks(final Mark startMark) {
        final StringBuilder chunks = new StringBuilder();
        while (true) {
            final String prefix = this.reader.prefix(3);
            if (("---".equals(prefix) || "...".equals(prefix)) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3))) {
                throw new ScannerException("while scanning a quoted scalar", startMark, "found unexpected document separator", this.reader.getMark());
            }
            while (" \t".indexOf(this.reader.peek()) != -1) {
                this.reader.forward();
            }
            final String lineBreak = this.scanLineBreak();
            if (lineBreak.length() == 0) {
                return chunks.toString();
            }
            chunks.append(lineBreak);
        }
    }
    
    private Token scanPlain() {
        final StringBuilder chunks = new StringBuilder();
        Mark endMark;
        final Mark startMark = endMark = this.reader.getMark();
        final int indent = this.indent + 1;
        String spaces = "";
        do {
            int length = 0;
            if (this.reader.peek() == '#') {
                break;
            }
            char ch;
            while (true) {
                ch = this.reader.peek(length);
                if (Constant.NULL_BL_T_LINEBR.has(ch) || (this.flowLevel == 0 && ch == ':' && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(length + 1))) || (this.flowLevel != 0 && ",:?[]{}".indexOf(ch) != -1)) {
                    break;
                }
                ++length;
            }
            if (this.flowLevel != 0 && ch == ':' && Constant.NULL_BL_T_LINEBR.hasNo(this.reader.peek(length + 1), ",[]{}")) {
                this.reader.forward(length);
                throw new ScannerException("while scanning a plain scalar", startMark, "found unexpected ':'", this.reader.getMark(), "Please check http://pyyaml.org/wiki/YAMLColonInFlowContext for details.");
            }
            if (length == 0) {
                break;
            }
            this.allowSimpleKey = false;
            chunks.append(spaces);
            chunks.append(this.reader.prefixForward(length));
            endMark = this.reader.getMark();
            spaces = this.scanPlainSpaces();
            if (spaces.length() == 0 || this.reader.peek() == '#') {
                break;
            }
        } while (this.flowLevel != 0 || this.reader.getColumn() >= indent);
        return new ScalarToken(chunks.toString(), startMark, endMark, true);
    }
    
    private String scanPlainSpaces() {
        int length;
        for (length = 0; this.reader.peek(length) == ' '; ++length) {}
        final String whitespaces = this.reader.prefixForward(length);
        final String lineBreak = this.scanLineBreak();
        if (lineBreak.length() == 0) {
            return whitespaces;
        }
        this.allowSimpleKey = true;
        String prefix = this.reader.prefix(3);
        if ("---".equals(prefix) || ("...".equals(prefix) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3)))) {
            return "";
        }
        final StringBuilder breaks = new StringBuilder();
        while (true) {
            if (this.reader.peek() == ' ') {
                this.reader.forward();
            }
            else {
                final String lb = this.scanLineBreak();
                if (lb.length() != 0) {
                    breaks.append(lb);
                    prefix = this.reader.prefix(3);
                    if ("---".equals(prefix) || ("...".equals(prefix) && Constant.NULL_BL_T_LINEBR.has(this.reader.peek(3)))) {
                        return "";
                    }
                    continue;
                }
                else {
                    if (!"\n".equals(lineBreak)) {
                        return lineBreak + (Object)breaks;
                    }
                    if (breaks.length() == 0) {
                        return " ";
                    }
                    return breaks.toString();
                }
            }
        }
    }
    
    private String scanTagHandle(final String name, final Mark startMark) {
        char ch = this.reader.peek();
        if (ch != '!') {
            throw new ScannerException("while scanning a " + name, startMark, "expected '!', but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        int length = 1;
        ch = this.reader.peek(length);
        if (ch != ' ') {
            while (Constant.ALPHA.has(ch)) {
                ++length;
                ch = this.reader.peek(length);
            }
            if (ch != '!') {
                this.reader.forward(length);
                throw new ScannerException("while scanning a " + name, startMark, "expected '!', but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
            }
            ++length;
        }
        final String value = this.reader.prefixForward(length);
        return value;
    }
    
    private String scanTagUri(final String name, final Mark startMark) {
        final StringBuilder chunks = new StringBuilder();
        int length;
        char ch;
        for (length = 0, ch = this.reader.peek(length); Constant.URI_CHARS.has(ch); ch = this.reader.peek(length)) {
            if (ch == '%') {
                chunks.append(this.reader.prefixForward(length));
                length = 0;
                chunks.append(this.scanUriEscapes(name, startMark));
            }
            else {
                ++length;
            }
        }
        if (length != 0) {
            chunks.append(this.reader.prefixForward(length));
            length = 0;
        }
        if (chunks.length() == 0) {
            throw new ScannerException("while scanning a " + name, startMark, "expected URI, but found " + ch + "(" + (int)ch + ")", this.reader.getMark());
        }
        return chunks.toString();
    }
    
    private String scanUriEscapes(final String name, final Mark startMark) {
        final Mark beginningMark = this.reader.getMark();
        final ByteBuffer buff = ByteBuffer.allocate(256);
        while (this.reader.peek() == '%') {
            this.reader.forward();
            try {
                final byte code = (byte)Integer.parseInt(this.reader.prefix(2), 16);
                buff.put(code);
            }
            catch (NumberFormatException nfe) {
                throw new ScannerException("while scanning a " + name, startMark, "expected URI escape sequence of 2 hexadecimal numbers, but found " + this.reader.peek() + "(" + (int)this.reader.peek() + ") and " + this.reader.peek(1) + "(" + (int)this.reader.peek(1) + ")", this.reader.getMark());
            }
            this.reader.forward(2);
        }
        buff.flip();
        try {
            return UriEncoder.decode(buff);
        }
        catch (CharacterCodingException e) {
            throw new ScannerException("while scanning a " + name, startMark, "expected URI in UTF-8: " + e.getMessage(), beginningMark);
        }
    }
    
    private String scanLineBreak() {
        final char ch = this.reader.peek();
        if (ch == '\r' || ch == '\n' || ch == '\u0085') {
            if (ch == '\r' && '\n' == this.reader.peek(1)) {
                this.reader.forward(2);
            }
            else {
                this.reader.forward();
            }
            return "\n";
        }
        if (ch == '\u2028' || ch == '\u2029') {
            this.reader.forward();
            return String.valueOf(ch);
        }
        return "";
    }
    
    static {
        NOT_HEXA = Pattern.compile("[^0-9A-Fa-f]");
        ESCAPE_REPLACEMENTS = new HashMap<Character, String>();
        ESCAPE_CODES = new HashMap<Character, Integer>();
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('0'), "\u0000");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('a'), "\u0007");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('b'), "\b");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('t'), "\t");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('n'), "\n");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('v'), "\u000b");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('f'), "\f");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('r'), "\r");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('e'), "\u001b");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character(' '), " ");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('\"'), "\"");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('\\'), "\\");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('N'), "\u0085");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('_'), " ");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('L'), "\u2028");
        ScannerImpl.ESCAPE_REPLACEMENTS.put(new Character('P'), "\u2029");
        ScannerImpl.ESCAPE_CODES.put(new Character('x'), 2);
        ScannerImpl.ESCAPE_CODES.put(new Character('u'), 4);
        ScannerImpl.ESCAPE_CODES.put(new Character('U'), 8);
    }
    
    private class Chomping
    {
        private final Boolean value;
        private final int increment;
        
        public Chomping(final Boolean value, final int increment) {
            this.value = value;
            this.increment = increment;
        }
        
        public boolean chompTailIsNotFalse() {
            return this.value == null || this.value;
        }
        
        public boolean chompTailIsTrue() {
            return this.value != null && this.value;
        }
        
        public int getIncrement() {
            return this.increment;
        }
    }
}
