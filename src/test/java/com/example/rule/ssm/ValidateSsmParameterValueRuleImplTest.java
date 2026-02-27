package com.example.rule.ssm;

import static com.example.TestUtil.PARAMETER_1;
import static com.example.TestUtil.PARAMETER_2;
import static com.example.TestUtil.PARAMETER_3;
import static com.example.TestUtil.PARAMETER_4;
import static com.example.TestUtil.VALUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.example.rule.outcome.ValidationOutcome;
import com.example.rule.ssm.config.ValidateSsmParameterValueRuleConfig;
import com.example.services.ssm.SsmConnector;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.ssm.model.Parameter;

class ValidateSsmParameterValueRuleImplTest {

    static MockedStatic<SsmConnector> mockedSsmConnector = Mockito.mockStatic(SsmConnector.class);
    static SsmConnector mockedSsmConnectorInstance = Mockito.mock(SsmConnector.class);
    static ValidateSsmParameterValueRuleImpl testObject;

    @BeforeEach
    public void beforeEach() {
        mockedSsmConnector.when(SsmConnector::create).thenReturn(mockedSsmConnectorInstance);
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
        final var config = ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(List.of(
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_1, VALUE),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_2, VALUE)))
                .build();

        final var optSsmParameter = Optional.of(Parameter.builder().value(VALUE).build());

        final var expectedResult = ImmutableList.of(ValidationOutcome.valid(), ValidationOutcome.valid());

        // When
        when(mockedSsmConnectorInstance.getParameter(anyString())).thenReturn(optSsmParameter);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateSsmParameterValueRuleReturnsInvalidWhenParameterNotFoundWithCorrectReason() {
        // Given
        final var config = ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(List.of(
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_1, VALUE),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_2, VALUE),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_3, VALUE),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_4, VALUE)))
                .build();

        final var optSsmParameter = Optional.of(Parameter.builder().value(VALUE).build());

        final var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(SsmReason.PARAMETER_NOT_FOUND),
                ValidationOutcome.invalid(SsmReason.PARAMETER_NOT_FOUND),
                ValidationOutcome.valid());

        // When
        when(mockedSsmConnectorInstance.getParameter(or(eq(PARAMETER_1), eq(PARAMETER_4))))
                .thenReturn(optSsmParameter);
        when(mockedSsmConnectorInstance.getParameter(or(eq(PARAMETER_2), eq(PARAMETER_3))))
                .thenReturn(Optional.empty());

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateSsmParameterValueRuleReturnsInvalidWhenParameterValueIsNotExpectedWithCorrectReason() {
        // Given
        final var config = ValidateSsmParameterValueRuleConfig.builder()
                .parameterValues(List.of(
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_1, VALUE),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_2, VALUE),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_3, VALUE),
                        new ValidateSsmParameterValueRuleConfig.SsmParameterValue(PARAMETER_4, VALUE)))
                .build();

        final var optSsmParameter = Optional.of(Parameter.builder().value(VALUE).build());
        final var optSsmParameterDifferentValue =
                Optional.of(Parameter.builder().value("differentValue").build());

        final var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(SsmReason.PARAMETER_VALUE_MISMATCH),
                ValidationOutcome.invalid(SsmReason.PARAMETER_VALUE_MISMATCH),
                ValidationOutcome.valid());

        // When
        when(mockedSsmConnectorInstance.getParameter(or(eq(PARAMETER_1), eq(PARAMETER_4))))
                .thenReturn(optSsmParameter);
        when(mockedSsmConnectorInstance.getParameter(or(eq(PARAMETER_2), eq(PARAMETER_3))))
                .thenReturn(optSsmParameterDifferentValue);

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }
}
