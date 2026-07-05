package com.lyr.rule;

import java.util.Optional;

public abstract class RuleConfigCreator<T extends RuleConfig> {

    public final T create(final Object input) {
        final T config = parse(input).orElseGet(this::createDefaultConfig);

        validate(config);

        return config;
    }

    protected abstract Optional<T> parse(final Object input);

    protected abstract T createDefaultConfig();

    protected abstract void validate(final T ruleConfig);
}
