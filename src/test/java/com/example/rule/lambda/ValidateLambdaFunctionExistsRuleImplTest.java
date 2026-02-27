package com.example.rule.lambda;

import static com.example.TestUtil.FUNCTION_1;
import static com.example.TestUtil.FUNCTION_2;
import static com.example.TestUtil.FUNCTION_3;
import static com.example.TestUtil.FUNCTION_4;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import com.example.rule.lambda.config.ValidateLambdaFunctionExistsRuleConfig;
import com.example.rule.outcome.ValidationOutcome;
import com.example.services.lambda.LambdaConnector;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.lambda.model.GetFunctionResponse;

class ValidateLambdaFunctionExistsRuleImplTest {

    static MockedStatic<LambdaConnector> mockedLambdaConnector = Mockito.mockStatic(LambdaConnector.class);
    static LambdaConnector mockedLambdaConnectorInstance = Mockito.mock(LambdaConnector.class);
    static ValidateLambdaFunctionExistsRuleImpl testObject;

    @BeforeEach
    public void beforeEach() {
        mockedLambdaConnector.when(LambdaConnector::create).thenReturn(mockedLambdaConnectorInstance);
        testObject = new ValidateLambdaFunctionExistsRuleImpl();
    }

    @AfterEach
    public void afterEach() {
        mockedLambdaConnector.reset();
        reset(mockedLambdaConnectorInstance);
    }

    @AfterAll
    public static void afterAll() {
        mockedLambdaConnector.closeOnDemand();
    }

    @Test
    void testThatValidateLambdaFunctionExistsRuleExecutesSuccessfully() {
        // Given
        final var config = ValidateLambdaFunctionExistsRuleConfig.builder()
                .functionNames(List.of(FUNCTION_1, FUNCTION_2))
                .build();

        final var optLambdaFunction = Optional.of(GetFunctionResponse.builder().build());

        final var expectedResult = ImmutableList.of(ValidationOutcome.valid(), ValidationOutcome.valid());

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(anyString())).thenReturn(optLambdaFunction);
        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }

    @Test
    void testThatValidateLambdaFunctionExistsRuleReturnsInvalidWhenFunctionNotFoundWithCorrectReason() {
        // Given
        final var config = ValidateLambdaFunctionExistsRuleConfig.builder()
                .functionNames(List.of(FUNCTION_1, FUNCTION_2, FUNCTION_3, FUNCTION_4))
                .build();

        final var optLambdaFunction = Optional.of(GetFunctionResponse.builder().build());

        final var expectedResult = ImmutableList.of(
                ValidationOutcome.valid(),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND),
                ValidationOutcome.invalid(LambdaReason.FUNCTION_NOT_FOUND),
                ValidationOutcome.valid());

        // When
        when(mockedLambdaConnectorInstance.getLambdaFunction(or(eq(FUNCTION_1), eq(FUNCTION_4))))
                .thenReturn(optLambdaFunction);
        when(mockedLambdaConnectorInstance.getLambdaFunction(or(eq(FUNCTION_2), eq(FUNCTION_3))))
                .thenReturn(Optional.empty());

        final var actualResult = testObject.execute(config);

        // Then
        assertThat(actualResult)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expectedResult);
    }
}
