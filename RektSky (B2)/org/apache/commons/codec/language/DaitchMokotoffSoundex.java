package org.apache.commons.codec.language;

import org.apache.commons.codec.*;
import java.io.*;
import java.util.*;

public class DaitchMokotoffSoundex implements StringEncoder
{
    private static final String COMMENT = "//";
    private static final String DOUBLE_QUOTE = "\"";
    private static final String MULTILINE_COMMENT_END = "*/";
    private static final String MULTILINE_COMMENT_START = "/*";
    private static final String RESOURCE_FILE = "org/apache/commons/codec/language/dmrules.txt";
    private static final int MAX_LENGTH = 6;
    private static final Map<Character, List<Rule>> RULES;
    private static final Map<Character, Character> FOLDINGS;
    private final boolean folding;
    
    private static void parseRules(final Scanner scanner, final String location, final Map<Character, List<Rule>> ruleMapping, final Map<Character, Character> asciiFoldings) {
        int currentLine = 0;
        boolean inMultilineComment = false;
        while (scanner.hasNextLine()) {
            ++currentLine;
            String line;
            final String rawLine = line = scanner.nextLine();
            if (inMultilineComment) {
                if (!line.endsWith("*/")) {
                    continue;
                }
                inMultilineComment = false;
            }
            else if (line.startsWith("/*")) {
                inMultilineComment = true;
            }
            else {
                final int cmtI = line.indexOf("//");
                if (cmtI >= 0) {
                    line = line.substring(0, cmtI);
                }
                line = line.trim();
                if (line.length() == 0) {
                    continue;
                }
                if (line.contains("=")) {
                    final String[] parts = line.split("=");
                    if (parts.length != 2) {
                        throw new IllegalArgumentException("Malformed folding statement split into " + parts.length + " parts: " + rawLine + " in " + location);
                    }
                    final String leftCharacter = parts[0];
                    final String rightCharacter = parts[1];
                    if (leftCharacter.length() != 1 || rightCharacter.length() != 1) {
                        throw new IllegalArgumentException("Malformed folding statement - patterns are not single characters: " + rawLine + " in " + location);
                    }
                    asciiFoldings.put(leftCharacter.charAt(0), rightCharacter.charAt(0));
                }
                else {
                    final String[] parts = line.split("\\s+");
                    if (parts.length != 4) {
                        throw new IllegalArgumentException("Malformed rule statement split into " + parts.length + " parts: " + rawLine + " in " + location);
                    }
                    try {
                        final String pattern = stripQuotes(parts[0]);
                        final String replacement1 = stripQuotes(parts[1]);
                        final String replacement2 = stripQuotes(parts[2]);
                        final String replacement3 = stripQuotes(parts[3]);
                        final Rule r = new Rule(pattern, replacement1, replacement2, replacement3);
                        final char patternKey = r.pattern.charAt(0);
                        List<Rule> rules = ruleMapping.get(patternKey);
                        if (rules == null) {
                            rules = new ArrayList<Rule>();
                            ruleMapping.put(patternKey, rules);
                        }
                        rules.add(r);
                    }
                    catch (IllegalArgumentException e) {
                        throw new IllegalStateException("Problem parsing line '" + currentLine + "' in " + location, e);
                    }
                }
            }
        }
    }
    
    private static String stripQuotes(String str) {
        if (str.startsWith("\"")) {
            str = str.substring(1);
        }
        if (str.endsWith("\"")) {
            str = str.substring(0, str.length() - 1);
        }
        return str;
    }
    
    public DaitchMokotoffSoundex() {
        this(true);
    }
    
    public DaitchMokotoffSoundex(final boolean folding) {
        this.folding = folding;
    }
    
    private String cleanup(final String input) {
        final StringBuilder sb = new StringBuilder();
        for (char ch : input.toCharArray()) {
            if (!Character.isWhitespace(ch)) {
                ch = Character.toLowerCase(ch);
                if (this.folding && DaitchMokotoffSoundex.FOLDINGS.containsKey(ch)) {
                    ch = DaitchMokotoffSoundex.FOLDINGS.get(ch);
                }
                sb.append(ch);
            }
        }
        return sb.toString();
    }
    
    @Override
    public Object encode(final Object obj) throws EncoderException {
        if (!(obj instanceof String)) {
            throw new EncoderException("Parameter supplied to DaitchMokotoffSoundex encode is not of type java.lang.String");
        }
        return this.encode((String)obj);
    }
    
    @Override
    public String encode(final String source) {
        if (source == null) {
            return null;
        }
        return this.soundex(source, false)[0];
    }
    
    public String soundex(final String source) {
        final String[] branches = this.soundex(source, true);
        final StringBuilder sb = new StringBuilder();
        int index = 0;
        for (final String branch : branches) {
            sb.append(branch);
            if (++index < branches.length) {
                sb.append('|');
            }
        }
        return sb.toString();
    }
    
