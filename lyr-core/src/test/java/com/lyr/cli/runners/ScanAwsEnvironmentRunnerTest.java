package com.lyr.cli.runners;

import static com.lyr.TestUtil.resetUserRuleSetConfigByLoadingEmptyFile;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.lyr.TestUtil;
import com.lyr.config.Settings;
import com.lyr.report.Reporter;
import com.lyr.report.ReporterFactory;
import com.lyr.rule.Rule;
import com.lyr.rule.RuleFactory;
import com.lyr.util.RuleDefinition;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ScanAwsEnvironmentRunnerTest {

    static MockedStatic<RuleFactory> mockedRuleFactory = mockStatic(RuleFactory.class);
    static MockedStatic<ReporterFactory> mockedReporterFactory = mockStatic(ReporterFactory.class);
    static Rule mockedRule = mock(Rule.class);
    static Reporter mockedReporter = mock(Reporter.class);

    @BeforeEach
    void beforeEach() {
        reset(mockedRule, mockedReporter);
        mockedRuleFactory.reset();
        mockedReporterFactory.reset();
        resetUserRuleSetConfigByLoadingEmptyFile();
        Settings.setAppSettings(null);
    }

    @AfterAll
    static void afterAll() {
        mockedRuleFactory.closeOnDemand();
    }

    @Test
    void testThatGivenNoUserDefinedRuleSetDefaultRulesAreCreatedAndEvaluatedAndAReportIsCreated() {
        // Given
        Settings.setAppSettings(Settings.builder().build());
        final var numberOfDefaultRules = 3;
        // When
        mockedRuleFactory.when(() -> RuleFactory.createRule(any())).thenReturn(mockedRule);
        mockedReporterFactory.when(() -> ReporterFactory.createReporter(any())).thenReturn(mockedReporter);

        ScanAwsEnvironmentRunner.run();

        // Then
        mockedRuleFactory.verify(() -> RuleFactory.createRule(RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE), times(1));
        mockedRuleFactory.verify(
                () -> RuleFactory.createRule(RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT), times(1));
        mockedRuleFactory.verify(
                () -> RuleFactory.createRule(RuleDefinition.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT), times(1));
        verify(mockedRule, times(numberOfDefaultRules)).evaluate();
        verify(mockedReporter, times(1)).report(any());
    }

    @Test
    void thatThatGivenUserDefinedRuleSetUserDefinedRulesAreCreatedAndEvaluatedAndAReportIsCreated() {
        // Given
        final var testUserRuleSetAbsolutePath =
                TestUtil.getAbsoluteFilePathOfResource("com/lyr/cli/runners/testUserConfig.yaml");
        final var numberOfRulesInTestUserRuleSet = 1;
        final var settings = Settings.builder()
                .userRuleSetConfigPath(Optional.of(testUserRuleSetAbsolutePath))
                .build();
        Settings.setAppSettings(settings);

        // When
        mockedRuleFactory.when(() -> RuleFactory.createRule(any())).thenReturn(mockedRule);
        mockedReporterFactory.when(() -> ReporterFactory.createReporter(any())).thenReturn(mockedReporter);

        ScanAwsEnvironmentRunner.run();

        // Then
        mockedRuleFactory.verify(() -> RuleFactory.createRule(RuleDefinition.SCAN_DYNAMODB_TABLE_IDLE), times(1));
        verify(mockedRule, times(numberOfRulesInTestUserRuleSet)).evaluate();
        verify(mockedReporter, times(1)).report(any());
    }
}
