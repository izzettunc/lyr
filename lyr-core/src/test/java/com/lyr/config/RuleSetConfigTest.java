package com.lyr.config;

import static com.lyr.util.RuleDefinition.SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY;
import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.lyr.TestUtil;
import com.lyr.config.parser.RuleSet;
import com.lyr.exception.NotYetInitializedException;
import com.lyr.exception.config.RuleWithNoConfigException;
import com.lyr.exception.rule.config.BadRuleConfigException;
import com.lyr.rule.RuleConfig;
import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithDisallowedArchitectureRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.util.RuleDefinition;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class RuleSetConfigTest {

    RuleSetConfig testObject;
    static MockedStatic<BadRuleConfigException> mockedBadRuleConfigExceptionStatic =
            mockStatic(BadRuleConfigException.class, CALLS_REAL_METHODS);

    @BeforeEach
    void beforeEach() {
        mockedBadRuleConfigExceptionStatic.reset();
        testObject = new RuleSetConfig(Map.of());
    }

    @AfterAll
    static void afterAll() {
        mockedBadRuleConfigExceptionStatic.closeOnDemand();
    }

    @Test
    void testThatRuleSetConfigIsSuccessfullyConstructedWhenMapIsValid() {
        // Given
        final var validConfigMap = Map.of(
                SCAN_DYNAMODB_TABLE_IDLE,
                ScanDynamodbTableIdleRuleConfig.builder()
                        .maxIdlePeriodInDays(123)
                        .excludeEmptyTables(true)
                        .build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(456)
                        .build());

        // When
        final var actualRuleSetConfig = new RuleSetConfig(validConfigMap);

        // Then
        assertThat(actualRuleSetConfig.getAllAvailableRuleDefinition()).isEqualTo(validConfigMap.keySet());
        assertThat(actualRuleSetConfig.getRuleConfig(SCAN_DYNAMODB_TABLE_IDLE))
                .isEqualTo(validConfigMap.get(SCAN_DYNAMODB_TABLE_IDLE));
        assertThat(actualRuleSetConfig.getRuleConfig(SCAN_DYNAMODB_TABLE_IDLE))
                .isNotSameAs(validConfigMap.get(SCAN_DYNAMODB_TABLE_IDLE));
        assertThat(actualRuleSetConfig.getRuleConfig(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT))
                .isEqualTo(validConfigMap.get(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT));
        assertThat(actualRuleSetConfig.getRuleConfig(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT))
                .isNotSameAs(validConfigMap.get(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT));
    }

    @Test
    void testThatRuleSetConfigThrowsBadRuleConfigExceptionIfMapIsInvalid() {
        // Given
        final var validConfigMap = Map.of(
                SCAN_DYNAMODB_TABLE_IDLE,
                ScanDynamodbTableIdleRuleConfig.builder()
                        .maxIdlePeriodInDays(-10)
                        .excludeEmptyTables(true)
                        .build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(456)
                        .build());

        // When & Then
        assertThatThrownBy(() -> new RuleSetConfig(validConfigMap)).isInstanceOf(BadRuleConfigException.class);
    }

    @Test
    void testThatRuleSetConfigThrowsBadRuleConfigExceptionIfMapContainsNullRuleConfig() {
        // Given
        final var validConfigMap = new EnumMap<>(Map.of(
                SCAN_DYNAMODB_TABLE_IDLE,
                ScanDynamodbTableIdleRuleConfig.builder()
                        .maxIdlePeriodInDays(123)
                        .excludeEmptyTables(true)
                        .build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(456)
                        .build()));
        validConfigMap.put(SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY, null);

        // When & Then
        assertThatThrownBy(() -> new RuleSetConfig(validConfigMap)).isInstanceOf(BadRuleConfigException.class);

        mockedBadRuleConfigExceptionStatic.verify(
                () -> BadRuleConfigException.forNull(SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY.getRuleName()),
                times(1));
    }

    @Test
    void testThatRuleSetConfigIsCreatedFromRuleSetCorrectly() {
        // Given
        final var scanDynamodbTableIdleRuleConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(123)
                .excludeEmptyTables(true)
                .build();
        final var scanGlueSessionActiveWithLongIdleTimeoutRuleConfig =
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(456)
                        .build();

        final var ruleSet = new RuleSet();
        ruleSet.setScanDynamodbTableIdleRuleConfig(scanDynamodbTableIdleRuleConfig);
        ruleSet.setScanGlueSessionActiveWithLongIdleTimeoutRuleConfig(
                scanGlueSessionActiveWithLongIdleTimeoutRuleConfig);

        final var validConfigMap = Map.of(
                SCAN_DYNAMODB_TABLE_IDLE, scanDynamodbTableIdleRuleConfig,
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT, scanGlueSessionActiveWithLongIdleTimeoutRuleConfig);

        final var expectedRuleSetConfig = new RuleSetConfig(validConfigMap);

        // When
        final var actualRuleSetConfig = RuleSetConfig.from(ruleSet);

        // Then
        assertThat(actualRuleSetConfig).usingRecursiveComparison().isEqualTo(expectedRuleSetConfig);
    }

    @Test
    void testThatRuleSetConfigReturnsCorrectConfigWhenRuleDefinitionIsProvided() {
        // Given
        var expectedConfig = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(123)
                .excludeEmptyTables(true)
                .build();
        Map<RuleDefinition, RuleConfig> configuration = Map.of(
                SCAN_DYNAMODB_TABLE_IDLE,
                expectedConfig,
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(456)
                        .build());
        testObject = new RuleSetConfig(configuration);

        // When
        var actualConfig = testObject.getRuleConfig(SCAN_DYNAMODB_TABLE_IDLE);

        // Then
        assertThat(actualConfig).usingRecursiveComparison().isEqualTo(expectedConfig);
    }

    @Test
    void testThatRuleWithNoConfigExceptionIsThrownWhenARuleThatIsNotPartOfConfigIsRequested() {
        // Given
        Map<RuleDefinition, RuleConfig> configuration = Map.of(
                SCAN_DYNAMODB_TABLE_IDLE,
                        ScanDynamodbTableIdleRuleConfig.builder()
                                .maxIdlePeriodInDays(123)
                                .excludeEmptyTables(true)
                                .build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                                .maxIdleTimeoutInMinutes(456)
                                .build());
        testObject = new RuleSetConfig(configuration);

        // When & Then
        assertThatThrownBy(() -> testObject.getRuleConfig(SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY))
                .isInstanceOf(RuleWithNoConfigException.class)
                .hasMessageContaining("No config found for rule");
    }

    @Test
    void testThatRuleSetConfigReturnsCorrectSetOfRuleDefinition() {
        // Given
        var expectedRuleDefinitionSet =
                Set.of(SCAN_DYNAMODB_TABLE_IDLE, SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT);
        var configuration = Map.of(
                SCAN_DYNAMODB_TABLE_IDLE,
                        ScanDynamodbTableIdleRuleConfig.builder()
                                .maxIdlePeriodInDays(123)
                                .excludeEmptyTables(true)
                                .build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                                .maxIdleTimeoutInMinutes(456)
                                .build());
        testObject = new RuleSetConfig(configuration);

        // When
        var actualRuleDefinitionSet = testObject.getAllAvailableRuleDefinition();

        // Then
        assertThat(actualRuleDefinitionSet)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedRuleDefinitionSet);
    }

    @Test
    void testThatNotYetInitializedExceptionThrownWhenRuleSetConfigGetRunBeforeSet() {
        // Given
        try (final var mockedRuleSetConfig = mockStatic(RuleSetConfig.class, CALLS_REAL_METHODS)) {
            // When & Then
            mockedRuleSetConfig.when(RuleSetConfig::getConfigInstance).thenReturn(null);
            assertThatThrownBy(RuleSetConfig::getRuleSetConfig)
                    .isInstanceOf(NotYetInitializedException.class)
                    .hasMessage("Rule set configuration can not be accessed as it is not yet initialized.");
        }
    }

    @Test
    void testThatRuleSetConfigSetterWorksAsExcepted() {
        // Given
        final var expectedRuleSetConfig = new RuleSetConfig(Map.of());

        // When
        RuleSetConfig.setRuleSetConfig(expectedRuleSetConfig);
        final var actualRuleSetConfig = RuleSetConfig.getRuleSetConfig();

        // Then
        assertThat(actualRuleSetConfig).isSameAs(expectedRuleSetConfig);
    }

    @Test
    void testThatRuleSetConfigSetterThrowsExceptionWhenRuleSetConfigIsNull() {
        // Given
        final RuleSetConfig ruleSetConfig = null;

        // When & Then
        assertThatThrownBy(() -> RuleSetConfig.setRuleSetConfig(ruleSetConfig))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void testThatRuleSetConfigIsParsedAndSetWhenLoadedGivenValidPath() {
        // Given
        final var customRuleSetConfigPath = TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSet.yaml");
        final var expectedRuleSetConfig = new RuleSetConfig(Map.of(
                SCAN_DYNAMODB_TABLE_IDLE,
                        ScanDynamodbTableIdleRuleConfig.builder()
                                .maxIdlePeriodInDays(11)
                                .excludeEmptyTables(true)
                                .build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                        ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                                .maxIdleTimeoutInMinutes(22)
                                .build()));

        // When
        final var actualRuleSetConfig = RuleSetConfig.loadUserRuleSetConfig(customRuleSetConfigPath);

        // Then
        assertThat(actualRuleSetConfig).usingRecursiveComparison().isEqualTo(expectedRuleSetConfig);
    }

    @Test
    void testThatRuleSetConfigIsParsedAndSetUsingDefaultConfigWhenLoadedWithoutAPath() {
        // Given
        final var expectedDefaultRuleSetConfig = new RuleSetConfig(Map.of(
                SCAN_DYNAMODB_TABLE_IDLE,
                ScanDynamodbTableIdleRuleConfig.builder().build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder().build(),
                SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY,
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build(),
                SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY,
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build(),
                SCAN_LAMBDA_FUNCTION_WITH_DISALLOWED_ARCHITECTURE,
                ScanLambdaFunctionWithDisallowedArchitectureRuleConfig.builder().build()));

        // When
        final var actualRuleSetConfig = RuleSetConfig.loadDefaultRuleSetConfig();

        // Then
        assertThat(actualRuleSetConfig).usingRecursiveComparison().isEqualTo(expectedDefaultRuleSetConfig);
    }
}
