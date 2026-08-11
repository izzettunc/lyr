package com.lyr.report.plain.text;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableMap;
import com.lyr.report.style.plain.text.PlainTextReportStyler;
import com.lyr.report.style.plain.text.TitleLevel;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PlainTextReportStylerTest {

    PlainTextReportStyler testObject = new PlainTextReportStyler();

    @Test
    void testThatPlainTextReportStylerStylesFirstLevelTitleCorrectly() {
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
    void testThatPlainTextReportStylerStylesSecondLevelTitleCorrectly() {
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
        final var actualFinding = PlainTextReportStyler.styleFindingReport(finding, sentiment);

        // Then
        assertThat(actualFinding).isEqualTo(expectedFinding);
    }

    @Test
    void testThatFindingStyledCorrectlyDependingOnBooleanPositivity() {
        // Given
        final var expectedPositiveFinding = "- [✅] DUMMY";
        final var expectedNegativeFinding = "- [❌] DUMMY";

        // When
        final var actualPositiveFinding = PlainTextReportStyler.styleFindingReport("DUMMY", true);
        final var actualNegativeFinding = PlainTextReportStyler.styleFindingReport("DUMMY", false);

        // Then
        assertThat(actualPositiveFinding).isEqualTo(expectedPositiveFinding);
        assertThat(actualNegativeFinding).isEqualTo(expectedNegativeFinding);
    }

    @Test
    void testThatToNewLinePutsTheTextToNewLine() {
        // Given
        final var expectedText = "\nDUMMY";

        // When
        final var actualText = PlainTextReportStyler.toNewLine("DUMMY");

        // Then
        assertThat(actualText).isEqualTo(expectedText);
    }

    @Test
    void testThatStyleExecutionConfigurationWorksCorrectlyGivenValidConfiguration() {
        // Given
        final ImmutableMap<String, Object> configuration =
                ImmutableMap.of("a", "1", "b", 2, "c", true, "d", List.of("123", 456, false));
        final var expectedText = """

                Execution configuration:
                a: "1"
                b: 2
                c: true
                d: ["123", 456, false]""";

        // When
        final var actualText = testObject.styleExecutionConfiguration(configuration);

        // Then
        assertThat(actualText).isEqualTo(expectedText);
    }

    @Test
    void testThatStyleExecutionConfigurationReturnsEmptyStringGivenEmptyConfiguration() {
        // Given
        final ImmutableMap<String, Object> configuration = ImmutableMap.of();
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
