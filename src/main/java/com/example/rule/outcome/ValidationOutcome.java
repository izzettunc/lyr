package com.example.rule.outcome;

import lombok.Builder;

import java.io.Serializable;

@Builder
public record ValidationOutcome<T extends Enum<T>>(boolean success, T reason) implements Serializable, Outcome {
    public static <T extends Enum<T>> ValidationOutcome<T> valid() {
        return ValidationOutcome.<T>builder()
                .success(true)
                .reason(null)
                .build();
    }

    public static <T extends Enum<T>> ValidationOutcome<T> valid(T reason) {
        return ValidationOutcome.<T>builder()
                .success(true)
                .reason(reason)
                .build();
    }

    public static <T extends Enum<T>> ValidationOutcome<T> invalid(T reason) {
        return ValidationOutcome.<T>builder()
                .success(false)
                .reason(reason)
                .build();
    }
}
