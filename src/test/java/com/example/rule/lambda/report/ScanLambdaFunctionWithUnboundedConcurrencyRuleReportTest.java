package com.example.rule.lambda.report;

import static com.example.TestUtil.createImmutableListOfScanOutcome;
import static com.example.rule.Constants.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.TestUtil;
import com.example.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScanLambdaFunctionWithUnboundedConcurrencyRuleReportTest {

    ScanLambdaFunctionWithUnboundedConcurrencyRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanLambdaFunctionWithUnboundedConcurrencyRuleReport();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome() {
        // Given
        final var config =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();

        final var outcome = createImmutableListOfScanOutcome(TestUtil.DUMMY_STRING, TestUtil.DUMMY2_STRING);

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains(TestUtil.DUMMY_STRING, TestUtil.DUMMY2_STRING)
                .contains(SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        final var config =
                ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();

        final var outcome = createImmutableListOfScanOutcome();

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY);
    }
}
