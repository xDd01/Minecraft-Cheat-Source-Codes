package shadersmod.client;

import optifine.*;
import java.util.*;

public abstract class ShaderOption
{
    public static final String COLOR_GREEN = "§a";
    public static final String COLOR_RED = "§c";
    public static final String COLOR_BLUE = "§9";
    private String name;
    private String description;
    private String value;
    private String[] values;
    private String valueDefault;
    private String[] paths;
    private boolean enabled;
    private boolean visible;
    
    public ShaderOption(final String name, final String description, final String value, final String[] values, final String valueDefault, final String path) {
        this.name = null;
        this.description = null;
        this.value = null;
        this.values = null;
        this.valueDefault = null;
        this.paths = null;
        this.enabled = true;
        this.visible = true;
        this.name = name;
        this.description = description;
        this.value = value;
        this.values = values;
        this.valueDefault = valueDefault;
        if (path != null) {
            this.paths = new String[] { path };
        }
    }
    
    private static int getIndex(final String str, final String[] strs) {
        for (int i = 0; i < strs.length; ++i) {
            final String s = strs[i];
            if (s.equals(str)) {
                return i;
            }
        }
        return -1;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public String getDescriptionText() {
        String desc = Config.normalize(this.description);
        desc = StrUtils.removePrefix(desc, "//");
        desc = Shaders.translate("option." + this.getName() + ".comment", desc);
        return desc;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public boolean setValue(final String value) {
        final int index = getIndex(value, this.values);
        if (index < 0) {
            return false;
        }
        this.value = value;
        return true;
    }
    
    public String getValueDefault() {
        return this.valueDefault;
    }
    
    public void resetValue() {
        this.value = this.valueDefault;
    }
    
    public void nextValue() {
        int index = getIndex(this.value, this.values);
        if (index >= 0) {
            index = (index + 1) % this.values.length;
            this.value = this.values[index];
        }
    }
    
    public String[] getPaths() {
        return this.paths;
    }
    
    public void addPaths(final String[] newPaths) {
        final List pathList = Arrays.asList(this.paths);
        for (int i = 0; i < newPaths.length; ++i) {
            final String newPath = newPaths[i];
            if (!pathList.contains(newPath)) {
                this.paths = (String[])Config.addObjectToArray(this.paths, newPath);
            }
        }
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isChanged() {
        return !Config.equals(this.value, this.valueDefault);
    }
    
    public boolean isVisible() {
        return this.visible;
    }
    
    public void setVisible(final boolean visible) {
        this.visible = visible;
    }
    
    public boolean isValidValue(final String val) {
        return getIndex(val, this.values) >= 0;
    }
    
    public String getNameText() {
        return Shaders.translate("option." + this.name, this.name);
    }
    
    public String getValueText(final String val) {
        return val;
    }
    
    public String getValueColor(final String val) {
        return "";
    }
    
    public boolean matchesLine(final String line) {
        return false;
    }
    
    public boolean checkUsed() {
        return false;
    }
    
    public boolean isUsedInLine(final String line) {
        return false;
    }
    
    public String getSourceLine() {
        return null;
    }
    
    public String[] getValues() {
        return this.values.clone();
    }
    
    @Override
    public String toString() {
        return "" + this.name + ", value: " + this.value + ", valueDefault: " + this.valueDefault + ", paths: " + Config.arrayToString(this.paths);
    }
}
