/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.util;

public abstract class LazyLoadBase<T> {
    private T value;
    private boolean isLoaded = false;

    public T getValue() {
        if (this.isLoaded) return this.value;
        this.isLoaded = true;
        this.value = this.load();
        return this.value;
    }

    protected abstract T load();
}

