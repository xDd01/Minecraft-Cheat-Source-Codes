package shadersmod.client;

import java.util.regex.*;
import optifine.*;
import java.util.*;

public class ShaderOptionVariable extends ShaderOption
{
    private static final Pattern PATTERN_VARIABLE;
    
    public ShaderOptionVariable(final String name, final String description, final String value, final String[] values, final String path) {
        super(name, description, value, values, value, path);
        this.setVisible(this.getValues().length > 1);
    }
    
    public static ShaderOption parseOption(final String line, String path) {
        final Matcher m = ShaderOptionVariable.PATTERN_VARIABLE.matcher(line);
        if (!m.matches()) {
            return null;
        }
        final String name = m.group(1);
        final String value = m.group(2);
        String description = m.group(3);
        final String vals = StrUtils.getSegment(description, "[", "]");
        if (vals != null && vals.length() > 0) {
            description = description.replace(vals, "").trim();
        }
        final String[] values = parseValues(value, vals);
        if (name != null && name.length() > 0) {
            path = StrUtils.removePrefix(path, "/shaders/");
            final ShaderOptionVariable so = new ShaderOptionVariable(name, description, value, values, path);
            return so;
        }
        return null;
    }
    
    public static String[] parseValues(final String value, String valuesStr) {
        final String[] values = { value };
        if (valuesStr == null) {
            return values;
        }
        valuesStr = valuesStr.trim();
        valuesStr = StrUtils.removePrefix(valuesStr, "[");
        valuesStr = StrUtils.removeSuffix(valuesStr, "]");
        valuesStr = valuesStr.trim();
        if (valuesStr.length() <= 0) {
            return values;
        }
        String[] parts = Config.tokenize(valuesStr, " ");
        if (parts.length <= 0) {
            return values;
        }
        if (!Arrays.asList(parts).contains(value)) {
            parts = (String[])Config.addObjectToArray(parts, value, 0);
        }
        return parts;
    }
    
    @Override
    public String getSourceLine() {
        return "#define " + this.getName() + " " + this.getValue() + " // Shader option " + this.getValue();
    }
    
    @Override
    public String getValueColor(final String val) {
        return "§a";
    }
    
    @Override
    public boolean matchesLine(final String line) {
        final Matcher m = ShaderOptionVariable.PATTERN_VARIABLE.matcher(line);
        if (!m.matches()) {
            return false;
        }
        final String defName = m.group(1);
        return defName.matches(this.getName());
    }
    
    static {
        PATTERN_VARIABLE = Pattern.compile("^\\s*#define\\s+([A-Za-z0-9_]+)\\s+(-?[0-9\\.]*)F?f?\\s*(//.*)?$");
    }
}
