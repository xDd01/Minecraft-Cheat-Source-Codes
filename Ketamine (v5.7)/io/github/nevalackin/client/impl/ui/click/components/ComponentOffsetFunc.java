package io.github.nevalackin.client.impl.ui.click.components;

import io.github.nevalackin.client.api.ui.framework.Component;

import java.util.function.Function;

@FunctionalInterface
public interface ComponentOffsetFunc extends Function<Component, Integer> {
}