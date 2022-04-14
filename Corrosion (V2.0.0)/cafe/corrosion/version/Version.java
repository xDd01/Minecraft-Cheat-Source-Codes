/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.version;

public class Version {
    private String major;
    private String minor;
    private String build;
    private boolean dev;

    public Version setMajor(String major) {
        this.major = major;
        return this;
    }

    public Version setMinor(String minor) {
        this.minor = minor;
        return this;
    }

    public Version setBuild(String build) {
        this.build = build;
        return this;
    }

    public Version setDev(boolean dev) {
        this.dev = dev;
        return this;
    }

    public String toString() {
        return this.major + "." + this.minor + "." + this.build + (this.dev ? "-DEV" : "");
    }
}

