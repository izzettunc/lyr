package com.lyr.cli.runners;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.lyr.TestUtil;
import com.lyr.config.RuleSetConfig;
import com.lyr.rule.Rule;
import com.lyr.rule.RuleFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

class ScanAwsEnvironmentRunnerTest {

    static MockedStatic<RuleFactory> mockedRuleFactory = mockStatic(RuleFactory.class);
    static Rule mockedRule = mock(Rule.class);

    @BeforeEach
    void beforeEach() {
        reset(mockedRule);
        mockedRuleFactory.reset();
        resetUserRuleSetConfigByLoadingEmptyFile();
    }

    @AfterAll
    static void afterAll() {
        mockedRuleFactory.closeOnDemand();
    }

    void resetUserRuleSetConfigByLoadingEmptyFile() { // NOPMD DetachedTestCase: Not a test case
        final var emptyUserRuleSetConfigAbsolutePath =
                TestUtil.getAbsoluteFilePathOfResource("emptyUserRuleSetConfig.yaml");
        RuleSetConfig.loadUserRuleSetConfig(emptyUserRuleSetConfigAbsolutePath);
    }

    @Test
    void testThatGivenNoUserDefinedRuleSetDefaultRulesAreCreatedAndEvaluatedAndAReportIsCreated() {
        // Given
        final String userRuleSetPath = null;
        final var numberOfDefaultRules = 3;
        // When
        mockedRuleFactory.when(() -> RuleFactory.createRule(any())).thenReturn(mockedRule);

        ScanAwsEnvironmentRunner.run(userRuleSetPath);

        // Then
        mockedRuleFactory.verify(() -> RuleFactory.createRule("scan.dynamodb.table.idle"), times(1));
        mockedRuleFactory.verify(() -> RuleFactory.createRule("scan.glue.session.activeWithLongIdleTimeout"), times(1));
        mockedRuleFactory.verify(
                () -> RuleFactory.createRule("scan.lambda.function.withUnboundedConcurrency"), times(1));
        verify(mockedRule, times(numberOfDefaultRules)).evaluate();
        verify(mockedRule, times(numberOfDefaultRules)).report();
    }

    @Test
    void thatThatGivenUserDefinedRuleSetUserDefinedRulesAreCreatedAndEvaluatedAndAReportIsCreated() {
        // Given
        final var testUserRuleSetAbsolutePath = TestUtil.getAbsoluteFilePathOfResource("testUserConfig.yaml");
        final var numberOfRulesInTestUserRuleSet = 1;

        // When
        mockedRuleFactory.when(() -> RuleFactory.createRule(any())).thenReturn(mockedRule);

        ScanAwsEnvironmentRunner.run(testUserRuleSetAbsolutePath);

        // Then
        mockedRuleFactory.verify(() -> RuleFactory.createRule("scan.dynamodb.table.idle"), times(1));
        verify(mockedRule, times(numberOfRulesInTestUserRuleSet)).evaluate();
        verify(mockedRule, times(numberOfRulesInTestUserRuleSet)).report();
    }
}
