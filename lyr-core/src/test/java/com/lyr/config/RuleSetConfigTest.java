package com.lyr.config;

import static com.lyr.util.RuleDefinition.SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY;
import static com.lyr.util.RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE;
import static com.lyr.util.RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.lyr.util.RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.lyr.TestUtil;
import com.lyr.exception.NotYetInitializedException;
import com.lyr.exception.config.RuleWithNoConfigException;
import com.lyr.rule.RuleConfig;
import com.lyr.rule.cloudwatch.config.ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import com.lyr.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import com.lyr.util.RuleDefinition;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class RuleSetConfigTest {

    RuleSetConfig testObject;

    @AfterEach
    void afterEach() {
        testObject = new RuleSetConfig(Map.of());
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
        var actualConfig = testObject.getConfig(SCAN_DYNAMODB_TABLE_IDLE);

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
        assertThatThrownBy(() -> testObject.getConfig(SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY))
                .isInstanceOf(RuleWithNoConfigException.class)
                .hasMessageContaining("No config found for rule");
    }

    @Test
    void testThatRuleSetConfigReturnsCorrectSetOfRuleDefinition() {
        // Given
        var expectedRuleDefinitionSet =
                Set.of(SCAN_DYNAMODB_TABLE_IDLE, SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT);
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
        RuleSetConfig.setRuleSetConfig(null);

        // When & Then
        assertThatThrownBy(RuleSetConfig::getRuleSetConfig)
                .isInstanceOf(NotYetInitializedException.class)
                .hasMessage("Rule set configuration can not be accessed as it is not yet initialized.");
    }

    @Test
    void testThatRuleSetConfigSetIsRuleSetConfigGot() {
        // Given
        final var expectedRuleSetConfig = new RuleSetConfig(Map.of());

        // When
        RuleSetConfig.setRuleSetConfig(expectedRuleSetConfig);
        final var actualRuleSetConfig = RuleSetConfig.getRuleSetConfig();

        // Then
        assertThat(actualRuleSetConfig).isSameAs(expectedRuleSetConfig);
    }

    @Test
    void testThatRuleSetConfigIsParsedAndSetWhenLoadedGivenValidPath() {
        // Given
        final var customRuleSetConfigPath =
                TestUtil.getAbsoluteFilePathOfResource("com/lyr/config/userRuleSetConfig.yaml");
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
                ScanDynamodbTableIdleRuleConfig.builder()
                        .maxIdlePeriodInDays(30)
                        .build(),
                SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT,
                ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                        .maxIdleTimeoutInMinutes(15)
                        .build(),
                SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY,
                ScanCloudwatchLogGroupWithoutRetentionPolicyRuleConfig.builder().build(),
                SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY,
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build()));

        // When
        final var actualRuleSetConfig = RuleSetConfig.loadDefaultRuleSetConfig();

        // Then
        assertThat(actualRuleSetConfig).usingRecursiveComparison().isEqualTo(expectedDefaultRuleSetConfig);
    }
}
