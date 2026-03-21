package com.lyr.rule.ssm.report;

import static com.lyr.TestUtil.PARAMETER_1;
import static com.lyr.TestUtil.PARAMETER_2;
import static com.lyr.TestUtil.PARAMETER_3;
import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.outcome.ValidationOutcome;
import com.lyr.rule.ssm.SsmReason;
import com.lyr.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidateSsmParameterExistsRuleReportTest {

    ValidateSsmParameterExistsRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ValidateSsmParameterExistsRuleReport();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome() {
        // Given
        final var config = ValidateSsmParameterExistsRuleConfig.builder()
                .parameterNames(List.of(PARAMETER_1, PARAMETER_2, PARAMETER_3))
                .build();

        final var outcome = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(null),
                ValidationOutcome.invalid(SsmReason.PARAMETER_NOT_FOUND));

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains(PARAMETER_1, PARAMETER_2, PARAMETER_3)
                .contains("null", "PARAMETER_NOT_FOUND")
                .contains(VALIDATE_SSM_PARAMETER_EXISTS);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        final var config = ValidateSsmParameterExistsRuleConfig.builder()
                .parameterNames(List.of())
                .build();

        final List<ValidationOutcome<SsmReason>> outcome = ImmutableList.of();

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(VALIDATE_SSM_PARAMETER_EXISTS);
    }
}
