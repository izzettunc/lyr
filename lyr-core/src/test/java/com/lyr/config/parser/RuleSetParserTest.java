package com.lyr.config.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

import com.lyr.TestUtil;
import com.lyr.exception.config.RuleSetParseException;
import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithDisallowedArchitectureRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.util.file.FileUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.MockedStatic;

class RuleSetParserTest {

    @Test
    void testThatDefaultRuleSetIsLoadedSuccessfully() {
        // Given
        final var expectedRuleSet = new RuleSet();
        expectedRuleSet.setScanDynamodbTableIdleRuleConfig(
                ScanDynamodbTableIdleRuleConfig.builder().build());
        expectedRuleSet.setScanLambdaFunctionWithDisallowedArchitectureRuleConfig(
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder().build());
        expectedRuleSet.setScanLambdaFunctionWithUnboundedConcurrencyRuleConfig(
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build());
        expectedRuleSet.setScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig(
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build());
        expectedRuleSet.setScanGlueSessionActiveWithLongIdleTimeoutRuleConfig(
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build());

        // When
        final var actualRuleSet = RuleSetParser.parseDefaultRuleSet();

        // Then
        assertThat(actualRuleSet)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleSet);
    }

    @Test
    void testThatGivenCustomRuleSetIsLoadedCorrectly() {
        // Given
        final var expectedRuleSet = new RuleSet();
        expectedRuleSet.setScanDynamodbTableIdleRuleConfig(ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(11)
                .excludeEmptyTables(true)
                .build());
        expectedRuleSet.setScanGlueSessionActiveWithLongIdleTimeoutRuleConfig(
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(22)
                        .build());

        final var customRuleSetPath = TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSet.yaml");

        // When
        final var actualRuleSet = RuleSetParser.parseRuleSet(customRuleSetPath);

        // Then
        assertThat(actualRuleSet)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleSet);
    }

    @Test
    void testThatGivenCustomRuleSetThatOnlyUsesRuleNameConfigLoadsThoseRulesWithDefaultSettings() {
        // Given
        final var expectedRuleSet = new RuleSet();
        expectedRuleSet.setScanDynamodbTableIdleRuleConfig(ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(30)
                .build());
        expectedRuleSet.setScanGlueSessionActiveWithLongIdleTimeoutRuleConfig(
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(15)
                        .build());

        final var customRuleSetPath =
                TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSetWithOnlyNames.yaml");

        // When
        final var actualRuleSet = RuleSetParser.parseRuleSet(customRuleSetPath);

        // Then
        assertThat(actualRuleSet)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleSet);
    }

    @Test
    void
            testThatGivenCustomRuleSetThatDefinesTheRuleConfigPartiallyRuleSetParserLoadsThoseRulesWithUserDefinedAndDefaultMixed() {
        // Given
        final var expectedRuleSet = new RuleSet();
        expectedRuleSet.setScanDynamodbTableIdleRuleConfig(ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(30)
                .excludeEmptyTables(true)
                .build());

        final var customRuleSetPath = TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/partialUserRuleSet.yaml");

        // When
        final var actualRuleSet = RuleSetParser.parseRuleSet(customRuleSetPath);

        // Then
        assertThat(actualRuleSet)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleSet);
    }

    @Test
    void testThatGivenCustomRuleSetContainsAnUnknownRuleRuleSetParserThrowsRuleSetParseException() {
        // Given
        final var customRuleSetPath =
                TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSetWithUnknownRules.yaml");

        // When & Then
        assertThatThrownBy(() -> RuleSetParser.parseRuleSet(customRuleSetPath))
                .isInstanceOf(RuleSetParseException.class)
                .hasMessageContaining(
                        "Failed to parse the ruleset due to an unrecognized property. Property: some.random.rule.that.is.not.implemented, ExceptionType:");
    }

    @Test
    void testThatGivenInvalidPathRuleSetParseExceptionIsThrownWhileParsingCustomRuleSet() {
        // Given
        final var customRuleSetPath = "a/path/that/does/not/exist";

        // When & Then
        assertThatThrownBy(() -> RuleSetParser.parseRuleSet(customRuleSetPath))
                .isInstanceOf(RuleSetParseException.class)
                .hasMessageContaining("Failed to parse ruleset as the file doesn't exist. ExceptionType:");
    }

    @Test
    void testThatGivenInvalidPathRuleSetParseExceptionIsThrownWhileParsingDefaultRuleSet() {
        // Given
        final var defaultRuleSetPath = "a/path/that/does/not/exist";

        // When
        try (MockedStatic<RuleSetParser> mockedRuleSetParser = mockStatic(RuleSetParser.class, CALLS_REAL_METHODS)) {
            mockedRuleSetParser.when(RuleSetParser::getDefaultRuleSetPath).thenReturn(defaultRuleSetPath);

            // Then
            assertThatThrownBy(RuleSetParser::parseDefaultRuleSet)
                    .isInstanceOf(RuleSetParseException.class)
                    .hasMessageContaining("Failed to parse ruleset as the file doesn't exist. ExceptionType:");
        }
    }

    @Test
    void testThatGivenEmptyRuleSetRuleSetParserThrowsRuleSetParseExceptionDueToInvalidFormat() {
        // Given
        final var customRuleSetPath = TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/emptyUserRuleSet.yaml");

        // When & Then
        assertThatThrownBy(() -> RuleSetParser.parseRuleSet(customRuleSetPath))
                .isInstanceOf(RuleSetParseException.class)
                .hasMessageContaining("Failed to parse ruleset as it is not in expected format. ExceptionType:");
    }

    @Test
    void testThatGivenInvalidDefaultRuleSetProvidedRuleSetParseExceptionIsThrown() {
        // Given
        final var badDefaultRuleSetPath = "com/lyr/config/invalidDefaultRuleSet.json";

        // When
        try (final var mockedRuleSetParser = mockStatic(RuleSetParser.class, CALLS_REAL_METHODS)) {
            mockedRuleSetParser.when(RuleSetParser::getDefaultRuleSetPath).thenReturn(badDefaultRuleSetPath);

            // Then
            assertThatThrownBy(RuleSetParser::parseDefaultRuleSet)
                    .isInstanceOf(RuleSetParseException.class)
                    .hasMessageContaining("Failed to parse ruleset as it is not in expected format. ExceptionType:");
        }
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "com/lyr/config/invalidRuleSetDueToAttributeTypeBooleanInteger.yaml",
                "com/lyr/config/invalidRuleSetDueToAttributeTypeBooleanString.yaml",
                "com/lyr/config/invalidRuleSetDueToAttributeTypeIntegerString.yaml",
            })
    void
            testThatGivenRuleSetContainsWrongTypeForRuleConfigAttributeRuleSetParserThrowsRuleSetParseExceptionDueToInvalidFormat(
                    final String path) {
        // Given
        final var customRuleSetPath = TestUtil.getAbsoluteFilePathOfResource(path);

        // When & Then
        assertThatThrownBy(() -> RuleSetParser.parseRuleSet(customRuleSetPath))
                .isInstanceOf(RuleSetParseException.class)
                .hasMessageContaining("Failed to parse ruleset as it is not in expected format. ExceptionType:");
    }

    @Test
    void testThatRuleSetParserThrowGenericRuleSetParseExceptionWhenAnUnknownIssueOccurs() {
        // Given
        // When & Then
        try (final var _ = mockStatic(FileUtils.class)) {
            assertThatThrownBy(RuleSetParser::parseDefaultRuleSet)
                    .isInstanceOf(RuleSetParseException.class)
                    .hasMessageContaining("Failed to parse ruleset due to unknown error. ExceptionType: ");
        }
    }
}
