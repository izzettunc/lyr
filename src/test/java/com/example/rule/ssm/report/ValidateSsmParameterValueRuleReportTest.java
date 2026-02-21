package com.example.rule.ssm.report;

import static com.example.rule.Constants.VALIDATE_SSM_PARAMETER_VALUE;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.rule.outcome.ValidationOutcome;
import com.example.rule.ssm.SsmReason;
import com.example.rule.ssm.config.ValidateSsmParameterValueRuleConfig;
import com.google.common.collect.ImmutableList;
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
        var config = ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(List.of(
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter1", "value1"),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter2", "value2"),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter3", "value3"),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter4", "value4")))
                .build();

        var outcome = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(null),
                ValidationOutcome.invalid(SsmReason.PARAMETER_NOT_FOUND),
                ValidationOutcome.invalid(SsmReason.PARAMETER_VALUE_MISMATCH));

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains("parameter1", "parameter2", "parameter3", "parameter4")
                .contains("null", "PARAMETER_NOT_FOUND", "PARAMETER_VALUE_MISMATCH")
                .contains(VALIDATE_SSM_PARAMETER_VALUE);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome() {
        // Given
        var config = ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(List.of())
                .build();

        ImmutableList<ValidationOutcome<SsmReason>> outcome = ImmutableList.of();

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult).contains(VALIDATE_SSM_PARAMETER_VALUE);
    }
}
