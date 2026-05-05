package com.lyr.report.console;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.lyr.report.style.console.ConsoleReportStyler;
import com.lyr.report.style.console.TitleLevel;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ConsoleReportStylerTest {

    ConsoleReportStyler testObject = new ConsoleReportStyler();

    @Test
    void testThatConsoleReportStylerStylesFirstLevelTitleCorrectly() {
        // Given
        final var expectedResult = """

                ###############
                ##   DUMMY   ##
                ###############
                """;

        // When
        final var actualResult = testObject.buildTitleBlock(TitleLevel.PRIMARY, "DUMMY");

        // Then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    void testThatConsoleReportStylerStylesSecondLevelTitleCorrectly() {
        // Given
        final var expectedResult = """

                =======
                  DUMMY
                =======""";
        // When
        final var actualResult = testObject.buildTitleBlock(TitleLevel.SECONDARY, "DUMMY");

        // Then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @ParameterizedTest
    @MethodSource("outcomeWithSentimentAndResultedStyledOutcomes")
    void testThatOutcomeStyledCorrectlyDependingOnTheSentiment(
            final String expectedOutcome, final String outcome, final Sentiment sentiment) {
        // Given expectedOutcome, outcome and sentiment
        // When
        final var actualOutcome = ConsoleReportStyler.styleFindingReport(outcome, sentiment);

        // Then
        assertThat(actualOutcome).isEqualTo(expectedOutcome);
    }

    @Test
    void testThatOutcomeStyledCorrectlyDependingOnBooleanPositivity() {
        // Given
        final var expectedPositiveOutcome = "- [✅] DUMMY";
        final var expectedNegativeOutcome = "- [❌] DUMMY";

        // When
        final var actualPositiveOutcome = ConsoleReportStyler.styleFindingReport("DUMMY", true);
        final var actualNegativeOutcome = ConsoleReportStyler.styleFindingReport("DUMMY", false);

        // Then
        assertThat(actualPositiveOutcome).isEqualTo(expectedPositiveOutcome);
        assertThat(actualNegativeOutcome).isEqualTo(expectedNegativeOutcome);
    }

    @Test
    void testThatToNewLinePutsTheTextToNewLine() {
        // Given
        final var expectedText = "\nDUMMY";

        // When
        final var actualText = ConsoleReportStyler.toNewLine("DUMMY");

        // Then
        assertThat(actualText).isEqualTo(expectedText);
    }

    @Test
    void testThatStyleExecutionConfigurationWorksCorrectlyGivenValidConfiguration() {
        // Given
        final var configuration = ImmutableMap.of("a", "1", "b", "2", "c", "3");
        final var expectedText = """

                Execution configuration:
                a: 1
                b: 2
                c: 3""";

        // When
        final var actualText = testObject.styleExecutionConfiguration(configuration);

        // Then
        assertThat(actualText).isEqualTo(expectedText);
    }

    @Test
    void testThatStyleExecutionConfigurationReturnsEmptyStringGivenEmptyConfiguration() {
        // Given
        final ImmutableMap<String, String> configuration = ImmutableMap.of();
        final var expectedText = "";

        // When
        final var actualText = testObject.styleExecutionConfiguration(configuration);

        // Then
        assertThat(actualText).isEqualTo(expectedText);
    }

    public static Stream<Arguments> outcomeWithSentimentAndResultedStyledOutcomes() {
        return Stream.of(
                Arguments.of("- [✅] DUMMY", "DUMMY", Sentiment.POSITIVE),
                Arguments.of("- [⚠️] DUMMY", "DUMMY", Sentiment.NEUTRAL),
                Arguments.of("- [❌] DUMMY", "DUMMY", Sentiment.NEGATIVE));
    }
}
