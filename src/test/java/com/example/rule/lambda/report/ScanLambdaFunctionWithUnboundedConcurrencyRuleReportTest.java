package com.example.rule.lambda.report;


import com.example.rule.lambda.config.ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.example.TestUtil.createImmutableListOfScanOutcome;
import static com.example.rule.Constants.SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY;
import static org.assertj.core.api.Assertions.assertThat;

class ScanLambdaFunctionWithUnboundedConcurrencyRuleReportTest {

    ScanLambdaFunctionWithUnboundedConcurrencyRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanLambdaFunctionWithUnboundedConcurrencyRuleReport ();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome(){
        // Given
        var config = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();

        var outcome = createImmutableListOfScanOutcome("outcome1", "outcome2");

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains("outcome1", "outcome2")
                .contains(SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome(){
        // Given
        var config = ScanLambdaFunctionWithUnboundedConcurrencyRuleConfig.builder().build();

        var outcome = createImmutableListOfScanOutcome();

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains(SCAN_LAMBDA_FUNCTION_WITH_UNBOUNDED_CONCURRENCY);
    }
}