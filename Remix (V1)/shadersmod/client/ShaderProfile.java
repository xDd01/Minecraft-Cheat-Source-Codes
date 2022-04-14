package shadersmod.client;

import java.util.*;

public class ShaderProfile
{
    private String name;
    private Map<String, String> mapOptionValues;
    private Set<String> disabledPrograms;
    
    public ShaderProfile(final String name) {
        this.name = null;
        this.mapOptionValues = new HashMap<String, String>();
        this.disabledPrograms = new HashSet<String>();
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void addOptionValue(final String option, final String value) {
        this.mapOptionValues.put(option, value);
    }
    
    public void addOptionValues(final ShaderProfile prof) {
        if (prof != null) {
            this.mapOptionValues.putAll(prof.mapOptionValues);
        }
    }
    
    public void applyOptionValues(final ShaderOption[] options) {
        for (int i = 0; i < options.length; ++i) {
            final ShaderOption so = options[i];
            final String key = so.getName();
            final String val = this.mapOptionValues.get(key);
            if (val != null) {
                so.setValue(val);
            }
        }
    }
    
    public String[] getOptions() {
        final Set keys = this.mapOptionValues.keySet();
        final String[] opts = keys.toArray(new String[keys.size()]);
        return opts;
    }
    
    public String getValue(final String key) {
        return this.mapOptionValues.get(key);
    }
    
    public void addDisabledProgram(final String program) {
        this.disabledPrograms.add(program);
    }
    
    public Collection<String> getDisabledPrograms() {
        return new HashSet<String>(this.disabledPrograms);
    }
    
    public void addDisabledPrograms(final Collection<String> programs) {
        this.disabledPrograms.addAll(programs);
    }
    
    public boolean isProgramDisabled(final String program) {
        return this.disabledPrograms.contains(program);
    }
}
