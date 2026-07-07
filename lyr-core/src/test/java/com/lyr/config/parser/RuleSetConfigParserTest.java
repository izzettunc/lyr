package com.lyr.config.parser;

import static com.lyr.util.RuleDefinition.SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY;
import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;

import com.lyr.TestUtil;
import com.lyr.exception.config.RuleSetConfigLoadException;
import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithDisallowedArchitectureRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class RuleSetConfigParserTest {

    @Test
    void testThatDefaultRuleSetConfigIsLoadedSuccessfully() {
        // Given
        final var expectedRuleDefToRuleConfigMap = Map.of(
                SCAN_DYNAMODB_TABLE_IDLE,
                ScanDynamodbTableIdleRuleConfig.builder().build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build(),
                SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY,
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build(),
                SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY,
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build(),
                SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE,
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder().build());

        // When
        final var actualRuleDefToRuleConfigMap = RuleSetConfigParser.parseDefaultRuleSetConfig();

        // Then
        assertThat(actualRuleDefToRuleConfigMap)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleDefToRuleConfigMap);
    }

    @Test
    void testThatGivenCustomRuleSetConfigIsLoadedCorrectly() {
        // Given
        final var expectedRuleDefToRuleConfigMap = Map.of(
                SCAN_DYNAMODB_TABLE_IDLE,
                        ScanDynamodbTableIdleRuleConfig.builder()
                                .maxIdlePeriodInDays(11)
                                .excludeEmptyTables(true)
                                .build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                                .maxIdleTimeoutInMinutes(22)
                                .build());
        final var customRuleSetConfigPath =
                TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSetConfig.yaml");

        // When
        final var actualRuleDefToRuleConfigMap = RuleSetConfigParser.parseRuleSetConfig(customRuleSetConfigPath);

        // Then
        assertThat(actualRuleDefToRuleConfigMap)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleDefToRuleConfigMap);
    }

    @Test
    void testThatGivenCustomRuleSetThatOnlyUsesRuleNameConfigLoadsThoseRulesWithDefaultSettings() {
        // Given
        final var expectedRuleDefToRuleConfigMap = Map.of(
                SCAN_DYNAMODB_TABLE_IDLE,
                        ScanDynamodbTableIdleRuleConfig.builder()
                                .maxIdlePeriodInDays(30)
                                .build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                                .maxIdleTimeoutInMinutes(15)
                                .build());
        final var customRuleSetConfigPath =
                TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSetConfigWithOnlyNames.yaml");

        // When
        final var actualRuleDefToRuleConfigMap = RuleSetConfigParser.parseRuleSetConfig(customRuleSetConfigPath);

        // Then
        assertThat(actualRuleDefToRuleConfigMap)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleDefToRuleConfigMap);
    }

    @Test
    void
            testThatGivenCustomRuleSetThatDefinesTheRuleConfigPartiallyRuleSetConfigParserLoadsThoseRulesWithUserDefinedAndDefaultMixed() {
        // Given
        final var expectedRuleDefToRuleConfigMap = Map.of(
                SCAN_DYNAMODB_TABLE_IDLE,
                ScanDynamodbTableIdleRuleConfig.builder()
                        .maxIdlePeriodInDays(30)
                        .excludeEmptyTables(true)
                        .build());
        final var customRuleSetConfigPath =
                TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/partialUserRuleSetConfig.yaml");

        // When
        final var actualRuleDefToRuleConfigMap = RuleSetConfigParser.parseRuleSetConfig(customRuleSetConfigPath);

        // Then
        assertThat(actualRuleDefToRuleConfigMap)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleDefToRuleConfigMap);
    }

    @Test
    void testThatGivenCustomRuleSetContainsUnknownRuleRuleSetConfigThrowsRuleSetConfigLoadException() {
        // Given
        final var customRuleSetConfigPath =
                TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSetConfigWithUnknownRules.yaml");

        // When & Then
        assertThatThrownBy(() -> RuleSetConfigParser.parseRuleSetConfig(customRuleSetConfigPath))
                .isInstanceOf(RuleSetConfigLoadException.class)
                .hasMessageContaining(
                        "Failed to generate rule set config. Exception: Given rule is not defined. Rule name:");
    }

    @Test
    void testThatGivenInvalidPathRuleSetConfigLoadExceptionIsThrownWhileParsingCustomRuleSet() {
        // Given
        final var customRuleSetConfigPath = "a/path/that/does/not/exist";

        // When & Then
        assertThatThrownBy(() -> RuleSetConfigParser.parseRuleSetConfig(customRuleSetConfigPath))
                .isInstanceOf(RuleSetConfigLoadException.class)
                .hasMessageContaining("Failed to load ruleset from system. ExceptionType:");
    }

    @Test
    void testThatGivenInvalidPathRuleSetConfigLoadExceptionIsThrownWhileParsingDefaultRuleSet() {
        // Given
        final var defaultRuleSetConfigPath = "a/path/that/does/not/exist";

        // When
        try (MockedStatic<RuleSetConfigParser> mockedRuleSetConfigParser =
                mockStatic(RuleSetConfigParser.class, CALLS_REAL_METHODS)) {
            mockedRuleSetConfigParser
                    .when(RuleSetConfigParser::getDefaultRulesetConfigPath)
                    .thenReturn(defaultRuleSetConfigPath);

            // Then
            assertThatThrownBy(RuleSetConfigParser::parseDefaultRuleSetConfig)
                    .isInstanceOf(RuleSetConfigLoadException.class)
                    .hasMessageContaining("Failed to load ruleset from resource. ExceptionType:");
        }
    }

    @Test
    void testThatGivenEmptyRuleSetConfigAnEmptyRuleDefinitionToRuleConfigMapIsProduced() {
        // Given
        final var customRuleSetConfigPath =
                TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/emptyUserRuleSetConfig.yaml");

        // When
        final var actualRuleDefToRuleConfigMap = RuleSetConfigParser.parseRuleSetConfig(customRuleSetConfigPath);

        // Then
        assertThat(actualRuleDefToRuleConfigMap).isEmpty();
    }

    @Test
    void testThatGivenInvalidDefaultRuleSetProvidedRuleSetConfigLoadExceptionIsThrown() {
        // Given
        final var badDefaultRuleSetConfigPath = "com/lyr/config/invalidDefaultRuleSet.json";

        // When
        try (MockedStatic<RuleSetConfigParser> mockedRuleSetConfigParser =
                mockStatic(RuleSetConfigParser.class, CALLS_REAL_METHODS)) {
            mockedRuleSetConfigParser
                    .when(RuleSetConfigParser::getDefaultRulesetConfigPath)
                    .thenReturn(badDefaultRuleSetConfigPath);

            // Then
            assertThatThrownBy(RuleSetConfigParser::parseDefaultRuleSetConfig)
                    .isInstanceOf(RuleSetConfigLoadException.class)
                    .hasMessageContaining("Failed to load ruleset from resource. ExceptionType:");
        }
    }
}
