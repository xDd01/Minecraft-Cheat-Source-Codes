/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util.font.type;

import cafe.corrosion.util.nameable.INameable;

public enum FontType implements INameable
{
    OPEN_SANS("Open Sans", "OpenSans-Regular"),
    ROBOTO("Roboto", "Roboto-Regular"),
    LATO("Lato", "Lato-Medium"),
    UBUNTU("Ubuntu", "Ubuntu-M"),
    COMFORTAA("Comfortaa", "Comfortaa-Regular"),
    FIRA_CODE_MEDIUM("Fira Code Medium", "FiraCode-Medium"),
    COMME_BOLD("Comme Bold", "Comme-SemiBold"),
    COMME_MEDIUM("Comme Medium", "Comme-Medium"),
    PRODUCT_SANS("Product Sans", "Product Sans 400");

    private final String name;
    private final String pathName;

    @Override
    public String getName() {
        return this.name;
    }

    public String getPathName() {
        return this.pathName;
    }

    private FontType(String name, String pathName) {
        this.name = name;
        this.pathName = pathName;
    }
}

