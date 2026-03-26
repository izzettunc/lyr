package com.lyr.rule.outcome;

import java.io.Serializable;
import lombok.Builder;

@Builder
public record ValidationOutcome<T extends Enum<T>>(boolean success, T reason) implements Serializable, Outcome {
    public static <T extends Enum<T>> ValidationOutcome<T> valid() {
        return ValidationOutcome.<T>builder().success(true).reason(null).build();
    }

    public static <T extends Enum<T>> ValidationOutcome<T> valid(final T reason) {
        return ValidationOutcome.<T>builder().success(true).reason(reason).build();
    }

    public static <T extends Enum<T>> ValidationOutcome<T> invalid(final T reason) {
        return ValidationOutcome.<T>builder().success(false).reason(reason).build();
    }
}
