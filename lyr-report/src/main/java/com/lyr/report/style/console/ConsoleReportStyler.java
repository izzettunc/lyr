package com.lyr.report.style.console;

import com.google.common.collect.ImmutableMap;
import com.lyr.report.console.Sentiment;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class ConsoleReportStyler {

    private static final char FIRST_LEVEL_BLOCK_CHAR = '#';
    private static final char SECOND_LEVEL_BLOCK_CHAR = '=';
    private static final char THIRD_LEVEL_BLOCK_CHAR = '_';
    private static final String LINE_BREAK = "\n";

    public String buildTitleBlock(final TitleLevel level, final String title) {
        return switch (level) {
            case PRIMARY -> buildFirstLevelTitleBlock(title);
            case SECONDARY -> buildSecondLevelTitleBlock(title);
            case TERTIARY -> buildThirdLevelTitleBlock(title);
        };
    }

    public String styleExecutionConfiguration(final ImmutableMap<String, String> configuration) {
        final StringBuilder builder = new StringBuilder();
        for (final Map.Entry<String, String> configEntry : configuration.entrySet()) {
            final var keyValuePair = String.format("%s: %s", configEntry.getKey(), configEntry.getValue());
            builder.append(toNewLine(keyValuePair));
        }
        return builder.toString();
    }

    public static String styleFindingReport(final String outcome, final Sentiment sentiment) {
        return switch (sentiment) {
            case POSITIVE -> String.format("- [✅] %s", outcome);
            case NEUTRAL -> String.format("- [⚠️] %s", outcome);
            case NEGATIVE -> String.format("- [❌] %s", outcome);
        };
    }

    public static String styleFindingReport(final String outcome, final boolean isPositive) {
        return styleFindingReport(outcome, isPositive ? Sentiment.POSITIVE : Sentiment.NEGATIVE);
    }

    public static String toNewLine(final String text) {
        return StringUtils.isBlank(text) ? LINE_BREAK : String.format("%n%s", text);
    }

    private String buildFirstLevelTitleBlock(final String title) {
        final var fancyTitle = String.format("##   %s   ##", title);
        final var outerBlock = buildFirstLevelBlock(fancyTitle.length());

        return toNewLine(outerBlock) + toNewLine(fancyTitle) + toNewLine(outerBlock) + LINE_BREAK;
    }

    private String buildSecondLevelTitleBlock(final String title) {
        final var fancyTitle = String.format("  %s", title);
        final var outerBlock = buildSecondLevelBlock(fancyTitle.length());

        return toNewLine(outerBlock) + toNewLine(fancyTitle) + toNewLine(outerBlock);
    }

    private String buildThirdLevelTitleBlock(final String title) {
        final var fancyTitle = String.format(" %s", title);
        final var outerBlock = buildThirdLevelBlock(fancyTitle.length());

        return toNewLine(fancyTitle) + toNewLine(outerBlock);
    }

    private String buildFirstLevelBlock(final int blockLength) {
        return buildBlock(FIRST_LEVEL_BLOCK_CHAR, blockLength);
    }

    private String buildSecondLevelBlock(final int blockLength) {
        return buildBlock(SECOND_LEVEL_BLOCK_CHAR, blockLength);
    }

    private String buildThirdLevelBlock(final int blockLength) {
        return buildBlock(THIRD_LEVEL_BLOCK_CHAR, blockLength);
    }

    private String buildBlock(final char blockChar, final int blockLength) {
        return String.valueOf(blockChar).repeat(blockLength);
    }
}