    private String[] soundex(final String source, final boolean branching) {
        if (source == null) {
            return null;
        }
        final String input = this.cleanup(source);
        final Set<Branch> currentBranches = new LinkedHashSet<Branch>();
        currentBranches.add(new Branch());
        char lastChar = '\0';
        for (int index = 0; index < input.length(); ++index) {
            final char ch = input.charAt(index);
            if (!Character.isWhitespace(ch)) {
                final String inputContext = input.substring(index);
                final List<Rule> rules = DaitchMokotoffSoundex.RULES.get(ch);
                if (rules != null) {
                    final List<Branch> nextBranches = branching ? new ArrayList<Branch>() : Collections.EMPTY_LIST;
                    for (final Rule rule : rules) {
                        if (rule.matches(inputContext)) {
                            if (branching) {
                                nextBranches.clear();
                            }
                            final String[] replacements = rule.getReplacements(inputContext, lastChar == '\0');
                            final boolean branchingRequired = replacements.length > 1 && branching;
                            for (final Branch branch : currentBranches) {
                                for (final String nextReplacement : replacements) {
                                    final Branch nextBranch = branchingRequired ? branch.createBranch() : branch;
                                    final boolean force = (lastChar == 'm' && ch == 'n') || (lastChar == 'n' && ch == 'm');
                                    nextBranch.processNextReplacement(nextReplacement, force);
                                    if (!branching) {
                                        break;
                                    }
                                    nextBranches.add(nextBranch);
                                }
                            }
                            if (branching) {
                                currentBranches.clear();
                                currentBranches.addAll(nextBranches);
                            }
                            index += rule.getPatternLength() - 1;
                            break;
                        }
                    }
                    lastChar = ch;
                }
            }
        }
        final String[] result = new String[currentBranches.size()];
        int index2 = 0;
        for (final Branch branch2 : currentBranches) {
            branch2.finish();
            result[index2++] = branch2.toString();
        }
        return result;
    }
    
    static {
        RULES = new HashMap<Character, List<Rule>>();
        FOLDINGS = new HashMap<Character, Character>();
        final InputStream rulesIS = DaitchMokotoffSoundex.class.getClassLoader().getResourceAsStream("org/apache/commons/codec/language/dmrules.txt");
        if (rulesIS == null) {
            throw new IllegalArgumentException("Unable to load resource: org/apache/commons/codec/language/dmrules.txt");
        }
        final Scanner scanner = new Scanner(rulesIS, "UTF-8");
        try {
            parseRules(scanner, "org/apache/commons/codec/language/dmrules.txt", DaitchMokotoffSoundex.RULES, DaitchMokotoffSoundex.FOLDINGS);
        }
        finally {
            scanner.close();
        }
        for (final Map.Entry<Character, List<Rule>> rule : DaitchMokotoffSoundex.RULES.entrySet()) {
            final List<Rule> ruleList = rule.getValue();
            Collections.sort(ruleList, new Comparator<Rule>() {
                @Override
                public int compare(final Rule rule1, final Rule rule2) {
                    return rule2.getPatternLength() - rule1.getPatternLength();
                }
            });
        }
    }
    
    private static final class Branch
    {
        private final StringBuilder builder;
        private String cachedString;
        private String lastReplacement;
        
        private Branch() {
            this.builder = new StringBuilder();
            this.lastReplacement = null;
            this.cachedString = null;
        }
        
        public Branch createBranch() {
            final Branch branch = new Branch();
            branch.builder.append(this.toString());
            branch.lastReplacement = this.lastReplacement;
            return branch;
        }
        
        @Override
        public boolean equals(final Object other) {
            return this == other || (other instanceof Branch && this.toString().equals(((Branch)other).toString()));
        }
        
        public void finish() {
            while (this.builder.length() < 6) {
                this.builder.append('0');
                this.cachedString = null;
            }
        }
        
        @Override
        public int hashCode() {
            return this.toString().hashCode();
        }
        
        public void processNextReplacement(final String replacement, final boolean forceAppend) {
            final boolean append = this.lastReplacement == null || !this.lastReplacement.endsWith(replacement) || forceAppend;
            if (append && this.builder.length() < 6) {
                this.builder.append(replacement);
                if (this.builder.length() > 6) {
                    this.builder.delete(6, this.builder.length());
                }
                this.cachedString = null;
            }
            this.lastReplacement = replacement;
        }
        
        @Override
        public String toString() {
            if (this.cachedString == null) {
                this.cachedString = this.builder.toString();
            }
            return this.cachedString;
        }
    }
    
    private static final class Rule
    {
        private final String pattern;
        private final String[] replacementAtStart;
        private final String[] replacementBeforeVowel;
        private final String[] replacementDefault;
        
        protected Rule(final String pattern, final String replacementAtStart, final String replacementBeforeVowel, final String replacementDefault) {
            this.pattern = pattern;
            this.replacementAtStart = replacementAtStart.split("\\|");
            this.replacementBeforeVowel = replacementBeforeVowel.split("\\|");
            this.replacementDefault = replacementDefault.split("\\|");
        }
        
        public int getPatternLength() {
            return this.pattern.length();
        }
        
        public String[] getReplacements(final String context, final boolean atStart) {
            if (atStart) {
                return this.replacementAtStart;
            }
            final int nextIndex = this.getPatternLength();
            final boolean nextCharIsVowel = nextIndex < context.length() && this.isVowel(context.charAt(nextIndex));
            if (nextCharIsVowel) {
                return this.replacementBeforeVowel;
            }
            return this.replacementDefault;
        }
        
        private boolean isVowel(final char ch) {
            return ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u';
        }
        
        public boolean matches(final String context) {
            return context.startsWith(this.pattern);
        }
        
        @Override
        public String toString() {
            return String.format("%s=(%s,%s,%s)", this.pattern, Arrays.asList(this.replacementAtStart), Arrays.asList(this.replacementBeforeVowel), Arrays.asList(this.replacementDefault));
        }
    }
}
