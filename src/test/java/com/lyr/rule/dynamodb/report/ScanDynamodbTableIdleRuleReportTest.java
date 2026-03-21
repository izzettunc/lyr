package com.lyr.rule.dynamodb.report;

import static com.lyr.TestUtil.createImmutableListOfScanOutcome;
import static com.lyr.rule.Constants.SCAN_DYNAMODB_TABLE_IDLE;
import static org.assertj.core.api.Assertions.assertThat;

import com.lyr.TestUtil;
import com.lyr.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScanDynamodbTableIdleRuleReportTest {

    ScanDynamodbTableIdleRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanDynamodbTableIdleRuleReport();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome() {
        // Given
        final var config =
                ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(1).build();

        final var outcome = createImmutableListOfScanOutcome(TestUtil.DUMMY_STRING, TestUtil.DUMMY2_STRING);

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains(TestUtil.DUMMY_STRING, TestUtil.DUMMY2_STRING)
                .contains(SCAN_DYNAMODB_TABLE_IDLE);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        final var config =
                ScanDynamodbTableIdleRuleConfig.builder().maxIdlePeriodInDays(1).build();

        final var outcome = createImmutableListOfScanOutcome();

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(SCAN_DYNAMODB_TABLE_IDLE);
    }
}
