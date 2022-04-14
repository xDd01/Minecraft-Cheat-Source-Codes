package shadersmod.client;

import java.util.regex.*;
import optifine.*;

public class ShaderOptionSwitch extends ShaderOption
{
    private static final Pattern PATTERN_DEFINE;
    private static final Pattern PATTERN_IFDEF;
    
    public ShaderOptionSwitch(final String name, final String description, final String value, final String path) {
        super(name, description, value, new String[] { "true", "false" }, value, path);
    }
    
    public static ShaderOption parseOption(final String line, String path) {
        final Matcher m = ShaderOptionSwitch.PATTERN_DEFINE.matcher(line);
        if (!m.matches()) {
            return null;
        }
        final String comment = m.group(1);
        final String name = m.group(2);
        final String description = m.group(3);
        if (name != null && name.length() > 0) {
            final boolean commented = Config.equals(comment, "//");
            final boolean enabled = !commented;
            path = StrUtils.removePrefix(path, "/shaders/");
            final ShaderOptionSwitch so = new ShaderOptionSwitch(name, description, String.valueOf(enabled), path);
            return so;
        }
        return null;
    }
    
    public static boolean isTrue(final String val) {
        return Boolean.valueOf(val);
    }
    
    @Override
    public String getSourceLine() {
        return isTrue(this.getValue()) ? ("#define " + this.getName() + " // Shader option ON") : ("//#define " + this.getName() + " // Shader option OFF");
    }
    
    @Override
    public String getValueText(final String val) {
        return isTrue(val) ? Lang.getOn() : Lang.getOff();
    }
    
    @Override
    public String getValueColor(final String val) {
        return isTrue(val) ? "§a" : "§c";
    }
    
    @Override
    public boolean matchesLine(final String line) {
        final Matcher m = ShaderOptionSwitch.PATTERN_DEFINE.matcher(line);
        if (!m.matches()) {
            return false;
        }
        final String defName = m.group(2);
        return defName.matches(this.getName());
    }
    
    @Override
    public boolean checkUsed() {
        return true;
    }
    
    @Override
    public boolean isUsedInLine(final String line) {
        final Matcher mif = ShaderOptionSwitch.PATTERN_IFDEF.matcher(line);
        if (mif.matches()) {
            final String name = mif.group(2);
            if (name.equals(this.getName())) {
                return true;
            }
        }
        return false;
    }
    
    static {
        PATTERN_DEFINE = Pattern.compile("^\\s*(//)?\\s*#define\\s+([A-Za-z0-9_]+)\\s*(//.*)?$");
        PATTERN_IFDEF = Pattern.compile("^\\s*#if(n)?def\\s+([A-Za-z0-9_]+)(\\s*)?$");
    }
}
