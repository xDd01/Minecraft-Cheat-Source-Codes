package org.yaml.snakeyaml;

import org.yaml.snakeyaml.nodes.*;
import java.util.*;
import org.yaml.snakeyaml.error.*;
import org.yaml.snakeyaml.emitter.*;

public class DumperOptions
{
    private ScalarStyle defaultStyle;
    private FlowStyle defaultFlowStyle;
    private boolean canonical;
    private boolean allowUnicode;
    private boolean allowReadOnlyProperties;
    private int indent;
    private int bestWidth;
    private LineBreak lineBreak;
    private boolean explicitStart;
    private boolean explicitEnd;
    private Tag explicitRoot;
    private Version version;
    private Map<String, String> tags;
    private Boolean prettyFlow;
    
    public DumperOptions() {
        this.defaultStyle = ScalarStyle.PLAIN;
        this.defaultFlowStyle = FlowStyle.AUTO;
        this.canonical = false;
        this.allowUnicode = true;
        this.allowReadOnlyProperties = false;
        this.indent = 2;
        this.bestWidth = 80;
        this.lineBreak = LineBreak.UNIX;
        this.explicitStart = false;
        this.explicitEnd = false;
        this.explicitRoot = null;
        this.version = null;
        this.tags = null;
        this.prettyFlow = false;
    }
    
    public boolean isAllowUnicode() {
        return this.allowUnicode;
    }
    
    public void setAllowUnicode(final boolean allowUnicode) {
        this.allowUnicode = allowUnicode;
    }
    
    public ScalarStyle getDefaultScalarStyle() {
        return this.defaultStyle;
    }
    
    public void setDefaultScalarStyle(final ScalarStyle defaultStyle) {
        if (defaultStyle == null) {
            throw new NullPointerException("Use ScalarStyle enum.");
        }
        this.defaultStyle = defaultStyle;
    }
    
    public void setIndent(final int indent) {
        if (indent < 1) {
            throw new YAMLException("Indent must be at least 1");
        }
        if (indent > 10) {
            throw new YAMLException("Indent must be at most 10");
        }
        this.indent = indent;
    }
    
    public int getIndent() {
        return this.indent;
    }
    
    public void setVersion(final Version version) {
        this.version = version;
    }
    
    public Version getVersion() {
        return this.version;
    }
    
    public void setCanonical(final boolean canonical) {
        this.canonical = canonical;
    }
    
    public boolean isCanonical() {
        return this.canonical;
    }
    
    public void setPrettyFlow(final boolean prettyFlow) {
        this.prettyFlow = prettyFlow;
    }
    
    public boolean isPrettyFlow() {
        return this.prettyFlow;
    }
    
    public void setWidth(final int bestWidth) {
        this.bestWidth = bestWidth;
    }
    
    public int getWidth() {
        return this.bestWidth;
    }
    
    public LineBreak getLineBreak() {
        return this.lineBreak;
    }
    
    public void setDefaultFlowStyle(final FlowStyle defaultFlowStyle) {
        if (defaultFlowStyle == null) {
            throw new NullPointerException("Use FlowStyle enum.");
        }
        this.defaultFlowStyle = defaultFlowStyle;
    }
    
    public FlowStyle getDefaultFlowStyle() {
        return this.defaultFlowStyle;
    }
    
    public Tag getExplicitRoot() {
        return this.explicitRoot;
    }
    
    @Deprecated
    public void setExplicitRoot(final String expRoot) {
        this.setExplicitRoot(new Tag(expRoot));
    }
    
    public void setExplicitRoot(final Tag expRoot) {
        if (expRoot == null) {
            throw new NullPointerException("Root tag must be specified.");
        }
        this.explicitRoot = expRoot;
    }
    
    public void setLineBreak(final LineBreak lineBreak) {
        if (lineBreak == null) {
            throw new NullPointerException("Specify line break.");
        }
        this.lineBreak = lineBreak;
    }
    
    public boolean isExplicitStart() {
        return this.explicitStart;
    }
    
    public void setExplicitStart(final boolean explicitStart) {
        this.explicitStart = explicitStart;
    }
    
    public boolean isExplicitEnd() {
        return this.explicitEnd;
    }
    
    public void setExplicitEnd(final boolean explicitEnd) {
        this.explicitEnd = explicitEnd;
    }
    
    public Map<String, String> getTags() {
        return this.tags;
    }
    
    public void setTags(final Map<String, String> tags) {
        this.tags = tags;
    }
    
    public ScalarStyle calculateScalarStyle(final ScalarAnalysis analysis, final ScalarStyle style) {
        return style;
    }
    
    public boolean isAllowReadOnlyProperties() {
        return this.allowReadOnlyProperties;
    }
    
    public void setAllowReadOnlyProperties(final boolean allowReadOnlyProperties) {
        this.allowReadOnlyProperties = allowReadOnlyProperties;
    }
    
    public enum ScalarStyle
    {
        DOUBLE_QUOTED(new Character('\"')), 
        SINGLE_QUOTED(new Character('\'')), 
        LITERAL(new Character('|')), 
        FOLDED(new Character('>')), 
        PLAIN((Character)null);
        
        private Character styleChar;
        
        private ScalarStyle(final Character style) {
            this.styleChar = style;
        }
        
        public Character getChar() {
            return this.styleChar;
        }
        
        @Override
        public String toString() {
            return "Scalar style: '" + this.styleChar + "'";
        }
        
        public static ScalarStyle createStyle(final Character style) {
            if (style == null) {
                return ScalarStyle.PLAIN;
            }
            switch ((char)style) {
                case '\"': {
                    return ScalarStyle.DOUBLE_QUOTED;
                }
                case '\'': {
                    return ScalarStyle.SINGLE_QUOTED;
                }
                case '|': {
                    return ScalarStyle.LITERAL;
                }
                case '>': {
                    return ScalarStyle.FOLDED;
                }
                default: {
                    throw new YAMLException("Unknown scalar style character: " + style);
                }
            }
        }
    }
    
    public enum FlowStyle
    {
        FLOW(Boolean.TRUE), 
        BLOCK(Boolean.FALSE), 
        AUTO((Boolean)null);
        
        private Boolean styleBoolean;
        
        private FlowStyle(final Boolean flowStyle) {
            this.styleBoolean = flowStyle;
        }
        
        public Boolean getStyleBoolean() {
            return this.styleBoolean;
        }
        
        @Override
        public String toString() {
            return "Flow style: '" + this.styleBoolean + "'";
        }
    }
    
    public enum LineBreak
    {
        WIN("\r\n"), 
        MAC("\r"), 
        UNIX("\n");
        
        private String lineBreak;
        
        private LineBreak(final String lineBreak) {
            this.lineBreak = lineBreak;
        }
        
        public String getString() {
            return this.lineBreak;
        }
        
        @Override
        public String toString() {
            return "Line break: " + this.name();
        }
        
        public static LineBreak getPlatformLineBreak() {
            final String platformLineBreak = System.getProperty("line.separator");
            for (final LineBreak lb : values()) {
                if (lb.lineBreak.equals(platformLineBreak)) {
                    return lb;
                }
            }
            return LineBreak.UNIX;
        }
    }
    
    public enum Version
    {
        V1_0(new Integer[] { 1, 0 }), 
        V1_1(new Integer[] { 1, 1 });
        
        private Integer[] version;
        
        private Version(final Integer[] version) {
            this.version = version;
        }
        
        public Integer[] getArray() {
            return this.version;
        }
        
        @Override
        public String toString() {
            return "Version: " + this.version[0] + "." + this.version[1];
        }
    }
}
