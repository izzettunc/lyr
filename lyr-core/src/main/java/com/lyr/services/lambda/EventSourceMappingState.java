package com.lyr.services.lambda;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum EventSourceMappingState {
    CREATING("creating"),
    ENABLING("enabling"),
    ENABLED("enabled"),
    UPDATING("updating"),
    DISABLING("disabling"),
    DISABLED("disabled"),
    DELETING("deleting");

    public static final Set<String> ALL_TRIGGER_STATES_AS_STRING = Arrays.stream(EventSourceMappingState.values())
            .map(state -> state.value)
            .collect(Collectors.toSet());

    private final String value;

    EventSourceMappingState(final String state) {
        this.value = state;
    }
}
