package com.lyr.report.io;

public class ConsolePrinter implements OutputStrategy {

    @Override
    public void write(final String text) {
        System.out.println(text);
    }
}
