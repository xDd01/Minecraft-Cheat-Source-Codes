/*
 * Decompiled with CFR 0.152.
 */
package cafe.corrosion.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Manager<T> {
    private final List<T> objects = new CopyOnWriteArrayList<T>();

    public void add(T object) {
        this.objects.add(object);
    }

    public void addAll(Collection<T> objects) {
        this.objects.addAll(objects);
    }

    @SafeVarargs
    public final void addAll(T ... objects) {
        this.objects.addAll(Arrays.asList(objects));
    }

    public List<T> getObjects() {
        return this.objects;
    }

    public Set<T> getIf(Predicate<T> predicate) {
        return this.objects.stream().filter(predicate).collect(Collectors.toSet());
    }

    public void removeIf(Predicate<T> predicate) {
        this.objects.removeIf(predicate);
    }
}

