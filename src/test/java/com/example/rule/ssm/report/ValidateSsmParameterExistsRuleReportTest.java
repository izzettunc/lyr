package com.example.rule.ssm.report;

import com.example.rule.outcome.ValidationOutcome;
import com.example.rule.ssm.SsmReason;
import com.example.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.example.rule.Constants.VALIDATE_SSM_PARAMETER_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;

class ValidateSsmParameterExistsRuleReportTest {

    ValidateSsmParameterExistsRuleReport testObject;

    @BeforeEach
    public void beforeEach() {
        testObject = new ValidateSsmParameterExistsRuleReport ();
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereIsAnOutcome(){
        // Given
        var config = ValidateSsmParameterExistsRuleConfig
                .builder()
                .parameterNames(List.of("parameter1", "parameter2", "parameter3"))
                .build();

        var outcome = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(null),
                ValidationOutcome.invalid(SsmReason.PARAMETER_NOT_FOUND)
        );

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains("parameter1", "parameter2", "parameter3")
                .contains("null", "PARAMETER_NOT_FOUND")
                .contains(VALIDATE_SSM_PARAMETER_EXISTS);
    }

    @Test
    void testThatReportReturnsAReportAsAStringWhenThereNoOutcome(){
        // Given
        var config = ValidateSsmParameterExistsRuleConfig
                .builder()
                .parameterNames(List.of())
                .build();

        ImmutableList<ValidationOutcome<SsmReason>> outcome = ImmutableList.of();

        // When
        var actualResult = testObject.report(config, outcome);

        // Then
        assertThat(actualResult)
                .contains(VALIDATE_SSM_PARAMETER_EXISTS);
    }
}