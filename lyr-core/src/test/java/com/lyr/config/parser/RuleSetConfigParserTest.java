package com.lyr.config.parser;

import com.lyr.TestUtil;
import com.lyr.exception.config.RuleSetConfigLoadException;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Map;

import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

class RuleSetConfigParserTest {

    @BeforeEach
    void beforeEach() {
        RuleSetConfigParser.setIsDefaultLoaded(false);
    }

    @Test
    void testThatGivenNoCustomRuleSetConfigDefaultIsLoadedSuccessfully() {
        // Given
        final var expectedRuleDefToRuleConfigMap = Map.of(
                SCAN_DYNAMODB_TABLE_IDLE, ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(30).build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT, ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().maxIdleTimeoutInMinutes(15).build(),
                SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY, ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build()
        );

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
                SCAN_DYNAMODB_TABLE_IDLE, ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(11).excludeEmptyTables(true).build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT, ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().maxIdleTimeoutInMinutes(22).build()
        );
        final var customRuleSetConfigPath = TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSetConfig.yaml");

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
                SCAN_DYNAMODB_TABLE_IDLE, ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(30).build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT, ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().maxIdleTimeoutInMinutes(15).build()
        );
        final var customRuleSetConfigPath = TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSetConfigWithOnlyNames.yaml");

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
        final var customRuleSetConfigPath = TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSetConfigWithUnknownRules.yaml");

        // When & Then
        assertThatThrownBy(() -> RuleSetConfigParser.parseRuleSetConfig(customRuleSetConfigPath))
                .isInstanceOf(RuleSetConfigLoadException.class)
                .hasMessageContaining("Failed to generate rule set config. Exception: Given rule is not defined. Rule name:");
    }

    @Test
    void testThatGivenInvalidPathRuleSetConfigLoadExceptionIsThrownWhileParsingCustomRuleSet() {
        // Given
        final var customRuleSetConfigPath = "a/path/that/does/not/exist";

        // When & Then
        assertThatThrownBy(() -> RuleSetConfigParser.parseRuleSetConfig(customRuleSetConfigPath))
                .isInstanceOf(RuleSetConfigLoadException.class)
                .hasMessageContaining("Failed to load ruleset from system. Exception:");
    }

    @Test
    void testThatGivenInvalidPathRuleSetConfigLoadExceptionIsThrownWhileParsingDefaultRuleSet() {
        // Given
        final var defaultRuleSetConfigPath = "a/path/that/does/not/exist";

        // When
        try(MockedStatic<RuleSetConfigParser> mockedRuleSetConfigParser = mockStatic(RuleSetConfigParser.class, CALLS_REAL_METHODS)){
            mockedRuleSetConfigParser.when(RuleSetConfigParser::getDefaultRulesetConfigPath).thenReturn(defaultRuleSetConfigPath);

            // Then
            assertThatThrownBy(() -> RuleSetConfigParser.parseRuleSetConfig(defaultRuleSetConfigPath))
                    .isInstanceOf(RuleSetConfigLoadException.class)
                    .hasMessageContaining("Failed to load ruleset from resource. Exception:");
        }
    }

    @Test
    void testThatGivenEmptyRuleSetConfigAnEmptyRuleDefinitionToRuleConfigMapIsProduced() {
        // Given
        final var customRuleSetConfigPath = TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/emptyUserRuleSetConfig.yaml");

        // When
        final var actualRuleDefToRuleConfigMap = RuleSetConfigParser.parseRuleSetConfig(customRuleSetConfigPath);

        // Then
        assertThat(actualRuleDefToRuleConfigMap).isEmpty();
    }

    @Test
    void testThatGivenInvalidDefaultRuleSetProvidedRuleSetConfigLoadExceptionIsThrown() {
        // Given
        final var badDefaultRuleSetConfigPath = "com/lyr/config/defaultRuleSetWithBadConfig.yaml";

        // When
        try (MockedStatic<RuleSetConfigParser> mockedRuleSetConfigParser = mockStatic(RuleSetConfigParser.class, CALLS_REAL_METHODS)) {
            mockedRuleSetConfigParser.when(RuleSetConfigParser::getDefaultRulesetConfigPath).thenReturn(badDefaultRuleSetConfigPath);

            // Then
            assertThatThrownBy(RuleSetConfigParser::parseDefaultRuleSetConfig)
                    .isInstanceOf(RuleSetConfigLoadException.class)
                    .hasMessageContaining("Failed to generate default rule set config. Exception:");
        }
    }

    @Test
    void testThatGivenCustomRuleSetThatOnlyUsesRuleNameRuleSetConfigParserThrowsRuleSetConfigLoadExceptionWhenThereIsNoDefaultConfigAvailableForARule() {
        // Given
        final var alternativeDefaultRuleSetConfigPath = "com/lyr/config/alternativeDefaultRuleSet.yaml";
        final var customRuleSetConfigPath = TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSetConfigWithOnlyNames.yaml");

        // When
        try (MockedStatic<RuleSetConfigParser> mockedRuleSetConfigParser = mockStatic(RuleSetConfigParser.class, CALLS_REAL_METHODS)) {
            mockedRuleSetConfigParser.when(RuleSetConfigParser::getDefaultRulesetConfigPath).thenReturn(alternativeDefaultRuleSetConfigPath);

            // Then
            assertThatThrownBy(() -> RuleSetConfigParser.parseRuleSetConfig(customRuleSetConfigPath))
                    .isInstanceOf(RuleSetConfigLoadException.class)
                    .hasMessageContaining("Failed to generate rule set config. Exception:");

        }
    }

    @Test
    void testThatDefaultConfigIsLoadedOnlyOnceLazilyDuringLoadingCustomRuleSet(){
        // Given
        final var customRuleSetConfigPath = TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSetConfig.yaml");

        try (MockedStatic<RuleSetConfigParser> mockedRuleSetConfigParser = mockStatic(RuleSetConfigParser.class, CALLS_REAL_METHODS)) {
            // Ensure that it's a clean run
            mockedRuleSetConfigParser.verify(() -> RuleSetConfigParser.parseRuleSetConfig(any()), never());
            mockedRuleSetConfigParser.verify(RuleSetConfigParser::parseDefaultRuleSetConfig, never());

            // When
            RuleSetConfigParser.parseRuleSetConfig(customRuleSetConfigPath);
            RuleSetConfigParser.parseRuleSetConfig(customRuleSetConfigPath);

            // Then
            mockedRuleSetConfigParser.verify(()-> RuleSetConfigParser.parseRuleSetConfig(any()), times(2));
            mockedRuleSetConfigParser.verify(RuleSetConfigParser::parseDefaultRuleSetConfig, times(1));
        }
    }
}