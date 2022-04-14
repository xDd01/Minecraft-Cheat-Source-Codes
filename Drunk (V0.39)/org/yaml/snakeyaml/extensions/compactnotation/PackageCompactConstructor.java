/*
 * Decompiled with CFR 0.152.
 */
package org.yaml.snakeyaml.extensions.compactnotation;

import org.yaml.snakeyaml.extensions.compactnotation.CompactConstructor;

public class PackageCompactConstructor
extends CompactConstructor {
    private String packageName;

    public PackageCompactConstructor(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected Class<?> getClassForName(String name) throws ClassNotFoundException {
        if (name.indexOf(46) >= 0) return super.getClassForName(name);
        try {
            return Class.forName(this.packageName + "." + name);
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
        return super.getClassForName(name);
    }
}
