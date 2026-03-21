package com.lyr.rule.outcome;

import java.io.Serializable;
import lombok.Builder;

@Builder
public record ScanOutcome(String result) implements Serializable, Outcome {}
