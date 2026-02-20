package com.example.rule.ssm;

import com.example.rule.outcome.ValidationOutcome;
import com.example.rule.ssm.config.ValidateSsmParameterValueRuleConfig;
import com.example.services.ssm.SsmConnector;
import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.ssm.model.Parameter;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

class ValidateSsmParameterValueRuleImplTest {

    static MockedStatic<SsmConnector> mockedSsmConnector = Mockito.mockStatic(SsmConnector.class);
    static SsmConnector mockedSsmConnectorInstance = Mockito.mock(SsmConnector.class);
    static ValidateSsmParameterValueRuleImpl testObject;

    @BeforeEach
    public void beforeEach() {
        mockedSsmConnector.when(SsmConnector::getInstance).thenReturn(mockedSsmConnectorInstance);
        testObject = new ValidateSsmParameterValueRuleImpl();
    }

    @AfterEach
    public void afterEach() {
        mockedSsmConnector.reset();
        reset(mockedSsmConnectorInstance);
    }

    @AfterAll
    public static void afterAll() {
        mockedSsmConnector.closeOnDemand();
    }

    @Test
    void testThatValidateSsmParameterValueRuleExecutesSuccessfully() {
        // Given
        var config = ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(List.of(
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter1", "value"),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter2", "value")
                ))
                .build();

        var optSsmParameter = Optional.of(Parameter.builder().value("value").build());

        var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.valid()
        );

        // When
        when(mockedSsmConnectorInstance.getParameter(anyString())).thenReturn(optSsmParameter);
        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateSsmParameterValueRuleReturnsInvalidWhenParameterNotFoundWithCorrectReason() {
        // Given
        var config = ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(List.of(
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter1", "value"),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter2", "value"),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter3", "value"),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter4", "value")
                ))
                .build();

        var optSsmParameter = Optional.of(Parameter.builder().value("value").build());

        var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(SsmReason.PARAMETER_NOT_FOUND),
                ValidationOutcome.invalid(SsmReason.PARAMETER_NOT_FOUND),
                ValidationOutcome.valid()
        );

        // When
        when(mockedSsmConnectorInstance.getParameter(or(eq("parameter1"), eq("parameter4")))).thenReturn(optSsmParameter);
        when(mockedSsmConnectorInstance.getParameter(or(eq("parameter2"), eq("parameter3")))).thenReturn(Optional.empty());

        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateSsmParameterValueRuleReturnsInvalidWhenParameterValueIsNotExpectedWithCorrectReason() {
        // Given
        var config = ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(List.of(
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter1", "value"),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter2", "value"),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter3", "value"),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue("parameter4", "value")
                ))
                .build();

        var optSsmParameter = Optional.of(Parameter.builder().value("value").build());
        var optSsmParameterDifferentValue = Optional.of(Parameter.builder().value("differentValue").build());

        var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(SsmReason.PARAMETER_VALUE_MISMATCH),
                ValidationOutcome.invalid(SsmReason.PARAMETER_VALUE_MISMATCH),
                ValidationOutcome.valid()
        );

        // When
        when(mockedSsmConnectorInstance.getParameter(or(eq("parameter1"), eq("parameter4")))).thenReturn(optSsmParameter);
        when(mockedSsmConnectorInstance.getParameter(or(eq("parameter2"), eq("parameter3")))).thenReturn(optSsmParameterDifferentValue);

        var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }
}