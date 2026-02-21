package com.example.rule.ssm;


import com.example.rule.outcome.ValidationOutcome;
import com.example.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
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

class ValidateSsmParameterExistsRuleImplTest {

    static MockedStatic<SsmConnector> mockedSsmConnector = Mockito.mockStatic(SsmConnector.class);
    static SsmConnector mockedSsmConnectorInstance = Mockito.mock(SsmConnector.class);
    static ValidateSsmParameterExistsRuleImpl testObject;

    @BeforeEach
    public void beforeEach() {
        mockedSsmConnector.when(SsmConnector::create).thenReturn(mockedSsmConnectorInstance);
        testObject = new ValidateSsmParameterExistsRuleImpl();
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
    void testThatValidateSsmParameterExistsRuleExecutesSuccessfully() {
        // Given
        var config = ValidateSsmParameterExistsRuleConfig.builder()
                .parameterNames(List.of("parameter1", "parameter2"))
                .build();

        var optSsmParameter = Optional.of(Parameter.builder().build());

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
    void testThatValidateSsmParameterExistsRuleReturnsInvalidWhenParameterNotFoundWithCorrectReason() {
        // Given
        var config = ValidateSsmParameterExistsRuleConfig.builder()
                .parameterNames(List.of("parameter1", "parameter2", "parameter3", "parameter4"))
                .build();

        var optSsmParameter = Optional.of(Parameter.builder().build());

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

}