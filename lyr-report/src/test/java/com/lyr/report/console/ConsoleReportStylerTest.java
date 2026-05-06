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
    @MethodSource("findingWithSentimentAndResultedStyledFindings")
    void testThatFindingStyledCorrectlyDependingOnTheSentiment(
            final String expectedFinding, final String finding, final Sentiment sentiment) {
        // Given expectedFinding, finding and sentiment
        // When
        final var actualFinding = ConsoleReportStyler.styleFindingReport(finding, sentiment);

        // Then
        assertThat(actualFinding).isEqualTo(expectedFinding);
    }

    @Test
    void testThatFindingStyledCorrectlyDependingOnBooleanPositivity() {
        // Given
        final var expectedPositiveFinding = "- [✅] DUMMY";
        final var expectedNegativeFinding = "- [❌] DUMMY";

        // When
        final var actualPositiveFinding = ConsoleReportStyler.styleFindingReport("DUMMY", true);
        final var actualNegativeFinding = ConsoleReportStyler.styleFindingReport("DUMMY", false);

        // Then
        assertThat(actualPositiveFinding).isEqualTo(expectedPositiveFinding);
        assertThat(actualNegativeFinding).isEqualTo(expectedNegativeFinding);
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

    public static Stream<Arguments> findingWithSentimentAndResultedStyledFindings() {
        return Stream.of(
                Arguments.of("- [✅] DUMMY", "DUMMY", Sentiment.POSITIVE),
                Arguments.of("- [⚠️] DUMMY", "DUMMY", Sentiment.NEUTRAL),
                Arguments.of("- [❌] DUMMY", "DUMMY", Sentiment.NEGATIVE));
    }
}
