package com.example.rule.dynamodb.report;

import static com.example.TestUtil.createImmutableListOfScanOutcome;
import static com.example.rule.Constants.SCAN_DYNAMODB_TABLE_IDLE;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.rule.dynamodb.config.ScanDynamodbTableIdleRuleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ScanDynamodbTableIdleRuleReportTest {

    ScanDynamodbTableIdleRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanDynamodbTableIdleRuleReport();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome() {
        // Given
        var config = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(180)
                .build();

        var outcome = createImmutableListOfScanOutcome("outcome1", "outcome2");

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains("outcome1", "outcome2").contains(SCAN_DYNAMODB_TABLE_IDLE);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        var config = ScanDynamodbTableIdleRuleConfig.builder()
                .maxIdlePeriodInDays(180)
                .build();

        var outcome = createImmutableListOfScanOutcome();

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(SCAN_DYNAMODB_TABLE_IDLE);
    }
}
