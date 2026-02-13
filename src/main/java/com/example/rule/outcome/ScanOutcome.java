package com.example.rule.outcome;

import lombok.Builder;

import java.io.Serializable;
import java.util.List;

@Builder
public record ScanOutcome(String result) implements Serializable, Outcome {
}
