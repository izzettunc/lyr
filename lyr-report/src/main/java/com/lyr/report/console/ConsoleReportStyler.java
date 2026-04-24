package com.lyr.report.console;

import org.apache.commons.lang3.StringUtils;

public class ConsoleReportStyler {

    private static final char OUTER_BLOCK_CHAR = '#';
    private static final char INNER_BLOCK_CHAR = '=';
    private static final String LINE_BREAK = "\n";

    public String buildTitleBlock(final int depth, final String title) {
        return switch (depth) {
            case 1 -> buildFirstLevelTitleBlock(title);
            case 2 -> buildSecondLevelTitleBlock(title);
            default ->
                throw new UnsupportedOperationException(
                        "Console report styler only supports first and second level titles");
        };
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

    private String buildFirstLevelBlock(final int blockLength) {
        return buildBlock(OUTER_BLOCK_CHAR, blockLength);
    }

    private String buildSecondLevelBlock(final int blockLength) {
        return buildBlock(INNER_BLOCK_CHAR, blockLength);
    }

    private String buildBlock(final char blockChar, final int blockLength) {
        return String.valueOf(blockChar).repeat(blockLength);
    }
}
