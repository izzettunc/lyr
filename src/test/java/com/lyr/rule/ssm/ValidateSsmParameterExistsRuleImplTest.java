package com.lyr.rule.ssm;

import static com.lyr.TestUtil.PARAMETER_1;
import static com.lyr.TestUtil.PARAMETER_2;
import static com.lyr.TestUtil.PARAMETER_3;
import static com.lyr.TestUtil.PARAMETER_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import com.lyr.rule.outcome.ValidationOutcome;
import com.lyr.rule.ssm.config.ValidateSsmParameterExistsRuleConfig;
import com.lyr.services.ssm.SsmConnector;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.ssm.model.Parameter;

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
        final var config = ValidateSsmParameterExistsRuleConfig.builder()
                .parameterNames(List.of(PARAMETER_1, PARAMETER_2))
                .build();

        final var optSsmParameter = Optional.of(Parameter.builder().build());

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
    void testThatValidateSsmParameterExistsRuleReturnsInvalidWhenParameterNotFoundWithCorrectReason() {
        // Given
        final var config = ValidateSsmParameterExistsRuleConfig.builder()
                .parameterNames(List.of(PARAMETER_1, PARAMETER_2, PARAMETER_3, PARAMETER_4))
                .build();

        final var optSsmParameter = Optional.of(Parameter.builder().build());

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
}
