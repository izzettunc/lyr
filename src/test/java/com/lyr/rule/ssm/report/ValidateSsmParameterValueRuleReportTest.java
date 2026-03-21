package com.lyr.rule.ssm.report;

import static com.lyr.TestUtil.PARAMETER_1;
import static com.lyr.TestUtil.PARAMETER_2;
import static com.lyr.TestUtil.PARAMETER_3;
import static com.lyr.TestUtil.PARAMETER_4;
import static com.lyr.TestUtil.VALUE_1;
import static com.lyr.TestUtil.VALUE_2;
import static com.lyr.TestUtil.VALUE_3;
import static com.lyr.TestUtil.VALUE_4;
import static com.lyr.rule.Constants.VALIDATE_SSM_PARAMETER_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.outcome.ValidationOutcome;
import com.lyr.rule.ssm.SsmReason;
import com.lyr.rule.ssm.config.ValidateSsmParameterValueRuleConfig;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValidateSsmParameterValueRuleReportTest {

    ValidateSsmParameterValueRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ValidateSsmParameterValueRuleReport();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome() {
        // Given
        final var config = ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(List.of(
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_1, VALUE_1),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_2, VALUE_2),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_3, VALUE_3),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_4, VALUE_4)))
                .build();

        final var outcome = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(null),
                ValidationOutcome.invalid(SsmReason.PARAMETER_NOT_FOUND),
                ValidationOutcome.invalid(SsmReason.PARAMETER_VALUE_MISMATCH));

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains(PARAMETER_1, PARAMETER_2, PARAMETER_3, PARAMETER_4)
                .contains("null", "PARAMETER_NOT_FOUND", "PARAMETER_VALUE_MISMATCH")
                .contains(VALIDATE_SSM_PARAMETER_VALUE);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        final var config = ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(List.of())
                .build();

        final List<ValidationOutcome<SsmReason>> outcome = ImmutableList.of();

        // When
        final var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(VALIDATE_SSM_PARAMETER_VALUE);
    }
}
