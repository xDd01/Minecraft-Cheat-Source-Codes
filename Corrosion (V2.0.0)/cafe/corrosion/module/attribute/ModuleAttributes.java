/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.module.attribute;

import cafe.corrosion.module.Module;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(value=RetentionPolicy.RUNTIME)
public @interface ModuleAttributes {
    public String name();

    public String description();

    public String tMobileName() default "";

    public Module.Category category();

    public boolean defaultModule() default false;

    public boolean hidden() default false;
}

