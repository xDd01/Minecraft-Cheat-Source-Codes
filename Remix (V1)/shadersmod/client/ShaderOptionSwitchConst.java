package shadersmod.client;

import optifine.*;
import java.util.regex.*;

public class ShaderOptionSwitchConst extends ShaderOptionSwitch
{
    private static final Pattern PATTERN_CONST;
    
    public ShaderOptionSwitchConst(final String name, final String description, final String value, final String path) {
        super(name, description, value, path);
    }
    
    public static ShaderOption parseOption(final String line, String path) {
        final Matcher m = ShaderOptionSwitchConst.PATTERN_CONST.matcher(line);
        if (!m.matches()) {
            return null;
        }
        final String name = m.group(1);
        final String value = m.group(2);
        final String description = m.group(3);
        if (name != null && name.length() > 0) {
            path = StrUtils.removePrefix(path, "/shaders/");
            final ShaderOptionSwitchConst so = new ShaderOptionSwitchConst(name, description, value, path);
            so.setVisible(false);
            return so;
        }
        return null;
    }
    
    @Override
    public String getSourceLine() {
        return "const bool " + this.getName() + " = " + this.getValue() + "; // Shader option " + this.getValue();
    }
    
    @Override
    public boolean matchesLine(final String line) {
        final Matcher m = ShaderOptionSwitchConst.PATTERN_CONST.matcher(line);
        if (!m.matches()) {
            return false;
        }
        final String defName = m.group(1);
        return defName.matches(this.getName());
    }
    
    @Override
    public boolean checkUsed() {
        return false;
    }
    
    static {
        PATTERN_CONST = Pattern.compile("^\\s*const\\s*bool\\s*([A-Za-z0-9_]+)\\s*=\\s*(true|false)\\s*;\\s*(//.*)?$");
    }
}
