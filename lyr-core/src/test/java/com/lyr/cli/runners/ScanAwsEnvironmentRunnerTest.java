package com.lyr.cli.runners;

import static com.lyr.TestUtil.DUMMY_STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lyr.config.RuleSetConfig;
import com.lyr.config.Settings;
import com.lyr.report.Reporter;
import com.lyr.report.ReporterFactory;
import com.lyr.rule.Rule;
import com.lyr.rule.RuleFactory;
import com.lyr.util.RuleDefinition;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ScanAwsEnvironmentRunnerTest {

    static MockedStatic<RuleFactory> mockedRuleFactory = mockStatic(RuleFactory.class);
    static MockedStatic<ReporterFactory> mockedReporterFactory = mockStatic(ReporterFactory.class);
    static MockedStatic<RuleSetConfig> mockedRuleSetConfig = mockStatic(RuleSetConfig.class);
    static RuleSetConfig mockedRuleSetConfigInstance = mock(RuleSetConfig.class);
    static Rule mockedRule = mock(Rule.class);
    static Reporter mockedReporter = mock(Reporter.class);

    @BeforeEach
    void beforeEach() {
        reset(mockedRule, mockedReporter, mockedRuleSetConfigInstance);
        mockedRuleFactory.reset();
        mockedReporterFactory.reset();
        mockedRuleSetConfig.reset();
        Settings.setAppSettings(null);
    }

    @AfterAll
    static void afterAll() {
        mockedRuleFactory.closeOnDemand();
        mockedReporterFactory.closeOnDemand();
        mockedRuleSetConfig.closeOnDemand();
    }

    @Test
    void testThatGivenNoUserDefinedRuleSetDefaultRulesAreCreatedAndEvaluatedAndAReportIsCreated() {
        // Given
        Settings.setAppSettings(Settings.builder().build());
        final var numberOfDefaultRules = 2;
        final var ruleSetFromConfig = Set.of(
                RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE,
                RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT);

        // When
        mockedRuleSetConfig.when(RuleSetConfig::getRuleSetConfig).thenReturn(mockedRuleSetConfigInstance);
        when(mockedRuleSetConfigInstance.getAllAvailableRuleDefinition()).thenReturn(ruleSetFromConfig);
        mockedRuleFactory.when(() -> RuleFactory.createRule(any())).thenReturn(mockedRule);
        mockedReporterFactory.when(() -> ReporterFactory.createReporter(any())).thenReturn(mockedReporter);

        ScanAwsEnvironmentRunner.run();

        // Then
        mockedRuleFactory.verify(() -> RuleFactory.createRule(RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE), times(1));
        mockedRuleFactory.verify(
                () -> RuleFactory.createRule(RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT), times(1));
        verify(mockedRule, times(numberOfDefaultRules)).evaluate();
        verify(mockedReporter, times(1)).report(any());
        mockedRuleSetConfig.verify(RuleSetConfig::loadDefaultRuleSetConfig, times(1));
    }

    @Test
    void thatThatGivenUserDefinedRuleSetUserDefinedRulesAreCreatedAndEvaluatedAndAReportIsCreated() {
        // Given
        final var numberOfRulesInTestUserRuleSet = 2;
        final var settings = Settings.builder()
                .userRuleSetConfigPath(Optional.of(DUMMY_STRING))
                .build();
        Settings.setAppSettings(settings);
        final var ruleSetFromConfig = Set.of(
                RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY,
                RuleDefinition.SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY);

        // When
        mockedRuleSetConfig.when(RuleSetConfig::getRuleSetConfig).thenReturn(mockedRuleSetConfigInstance);
        when(mockedRuleSetConfigInstance.getAllAvailableRuleDefinition()).thenReturn(ruleSetFromConfig);
        mockedRuleFactory.when(() -> RuleFactory.createRule(any())).thenReturn(mockedRule);
        mockedReporterFactory.when(() -> ReporterFactory.createReporter(any())).thenReturn(mockedReporter);

        ScanAwsEnvironmentRunner.run();

        // Then
        mockedRuleFactory.verify(
                () -> RuleFactory.createRule(RuleDefinition.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY), times(1));
        mockedRuleFactory.verify(
                () -> RuleFactory.createRule(RuleDefinition.SCAN_CLOUDWATCH_LOG_GROUP_WITHOUT_RETENTION_POLICY),
                times(1));
        verify(mockedRule, times(numberOfRulesInTestUserRuleSet)).evaluate();
        verify(mockedReporter, times(1)).report(any());
        mockedRuleSetConfig.verify(() -> RuleSetConfig.loadUserRuleSetConfig(DUMMY_STRING), times(1));
    }
}
