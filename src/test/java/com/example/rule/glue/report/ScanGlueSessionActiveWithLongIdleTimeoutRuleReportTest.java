package com.example.rule.glue.report;

import com.example.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.example.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static com.example.TestUtil.createImmutableListOfScanOutcome;
import static org.assertj.core.api.Assertions.assertThat;

class ScanGlueSessionActiveWithLongIdleTimeoutRuleReportTest {

    ScanGlueSessionActiveWithLongIdleTimeoutRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanGlueSessionActiveWithLongIdleTimeoutRuleReport();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome(){
        // Given
        var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig
                .builder()
                .maxIdleTimeoutInMinutes(5)
                .build();

        var outcome = createImmutableListOfScanOutcome("outcome1", "outcome2");

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains("outcome1", "outcome2")
                .contains(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome(){
        // Given
        var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig
                .builder()
                .maxIdleTimeoutInMinutes(5)
                .build();

        var outcome = createImmutableListOfScanOutcome();

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT);
    }
}