package com.lyr.report.io;

@SuppressWarnings("PMD.ImplicitFunctionalInterface")
public interface OutputStrategy {
    void write(final String text);
}
