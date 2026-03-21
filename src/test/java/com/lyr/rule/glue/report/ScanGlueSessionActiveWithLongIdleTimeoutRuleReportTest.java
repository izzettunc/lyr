package com.lyr.rule.glue.report;

import static com.lyr.TestUtil.createImmutableListOfScanOutcome;
import static com.lyr.rule.Constants.SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT;
import static org.assertj.core.api.Assertions.assertThat;

import com.lyr.TestUtil;
import com.lyr.rule.glue.config.ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ScanGlueSessionActiveWithLongIdleTimeoutRuleReportTest {

    ScanGlueSessionActiveWithLongIdleTimeoutRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ScanGlueSessionActiveWithLongIdleTimeoutRuleReport();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome() {
        // Given
        final var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(1)
                .build();

        final var outcome = createImmutableListOfScanOutcome(TestUtil.DUMMY_STRING, TestUtil.DUMMY2_STRING);

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains(TestUtil.DUMMY_STRING, TestUtil.DUMMY2_STRING)
                .contains(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        final var config = ScanGlueSessionActiveWithLongIdleTimeoutRuleConfig.builder()
                .maxIdleTimeoutInMinutes(1)
                .build();

        final var outcome = createImmutableListOfScanOutcome();

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(SCAN_GLUE_SESSION_ACTIVE_WITH_LONG_IDLE_TIMEOUT);
    }
}
