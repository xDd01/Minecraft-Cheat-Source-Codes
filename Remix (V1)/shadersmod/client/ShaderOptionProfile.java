package shadersmod.client;

import java.util.*;
import optifine.*;

public class ShaderOptionProfile extends ShaderOption
{
    private static final String NAME_PROFILE = "<profile>";
    private static final String VALUE_CUSTOM = "<custom>";
    private ShaderProfile[] profiles;
    private ShaderOption[] options;
    
    public ShaderOptionProfile(final ShaderProfile[] profiles, final ShaderOption[] options) {
        super("<profile>", "", detectProfileName(profiles, options), getProfileNames(profiles), detectProfileName(profiles, options, true), null);
        this.profiles = null;
        this.options = null;
        this.profiles = profiles;
        this.options = options;
    }
    
    private static String detectProfileName(final ShaderProfile[] profs, final ShaderOption[] opts) {
        return detectProfileName(profs, opts, false);
    }
    
    private static String detectProfileName(final ShaderProfile[] profs, final ShaderOption[] opts, final boolean def) {
        final ShaderProfile prof = ShaderUtils.detectProfile(profs, opts, def);
        return (prof == null) ? "<custom>" : prof.getName();
    }
    
    private static String[] getProfileNames(final ShaderProfile[] profs) {
        final ArrayList list = new ArrayList();
        for (int names = 0; names < profs.length; ++names) {
            final ShaderProfile prof = profs[names];
            list.add(prof.getName());
        }
        list.add("<custom>");
        final String[] var4 = list.toArray(new String[list.size()]);
        return var4;
    }
    
    @Override
    public void nextValue() {
        super.nextValue();
        if (this.getValue().equals("<custom>")) {
            super.nextValue();
        }
        this.applyProfileOptions();
    }
    
    public void updateProfile() {
        final ShaderProfile prof = this.getProfile(this.getValue());
        if (prof == null || !ShaderUtils.matchProfile(prof, this.options, false)) {
            final String val = detectProfileName(this.profiles, this.options);
            this.setValue(val);
        }
    }
    
    private void applyProfileOptions() {
        final ShaderProfile prof = this.getProfile(this.getValue());
        if (prof != null) {
            final String[] opts = prof.getOptions();
            for (int i = 0; i < opts.length; ++i) {
                final String name = opts[i];
                final ShaderOption so = this.getOption(name);
                if (so != null) {
                    final String val = prof.getValue(name);
                    so.setValue(val);
                }
            }
        }
    }
    
    private ShaderOption getOption(final String name) {
        for (int i = 0; i < this.options.length; ++i) {
            final ShaderOption so = this.options[i];
            if (so.getName().equals(name)) {
                return so;
            }
        }
        return null;
    }
    
    private ShaderProfile getProfile(final String name) {
        for (int i = 0; i < this.profiles.length; ++i) {
            final ShaderProfile prof = this.profiles[i];
            if (prof.getName().equals(name)) {
                return prof;
            }
        }
        return null;
    }
    
    @Override
    public String getNameText() {
        return Lang.get("of.shaders.profile");
    }
    
    @Override
    public String getValueText(final String val) {
        return val.equals("<custom>") ? Lang.get("of.general.custom", "<custom>") : Shaders.translate("profile." + val, val);
    }
    
    @Override
    public String getValueColor(final String val) {
        return val.equals("<custom>") ? "§c" : "§a";
    }
}
